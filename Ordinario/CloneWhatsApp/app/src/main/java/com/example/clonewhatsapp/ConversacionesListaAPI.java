package com.example.clonewhatsapp;

import android.os.Handler;
import android.os.Looper;

import com.example.clonewhatsapp.config.SupabaseConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConversacionesListaAPI {

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private static final Handler handler =
            new Handler(Looper.getMainLooper());

    public interface Callback {
        void onSuccess(ArrayList<Conversacion> conversaciones);
        void onError(String error);
    }

    public static void obtenerConversaciones(
            String accessToken,
            Callback callback
    ) {
        executor.execute(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.SUPABASE_URL
                                + "/rest/v1/rpc/obtener_mis_conversaciones"
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
                        "Accept", "application/json"
                );

                try (java.io.OutputStream output =
                             connection.getOutputStream()) {
                    output.write("{}".getBytes(StandardCharsets.UTF_8));
                }

                int code = connection.getResponseCode();

                InputStream stream = code >= 200 && code < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream, StandardCharsets.UTF_8)
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                if (code >= 200 && code < 300) {

                    JSONArray array = new JSONArray(response.toString());
                    ArrayList<Conversacion> lista = new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                        lista.add(new Conversacion(
                                obj.optString("id_conversacion"),
                                obj.optString("tipo"),
                                obj.optString("contacto_nombre") + " "
                                        + obj.optString("contacto_apellido"),
                                obj.optString("contacto_foto"),
                                obj.optString("contacto_id"),
                                obj.optString("ultimo_contenido"),
                                obj.optString("ultimo_created_at"),
                                obj.optString("ultimo_sender")
                        ));
                    }

                    handler.post(() -> callback.onSuccess(lista));

                } else {
                    String error = response.toString();
                    handler.post(() -> callback.onError(error));
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
