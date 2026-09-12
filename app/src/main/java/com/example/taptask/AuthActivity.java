package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    // ============================================================
    // VIEWS
    // ============================================================

    private TextView tvAuthTitle;
    private TextView tvAuthSubtitle;

    private TextView tabSignIn;
    private TextView tabSignUp;

    private LinearLayout signInForm;
    private LinearLayout signUpForm;

    // Sign In
    private EditText etSignInEmail;
    private EditText etSignInPassword;
    private TextView tvForgotPassword;
    private TextView signInRoleCustomer;
    private TextView signInRoleWorker;
    private Button btnSignIn;
    private TextView linkGoToSignUp;
    private Button btnGoogleSignIn;

    // Sign Up
    private EditText etSignUpName;
    private EditText etSignUpEmail;
    private EditText etSignUpPhone;
    private EditText etSignUpCity;
    private EditText etSignUpPassword;

    // Worker fields
    private EditText etWorkerCategory;
    private EditText etWorkerProfession;
    private EditText etWorkerArea;
    private EditText etWorkerExperience;
    private EditText etWorkerRate;

    private TextView workerCategoryLabel;
    private TextView workerProfessionLabel;
    private TextView workerAreaLabel;
    private TextView workerExperienceLabel;
    private TextView workerRateLabel;

    private TextView roleCustomer;
    private TextView roleWorker;

    private Button btnSignUp;
    private TextView linkGoToSignIn;

    // ============================================================
    // FIREBASE
    // ============================================================

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // ============================================================
    // ROLES
    // ============================================================

    private String signInRole = "customer";
    private String signUpRole = "customer";

    // ============================================================
    // ADMIN
    // ============================================================

    private static final String ADMIN_EMAIL = "admin@test.com";

    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auth);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bindViews();

        setupTabs();
        setupSignIn();
        setupSignUp();
        setupForgotPassword();
        setupGoogleButton();

        showSignIn();
    }

    // ============================================================
    // BIND VIEWS
    // ============================================================

    private void bindViews() {

        tvAuthTitle = findViewById(R.id.tvAuthTitle);
        tvAuthSubtitle = findViewById(R.id.tvAuthSubtitle);

        tabSignIn = findViewById(R.id.tabSignIn);
        tabSignUp = findViewById(R.id.tabSignUp);

        signInForm = findViewById(R.id.signInForm);
        signUpForm = findViewById(R.id.signUpForm);

        // Sign In
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);

        etSignInEmail = findViewById(R.id.etSignInEmail);
        etSignInPassword = findViewById(R.id.etSignInPassword);

        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        signInRoleCustomer = findViewById(R.id.signInRoleCustomer);
        signInRoleWorker = findViewById(R.id.signInRoleWorker);

        btnSignIn = findViewById(R.id.btnSignIn);

        linkGoToSignUp = findViewById(R.id.linkGoToSignUp);

        // Sign Up
        etSignUpName = findViewById(R.id.etSignUpName);
        etSignUpEmail = findViewById(R.id.etSignUpEmail);
        etSignUpPhone = findViewById(R.id.etSignUpPhone);
        etSignUpCity = findViewById(R.id.etSignUpCity);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);

        // Worker fields
        etWorkerCategory = findViewById(R.id.etWorkerCategory);
        etWorkerProfession = findViewById(R.id.etWorkerProfession);
        etWorkerArea = findViewById(R.id.etWorkerArea);
        etWorkerExperience = findViewById(R.id.etWorkerExperience);
        etWorkerRate = findViewById(R.id.etWorkerRate);

        workerCategoryLabel = findViewById(R.id.workerCategoryLabel);
        workerProfessionLabel = findViewById(R.id.workerProfessionLabel);
        workerAreaLabel = findViewById(R.id.workerAreaLabel);
        workerExperienceLabel = findViewById(R.id.workerExperienceLabel);
        workerRateLabel = findViewById(R.id.workerRateLabel);

        roleCustomer = findViewById(R.id.roleCustomer);
        roleWorker = findViewById(R.id.roleWorker);

        btnSignUp = findViewById(R.id.btnSignUp);
        linkGoToSignIn = findViewById(R.id.linkGoToSignIn);
    }

    // ============================================================
    // TABS
    // ============================================================

    private void setupTabs() {

        tabSignIn.setOnClickListener(v -> showSignIn());

        tabSignUp.setOnClickListener(v -> showSignUp());

        linkGoToSignUp.setOnClickListener(v -> showSignUp());

        linkGoToSignIn.setOnClickListener(v -> showSignIn());
    }

    // ============================================================
    // SHOW SIGN IN
    // ============================================================

    private void showSignIn() {

        signInForm.setVisibility(View.VISIBLE);
        signUpForm.setVisibility(View.GONE);

        tvAuthTitle.setText("Welcome Back");

        tvAuthSubtitle.setText("Sign in to continue to TapTask");

        tabSignIn.setBackgroundResource(R.drawable.bg_tab_active);
        tabSignUp.setBackgroundResource(R.drawable.bg_role_btn_inactive);

        tabSignIn.setTextColor(
                getResources().getColor(R.color.primary)
        );

        tabSignUp.setTextColor(
                getResources().getColor(R.color.muted)
        );
    }

    // ============================================================
    // SHOW SIGN UP
    // ============================================================

    private void showSignUp() {

        signInForm.setVisibility(View.GONE);
        signUpForm.setVisibility(View.VISIBLE);

        tvAuthTitle.setText("Create Account");

        tvAuthSubtitle.setText("Join TapTask today");

        tabSignUp.setBackgroundResource(R.drawable.bg_tab_active);
        tabSignIn.setBackgroundResource(R.drawable.bg_role_btn_inactive);

        tabSignUp.setTextColor(
                getResources().getColor(R.color.primary)
        );

        tabSignIn.setTextColor(
                getResources().getColor(R.color.muted)
        );
    }

    // ============================================================
    // SIGN IN SETUP
    // ============================================================

    private void setupSignIn() {

        signInRoleCustomer.setOnClickListener(
                v -> selectSignInRole("customer")
        );

        signInRoleWorker.setOnClickListener(
                v -> selectSignInRole("worker")
        );

        btnSignIn.setOnClickListener(
                v -> signInUser()
        );
    }

    // ============================================================
    // SIGN IN ROLE
    // ============================================================

    private void selectSignInRole(String role) {

        signInRoleCustomer.setBackgroundResource(
                R.drawable.bg_role_btn_inactive
        );

        signInRoleWorker.setBackgroundResource(
                R.drawable.bg_role_btn_inactive
        );

        if ("worker".equals(role)) {

            signInRoleWorker.setBackgroundResource(
                    R.drawable.bg_role_btn_active
            );

            signInRole = "worker";

        } else {

            signInRoleCustomer.setBackgroundResource(
                    R.drawable.bg_role_btn_active
            );

            signInRole = "customer";
        }
    }

    // ============================================================
    // SIGN IN
    // ============================================================

    private void signInUser() {

        String email =
                etSignInEmail.getText().toString().trim();

        String password =
                etSignInPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {

            etSignInEmail.setError("Enter your email");
            etSignInEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {

            etSignInPassword.setError("Enter your password");
            etSignInPassword.requestFocus();
            return;
        }

        btnSignIn.setEnabled(false);
        btnSignIn.setText("Signing in...");

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    FirebaseUser user = auth.getCurrentUser();

                    if (user == null) {

                        resetSignInButton();

                        Toast.makeText(
                                AuthActivity.this,
                                "Login failed",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    loadUserRoleAndOpenHome(user);
                })
                .addOnFailureListener(e -> {

                    resetSignInButton();

                    Toast.makeText(
                            AuthActivity.this,
                            "Login failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // ============================================================
    // RESET SIGN IN BUTTON
    // ============================================================

    private void resetSignInButton() {

        btnSignIn.setEnabled(true);
        btnSignIn.setText(R.string.sign_in_btn);
    }

    // ============================================================
    // LOAD USER ROLE
    // ============================================================

    private void loadUserRoleAndOpenHome(FirebaseUser user) {

        String uid = user.getUid();

        String email = user.getEmail();

        // ========================================================
        // ADMIN LOGIN
        // ========================================================

        if (email != null &&
                ADMIN_EMAIL.equalsIgnoreCase(email.trim())) {

            openCorrectDashboard("admin");
            return;
        }

        // ========================================================
        // CUSTOMER / WORKER LOGIN
        // ========================================================

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    String savedRole =
                            documentSnapshot.getString("role");

                    if (savedRole == null ||
                            savedRole.trim().isEmpty()) {

                        savedRole = signInRole;
                    }

                    openCorrectDashboard(savedRole);
                })
                .addOnFailureListener(e ->
                        openCorrectDashboard(signInRole)
                );
    }

    // ============================================================
    // OPEN DASHBOARD
    // ============================================================

    private void openCorrectDashboard(String role) {

        Intent intent;

        // ========================================================
        // ADMIN
        // ========================================================

        if ("admin".equalsIgnoreCase(role)) {

            intent = new Intent(
                    AuthActivity.this,
                    AdminDashboardActivity.class
            );

            // ========================================================
            // WORKER
            // ========================================================

        } else if ("worker".equalsIgnoreCase(role)) {

            intent = new Intent(
                    AuthActivity.this,
                    WorkerDashboardActivity.class
            );

            // ========================================================
            // CUSTOMER
            // ========================================================

        } else {

            intent = new Intent(
                    AuthActivity.this,
                    HomeActivity.class
            );
        }

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    // ============================================================
    // SIGN UP SETUP
    // ============================================================

    private void setupSignUp() {

        roleCustomer.setOnClickListener(
                v -> selectSignUpRole("customer")
        );

        roleWorker.setOnClickListener(
                v -> selectSignUpRole("worker")
        );

        btnSignUp.setOnClickListener(
                v -> signUpUser()
        );
    }

    // ============================================================
    // SIGN UP ROLE
    // ============================================================

    private void selectSignUpRole(String role) {

        roleCustomer.setBackgroundResource(
                R.drawable.bg_role_btn_inactive
        );

        roleWorker.setBackgroundResource(
                R.drawable.bg_role_btn_inactive
        );

        if ("worker".equals(role)) {

            roleWorker.setBackgroundResource(
                    R.drawable.bg_role_btn_active
            );

            signUpRole = "worker";

            workerCategoryLabel.setVisibility(View.VISIBLE);
            etWorkerCategory.setVisibility(View.VISIBLE);

            workerProfessionLabel.setVisibility(View.VISIBLE);
            etWorkerProfession.setVisibility(View.VISIBLE);

            workerAreaLabel.setVisibility(View.VISIBLE);
            etWorkerArea.setVisibility(View.VISIBLE);

            workerExperienceLabel.setVisibility(View.VISIBLE);
            etWorkerExperience.setVisibility(View.VISIBLE);

            workerRateLabel.setVisibility(View.VISIBLE);
            etWorkerRate.setVisibility(View.VISIBLE);

        } else {

            roleCustomer.setBackgroundResource(
                    R.drawable.bg_role_btn_active
            );

            signUpRole = "customer";

            workerCategoryLabel.setVisibility(View.GONE);
            etWorkerCategory.setVisibility(View.GONE);

            workerProfessionLabel.setVisibility(View.GONE);
            etWorkerProfession.setVisibility(View.GONE);

            workerAreaLabel.setVisibility(View.GONE);
            etWorkerArea.setVisibility(View.GONE);

            workerExperienceLabel.setVisibility(View.GONE);
            etWorkerExperience.setVisibility(View.GONE);

            workerRateLabel.setVisibility(View.GONE);
            etWorkerRate.setVisibility(View.GONE);
        }
    }

    // ============================================================
    // SIGN UP
    // ============================================================

    private void signUpUser() {

        String name =
                etSignUpName.getText().toString().trim();

        String email =
                etSignUpEmail.getText().toString().trim();

        String phone =
                etSignUpPhone.getText().toString().trim();

        String city =
                etSignUpCity.getText().toString().trim();

        String password =
                etSignUpPassword.getText().toString().trim();

        // Worker information
        String workerCategory =
                etWorkerCategory.getText().toString().trim();

        String workerProfession =
                etWorkerProfession.getText().toString().trim();

        String workerArea =
                etWorkerArea.getText().toString().trim();

        String workerExperience =
                etWorkerExperience.getText().toString().trim();

        String workerRate =
                etWorkerRate.getText().toString().trim();

        // ========================================================
        // BASIC VALIDATION
        // ========================================================

        if (TextUtils.isEmpty(name)) {

            etSignUpName.setError("Enter your name");
            etSignUpName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {

            etSignUpEmail.setError("Enter your email");
            etSignUpEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {

            etSignUpPhone.setError("Enter your phone number");
            etSignUpPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(city)) {

            etSignUpCity.setError("Enter your city / area");
            etSignUpCity.requestFocus();
            return;
        }

        // ========================================================
        // WORKER VALIDATION
        // ========================================================

        if ("worker".equals(signUpRole)) {

            if (TextUtils.isEmpty(workerCategory)) {

                etWorkerCategory.setError(
                        "Enter your category"
                );

                etWorkerCategory.requestFocus();
                return;
            }

            if (workerCategory.equalsIgnoreCase("select")) {

                etWorkerCategory.setError(
                        "Enter a valid category"
                );

                etWorkerCategory.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(workerProfession)) {

                etWorkerProfession.setError(
                        "Enter your profession / subject"
                );

                etWorkerProfession.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(workerArea)) {

                etWorkerArea.setError(
                        "Enter your area"
                );

                etWorkerArea.requestFocus();
                return;
            }

            if (workerArea.equalsIgnoreCase("select")) {

                etWorkerArea.setError(
                        "Enter a valid area"
                );

                etWorkerArea.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(workerExperience)) {

                etWorkerExperience.setError(
                        "Enter your experience"
                );

                etWorkerExperience.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(workerRate)) {

                etWorkerRate.setError(
                        "Enter your rate"
                );

                etWorkerRate.requestFocus();
                return;
            }
        }

        if (TextUtils.isEmpty(password)) {

            etSignUpPassword.setError(
                    "Enter a password"
            );

            etSignUpPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {

            etSignUpPassword.setError(
                    "Password must be at least 6 characters"
            );

            etSignUpPassword.requestFocus();
            return;
        }

        btnSignUp.setEnabled(false);
        btnSignUp.setText("Creating account...");

        // ========================================================
        // CREATE FIREBASE ACCOUNT
        // ========================================================

        auth.createUserWithEmailAndPassword(
                        email,
                        password
                )
                .addOnSuccessListener(authResult -> {

                    FirebaseUser user =
                            auth.getCurrentUser();

                    if (user == null) {

                        resetSignUpButton();

                        Toast.makeText(
                                AuthActivity.this,
                                "Account creation failed",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    saveUserProfile(
                            user,
                            name,
                            email,
                            phone,
                            city,
                            workerCategory,
                            workerProfession,
                            workerArea,
                            workerExperience,
                            workerRate
                    );
                })
                .addOnFailureListener(e -> {

                    resetSignUpButton();

                    Toast.makeText(
                            AuthActivity.this,
                            "Sign up failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // ============================================================
    // SAVE USER PROFILE
    // ============================================================

    private void saveUserProfile(
            FirebaseUser user,
            String name,
            String email,
            String phone,
            String city,
            String workerCategory,
            String workerProfession,
            String workerArea,
            String workerExperience,
            String workerRate
    ) {

        String uid = user.getUid();

        Map<String, Object> userData =
                new HashMap<>();

        userData.put("uid", uid);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("city", city);
        userData.put("role", signUpRole);
        userData.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users")
                .document(uid)
                .set(userData)
                .addOnSuccessListener(unused -> {

                    if (!"worker".equals(signUpRole)) {

                        Toast.makeText(
                                AuthActivity.this,
                                "Account created successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                        openCorrectDashboard(signUpRole);
                        return;
                    }

                    saveWorkerProfile(
                            uid,
                            name,
                            workerCategory,
                            workerProfession,
                            workerArea,
                            workerExperience,
                            workerRate,
                            phone
                    );
                })
                .addOnFailureListener(e -> {

                    resetSignUpButton();

                    Toast.makeText(
                            AuthActivity.this,
                            "Account created, but profile could not be saved: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // ============================================================
    // NORMALIZE WORKER CATEGORY
    // ============================================================

    private String normalizeWorkerCategory(String category) {

        if (category == null) {
            return "";
        }

        String value =
                category.trim().toLowerCase();

        // HOME
        if (value.equals("electrician") ||
                value.equals("electrician service") ||
                value.equals("electrical")) {

            return "electrician";
        }

        if (value.equals("plumber") ||
                value.equals("plumbing") ||
                value.equals("plumbing service")) {

            return "plumber";
        }

        if (value.equals("cleaner") ||
                value.equals("cleaning") ||
                value.equals("cleaning service")) {

            return "cleaner";
        }

        // SHOP
        if (value.equals("tailor") ||
                value.equals("tailoring")) {

            return "tailor";
        }

        if (value.equals("mechanic") ||
                value.equals("mechanics") ||
                value.equals("auto mechanic")) {

            return "mechanic";
        }

        if (value.equals("tutor") ||
                value.equals("shop tutor") ||
                value.equals("tutor shop")) {

            return "tutor_shop";
        }

        // ONLINE
        if (value.equals("home tutor") ||
                value.equals("home tutoring") ||
                value.equals("online tutor")) {

            return "home_tutor";
        }

        if (value.equals("consultation") ||
                value.equals("consultant") ||
                value.equals("legal consultation")) {

            return "consultation";
        }

        if (value.equals("freelancer") ||
                value.equals("freelancing") ||
                value.equals("freelance")) {

            return "freelancer";
        }

        return value.replace(" ", "_");
    }

    // ============================================================
    // NORMALIZE WORKER AREA
    // ============================================================

    private String normalizeWorkerArea(String area) {

        if (area == null) {
            return "";
        }

        String value =
                area.trim().toLowerCase();

        if (value.equals("tench") ||
                value.equals("tench bhatta") ||
                value.equals("tench bhatta rawalpindi")) {

            return "tench";
        }

        if (value.equals("saddar") ||
                value.equals("saddar rawalpindi")) {

            return "saddar";
        }

        if (value.equals("online")) {

            return "online";
        }

        return value;
    }

    // ============================================================
    // SAVE WORKER PROFILE
    // ============================================================

    private void saveWorkerProfile(
            String uid,
            String name,
            String category,
            String profession,
            String area,
            String experience,
            String rate,
            String phone
    ) {

        String finalCategory =
                normalizeWorkerCategory(category);

        String finalArea =
                normalizeWorkerArea(area);

        String finalName =
                name == null ? "" : name.trim();

        String finalProfession =
                profession == null ? "" : profession.trim();

        String finalExperience =
                experience == null ? "" : experience.trim();

        String finalRate =
                rate == null ? "" : rate.trim();

        String finalPhone =
                phone == null ? "" : phone.trim();

        // ========================================================
        // WORKER DATA
        // ========================================================

        Map<String, Object> workerData =
                new HashMap<>();

        workerData.put("uid", uid);

        workerData.put("name", finalName);

        // Category remains the filtering category
        workerData.put("category", finalCategory);

        // Profession / subject
        workerData.put("profession", finalProfession);

        // Title shows profession / subject
        workerData.put(
                "title",
                finalProfession.isEmpty()
                        ? finalCategory
                        : finalProfession
        );

        workerData.put("area", finalArea);

        workerData.put("experience", finalExperience);

        workerData.put("rate", finalRate);

        workerData.put("phone", finalPhone);

        workerData.put("rating", 0.0);

        workerData.put("reviews", 0);

        workerData.put("isAvailable", true);
        workerData.put("verificationStatus", "pending");

        workerData.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );

        // ========================================================
        // SAVE USING UID
        // ========================================================

        db.collection("workers")
                .document(uid)
                .set(workerData)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            AuthActivity.this,
                            "Worker account created successfully!",
                            Toast.LENGTH_SHORT
                    ).show();

                    openCorrectDashboard("worker");
                })
                .addOnFailureListener(e -> {

                    resetSignUpButton();

                    Toast.makeText(
                            AuthActivity.this,
                            "Account created, but worker profile could not be saved: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // ============================================================
    // RESET SIGN UP BUTTON
    // ============================================================

    private void resetSignUpButton() {

        btnSignUp.setEnabled(true);

        btnSignUp.setText(
                R.string.create_account_btn
        );
    }

    // ============================================================
    // FORGOT PASSWORD
    // ============================================================

    private void setupForgotPassword() {

        tvForgotPassword.setOnClickListener(
                v -> resetPassword()
        );
    }

    private void resetPassword() {

        String email =
                etSignInEmail.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(email)) {

            etSignInEmail.setError(
                    "Enter your email first"
            );

            etSignInEmail.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(
                        unused ->
                                Toast.makeText(
                                        AuthActivity.this,
                                        "Password reset email sent.",
                                        Toast.LENGTH_LONG
                                ).show()
                )
                .addOnFailureListener(
                        e ->
                                Toast.makeText(
                                        AuthActivity.this,
                                        "Could not send reset email: "
                                                + e.getMessage(),
                                        Toast.LENGTH_LONG
                                ).show()
                );
    }

    // ============================================================
    // GOOGLE SIGN IN
    // ============================================================

    private void setupGoogleButton() {

        btnGoogleSignIn.setOnClickListener(
                v -> Toast.makeText(
                        AuthActivity.this,
                        "Google sign-in setup is not connected yet.",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }
}