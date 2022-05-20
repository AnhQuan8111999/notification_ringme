package com.ringme.camid.Notification.repos.mysql.impl;

import com.ringme.camid.Notification.repos.mysql.CampaignDao;
import com.ringme.camid.Notification.repos.mysql.entity.Campaign;
import com.ringme.camid.Notification.service.CampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CampaignDaoImpl implements CampaignDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    private static Logger logger = LoggerFactory.getLogger(CampaignDaoImpl.class);

    @Override
    public List<Campaign> getCampaign() {
        List<Campaign> campaigns = new ArrayList<>();
        String sql = "SELECT * FROM camid_campaign WHERE active=1 AND process_status IN (0,1)";
        campaigns = jdbcTemplate.query(sql, new RowMapper<Campaign>() {
            @Override
            public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException {
                Campaign campaign = new Campaign();
                campaign.setId(rs.getString("id"));
                campaign.setTitle(rs.getString("title"));
                campaign.setLayout_position(rs.getString("layout_position"));
                campaign.setLayout_style(rs.getInt("layout_style"));
                campaign.setButton_layout(rs.getInt("button_layout"));
                campaign.setMessage(rs.getString("message"));
                campaign.setImage(rs.getString("image"));
                campaign.setDeeplink(rs.getString("deeplink"));
                campaign.setStarted_at(rs.getDate("started_at"));
                campaign.setEnded_at(rs.getDate("ended_at"));
                campaign.setEnable_popup(rs.getInt("enable_popup"));
                campaign.setVersion(rs.getString("version"));
                campaign.setCron_expression(rs.getString("cron_expression"));
                campaign.setDisplay_in_app(rs.getInt("display_in_app"));
                campaign.setOn_event(rs.getString("on_event"));
                campaign.setDelay(rs.getInt("delay"));
                campaign.setUnit(rs.getString("unit"));
                campaign.setDuration(rs.getInt("duration"));
                campaign.setPriority(rs.getInt("priority"));
                campaign.setDisplay_one_per(rs.getString("display_one_per"));
                campaign.setActive(rs.getInt("active"));
                campaign.setProcess_status(rs.getInt("process_status"));
                campaign.setSegment_id(rs.getString("segment_id"));
                return campaign;
            }
        });
        return campaigns;
    }

    @Override
    public void updateCampaign() {
        String sql = "UPDATE camid_campaign SET process_status=2 WHERE ended_at <= NOW() ";
        try {
            jdbcTemplate.update(sql);
        } catch (Exception e) {
            logger.info("updateCampaign|EXCEPTION : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Campaign> getCampaignUnCron_expression() {
        List<Campaign> campaigns = new ArrayList<>();
        String sql = "SELECT * FROM camid_campaign WHERE cron_expression is null";
        campaigns = jdbcTemplate.query(sql, new RowMapper<Campaign>() {
            @Override
            public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException {
                Campaign campaign = new Campaign();
                campaign.setId(rs.getString("id"));
                campaign.setTitle(rs.getString("title"));
                campaign.setLayout_position(rs.getString("layout_position"));
                campaign.setLayout_style(rs.getInt("layout_style"));
                campaign.setButton_layout(rs.getInt("button_layout"));
                campaign.setMessage(rs.getString("message"));
                campaign.setImage(rs.getString("image"));
                campaign.setDeeplink(rs.getString("deeplink"));
                campaign.setStarted_at(rs.getDate("started_at"));
                campaign.setEnded_at(rs.getDate("ended_at"));
                campaign.setEnable_popup(rs.getInt("enable_popup"));
                campaign.setVersion(rs.getString("version"));
                campaign.setCron_expression(rs.getString("cron_expression"));
                campaign.setDisplay_in_app(rs.getInt("display_in_app"));
                campaign.setOn_event(rs.getString("on_event"));
                campaign.setDelay(rs.getInt("delay"));
                campaign.setUnit(rs.getString("unit"));
                campaign.setDuration(rs.getInt("duration"));
                campaign.setPriority(rs.getInt("priority"));
                campaign.setDisplay_one_per(rs.getString("display_one_per"));
                campaign.setActive(rs.getInt("active"));
                campaign.setProcess_status(rs.getInt("process_status"));
                campaign.setSegment_id(rs.getString("segment_id"));
                return campaign;
            }
        });
        return campaigns;
    }

    @Override
    public List<Campaign> getCampaignCron_expression() {
        List<Campaign> campaigns = new ArrayList<>();
        String sql = "SELECT * FROM camid_campaign WHERE cron_expression is not null";
        campaigns = jdbcTemplate.query(sql, new RowMapper<Campaign>() {
            @Override
            public Campaign mapRow(ResultSet rs, int rowNum) throws SQLException {
                Campaign campaign = new Campaign();
                campaign.setId(rs.getString("id"));
                campaign.setTitle(rs.getString("title"));
                campaign.setLayout_position(rs.getString("layout_position"));
                campaign.setLayout_style(rs.getInt("layout_style"));
                campaign.setButton_layout(rs.getInt("button_layout"));
                campaign.setMessage(rs.getString("message"));
                campaign.setImage(rs.getString("image"));
                campaign.setDeeplink(rs.getString("deeplink"));
                campaign.setStarted_at(rs.getDate("started_at"));
                campaign.setEnded_at(rs.getDate("ended_at"));
                campaign.setEnable_popup(rs.getInt("enable_popup"));
                campaign.setVersion(rs.getString("version"));
                campaign.setCron_expression(rs.getString("cron_expression"));
                campaign.setDisplay_in_app(rs.getInt("display_in_app"));
                campaign.setOn_event(rs.getString("on_event"));
                campaign.setDelay(rs.getInt("delay"));
                campaign.setUnit(rs.getString("unit"));
                campaign.setDuration(rs.getInt("duration"));
                campaign.setPriority(rs.getInt("priority"));
                campaign.setDisplay_one_per(rs.getString("display_one_per"));
                campaign.setActive(rs.getInt("active"));
                campaign.setProcess_status(rs.getInt("process_status"));
                campaign.setSegment_id(rs.getString("segment_id"));
                return campaign;
            }
        });
        return campaigns;
    }
}
