
package com.example.clonewhatsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import android.net.Uri;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.widget.ImageView;

public class PerfilActivity extends AppCompatActivity {

    private EditText etNombrePerfil;
    private EditText etApellidoPerfil;
    private EditText etNumeroPerfil;
    private EditText etDescripcionPerfil;
    private TextView btnVolverPerfil;
    private Button btnGuardarPerfil;

    private PerfilAPI perfilAPI;

    private String userId;
    private String accessToken;
    private ImageView ivFotoPerfil;
    private String fotoUrl;
    private ActivityResultLauncher<String> selectorImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        etNombrePerfil = findViewById(R.id.etNombrePerfil);
        etApellidoPerfil = findViewById(R.id.etApellidoPerfil);
        etNumeroPerfil = findViewById(R.id.etNumeroPerfil);
        etDescripcionPerfil = findViewById(R.id.etDescripcionPerfil);


        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        btnVolverPerfil = findViewById(R.id.btnVolverPerfil);

        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);

        selectorImagen = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        ivFotoPerfil.setImageURI(uri);

                        FotoPerfilAPI api = new FotoPerfilAPI();

                        api.subirFoto(
                                this,
                                uri,
                                userId,
                                accessToken,
                                (exito, mensaje, url) -> {
                                    Toast.makeText(
                                            this,
                                            mensaje,
                                            Toast.LENGTH_LONG
                                    ).show();

                                    if (exito && url != null) {
                                        ImagenAPI.cargarImagen(url, ivFotoPerfil);
                                    }
                                }
                        );
                    }
                }
        );

        ivFotoPerfil.setOnClickListener(v ->
                selectorImagen.launch("image/*")
        );

        perfilAPI = new PerfilAPI();

        SharedPreferences preferences = getSharedPreferences(
                "supabase_session", MODE_PRIVATE
        );

        userId = preferences.getString("user_id", null);
        accessToken = preferences.getString("access_token", null);

        if (userId == null || accessToken == null) {
            Toast.makeText(this,
                    "No hay una sesión activa.",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        consultarPerfil();

        btnGuardarPerfil.setOnClickListener(v -> guardarPerfil());

        btnVolverPerfil.setOnClickListener(v -> finish());
    }

    private void consultarPerfil() {

        btnGuardarPerfil.setEnabled(false);

        perfilAPI.consultarPerfil(userId, accessToken,
                new PerfilAPI.PerfilCallback() {

                    @Override
                    public void onSuccess(JSONObject perfil) {

                        etNombrePerfil.setText(
                                perfil.optString("nombre", "")
                        );

                        etApellidoPerfil.setText(
                                perfil.optString("apellido", "")
                        );

                        etNumeroPerfil.setText(
                                perfil.isNull("numero")
                                        ? ""
                                        : perfil.optString("numero", "")
                        );

                        etDescripcionPerfil.setText(
                                perfil.isNull("descripcion")
                                        ? ""
                                        : perfil.optString("descripcion", "")
                        );

                        fotoUrl = perfil.optString("foto_perfil", "");

                        if (!fotoUrl.isEmpty() && !fotoUrl.equals("null")) {
                            ImagenAPI.cargarImagen(fotoUrl, ivFotoPerfil);
                        }

                        actualizarIniciales();

                        btnGuardarPerfil.setEnabled(true);
                    }

                    @Override
                    public void onError(String mensaje) {

                        btnGuardarPerfil.setEnabled(true);

                        new androidx.appcompat.app.AlertDialog.Builder(
                                PerfilActivity.this)
                                .setTitle("ERROR AL CARGAR PERFIL")
                                .setMessage(mensaje)
                                .setPositiveButton("ACEPTAR", null)
                                .show();
                    }
                });
    }

    private void guardarPerfil() {

        String nombre = etNombrePerfil.getText().toString().trim();
        String apellido = etApellidoPerfil.getText().toString().trim();
        String numero = etNumeroPerfil.getText().toString().trim();
        String descripcion = etDescripcionPerfil.getText().toString().trim();

        if (nombre.isEmpty()) {
            etNombrePerfil.setError("El nombre es obligatorio");
            return;
        }

        if (apellido.isEmpty()) {
            etApellidoPerfil.setError("El apellido es obligatorio");
            return;
        }

        if (nombre.length() > 50 || apellido.length() > 50) {
            Toast.makeText(this,
                    "Nombre y apellido deben tener máximo 50 caracteres.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (numero.length() > 20) {
            etNumeroPerfil.setError("Máximo 20 caracteres");
            return;
        }

        if (descripcion.length() > 150) {
            etDescripcionPerfil.setError("Máximo 150 caracteres");
            return;
        }

        btnGuardarPerfil.setEnabled(false);
        btnGuardarPerfil.setText("GUARDANDO...");

        perfilAPI.actualizarPerfil(
                userId,
                accessToken,
                nombre,
                apellido,
                numero,
                descripcion,
                new PerfilAPI.ActualizarCallback() {

                    @Override
                    public void onSuccess() {

                        btnGuardarPerfil.setEnabled(true);
                        btnGuardarPerfil.setText("GUARDAR CAMBIOS");

                        actualizarIniciales();

                        Toast.makeText(PerfilActivity.this,
                                "Perfil actualizado correctamente.",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String mensaje) {

                        btnGuardarPerfil.setEnabled(true);
                        btnGuardarPerfil.setText("GUARDAR CAMBIOS");

                        new androidx.appcompat.app.AlertDialog.Builder(
                                PerfilActivity.this)
                                .setTitle("ERROR AL GUARDAR")
                                .setMessage(mensaje)
                                .setPositiveButton("ACEPTAR", null)
                                .show();
                    }
                }
        );
    }

    private void actualizarIniciales() {

        String nombre = etNombrePerfil.getText().toString().trim();
        String apellido = etApellidoPerfil.getText().toString().trim();

        String iniciales = "";

        if (!nombre.isEmpty()) {
            iniciales += nombre.substring(0, 1).toUpperCase();
        }

        if (!apellido.isEmpty()) {
            iniciales += apellido.substring(0, 1).toUpperCase();
        }
    }
}
