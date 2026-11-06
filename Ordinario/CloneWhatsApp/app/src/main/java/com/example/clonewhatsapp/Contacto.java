package com.example.clonewhatsapp;

public class Contacto {

    private String id;
    private String nombre;
    private String apellido;
    private String fotoPerfil;

    public Contacto(String id, String nombre,
                    String apellido, String fotoPerfil) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fotoPerfil = fotoPerfil;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
