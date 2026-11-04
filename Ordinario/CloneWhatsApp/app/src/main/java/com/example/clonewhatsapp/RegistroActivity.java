
package com.example.clonewhatsapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clonewhatsapp.config.AuthAPI;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etApellido;
    private EditText etNumero;
    private EditText etCorreo;
    private EditText etPassword;

    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registro);

        // Inicializar componentes
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etNumero = findViewById(R.id.etNumero);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);

        btnRegistrar = findViewById(R.id.btnRegistrar);

        // Evento del botón
        btnRegistrar.setOnClickListener(v -> registrarUsuario());

    }

    private void registrarUsuario() {

        String nombre = etNombre.getText().toString().trim();
        String apellido = etApellido.getText().toString().trim();
        String numero = etNumero.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validar campos
        if (nombre.isEmpty() || apellido.isEmpty()
                || correo.isEmpty() || password.isEmpty()) {

            Toast.makeText(
                    this,
                    "Completa todos los campos obligatorios",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (nombre.length() > 50 || apellido.length() > 50) {
            Toast.makeText(
                    this,
                    "Nombre y apellido deben tener máximo 50 caracteres",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (numero.length() > 20) {
            Toast.makeText(
                    this,
                    "El número no puede superar los 20 caracteres",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(correo).matches()) {

            Toast.makeText(
                    this,
                    "Introduce un correo electrónico válido",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (password.length() < 6) {
            Toast.makeText(
                    this,
                    "La contraseña debe tener al menos 6 caracteres",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Evitar múltiples solicitudes
        btnRegistrar.setEnabled(false);
        btnRegistrar.setText("REGISTRANDO...");

        // Registrar en Supabase
        AuthAPI.registrarUsuario(
                nombre,
                apellido,
                numero,
                correo,
                password,

                new AuthAPI.RegistrationCallback() {

                    @Override
                    public void onSuccess(
                            String userId,
                            boolean emailConfirmationRequired
                    ) {

                        btnRegistrar.setEnabled(true);
                        btnRegistrar.setText("REGISTRARSE");

                        if (emailConfirmationRequired) {

                            new AlertDialog.Builder(RegistroActivity.this)
                                    .setTitle("REGISTRO EXITOSO")
                                    .setMessage(
                                            "Tu cuenta fue creada correctamente.\n\n"
                                                    + "Confirma tu correo electrónico "
                                                    + "para continuar.\n\n"
                                                    + "ID: " + userId
                                    )
                                    .setPositiveButton("ACEPTAR", (dialog, which) -> {
                                        etNombre.setText("");
                                        etApellido.setText("");
                                        etNumero.setText("");
                                        etCorreo.setText("");
                                        etPassword.setText("");
                                    });

                        } else {

                            Toast.makeText(
                                    RegistroActivity.this,
                                    "Usuario registrado correctamente",
                                    Toast.LENGTH_LONG
                            ).show();

                            etNombre.setText("");
                            etApellido.setText("");
                            etNumero.setText("");
                            etCorreo.setText("");
                            etPassword.setText("");
                        }
                    }

                    @Override
                    public void onError(String error) {

                        btnRegistrar.setEnabled(true);
                        btnRegistrar.setText("REGISTRARSE");

                        new AlertDialog.Builder(RegistroActivity.this)
                                .setTitle("ERROR DE REGISTRO")
                                .setMessage(error)
                                .setPositiveButton("ACEPTAR", null)
                                .show();
                    }
                }
        );
    }
}
