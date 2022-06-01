package com.ringme.camid.Notification.repos.mysql.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Survey {
    private int id;
    private String name;
    private String title;
    private String message;
    private int layout_style;
    private int is_answer;
    private int segment_id;
    private String delivery;
    private int delivery_intime;
    @Nullable
    private String[] phone_lists;
    @Nullable
    private String file_path;
    private String input_type;
    private String image;
    private String deeplink;

    public void setPhones(String phones) {
        if (null != phones) {
            this.phone_lists = phones.split("\\,");
        }
    }
}
