package com.example.listas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EstudianteAdapter
        extends RecyclerView.Adapter<EstudianteAdapter.EstudianteViewHolder> {

    private final List<String[]> estudiantes;

    public EstudianteAdapter(List<String[]> estudiantes) {
        this.estudiantes = estudiantes;
    }

    // ViewHolder: guarda las referencias a las vistas
    public static class EstudianteViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtNombre;
        TextView txtCarrera;

        public EstudianteViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtCarrera = itemView.findViewById(R.id.txtCarrera);
        }
    }

    // Crea la vista de cada elemento
    @NonNull
    @Override
    public EstudianteViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_estudiante, parent, false);

        return new EstudianteViewHolder(vista);
    }

    // Asigna los datos a las vistas
    @Override
    public void onBindViewHolder(
            @NonNull EstudianteViewHolder holder, int position) {

        String[] estudiante = estudiantes.get(position);

        holder.txtNombre.setText(estudiante[0]);
        holder.txtCarrera.setText(estudiante[1]);
    }

    // Devuelve el número de elementos
    @Override
    public int getItemCount() {
        return estudiantes.size();
    }
}
