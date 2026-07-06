
package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class WorkerProfileActivity extends AppCompatActivity {

    private TextView btnBack;
    private EditText etWorkerName, etWorkerSpecialization, etWorkerPhone, etWorkerArea, etWorkerRate;
    private Button btnSaveWorkerProfile, btnWorkerLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile);

        btnBack = findViewById(R.id.btnBack);
        etWorkerName = findViewById(R.id.etWorkerName);
        etWorkerSpecialization = findViewById(R.id.etWorkerSpecialization);
        etWorkerPhone = findViewById(R.id.etWorkerPhone);
        etWorkerArea = findViewById(R.id.etWorkerArea);
        etWorkerRate = findViewById(R.id.etWorkerRate);
        btnSaveWorkerProfile = findViewById(R.id.btnSaveWorkerProfile);
        btnWorkerLogout = findViewById(R.id.btnWorkerLogout);

        btnBack.setOnClickListener(v -> finish());

        btnSaveWorkerProfile.setOnClickListener(v ->
                Toast.makeText(this, "Profile updated! (Will sync with backend once connected)", Toast.LENGTH_SHORT).show());

        btnWorkerLogout.setOnClickListener(v -> {
            Intent intent = new Intent(WorkerProfileActivity.this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}