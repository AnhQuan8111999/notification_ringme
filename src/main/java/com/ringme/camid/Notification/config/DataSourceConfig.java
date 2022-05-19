package com.ringme.camid.Notification.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    //kakoakcms
    @Primary
    @Bean(name = "appDataSourceProperties")
    @ConfigurationProperties("database.app.datasource")
    public DataSourceProperties videoDataSourceProperties() {
        return new DataSourceProperties();
    }

    //kakoakcms
    @Primary
    @Bean(name = "appDataSource")
    @ConfigurationProperties("database.app.datasource.hikari")
    public DataSource videoDataSource() {
        return videoDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "appNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate videoNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(videoDataSource());
    }

    @Bean(name = "appJdbcTemplate")
    public JdbcTemplate videoJdbcTemplate() {
        return new JdbcTemplate(videoDataSource());
    }

    //kakoak
    @Primary
    @Bean(name = "kakoakDataSourceProperties")
    @ConfigurationProperties("database.kakoak.datasource")
    public DataSourceProperties kakoakDataSourceProperties() {
        return new DataSourceProperties();
    }

    // kakoak
    @Primary
    @Bean(name = "kakoakDataSource")
    @ConfigurationProperties("database.kakoak.datasource.hikari")
    public DataSource kakoakDataSource() {
        return kakoakDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "kakoakJdbcTemplate")
    public JdbcTemplate kakoakJdbcTemplate() {
        return new JdbcTemplate(kakoakDataSource());
    }
}
