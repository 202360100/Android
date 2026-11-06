
package com.example.clonewhatsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import org.json.JSONObject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView btnCerrarSesion;
    private TextView btnNuevoChat;
    private TextView tabChats;
    private TextView tabEstados;
    private TextView tabLlamadas;
    private ImageView ivFotoHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnNuevoChat = findViewById(R.id.btnNuevoChat);

        tabChats = findViewById(R.id.tabChats);
        tabEstados = findViewById(R.id.tabEstados);
        tabLlamadas = findViewById(R.id.tabLlamadas);
        ivFotoHome = findViewById(R.id.ivFotoHome);

        ivFotoHome.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    PerfilActivity.class
            );
            startActivity(intent);
        });

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

    @Override
    protected void onResume() {
        super.onResume();

        if (ivFotoHome != null) {
            cargarFotoPerfil();
        }
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

    private void cargarFotoPerfil() {

        SharedPreferences preferences = getSharedPreferences(
                "supabase_session",
                MODE_PRIVATE
        );

        String userId = preferences.getString("user_id", null);
        String accessToken = preferences.getString("access_token", null);

        if (userId == null || accessToken == null) {
            return;
        }

        PerfilAPI api = new PerfilAPI();

        api.consultarPerfil(userId, accessToken,
                new PerfilAPI.PerfilCallback() {

                    @Override
                    public void onSuccess(JSONObject perfil) {

                        String fotoUrl = perfil.optString(
                                "foto_perfil", ""
                        );

                        if (!fotoUrl.isEmpty()
                                && !fotoUrl.equals("null")) {

                            ImagenAPI.cargarImagen(
                                    fotoUrl,
                                    ivFotoHome
                            );
                        }
                    }

                    @Override
                    public void onError(String mensaje) {
                        // Se conserva la imagen predeterminada.
                    }
                });
    }
}
