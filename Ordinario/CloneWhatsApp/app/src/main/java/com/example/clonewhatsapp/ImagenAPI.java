package com.example.clonewhatsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.os.Handler;
import android.os.Looper;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImagenAPI {

    private static final ExecutorService executor =
            Executors.newFixedThreadPool(4);

    private static final Handler handler =
            new Handler(Looper.getMainLooper());

    public static void cargarImagen(String url, ImageView imageView) {

        if (url == null || url.trim().isEmpty()) {
            return;
        }

        imageView.setTag(url);

        executor.execute(() -> {
            HttpURLConnection conexion = null;

            try {
                conexion = (HttpURLConnection)
                        new URL(url).openConnection();

                conexion.setRequestMethod("GET");
                conexion.setConnectTimeout(15000);
                conexion.setReadTimeout(15000);
                conexion.setInstanceFollowRedirects(true);

                int codigo = conexion.getResponseCode();

                if (codigo < 200 || codigo >= 300) {
                    throw new Exception("HTTP " + codigo);
                }

                Bitmap bitmap;

                try (InputStream input = conexion.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(input);
                }

                if (bitmap != null) {
                    handler.post(() -> {
                        Object etiqueta = imageView.getTag();

                        if (url.equals(etiqueta)) {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }

            } catch (Exception e) {
                android.util.Log.e(
                        "ImagenAPI",
                        "Error al cargar imagen: " + url,
                        e
                );
            } finally {
                if (conexion != null) {
                    conexion.disconnect();
                }
            }
        });
    }
}
