package com.ringme.camid.Notification.service;

import com.ringme.camid.Notification.repos.mysql.CampaignDao;
import com.ringme.camid.Notification.repos.mysql.entity.Campaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CampaignService {
    @Autowired
    CampaignDao campaignDao;

    private static Logger logger = LoggerFactory.getLogger(CampaignService.class);

//    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public List<Campaign> getCampaigns(){
        List<Campaign> campaigns=new ArrayList<>();
        try {
            campaigns = campaignDao.getCampaign();
        }catch(Exception e){
            logger.info("GetCampaingn| Exception : " + e);
        }
        return campaigns;
    }

//    @Scheduled(fixedDelay = 1000, initialDelay = 1000)
    public void updateCampaign(){
        campaignDao.updateCampaign();
    }

    public List<Campaign> getCampaignUnCron_expression(){
        List<Campaign> campaigns=new ArrayList<>();
        try {
            campaigns = campaignDao.getCampaignUnCron_expression();
        }catch(Exception e){
            logger.info("getCampaignUnCron_expression| Exception : " + e);
        }
        return campaigns;
    }

    public List<Campaign> getCampaignCron_expression(){
        List<Campaign> campaigns=new ArrayList<>();
        try {
            campaigns = campaignDao.getCampaignCron_expression();
        }catch(Exception e){
            logger.info("getCampaignCron_expression| Exception : " + e);
        }
        return campaigns;
    }
}
