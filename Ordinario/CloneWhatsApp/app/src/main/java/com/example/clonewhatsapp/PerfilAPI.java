
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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerfilAPI {

    public interface PerfilCallback {
        void onSuccess(JSONObject perfil);
        void onError(String mensaje);
    }

    public interface ActualizarCallback {
        void onSuccess();
        void onError(String mensaje);
    }

    private void ejecutarPeticion(
            String metodo,
            String endpoint,
            String token,
            JSONObject body,
            PeticionCallback callback) {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {

            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.SUPABASE_URL + endpoint
                );

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(metodo);
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                connection.setRequestProperty(
                        "apikey", SupabaseConfig.SUPABASE_KEY
                );
                connection.setRequestProperty(
                        "Authorization", "Bearer " + token
                );
                connection.setRequestProperty(
                        "Content-Type", "application/json"
                );

                if (body != null) {
                    connection.setDoOutput(true);

                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                    }
                }

                int code = connection.getResponseCode();

                InputStream stream = code >= 200 && code < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                StringBuilder response = new StringBuilder();

                if (stream != null) {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                    }
                }

                String result = response.toString();

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (code >= 200 && code < 300) {
                        callback.onSuccess(result);
                    } else {
                        callback.onError(result);
                    }
                });

            } catch (Exception e) {

                new Handler(Looper.getMainLooper()).post(
                        () -> callback.onError(e.getMessage())
                );

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                executor.shutdown();
            }
        });
    }

    private interface PeticionCallback {
        void onSuccess(String respuesta);
        void onError(String mensaje);
    }

    public void consultarPerfil(String userId, String token,
                                PerfilCallback callback) {

        String endpoint = "/rest/v1/profiles"
                + "?select=idProfile,nombre,apellido,numero,foto_perfil,descripcion"
                + "&idProfile=eq." + userId;

        ejecutarPeticion("GET", endpoint, token, null,
                new PeticionCallback() {

                    @Override
                    public void onSuccess(String respuesta) {
                        try {
                            JSONArray array = new JSONArray(respuesta);

                            if (array.length() == 0) {
                                callback.onError("No se encontró el perfil.");
                                return;
                            }

                            callback.onSuccess(array.getJSONObject(0));

                        } catch (Exception e) {
                            callback.onError(
                                    "No se pudo interpretar el perfil: " + e.getMessage()
                            );
                        }
                    }

                    @Override
                    public void onError(String mensaje) {
                        callback.onError(mensaje);
                    }
                });
    }

    public void actualizarPerfil(
            String userId,
            String token,
            String nombre,
            String apellido,
            String numero,
            String descripcion,
            ActualizarCallback callback) {

        try {
            JSONObject body = new JSONObject();

            body.put("nombre", nombre);
            body.put("apellido", apellido);
            body.put("numero", numero.isEmpty() ? JSONObject.NULL : numero);
            body.put("descripcion",
                    descripcion.isEmpty() ? JSONObject.NULL : descripcion);

            String endpoint = "/rest/v1/profiles"
                    + "?idProfile=eq." + userId;

            ejecutarPeticion("PATCH", endpoint, token, body,
                    new PeticionCallback() {

                        @Override
                        public void onSuccess(String respuesta) {
                            callback.onSuccess();
                        }

                        @Override
                        public void onError(String mensaje) {
                            callback.onError(mensaje);
                        }
                    });

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
}
