package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WorkerProfileActivity extends AppCompatActivity {

    private TextView btnBack;

    private TextView tvProfileName;
    private TextView tvProfileEmail;
    private TextView tvProfilePhone;
    private TextView tvProfileCity;

    private Button btnLogout;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_worker_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        bindViews();

        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {

            firebaseAuth.signOut();

            Intent intent = new Intent(
                    WorkerProfileActivity.this,
                    AuthActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
        });

        loadWorkerProfile();
    }

    private void bindViews() {

        btnBack = findViewById(R.id.btnBack);

        tvProfileName =
                findViewById(R.id.tvProfileName);

        tvProfileEmail =
                findViewById(R.id.tvProfileEmail);

        tvProfilePhone =
                findViewById(R.id.tvProfilePhone);

        tvProfileCity =
                findViewById(R.id.tvProfileCity);

        btnLogout =
                findViewById(R.id.btnLogout);
    }

    private void loadWorkerProfile() {

        FirebaseUser currentUser =
                firebaseAuth.getCurrentUser();

        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid = currentUser.getUid();

        // First get worker document using Firebase UID
        firestore
                .collection("workers")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (document.exists()) {

                        showWorkerData(document);

                    } else {

                        // Fallback: get name from users collection
                        loadWorkerUsingName(currentUser);
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Could not load worker profile",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void loadWorkerUsingName(
            FirebaseUser currentUser
    ) {

        firestore
                .collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(userDocument -> {

                    String name =
                            getStringValue(
                                    userDocument,
                                    "name"
                            );

                    if (name.isEmpty()) {

                        showBasicFirebaseUserData(
                                currentUser
                        );

                        return;
                    }

                    firestore
                            .collection("workers")
                            .whereEqualTo(
                                    "name",
                                    name
                            )
                            .limit(1)
                            .get()
                            .addOnSuccessListener(workerSnapshot -> {

                                if (!workerSnapshot.isEmpty()) {

                                    DocumentSnapshot worker =
                                            workerSnapshot
                                                    .getDocuments()
                                                    .get(0);

                                    showWorkerData(worker);

                                } else {

                                    showBasicFirebaseUserData(
                                            currentUser
                                    );
                                }
                            });
                })
                .addOnFailureListener(e -> {

                    showBasicFirebaseUserData(
                            currentUser
                    );
                });
    }

    private void showWorkerData(
            DocumentSnapshot document
    ) {

        String name =
                getStringValue(
                        document,
                        "name"
                );

        String email =
                getStringValue(
                        document,
                        "email"
                );

        String phone =
                getStringValue(
                        document,
                        "phone"
                );

        String area =
                getStringValue(
                        document,
                        "area"
                );

        String category =
                getStringValue(
                        document,
                        "category"
                );

        String experience =
                getStringValue(
                        document,
                        "experience"
                );

        String rate =
                getStringValue(
                        document,
                        "rate"
                );

        // Name
        tvProfileName.setText(
                name.isEmpty()
                        ? "Worker"
                        : name
        );

        // Email
        if (email.isEmpty()) {

            FirebaseUser user =
                    firebaseAuth.getCurrentUser();

            if (user != null
                    && user.getEmail() != null) {

                email = user.getEmail();
            }
        }

        tvProfileEmail.setText(
                email
        );

        // Phone
        tvProfilePhone.setText(
                phone.isEmpty()
                        ? "Not provided"
                        : phone
        );

        // City / Area
        StringBuilder location =
                new StringBuilder();

        if (!area.isEmpty()) {

            location.append(
                    displayArea(area)
            );
        }

        if (!category.isEmpty()) {

            if (location.length() > 0) {
                location.append(" • ");
            }

            location.append(
                    displayCategory(category)
            );
        }

        if (!experience.isEmpty()) {

            if (location.length() > 0) {
                location.append("\n");
            }

            location.append(
                    "Experience: "
            );

            location.append(
                    experience
            );
        }

        if (!rate.isEmpty()) {

            if (location.length() > 0) {
                location.append("\n");
            }

            location.append(
                    "Rate: Rs. "
            );

            location.append(
                    rate
            );
        }

        tvProfileCity.setText(
                location.length() == 0
                        ? "Location not provided"
                        : location.toString()
        );
    }

    private void showBasicFirebaseUserData(
            FirebaseUser currentUser
    ) {

        String email =
                currentUser.getEmail();

        tvProfileEmail.setText(
                email == null
                        ? ""
                        : email
        );

        firestore
                .collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    String name =
                            getStringValue(
                                    document,
                                    "name"
                            );

                    String phone =
                            getStringValue(
                                    document,
                                    "phone"
                            );

                    tvProfileName.setText(
                            name.isEmpty()
                                    ? "Worker"
                                    : name
                    );

                    tvProfilePhone.setText(
                            phone.isEmpty()
                                    ? "Not provided"
                                    : phone
                    );
                });
    }

    private String getStringValue(
            DocumentSnapshot document,
            String field
    ) {

        Object value =
                document.get(field);

        if (value == null) {
            return "";
        }

        return String.valueOf(value).trim();
    }

    private String displayArea(
            String area
    ) {

        String value =
                area
                        .toLowerCase()
                        .trim();

        if (value.equals("tench")
                || value.equals("tench bhatta")) {

            return "Tench Bhatta";
        }

        if (value.equals("saddar")) {
            return "Saddar";
        }

        if (value.equals("online")) {
            return "Online";
        }

        return area;
    }

    private String displayCategory(
            String category
    ) {

        String value =
                category
                        .toLowerCase()
                        .trim();

        if (value.equals("electrician")) {
            return "Electrician";
        }

        if (value.equals("plumber")) {
            return "Plumber";
        }

        if (value.equals("cleaner")) {
            return "Cleaner";
        }

        if (value.equals("tailor")) {
            return "Tailor";
        }

        if (value.equals("mechanic")) {
            return "Mechanic";
        }

        if (value.equals("tutor_shop")) {
            return "Tutor";
        }

        if (value.equals("home_tutor")) {
            return "Home Tutor";
        }

        if (value.equals("consultation")) {
            return "Consultation";
        }

        if (value.equals("freelancer")) {
            return "Freelancer";
        }

        return category;
    }
}