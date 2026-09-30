package com.example.taptask;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AdminProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private EditText etAdminName;
    private TextView tvEmail;
    private TextView tvRole;
    private TextView tvAvatar;
    private TextView btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        buildPage();
        loadAdminProfile();
    }

    // ============================================================
    // BUILD PAGE
    // ============================================================

    private void buildPage() {

        LinearLayout root =
                new LinearLayout(this);

        root.setOrientation(
                LinearLayout.VERTICAL
        );

        root.setBackgroundColor(
                Color.rgb(247, 246, 251)
        );

        // ========================================================
        // HEADER
        // ========================================================

        LinearLayout header =
                new LinearLayout(this);

        header.setOrientation(
                LinearLayout.HORIZONTAL
        );

        header.setGravity(
                Gravity.CENTER_VERTICAL
        );

        header.setPadding(
                dp(20),
                dp(18),
                dp(20),
                dp(18)
        );

        header.setBackground(
                createGradient()
        );

        LinearLayout.LayoutParams headerParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        header.setLayoutParams(
                headerParams
        );

        TextView back =
                new TextView(this);

        back.setText("←");

        back.setTextColor(
                Color.WHITE
        );

        back.setTextSize(28);

        back.setGravity(
                Gravity.CENTER
        );

        back.setPadding(
                0,
                0,
                dp(18),
                0
        );

        back.setOnClickListener(
                v -> finish()
        );

        header.addView(back);

        TextView title =
                new TextView(this);

        title.setText(
                "Admin Profile"
        );

        title.setTextColor(
                Color.WHITE
        );

        title.setTextSize(21);

        title.setTypeface(
                null,
                Typeface.BOLD
        );

        header.addView(title);

        root.addView(header);

        // ========================================================
        // CONTENT
        // ========================================================

        LinearLayout content =
                new LinearLayout(this);

        content.setOrientation(
                LinearLayout.VERTICAL
        );

        content.setPadding(
                dp(20),
                dp(24),
                dp(20),
                dp(30)
        );

        LinearLayout.LayoutParams contentParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -1
                );

        content.setLayoutParams(
                contentParams
        );

        // ========================================================
        // PROFILE CARD
        // ========================================================

        LinearLayout profileCard =
                new LinearLayout(this);

        profileCard.setOrientation(
                LinearLayout.VERTICAL
        );

        profileCard.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        profileCard.setPadding(
                dp(22),
                dp(26),
                dp(22),
                dp(26)
        );

        profileCard.setBackground(
                createCardBackground()
        );

        // Avatar

        tvAvatar =
                new TextView(this);

        tvAvatar.setText("A");

        tvAvatar.setTextColor(
                Color.WHITE
        );

        tvAvatar.setTextSize(28);

        tvAvatar.setTypeface(
                null,
                Typeface.BOLD
        );

        tvAvatar.setGravity(
                Gravity.CENTER
        );

        tvAvatar.setBackground(
                createAvatarBackground()
        );

        LinearLayout.LayoutParams avatarParams =
                new LinearLayout.LayoutParams(
                        dp(82),
                        dp(82)
                );

        avatarParams.bottomMargin =
                dp(16);

        tvAvatar.setLayoutParams(
                avatarParams
        );

        profileCard.addView(tvAvatar);

        TextView profileTitle =
                new TextView(this);

        profileTitle.setText(
                "Administrator Account"
        );

        profileTitle.setTextColor(
                Color.rgb(
                        38,
                        36,
                        55
                )
        );

        profileTitle.setTextSize(18);

        profileTitle.setTypeface(
                null,
                Typeface.BOLD
        );

        profileCard.addView(
                profileTitle
        );

        TextView profileSubtitle =
                new TextView(this);

        profileSubtitle.setText(
                "Manage your TapTask admin information"
        );

        profileSubtitle.setTextColor(
                Color.rgb(
                        120,
                        117,
                        139
                )
        );

        profileSubtitle.setTextSize(12);

        LinearLayout.LayoutParams subtitleParams =
                new LinearLayout.LayoutParams(
                        -2,
                        -2
                );

        subtitleParams.topMargin =
                dp(6);

        profileSubtitle.setLayoutParams(
                subtitleParams
        );

        profileCard.addView(
                profileSubtitle
        );

        content.addView(
                profileCard
        );

        // ========================================================
        // PERSONAL INFORMATION TITLE
        // ========================================================

        TextView sectionTitle =
                new TextView(this);

        sectionTitle.setText(
                "Personal Information"
        );

        sectionTitle.setTextColor(
                Color.rgb(
                        38,
                        36,
                        55
                )
        );

        sectionTitle.setTextSize(16);

        sectionTitle.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams sectionParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        sectionParams.topMargin =
                dp(24);

        sectionParams.bottomMargin =
                dp(12);

        sectionTitle.setLayoutParams(
                sectionParams
        );

        content.addView(
                sectionTitle
        );

        // ========================================================
        // NAME LABEL
        // ========================================================

        TextView nameLabel =
                createLabel(
                        "Admin Name"
                );

        content.addView(
                nameLabel
        );

        // ========================================================
        // NAME INPUT
        // ========================================================

        etAdminName =
                new EditText(this);

        etAdminName.setSingleLine(true);

        etAdminName.setTextSize(15);

        etAdminName.setTextColor(
                Color.rgb(
                        38,
                        36,
                        55
                )
        );

        etAdminName.setHint(
                "Enter admin name"
        );

        etAdminName.setHintTextColor(
                Color.rgb(
                        150,
                        147,
                        165
                )
        );

        etAdminName.setPadding(
                dp(16),
                dp(14),
                dp(16),
                dp(14)
        );

        etAdminName.setBackground(
                createInputBackground()
        );

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(56)
                );

        nameParams.bottomMargin =
                dp(20);

        etAdminName.setLayoutParams(
                nameParams
        );

        content.addView(
                etAdminName
        );

        // ========================================================
        // EMAIL LABEL
        // ========================================================

        TextView emailLabel =
                createLabel(
                        "Email Address"
                );

        content.addView(
                emailLabel
        );

        // ========================================================
        // EMAIL CARD
        // ========================================================

        tvEmail =
                new TextView(this);

        tvEmail.setText(
                "Loading..."
        );

        tvEmail.setTextColor(
                Color.rgb(
                        90,
                        87,
                        108
                )
        );

        tvEmail.setTextSize(14);

        tvEmail.setGravity(
                Gravity.CENTER_VERTICAL
        );

        tvEmail.setPadding(
                dp(16),
                0,
                dp(16),
                0
        );

        tvEmail.setBackground(
                createDisabledBackground()
        );

        LinearLayout.LayoutParams emailParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(56)
                );

        emailParams.bottomMargin =
                dp(20);

        tvEmail.setLayoutParams(
                emailParams
        );

        content.addView(
                tvEmail
        );

        // ========================================================
        // ROLE LABEL
        // ========================================================

        TextView roleLabel =
                createLabel(
                        "Account Role"
                );

        content.addView(
                roleLabel
        );

        // ========================================================
        // ROLE CARD
        // ========================================================

        tvRole =
                new TextView(this);

        tvRole.setText(
                "Administrator"
        );

        tvRole.setTextColor(
                Color.rgb(
                        93,
                        85,
                        200
                )
        );

        tvRole.setTextSize(14);

        tvRole.setTypeface(
                null,
                Typeface.BOLD
        );

        tvRole.setGravity(
                Gravity.CENTER_VERTICAL
        );

        tvRole.setPadding(
                dp(16),
                0,
                dp(16),
                0
        );

        tvRole.setBackground(
                createRoleBackground()
        );

        LinearLayout.LayoutParams roleParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(56)
                );

        roleParams.bottomMargin =
                dp(28);

        tvRole.setLayoutParams(
                roleParams
        );

        content.addView(
                tvRole
        );

        // ========================================================
        // SAVE BUTTON
        // ========================================================

        btnSave =
                new TextView(this);

        btnSave.setText(
                "Save Changes"
        );

        btnSave.setTextColor(
                Color.WHITE
        );

        btnSave.setTextSize(15);

        btnSave.setTypeface(
                null,
                Typeface.BOLD
        );

        btnSave.setGravity(
                Gravity.CENTER
        );

        btnSave.setBackground(
                createButtonBackground()
        );

        LinearLayout.LayoutParams saveParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(56)
                );

        btnSave.setLayoutParams(
                saveParams
        );

        btnSave.setOnClickListener(
                v -> saveAdminProfile()
        );

        content.addView(
                btnSave
        );

        // ========================================================
        // SECURITY NOTE
        // ========================================================

        TextView security =
                new TextView(this);

        security.setText(
                "🔒  Your email address is managed securely by Firebase. "
                        + "Only your display name can be changed from this page."
        );

        security.setTextColor(
                Color.rgb(
                        120,
                        117,
                        139
                )
        );

        security.setTextSize(11);

        security.setGravity(
                Gravity.CENTER
        );

        security.setLineSpacing(
                dp(2),
                1.0f
        );

        LinearLayout.LayoutParams securityParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        securityParams.topMargin =
                dp(16);

        security.setLayoutParams(
                securityParams
        );

        content.addView(
                security
        );

        root.addView(content);

        setContentView(root);
    }

    // ============================================================
    // LOAD PROFILE
    // ============================================================

    private void loadAdminProfile() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Admin account not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        // Email

        String email =
                user.getEmail();

        if (email != null &&
                !email.trim().isEmpty()) {

            tvEmail.setText(
                    email
            );

        } else {

            tvEmail.setText(
                    "Email unavailable"
            );
        }

        // Display name

        String displayName =
                user.getDisplayName();

        if (displayName != null &&
                !displayName.trim().isEmpty()) {

            etAdminName.setText(
                    displayName
            );

            etAdminName.setSelection(
                    etAdminName.length()
            );

            updateAvatar(
                    displayName
            );

        } else {

            String fallbackName =
                    "Administrator";

            etAdminName.setText(
                    fallbackName
            );

            updateAvatar(
                    fallbackName
            );
        }
    }

    // ============================================================
    // SAVE PROFILE
    // ============================================================

    private void saveAdminProfile() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Admin account not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String name =
                etAdminName.getText()
                        .toString()
                        .trim();

        if (name.isEmpty()) {

            etAdminName.setError(
                    "Please enter admin name"
            );

            etAdminName.requestFocus();

            return;
        }

        if (name.length() < 2) {

            etAdminName.setError(
                    "Name must contain at least 2 characters"
            );

            etAdminName.requestFocus();

            return;
        }

        btnSave.setText(
                "Saving..."
        );

        btnSave.setEnabled(false);

        UserProfileChangeRequest request =
                new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();

        user.updateProfile(request)
                .addOnSuccessListener(unused -> {

                    btnSave.setText(
                            "Save Changes"
                    );

                    btnSave.setEnabled(true);

                    updateAvatar(name);

                    Toast.makeText(
                            this,
                            "Admin profile updated successfully",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {

                    btnSave.setText(
                            "Save Changes"
                    );

                    btnSave.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Could not update profile",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // ============================================================
    // AVATAR
    // ============================================================

    private void updateAvatar(
            String name) {

        if (name == null ||
                name.trim().isEmpty()) {

            tvAvatar.setText("A");

            return;
        }

        String first =
                name.trim()
                        .substring(0, 1)
                        .toUpperCase();

        tvAvatar.setText(
                first
        );
    }

    // ============================================================
    // LABEL
    // ============================================================

    private TextView createLabel(
            String text) {

        TextView label =
                new TextView(this);

        label.setText(text);

        label.setTextColor(
                Color.rgb(
                        70,
                        67,
                        88
                )
        );

        label.setTextSize(13);

        label.setTypeface(
                null,
                Typeface.BOLD
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.bottomMargin =
                dp(8);

        label.setLayoutParams(
                params
        );

        return label;
    }

    // ============================================================
    // BACKGROUND HELPERS
    // ============================================================

    private GradientDrawable createGradient() {

        GradientDrawable drawable =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                Color.rgb(83, 73, 190),
                                Color.rgb(116, 91, 205)
                        }
                );

        drawable.setCornerRadius(
                dp(0)
        );

        return drawable;
    }

    private GradientDrawable createCardBackground() {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                Color.WHITE
        );

        drawable.setCornerRadius(
                dp(20)
        );

        drawable.setStroke(
                dp(1),
                Color.rgb(
                        229,
                        227,
                        238
                )
        );

        return drawable;
    }

    private GradientDrawable createAvatarBackground() {

        GradientDrawable drawable =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                Color.rgb(93, 85, 200),
                                Color.rgb(128, 91, 210)
                        }
                );

        drawable.setShape(
                GradientDrawable.OVAL
        );

        return drawable;
    }

    private GradientDrawable createInputBackground() {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                Color.WHITE
        );

        drawable.setCornerRadius(
                dp(14)
        );

        drawable.setStroke(
                dp(1),
                Color.rgb(
                        215,
                        212,
                        228
                )
        );

        return drawable;
    }

    private GradientDrawable createDisabledBackground() {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                Color.rgb(
                        239,
                        238,
                        245
                )
        );

        drawable.setCornerRadius(
                dp(14)
        );

        drawable.setStroke(
                dp(1),
                Color.rgb(
                        222,
                        220,
                        232
                )
        );

        return drawable;
    }

    private GradientDrawable createRoleBackground() {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(
                Color.rgb(
                        241,
                        239,
                        255
                )
        );

        drawable.setCornerRadius(
                dp(14)
        );

        drawable.setStroke(
                dp(1),
                Color.rgb(
                        213,
                        208,
                        247
                )
        );

        return drawable;
    }

    private GradientDrawable createButtonBackground() {

        GradientDrawable drawable =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                Color.rgb(93, 85, 200),
                                Color.rgb(116, 91, 205)
                        }
                );

        drawable.setCornerRadius(
                dp(16)
        );

        return drawable;
    }

    // ============================================================
    // DP
    // ============================================================

    private int dp(
            int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int)
                (value * density + 0.5f);
    }
}