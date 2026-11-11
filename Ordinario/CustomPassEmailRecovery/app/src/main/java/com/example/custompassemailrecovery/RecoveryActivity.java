package com.example.custompassemailrecovery;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

public class RecoveryActivity extends AppCompatActivity {

    EditText email;
    Button btnSend;
    TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recovery);

        email = findViewById(R.id.etRecoveryEmail);
        btnSend = findViewById(R.id.btnSendRecovery);
        status = findViewById(R.id.tvRecoveryStatus);

        btnSend.setOnClickListener(v -> sendRecovery());
    }

    private void sendRecovery() {

        String userEmail = email.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            status.setText("ERROR: INVALID EMAIL");
            return;
        }

        btnSend.setEnabled(false);
        status.setText("SENDING EMAIL...");

        SupabaseManager.recoverPassword(userEmail,
                new SupabaseManager.AuthCallback() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        runOnUiThread(() -> {
                            btnSend.setEnabled(true);

                            status.setText(
                                    "REQUEST PROCESSED.\n" +
                                            "CHECK YOUR EMAIL INBOX."
                            );
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            btnSend.setEnabled(true);
                            status.setText("ERROR: " + message);
                        });
                    }
                });
    }
}
