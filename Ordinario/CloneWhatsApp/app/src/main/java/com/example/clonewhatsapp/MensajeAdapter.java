package com.example.clonewhatsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.ViewHolder> {

    private final List<Mensaje> mensajes;
    private final String usuarioActual;

    public MensajeAdapter(
            List<Mensaje> mensajes,
            String usuarioActual
    ) {
        this.mensajes = mensajes;
        this.usuarioActual = usuarioActual;
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).getIdProfile().equals(usuarioActual)
                ? 1 : 2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        int layout = viewType == 1
                ? R.layout.item_mensaje_enviado
                : R.layout.item_mensaje_recibido;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        Mensaje mensaje = mensajes.get(position);

        holder.tvContenido.setText(mensaje.getContenido());
        holder.tvHora.setText(obtenerHora(mensaje.getCreatedAt()));
    }

    private String obtenerHora(String fecha) {
        try {
            SimpleDateFormat entrada = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
                    Locale.getDefault()
            );

            Date date = entrada.parse(fecha);

            if (date == null) return "";

            SimpleDateFormat salida = new SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
            );

            return salida.format(date);

        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvContenido;
        TextView tvHora;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvContenido = itemView.findViewById(
                    R.id.tvContenidoMensaje
            );

            tvHora = itemView.findViewById(
                    R.id.tvHoraMensaje
            );
        }
    }
}
