package com.example.listas;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerEstudiantes;
    EstudianteAdapter adapter;
    List<String[]> estudiantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencia al RecyclerView del XML
        recyclerEstudiantes =
                findViewById(R.id.recyclerEstudiantes);

        // Crear los datos
        estudiantes = new ArrayList<>();

        estudiantes.add(new String[]{
                "Ana López", "Ingeniería en Sistemas"
        });

        estudiantes.add(new String[]{
                "Carlos Pérez", "Ingeniería Industrial"
        });

        estudiantes.add(new String[]{
                "María García", "Administración"
        });

        estudiantes.add(new String[]{
                "José Hernández", "Diseño Gráfico"
        });

        estudiantes.add(new String[]{
                "Laura Martínez", "Ingeniería Civil"
        });

        estudiantes.add(new String[]{
                "Daniel Torres", "Contaduría"
        });

        // Definir el posicionamiento vertical
        recyclerEstudiantes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Conectar los datos con el RecyclerView
        adapter = new EstudianteAdapter(estudiantes);
        recyclerEstudiantes.setAdapter(adapter);
    }
}