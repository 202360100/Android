package com.example.clonewhatsapp;

public class Conversacion {

    private String id;
    private String tipo;
    private String nombre;
    private String foto;
    private String contactoId;
    private String ultimoContenido;
    private String ultimoCreatedAt;
    private String ultimoSender;

    public Conversacion(
            String id,
            String tipo,
            String nombre,
            String foto,
            String contactoId,
            String ultimoContenido,
            String ultimoCreatedAt,
            String ultimoSender
    ) {
        this.id = id;
        this.tipo = tipo;
        this.nombre = nombre;
        this.foto = foto;
        this.contactoId = contactoId;
        this.ultimoContenido = ultimoContenido;
        this.ultimoCreatedAt = ultimoCreatedAt;
        this.ultimoSender = ultimoSender;
    }

    public String getId() { return id; }
    public String getTipo() { return tipo; }
    public String getNombre() { return nombre; }
    public String getFoto() { return foto; }
    public String getContactoId() { return contactoId; }
    public String getUltimoContenido() { return ultimoContenido; }
    public String getUltimoCreatedAt() { return ultimoCreatedAt; }
    public String getUltimoSender() { return ultimoSender; }
}
