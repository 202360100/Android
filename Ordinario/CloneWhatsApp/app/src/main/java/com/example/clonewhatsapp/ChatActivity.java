package com.example.clonewhatsapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Window;
import android.view.WindowInsets;
import android.view.View;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private String idConversacion;
    private String userId;
    private String accessToken;

    private RecyclerView rvMensajes;
    private EditText etMensaje;

    private MensajeAdapter adapter;
    private ArrayList<Mensaje> mensajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        View root = findViewById(android.R.id.content);

        root.setOnApplyWindowInsetsListener((view, insets) -> {

            int top = insets.getSystemWindowInsetTop();
            int bottom = insets.getSystemWindowInsetBottom();

            view.setPadding(0, top, 0, bottom);

            return insets;
        });

        idConversacion = getIntent().getStringExtra("idConversacion");

        String nombreContacto =
                getIntent().getStringExtra("nombreContacto");

        String fotoContacto =
                getIntent().getStringExtra("fotoContacto");

        android.content.SharedPreferences preferences =
                getSharedPreferences(
                        "supabase_session",
                        MODE_PRIVATE
                );

        userId = preferences.getString("user_id", null);
        accessToken = preferences.getString("access_token", null);

        TextView tvNombreChat = findViewById(R.id.tvNombreChat);
        ImageView ivFotoChat = findViewById(R.id.ivFotoChat);

        rvMensajes = findViewById(R.id.rvMensajes);
        etMensaje = findViewById(R.id.etMensaje);

        tvNombreChat.setText(nombreContacto);

        if (fotoContacto != null && !fotoContacto.isEmpty()) {
            ImagenAPI.cargarImagen(
                    fotoContacto,
                    ivFotoChat
            );
        }

        mensajes = new ArrayList<>();

        adapter = new MensajeAdapter(mensajes, userId);

        rvMensajes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvMensajes.setAdapter(adapter);

        if (idConversacion == null || userId == null
                || accessToken == null) {
            Toast.makeText(
                    this,
                    "No se pudieron cargar los datos del chat",
                    Toast.LENGTH_LONG
            ).show();
            finish();
            return;
        }

        findViewById(R.id.btnEnviarMensaje).setOnClickListener(v -> {
            String contenido = etMensaje.getText().toString().trim();

            if (contenido.isEmpty()) {
                return;
            }

            Toast.makeText(
                    this,
                    "Conectaremos el envío de mensajes en el siguiente paso",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}
