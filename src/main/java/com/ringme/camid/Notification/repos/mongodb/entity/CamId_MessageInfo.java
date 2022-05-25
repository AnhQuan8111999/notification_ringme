package com.ringme.camid.Notification.repos.mongodb.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(value = "message_info")
public class CamId_MessageInfo {
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
    private int status; //default = 1
    @Field(value = "type")
    private String type;
}
