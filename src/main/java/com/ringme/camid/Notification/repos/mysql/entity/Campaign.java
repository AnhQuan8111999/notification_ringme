package com.ringme.camid.Notification.repos.mysql.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {
    private String id;
    private String title;
    private String layout_position;
    private int layout_style;
    private int button_layout;
    private String message;
    private String image;
    private String deeplink;
    private Date started_at;
    private Date ended_at;
    private int enable_popup;
    private String version;
    @Nullable
    private String cron_expression;
    private int display_in_app;
    private String on_event;
    private int delay;
    private String unit;
    private int duration;
    private int priority;
    private String display_one_per;
    private int active;
    private int process_status;
    private String segment_id;
    @Nullable
    private String[] phone_lists;
    @Nullable
    private String file_path;
    private String input_type;
    private Date updated_at;
    @Nullable
    private String button1_name;
    @Nullable
    private String button1_deeplink;
    @Nullable
    private String button2_name;
    @Nullable
    private String button2_deeplink;

    public void setPhones(String phones) {
        if (null != phones) {
            this.phone_lists = phones.split("\\,");
        }
    }
}
