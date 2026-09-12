package com.example.taptask;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private TextView tvTotalUsers;
    private TextView tvTotalWorkers;
    private TextView tvPendingVerification;
    private TextView tvTotalBookings;
    private TextView tvTotalRevenue;

    private LinearLayout pendingWorkersContainer;
    private LinearLayout recentActivityContainer;

    private final List<DocumentSnapshot> pendingWorkers =
            new ArrayList<>();

    private final List<DocumentSnapshot> recentBookings =
            new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_dashboard);

        db = FirebaseFirestore.getInstance();

        bindViews();
        setupClicks();
        loadDashboardData();
    }

    private void bindViews() {

        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalWorkers = findViewById(R.id.tvTotalWorkers);
        tvPendingVerification = findViewById(R.id.tvPendingVerification);
        tvTotalBookings = findViewById(R.id.tvTotalBookings);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);

        pendingWorkersContainer =
                findViewById(R.id.pendingWorkersContainer);

        recentActivityContainer =
                findViewById(R.id.recentActivityContainer);
    }

    // ============================================================
    // BUTTON CLICKS
    // ============================================================

    private void setupClicks() {

        View manageUsers = findViewById(R.id.btnManageUsers);

        if (manageUsers != null) {
            manageUsers.setOnClickListener(v -> {

                Intent intent = new Intent(
                        AdminDashboardActivity.this,
                        AdminManagementActivity.class
                );

                intent.putExtra("MODE", "USERS");
                startActivity(intent);
            });
        }


        View manageWorkers = findViewById(R.id.btnManageWorkers);

        if (manageWorkers != null) {
            manageWorkers.setOnClickListener(v -> {

                Intent intent = new Intent(
                        AdminDashboardActivity.this,
                        AdminManagementActivity.class
                );

                intent.putExtra("MODE", "WORKERS");
                startActivity(intent);
            });
        }


        View manageBookings = findViewById(R.id.btnManageBookings);

        if (manageBookings != null) {
            manageBookings.setOnClickListener(v -> {

                Intent intent = new Intent(
                        AdminDashboardActivity.this,
                        AdminManagementActivity.class
                );

                intent.putExtra("MODE", "BOOKINGS");
                startActivity(intent);
            });
        }


        View manageAreas = findViewById(R.id.btnManageAreas);

        if (manageAreas != null) {
            manageAreas.setOnClickListener(v -> {

                Intent intent = new Intent(
                        AdminDashboardActivity.this,
                        AdminManagementActivity.class
                );

                intent.putExtra("MODE", "AREAS");
                startActivity(intent);
            });
        }


        View viewAllBookings =
                findViewById(R.id.btnViewAllBookings);

        if (viewAllBookings != null) {
            viewAllBookings.setOnClickListener(v -> {

                Intent intent = new Intent(
                        AdminDashboardActivity.this,
                        AdminManagementActivity.class
                );

                intent.putExtra("MODE", "BOOKINGS");
                startActivity(intent);
            });
        }


        View viewPendingWorkers =
                findViewById(R.id.btnViewPendingWorkers);

        if (viewPendingWorkers != null) {
            viewPendingWorkers.setOnClickListener(v -> {

                Intent intent = new Intent(
                        AdminDashboardActivity.this,
                        AdminManagementActivity.class
                );

                intent.putExtra("MODE", "PENDING_WORKERS");
                startActivity(intent);
            });
        }


        View statistics =
                findViewById(R.id.btnStatistics);

        if (statistics != null) {
            statistics.setOnClickListener(v -> {

                Intent intent = new Intent(
                        AdminDashboardActivity.this,
                        AdminManagementActivity.class
                );

                intent.putExtra("MODE", "STATISTICS");
                startActivity(intent);
            });
        }
    }

    // ============================================================
    // DASHBOARD DATA
    // ============================================================

    private void loadDashboardData() {

        loadUsersCount();
        loadWorkers();
        loadBookings();
    }

    // ============================================================
    // USERS
    // ============================================================

    private void loadUsersCount() {

        db.collection("users")
                .get()
                .addOnSuccessListener(snapshot -> {

                    int totalUsers = snapshot.size();

                    tvTotalUsers.setText(
                            String.valueOf(totalUsers)
                    );
                })
                .addOnFailureListener(e ->
                        tvTotalUsers.setText("0")
                );
    }

    // ============================================================
    // WORKERS
    // ============================================================

    private void loadWorkers() {

        db.collection("workers")
                .get()
                .addOnSuccessListener(snapshot -> {

                    int totalWorkers = snapshot.size();

                    int pendingCount = 0;

                    pendingWorkers.clear();

                    for (DocumentSnapshot worker :
                            snapshot.getDocuments()) {

                        String status = getString(
                                worker,
                                "verificationStatus",
                                "pending"
                        );

                        if (status.equalsIgnoreCase("pending")) {

                            pendingCount++;

                            if (pendingWorkers.size() < 2) {
                                pendingWorkers.add(worker);
                            }
                        }
                    }

                    tvTotalWorkers.setText(
                            String.valueOf(totalWorkers)
                    );

                    tvPendingVerification.setText(
                            String.valueOf(pendingCount)
                    );

                    renderPendingWorkers();
                })
                .addOnFailureListener(e -> {

                    tvTotalWorkers.setText("0");
                    tvPendingVerification.setText("0");

                    pendingWorkers.clear();
                    renderPendingWorkers();
                });
    }

    // ============================================================
    // BOOKINGS
    // ============================================================

    private void loadBookings() {

        db.collection("bookings")
                .get()
                .addOnSuccessListener(snapshot -> {

                    int totalBookings = snapshot.size();

                    tvTotalBookings.setText(
                            String.valueOf(totalBookings)
                    );

                    calculateRevenue(snapshot);

                    loadRecentBookings(snapshot);
                })
                .addOnFailureListener(e -> {

                    tvTotalBookings.setText("0");
                    tvTotalRevenue.setText("Rs. 0");

                    recentActivityContainer.removeAllViews();

                    showEmptyRecentBookings();
                });
    }

    // ============================================================
    // REVENUE
    // ============================================================

    private void calculateRevenue(
            com.google.firebase.firestore.QuerySnapshot snapshot) {

        double totalRevenue = 0;

        for (DocumentSnapshot booking :
                snapshot.getDocuments()) {

            totalRevenue += extractAmount(
                    booking,
                    "workerRate"
            );
        }

        tvTotalRevenue.setText(
                "Rs. " + formatNumber(totalRevenue)
        );
    }

    // ============================================================
    // RECENT BOOKINGS
    // ============================================================

    private void loadRecentBookings(
            com.google.firebase.firestore.QuerySnapshot snapshot) {

        recentBookings.clear();

        List<DocumentSnapshot> allBookings =
                new ArrayList<>(snapshot.getDocuments());

        allBookings.sort((a, b) -> {

            Object aCreated = a.get("createdAt");
            Object bCreated = b.get("createdAt");

            if (aCreated == null && bCreated == null) {
                return 0;
            }

            if (aCreated == null) {
                return 1;
            }

            if (bCreated == null) {
                return -1;
            }

            return bCreated.toString()
                    .compareTo(aCreated.toString());
        });

        int limit = Math.min(
                3,
                allBookings.size()
        );

        for (int i = 0; i < limit; i++) {

            recentBookings.add(
                    allBookings.get(i)
            );
        }

        renderRecentBookings();
    }

    private void renderRecentBookings() {

        recentActivityContainer.removeAllViews();

        if (recentBookings.isEmpty()) {

            showEmptyRecentBookings();
            return;
        }

        for (DocumentSnapshot booking :
                recentBookings) {

            recentActivityContainer.addView(
                    createRecentBookingCard(booking)
            );
        }
    }

    // ============================================================
    // RECENT BOOKING CARD
    // ============================================================

    private View createRecentBookingCard(
            DocumentSnapshot booking) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(14),
                dp(14),
                dp(14),
                dp(14)
        );

        card.setBackground(
                createCardBackground()
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        cardParams.bottomMargin =
                dp(10);

        card.setLayoutParams(
                cardParams
        );


        // TOP ROW

        LinearLayout topRow =
                new LinearLayout(this);

        topRow.setGravity(
                Gravity.CENTER_VERTICAL
        );


        // BOOKING ICON

        TextView icon =
                createCircleIcon(
                        "B",
                        "#5D55C8"
                );

        topRow.addView(icon);


        // INFO

        LinearLayout info =
                new LinearLayout(this);

        info.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                );

        infoParams.leftMargin =
                dp(12);

        info.setLayoutParams(
                infoParams
        );


        String service =
                getString(
                        booking,
                        "serviceTitle",
                        getString(
                                booking,
                                "service",
                                "Home Service"
                        )
                );

        info.addView(
                createMainText(service)
        );


        String customer =
                getString(
                        booking,
                        "customerName",
                        getString(
                                booking,
                                "userName",
                                "Customer"
                        )
                );

        info.addView(
                createSmallText(customer)
        );

        topRow.addView(info);


        // STATUS

        String status =
                getString(
                        booking,
                        "status",
                        "pending"
                );

        topRow.addView(
                createStatusBadge(status)
        );

        card.addView(topRow);


        // DATE + TIME

        String date =
                getString(
                        booking,
                        "date",
                        ""
                );

        String time =
                getString(
                        booking,
                        "time",
                        ""
                );

        String dateTime = "";

        if (!date.isEmpty()) {

            dateTime = date;

            if (!time.isEmpty()) {
                dateTime += "  •  " + time;
            }
        }


        if (!dateTime.isEmpty()) {

            TextView dateText =
                    createSmallText(
                            "📅 " + dateTime
                    );

            // FIX:
            // Directly create LayoutParams instead of
            // calling getLayoutParams(), which was null.

            LinearLayout.LayoutParams dateParams =
                    new LinearLayout.LayoutParams(
                            -1,
                            -2
                    );

            dateParams.topMargin =
                    dp(10);

            dateText.setLayoutParams(
                    dateParams
            );

            card.addView(dateText);
        }

        return card;
    }

    // ============================================================
    // PENDING WORKERS
    // ============================================================

    private void renderPendingWorkers() {

        pendingWorkersContainer.removeAllViews();

        if (pendingWorkers.isEmpty()) {

            TextView empty =
                    createSmallText(
                            "No workers waiting for verification."
                    );

            empty.setPadding(
                    dp(5),
                    dp(15),
                    dp(5),
                    dp(15)
            );

            pendingWorkersContainer.addView(
                    empty
            );

            return;
        }

        for (DocumentSnapshot worker :
                pendingWorkers) {

            pendingWorkersContainer.addView(
                    createPendingWorkerCard(worker)
            );
        }
    }

    // ============================================================
    // PENDING WORKER CARD
    // ============================================================

    private View createPendingWorkerCard(
            DocumentSnapshot worker) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(14),
                dp(14),
                dp(14),
                dp(14)
        );

        card.setBackground(
                createCardBackground()
        );


        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.bottomMargin =
                dp(10);

        card.setLayoutParams(params);


        LinearLayout top =
                new LinearLayout(this);

        top.setGravity(
                Gravity.CENTER_VERTICAL
        );


        String workerName =
                getString(
                        worker,
                        "name",
                        "Worker"
                );


        TextView avatar =
                createCircleIcon(
                        getInitial(workerName),
                        "#159B87"
                );

        top.addView(avatar);


        LinearLayout info =
                new LinearLayout(this);

        info.setOrientation(
                LinearLayout.VERTICAL
        );


        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                );

        infoParams.leftMargin =
                dp(12);

        info.setLayoutParams(
                infoParams
        );


        info.addView(
                createMainText(workerName)
        );


        String profession =
                getString(
                        worker,
                        "profession",
                        getString(
                                worker,
                                "title",
                                "Service Worker"
                        )
                );

        info.addView(
                createSmallText(profession)
        );


        String area =
                getString(
                        worker,
                        "area",
                        ""
                );

        if (!area.isEmpty()) {

            info.addView(
                    createSmallText(
                            "📍 " + area
                    )
            );
        }


        top.addView(info);


        top.addView(
                createBadge(
                        "Pending",
                        "#FFF1D8",
                        "#B66A00"
                )
        );

        card.addView(top);


        // BUTTONS

        LinearLayout buttons =
                new LinearLayout(this);

        buttons.setOrientation(
                LinearLayout.HORIZONTAL
        );


        LinearLayout.LayoutParams buttonRowParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(42)
                );

        buttonRowParams.topMargin =
                dp(12);

        buttons.setLayoutParams(
                buttonRowParams
        );


        TextView reject =
                createActionButton(
                        "Reject",
                        "#FFF0F2",
                        "#BE3A48"
                );


        TextView verify =
                createActionButton(
                        "✓  Verify",
                        "#159B7E",
                        "#FFFFFF"
                );


        reject.setOnClickListener(
                v -> updateWorkerStatus(
                        worker,
                        "rejected"
                )
        );


        verify.setOnClickListener(
                v -> updateWorkerStatus(
                        worker,
                        "verified"
                )
        );


        LinearLayout.LayoutParams rejectParams =
                new LinearLayout.LayoutParams(
                        0,
                        -1,
                        1
                );

        reject.setLayoutParams(
                rejectParams
        );


        LinearLayout.LayoutParams verifyParams =
                new LinearLayout.LayoutParams(
                        0,
                        -1,
                        1
                );

        verifyParams.leftMargin =
                dp(8);

        verify.setLayoutParams(
                verifyParams
        );


        buttons.addView(reject);
        buttons.addView(verify);

        card.addView(buttons);

        return card;
    }

    // ============================================================
    // UPDATE WORKER STATUS
    // ============================================================

    private void updateWorkerStatus(
            DocumentSnapshot worker,
            String newStatus) {

        db.collection("workers")
                .document(worker.getId())
                .update(
                        "verificationStatus",
                        newStatus
                )
                .addOnSuccessListener(unused -> {

                    if (newStatus.equals("verified")) {

                        Toast.makeText(
                                this,
                                "Worker verified successfully",
                                Toast.LENGTH_SHORT
                        ).show();

                    } else {

                        Toast.makeText(
                                this,
                                "Worker rejected",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                    loadDashboardData();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Could not update worker",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // ============================================================
    // EMPTY BOOKINGS
    // ============================================================

    private void showEmptyRecentBookings() {

        TextView empty =
                createSmallText(
                        "No recent bookings available."
                );

        empty.setGravity(
                Gravity.CENTER
        );

        empty.setPadding(
                dp(10),
                dp(20),
                dp(10),
                dp(20)
        );

        recentActivityContainer.addView(
                empty
        );
    }

    // ============================================================
    // TEXT HELPERS
    // ============================================================

    private TextView createMainText(
            String text) {

        TextView tv =
                new TextView(this);

        tv.setText(text);

        tv.setTextColor(
                Color.rgb(
                        38,
                        36,
                        55
                )
        );

        tv.setTextSize(14);

        tv.setTypeface(
                null,
                Typeface.BOLD
        );

        tv.setMaxLines(2);

        return tv;
    }


    private TextView createSmallText(
            String text) {

        TextView tv =
                new TextView(this);

        tv.setText(text);

        tv.setTextColor(
                Color.rgb(
                        120,
                        117,
                        139
                )
        );

        tv.setTextSize(11);

        return tv;
    }


    private TextView createCircleIcon(
            String text,
            String color) {

        TextView tv =
                new TextView(this);

        tv.setText(text);

        tv.setTextColor(
                Color.WHITE
        );

        tv.setTextSize(15);

        tv.setTypeface(
                null,
                Typeface.BOLD
        );

        tv.setGravity(
                Gravity.CENTER
        );

        tv.setBackground(
                roundedBackground(
                        Color.parseColor(color),
                        Color.TRANSPARENT,
                        50
                )
        );

        tv.setLayoutParams(
                new LinearLayout.LayoutParams(
                        dp(45),
                        dp(45)
                )
        );

        return tv;
    }

    // ============================================================
    // STATUS BADGE
    // ============================================================

    private TextView createStatusBadge(
            String status) {

        String background;
        String textColor;


        if (status.equalsIgnoreCase("completed")) {

            background = "#E0F6EA";
            textColor = "#168050";

        } else if (status.equalsIgnoreCase("accepted")) {

            background = "#E1F2FF";
            textColor = "#2872A7";

        } else if (status.equalsIgnoreCase("rejected")) {

            background = "#FFE7EA";
            textColor = "#BE3A48";

        } else {

            background = "#FFF1D8";
            textColor = "#B66A00";
        }


        return createBadge(
                formatText(status),
                background,
                textColor
        );
    }


    private TextView createBadge(
            String text,
            String background,
            String textColor) {

        TextView tv =
                new TextView(this);

        tv.setText(text);

        tv.setTextColor(
                Color.parseColor(textColor)
        );

        tv.setTextSize(9);

        tv.setTypeface(
                null,
                Typeface.BOLD
        );

        tv.setGravity(
                Gravity.CENTER
        );

        tv.setPadding(
                dp(9),
                dp(5),
                dp(9),
                dp(5)
        );

        tv.setBackground(
                roundedBackground(
                        Color.parseColor(background),
                        Color.TRANSPARENT,
                        20
                )
        );

        return tv;
    }


    private TextView createActionButton(
            String text,
            String background,
            String textColor) {

        TextView button =
                new TextView(this);

        button.setText(text);

        button.setTextColor(
                Color.parseColor(textColor)
        );

        button.setTextSize(11);

        button.setTypeface(
                null,
                Typeface.BOLD
        );

        button.setGravity(
                Gravity.CENTER
        );

        button.setBackground(
                roundedBackground(
                        Color.parseColor(background),
                        Color.TRANSPARENT,
                        12
                )
        );

        return button;
    }

    // ============================================================
    // DRAWABLE HELPERS
    // ============================================================

    private GradientDrawable createCardBackground() {

        return roundedBackground(
                Color.WHITE,
                Color.rgb(
                        225,
                        223,
                        237
                ),
                17
        );
    }


    private GradientDrawable roundedBackground(
            int fill,
            int stroke,
            int radius) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(fill);

        drawable.setCornerRadius(
                dp(radius)
        );

        if (stroke != Color.TRANSPARENT) {

            drawable.setStroke(
                    dp(1),
                    stroke
            );
        }

        return drawable;
    }

    // ============================================================
    // FIRESTORE HELPERS
    // ============================================================

    private String getString(
            DocumentSnapshot document,
            String field,
            String fallback) {

        String value =
                document.getString(field);

        if (value == null ||
                value.trim().isEmpty()) {

            return fallback;
        }

        return value;
    }


    private double extractAmount(
            DocumentSnapshot document,
            String field) {

        Object value =
                document.get(field);

        if (value == null) {
            return 0;
        }


        if (value instanceof Number) {

            return ((Number) value)
                    .doubleValue();
        }


        String text =
                value.toString()
                        .replaceAll(
                                "[^0-9.]",
                                ""
                        );


        if (text.isEmpty()) {
            return 0;
        }


        try {

            return Double.parseDouble(text);

        } catch (Exception e) {

            return 0;
        }
    }


    private String formatNumber(
            double number) {

        if (number == (long) number) {

            return String.format(
                    Locale.getDefault(),
                    "%d",
                    (long) number
            );
        }


        return String.format(
                Locale.getDefault(),
                "%.2f",
                number
        );
    }


    private String getInitial(
            String name) {

        if (name == null ||
                name.trim().isEmpty()) {

            return "W";
        }


        return name.trim()
                .substring(0, 1)
                .toUpperCase(
                        Locale.getDefault()
                );
    }


    private String formatText(
            String text) {

        if (text == null ||
                text.trim().isEmpty()) {

            return "";
        }


        String clean =
                text.trim();


        return clean.substring(0, 1)
                .toUpperCase(
                        Locale.getDefault()
                )
                + clean.substring(1);
    }


    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int) (
                value * density + 0.5f
        );
    }
}