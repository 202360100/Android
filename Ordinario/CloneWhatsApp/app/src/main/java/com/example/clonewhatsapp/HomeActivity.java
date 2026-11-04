
package com.example.clonewhatsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView btnCerrarSesion;
    private TextView btnNuevoChat;
    private TextView tabChats;
    private TextView tabEstados;
    private TextView tabLlamadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnNuevoChat = findViewById(R.id.btnNuevoChat);

        tabChats = findViewById(R.id.tabChats);
        tabEstados = findViewById(R.id.tabEstados);
        tabLlamadas = findViewById(R.id.tabLlamadas);

        btnCerrarSesion.setOnClickListener(v -> confirmarCierreSesion());

        btnNuevoChat.setOnClickListener(v ->
                Toast.makeText(this,
                        "La función de nuevo chat estará disponible próximamente.",
                        Toast.LENGTH_SHORT).show()
        );

        tabChats.setOnClickListener(v ->
                Toast.makeText(this, "Sección de chats", Toast.LENGTH_SHORT).show()
        );

        tabEstados.setOnClickListener(v ->
                Toast.makeText(this, "Sección de estados próximamente",
                        Toast.LENGTH_SHORT).show()
        );

        tabLlamadas.setOnClickListener(v ->
                Toast.makeText(this, "Sección de llamadas próximamente",
                        Toast.LENGTH_SHORT).show()
        );
    }

    private void confirmarCierreSesion() {

        new AlertDialog.Builder(this)
                .setTitle("CERRAR SESIÓN")
                .setMessage("¿Seguro que deseas desconectarte?")
                .setNegativeButton("CANCELAR", null)
                .setPositiveButton("DESCONECTAR", (dialog, which) -> cerrarSesion())
                .show();
    }

    private void cerrarSesion() {

        SharedPreferences preferences = getSharedPreferences(
                "supabase_session", MODE_PRIVATE
        );

        preferences.edit().clear().apply();

        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }
}
