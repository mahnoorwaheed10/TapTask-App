package com.example.taptask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminSettingsActivity extends AppCompatActivity {

    private Switch switchNotifications;
    private TextView tvAdminEmail;

    private SharedPreferences preferences;
    private FirebaseAuth auth;

    private static final String PREFS_NAME =
            "admin_settings";

    private static final String KEY_NOTIFICATIONS =
            "notifications_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_settings
        );

        preferences =
                getSharedPreferences(
                        PREFS_NAME,
                        MODE_PRIVATE
                );

        auth =
                FirebaseAuth.getInstance();

        bindViews();
        setupPage();
        setupClicks();
    }

    // ============================================================
    // BIND VIEWS
    // ============================================================

    private void bindViews() {

        /*
         * IMPORTANT:
         *
         * In XML these are LinearLayouts,
         * so we use View instead of TextView.
         *
         * This prevents the previous ClassCastException.
         */

        TextView btnBack =
                findViewById(R.id.btnBack);

        View btnAdminProfile =
                findViewById(R.id.btnAdminProfile);

        View btnPaymentSettings =
                findViewById(R.id.btnPaymentSettings);

        View btnManageAreas =
                findViewById(R.id.btnManageAreas);

        View btnAbout =
                findViewById(R.id.btnAbout);

        View btnLogout =
                findViewById(R.id.btnLogout);

        switchNotifications =
                findViewById(
                        R.id.switchNotifications
                );

        tvAdminEmail =
                findViewById(
                        R.id.tvAdminEmail
                );
    }

    // ============================================================
    // PAGE SETUP
    // ============================================================

    private void setupPage() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user != null) {

            String email =
                    user.getEmail();

            if (email != null &&
                    !email.trim().isEmpty()) {

                tvAdminEmail.setText(
                        email
                );

            } else {

                tvAdminEmail.setText(
                        "Administrator"
                );
            }

        } else {

            tvAdminEmail.setText(
                    "Administrator"
            );
        }

        boolean notificationsEnabled =
                preferences.getBoolean(
                        KEY_NOTIFICATIONS,
                        true
                );

        switchNotifications.setChecked(
                notificationsEnabled
        );
    }

    // ============================================================
    // CLICK EVENTS
    // ============================================================

    private void setupClicks() {

        // ========================================================
        // BACK
        // ========================================================

        View btnBack =
                findViewById(R.id.btnBack);

        if (btnBack != null) {

            btnBack.setOnClickListener(
                    v -> finish()
            );
        }

        // ========================================================
        // ADMIN PROFILE
        // ========================================================

        View btnAdminProfile =
                findViewById(
                        R.id.btnAdminProfile
                );

        if (btnAdminProfile != null) {

            btnAdminProfile.setOnClickListener(
                    v -> {

                        Intent intent =
                                new Intent(
                                        AdminSettingsActivity.this,
                                        AdminProfileActivity.class
                                );

                        startActivity(intent);
                    }
            );
        }

        // ========================================================
        // PAYMENT SETTINGS
        // ========================================================

        View btnPaymentSettings =
                findViewById(
                        R.id.btnPaymentSettings
                );

        if (btnPaymentSettings != null) {

            btnPaymentSettings.setOnClickListener(
                    v -> {

                        Intent intent =
                                new Intent(
                                        AdminSettingsActivity.this,
                                        AdminPaymentSettingsActivity.class
                                );

                        startActivity(intent);
                    }
            );
        }

        // ========================================================
        // SERVICE AREAS
        // ========================================================

        View btnManageAreas =
                findViewById(
                        R.id.btnManageAreas
                );

        if (btnManageAreas != null) {

            btnManageAreas.setOnClickListener(
                    v -> {

                        Intent intent =
                                new Intent(
                                        AdminSettingsActivity.this,
                                        AdminManagementActivity.class
                                );

                        intent.putExtra(
                                "MODE",
                                "AREAS"
                        );

                        startActivity(intent);
                    }
            );
        }

        // ========================================================
        // ABOUT TAPTASK
        // ========================================================

        View btnAbout =
                findViewById(
                        R.id.btnAbout
                );

        if (btnAbout != null) {

            btnAbout.setOnClickListener(
                    v -> {

                        Intent intent =
                                new Intent(
                                        AdminSettingsActivity.this,
                                        AdminAboutActivity.class
                                );

                        startActivity(intent);
                    }
            );
        }

        // ========================================================
        // LOGOUT
        // ========================================================

        View btnLogout =
                findViewById(
                        R.id.btnLogout
                );

        if (btnLogout != null) {

            btnLogout.setOnClickListener(
                    v -> showLogoutConfirmation()
            );
        }

        // ========================================================
        // NOTIFICATIONS
        // ========================================================

        if (switchNotifications != null) {

            switchNotifications.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> {

                        preferences
                                .edit()
                                .putBoolean(
                                        KEY_NOTIFICATIONS,
                                        isChecked
                                )
                                .apply();

                        if (isChecked) {

                            Toast.makeText(
                                    this,
                                    "Admin notifications enabled",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else {

                            Toast.makeText(
                                    this,
                                    "Admin notifications disabled",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
        }

        // ========================================================
        // REMOVE OLD VERSION PLACEHOLDER
        // ========================================================

        removeVersionPlaceholder();
    }

    // ============================================================
    // REMOVE VERSION 1.0 TEXT
    // ============================================================

    private void removeVersionPlaceholder() {

        /*
         * Your existing XML contains:
         *
         * TapTask Admin Panel • Version 1.0
         *
         * We don't want to show a fake version number.
         *
         * Instead of changing XML, we update it here.
         */

        View root =
                findViewById(
                        android.R.id.content
                );

        if (root != null) {

            removeVersionText(
                    root
            );
        }
    }

    private void removeVersionText(
            View view) {

        if (view instanceof TextView) {

            TextView textView =
                    (TextView) view;

            String text =
                    textView.getText()
                            .toString();

            if (text.contains(
                    "Version 1.0"
            )) {

                textView.setText(
                        "TapTask Admin Panel"
                );
            }

            return;
        }

        if (view instanceof android.view.ViewGroup) {

            android.view.ViewGroup group =
                    (android.view.ViewGroup) view;

            for (int i = 0;
                 i < group.getChildCount();
                 i++) {

                removeVersionText(
                        group.getChildAt(i)
                );
            }
        }
    }

    // ============================================================
    // LOGOUT CONFIRMATION
    // ============================================================

    private void showLogoutConfirmation() {

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                "Logout"
                        )
                        .setMessage(
                                "Are you sure you want to logout from the admin panel?"
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Logout",
                                (d, which) ->
                                        performLogout()
                        )
                        .create();

        dialog.setOnShowListener(
                d -> {

                    TextView positive =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    TextView negative =
                            dialog.getButton(
                                    AlertDialog.BUTTON_NEGATIVE
                            );

                    if (positive != null) {

                        positive.setTextColor(
                                Color.rgb(
                                        190,
                                        58,
                                        72
                                )
                        );
                    }

                    if (negative != null) {

                        negative.setTextColor(
                                Color.rgb(
                                        93,
                                        85,
                                        200
                                )
                        );
                    }
                }
        );

        dialog.show();
    }

    // ============================================================
    // PERFORM LOGOUT
    // ============================================================

    private void performLogout() {

        auth.signOut();

        Intent intent =
                new Intent(
                        AdminSettingsActivity.this,
                        AuthActivity.class
                );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }
}