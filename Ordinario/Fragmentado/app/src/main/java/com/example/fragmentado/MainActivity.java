package com.example.fragmentado;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    Button btnInicio, btnAcerca, btnContacto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enlazar los botones con el XML
        btnInicio = findViewById(R.id.btnInicio);
        btnAcerca = findViewById(R.id.btnAcerca);
        btnContacto = findViewById(R.id.btnContacto);

        // Mostrar Inicio al abrir la aplicación
        if (savedInstanceState == null) {
            cargarFragmento(new InicioFragment());
        }

        // Botón Inicio
        btnInicio.setOnClickListener(v -> {
            cargarFragmento(new InicioFragment());
        });

        // Botón Acerca de
        btnAcerca.setOnClickListener(v -> {
            cargarFragmento(new AcercaFragment());
        });

        // Botón Contacto
        btnContacto.setOnClickListener(v -> {
            cargarFragmento(new ContactoFragment());
        });
    }

    private void cargarFragmento(Fragment fragmento) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedorFragmentos, fragmento)
                .commit();
    }
}
