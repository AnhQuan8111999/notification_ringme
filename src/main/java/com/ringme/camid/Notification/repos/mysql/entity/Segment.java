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
public class Segment {
    private int id;
    private String title;
    @Nullable
    private String phone_list;
    @Nullable
    private String file_path;
    private int count;
    private String input_type;
}
