package com.example.myapplicationhector;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName("imageUrl")
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }
}

