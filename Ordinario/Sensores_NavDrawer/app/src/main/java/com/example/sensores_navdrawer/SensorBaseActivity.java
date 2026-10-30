package com.example.sensores_navdrawer;

import android.os.Bundle;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

public abstract class SensorBaseActivity extends AppCompatActivity {

    protected abstract String obtenerNombreSensor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String titulo = obtenerNombreSensor();

        TextView texto = new TextView(this);
        texto.setText("Ejemplo de " + titulo +
                "\n\nAquí implementaremos su funcionamiento.");
        texto.setTextSize(22);
        texto.setTextColor(Color.rgb(33, 33, 33));
        texto.setGravity(Gravity.CENTER);
        texto.setPadding(30, 30, 30, 30);

        setContentView(texto);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titulo);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}