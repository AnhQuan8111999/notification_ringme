package com.ringme.camid.Notification.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ringme.camid.Notification.job.JobConfiguration;
import com.ringme.camid.Notification.repos.mongodb.MongoDao;
import com.ringme.camid.Notification.repos.mongodb.entity.CamId_MessageInfo;
import com.ringme.camid.Notification.repos.mongodb.entity.Camid_SurveyMessage;
import com.ringme.camid.Notification.repos.mongodb.entity.User;
import com.ringme.camid.Notification.repos.mysql.entity.Campaign;
import com.ringme.camid.Notification.repos.mysql.entity.Survey;
import com.ringme.camid.Notification.repos.mysql.impl.CampaignDaoImpl;
import com.ringme.camid.Notification.repos.mysql.impl.SurveyDao;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class SurveyService {
    @Autowired
    private MongoDao mongoDao;

    @Autowired
    private SurveyDao surveyDao;

    @Autowired
    private CampaignDaoImpl campaignDao;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private CampaignService campaignService;

    private static SecureRandom random = new SecureRandom();
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private BlockingQueue<Survey> queue = new ArrayBlockingQueue<>(10000);
    SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
    private Scheduler scheduler = schedFact.getScheduler();

    private static final String survey_template = "  { \"to\": \"_TO_\",\n" +
            "  \"message_id\": \"_MSGID_\",\n" +
            "  \"data\": {\n" +
            "     \"content\": {\n" +
            "      \"title\": \"_TITLE_\",\n" +
            "      \"code\": \"218\",\n" +
            "      \"avatar\": \"_IMAGE_\",\n" +
            "      \"content\": \"_CONTENT_\",\n" +
            "      \"layout_style\":\"_LAYOUT_STYLE_\",\n" +
            "      \"is_answer\":_IS_ANSWER_,\n" +
            "      \"receiver\":\"_RECEIVER_\",\n" +
            "      \"big_img\":\"_IMAGE_\",\n" +
            "      \"deeplink\":\"_DEPPLINK_\"\n" +
            "    }\n" +
            "  }\n" +
            "  \"time_to_live\": _TTL_}";
    private static final Logger logger = LogManager.getLogger(SurveyService.class);

    public SurveyService() throws SchedulerException {
        scheduler.start();
    }

    // Lay danh sach survey
    @Scheduled(cron = "0/40 * * * * ?")
    public void LoadSurvey() {
        List<Survey> list = new ArrayList<>();
        long start = System.currentTimeMillis();
        try {
            logger.info("Load Survey");
            list = surveyDao.getSurveyActive();
            logger.info("LoadSurvey|Count|" + list.size());
            for (Survey survey : list) {
                if (StringUtils.isEmpty(survey.getDelivery()) || "now".contains(survey.getDelivery())) {
                    process_Survey(survey);
                } else schedule(survey);
                updateSurvey(survey.getId(), 2);
            }
            long proc = System.currentTimeMillis() - start;
            logger.info("LoadSurvey|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("LoadSurvey|Exception|" + e.getMessage(), e);
        }
    }

    public void process_Survey(Survey survey) {
        List<String> list = new ArrayList<>();
        try {
            switch (survey.getInput_type()) {
                case "text":
                    String[] phones = survey.getPhone_lists();
                    list = Arrays.asList(phones);
                    //System.out.println(new Gson().toJson(list));
                    break;
                case "file":
                    String filePath = survey.getFile_path();
                    list = campaignService.processExcel(filePath);
                    //System.out.println(new Gson().toJson(list));
                    break;
                case "active_users":
                    process_ActiveUsers(survey);
                    break;
                default:
                    logger.error("InputType wrong!");
                    break;
            }
            if ("text|file".contains(survey.getInput_type())) {
                for (String msisdn : list) {
                    CamId_MessageInfo message = generateMessage(msisdn, survey);
                    if (message != null) {
                        mongoDao.saveMessageInfo(message);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("process_Survey|Exception|" + e.getMessage(), e);
            updateSurvey(survey.getId(), 3);
            unSchedule(survey.getId());

        }
    }

    private void updateSurvey(int id, int i) {
        try {
            surveyDao.updateSurvey(id, i);
        } catch (Exception e) {
            logger.error("updateSurvey|Exception|" + e.getMessage(), e);
        }
    }

    private void process_ActiveUsers(Survey survey) {
        long start = System.currentTimeMillis();
        int limit = 500;
        int total = campaignDao.getCountUser();
        int count = (total / limit);
        if (total % limit != 0) count++;
        try {
            for (int i = 0; i < count; i++) {
                int offset = i * limit;
                List<String> list = campaignDao.getActiveUsers(offset, limit);
                for (String s : list) {
                    CamId_MessageInfo message = generateMessage(s, survey);
                    if (message.getId() != null) {
                        mongoDao.saveMessageInfo(message);
                    }
                }
//                logger.info("process_ActiveUsers|SendSurvey|CountUser|" + list.size());
            }
            long proc = System.currentTimeMillis() - start;
            logger.info("process_ActiveUsers|SendSurvey|TotalUser|" + total + "|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("process_ActiveUsers|Exception|" + e.getMessage(), e);
        }
    }

    private void unSchedule(int id) {
        try {
            scheduler.unscheduleJob(TriggerKey.triggerKey(String.valueOf(id), "trigger"));
            scheduler.deleteJob(JobKey.jobKey(String.valueOf(id), "job"));
            //campaignDao.finishSurvey(fcId);
            // logger.info("unscheduler success: " + fc);
        } catch (SchedulerException ex) {
            logger.error("unscheduler|Exception|" + ex.getLocalizedMessage(), ex);
        }
    }

    public CamId_MessageInfo generateMessage(String msisdn, Survey survey) {

        User user = mongoDao.getRegID(msisdn);
        String message = "";
        CamId_MessageInfo messageInfo = new CamId_MessageInfo();
        if (user != null) {
            if (!"MOCHA_UNKNOWN".contains(user.getRegid()) && user.getPlatform().equalsIgnoreCase("ANDROID")) {
                message = makeMessageSurvey(survey, user);
                sendOA(msisdn, message);
                messageInfo.setMsisdn(msisdn);
                messageInfo.setContent(survey.getMessage());
                messageInfo.setDeep_link(survey.getDeeplink());
                messageInfo.setThumbnail(survey.getImage());
                messageInfo.setNotified_date(new Date());
                messageInfo.setType("Survey");
                messageInfo.setStatus(0);
                return messageInfo;
            } else {
                logger.info("generateMessage|RegId is Empty!");
                return new CamId_MessageInfo();
            }
        } else {
            logger.info("generateMessage|User is Empty!");
            return new CamId_MessageInfo();
        }
        // push messageInfo to fcm
    }

    public String makeMessageSurvey(Survey survey, User user) {
        String msg = survey_template;
        String id = generateRandomString(20);
        if (survey.getLayout_style() == 0) {
            msg = msg.replace("_IMAGE_", null);
        } else msg = msg.replace("_IMAGE_", survey.getImage());
        msg = msg.replace("_TO_", user.getRegid())
                .replace("_MSGID_", id)
//                .replace("_IMAGE_", survey.getImage())
                .replace("_CONTENT_", survey.getMessage())
                .replace("_IMAGE", survey.getImage())
                .replace("_RECEIVER_", user.getUsername())
                .replace("_DEEPLINK_", survey.getDeeplink())
                .replace("_IS_ANSWER_", survey.getIs_answer() + "")
                .replace("_TITLE_", survey.getTitle())
                .replace("_LAYOUT_STYLE_", survey.getLayout_style() + "")
                .replace("_TTL_", "604800");
        return msg;
    }

    public String generateRandomString(int length) {
        if (length < 1) {
            throw new IllegalArgumentException();
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // 0-62 (exclusive), random returns 0-61
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            // debug
//      System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);
            sb.append(rndChar);
        }
        return sb.toString();
    }

    public void sendOA(String msisdn, String message) {
        //Message oaMessage = makeOAWithoutDeepLink(msisdn, message);
        Message oaMessage = new Message(message.getBytes(), new MessageProperties());
        String mes = new String(oaMessage.getBody());
        logger.info("sendMessage|Message|" + mes);
//        String message_process = mes;
//        int start = message_process.indexOf("[{");
//        int end = message_process.indexOf("}]");
//        String mess = message_process.substring(start + 1, end + 1);
//        JsonObject jsonObject = new Gson().fromJson(mess, JsonObject.class);
//        System.out.println(jsonObject.get("regid").getAsString());
//        JsonObject body = jsonObject.getAsJsonObject("body");
//        System.out.println(body.get("title"));

//        rabbitTemplate.send("camid_notification", oaMessage);
        kafkaTemplate.send("hieu-topic", message);
    }

    //tao schedule
    public void schedule(Survey entity) throws SchedulerException {

        if (entity != null) {
            JobDetail crontab = newJob(JobConfiguration.class).withIdentity(String.valueOf(entity.getId()), "job").build();
            crontab.getJobDataMap().put("service", this);
            crontab.getJobDataMap().put("entity", entity);
            Trigger trigger = newTrigger().startNow().withIdentity(String.valueOf(entity.getId()), "trigger")
                    .withSchedule(cronSchedule(entity.getDelivery())).build();
            JobKey key = crontab.getKey();
            // System.out.println(key);
            if (scheduler.checkExists(key) == false) {
                logger.info("schedule|INFO|JobKey|" + key);
                scheduler.scheduleJob(crontab, trigger);
            }
        } else {
            System.out.println("[schedule] Entity is null");
        }
    }
    public String getSurveyMessage(String msisdn, int limit, int skip) {
        String result = "";
        JsonObject jsonObject = new JsonObject();
        try {
            List<Camid_SurveyMessage> list = mongoDao.getSurvey(msisdn, limit, skip);
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("message", "success");
            JsonElement element = new Gson().toJsonTree(list, new TypeToken<List<CamId_MessageInfo>>() {
            }.getType());
            jsonObject.add("data", element);
        } catch (Exception e) {
            logger.error("getSurveyMessage|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("message", "fail");
            jsonObject.addProperty("data", "");
        }
        result = jsonObject.toString();
        return result;
    }
}
