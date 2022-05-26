package com.ringme.camid.Notification.service;

import com.ringme.camid.Notification.repos.mongodb.MongoDao;
import com.ringme.camid.Notification.repos.mongodb.entity.CamId_MessageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SegmentSercive {
    @Autowired
    private MongoDao mongoDao;

    public void saveMessageInfo(CamId_MessageInfo messageInfo) {
        mongoDao.saveMessageInfo(messageInfo);
    }
}
