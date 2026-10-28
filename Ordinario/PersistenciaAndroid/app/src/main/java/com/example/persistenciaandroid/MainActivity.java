
package com.example.persistenciaandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    private static final String SUPABASE_URL = "https://jovmgeyyoqwlxjrbpken.supabase.co";

    private static final String SUPABASE_KEY = "sb_publishable_RV1YCHUiA93Stl-QhgSm5w_D57OHZFy";

    private static final String ENDPOINT =
            SUPABASE_URL + "/rest/v1/productos";

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private EditText etProducto, etDescripcion, etPrecio;
    private EditText etIdActualizar, etProductoActualizar;
    private EditText etDescripcionActualizar, etPrecioActualizar;
    private EditText etIdEliminar;
    private TextView tvProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etProducto = findViewById(R.id.etProducto);
        etDescripcion = findViewById(R.id.etDescripcion);
        etPrecio = findViewById(R.id.etPrecio);

        etIdActualizar = findViewById(R.id.etIdActualizar);
        etProductoActualizar = findViewById(R.id.etProductoActualizar);
        etDescripcionActualizar =
                findViewById(R.id.etDescripcionActualizar);
        etPrecioActualizar = findViewById(R.id.etPrecioActualizar);

        etIdEliminar = findViewById(R.id.etIdEliminar);
        tvProductos = findViewById(R.id.tvProductos);

        Button btnAgregar = findViewById(R.id.btnAgregar);
        Button btnConsultar = findViewById(R.id.btnConsultar);
        Button btnActualizar = findViewById(R.id.btnActualizar);
        Button btnEliminar = findViewById(R.id.btnEliminar);

        btnAgregar.setOnClickListener(v -> agregarProducto());
        btnConsultar.setOnClickListener(v -> consultarProductos());
        btnActualizar.setOnClickListener(v -> actualizarProducto());
        btnEliminar.setOnClickListener(v -> eliminarProducto());
    }

    // Ejecuta las peticiones HTTP fuera del hilo de interfaz.
    private void ejecutarPeticion(
            String metodo,
            String endpoint,
            JSONObject cuerpo,
            String mensajeExito,
            boolean comprobarRegistro,
            boolean mostrarRespuesta
    ) {
        executor.execute(() -> {
            HttpURLConnection conexion = null;

            try {
                URL url = new URL(endpoint);
                conexion = (HttpURLConnection) url.openConnection();

                conexion.setRequestMethod(metodo);
                conexion.setConnectTimeout(15000);
                conexion.setReadTimeout(15000);
                conexion.setRequestProperty("apikey", SUPABASE_KEY);
                conexion.setRequestProperty("Accept", "application/json");

                if (cuerpo != null) {
                    conexion.setRequestProperty(
                            "Content-Type", "application/json; charset=UTF-8");
                    conexion.setRequestProperty(
                            "Prefer", "return=representation");
                    conexion.setDoOutput(true);

                    byte[] datos = cuerpo.toString()
                            .getBytes(StandardCharsets.UTF_8);

                    try (OutputStream salida = conexion.getOutputStream()) {
                        salida.write(datos);
                    }
                } else if (metodo.equals("DELETE")
                        || metodo.equals("PATCH")) {
                    conexion.setRequestProperty(
                            "Prefer", "return=representation");
                }

                int codigo = conexion.getResponseCode();

                InputStream flujo = codigo >= 200 && codigo < 300
                        ? conexion.getInputStream()
                        : conexion.getErrorStream();

                StringBuilder respuesta = new StringBuilder();

                if (flujo != null) {
                    try (BufferedReader lector = new BufferedReader(
                            new InputStreamReader(
                                    flujo, StandardCharsets.UTF_8))) {
                        String linea;
                        while ((linea = lector.readLine()) != null) {
                            respuesta.append(linea);
                        }
                    }
                }

                String resultado = respuesta.toString();

                if (codigo < 200 || codigo >= 300) {
                    mostrarMensaje("Error HTTP " + codigo + ": " + resultado);
                    return;
                }

                if (comprobarRegistro) {
                    JSONArray registros = new JSONArray(resultado);

                    if (registros.length() == 0) {
                        mostrarMensaje(
                                "No se encontró el producto o no tienes permiso.");
                        return;
                    }
                }

                if (mostrarRespuesta) {
                    mostrarProductos(resultado);
                } else {
                    mostrarMensaje(mensajeExito);
                }

            } catch (Exception e) {
                mostrarMensaje("Error: " + e.getMessage());
            } finally {
                if (conexion != null) {
                    conexion.disconnect();
                }
            }
        });
    }

    private void agregarProducto() {
        String producto = etProducto.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String precioTexto = etPrecio.getText().toString().trim();

        if (producto.isEmpty()
                || descripcion.isEmpty()
                || precioTexto.isEmpty()) {
            mostrarMensaje("Completa todos los campos.");
            return;
        }

        try {
            double precio = Double.parseDouble(precioTexto);

            if (!Double.isFinite(precio) || precio < 0) {
                mostrarMensaje("Ingresa un precio válido.");
                return;
            }

            JSONObject cuerpo = new JSONObject();
            cuerpo.put("producto", producto);
            cuerpo.put("descripción", descripcion);
            cuerpo.put("precio", precio);

            ejecutarPeticion(
                    "POST", ENDPOINT, cuerpo,
                    "Producto agregado correctamente.",
                    false, false
            );

        } catch (Exception e) {
            mostrarMensaje("El precio no es válido.");
        }
    }

    private void consultarProductos() {
        ejecutarPeticion(
                "GET",
                ENDPOINT + "?select=*&order=id.asc",
                null,
                "",
                false,
                true
        );
    }

    private void actualizarProducto() {
        String id = etIdActualizar.getText().toString().trim();
        String producto =
                etProductoActualizar.getText().toString().trim();
        String descripcion =
                etDescripcionActualizar.getText().toString().trim();
        String precioTexto =
                etPrecioActualizar.getText().toString().trim();

        if (id.isEmpty() || producto.isEmpty()
                || descripcion.isEmpty() || precioTexto.isEmpty()) {
            mostrarMensaje("Completa todos los campos.");
            return;
        }

        try {
            int idNumero = Integer.parseInt(id);
            double precio = Double.parseDouble(precioTexto);

            if (idNumero <= 0 || !Double.isFinite(precio) || precio < 0) {
                mostrarMensaje("Revisa el ID y el precio.");
                return;
            }

            JSONObject cuerpo = new JSONObject();
            cuerpo.put("producto", producto);
            cuerpo.put("descripción", descripcion);
            cuerpo.put("precio", precio);

            ejecutarPeticion(
                    "PATCH",
                    ENDPOINT + "?id=eq." + idNumero,
                    cuerpo,
                    "Producto actualizado correctamente.",
                    true,
                    false
            );

        } catch (Exception e) {
            mostrarMensaje("El ID o el precio no son válidos.");
        }
    }

    private void eliminarProducto() {
        String id = etIdEliminar.getText().toString().trim();

        if (id.isEmpty()) {
            mostrarMensaje("Ingresa el ID del producto.");
            return;
        }

        try {
            int idNumero = Integer.parseInt(id);

            if (idNumero <= 0) {
                mostrarMensaje("Ingresa un ID válido.");
                return;
            }

            ejecutarPeticion(
                    "DELETE",
                    ENDPOINT + "?id=eq." + idNumero,
                    null,
                    "Producto eliminado correctamente.",
                    true,
                    false
            );

        } catch (NumberFormatException e) {
            mostrarMensaje("El ID debe ser un número entero.");
        }
    }

    private void mostrarProductos(String respuesta) {
        runOnUiThread(() -> {
            try {
                JSONArray lista = new JSONArray(respuesta);
                StringBuilder texto = new StringBuilder();

                if (lista.length() == 0) {
                    texto.append("No hay productos registrados.");
                }

                for (int i = 0; i < lista.length(); i++) {
                    JSONObject producto = lista.getJSONObject(i);

                    texto.append("ID: ")
                            .append(producto.optString("id"))
                            .append("\nProducto: ")
                            .append(producto.optString("producto"))
                            .append("\nDescripción: ")
                            .append(producto.optString("descripción"))
                            .append("\nPrecio: ")
                            .append(producto.optString("precio"))
                            .append("\n\n");
                }

                tvProductos.setText(texto.toString());

            } catch (Exception e) {
                tvProductos.setText(
                        "No se pudo interpretar la respuesta de Supabase.");
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        runOnUiThread(() ->
                Toast.makeText(
                        MainActivity.this,
                        mensaje,
                        Toast.LENGTH_LONG
                ).show()
        );
    }

    @Override
    protected void onDestroy() {
        executor.shutdown();
        super.onDestroy();
    }
}
