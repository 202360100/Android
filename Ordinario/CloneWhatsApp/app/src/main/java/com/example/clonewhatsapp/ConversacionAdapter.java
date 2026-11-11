package com.example.clonewhatsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ConversacionAdapter
        extends RecyclerView.Adapter<ConversacionAdapter.ViewHolder> {

    public interface OnConversacionClick {
        void onClick(Conversacion conversacion);
    }

    private final List<Conversacion> conversaciones;
    private final OnConversacionClick listener;

    public ConversacionAdapter(
            List<Conversacion> conversaciones,
            OnConversacionClick listener
    ) {
        this.conversaciones = conversaciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversacion, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        Conversacion conversacion = conversaciones.get(position);

        holder.tvNombre.setText(conversacion.getNombre());

        String ultimoMensaje = conversacion.getUltimoContenido();

        if (ultimoMensaje == null || ultimoMensaje.isEmpty()) {
            ultimoMensaje = "Inicia una conversación";
        }

        holder.tvUltimoMensaje.setText(ultimoMensaje);

        holder.ivFoto.setImageResource(
                android.R.drawable.sym_def_app_icon
        );

        if (conversacion.getFoto() != null
                && !conversacion.getFoto().isEmpty()) {

            ImagenAPI.cargarImagen(
                    conversacion.getFoto(),
                    holder.ivFoto
            );
        }

        holder.itemView.setOnClickListener(v ->
                listener.onClick(conversacion)
        );
    }

    @Override
    public int getItemCount() {
        return conversaciones.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFoto;
        TextView tvNombre;
        TextView tvUltimoMensaje;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivFoto = itemView.findViewById(
                    R.id.ivFotoConversacion
            );

            tvNombre = itemView.findViewById(
                    R.id.tvNombreConversacion
            );

            tvUltimoMensaje = itemView.findViewById(
                    R.id.tvUltimoMensaje
            );
        }
    }
}
