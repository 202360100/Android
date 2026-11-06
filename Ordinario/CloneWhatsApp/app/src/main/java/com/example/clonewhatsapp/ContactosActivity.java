package com.example.clonewhatsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactosActivity extends AppCompatActivity {

    private RecyclerView rvContactos;
    private EditText etBuscarContacto;
    private TextView tvSinContactos;
    private Button btnVolverContactos;

    private ContactosAPI contactosAPI;
    private ContactoAdapter adapter;

    private String userId;
    private String accessToken;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable busquedaPendiente = () ->
            cargarContactos(etBuscarContacto.getText().toString());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        rvContactos = findViewById(R.id.rvContactos);
        etBuscarContacto = findViewById(R.id.etBuscarContacto);
        tvSinContactos = findViewById(R.id.tvSinContactos);
        btnVolverContactos = findViewById(R.id.btnVolverContactos);

        rvContactos.setLayoutManager(
                new LinearLayoutManager(this));

        SharedPreferences preferences = getSharedPreferences(
                "supabase_session", MODE_PRIVATE);

        userId = preferences.getString("user_id", null);
        accessToken = preferences.getString("access_token", null);

        if (userId == null || accessToken == null) {
            Toast.makeText(this,
                    "No hay una sesión activa.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        contactosAPI = new ContactosAPI();

        btnVolverContactos.setOnClickListener(v -> finish());

        cargarContactos("");

        etBuscarContacto.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                handler.removeCallbacks(busquedaPendiente);
                handler.postDelayed(busquedaPendiente, 400);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void cargarContactos(String busqueda) {

        contactosAPI.consultarContactos(
                userId,
                accessToken,
                busqueda,
                new ContactosAPI.ContactosCallback() {

                    @Override
                    public void onSuccess(
                            java.util.List<Contacto> contactos) {

                        adapter = new ContactoAdapter(contactos, contacto -> {

                            ConversacionesAPI.crearConversacionIndividual(
                                    contacto.getId(),
                                    accessToken,
                                    new ConversacionesAPI.Callback() {

                                        @Override
                                        public void onSuccess(String idConversacion) {

                                            Intent intent = new Intent(
                                                    ContactosActivity.this,
                                                    ChatActivity.class
                                            );

                                            intent.putExtra("idConversacion", idConversacion);
                                            intent.putExtra(
                                                    "nombreContacto",
                                                    contacto.getNombreCompleto()
                                            );
                                            intent.putExtra(
                                                    "fotoContacto",
                                                    contacto.getFotoPerfil()
                                            );

                                            startActivity(intent);
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Toast.makeText(
                                                    ContactosActivity.this,
                                                    "Error al crear conversación: " + error,
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        }
                                    }
                            );
                        });

                        rvContactos.setAdapter(adapter);

                        tvSinContactos.setVisibility(
                                contactos.isEmpty()
                                        ? TextView.VISIBLE
                                        : TextView.GONE
                        );
                    }

                    @Override
                    public void onError(String mensaje) {

                        Toast.makeText(
                                ContactosActivity.this,
                                "Error al consultar usuarios: " + mensaje,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(busquedaPendiente);
        super.onDestroy();
    }
}
