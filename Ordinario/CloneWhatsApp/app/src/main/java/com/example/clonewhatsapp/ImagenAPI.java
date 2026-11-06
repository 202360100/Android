
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
            Executors.newFixedThreadPool(2);

    private static final Handler handler =
            new Handler(Looper.getMainLooper());

    public static void cargarImagen(String url, ImageView imageView) {

        if (url == null || url.trim().isEmpty()) {
            return;
        }

        executor.execute(() -> {
            HttpURLConnection conexion = null;

            try {
                conexion = (HttpURLConnection)
                        new URL(url).openConnection();

                conexion.setConnectTimeout(10000);
                conexion.setReadTimeout(10000);

                try (InputStream input = conexion.getInputStream()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(input);

                    if (bitmap != null) {
                        handler.post(() ->
                                imageView.setImageBitmap(bitmap));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conexion != null) {
                    conexion.disconnect();
                }
            }
        });
    }
}
