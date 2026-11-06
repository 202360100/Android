package com.example.clonewhatsapp;

public class Mensaje {

    private String id;
    private String contenido;
    private String idProfile;
    private String createdAt;

    public Mensaje(
            String id,
            String contenido,
            String idProfile,
            String createdAt
    ) {
        this.id = id;
        this.contenido = contenido;
        this.idProfile = idProfile;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getContenido() {
        return contenido;
    }

    public String getIdProfile() {
        return idProfile;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
