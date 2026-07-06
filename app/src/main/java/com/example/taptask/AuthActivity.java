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

public class AuthActivity extends AppCompatActivity {

    private TextView tabSignIn, tabSignUp;
    private LinearLayout signInForm, signUpForm;
    private TextView tvAuthTitle, tvAuthSubtitle;

    // Sign Up role buttons
    private TextView roleCustomer, roleWorker;

    // Sign In role buttons (new)
    private TextView signInRoleCustomer, signInRoleWorker;

    // Selected role for Sign Up
    private String selectedSignUpRole = "customer";

    // Selected role for Sign In
    private String selectedSignInRole = "customer";

    private EditText etSignInEmail, etSignInPassword;
    private EditText etSignUpName, etSignUpEmail, etSignUpPhone, etSignUpCity, etSignUpPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        bindViews();
        setupTabs();
        setupSignUpRoleButtons();
        setupSignInRoleButtons();
        setupButtons();
        setupSwitchLinks();
    }

    private void bindViews() {
        tabSignIn = findViewById(R.id.tabSignIn);
        tabSignUp = findViewById(R.id.tabSignUp);
        signInForm = findViewById(R.id.signInForm);
        signUpForm = findViewById(R.id.signUpForm);
        tvAuthTitle = findViewById(R.id.tvAuthTitle);
        tvAuthSubtitle = findViewById(R.id.tvAuthSubtitle);

        // Sign Up role buttons
        roleCustomer = findViewById(R.id.roleCustomer);
        roleWorker = findViewById(R.id.roleWorker);

        // Sign In role buttons
        signInRoleCustomer = findViewById(R.id.signInRoleCustomer);
        signInRoleWorker = findViewById(R.id.signInRoleWorker);

        etSignInEmail = findViewById(R.id.etSignInEmail);
        etSignInPassword = findViewById(R.id.etSignInPassword);

        etSignUpName = findViewById(R.id.etSignUpName);
        etSignUpEmail = findViewById(R.id.etSignUpEmail);
        etSignUpPhone = findViewById(R.id.etSignUpPhone);
        etSignUpCity = findViewById(R.id.etSignUpCity);
        etSignUpPassword = findViewById(R.id.etSignUpPassword);
    }

    private void setupTabs() {
        tabSignIn.setOnClickListener(v -> showSignIn());
        tabSignUp.setOnClickListener(v -> showSignUp());
    }

    private void showSignIn() {
        signInForm.setVisibility(View.VISIBLE);
        signUpForm.setVisibility(View.GONE);

        tabSignIn.setBackgroundResource(R.drawable.bg_tab_active);
        tabSignIn.setTextColor(getResources().getColor(R.color.primary));
        tabSignUp.setBackground(null);
        tabSignUp.setTextColor(getResources().getColor(R.color.muted));

        tvAuthTitle.setText(R.string.welcome_back);
        tvAuthSubtitle.setText(R.string.signin_subtitle);
    }

    private void showSignUp() {
        signInForm.setVisibility(View.GONE);
        signUpForm.setVisibility(View.VISIBLE);

        tabSignUp.setBackgroundResource(R.drawable.bg_tab_active);
        tabSignUp.setTextColor(getResources().getColor(R.color.primary));
        tabSignIn.setBackground(null);
        tabSignIn.setTextColor(getResources().getColor(R.color.muted));

        tvAuthTitle.setText("Create Account 🎉");
        tvAuthSubtitle.setText(R.string.or_form);
    }

    private void setupSignUpRoleButtons() {
        roleCustomer.setOnClickListener(v -> {
            selectedSignUpRole = "customer";
            roleCustomer.setBackgroundResource(R.drawable.bg_role_btn_active);
            roleWorker.setBackgroundResource(R.drawable.bg_role_btn_inactive);
        });

        roleWorker.setOnClickListener(v -> {
            selectedSignUpRole = "worker";
            roleWorker.setBackgroundResource(R.drawable.bg_role_btn_active);
            roleCustomer.setBackgroundResource(R.drawable.bg_role_btn_inactive);
        });
    }

    private void setupSignInRoleButtons() {
        signInRoleCustomer.setOnClickListener(v -> {
            selectedSignInRole = "customer";
            signInRoleCustomer.setBackgroundResource(R.drawable.bg_role_btn_active);
            signInRoleWorker.setBackgroundResource(R.drawable.bg_role_btn_inactive);
        });

        signInRoleWorker.setOnClickListener(v -> {
            selectedSignInRole = "worker";
            signInRoleWorker.setBackgroundResource(R.drawable.bg_role_btn_active);
            signInRoleCustomer.setBackgroundResource(R.drawable.bg_role_btn_inactive);
        });
    }

    private void setupButtons() {
        Button btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(v -> handleSignIn());

        Button btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(v -> handleSignUp());

        Button btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        btnGoogleSignIn.setOnClickListener(v ->
                Toast.makeText(this, "Google Sign-In (demo only)", Toast.LENGTH_SHORT).show());
    }

    private void setupSwitchLinks() {
        TextView linkGoToSignUp = findViewById(R.id.linkGoToSignUp);
        linkGoToSignUp.setOnClickListener(v -> showSignUp());

        TextView linkGoToSignIn = findViewById(R.id.linkGoToSignIn);
        linkGoToSignIn.setOnClickListener(v -> showSignIn());

        TextView tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(AuthActivity.this, ForgotPasswordActivity.class)));
    }

    private void handleSignIn() {
        String email = etSignInEmail.getText().toString().trim();
        String password = etSignInPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Admin check
        if (email.toLowerCase().contains("admin")) {
            startActivity(new Intent(AuthActivity.this, AdminDashboardActivity.class));
            finish();
            return;
        }

        // Navigate based on Sign In role selection
        if (selectedSignInRole.equals("worker")) {
            startActivity(new Intent(AuthActivity.this, WorkerDashboardActivity.class));
        } else {
            startActivity(new Intent(AuthActivity.this, HomeActivity.class));
        }
        finish();
    }

    private void handleSignUp() {
        String name = etSignUpName.getText().toString().trim();
        String email = etSignUpEmail.getText().toString().trim();
        String password = etSignUpPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Navigate based on Sign Up role selection
        if (selectedSignUpRole.equals("worker")) {
            startActivity(new Intent(AuthActivity.this, WorkerDashboardActivity.class));
        } else {
            startActivity(new Intent(AuthActivity.this, HomeActivity.class));
        }
        finish();
    }
}