
package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView btnBack;
    private TextView tvProfileName, tvProfileEmail, tvProfilePhone, tvProfileCity;
    private Button btnLogout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnBack = findViewById(R.id.btnBack);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvProfileEmail = findViewById(R.id.tvProfileEmail);
        tvProfilePhone = findViewById(R.id.tvProfilePhone);
        tvProfileCity = findViewById(R.id.tvProfileCity);
        btnLogout = findViewById(R.id.btnLogout);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnBack.setOnClickListener(v -> finish());

        loadUserProfile();

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();

            Intent intent = new Intent(
                    ProfileActivity.this,
                    AuthActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }

    private void loadUserProfile() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String uid = currentUser.getUid();
        String email = currentUser.getEmail();

        // Email Firebase Authentication se
        if (email != null) {
            tvProfileEmail.setText(email);
        } else {
            tvProfileEmail.setText("No email");
        }

        // Firestore users collection se baqi data
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String name = documentSnapshot.getString("name");
                        String phone = documentSnapshot.getString("phone");
                        String city = documentSnapshot.getString("city");

                        if (name != null && !name.isEmpty()) {
                            tvProfileName.setText(name);
                        } else {
                            tvProfileName.setText("User");
                        }

                        if (phone != null && !phone.isEmpty()) {
                            tvProfilePhone.setText(phone);
                        } else {
                            tvProfilePhone.setText("Not added");
                        }

                        if (city != null && !city.isEmpty()) {
                            tvProfileCity.setText(city);
                        } else {
                            tvProfileCity.setText("Not added");
                        }

                    } else {

                        tvProfileName.setText("User");
                        tvProfilePhone.setText("Not added");
                        tvProfileCity.setText("Not added");

                        Toast.makeText(
                                this,
                                "Profile data not found in users",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load profile",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}