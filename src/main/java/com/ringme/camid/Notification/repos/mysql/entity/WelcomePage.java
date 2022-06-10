package com.ringme.camid.Notification.repos.mysql.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WelcomePage implements Serializable {
    private int id;
    private String title;
    private String content;
    private int page;

}
