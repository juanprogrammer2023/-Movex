package com.example.myapplicationhector.network;

import com.google.gson.annotations.SerializedName;

public class Etiqueta {
    private int id;

    // Mapea el campo "nombre_etiqueta" del JSON al atributo "nombre" de la clase
    @SerializedName("nombre_etiqueta")
    private String nombre;

    // Constructor vacío
    public Etiqueta() {
    }

    // Constructor con parámetros
    public Etiqueta(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre; // Ajuste del método para que sea 'getNombre'
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Método toString para mostrar el nombre de la etiqueta en el Spinner y en cualquier vista que use la clase
    @Override
    public String toString() {
        return nombre;
    }
}
