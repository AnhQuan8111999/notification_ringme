package com.ringme.camid.Notification.repos.mysql.impl;

import com.ringme.camid.Notification.repos.mysql.CampaignDao;
import com.ringme.camid.Notification.repos.mysql.entity.Campaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CampaignDaoImpl implements CampaignDao {
    @Autowired
    @Qualifier("kakoakCmsJdbcTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static Logger logger = LoggerFactory.getLogger(CampaignDaoImpl.class);

    @Override
    public List<Campaign> getCampaign() {
        List<Campaign> campaigns = new ArrayList<>();
        String sql = "SELECT cc.*, cs.title as seg_name,cs.phone_list, cs.file_path, cs.count, cs.input_type\n" +
                " FROM camid_campaign cc INNER JOIN camid_segment cs \n" +
                " WHERE cc.segment_id=cs.id AND cc.active=1 AND cc.process_status IN (0,1)";
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
                campaign.setPhones(rs.getString("phone_list"));
                campaign.setInput_type(rs.getString("input_type"));
                campaign.setFile_path(rs.getString("file_path"));
                return campaign;
            }
        });
        return campaigns;
    }

    @Override
    public void updateCampaignDoneEndedAt() {
        String sql = "UPDATE camid_campaign SET process_status=2 WHERE ended_at < NOW() ";
        try {
            jdbcTemplate.update(sql, new MapSqlParameterSource());
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
                campaign.setDeeplink_param(rs.getString("deeplink_param"));
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

    public int getCountCampaign() {
        int result = 0;
        return result;
    }

    public List<Campaign> getCampaignProcessing(int counter, int page_size) {
        List<Campaign> list = new ArrayList<>();

        return list;
    }

    public int getCountUser() {
        int result = 0;

        return result;
    }

    public List<String> getActiveUsers(int offset, int limit) {
        List<String> list = new ArrayList<>();

        return list;
    }

    public void updateCampaignV2(String id) {
    }

    public void refreshCampaign(String id) {
    }

    public List<String> getProcessedCampaignId() {
        List<String> list = new ArrayList<>();

        return list;
    }

    public int finishCampaign(String id) {
        int result = 0;

        return result;
    }

    public int finishCampaign() {
        int result = 0;

        return result;
    }
}
