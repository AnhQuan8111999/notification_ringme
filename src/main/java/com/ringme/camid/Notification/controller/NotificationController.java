package com.ringme.camid.Notification.controller;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ringme.camid.Notification.repos.mysql.entity.Campaign;
import com.ringme.camid.Notification.repos.mysql.entity.Segment;
import com.ringme.camid.Notification.repos.mysql.impl.CampaignDaoImpl;
import com.ringme.camid.Notification.service.CampaignService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/v1/camid/")
public class NotificationController {
    private static final Logger logger = LogManager.getLogger(NotificationController.class);
    @Autowired
    private CampaignService campaignService;
    @Autowired
    private CampaignDaoImpl campaignDao;

    @GetMapping("/test")
    public ResponseEntity Test() {
        return ResponseEntity.ok().body("OK");
    }

    /**
     * GET ALL NOTIFICATION BY MSISDN
     */
    @RequestMapping(value = "/getNotification/home", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getNotification(
            @RequestParam("msisdn") String msisdn,
            @RequestParam("size") int size,
            @RequestParam("page") int page,
            @RequestHeader Map<String, String> headers
    ) {
        String result = "";
        long start = System.currentTimeMillis();
        try {
            result = campaignService.getMessageInfo(msisdn, size, page);
            long proc = System.currentTimeMillis() - start;
            logger.info("getNotification|Msisdn|" + msisdn + "|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("getNotification|Exception|" + e.getMessage(), e);
        }
        return result;
    }

    /**
     * GET LIST NOTIFICATION FOR TEST
     */
    @RequestMapping(value = "/getNotification/test", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getNotificationMySQL(
            @RequestParam("msisdn") String msisdn,
            @RequestHeader Map<String, String> headers
    ) {
        String result = "";
        long start = System.currentTimeMillis();
        JsonObject jsonObject = new JsonObject();
        try {
            List<Campaign> list = campaignDao.getCampaign();

            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("message", "get notification success");
            JsonElement element = new Gson().toJsonTree(list, new TypeToken<List<Campaign>>() {
            }.getType());
            jsonObject.addProperty("count", list.size());
            jsonObject.add("data", element);

            long proc = System.currentTimeMillis() - start;
            logger.info("getNotification|Msisdn|" + msisdn + "|ExecuteTime|" + proc);

        } catch (Exception e) {
            logger.error("getNotification|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("message", "get notification fail");
            jsonObject.addProperty("data", "");

        }
        result = jsonObject.toString();
        return result;
    }

    /**
     * UPDATE NOTIFICATION -> SEEN
     */
    @RequestMapping(value = "/seen", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateMessageInfo(@RequestParam("msisdn") String msisdn,
                                    @RequestParam("msId") String msId) {
        String result = "";
        long start = System.currentTimeMillis();
        JsonObject jsonObject = new JsonObject();
        try {
            campaignService.updateMessageInfo(msId);
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("message", "update message status to seen success");
            jsonObject.addProperty("data", msId);
            long proc = System.currentTimeMillis() - start;
            logger.info("updateMessageInfo|Msisdn|" + msisdn + "|MessageId|" + msId + "|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("updateMessageInfo|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("message", "update message status to seen fail");
            jsonObject.addProperty("data", "");
        }
        result = jsonObject.toString();
        return result;
    }

    /**
     * FILL ALL NOTIFICATION -> SEEN
     */
    @RequestMapping(value = "/getNotification/count", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String fillSeenNotification(@RequestParam("msisdn") String msisdn
    ) {
        String result = "";
        long start = System.currentTimeMillis();
        JsonObject jsonObject = new JsonObject();
        try {
            long count = campaignService.fillSeenCampaign(msisdn);
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("message", "success");
            jsonObject.addProperty("data", count);
            long proc = System.currentTimeMillis() - start;
            logger.info("fillSeenNotification|Msisdn|" + msisdn + "|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("fillSeenNotification|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("message", "fail");
            jsonObject.addProperty("data", "null");
        }
        result = jsonObject.toString();
        return result;
    }

    /**
     * STOP NOTIFICATION
     */
    @RequestMapping(value = "/stop", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String unscheduleNotification(
            @RequestParam("msisdn") String msisdn,
            @RequestParam("fcId") String fcId
    ) {
        String result = "";
        long start = System.currentTimeMillis();
        JsonObject jsonObject = new JsonObject();
        try {
            campaignService.unSchedule(fcId);
            campaignService.finishCampaign(fcId);
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("message", "stop shedule success");
            jsonObject.addProperty("data", fcId);
            long proc = System.currentTimeMillis() - start;
            logger.info("unscheduleNotification|Msisdn|" + msisdn + "|NotificationId|" + fcId + "|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("unscheduleNotification|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("message", "stop shedule fail");
            jsonObject.addProperty("data", "");
        }
        result = jsonObject.toString();
        return result;
    }

    /**
     * STOP NOTIFICATION
     */
    @RequestMapping(value = "/refresh", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String refreshNotification(
            @RequestParam("msisdn") String msisdn,
            @RequestParam("fcId") String fcId
    ) {
        String result = "";
        long start = System.currentTimeMillis();
        JsonObject jsonObject = new JsonObject();
        try {
            campaignService.unSchedule(fcId);
            campaignService.refreshCampaign(fcId);
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("message", "refresh shedule success");
            jsonObject.addProperty("data", fcId);
            long proc = System.currentTimeMillis() - start;
            logger.info("refreshNotification|Msisdn|" + msisdn + "|NotificationId|" + fcId + "|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("refreshNotification|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("message", "refresh shedule fail");
            jsonObject.addProperty("data", "");
        }
        result = jsonObject.toString();
        return result;
    }

    /**
     * DELETE A MESSAGEINFO BY ID
     */
    @RequestMapping(value = "/delete", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteMessageInfo(@RequestParam("msisdn") String msisdn,
                                    @RequestParam("msId") String msId) {
        String result = "";
        long start = System.currentTimeMillis();
        JsonObject jsonObject = new JsonObject();
        try {
            campaignService.deleteMessageInfo(msId);
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("message", "delete messageinfo success");
            jsonObject.addProperty("data", msId);
            long proc = System.currentTimeMillis() - start;
            logger.info("deleteMessageInfo|Msisdn|" + msisdn + "|msId|" + msisdn + "|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("deleteMessageInfo|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("message", "delete messageinfo fail");
            jsonObject.addProperty("data", "");

        }
        result = jsonObject.toString();
        return result;
    }

    /**
     * SEND TEST PUSH
     */
    @RequestMapping(value = "/notification/push", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String pushNotification(@RequestBody Campaign campaign,
                                   @RequestBody Segment segment) {
        String result = "";
        long start = System.currentTimeMillis();
        JsonObject jsonObject = new JsonObject();
        try {
            campaignService.pushNotification(campaign, segment);
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("message", "push notification success");
//            jsonObject.addProperty("data", msId);
            long proc = System.currentTimeMillis() - start;
            logger.info("pushNotification|Success|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("pushNotification|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("message", "push notification fail");
            jsonObject.addProperty("data", "");
        }
        return result;
    }

    /**
     * SEND TEST PUSH
     */
    @RequestMapping(value = "/notification/update", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String pushNotification(@RequestParam("id") String id,
                                   @RequestParam("active") int active) {
        String result = "";
        long start = System.currentTimeMillis();
        JsonObject jsonObject = new JsonObject();
        try {
            campaignService.updateCampaignActive(id, active);
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("message", "update campaign success");
//            jsonObject.addProperty("data", msId);
            long proc = System.currentTimeMillis() - start;
            logger.info("pushNotification|Success|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("pushNotification|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("message", "update campaign fail");
            jsonObject.addProperty("data", "");
        }
        return result;
    }
}
