package com.ringme.camid.Notification.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ringme.camid.Notification.repos.mysql.entity.WelcomePage;
import com.ringme.camid.Notification.repos.mysql.impl.WelcomePageDao;
import com.ringme.camid.Notification.service.WelcomePageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/welcome")
public class WelcomePageController {
    private static final Logger logger = LogManager.getLogger(WelcomePageController.class);
    @Autowired
    private WelcomePageDao welcomePageDao;

    @Autowired
    private WelcomePageService welcomePageService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getList(@RequestParam("msisdn") String msisdn) {
        String result = "";
        long start = System.currentTimeMillis();
        JsonObject jsonObject = new JsonObject();
        try {
            List<WelcomePage> list = welcomePageService.getList();
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("desc", "get list welcome page success");
            jsonObject.add("data", new Gson().toJsonTree(list));
            long proc = System.currentTimeMillis() - start;
            logger.info("getList|Msisdn|" + msisdn + "|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("getList|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("desc", "get list welcome page fail");
            jsonObject.add("data", null);
        }
        result = jsonObject.toString();
        return result;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateStatus(@RequestParam("wpId") int wpId,
                               @RequestParam("status") int status) {
        String result = "";
        JsonObject jsonObject = new JsonObject();
        long start = System.currentTimeMillis();
        try {
            welcomePageDao.updateStatus(wpId, status);
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("desc", "update status success");
            Map<String, Integer> map = new HashMap<>();
            map.put("Welcome_Page_Id", wpId);
            map.put("status", status);
            jsonObject.add("data", new Gson().toJsonTree(map));
            long proc = System.currentTimeMillis() - start;
            logger.info("updateStatus|Welcomepage ID|" + wpId + "|status|" + status + "|ExecuteTime|" + proc);

        } catch (Exception e) {
            logger.error("updateStatus|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("desc", "update status fail");
            jsonObject.add("data", null);

        }
        result = jsonObject.toString();
        return result;
    }

    @RequestMapping(value = "/clearCache", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String clearCache() {
        String result = "";
        JsonObject jsonObject = new JsonObject();
        long start = System.currentTimeMillis();
        try {
            welcomePageService.clearCache();
            jsonObject.addProperty("code", 200);
            jsonObject.addProperty("desc", "clear cache success");
            long proc = System.currentTimeMillis() - start;
            logger.info("clearCache|Success|ExecuteTime|" + proc);
        } catch (Exception e) {
            logger.error("clearCache|Exception|" + e.getMessage(), e);
            jsonObject.addProperty("code", 400);
            jsonObject.addProperty("desc", "clear cache fail");
        }
        result = jsonObject.toString();
        return result;
    }
}
