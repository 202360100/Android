package com.example.clonewhatsapp;

public class EstadoMensaje {

    private String idMessage;
    private String idProfile;
    private String estado;
    private String deliveredAt;
    private String readAt;

    public EstadoMensaje(
            String idMessage,
            String idProfile,
            String estado,
            String deliveredAt,
            String readAt
    ) {
        this.idMessage = idMessage;
        this.idProfile = idProfile;
        this.estado = estado;
        this.deliveredAt = deliveredAt;
        this.readAt = readAt;
    }

    public String getIdMessage() {
        return idMessage;
    }

    public String getIdProfile() {
        return idProfile;
    }

    public String getEstado() {
        return estado;
    }

    public String getDeliveredAt() {
        return deliveredAt;
    }

    public String getReadAt() {
        return readAt;
    }
}
