package com.ringme.camid.Notification.repos.mongodb;

import com.ringme.camid.Notification.repos.mongodb.entity.CamId_MessageInfo;
import com.ringme.camid.Notification.repos.mongodb.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MongoDao {
    @Autowired
    private MongoTemplate mongoTemplate;
    private static final Logger logger = LogManager.getLogger(MongoDao.class);

    public void saveMessageInfo(CamId_MessageInfo messageInfo) {
        try {
            mongoTemplate.save(messageInfo, "message_info");
        } catch (Exception e) {
            logger.error("saveMessageInfo|Exception|" + e.getMessage(), e);
        }

    }

    public User getRegID(String msisdn) {
        return new User();
    }

    public List<CamId_MessageInfo> getCampaign(String msisdn, int limit, int skip) {
        List<CamId_MessageInfo> list = new ArrayList<>();

        return list;
    }

    public void deleteMessageInfo(String msId) {
    }

    public long fillSeenCampaign(String msisdn) {
        long result = 0L;

        return result;
    }

    public void updateCampaign(String msId) {
    }
}
