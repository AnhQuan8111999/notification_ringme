package com.ringme.camid.Notification.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import com.ringme.camid.Notification.job.JobConfiguration;
import com.ringme.camid.Notification.repos.mongodb.MongoDao;
import com.ringme.camid.Notification.repos.mongodb.entity.CamId_MessageInfo;
import com.ringme.camid.Notification.repos.mongodb.entity.User;
import com.ringme.camid.Notification.repos.mysql.entity.Campaign;
import com.ringme.camid.Notification.repos.mysql.entity.Segment;
import com.ringme.camid.Notification.repos.mysql.impl.CampaignDaoImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.quartz.*;

import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.*;
import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class CampaignService {
    @Autowired
    private MongoDao mongoDao;
    @Autowired
    private CampaignDaoImpl campaignDao;
    @Autowired
//    @Qualifier(value = "rabbitmqTemplate")
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private String msisdnRegex = "\\+?\\d+";
    SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
    private Scheduler scheduler = schedFact.getScheduler();
    private int counter;
    private static final int page_size = 20;
    private static SecureRandom random = new SecureRandom();
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private BlockingQueue<Campaign> queue = new ArrayBlockingQueue<>(10000);
    private static final String domain = "http://freeapi.kakoak.tls.tl/timor158/media1/cms_medias";
    private static String message_template = "{\"to\": \"_TO_\",\n" +
            "  \"message_id\": \"_MSGID_\",\n" +
//            "  \"campaign\": {\n" +
//            "    \"body\": \"_BODY_\",\n" +
//            "    \"title\": \"_TITLE_\"\n" +
//            "  },\n" +
            "  \"data\": {\n" +
            "     \"content\": {\n" +
            "      \"name\": \"_TYPE_\",\n" +
            "      \"code\": \"215\",\n" +
            "      \"avatar\": \"_IMAGE_\",\n" +
            "      \"content\": \"_CONTENT_\",\n" +
            "      \"receiver\":\"_RECEIVER_\",\n" +
            "      \"url\":\"_URL_\",\n" +
            "       \"big_img\":\"_BIG_IMAGE_\"" +
            "    }\n" +
            "  },\n" +
            "  \"time_to_live\": _TTL_ }";
    public static String campaign_template = "{ \"to\": \"_TO_\",\n" +
            "  \"message_id\": \"_MSGID_\",\n" +
            "  \"data\": {\n" +
            "     \"content\": {\n" +
            "      \"title\": \"_TITLE_\",\n" +
            "      \"code\": \"217\",\n" +
            "      \"avatar\": \"_IMAGE_\",\n" +
            "      \"content\": \"_CONENT_\",\n" +
            "      \"is_full_screen\":\"_FULL_SCREEN_\",\n" +
            "      \"layout_style\":\"_LAYOUT_STYLE_\",\n" +
            "      \"button_layout\":\"_BUTTON_LAYOUT_\",\n" +
            "      \"button\":_BUTTON_,\n" +
            "      \"display_in_app\":\"_DISPLAY_IN_APP_\",\n" +
            "      \"on_event\":\"_ON_EVENT_\",\n" +
            "      \"delay\":_DELAY_,\n" +
            "      \"receiver\":\"_RECEIVER_\",\n" +
            "      \"big_img\":\"_IMAGE_\",\n" +
            "      \"deeplink\":\"_DEPPLINK_\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"notification\": {\n" +
            "    \"body\": \"_CONENT_\",\n" +
            "    \"title\": \"_TITLE_\"\n" +
            "  },\n" +
            "  \"time_to_live\": _TTL_}";

    private static final Logger logger = LogManager.getLogger(CampaignService.class);

    public CampaignService() throws SchedulerException {
        counter = 0;
        scheduler.start();
    }

    // Lay danh sach campaign
    @Scheduled(cron = "0/30 * * * * ?")
    public void LoadCampaign() {
        List<Campaign> list = new ArrayList<>();
        List<Campaign> list1 = new ArrayList<>();
        long start = System.currentTimeMillis();
        int totalCampaign = campaignDao.getCountCampaign();
        int number_of_page = 0;
        if (totalCampaign % 20 == 0) number_of_page = totalCampaign / 20;
        else number_of_page = (totalCampaign / 20) + 1;

        try {
            logger.info("LoadCampaign|Page|" + counter + "|Size|" + page_size);
            list = campaignDao.getCampaign();
            list1 = campaignDao.getCampaignProcessing(counter, page_size);
            if (counter >= number_of_page) counter = 0;
            else counter++; // tang page len 1 don vi
            // xu ly campaign new
            nonProcess_Campaign(list);
            // them campaign co process_status = 1
            processing_Campaign(list1);
            // lay campaign trong queue ra xu ly
            int i = page_size; // 20
            if (queue.size() < i) i = queue.size();
            while (i-- > 0) {
                Campaign campaign = queue.poll(5, TimeUnit.SECONDS);
                schedule(campaign);
            }
            // danh dau campaign da xu ly xong, het thoi gian xu ly
            logger.info("LoadCampaign|ProcessedCampaign|" + finishCampaign());
            long proc = System.currentTimeMillis() - start;
            logger.info("LoadCampaign|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("LoadCampaign|Exception|" + e.getMessage(), e);
        }
    }

    public void nonProcess_Campaign(List<Campaign> list) {
        long start = System.currentTimeMillis();
        try {
            if (list != null && list.size() > 0) {
                for (Campaign campaign : list) {
//                    System.out.println(campaign.getId());
                    if (campaign.getCron_expression() == null || campaign.getCron_expression().isEmpty()) {
                        process_Campaign(campaign);
                        logger.info("nonProcess_Campaign|Process now|CampaignId|" + campaign.getId());
                        finishCampaign(campaign.getId());
                    } else {
                        // len lich
                        schedule(campaign);
                        // update -> process_status = 1
                        updateCampaign(campaign.getId(), 1);
                    }

                }
                long proc = System.currentTimeMillis() - start;
                logger.info("LoadCampaign|NonProcess|Count|" + list.size() + "|ExecuteTime|" + proc);
            } else {
                logger.info("LoadCampaign|NonProcess|No Campaign Active!");
            }
        } catch (Exception e) {
            logger.error("nonProcess_Campaign|Exception|" + e.getMessage(), e);
        }
    }

    public void processing_Campaign(List<Campaign> list) {
        try {
            if (list != null && list.size() > 0) {
                logger.info("LoadCampaign|Processing|Count|" + list.size());
                for (Campaign campaign : list) {
                    queue.offer(campaign);
//                    if (!campaign.getCrontabExpress().isEmpty()) {
//                        // len lich
//                        schedule(campaign);
//                    }
                }
            } else {
                logger.info("LoadCampaign|Processing|No Campaign Active!");
            }
        } catch (Exception e) {
            logger.error("processing_Campaign|Exception|" + e.getMessage(), e);
        }
    }

    public String makeMessageCampaign(Campaign campaign, User user) {
        String msg = campaign_template;
        String id = generateRandomString(20);
        JsonObject jsonObject = new JsonObject();
        if (campaign.getButton1_name() != null && !campaign.getButton1_name().isEmpty()) {
            JsonObject button1 = new JsonObject();
            button1.addProperty("name", campaign.getButton1_name());
            button1.addProperty("deeplink", campaign.getButton1_deeplink());
            jsonObject.add("button1", button1);
        }
        if (campaign.getButton2_name() != null && !campaign.getButton2_name().isEmpty()) {
            JsonObject button2 = new JsonObject();
            button2.addProperty("name", campaign.getButton2_name());
            button2.addProperty("deeplink", campaign.getButton2_deeplink());
            jsonObject.add("button2", button2);
        }
        String button = jsonObject.toString().replace("\"{", "{").replace("}\"", "}");
        int is_full_screen = 0;
        if (campaign.getLayout_position().equalsIgnoreCase("full_screen")) is_full_screen = 1;
        msg = msg.replace("_TO_", user.getRegid())
                .replace("_MSGID_", id)
                .replace("_IMAGE_", campaign.getImage())
                .replace("_CONTENT_", campaign.getMessage())
                .replace("_IMAGE", campaign.getImage())
                .replace("_RECEIVER_", user.getUsername())
                .replace("_DEEPLINK_", campaign.getDeeplink())
                .replace("_TITLE_", campaign.getTitle())
                .replace("_FULL_SCREEN_", is_full_screen + "")
                .replace("_LAYOUT_STYLE_", campaign.getLayout_style() + "")
                .replace("_BUTTON_LAYOUT_", campaign.getButton_layout() + "")
                .replace("_BUTTON_", button)
                .replace("_DISPLAY_IN_APP_", campaign.getDisplay_in_app() + "")
                .replace("_ON_EVENT_", campaign.getOn_event())
                .replace("_DELAY_", "" + campaign.getDelay() * 1000)
                .replace("_TTL_", "604800");
        return msg;
    }

    public void process_Campaign(Campaign campaign) {

        List<String> list = new ArrayList<>();
        try {
            switch (campaign.getInput_type()) {
                case "text":
                    String[] phones = campaign.getPhone_lists();
                    list = Arrays.asList(phones);
                    //System.out.println(new Gson().toJson(list));
                    break;
                case "file":
                    String filePath = campaign.getFile_path();
                    list = processExcel(filePath);
                    //System.out.println(new Gson().toJson(list));
                    break;
                case "active_users":
                    process_ActiveUsers(campaign);
                    break;
                default:
                    logger.error("InputType wrong!");
                    break;
            }
            if ("text|file".contains(campaign.getInput_type())) {
                for (String msisdn : list) {
                    CamId_MessageInfo message = generateMessage(msisdn, campaign);
                    if (message != null) {
                        mongoDao.saveMessageInfo(message);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("process_Campaign|Exception|" + e.getMessage(), e);
            updateCampaign(campaign.getId(), 3);
            unSchedule(campaign.getId());

        }

    }

    private void process_ActiveUsers(Campaign campaign) {
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
                    CamId_MessageInfo message = generateMessage(s, campaign);
                    if (message.getId() != null) {
                        mongoDao.saveMessageInfo(message);
                    }
                }
//                logger.info("process_ActiveUsers|SendCampaign|CountUser|" + list.size());
            }
            long proc = System.currentTimeMillis() - start;
            logger.info("process_ActiveUsers|SendCampaign|TotalUser|" + total + "|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("process_ActiveUsers|Exception|" + e.getMessage(), e);
        }
    }

    public List<String> processExcel(String filePath) {
        List<String> list = new ArrayList<>();
        int count = 0;
        try (FileInputStream fis = new FileInputStream(filePath);
             XSSFWorkbook wb = new XSSFWorkbook(fis);) {
            XSSFSheet sheet = wb.getSheetAt(0);
            //evaluating cell type
            FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();

            Iterator<Row> itr = sheet.iterator();
            logger.info("[DEBUG] start load file: " + filePath);
            while (itr.hasNext()) //iteration over row using for each loop
            {
                org.apache.poi.ss.usermodel.Row row = itr.next();
                Cell cell = row.getCell(0);
                count++;
                String msisdn = readCustomer2(formulaEvaluator, cell); //field that represents numeric cell type
                if (msisdn.matches(msisdnRegex)) {
                    list.add(msisdn);
                }
            }
            logger.info("[DEBUG] Campaign finish load file: " + filePath + " with " + count + " rows.");

        } catch (Exception e) {
            logger.error("read file " + filePath + " error", e);
        }
        return list;
    }

    private String readCustomer2(FormulaEvaluator formulaEvaluator, Cell cell) {
        cell.setCellType(Cell.CELL_TYPE_STRING);

        switch (formulaEvaluator.evaluateInCell(cell).getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:   //field that represents numeric cell type
                //logger.info("read excel " + filePath + " [" + count + "]: " + cell.getNumericCellValue());
                return String.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:    //field that represents string cell type
                //logger.info("read excel " + filePath + " [" + count + "]: " + cell.getStringCellValue());
                try {
                    String val = cell.getStringCellValue().trim();
                    return val;
                } catch (Exception e) {

                }
                break;
        }
        return "";
    }


    public CamId_MessageInfo generateMessage(String msisdn, Campaign campaign) {

        User user = mongoDao.getRegID(msisdn);
        String message = "";
        CamId_MessageInfo messageInfo = new CamId_MessageInfo();
        if (user != null) {
            if (!"MOCHA_UNKNOWN".contains(user.getRegid()) && user.getPlatform().equalsIgnoreCase("ANDROID")) {
                message = makeMessageCampaign(campaign, user);
                sendOA(msisdn, message);
                messageInfo.setMsisdn(msisdn);
                messageInfo.setContent(campaign.getMessage());
                messageInfo.setDeep_link(campaign.getDeeplink());
                messageInfo.setThumbnail(campaign.getImage());
                messageInfo.setNotified_date(new Date());
                messageInfo.setType("Notification");
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

    private String genRandomString() {
        return generateRandomString(20);
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

    //tao schedule
    public void schedule(Campaign entity) throws SchedulerException {

        if (entity != null) {
            JobDetail crontab = newJob(JobConfiguration.class).withIdentity(String.valueOf(entity.getId()), "job_campaign").build();
            crontab.getJobDataMap().put("service", this);
            crontab.getJobDataMap().put("entity", entity);
            Trigger trigger = newTrigger().startNow().withIdentity(String.valueOf(entity.getId()), "trigger_campaign")
                    .withSchedule(cronSchedule(entity.getCron_expression())).build();
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

    //huy bo schedule
    public void unSchedule(String fcId) {
        try {
            scheduler.unscheduleJob(TriggerKey.triggerKey(String.valueOf(fcId), "job_campaign"));
            scheduler.deleteJob(JobKey.jobKey(String.valueOf(fcId), "trigger_campaign"));
            //campaignDao.finishCampaign(fcId);
            // logger.info("unscheduler success: " + fc);
        } catch (SchedulerException ex) {
            logger.error("unscheduler|Exception|" + ex.getLocalizedMessage(), ex);
        }
    }

    public String getMessageInfo(String msisdn, int limit, int skip) {
        long start = System.currentTimeMillis();
        String result = "";
        JsonObject jsonObject = new JsonObject();
        try {
            List<CamId_MessageInfo> list = mongoDao.getCampaign(msisdn, limit, skip);
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("message", "success");
            JsonElement element = new Gson().toJsonTree(list, new TypeToken<List<CamId_MessageInfo>>() {
            }.getType());
            jsonObject.add("data", element);
        } catch (Exception e) {
            logger.error("getCampaign|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("message", "fail");
            jsonObject.addProperty("data", "");
        }
        result = jsonObject.toString();
        return result;
    }


    public void updateMessageInfo(String msId) {
        try {
            mongoDao.updateCampaign(msId);
        } catch (Exception e) {
            logger.error("updateMessageInfo|Exception|" + e.getMessage(), e);
        }
    }

    // dang xu ly
    public void updateCampaign(String id, int status) {
        try {
            campaignDao.updateCampaign(id, status);
        } catch (Exception e) {
            logger.error("updateCampaign|Exception|" + e.getMessage(), e);
        }
    }

    // lam moi
    public void refreshCampaign(String id) {
        try {
            campaignDao.refreshCampaign(id);
        } catch (Exception e) {
            logger.error("refreshCampaign|Exception|" + e.getMessage(), e);
        }
    }

    // xu ly xong
    private int finishCampaign() {
        int result = 0;
        try {
            List<String> list = campaignDao.getProcessedCampaignId();
            for (String i : list) {
                unSchedule(i);
            }
            result = (campaignDao.finishCampaign());
        } catch (Exception e) {
            logger.error("finishCampaign|Exception|" + e.getMessage(), e);
        }
        return result;
    }

    // xu ly xong
    public int finishCampaign(String id) {
        int result = 0;
        try {
            result = campaignDao.finishCampaign(id);
        } catch (Exception e) {
            logger.error("finishCampaign|Exception|" + e.getMessage(), e);
        }
        return result;
    }

    public void deleteMessageInfo(String msId) {
        try {
            mongoDao.deleteMessageInfo(msId);
        } catch (Exception e) {
            logger.error("deleteMessageInfo|Exception|" + e.getMessage(), e);
        }
    }

    public Long fillSeenCampaign(String msisdn) {
        long count = -1;
        try {
            count = mongoDao.fillSeenCampaign(msisdn);

        } catch (Exception e) {
            logger.error("fillSeenCampaign|Exception|" + e.getMessage(), e);
        }
        return count;
    }

    public void pushNotification(Campaign campaign, Segment segment) {
        try {
            if ("text".contains(segment.getInput_type())) {
                campaign.setPhones(segment.getPhone_list());
            } else if ("file".contains(segment.getInput_type())) {
                campaign.setFile_path(segment.getFile_path());
            }
            campaign.setInput_type(segment.getInput_type());
            process_Campaign(campaign);
            updateCampaign(campaign.getId(), 1);
        } catch (Exception e) {
            logger.error("pushNotification|Exception|" + e.getMessage(), e);
        }
    }

    public void updateCampaignActive(String id, int active) {
        try {
            campaignDao.updateCampaignActive(id, active);
        } catch (Exception e) {
            logger.error("updateCampaignActive|Exception|" + e.getMessage(), e);
        }
    }

    public long deleteMessageInfoAll(String msisdn) {
        try {
            return mongoDao.deleteMessageInfoAll(msisdn);
        } catch (Exception e) {
            logger.error("deleteMessageInfoAll|Exception|" + e.getMessage(), e);
            return -1;
        }
    }
}
