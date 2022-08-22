package com.example.requesttest;
import com.google.gson.annotations.SerializedName;
public class PageResponseBean {
    @SerializedName("page")
    Integer page;

    @SerializedName("total_pages")
    Integer totalPages;

    @SerializedName("total")
    Integer total;
}
