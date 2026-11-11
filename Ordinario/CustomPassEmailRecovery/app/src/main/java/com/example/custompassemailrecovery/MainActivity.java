package com.example.custompassemailrecovery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnRegister;
    TextView tvRecovery, tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvRecovery = findViewById(R.id.tvRecovery);
        tvStatus = findViewById(R.id.tvStatus);

        handleRecoveryLink(getIntent());

        btnLogin.setOnClickListener(v -> login());

        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(
                    this, RegisterActivity.class
            ));
        });

        tvRecovery.setOnClickListener(v -> {
            startActivity(new Intent(
                    this, RecoveryActivity.class
            ));
        });
    }

    private void login() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            tvStatus.setText("ERROR: COMPLETE ALL FIELDS");
            return;
        }

        btnLogin.setEnabled(false);
        tvStatus.setText("CONNECTING TO SERVER...");

        SupabaseManager.login(email, password,
                new SupabaseManager.AuthCallback() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        runOnUiThread(() -> {
                            btnLogin.setEnabled(true);

                            tvStatus.setText("ACCESS GRANTED");

                            Toast.makeText(
                                    MainActivity.this,
                                    "LOGIN SUCCESSFUL",
                                    Toast.LENGTH_SHORT
                            ).show();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            btnLogin.setEnabled(true);
                            tvStatus.setText("ACCESS DENIED: " + message);
                        });
                    }
                });
    }

    private void handleRecoveryLink(Intent intent) {

        if (intent == null ||
                intent.getData() == null) {
            return;
        }

        Uri uri = intent.getData();

        String token = SupabaseManager.getAccessToken(uri);

        if (token != null && !token.isEmpty()) {

            Intent newIntent = new Intent(
                    this, NewPasswordActivity.class
            );

            newIntent.putExtra("ACCESS_TOKEN", token);

            startActivity(newIntent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleRecoveryLink(intent);
    }
}
