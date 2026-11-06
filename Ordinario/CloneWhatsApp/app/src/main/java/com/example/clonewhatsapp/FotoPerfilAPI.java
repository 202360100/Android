
package com.example.clonewhatsapp;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.example.clonewhatsapp.config.SupabaseConfig;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FotoPerfilAPI {

    public interface FotoCallback {
        void onResultado(boolean exito, String mensaje, String url);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public void subirFoto(
            Context context,
            Uri imagen,
            String userId,
            String token,
            FotoCallback callback) {

        executor.execute(() -> {
            HttpURLConnection conexion = null;

            try {
                String mime = context.getContentResolver()
                        .getType(imagen);

                if (mime == null || !mime.startsWith("image/")) {
                    throw new Exception("Formato de imagen no válido");
                }

                String extension = mime.equals("image/jpeg") ? "jpg"
                        : mime.substring(mime.indexOf("/") + 1);

                InputStream input = context.getContentResolver()
                        .openInputStream(imagen);

                if (input == null) {
                    throw new Exception("No se pudo leer la imagen");
                }

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] datos = new byte[8192];
                int cantidad;

                while ((cantidad = input.read(datos)) != -1) {
                    buffer.write(datos, 0, cantidad);
                }
                input.close();

                byte[] imagenBytes = buffer.toByteArray();

                String base = SupabaseConfig.SUPABASE_URL;
                while (base.endsWith("/")) {
                    base = base.substring(0, base.length() - 1);
                }

                String ruta = userId + "/profile." + extension;

                URL urlSubida = new URL(
                        base + "/storage/v1/object/avatars/" + ruta);

                conexion = (HttpURLConnection) urlSubida.openConnection();
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                conexion.setRequestProperty(
                        "apikey", SupabaseConfig.SUPABASE_KEY);
                conexion.setRequestProperty(
                        "Authorization", "Bearer " + token);
                conexion.setRequestProperty("Content-Type", mime);
                conexion.setRequestProperty("x-upsert", "true");

                try (OutputStream output = conexion.getOutputStream()) {
                    output.write(imagenBytes);
                }

                int codigo = conexion.getResponseCode();

                if (codigo < 200 || codigo >= 300) {
                    throw new Exception("Error al subir la imagen: " + codigo);
                }

                conexion.disconnect();
                conexion = null;

                String fotoUrl = base
                        + "/storage/v1/object/public/avatars/" + ruta;

                URL urlPerfil = new URL(
                        base + "/rest/v1/profiles?idProfile=eq." + userId);

                conexion = (HttpURLConnection) urlPerfil.openConnection();
                conexion.setRequestMethod("PATCH");
                conexion.setDoOutput(true);
                conexion.setRequestProperty(
                        "apikey", SupabaseConfig.SUPABASE_KEY);
                conexion.setRequestProperty(
                        "Authorization", "Bearer " + token);
                conexion.setRequestProperty(
                        "Content-Type", "application/json");
                conexion.setRequestProperty("Prefer", "return=minimal");

                JSONObject json = new JSONObject();
                json.put("foto_perfil", fotoUrl);

                try (OutputStream output = conexion.getOutputStream()) {
                    output.write(json.toString()
                            .getBytes(StandardCharsets.UTF_8));
                }

                codigo = conexion.getResponseCode();

                if (codigo < 200 || codigo >= 300) {
                    throw new Exception(
                            "Imagen subida, pero no se pudo guardar el perfil: "
                                    + codigo);
                }

                String resultadoUrl = fotoUrl;

                handler.post(() -> callback.onResultado(
                        true, "Foto de perfil actualizada", resultadoUrl));

            } catch (Exception e) {
                String error = e.getMessage();
                handler.post(() -> callback.onResultado(
                        false, error, null));
            } finally {
                if (conexion != null) {
                    conexion.disconnect();
                }
            }
        });
    }
}
