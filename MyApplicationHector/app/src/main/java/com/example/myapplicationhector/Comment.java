package com.example.myapplicationhector;

public class Comment {
    private int commentId;       // Identificador único del comentario
    private String userName;     // Nombre del usuario que hizo el comentario
    private String text;         // Texto del comentario
    private String userImageUrl; // URL de la imagen de perfil del usuario
    private String createdAt;    // Fecha de creación del comentario

    // Constructor
    public Comment(int commentId, String userName, String text, String userImageUrl, String createdAt) {
        this.commentId = commentId;
        this.userName = userName;
        this.text = text;
        this.userImageUrl = userImageUrl;
        this.createdAt = createdAt;
    }

    // Getters y setters
    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
