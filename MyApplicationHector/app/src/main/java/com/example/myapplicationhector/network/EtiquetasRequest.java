package com.example.myapplicationhector.network;

import java.util.List;

public class EtiquetasRequest {
    private int postId;
    private List<Integer> etiquetas;

    // Constructor
    public EtiquetasRequest(int postId, List<Integer> etiquetas) {
        this.postId = postId;
        this.etiquetas = etiquetas;
    }

    // Getters y Setters
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public List<Integer> getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(List<Integer> etiquetas) {
        this.etiquetas = etiquetas;
    }
}
