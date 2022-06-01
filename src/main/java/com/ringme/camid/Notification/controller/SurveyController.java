package com.ringme.camid.Notification.controller;

import com.ringme.camid.Notification.repos.mongodb.MongoDao;
import com.ringme.camid.Notification.service.SurveyService;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @RequestMapping(value = "/getSurvey/home", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
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
    @RequestMapping(value = "/getSurvey/home", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String increaseOptionSurvey(@RequestParam("msisdn") String msisnd,
                                       @RequestParam("type") String type,
                                       @RequestParam("num") int num,
                                       @RequestParam("surveyId") String id) {
        String result = "";
        long start = System.currentTimeMillis();
        try {
            mongoDao.increaseSurvey(id, num, type);
        } catch (Exception e) {
            logger.error("increaseOptionSurvey|Exception|" + e.getMessage(), e);
        }
        return result;
    }
}
