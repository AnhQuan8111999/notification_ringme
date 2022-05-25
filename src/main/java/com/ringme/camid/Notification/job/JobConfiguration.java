package com.ringme.camid.Notification.job;

import com.ringme.camid.Notification.repos.mysql.entity.Campaign;
import com.ringme.camid.Notification.service.CampaignService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class JobConfiguration extends QuartzJobBean {
    private static final Logger logger = LogManager.getLogger(JobConfiguration.class);

    @Override
    protected void executeInternal(JobExecutionContext jec) throws JobExecutionException {
        JobDataMap dataMap = jec.getJobDetail().getJobDataMap();
        CampaignService service = (CampaignService) dataMap.get("service");
        Campaign campaign = (Campaign) dataMap.get("entity");
        try {
            service.process_Notification(campaign);
//            service.refreshNotification(notification.getId());
            logger.info("NotificationJob|Process_Notification|JobKey|job." + campaign.getId());
        } catch (Exception e) {
            logger.error("NotificationJob|Exception|" + e.getMessage(), e);
        }
    }
}
