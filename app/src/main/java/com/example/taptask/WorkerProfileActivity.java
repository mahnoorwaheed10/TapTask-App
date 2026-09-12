package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WorkerProfileActivity extends AppCompatActivity {

    private TextView btnBack;

    // Top profile section
    private TextView tvWorkerProfileName;
    private TextView tvWorkerProfileSpecialization;
    private TextView tvWorkerProfileJobs;
    private TextView tvWorkerProfileRating;

    // Editable fields
    private EditText etWorkerName;
    private EditText etWorkerCategory;
    private EditText etWorkerSpecialization;
    private EditText etWorkerPhone;
    private EditText etWorkerArea;
    private EditText etWorkerExperience;
    private EditText etWorkerRate;

    private Button btnSaveWorkerProfile;
    private Button btnWorkerLogout;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    // Existing worker document
    private DocumentSnapshot currentWorkerDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_worker_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        bindViews();

        btnBack.setOnClickListener(v -> finish());

        btnWorkerLogout.setOnClickListener(v -> {

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

        btnSaveWorkerProfile.setOnClickListener(
                v -> saveWorkerProfile()
        );

        loadWorkerProfile();
    }


    // ============================================================
    // BIND VIEWS
    // ============================================================

    private void bindViews() {

        btnBack = findViewById(R.id.btnBack);

        tvWorkerProfileName =
                findViewById(R.id.tvWorkerProfileName);

        tvWorkerProfileSpecialization =
                findViewById(R.id.tvWorkerProfileSpecialization);

        tvWorkerProfileJobs =
                findViewById(R.id.tvWorkerProfileJobs);

        tvWorkerProfileRating =
                findViewById(R.id.tvWorkerProfileRating);


        etWorkerName =
                findViewById(R.id.etWorkerName);

        etWorkerCategory =
                findViewById(R.id.etWorkerCategory);

        etWorkerSpecialization =
                findViewById(R.id.etWorkerSpecialization);

        etWorkerPhone =
                findViewById(R.id.etWorkerPhone);

        etWorkerArea =
                findViewById(R.id.etWorkerArea);

        etWorkerExperience =
                findViewById(R.id.etWorkerExperience);

        etWorkerRate =
                findViewById(R.id.etWorkerRate);


        btnSaveWorkerProfile =
                findViewById(R.id.btnSaveWorkerProfile);

        btnWorkerLogout =
                findViewById(R.id.btnWorkerLogout);
    }


    // ============================================================
    // LOAD WORKER PROFILE
    // ============================================================

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

        // --------------------------------------------------------
        // STEP 1
        // Automatic worker:
        // workers/{UID}
        // --------------------------------------------------------

        firestore
                .collection("workers")
                .document(uid)
                .get()
                .addOnSuccessListener(document -> {

                    if (document.exists()) {

                        currentWorkerDocument = document;

                        // Make sure this worker is linked
                        linkWorkerToAuth(document, uid);

                        showWorkerData(document);

                    } else {

                        // ------------------------------------------------
                        // STEP 2
                        // Manual worker already linked with authUid
                        // ------------------------------------------------

                        findWorkerByAuthUid(uid);
                    }

                })
                .addOnFailureListener(e ->
                        findWorkerByAuthUid(uid)
                );
    }


    // ============================================================
    // FIND MANUAL WORKER BY AUTH UID
    // ============================================================

    private void findWorkerByAuthUid(String uid) {

        firestore
                .collection("workers")
                .whereEqualTo("authUid", uid)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.isEmpty()) {

                        DocumentSnapshot worker =
                                snapshot.getDocuments().get(0);

                        currentWorkerDocument = worker;

                        showWorkerData(worker);

                    } else {

                        FirebaseUser user =
                                firebaseAuth.getCurrentUser();

                        if (user != null) {

                            // No authUid yet.
                            // Find existing manual worker
                            // through users/{UID}.
                            findWorkerUsingUserRecord(user);
                        }
                    }

                })
                .addOnFailureListener(e -> {

                    FirebaseUser user =
                            firebaseAuth.getCurrentUser();

                    if (user != null) {
                        findWorkerUsingUserRecord(user);
                    }
                });
    }


    // ============================================================
    // FIND MANUAL WORKER USING users/{UID}
    // ============================================================

    private void findWorkerUsingUserRecord(
            FirebaseUser currentUser
    ) {

        String uid = currentUser.getUid();

        firestore
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(userDocument -> {

                    if (!userDocument.exists()) {

                        showBasicUserData(currentUser);
                        return;
                    }

                    String userName =
                            getStringValue(
                                    userDocument,
                                    "name"
                            );

                    if (TextUtils.isEmpty(userName)) {

                        showBasicUserData(currentUser);
                        return;
                    }

                    // ------------------------------------------------
                    // Existing manual worker is stored using NAME
                    // ------------------------------------------------

                    firestore
                            .collection("workers")
                            .whereEqualTo("name", userName)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(workerSnapshot -> {

                                if (!workerSnapshot.isEmpty()) {

                                    DocumentSnapshot worker =
                                            workerSnapshot
                                                    .getDocuments()
                                                    .get(0);

                                    currentWorkerDocument = worker;

                                    // ------------------------------------------------
                                    // IMPORTANT:
                                    // Automatically connect this existing
                                    // worker document to logged-in Auth account.
                                    // ------------------------------------------------

                                    Map<String, Object> link =
                                            new HashMap<>();

                                    link.put("authUid", uid);

                                    firestore
                                            .collection("workers")
                                            .document(worker.getId())
                                            .update(link)
                                            .addOnSuccessListener(unused -> {

                                                // Now permanently linked.
                                                showWorkerData(worker);

                                            })
                                            .addOnFailureListener(e -> {

                                                // Even if linking fails,
                                                // still show existing data.
                                                showWorkerData(worker);
                                            });

                                } else {

                                    showBasicUserData(currentUser);
                                }

                            })
                            .addOnFailureListener(e ->
                                    showBasicUserData(currentUser)
                            );

                })
                .addOnFailureListener(e ->
                        showBasicUserData(currentUser)
                );
    }


    // ============================================================
    // LINK EXISTING WORKER TO AUTH
    // ============================================================

    private void linkWorkerToAuth(
            DocumentSnapshot worker,
            String uid
    ) {

        String existingAuthUid =
                getStringValue(worker, "authUid");

        if (!uid.equals(existingAuthUid)) {

            Map<String, Object> update =
                    new HashMap<>();

            update.put("authUid", uid);

            firestore
                    .collection("workers")
                    .document(worker.getId())
                    .update(update);
        }
    }


    // ============================================================
    // SHOW EXACT FIREBASE WORKER DATA
    // ============================================================

    private void showWorkerData(
            DocumentSnapshot document
    ) {

        String name =
                getStringValue(document, "name");

        String category =
                getStringValue(document, "category");

        String title =
                getStringValue(document, "title");

        String phone =
                getStringValue(document, "phone");

        String area =
                getStringValue(document, "area");

        String experience =
                getStringValue(document, "experience");

        String rate =
                getStringValue(document, "rate");


        Object ratingObject =
                document.get("rating");

        Object reviewsObject =
                document.get("reviews");


        // --------------------------------------------------------
        // NAME
        // --------------------------------------------------------

        etWorkerName.setText(name);

        tvWorkerProfileName.setText(
                name.isEmpty()
                        ? "Worker"
                        : name
        );


        // --------------------------------------------------------
        // CATEGORY
        // --------------------------------------------------------
        // Keep the actual Firebase value.
        // Example:
        // cleaner
        // tutor_shop
        // freelancer
        // --------------------------------------------------------

        etWorkerCategory.setText(category);


        // --------------------------------------------------------
        // SPECIALIZATION / TITLE
        // --------------------------------------------------------

        etWorkerSpecialization.setText(title);

        tvWorkerProfileSpecialization.setText(
                title.isEmpty()
                        ? category
                        : title
        );


        // --------------------------------------------------------
        // PHONE
        // --------------------------------------------------------

        etWorkerPhone.setText(phone);


        // --------------------------------------------------------
        // AREA
        // --------------------------------------------------------

        etWorkerArea.setText(area);


        // --------------------------------------------------------
        // EXPERIENCE
        // --------------------------------------------------------

        etWorkerExperience.setText(experience);


        // --------------------------------------------------------
        // RATE
        // --------------------------------------------------------

        etWorkerRate.setText(rate);


        // --------------------------------------------------------
        // RATING
        // --------------------------------------------------------

        double rating = 0;

        if (ratingObject instanceof Number) {

            rating =
                    ((Number) ratingObject).doubleValue();
        }

        String ratingText =
                String.format("%.1f", rating);

        tvWorkerProfileRating.setText(
                ratingText
        );


        // --------------------------------------------------------
        // REVIEWS / JOBS
        // --------------------------------------------------------

        int reviews = 0;

        if (reviewsObject instanceof Number) {

            reviews =
                    ((Number) reviewsObject).intValue();
        }

        tvWorkerProfileJobs.setText(
                String.valueOf(reviews)
        );
    }


    // ============================================================
    // BASIC USER FALLBACK
    // ============================================================

    private void showBasicUserData(
            FirebaseUser currentUser
    ) {

        firestore
                .collection("users")
                .document(currentUser.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    String userName =
                            getStringValue(
                                    document,
                                    "name"
                            );

                    String userPhone =
                            getStringValue(
                                    document,
                                    "phone"
                            );

                    etWorkerName.setText(userName);
                    etWorkerPhone.setText(userPhone);

                    tvWorkerProfileName.setText(
                            userName.isEmpty()
                                    ? "Worker"
                                    : userName
                    );

                    tvWorkerProfileSpecialization.setText(
                            "Worker"
                    );

                    tvWorkerProfileJobs.setText(
                            "0"
                    );

                    tvWorkerProfileRating.setText(
                            "0.0"
                    );

                });
    }


    // ============================================================
    // SAVE PROFILE
    // ============================================================

    private void saveWorkerProfile() {

        if (currentWorkerDocument == null) {

            Toast.makeText(
                    this,
                    "Worker profile not found",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        String name =
                etWorkerName.getText()
                        .toString()
                        .trim();

        String category =
                etWorkerCategory.getText()
                        .toString()
                        .trim();

        String title =
                etWorkerSpecialization.getText()
                        .toString()
                        .trim();

        String phone =
                etWorkerPhone.getText()
                        .toString()
                        .trim();

        String area =
                etWorkerArea.getText()
                        .toString()
                        .trim();

        String experience =
                etWorkerExperience.getText()
                        .toString()
                        .trim();

        String rate =
                etWorkerRate.getText()
                        .toString()
                        .trim();


        if (TextUtils.isEmpty(name)) {

            Toast.makeText(
                    this,
                    "Please enter worker name",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String documentId =
                currentWorkerDocument.getId();


        Map<String, Object> updates =
                new HashMap<>();

        updates.put("name", name);
        updates.put("category", category);
        updates.put("title", title);
        updates.put("phone", phone);
        updates.put("area", area);
        updates.put("experience", experience);
        updates.put("rate", rate);


        // --------------------------------------------------------
        // IMPORTANT:
        // Keep Auth UID attached to the worker.
        // --------------------------------------------------------

        FirebaseUser currentUser =
                firebaseAuth.getCurrentUser();

        if (currentUser != null) {

            updates.put(
                    "authUid",
                    currentUser.getUid()
            );
        }


        btnSaveWorkerProfile.setEnabled(false);


        firestore
                .collection("workers")
                .document(documentId)
                .update(updates)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            WorkerProfileActivity.this,
                            "Profile updated successfully!",
                            Toast.LENGTH_SHORT
                    ).show();


                    tvWorkerProfileName.setText(
                            name
                    );

                    tvWorkerProfileSpecialization.setText(
                            title.isEmpty()
                                    ? category
                                    : title
                    );


                    btnSaveWorkerProfile.setEnabled(true);

                })
                .addOnFailureListener(e -> {

                    btnSaveWorkerProfile.setEnabled(true);

                    Toast.makeText(
                            WorkerProfileActivity.this,
                            "Update failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }


    // ============================================================
    // FIRESTORE VALUE
    // ============================================================

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
}