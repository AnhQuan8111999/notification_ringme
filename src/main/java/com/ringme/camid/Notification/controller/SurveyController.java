package com.ringme.camid.Notification.controller;

import com.google.gson.JsonObject;
import com.ringme.camid.Notification.repos.mongodb.MongoDao;
import com.ringme.camid.Notification.service.SurveyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.TreeMap;

@Controller
@RequestMapping("/v1/survey")
public class SurveyController {
    @Autowired
    private MongoDao mongoDao;

    @Autowired
    private SurveyService surveyService;

    private static final Logger logger = LogManager.getLogger(SurveyController.class);

    /**
     * GET ALL Survey Message BY MSISDN
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
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
            result = surveyService.getSurveyMessage(msisdn, size, page);
            long proc = System.currentTimeMillis() - start;
            logger.info("getNotification|Msisdn|" + msisdn + "|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("getNotification|Exception|" + e.getMessage(), e);
        }
        return result;
    }

    /**
     * INCREASE OPTIONS SURVEY
     */
    @RequestMapping(value = "/assess", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String assessSurvey(@RequestParam("msisdn") String msisnd,
                               @RequestParam("type") String type,
                               @RequestParam("num") int num,
                               @RequestParam("surveyId") String id) {
        String result = "";
        long start = System.currentTimeMillis();
        JsonObject jsonObject = new JsonObject();
        try {
            mongoDao.increaseSurvey(id, num, type);
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("desc", "successful survey");
            long proc = System.currentTimeMillis() - start;
            logger.info("assessSurvey|Msisdn|" + msisnd + "|Type|" + type + "|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("assessSurvey|Exception|" + e.getMessage(), e);
        }
        return result;
    }

    /**
     * STOP A SURVEY BY ID
     */

    @RequestMapping(value = "/stop", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String stopSurvey(
            @RequestParam("surveyId") int surveyId) {
        String result = "";
        long start = System.currentTimeMillis();
        JsonObject jsonObject = new JsonObject();
        try {
            surveyService.unSchedule(surveyId);
            surveyService.finishSurvey(surveyId);
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("desc", "stop survey success");
//            jsonObject.addProperty("data",);
            long proc = System.currentTimeMillis() - start;
            logger.info("stopSurvey|SurveyId|" + surveyId + "|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("stopSurvey|Exception|" + e.getMessage(), e);
        }
        result = jsonObject.toString();
        return result;
    }
}
