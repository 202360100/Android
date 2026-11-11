
package com.example.clonewhatsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import org.json.JSONObject;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private TextView btnCerrarSesion;
    private TextView btnNuevoChat;
    private TextView tabChats;
    private TextView tabEstados;
    private TextView tabLlamadas;
    private ImageView ivFotoHome;
    private RecyclerView rvConversaciones;
    private TextView tvSinConversaciones;

    private ArrayList<Conversacion> listaConversaciones;
    private ConversacionAdapter conversacionAdapter;

    private String userId;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        android.content.SharedPreferences preferences =
                getSharedPreferences("supabase_session", MODE_PRIVATE);

        userId = preferences.getString("user_id", null);
        accessToken = preferences.getString("access_token", null);

        rvConversaciones = findViewById(R.id.rvConversaciones);
        tvSinConversaciones = findViewById(R.id.tvSinConversaciones);

        listaConversaciones = new ArrayList<>();

        conversacionAdapter = new ConversacionAdapter(
                listaConversaciones,
                conversacion -> {

                    Intent intent = new Intent(
                            HomeActivity.this,
                            ChatActivity.class
                    );

                    intent.putExtra(
                            "idConversacion",
                            conversacion.getId()
                    );

                    intent.putExtra(
                            "nombreContacto",
                            conversacion.getNombre()
                    );

                    intent.putExtra(
                            "fotoContacto",
                            conversacion.getFoto()
                    );

                    startActivity(intent);
                }
        );

        rvConversaciones.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvConversaciones.setAdapter(conversacionAdapter);

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

        btnNuevoChat.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    ContactosActivity.class
            );
            startActivity(intent);
        });

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

        cargarFotoPerfil();
        cargarConversaciones();
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

    private void cargarConversaciones() {

        if (accessToken == null) return;

        ConversacionesListaAPI.obtenerConversaciones(
                accessToken,
                new ConversacionesListaAPI.Callback() {

                    @Override
                    public void onSuccess(
                            ArrayList<Conversacion> conversaciones
                    ) {
                        listaConversaciones.clear();
                        listaConversaciones.addAll(conversaciones);

                        conversacionAdapter.notifyDataSetChanged();

                        tvSinConversaciones.setVisibility(
                                conversaciones.isEmpty()
                                        ? View.VISIBLE
                                        : View.GONE
                        );
                    }

                    @Override
                    public void onError(String error) {

                        Toast.makeText(
                                HomeActivity.this,
                                "Error al cargar conversaciones: " + error,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }
}
