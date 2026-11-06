
package com.example.clonewhatsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clonewhatsapp.config.AuthAPI;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etNuevaPassword;
    private EditText etConfirmarPassword;
    private Button btnCambiarPassword;

    private AuthAPI authAPI;
    private String recoveryAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etNuevaPassword = findViewById(R.id.etNuevaPassword);
        etConfirmarPassword = findViewById(R.id.etConfirmarPassword);
        btnCambiarPassword = findViewById(R.id.btnCambiarPassword);

        authAPI = new AuthAPI();

        procesarEnlace(getIntent());

        btnCambiarPassword.setOnClickListener(v -> cambiarPassword());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        procesarEnlace(intent);
    }

    private void procesarEnlace(Intent intent) {

        Uri uri = intent.getData();

        if (uri == null) {
            mostrarErrorEnlace();
            return;
        }

        // Supabase puede devolver los tokens en el fragmento (#...)
        // o como parámetros de consulta (?...).
        String accessToken = uri.getQueryParameter("access_token");
        String tipo = uri.getQueryParameter("type");

        String fragmento = uri.getEncodedFragment();

        if (fragmento != null && !fragmento.isEmpty()) {

            Uri fragmentUri = Uri.parse(
                    "https://recovery.local/?" + fragmento
            );

            if (accessToken == null) {
                accessToken = fragmentUri.getQueryParameter("access_token");
            }

            if (tipo == null) {
                tipo = fragmentUri.getQueryParameter("type");
            }
        }

        String error = uri.getQueryParameter("error_description");

        if (error == null && fragmento != null) {
            Uri fragmentUri = Uri.parse(
                    "https://recovery.local/?" + fragmento
            );
            error = fragmentUri.getQueryParameter("error_description");
        }

        if (error != null) {
            mostrarMensaje("ENLACE NO VÁLIDO", error);
            btnCambiarPassword.setEnabled(false);
            return;
        }

        if (accessToken == null || accessToken.isEmpty()
                || (tipo != null && !tipo.equals("recovery"))) {

            mostrarErrorEnlace();
            btnCambiarPassword.setEnabled(false);
            return;
        }

        recoveryAccessToken = accessToken;
    }

    private void cambiarPassword() {

        String nuevaPassword = etNuevaPassword.getText().toString();
        String confirmarPassword = etConfirmarPassword.getText().toString();

        if (nuevaPassword.isEmpty()) {
            etNuevaPassword.setError("Ingresa una contraseña");
            return;
        }

        if (nuevaPassword.length() < 6) {
            etNuevaPassword.setError(
                    "La contraseña debe tener al menos 6 caracteres"
            );
            return;
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            etConfirmarPassword.setError(
                    "Las contraseñas no coinciden"
            );
            return;
        }

        if (recoveryAccessToken == null) {
            mostrarErrorEnlace();
            return;
        }

        btnCambiarPassword.setEnabled(false);
        btnCambiarPassword.setText("ACTUALIZANDO...");

        authAPI.actualizarPassword(
                recoveryAccessToken,
                nuevaPassword,
                new AuthAPI.UpdatePasswordCallback() {

                    @Override
                    public void onSuccess() {

                        new AlertDialog.Builder(ResetPasswordActivity.this)
                                .setTitle("CONTRASEÑA ACTUALIZADA")
                                .setMessage(
                                        "Tu contraseña se cambió correctamente. " +
                                                "Ya puedes iniciar sesión."
                                )
                                .setCancelable(false)
                                .setPositiveButton("CONTINUAR", (dialog, which) -> {

                                    Intent intent = new Intent(
                                            ResetPasswordActivity.this,
                                            MainActivity.class
                                    );

                                    intent.setFlags(
                                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    );

                                    startActivity(intent);
                                    finish();
                                })
                                .show();
                    }

                    @Override
                    public void onError(String mensaje) {

                        btnCambiarPassword.setEnabled(true);
                        btnCambiarPassword.setText(
                                "ACTUALIZAR CONTRASEÑA"
                        );

                        mostrarMensaje("ERROR AL ACTUALIZAR", mensaje);
                    }
                }
        );
    }

    private void mostrarErrorEnlace() {
        mostrarMensaje(
                "ENLACE NO VÁLIDO",
                "El enlace de recuperación no contiene un token válido. " +
                        "Solicita uno nuevo desde la pantalla de inicio de sesión."
        );
    }

    private void mostrarMensaje(String titulo, String mensaje) {

        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("ACEPTAR", null)
                .show();
    }
}
