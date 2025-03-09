package com.example.myapplicationhector.network;

public class LoginResponse {
    private String token;
    private int userId; // Agrega este campo para el ID del usuario

    // Getter para el token
    public String getToken() {
        return token;
    }

    // Setter para el token
    public void setToken(String token) {
        this.token = token;
    }

    // Getter para el ID del usuario
    public int getUserId() {
        return userId;
    }

    // Setter para el ID del usuario
    public void setUserId(int userId) {
        this.userId = userId;
    }
}

