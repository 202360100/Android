package com.example.custompassemailrecovery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class NewPasswordActivity extends AppCompatActivity {

    EditText newPassword, confirmPassword;
    Button btnUpdate;
    TextView status;

    String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);

        newPassword = findViewById(R.id.etNewPassword);
        confirmPassword = findViewById(R.id.etConfirmPassword);
        btnUpdate = findViewById(R.id.btnUpdatePassword);
        status = findViewById(R.id.tvNewPasswordStatus);

        accessToken = getIntent().getStringExtra("ACCESS_TOKEN");

        if (accessToken == null || accessToken.isEmpty()) {
            status.setText("ERROR: INVALID RECOVERY SESSION");
            btnUpdate.setEnabled(false);
            return;
        }

        btnUpdate.setOnClickListener(v -> updatePassword());
    }

    private void updatePassword() {

        String pass = newPassword.getText().toString();
        String confirm = confirmPassword.getText().toString();

        if (pass.length() < 8) {
            status.setText("ERROR: MINIMUM 8 CHARACTERS");
            return;
        }

        if (!pass.equals(confirm)) {
            status.setText("ERROR: PASSWORDS DO NOT MATCH");
            return;
        }

        btnUpdate.setEnabled(false);
        status.setText("UPDATING PASSWORD...");

        SupabaseManager.updatePassword(
                accessToken,
                pass,
                new SupabaseManager.AuthCallback() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        runOnUiThread(() -> {
                            status.setText("PASSWORD UPDATED SUCCESSFULLY");

                            btnUpdate.setEnabled(true);

                            newPassword.postDelayed(() -> {
                                Intent intent = new Intent(
                                        NewPasswordActivity.this,
                                        MainActivity.class
                                );

                                intent.addFlags(
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                                Intent.FLAG_ACTIVITY_SINGLE_TOP
                                );

                                startActivity(intent);
                                finish();

                            }, 1800);
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            btnUpdate.setEnabled(true);
                            status.setText("ERROR: " + message);
                        });
                    }
                });
    }
}
