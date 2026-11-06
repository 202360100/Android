package com.example.clonewhatsapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clonewhatsapp.config.AuthAPI;

public class MainActivity extends AppCompatActivity {

    private EditText etCorreoLogin, etPasswordLogin;
    private Button btnIniciarSesion, btnIrRegistro;
    private TextView btnRecuperarPassword;

    private AuthAPI authAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCorreoLogin = findViewById(R.id.etCorreoLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);
        btnRecuperarPassword = findViewById(R.id.btnRecuperarPassword);

        btnRecuperarPassword.setOnClickListener(v -> solicitarRecuperacion());

        authAPI = new AuthAPI();

        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());

        btnIrRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    private void iniciarSesion() {

        String email = etCorreoLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString();

        if (email.isEmpty()) {
            etCorreoLogin.setError("Ingresa tu correo");
            return;
        }

        if (password.isEmpty()) {
            etPasswordLogin.setError("Ingresa tu contraseña");
            return;
        }

        btnIniciarSesion.setEnabled(false);
        btnIniciarSesion.setText("CONECTANDO...");

        authAPI.iniciarSesion(email, password, new AuthAPI.LoginCallback() {

            @Override
            public void onSuccess(String userId, String accessToken,
                                  String refreshToken) {

                SharedPreferences preferences = getSharedPreferences(
                        "supabase_session", MODE_PRIVATE
                );

                preferences.edit()
                        .putString("user_id", userId)
                        .putString("access_token", accessToken)
                        .putString("refresh_token", refreshToken)
                        .apply();

                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String mensaje) {

                btnIniciarSesion.setEnabled(true);
                btnIniciarSesion.setText("INICIAR SESIÓN");

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("ERROR DE ACCESO")
                        .setMessage(mensaje)
                        .setPositiveButton("ACEPTAR", null)
                        .show();
            }
        });
    }


    private void solicitarRecuperacion() {

        String email = etCorreoLogin.getText().toString().trim();

        if (email.isEmpty()) {
            etCorreoLogin.setError("Ingresa tu correo electrónico");
            etCorreoLogin.requestFocus();
            return;
        }

        btnRecuperarPassword.setEnabled(false);

        authAPI.recuperarPassword(email, new AuthAPI.RecoveryCallback() {

            @Override
            public void onSuccess() {

                btnRecuperarPassword.setEnabled(true);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("CORREO ENVIADO")
                        .setMessage(
                                "Si la cuenta existe, recibirás un correo " +
                                        "con las instrucciones para recuperar tu contraseña."
                        )
                        .setPositiveButton("ACEPTAR", null)
                        .show();
            }

            @Override
            public void onError(String mensaje) {

                btnRecuperarPassword.setEnabled(true);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("ERROR")
                        .setMessage(mensaje)
                        .setPositiveButton("ACEPTAR", null)
                        .show();
            }
        });
    }

}