package com.example.clonewhatsapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {

    public interface OnContactoClickListener {
        void onContactoClick(Contacto contacto);
    }

    private final List<Contacto> contactos;
    private final OnContactoClickListener listener;

    public ContactoAdapter(
            List<Contacto> contactos,
            OnContactoClickListener listener) {
        this.contactos = contactos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contacto, parent, false);

        return new ContactoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ContactoViewHolder holder, int position) {

        Contacto contacto = contactos.get(position);

        holder.tvNombre.setText(contacto.getNombreCompleto());

        // Imagen predeterminada mientras carga la fotografía.
        holder.ivFoto.setTag(null);
        holder.ivFoto.setImageResource(
                R.drawable.ic_launcher_foreground
        );

        String foto = contacto.getFotoPerfil();

        if (foto != null
                && !foto.isEmpty()
                && !foto.equals("null")) {

            ImagenAPI.cargarImagen(foto, holder.ivFoto);
        }

        holder.itemView.setOnClickListener(v ->
                listener.onContactoClick(contacto));
    }


    @Override
    public int getItemCount() {
        return contactos.size();
    }

    static class ContactoViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFoto;
        TextView tvNombre;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);

            ivFoto = itemView.findViewById(R.id.ivContactoFoto);
            tvNombre = itemView.findViewById(R.id.tvContactoNombre);
        }
    }
}
