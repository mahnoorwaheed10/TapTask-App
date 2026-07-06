package com.example.taptask;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView btnBack;
    private EditText etForgotEmail;
    private Button btnSendResetLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnBack = findViewById(R.id.btnBack);
        etForgotEmail = findViewById(R.id.etForgotEmail);
        btnSendResetLink = findViewById(R.id.btnSendResetLink);

        btnBack.setOnClickListener(v -> finish());

        btnSendResetLink.setOnClickListener(v -> {
            String email = etForgotEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Reset link sent to your email (demo only)", Toast.LENGTH_LONG).show();
        });
    }
}