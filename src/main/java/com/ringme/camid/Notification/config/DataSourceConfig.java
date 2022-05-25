package com.ringme.camid.Notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource kakoakdataSource(){
//        System.out.println(driverClass+" "+ url+" "++" "+password);
        DriverManagerDataSource source = new DriverManagerDataSource();
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl("jdbc:mysql://192.168.1.88:3306/kakoak");
        source.setUsername("dbcms");
        source.setPassword("120M1Tko4kaK53rv1cE");
        return source;
    }

    @Bean(name = "kakoakJdbcTemplate")
    public NamedParameterJdbcTemplate kakoakJdbcTemplate(){
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.kakoakdataSource());
        return namedParameterJdbcTemplate;
    }

    @Bean
    public DataSource kakoakCmsdataSource(){
//        System.out.println(driverClass+" "+ url+" "++" "+password);
        DriverManagerDataSource source = new DriverManagerDataSource();
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl("jdbc:mysql://192.168.1.88:3306/helpdesk_camid");
        source.setUsername("ukakoak");
        source.setPassword("Kakoak@123");
        return source;
    }

    @Bean(name = "kakoakCmsJdbcTemplate")
    public NamedParameterJdbcTemplate kakoakCmsJdbcTemplate(){
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(this.kakoakCmsdataSource());
        return namedParameterJdbcTemplate;
    }
}
