package com.ringme.camid.Notification.repos.mysql.impl;

import com.ringme.camid.Notification.repos.mysql.entity.Survey;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class SurveyDao {
    private static final Logger logger = LogManager.getLogger(SurveyDao.class);
    @Autowired
    @Qualifier("kakoakCmsJdbcTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    // get 20 survey non process
    public List<Survey> getSurveyActive(int page, int size) {
        List<Survey> list = new ArrayList<>();
        String SQL = "SELECT cc.*, cs.title as seg_name,cs.phone_list, cs.file_path, cs.count, cs.input_type \n" +
                "                                 FROM camid_survey cc INNER JOIN camid_segment cs \n" +
                "                                 WHERE cc.segment_id=cs.id AND cc.active=1 AND cc.process_status = 0 \n" +
                "                                 limit :page, :size";
        try {
            list = jdbcTemplate.query(SQL, new MapSqlParameterSource()
                    .addValue("page", page * size).addValue("size", size), new RowMapper<Survey>() {
                @Override
                public Survey mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Survey survey = new Survey();
                    survey.setId(rs.getInt("id"));
                    survey.setName(rs.getString("name"));
                    survey.setTitle(rs.getString("title"));
                    survey.setMessage(rs.getString("message"));
                    survey.setLayout_style(rs.getInt("layout_style"));
                    survey.setSegment_id(rs.getInt("segment_id"));
                    survey.setIs_answer(rs.getInt("is_answer"));
                    survey.setInput_type(rs.getString("input_type"));
                    survey.setPhones(rs.getString("phone_list"));
                    survey.setFile_path(rs.getString("file_path"));
                    survey.setDelivery(rs.getString("delivery"));
                    survey.setImage(rs.getString("image"));
                    survey.setDeeplink(rs.getString("deeplink"));
                    return survey;
                }
            });

        } catch (Exception e) {
            logger.error("getSurvey|Exception|" + e.getMessage(), e);
        }
        return list;
    }

    public void updateSurvey(int id, int i) {
        String SQL = "UPDATE camid_survey SET process_status = :status, updated_at =:date WHERE id =:id";
        try {
            jdbcTemplate.update(SQL, new MapSqlParameterSource()
                    .addValue("status", i).addValue("id", id)
                    .addValue("date", new Date()));
        } catch (Exception e) {
            logger.error("updateSurvey|Exception|" + e.getMessage(), e);
        }
    }

    public int getCountSurvey() {
        int result = 0;
        String SQL = "SELECT count(*) from camid_survey WHERE process_status in (0,1)";
        try {
            result = jdbcTemplate.queryForObject(SQL, new MapSqlParameterSource(), Integer.class);
        } catch (Exception e) {
            logger.error("getCountSurvey|Exception|" + e.getMessage(), e);
            return 0;
        }
        return result;
    }

    public int finishSurvey(int id) {
        int result = 0;
        String SQL = "UPDATE camid_survey SET process_status=2, updated_at=NOW()\n" +
                "WHERE id=:id";
        try {
            result = jdbcTemplate.update(SQL, new MapSqlParameterSource().addValue("id", id));
        } catch (Exception e) {
            logger.error("finishSurvey|Exception|" + e.getMessage(), e);
        }
        return result;
    }
}
