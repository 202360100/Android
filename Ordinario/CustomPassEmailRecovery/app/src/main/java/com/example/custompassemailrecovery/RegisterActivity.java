package com.example.custompassemailrecovery;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText email, password, confirm;
    Button btnCreate;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.etRegEmail);
        password = findViewById(R.id.etRegPassword);
        confirm = findViewById(R.id.etRegConfirm);
        btnCreate = findViewById(R.id.btnCreate);
        status = findViewById(R.id.tvRegStatus);

        btnCreate.setOnClickListener(v -> register());
    }

    private void register() {

        String userEmail = email.getText().toString().trim();
        String pass = password.getText().toString();
        String passConfirm = confirm.getText().toString();

        if (userEmail.isEmpty() || pass.isEmpty() ||
                passConfirm.isEmpty()) {
            status.setText("ERROR: COMPLETE ALL FIELDS");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(userEmail).matches()) {
            status.setText("ERROR: INVALID EMAIL");
            return;
        }

        if (pass.length() < 8) {
            status.setText("ERROR: PASSWORD TOO SHORT");
            return;
        }

        if (!pass.equals(passConfirm)) {
            status.setText("ERROR: PASSWORDS DO NOT MATCH");
            return;
        }

        btnCreate.setEnabled(false);
        status.setText("CREATING USER...");

        SupabaseManager.register(userEmail, pass,
                new SupabaseManager.AuthCallback() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        runOnUiThread(() -> {
                            btnCreate.setEnabled(true);

                            status.setText(
                                    "REGISTRATION SUCCESSFUL\n" +
                                            "CHECK YOUR EMAIL IF CONFIRMATION IS REQUIRED."
                            );
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            btnCreate.setEnabled(true);
                            status.setText("ERROR: " + message);
                        });
                    }
                });
    }
}
