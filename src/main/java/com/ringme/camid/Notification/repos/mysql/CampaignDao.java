package com.ringme.camid.Notification.repos.mysql;

import com.ringme.camid.Notification.repos.mysql.entity.Campaign;

import java.util.List;

public interface CampaignDao {
    List<Campaign> getCampaign();

    void updateCampaign();

    List<Campaign> getCampaignUnCron_expression();

    List<Campaign> getCampaignCron_expression();
}
