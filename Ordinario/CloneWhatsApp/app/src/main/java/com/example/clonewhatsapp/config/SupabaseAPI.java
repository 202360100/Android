
package com.example.clonewhatsapp.config;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SupabaseAPI {

    public interface ConnectionCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public static void probarConexion(ConnectionCallback callback) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            HttpURLConnection connection = null;

            try {
                String endpoint = SupabaseConfig.SUPABASE_URL
                        + "/auth/v1/settings";

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
                        "Accept",
                        "application/json"
                );

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

                    handler.post(() ->
                            callback.onSuccess(response.toString())
                    );

                } else {

                    String errorMessage = "HTTP " + responseCode
                            + ": " + response;

                    handler.post(() ->
                            callback.onError(errorMessage)
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

                executor.shutdown();
            }
        });
    }
}
