package com.example.clonewhatsapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.os.Looper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    private String contactoId;
    private TextView tvEstadoConexion;
    private PerfilAPI perfilAPI;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable actualizarEstadoRunnable = new Runnable() {
        @Override
        public void run() {
            consultarEstadoConexion();
            handler.postDelayed(this, 20000);
        }
    };

    private RealtimeManager realtimeManager;

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

                            actualizarEstadosMensajes();
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

    private void conectarRealtime() {
        realtimeManager = new RealtimeManager();

        realtimeManager.conectar(
                idConversacion,
                userId,
                accessToken,
                new RealtimeManager.RealtimeCallback() {

                    @Override
                    public void onNewMessage(Mensaje mensaje) {
                        // Evitar mensajes duplicados
                        for (Mensaje existente : mensajes) {
                            if (existente.getId().equals(mensaje.getId())) {
                                return;
                            }
                        }

                        mensajes.add(mensaje);
                        adapter.notifyItemInserted(mensajes.size() - 1);
                        rvMensajes.scrollToPosition(mensajes.size() - 1);

                        // Marcar como entregado y leído
                        actualizarEstadosMensajes();
                    }

                    @Override
                    public void onConnected() {
                        android.util.Log.d("REALTIME", "Conectado correctamente");
                    }

                    @Override
                    public void onError(String error) {
                        android.util.Log.e("REALTIME", error);
                    }
                }
        );
    }
    private void actualizarEstadosMensajes() {

        MensajesAPI.marcarMensajesEntregados(
                idConversacion,
                accessToken,
                new MensajesAPI.OperacionCallback() {

                    @Override
                    public void onSuccess() {
                        marcarComoLeidosYCargarEstados();
                    }

                    @Override
                    public void onError(String error) {
                        marcarComoLeidosYCargarEstados();
                    }
                }
        );
    }

    private void marcarComoLeidosYCargarEstados() {

        MensajesAPI.marcarMensajesLeidos(
                idConversacion,
                accessToken,
                new MensajesAPI.OperacionCallback() {

                    @Override
                    public void onSuccess() {
                        consultarEstados();
                    }

                    @Override
                    public void onError(String error) {
                        consultarEstados();
                    }
                }
        );
    }

    private void consultarEstados() {

        MensajesAPI.cargarEstadosMensajes(
                idConversacion,
                accessToken,
                new MensajesAPI.EstadosCallback() {

                    @Override
                    public void onSuccess(
                            ArrayList<EstadoMensaje> estados
                    ) {

                        for (EstadoMensaje estado : estados) {

                            // Solo nos interesan los estados
                            // registrados para el otro usuario.
                            if (estado.getIdProfile().equals(userId)) {
                                continue;
                            }

                            for (Mensaje mensaje : mensajes) {

                                if (mensaje.getId().equals(
                                        estado.getIdMessage()
                                ) && mensaje.getIdProfile().equals(userId)) {

                                    mensaje.setEstado(estado.getEstado());
                                    break;
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(
                                ChatActivity.this,
                                "Error al consultar estados: " + error,
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
    protected void onResume() {
        super.onResume();

        handler.removeCallbacks(actualizarEstadoRunnable);
        actualizarEstadoRunnable.run();
    }

    @Override
    protected void onPause() {
        super.onPause();

        handler.removeCallbacks(actualizarEstadoRunnable);
    }

    private void consultarEstadoConexion() {

        if (contactoId == null || contactoId.isEmpty()
                || accessToken == null) {
            tvEstadoConexion.setText("Estado no disponible");
            return;
        }

        perfilAPI.consultarUltimaConexion(
                contactoId,
                accessToken,
                new PerfilAPI.UltimaConexionCallback() {

                    @Override
                    public void onSuccess(String fecha) {

                        if (fecha == null || fecha.isEmpty()) {
                            tvEstadoConexion.setText(
                                    "Sin conexión registrada"
                            );
                            return;
                        }

                        try {
                            SimpleDateFormat formato =
                                    new SimpleDateFormat(
                                            "yyyy-MM-dd'T'HH:mm:ss",
                                            Locale.US
                                    );

                            formato.setTimeZone(
                                    TimeZone.getTimeZone("UTC")
                            );

                            Date ultimaConexion = formato.parse(fecha);

                            if (ultimaConexion == null) return;

                            long diferencia =
                                    System.currentTimeMillis()
                                            - ultimaConexion.getTime();

                            if (diferencia >= 0 && diferencia < 45000) {

                                tvEstadoConexion.setText("En línea");

                            } else {

                                tvEstadoConexion.setText(
                                        "Últ. vez: " + obtenerFechaConexion(fecha)
                                );
                            }

                        } catch (Exception e) {
                            tvEstadoConexion.setText("Desconectado");
                        }
                    }

                    @Override
                    public void onError(String mensaje) {
                        tvEstadoConexion.setText(
                                "Estado no disponible"
                        );
                    }
                }
        );
    }


    private String obtenerFechaConexion(String fecha) {

        try {
            java.text.SimpleDateFormat entrada =
                    new java.text.SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss",
                            java.util.Locale.getDefault()
                    );

            entrada.setTimeZone(
                    java.util.TimeZone.getTimeZone("UTC")
            );

            java.util.Date date = entrada.parse(fecha);

            if (date == null) return "";

            java.text.SimpleDateFormat salida =
                    new java.text.SimpleDateFormat(
                            "dd/MM/yyyy HH:mm",
                            java.util.Locale.getDefault()
                    );

            salida.setTimeZone(
                    java.util.TimeZone.getDefault()
            );

            return salida.format(date);

        } catch (Exception e) {
            return fecha;
        }
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

        contactoId = getIntent().getStringExtra("contactoId");

        tvEstadoConexion = findViewById(R.id.tvEstadoConexion);

        perfilAPI = new PerfilAPI();

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

        consultarEstadoConexion();

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
        conectarRealtime();

        cargarMensajes();

        findViewById(R.id.btnEnviarMensaje).setOnClickListener(v -> {
            enviarMensaje();
        });

        findViewById(R.id.btnVolverChat).setOnClickListener(v -> {
            finish();
        });

    }

    @Override
    protected void onDestroy() {
        if (realtimeManager != null) {
            realtimeManager.desconectar();
        }

        super.onDestroy();
    }
}
