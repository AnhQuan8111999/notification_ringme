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

    @Autowired
    @Qualifier("kakoakJdbcTemplate")
    private NamedParameterJdbcTemplate kakoakJdbcTemplate;

    private static Logger logger = LoggerFactory.getLogger(CampaignDaoImpl.class);

    @Override
    public List<Campaign> getCampaign() {
        List<Campaign> campaigns = new ArrayList<>();
        String sql = "SELECT cc.*, cs.title as seg_name,cs.phone_list, cs.file_path, cs.count, cs.input_type\n" +
                " FROM camid_campaign cc INNER JOIN camid_segment cs \n" +
                " WHERE cc.segment_id=cs.id AND cc.active=1 AND cc.process_status = 0 " +
                " And started_at < NOW() AND ended_at > NOW() limit 0,20 ";
        try {
            campaigns = jdbcTemplate.query(sql,
                    new RowMapper<Campaign>() {
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
                            campaign.setUpdated_at(rs.getDate("updated_at"));
                            return campaign;
                        }
                    });
        } catch (Exception e) {
            logger.error("getCampaign|Exception|" + e.getMessage(), e);
        }

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
                campaign.setPhones(rs.getString("phone_list"));
                campaign.setInput_type(rs.getString("input_type"));
                campaign.setFile_path(rs.getString("file_path"));
                campaign.setUpdated_at(rs.getDate("updated_at"));
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

    public int getCountCampaign() {
        int result = 0;
        String SQL = "SELECT count(*) from camid_campaign WHERE process_status in (0,1) AND ended_at>=NOW() ";
        try {
            result = jdbcTemplate.queryForObject(SQL, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            logger.error("getCountNotification|Exception|" + e.getMessage(), e);
            return 0;
        }
        return result;
    }

    public List<Campaign> getCampaignProcessing(int counter, int page_size) {
        List<Campaign> campaigns = new ArrayList<>();
        String sql = "SELECT cc.*, cs.title as seg_name,cs.phone_list, cs.file_path, cs.count, cs.input_type\n" +
                " FROM camid_campaign cc INNER JOIN camid_segment cs \n" +
                " WHERE cc.segment_id=cs.id AND cc.active=1 AND cc.process_status = 1 " +
                " And started_at < NOW() AND ended_at > NOW() limit :offset,:limit";
        campaigns = jdbcTemplate.query(sql, new MapSqlParameterSource()
                        .addValue("offset", counter * page_size)
                        .addValue("limit", page_size),
                new RowMapper<Campaign>() {
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
                        campaign.setUpdated_at(rs.getDate("updated_at"));
                        return campaign;
                    }
                });
        return campaigns;
    }

    public int getCountUser() {
        int result = 0;
        String SQL = "SELECT count(*) FROM kakoak.users WHERE active =1 AND country_code=\"TL\"";
        try {
            result = kakoakJdbcTemplate.queryForObject(SQL, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            logger.error("getCountUser|Exception|" + e.getMessage(), e);
        }
        return result;
    }

    public List<String> getActiveUsers(int offset, int limit) {
        List<String> phones = new ArrayList<>();
        try {
            String SQL = "SELECT username FROM kakoak.users where country_code=\"TL\" and active =1\n" +
                    "limit :offset,:limit";
            phones = kakoakJdbcTemplate.queryForList(SQL, new MapSqlParameterSource()
                    .addValue("offset", offset)
                    .addValue("limit", limit), String.class);
        } catch (Exception e) {
            logger.error("getActiveUsers|Exception|" + e.getMessage(), e);
            return new ArrayList<>();
        }

        return phones;
    }

    public void updateCampaign(String id, int status) {
        String SQL = "UPDATE camid_campaign SET process_status=:status, updated_at = NOW()\n" +
                " WHERE id=:id";
        try {
            jdbcTemplate.update(SQL, new MapSqlParameterSource().addValue("id", id).addValue("status", status));
        } catch (Exception e) {
            logger.error("updateNotification|Exception|" + e.getMessage(), e);
        }
    }

    public void refreshCampaign(String id) {
        String SQL = "UPDATE camid_campaign SET process_status=0, updated_at=NOW()\n" +
                "WHERE id=:id";
        try {
            jdbcTemplate.update(SQL, new MapSqlParameterSource().addValue("id", id));
        } catch (Exception e) {
            logger.error("refreshNotification|Exception|" + e.getMessage(), e);
        }
    }

    public List<String> getProcessedCampaignId() {
        String SQL = "select id from camid_campaign where ended_at< NOW() and process_status in(0,1)";
        List<String> list = new ArrayList<>();
        try {
            list = jdbcTemplate.queryForList(SQL, new MapSqlParameterSource(), String.class);
        } catch (Exception e) {
            logger.error("getProcessedNotificationId|Exception|" + e.getMessage(), e);
        }
        return list;
    }

    public int finishCampaign(String id) {
        int result = 0;
        String SQL = "UPDATE camid_campaign SET process_status=2, updated_at=NOW()\n" +
                "WHERE id=:id";
        try {
            result = jdbcTemplate.update(SQL, new MapSqlParameterSource().addValue("id", id));
        } catch (Exception e) {
            logger.error("finishNotification|Exception|" + e.getMessage(), e);
        }
        return result;
    }

    public int finishCampaign() {
        int result = 0;
        String SQL = "UPDATE camid_campaign SET process_status=2, updated_at=NOW()\n" +
                "WHERE ended_at < NOW() and process_status in(0,1)";
        try {
            result = jdbcTemplate.update(SQL, new MapSqlParameterSource());
        } catch (Exception e) {
            logger.error("finishNotification|Exception|" + e.getMessage(), e);
        }
        return result;
    }

}
