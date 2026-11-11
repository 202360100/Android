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

    private void cargarMensajes() {

        MensajesAPI.cargarMensajes(
                idConversacion,
                accessToken,
                new MensajesAPI.MensajesCallback() {

                    @Override
                    public void onSuccess(ArrayList<Mensaje> lista) {

                        mensajes.clear();
                        mensajes.addAll(lista);

                        adapter.notifyDataSetChanged();

                        if (!mensajes.isEmpty()) {
                            rvMensajes.scrollToPosition(mensajes.size() - 1);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(
                                ChatActivity.this,
                                "Error al cargar mensajes: " + error,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void enviarMensaje() {

        String contenido = etMensaje.getText().toString().trim();

        if (contenido.isEmpty()) return;

        MensajesAPI.enviarMensaje(
                idConversacion,
                userId,
                contenido,
                accessToken,
                new MensajesAPI.EnviarCallback() {

                    @Override
                    public void onSuccess(Mensaje mensaje) {

                        mensajes.add(mensaje);
                        adapter.notifyItemInserted(mensajes.size() - 1);

                        rvMensajes.scrollToPosition(mensajes.size() - 1);

                        etMensaje.setText("");
                    }

                    @Override
                    public void onError(String error) {

                        Toast.makeText(
                                ChatActivity.this,
                                "No se pudo enviar: " + error,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

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

        cargarMensajes();

        findViewById(R.id.btnEnviarMensaje).setOnClickListener(v -> {
            enviarMensaje();
        });

        findViewById(R.id.btnVolverChat).setOnClickListener(v -> {
            finish();
        });

    }
}
