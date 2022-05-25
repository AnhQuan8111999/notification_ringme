package com.ringme.camid.Notification.repos.mongodb.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

import java.util.Date;

@Document(collection = "users")
public class User {
    public String username;
    public String password;
    public String active;
    public String status;
    public String gender;
    public Date password_expired;
    public Date created_at;
    public String revision;
    public String platform;
    public String device_name;
    public String app_version;
    public String os_version;
    public String country_code;
    public String uuid;
    public Date last_login;
    public boolean block_vqmm;
    public boolean block_sponsor;
    public int sponsor_size;
    public String token;
    public String provision;
    public String language;
    public String version;
    public String e2e_prekey;
    @Nullable
    public String regid;
    public String birthday;
    public String name;
    public String lastDeactive;

    public User() {
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
