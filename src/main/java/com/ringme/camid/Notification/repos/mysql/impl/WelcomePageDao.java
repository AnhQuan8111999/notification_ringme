package com.ringme.camid.Notification.repos.mysql.impl;

import com.ringme.camid.Notification.repos.mysql.entity.WelcomePage;
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
import java.util.List;

@Repository
public class WelcomePageDao {
    private static final Logger logger = LogManager.getLogger(WelcomePageDao.class);
    @Autowired
    @Qualifier("kakoakCmsJdbcTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<WelcomePage> getList() {
        List<WelcomePage> list = new ArrayList<>();
        String SQL = "SELECT wp.*, wpn.id AS page FROM welcome_page wp join welcome_page_number wpn WHERE wp.active = 1 and wp.id=wpn.welcome_page_id\n" +
                "ORDER BY wpn.id asc";
        try {
            list = jdbcTemplate.query(SQL, new MapSqlParameterSource(), new RowMapper<WelcomePage>() {
                @Override
                public WelcomePage mapRow(ResultSet rs, int rowNum) throws SQLException {
                    WelcomePage welcomePage = new WelcomePage();
                    welcomePage.setId(rs.getInt("id"));
                    welcomePage.setTitle(rs.getString("title"));
                    welcomePage.setContent(rs.getString("content"));
                    welcomePage.setPage(rs.getInt("page"));
                    return welcomePage;
                }
            });
        } catch (Exception e) {
            logger.error("getList|WelcomePage|Exception|" + e.getMessage(), e);
            return null;
        }
        if (list.size() < 1) {
            return null;
        }
        return list;
    }

    public void updateStatus(int id, int status) {
        String SQL = "UPDATE welcome_page SET active = :status WHERE id = :id";
        try {
            jdbcTemplate.update(SQL, new MapSqlParameterSource().addValue("status", status)
                    .addValue("id", id));
        } catch (Exception e) {
            logger.error("updateStatus|Exception|" + e.getMessage(), e);
        }
    }


}
