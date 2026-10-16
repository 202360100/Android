package com.example.verthorizon;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CicloVida";

    private EditText editTexto;
    private TextView txtResultado;
    private Button btnMostrar;
    private static final String CLAVE_RESULTADO = "resultado";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Se ejecuto onCreate()");
        Toast.makeText(this, "onCreate()",
                Toast.LENGTH_SHORT).show();

        editTexto = findViewById(R.id.editTexto);
        txtResultado = findViewById(R.id.txtResultado);
        btnMostrar = findViewById(R.id.btnMostrar);

        if (savedInstanceState != null) {
            String datoGuardado =
                    savedInstanceState.getString(CLAVE_RESULTADO);

            txtResultado.setText(datoGuardado);
        }

        btnMostrar.setOnClickListener(v -> {
            String dato = editTexto.getText().toString();
            txtResultado.setText(dato);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "Se ejecuto onPause()");
        Toast.makeText(this, "onPause()",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "Se ejecuto onDestroy()");
        Toast.makeText(this, "onDestroy()",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(
                CLAVE_RESULTADO,
                txtResultado.getText().toString()
        );
    }
}