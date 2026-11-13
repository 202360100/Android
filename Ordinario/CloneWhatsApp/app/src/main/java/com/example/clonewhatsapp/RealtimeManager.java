package com.example.clonewhatsapp;

import android.os.Handler;
import android.os.Looper;

import com.example.clonewhatsapp.config.SupabaseConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class RealtimeManager {

    public interface RealtimeCallback {
        void onNewMessage(Mensaje mensaje);
        void onConnected();
        void onError(String error);
    }

    private final OkHttpClient client;
    private final Handler mainHandler;

    private WebSocket webSocket;
    private RealtimeCallback callback;

    private String userId;
    private String accessToken;
    private String idConversacion;

    public RealtimeManager() {

        client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void conectar(
            String idConversacion,
            String userId,
            String accessToken,
            RealtimeCallback callback) {

        desconectar();

        this.idConversacion = idConversacion;
        this.userId = userId;
        this.accessToken = accessToken;
        this.callback = callback;

        try {
            String baseUrl = SupabaseConfig.SUPABASE_URL;

            String wsUrl = baseUrl
                    .replace("https://", "wss://")
                    .replace("http://", "ws://");

            if (wsUrl.endsWith("/")) {
                wsUrl = wsUrl.substring(0, wsUrl.length() - 1);
            }

            wsUrl += "/realtime/v1/websocket?apikey="
                    + URLEncoder.encode(
                    SupabaseConfig.SUPABASE_KEY,
                    "UTF-8"
            )
                    + "&vsn=1.0.0";

            Request request = new Request.Builder()
                    .url(wsUrl)
                    .addHeader(
                            "Authorization",
                            "Bearer " + accessToken
                    )
                    .build();

            webSocket = client.newWebSocket(
                    request,
                    new WebSocketListener() {

                        @Override
                        public void onOpen(
                                WebSocket socket,
                                Response response) {

                            enviarSuscripcion(socket);
                        }

                        @Override
                        public void onMessage(
                                WebSocket socket,
                                String text) {

                            procesarEvento(text);
                        }

                        @Override
                        public void onFailure(
                                WebSocket socket,
                                Throwable error,
                                Response response) {

                            notificarError(error.getMessage());
                        }
                    });

        } catch (Exception e) {
            notificarError(e.getMessage());
        }
    }

    private void enviarSuscripcion(WebSocket socket) {

        try {
            JSONObject config = new JSONObject();

            config.put(
                    "broadcast",
                    new JSONObject()
                            .put("ack", false)
                            .put("self", false)
            );

            config.put(
                    "presence",
                    new JSONObject().put("key", "")
            );

            JSONArray cambios = new JSONArray();

            JSONObject filtro = new JSONObject();

            filtro.put("event", "INSERT");
            filtro.put("schema", "public");
            filtro.put("table", "messages");
            filtro.put(
                    "filter",
                    "idConversacion=eq." + idConversacion
            );

            cambios.put(filtro);

            config.put("postgres_changes", cambios);

            JSONObject payload = new JSONObject();
            payload.put("config", config);
            payload.put("access_token", accessToken);

            JSONObject mensaje = new JSONObject();

            mensaje.put(
                    "topic",
                    "realtime:chat-" + idConversacion
            );

            mensaje.put("event", "phx_join");
            mensaje.put("payload", payload);
            mensaje.put("ref", "1");
            mensaje.put("join_ref", "1");

            socket.send(mensaje.toString());

        } catch (Exception e) {
            notificarError(e.getMessage());
        }
    }

    private void procesarEvento(String texto) {

        try {
            JSONObject evento = new JSONObject(texto);

            String nombreEvento = evento.optString("event");

            if ("postgres_changes".equals(nombreEvento)) {

                JSONObject payload = evento.optJSONObject("payload");

                if (payload == null) return;

                JSONObject data = payload.optJSONObject("data");

                if (data == null) {
                    data = payload;
                }

                JSONObject record = data.optJSONObject("record");

                if (record == null) return;

                String id = record.optString("idMessage");
                String contenido = record.optString("contenido");
                String remitente = record.optString("idProfile");
                String fecha = record.optString("created_at");

                if (id.isEmpty() || remitente.isEmpty()) return;

                // Evitar duplicar los mensajes propios.
                if (remitente.equals(userId)) return;

                Mensaje mensaje = new Mensaje(
                        id,
                        contenido,
                        remitente,
                        fecha
                );

                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onNewMessage(mensaje);
                    }
                });
            }

            if ("phx_reply".equals(nombreEvento)) {

                JSONObject payload = evento.optJSONObject("payload");

                if (payload != null
                        && "ok".equals(payload.optString("status"))) {

                    mainHandler.post(() -> {
                        if (callback != null) {
                            callback.onConnected();
                        }
                    });
                }
            }

        } catch (Exception e) {
            notificarError(e.getMessage());
        }
    }

    private void notificarError(String error) {

        mainHandler.post(() -> {
            if (callback != null) {
                callback.onError(
                        error != null ? error : "Error de Realtime"
                );
            }
        });
    }

    public void desconectar() {

        if (webSocket != null) {
            webSocket.close(1000, "Salir del chat");
            webSocket = null;
        }
    }
}
