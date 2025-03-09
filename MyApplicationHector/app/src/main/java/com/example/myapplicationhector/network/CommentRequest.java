package com.example.myapplicationhector.network;

import com.google.gson.annotations.SerializedName;

public class CommentRequest {

    @SerializedName("user_id")
    private int userId;

    @SerializedName("comment_text")
    private String commentText;

    // Constructor
    public CommentRequest(int userId, String commentText) {
        this.userId = userId;
        this.commentText = commentText;
    }

    // Getters y Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }

    @Override
    public String toString() {
        return "CommentRequest{" +
                "userId=" + userId +
                ", commentText='" + commentText + '\'' +
                '}';
    }
}
