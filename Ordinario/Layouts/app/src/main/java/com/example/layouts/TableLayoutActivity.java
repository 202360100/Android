package com.example.layouts;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TableLayoutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        EditText nombre = findViewById(R.id.edtNombre);
        EditText edad = findViewById(R.id.edtEdad);
        EditText correo = findViewById(R.id.edtCorreo);
        Button enviar = findViewById(R.id.btnEnviar);

        enviar.setOnClickListener(v -> {
            String datos = "Nombre: " +
                    nombre.getText().toString() +
                    "\nEdad: " + edad.getText().toString() +
                    "\nCorreo: " + correo.getText().toString();

            Toast.makeText(
                    this,
                    datos,
                    Toast.LENGTH_LONG
            ).show();
        });
    }
}

