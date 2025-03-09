package com.example.myapplicationhector.network;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("name")
    private String name;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("password")
    private String password;

    @SerializedName("profileImage")
    private String profileImage;

    // Constructor de cinco parámetros (sin imagen de perfil)
    public User(String name, String lastName, String email, String phone, String password) {
        this(name, lastName, email, phone, password, ""); // Llama al constructor principal con una imagen vacía por defecto
    }

    // Constructor de seis parámetros (con imagen de perfil)
    public User(String name, String lastName, String email, String phone, String password, String profileImage) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.profileImage = profileImage;
    }

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}
