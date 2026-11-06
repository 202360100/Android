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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactosAPI {

    public interface ContactosCallback {
        void onSuccess(List<Contacto> contactos);
        void onError(String mensaje);
    }

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    public void consultarContactos(
            String userId,
            String token,
            String busqueda,
            ContactosCallback callback) {

        executor.execute(() -> {
            HttpURLConnection conexion = null;

            try {
                String base = SupabaseConfig.SUPABASE_URL;

                while (base.endsWith("/")) {
                    base = base.substring(0, base.length() - 1);
                }

                String endpoint = base
                        + "/rest/v1/profiles"
                        + "?select=idProfile,nombre,apellido,foto_perfil"
                        + "&idProfile=neq." + userId
                        + "&order=nombre.asc";

                if (busqueda != null && !busqueda.trim().isEmpty()) {

                    String texto = busqueda.trim();

                    String filtro = "(nombre.ilike.*"
                            + texto
                            + "*,apellido.ilike.*"
                            + texto
                            + "*)";

                    endpoint += "&or=" + URLEncoder.encode(
                            filtro, "UTF-8");

                    endpoint += "&" + URLEncoder.encode(
                            filtro, "UTF-8");
                }

                conexion = (HttpURLConnection)
                        new URL(endpoint).openConnection();

                conexion.setRequestMethod("GET");
                conexion.setConnectTimeout(20000);
                conexion.setReadTimeout(20000);

                conexion.setRequestProperty(
                        "apikey", SupabaseConfig.SUPABASE_KEY);

                conexion.setRequestProperty(
                        "Authorization", "Bearer " + token);

                int codigo = conexion.getResponseCode();

                InputStream input = codigo >= 200 && codigo < 300
                        ? conexion.getInputStream()
                        : conexion.getErrorStream();

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(input));

                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = reader.readLine()) != null) {
                    respuesta.append(linea);
                }

                reader.close();

                if (codigo < 200 || codigo >= 300) {
                    throw new Exception(respuesta.toString());
                }

                JSONArray array = new JSONArray(
                        respuesta.toString());

                List<Contacto> lista = new ArrayList<>();

                for (int i = 0; i < array.length(); i++) {

                    JSONObject objeto = array.getJSONObject(i);

                    lista.add(new Contacto(
                            objeto.optString("idProfile"),
                            objeto.optString("nombre"),
                            objeto.optString("apellido"),
                            objeto.optString("foto_perfil", "")
                    ));
                }

                handler.post(() -> callback.onSuccess(lista));

            } catch (Exception e) {

                String error = e.getMessage();

                handler.post(() ->
                        callback.onError(error));

            } finally {
                if (conexion != null) {
                    conexion.disconnect();
                }
            }
        });
    }
}
