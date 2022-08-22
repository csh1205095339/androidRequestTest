package com.example.requesttest;

import com.google.gson.annotations.SerializedName;

public class UserBean {
    @SerializedName("name")
    String name;
    @SerializedName("job")
    String job;
    @SerializedName("id")
    Integer id;
    @SerializedName("avatar")
    String avatar;
}
