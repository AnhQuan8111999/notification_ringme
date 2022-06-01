package com.ringme.camid.Notification.repos.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "message_survey")
public class Camid_SurveyMessage {
    @Id
    private String id;
    @Field(value = "msisdn")
    private String msisdn;
    @Field(value = "content")
    private String content;
    @Field(value = "thumbnail")
    private String thumbnail;
    @Field(value = "notified_date")
    private Date notified_date;
    @Field(value = "deep_link")
    private String deep_link;
    @Field(value = "status")
    private int status; //default = 0
    @Field(value = "type")
    private String type;
    @Field(value = "satisfied")
    private long satisfied;
    @Field(value = "normal")
    private long normal;
    @Field(value = "unsatisfied")
    private long unsatisfied;

}
