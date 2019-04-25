package com.example.bandup.userprofile;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserModel implements Serializable {

    private String imageurl;
    private String userid;
    private String username;

    public UserModel() {

    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
