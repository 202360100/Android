package com.example.clonewhatsapp;

import android.os.Handler;
import android.os.Looper;

import com.example.clonewhatsapp.config.SupabaseConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MensajesAPI {

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private static final Handler handler =
            new Handler(Looper.getMainLooper());

    public interface MensajesCallback {
        void onSuccess(ArrayList<Mensaje> mensajes);
        void onError(String error);
    }

    public interface EnviarCallback {
        void onSuccess(Mensaje mensaje);
        void onError(String error);
    }

    public interface OperacionCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface EstadosCallback {
        void onSuccess(ArrayList<EstadoMensaje> estados);
        void onError(String error);
    }

    private static String leerRespuesta(HttpURLConnection connection)
            throws Exception {

        int codigo = connection.getResponseCode();

        InputStream stream = codigo >= 200 && codigo < 300
                ? connection.getInputStream()
                : connection.getErrorStream();

        if (stream == null) return "";

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(stream, StandardCharsets.UTF_8)
        );

        StringBuilder respuesta = new StringBuilder();
        String linea;

        while ((linea = reader.readLine()) != null) {
            respuesta.append(linea);
        }

        reader.close();
        return respuesta.toString();
    }

    public static void cargarMensajes(
            String idConversacion,
            String accessToken,
            MensajesCallback callback
    ) {
        executor.execute(() -> {
            HttpURLConnection connection = null;

            try {
                String endpoint = SupabaseConfig.SUPABASE_URL
                        + "/rest/v1/messages"
                        + "?select=idMessage,contenido,idProfile,created_at"
                        + "&idConversacion=eq."
                        + URLEncoder.encode(idConversacion, "UTF-8")
                        + "&eliminado=eq.false"
                        + "&order=created_at.asc";

                URL url = new URL(endpoint);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                connection.setRequestProperty(
                        "apikey", SupabaseConfig.SUPABASE_KEY
                );

                connection.setRequestProperty(
                        "Authorization", "Bearer " + accessToken
                );

                String respuesta = leerRespuesta(connection);

                if (connection.getResponseCode() >= 200
                        && connection.getResponseCode() < 300) {

                    JSONArray array = new JSONArray(respuesta);
                    ArrayList<Mensaje> lista = new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject objeto = array.getJSONObject(i);

                        lista.add(new Mensaje(
                                objeto.optString("idMessage"),
                                objeto.optString("contenido"),
                                objeto.optString("idProfile"),
                                objeto.optString("created_at")
                        ));
                    }

                    handler.post(() -> callback.onSuccess(lista));

                } else {
                    handler.post(() -> callback.onError(respuesta));
                }

            } catch (Exception e) {
                handler.post(() ->
                        callback.onError(e.getMessage())
                );

            } finally {
                if (connection != null) connection.disconnect();
            }
        });
    }

    public static void enviarMensaje(
            String idConversacion,
            String idProfile,
            String contenido,
            String accessToken,
            EnviarCallback callback
    ) {
        executor.execute(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.SUPABASE_URL
                                + "/rest/v1/messages"
                );

                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setDoOutput(true);

                connection.setRequestProperty(
                        "apikey", SupabaseConfig.SUPABASE_KEY
                );

                connection.setRequestProperty(
                        "Authorization", "Bearer " + accessToken
                );

                connection.setRequestProperty(
                        "Content-Type", "application/json"
                );

                connection.setRequestProperty(
                        "Prefer", "return=representation"
                );

                JSONObject body = new JSONObject();
                body.put("idConversacion", idConversacion);
                body.put("idProfile", idProfile);
                body.put("contenido", contenido);
                body.put("tipo", "texto");

                try (OutputStream output = connection.getOutputStream()) {
                    output.write(
                            body.toString().getBytes(StandardCharsets.UTF_8)
                    );
                }

                String respuesta = leerRespuesta(connection);

                if (connection.getResponseCode() >= 200
                        && connection.getResponseCode() < 300) {

                    JSONArray array = new JSONArray(respuesta);
                    JSONObject objeto = array.getJSONObject(0);

                    Mensaje mensaje = new Mensaje(
                            objeto.optString("idMessage"),
                            objeto.optString("contenido"),
                            objeto.optString("idProfile"),
                            objeto.optString("created_at")
                    );

                    handler.post(() -> callback.onSuccess(mensaje));

                } else {
                    handler.post(() -> callback.onError(respuesta));
                }

            } catch (Exception e) {
                handler.post(() ->
                        callback.onError(e.getMessage())
                );

            } finally {
                if (connection != null) connection.disconnect();
            }
        });
    }


    private static void ejecutarFuncionEstado(
            String nombreFuncion,
            String idConversacion,
            String accessToken,
            OperacionCallback callback
    ) {
        executor.execute(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.SUPABASE_URL
                                + "/rest/v1/rpc/"
                                + nombreFuncion
                );

                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);
                connection.setDoOutput(true);

                connection.setRequestProperty(
                        "apikey",
                        SupabaseConfig.SUPABASE_KEY
                );

                connection.setRequestProperty(
                        "Authorization",
                        "Bearer " + accessToken
                );

                connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                JSONObject body = new JSONObject();
                body.put("p_id_conversacion", idConversacion);

                try (OutputStream output = connection.getOutputStream()) {
                    output.write(
                            body.toString().getBytes(StandardCharsets.UTF_8)
                    );
                }

                String respuesta = leerRespuesta(connection);

                int codigo = connection.getResponseCode();

                if (codigo >= 200 && codigo < 300) {
                    handler.post(callback::onSuccess);
                } else {
                    handler.post(() -> callback.onError(respuesta));
                }

            } catch (Exception e) {
                handler.post(() ->
                        callback.onError(e.getMessage())
                );

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    public static void marcarMensajesEntregados(
            String idConversacion,
            String accessToken,
            OperacionCallback callback
    ) {
        ejecutarFuncionEstado(
                "marcar_mensajes_entregados",
                idConversacion,
                accessToken,
                callback
        );
    }

    public static void marcarMensajesLeidos(
            String idConversacion,
            String accessToken,
            OperacionCallback callback
    ) {
        ejecutarFuncionEstado(
                "marcar_mensajes_leidos",
                idConversacion,
                accessToken,
                callback
        );
    }


    public static void cargarEstadosMensajes(
            String idConversacion,
            String accessToken,
            EstadosCallback callback
    ) {
        executor.execute(() -> {
            HttpURLConnection connection = null;

            try {
                String endpoint =
                        SupabaseConfig.SUPABASE_URL
                                + "/rest/v1/message_status"
                                + "?select=idMessage,idProfile,estado,delivered_at,read_at"
                                + "&order=delivered_at.desc";

                URL url = new URL(endpoint);
                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                connection.setRequestProperty(
                        "apikey",
                        SupabaseConfig.SUPABASE_KEY
                );

                connection.setRequestProperty(
                        "Authorization",
                        "Bearer " + accessToken
                );

                String respuesta = leerRespuesta(connection);
                int codigo = connection.getResponseCode();

                if (codigo >= 200 && codigo < 300) {

                    JSONArray array = new JSONArray(respuesta);
                    ArrayList<EstadoMensaje> lista = new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject objeto = array.getJSONObject(i);

                        lista.add(new EstadoMensaje(
                                objeto.optString("idMessage"),
                                objeto.optString("idProfile"),
                                objeto.optString("estado"),
                                objeto.optString("delivered_at"),
                                objeto.optString("read_at")
                        ));
                    }

                    handler.post(() -> callback.onSuccess(lista));

                } else {
                    handler.post(() -> callback.onError(respuesta));
                }

            } catch (Exception e) {
                handler.post(() ->
                        callback.onError(e.getMessage())
                );

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

}
