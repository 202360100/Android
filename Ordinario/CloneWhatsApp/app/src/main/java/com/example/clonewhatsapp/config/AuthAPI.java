
package com.example.clonewhatsapp.config;

import android.os.Handler;
import android.os.Looper;

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


public class AuthAPI {

    public interface RegistrationCallback {
        void onSuccess(String userId, boolean emailConfirmationRequired);
        void onError(String error);
    }

    public static void registrarUsuario(
            String nombre,
            String apellido,
            String numero,
            String email,
            String password,
            RegistrationCallback callback
    ) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            HttpURLConnection connection = null;

            try {

                String endpoint = SupabaseConfig.SUPABASE_URL
                        + "/auth/v1/signup";

                URL url = new URL(endpoint);

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
                        "Content-Type",
                        "application/json"
                );

                connection.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                // Datos del usuario
                JSONObject body = new JSONObject();

                body.put("email", email);
                body.put("password", password);

                // Datos adicionales del perfil
                JSONObject metadata = new JSONObject();

                metadata.put("nombre", nombre);
                metadata.put("apellido", apellido);
                metadata.put("numero", numero);

                body.put("data", metadata);

                // Enviar solicitud
                try (OutputStream output = connection.getOutputStream()) {

                    output.write(
                            body.toString().getBytes(StandardCharsets.UTF_8)
                    );

                }

                int responseCode = connection.getResponseCode();

                InputStream inputStream;

                if (responseCode >= 200 && responseCode < 300) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                }

                StringBuilder response = new StringBuilder();

                if (inputStream != null) {

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream)
                    );

                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                }

                if (responseCode >= 200 && responseCode < 300) {

                    JSONObject result = new JSONObject(
                            response.toString()
                    );

                    JSONObject user = result.optJSONObject("user");

                    if (user == null) {
                        user = result;
                    }

                    String userId = user.optString("id", "");

                    if (userId.isEmpty()) {
                        throw new Exception(
                                "Supabase no devolvió el ID del usuario."
                        );
                    }

                    boolean confirmationRequired =
                            result.optString("access_token", "").isEmpty();

                    final String finalUserId = userId;
                    final boolean finalConfirmationRequired =
                            confirmationRequired;

                    handler.post(() ->
                            callback.onSuccess(
                                    finalUserId,
                                    finalConfirmationRequired
                            )
                    );

                } else {

                    JSONObject errorJson = new JSONObject(
                            response.toString()
                    );

                    String errorMessage = errorJson.optString(
                            "msg",
                            errorJson.optString(
                                    "message",
                                    errorJson.optString(
                                            "error_description",
                                            "Error HTTP " + responseCode
                                    )
                            )
                    );

                    handler.post(() ->
                            callback.onError(errorMessage)
                    );
                }

            } catch (Exception e) {

                handler.post(() ->
                        callback.onError(e.getMessage() != null
                                ? e.getMessage()
                                : "Error desconocido")
                );

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }

                executor.shutdown();
            }
        });
    }


    public interface LoginCallback {
        void onSuccess(String userId, String accessToken, String refreshToken);
        void onError(String mensaje);
    }

    public void iniciarSesion(String email, String password, LoginCallback callback) {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            HttpURLConnection connection = null;

            try {
                URL url = new URL(
                        SupabaseConfig.SUPABASE_URL +
                                "/auth/v1/token?grant_type=password"
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
                        "Content-Type", "application/json"
                );

                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("password", password);

                try (OutputStream os = connection.getOutputStream()) {
                    os.write(body.toString().getBytes(StandardCharsets.UTF_8));
                }

                int responseCode = connection.getResponseCode();

                InputStream stream = responseCode >= 200 && responseCode < 300
                        ? connection.getInputStream()
                        : connection.getErrorStream();

                StringBuilder response = new StringBuilder();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream, StandardCharsets.UTF_8))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                JSONObject json = new JSONObject(response.toString());

                if (responseCode >= 200 && responseCode < 300) {

                    String accessToken = json.getString("access_token");
                    String refreshToken = json.getString("refresh_token");
                    String userId = json.getJSONObject("user").getString("id");

                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onSuccess(userId, accessToken, refreshToken)
                    );

                } else {

                    String mensaje = json.optString(
                            "msg",
                            json.optString(
                                    "message",
                                    json.optString(
                                            "error_description",
                                            "No se pudo iniciar sesión."
                                    )
                            )
                    );

                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onError(mensaje)
                    );
                }

            } catch (Exception e) {

                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError("Error de conexión: " + e.getMessage())
                );

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                executor.shutdown();
            }
        });
    }

}
