package com.ringme.camid.Notification.service;

import com.google.gson.JsonObject;
import com.ringme.camid.Notification.repos.mysql.entity.WelcomePage;
import com.ringme.camid.Notification.repos.mysql.impl.WelcomePageDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WelcomePageService {
    private static final Logger logger = LogManager.getLogger(WelcomePageService.class);
    @Autowired
    private WelcomePageDao welcomePageDao;

    @Cacheable(value = "welcomepage", unless = "#result==null")
    public List<WelcomePage> getList() {
        List<WelcomePage> list = new ArrayList<>();
        try {
            list = welcomePageDao.getList();
        } catch (Exception e) {
            logger.error("getList|WelcomePage|Exception|" + e.getMessage(), e);
        }
        return list;
    }

    @CacheEvict(value = "welcomepage")
    public void clearCache() {
        logger.info("clearCache|Key|welcomepage");
    }


}
