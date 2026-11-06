package com.example.clonewhatsapp;

import android.os.Handler;
import android.os.Looper;

import com.example.clonewhatsapp.config.SupabaseConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;

public class ConversacionesAPI {

    private static final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private static final Handler handler =
            new Handler(Looper.getMainLooper());

    public interface Callback {
        void onSuccess(String idConversacion);
        void onError(String error);
    }

    public static void crearConversacionIndividual(
            String destinatarioId,
            String accessToken,
            Callback callback
    ) {
        executor.execute(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.SUPABASE_URL
                                + "/rest/v1/rpc/crear_conversacion_individual"
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
                body.put("p_destinatario", destinatarioId);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = connection.getResponseCode();

                InputStream inputStream =
                        responseCode >= 200 && responseCode < 300
                                ? connection.getInputStream()
                                : connection.getErrorStream();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream)
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                if (responseCode >= 200 && responseCode < 300) {

                    String idConversacion = response.toString().trim();

                    // Supabase devuelve el UUID como texto JSON
                    if (idConversacion.startsWith("\"")
                            && idConversacion.endsWith("\"")) {
                        idConversacion = idConversacion.substring(
                                1, idConversacion.length() - 1
                        );
                    }

                    String resultado = idConversacion;

                    handler.post(() ->
                            callback.onSuccess(resultado)
                    );

                } else {
                    String error = response.toString();

                    handler.post(() ->
                            callback.onError(error)
                    );
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
