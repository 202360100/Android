package com.example.custompassemailrecovery;

import android.net.Uri;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseManager {

    private static final String SUPABASE_URL =
            "https://wydkmagewxypcxvkvzhv.supabase.co";

    private static final String SUPABASE_KEY =
            "sb_publishable_ag2KJNbQ2albkzIYr_nWjA_geCHiiNZ";

    private static final String REDIRECT_URL =
            "custompass://reset";

    private static final MediaType JSON =
            MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient client =
            new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

    public interface AuthCallback {
        void onSuccess(JSONObject response);
        void onError(String message);
    }

    private static void request(
            String endpoint,
            String method,
            JSONObject data,
            String accessToken,
            AuthCallback callback) {

        RequestBody body = null;

        if (data != null) {
            body = RequestBody.create(data.toString(), JSON);
        }

        String bearer = accessToken == null
                ? SUPABASE_KEY : accessToken;

        Request.Builder builder = new Request.Builder()
                .url(SUPABASE_URL + endpoint)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + bearer)
                .addHeader("Content-Type", "application/json");

        switch (method) {
            case "POST":
                builder.post(body);
                break;

            case "PUT":
                builder.put(body);
                break;

            default:
                callback.onError("Método no soportado");
                return;
        }

        client.newCall(builder.build()).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(
                        "Error de conexión: " + e.getMessage()
                );
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {

                String result = response.body() != null
                        ? response.body().string() : "";

                try {
                    JSONObject json = result.isEmpty()
                            ? new JSONObject()
                            : new JSONObject(result);

                    if (response.isSuccessful()) {
                        callback.onSuccess(json);
                    } else {
                        String error = json.optString(
                                "msg",
                                json.optString(
                                        "message",
                                        json.optString(
                                                "error_description",
                                                "Error de autenticación"
                                        )
                                )
                        );

                        callback.onError(error);
                    }

                } catch (Exception e) {
                    callback.onError("Respuesta inválida del servidor");
                } finally {
                    response.close();
                }
            }
        });
    }

    // REGISTRO
    public static void register(
            String email,
            String password,
            AuthCallback callback) {

        try {
            JSONObject data = new JSONObject();

            data.put("email", email);
            data.put("password", password);

            request(
                    "/auth/v1/signup",
                    "POST",
                    data,
                    null,
                    callback
            );

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    // INICIO DE SESIÓN
    public static void login(
            String email,
            String password,
            AuthCallback callback) {

        try {
            JSONObject data = new JSONObject();

            data.put("email", email);
            data.put("password", password);

            request(
                    "/auth/v1/token?grant_type=password",
                    "POST",
                    data,
                    null,
                    callback
            );

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    // RECUPERACIÓN DE CONTRASEÑA
    public static void recoverPassword(
            String email,
            AuthCallback callback) {

        try {
            JSONObject data = new JSONObject();
            data.put("email", email);

            String redirect = URLEncoder.encode(
                    REDIRECT_URL,
                    StandardCharsets.UTF_8.name()
            );

            request(
                    "/auth/v1/recover?redirect_to=" + redirect,
                    "POST",
                    data,
                    null,
                    callback
            );

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    // ACTUALIZAR CONTRASEÑA
    public static void updatePassword(
            String accessToken,
            String newPassword,
            AuthCallback callback) {

        try {
            JSONObject data = new JSONObject();
            data.put("password", newPassword);

            request(
                    "/auth/v1/user",
                    "PUT",
                    data,
                    accessToken,
                    callback
            );

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    // OBTENER ACCESS TOKEN DEL ENLACE
    public static String getAccessToken(Uri uri) {

        if (uri == null || uri.getFragment() == null) {
            return null;
        }

        String fragment = uri.getFragment();

        Uri parsed = Uri.parse(
                "https://temporary.local/?" + fragment
        );

        if (!"recovery".equals(parsed.getQueryParameter("type"))) {
            return null;
        }

        return parsed.getQueryParameter("access_token");
    }
}
