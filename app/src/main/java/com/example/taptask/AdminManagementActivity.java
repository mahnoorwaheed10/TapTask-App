package com.example.taptask;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminManagementActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private LinearLayout container;
    private EditText searchBox;
    private TextView title;
    private TextView subtitle;

    private String mode = "USERS";

    private final List<DocumentSnapshot> items = new ArrayList<>();

    private ListenerRegistration usersListener;
    private ListenerRegistration workersListener;
    private ListenerRegistration bookingsListener;

    // =========================================================
    // STATISTICS REAL-TIME DATA
    // =========================================================

    private QuerySnapshot statisticsUsersSnapshot;
    private QuerySnapshot statisticsWorkersSnapshot;
    private QuerySnapshot statisticsBookingsSnapshot;

    private final List<ListenerRegistration> statisticsListeners =
            new ArrayList<>();

    private BarChartView usersWorkersChart;
    private AreaChartView bookingsTrendChart;
    private AreaChartView revenueTrendChart;
    private DonutChartView bookingStatusChart;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_management);

        db = FirebaseFirestore.getInstance();

        String receivedMode =
                getIntent().getStringExtra("MODE");

        if (receivedMode != null) {
            mode = receivedMode;
        }

        container =
                findViewById(R.id.managementContainer);

        searchBox =
                findViewById(R.id.etManagementSearch);

        title =
                findViewById(R.id.tvManagementTitle);

        subtitle =
                findViewById(R.id.tvManagementSubtitle);

        View back =
                findViewById(R.id.btnManagementBack);

        if (back != null) {
            back.setOnClickListener(v -> finish());
        }

        setupScreen();
        setupSearch();
        loadData();
    }

    // =========================================================
    // SCREEN
    // =========================================================

    private void setupScreen() {

        searchBox.setVisibility(View.VISIBLE);

        if (mode.equals("USERS")) {

            title.setText("Manage Users");
            subtitle.setText(
                    "View and manage registered users"
            );

            searchBox.setHint(
                    "Search by name or email"
            );

        } else if (mode.equals("WORKERS")) {

            title.setText("Manage Workers");
            subtitle.setText(
                    "View and manage service workers"
            );

            searchBox.setHint(
                    "Search workers"
            );

        } else if (mode.equals("PENDING_WORKERS")) {

            title.setText(
                    "Pending Verification"
            );

            subtitle.setText(
                    "Workers waiting for admin approval"
            );

            searchBox.setHint(
                    "Search pending workers"
            );

        } else if (mode.equals("BOOKINGS")) {

            title.setText("All Bookings");
            subtitle.setText(
                    "View all TapTask bookings"
            );

            searchBox.setHint(
                    "Search bookings"
            );

        } else if (mode.equals("AREAS")) {

            title.setText("Manage Areas");
            subtitle.setText(
                    "TapTask service coverage"
            );

            searchBox.setVisibility(
                    View.GONE
            );

        } else if (mode.equals("STATISTICS")) {

            title.setText("Statistics");
            subtitle.setText(
                    "Live TapTask platform analytics"
            );

            searchBox.setVisibility(
                    View.GONE
            );
        }
    }

    // =========================================================
    // SEARCH
    // =========================================================

    private void setupSearch() {

        searchBox.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        filterData(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    // =========================================================
    // LOAD DATA
    // =========================================================

    private void loadData() {

        if (mode.equals("USERS")) {

            listenUsers();

        } else if (
                mode.equals("WORKERS")
                        ||
                        mode.equals("PENDING_WORKERS")) {

            listenWorkers();

        } else if (mode.equals("BOOKINGS")) {

            listenBookings();

        } else if (mode.equals("AREAS")) {

            showAreas();

        } else if (mode.equals("STATISTICS")) {

            showStatistics();
        }
    }

    // =========================================================
    // USERS
    // =========================================================

    private void listenUsers() {

        usersListener =
                db.collection("users")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (
                                            error != null
                                                    ||
                                                    snapshot == null
                                    ) {

                                        showError();
                                        return;
                                    }

                                    items.clear();

                                    items.addAll(
                                            snapshot.getDocuments()
                                    );

                                    filterData(
                                            searchBox
                                                    .getText()
                                                    .toString()
                                    );
                                }
                        );
    }

    // =========================================================
    // WORKERS
    // =========================================================

    private void listenWorkers() {

        workersListener =
                db.collection("workers")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (
                                            error != null
                                                    ||
                                                    snapshot == null
                                    ) {

                                        showError();
                                        return;
                                    }

                                    items.clear();

                                    for (
                                            DocumentSnapshot worker :
                                            snapshot.getDocuments()
                                    ) {

                                        String status =
                                                getString(
                                                        worker,
                                                        "verificationStatus",
                                                        "pending"
                                                );

                                        if (
                                                mode.equals(
                                                        "WORKERS"
                                                )
                                        ) {

                                            items.add(worker);

                                        } else if (
                                                mode.equals(
                                                        "PENDING_WORKERS"
                                                )
                                                        &&
                                                        status.equalsIgnoreCase(
                                                                "pending"
                                                        )
                                        ) {

                                            items.add(worker);
                                        }
                                    }

                                    filterData(
                                            searchBox
                                                    .getText()
                                                    .toString()
                                    );
                                }
                        );
    }

    // =========================================================
    // BOOKINGS
    // =========================================================

    private void listenBookings() {

        bookingsListener =
                db.collection("bookings")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (
                                            error != null
                                                    ||
                                                    snapshot == null
                                    ) {

                                        showError();
                                        return;
                                    }

                                    items.clear();

                                    items.addAll(
                                            snapshot.getDocuments()
                                    );

                                    filterData(
                                            searchBox
                                                    .getText()
                                                    .toString()
                                    );
                                }
                        );
    }

    // =========================================================
    // FILTER
    // =========================================================

    private void filterData(String query) {

        String q =
                query
                        .toLowerCase(
                                Locale.getDefault()
                        )
                        .trim();

        if (mode.equals("USERS")) {

            List<DocumentSnapshot> filtered =
                    new ArrayList<>();

            for (
                    DocumentSnapshot user :
                    items
            ) {

                String name =
                        getString(
                                user,
                                "name",
                                ""
                        );

                String email =
                        getString(
                                user,
                                "email",
                                ""
                        );

                if (
                        name
                                .toLowerCase(
                                        Locale.getDefault()
                                )
                                .contains(q)
                                ||
                                email
                                        .toLowerCase(
                                                Locale.getDefault()
                                        )
                                        .contains(q)
                ) {

                    filtered.add(user);
                }
            }

            showUsers(filtered);

        } else if (
                mode.equals("WORKERS")
                        ||
                        mode.equals("PENDING_WORKERS")
        ) {

            List<DocumentSnapshot> filtered =
                    new ArrayList<>();

            for (
                    DocumentSnapshot worker :
                    items
            ) {

                String name =
                        getString(
                                worker,
                                "name",
                                ""
                        );

                String profession =
                        getString(
                                worker,
                                "profession",
                                getString(
                                        worker,
                                        "title",
                                        ""
                                )
                        );

                String area =
                        getString(
                                worker,
                                "area",
                                ""
                        );

                if (
                        name
                                .toLowerCase(
                                        Locale.getDefault()
                                )
                                .contains(q)
                                ||
                                profession
                                        .toLowerCase(
                                                Locale.getDefault()
                                        )
                                        .contains(q)
                                ||
                                area
                                        .toLowerCase(
                                                Locale.getDefault()
                                        )
                                        .contains(q)
                ) {

                    filtered.add(worker);
                }
            }

            showWorkers(filtered);

        } else if (mode.equals("BOOKINGS")) {

            List<DocumentSnapshot> filtered =
                    new ArrayList<>();

            for (
                    DocumentSnapshot booking :
                    items
            ) {

                String service =
                        getString(
                                booking,
                                "serviceTitle",
                                getString(
                                        booking,
                                        "service",
                                        ""
                                )
                        );

                String worker =
                        getString(
                                booking,
                                "workerName",
                                ""
                        );

                String customer =
                        getString(
                                booking,
                                "customerName",
                                ""
                        );

                if (
                        service
                                .toLowerCase(
                                        Locale.getDefault()
                                )
                                .contains(q)
                                ||
                                worker
                                        .toLowerCase(
                                                Locale.getDefault()
                                        )
                                        .contains(q)
                                ||
                                customer
                                        .toLowerCase(
                                                Locale.getDefault()
                                        )
                                        .contains(q)
                ) {

                    filtered.add(booking);
                }
            }

            showBookings(filtered);
        }
    }

    // =========================================================
    // USERS UI
    // =========================================================

    private void showUsers(
            List<DocumentSnapshot> users) {

        container.removeAllViews();

        if (users.isEmpty()) {

            empty("No users found");
            return;
        }

        for (
                DocumentSnapshot user :
                users
        ) {

            container.addView(
                    userCard(user)
            );
        }
    }

    private View userCard(
            DocumentSnapshot user) {

        LinearLayout card =
                horizontalCard();

        String name =
                getString(
                        user,
                        "name",
                        "Unnamed User"
                );

        String email =
                getString(
                        user,
                        "email",
                        "No email available"
                );

        String role =
                getString(
                        user,
                        "role",
                        "customer"
                );

        card.addView(
                avatar(
                        getInitial(name),
                        "#6658D9"
                )
        );

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
                dp(13);

        info.setLayoutParams(infoParams);

        info.addView(
                mainText(name)
        );

        info.addView(
                smallText(email)
        );

        boolean isWorker =
                role.equalsIgnoreCase("worker");

        TextView roleBadge =
                badge(
                        isWorker
                                ? "Worker"
                                : "Customer",
                        isWorker
                                ? "#E0F6F1"
                                : "#EAE7FF",
                        isWorker
                                ? "#087E70"
                                : "#5144B7"
                );

        LinearLayout.LayoutParams badgeParams =
                new LinearLayout.LayoutParams(
                        -2,
                        -2
                );

        badgeParams.topMargin =
                dp(7);

        roleBadge.setLayoutParams(
                badgeParams
        );

        info.addView(roleBadge);

        card.addView(info);

        return card;
    }

    // =========================================================
    // WORKERS UI
    // =========================================================

    private void showWorkers(
            List<DocumentSnapshot> workers) {

        container.removeAllViews();

        if (workers.isEmpty()) {

            if (
                    mode.equals(
                            "PENDING_WORKERS"
                    )
            ) {

                empty(
                        "No pending workers"
                );

            } else {

                empty(
                        "No workers found"
                );
            }

            return;
        }

        for (
                DocumentSnapshot worker :
                workers
        ) {

            container.addView(
                    workerCard(worker)
            );
        }
    }

    private View workerCard(
            DocumentSnapshot worker) {

        LinearLayout card =
                verticalCard();

        String name =
                getString(
                        worker,
                        "name",
                        "Worker"
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

        String area =
                getString(
                        worker,
                        "area",
                        "Area not specified"
                );

        String experience =
                getString(
                        worker,
                        "experience",
                        "N/A"
                );

        String rate =
                getString(
                        worker,
                        "rate",
                        getString(
                                worker,
                                "workerRate",
                                "0"
                        )
                );

        String rating =
                getString(
                        worker,
                        "rating",
                        "0.0"
                );

        String status =
                getString(
                        worker,
                        "verificationStatus",
                        "pending"
                );

        LinearLayout top =
                new LinearLayout(this);

        top.setGravity(
                Gravity.CENTER_VERTICAL
        );

        top.addView(
                avatar(
                        getInitial(name),
                        "#159B87"
                )
        );

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
                mainText(name)
        );

        info.addView(
                smallText(profession)
        );

        info.addView(
                smallText(
                        "Location  •  " + area
                )
        );

        top.addView(info);

        String bg;
        String fg;

        if (
                status.equalsIgnoreCase(
                        "verified"
                )
        ) {

            bg = "#E0F6EA";
            fg = "#168050";

        } else if (
                status.equalsIgnoreCase(
                        "rejected"
                )
        ) {

            bg = "#FFE7EA";
            fg = "#BE3A48";

        } else {

            bg = "#FFF1D8";
            fg = "#B66A00";
        }

        top.addView(
                badge(
                        formatText(status),
                        bg,
                        fg
                )
        );

        card.addView(top);

        LinearLayout details =
                new LinearLayout(this);

        details.setOrientation(
                LinearLayout.HORIZONTAL
        );

        details.setPadding(
                0,
                dp(12),
                0,
                0
        );

        details.addView(
                detailBox(
                        "Experience",
                        experience
                )
        );

        details.addView(
                detailBox(
                        "Rate",
                        "Rs. " + rate
                )
        );

        details.addView(
                detailBox(
                        "Rating",
                        "★ " + rating
                )
        );

        card.addView(details);

        if (
                status.equalsIgnoreCase(
                        "pending"
                )
        ) {

            LinearLayout buttons =
                    new LinearLayout(this);

            buttons.setOrientation(
                    LinearLayout.HORIZONTAL
            );

            LinearLayout.LayoutParams row =
                    new LinearLayout.LayoutParams(
                            -1,
                            dp(42)
                    );

            row.topMargin =
                    dp(12);

            buttons.setLayoutParams(row);

            TextView reject =
                    actionButton(
                            "Reject",
                            "#FFF0F2",
                            "#BE3A48"
                    );

            TextView verify =
                    actionButton(
                            "✓  Verify",
                            "#159B7E",
                            "#FFFFFF"
                    );

            reject.setOnClickListener(
                    v ->
                            updateWorker(
                                    worker,
                                    "rejected"
                            )
            );

            verify.setOnClickListener(
                    v ->
                            updateWorker(
                                    worker,
                                    "verified"
                            )
            );

            LinearLayout.LayoutParams p1 =
                    new LinearLayout.LayoutParams(
                            0,
                            -1,
                            1
                    );

            reject.setLayoutParams(p1);

            LinearLayout.LayoutParams p2 =
                    new LinearLayout.LayoutParams(
                            0,
                            -1,
                            1
                    );

            p2.leftMargin =
                    dp(8);

            verify.setLayoutParams(p2);

            buttons.addView(reject);
            buttons.addView(verify);

            card.addView(buttons);
        }

        return card;
    }

    // =========================================================
    // VERIFY / REJECT
    // =========================================================

    private void updateWorker(
            DocumentSnapshot worker,
            String status) {

        db.collection("workers")
                .document(worker.getId())
                .update(
                        "verificationStatus",
                        status
                )
                .addOnSuccessListener(v -> {

                    Toast.makeText(
                            this,
                            status.equals("verified")
                                    ? "Worker verified successfully"
                                    : "Worker rejected",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Update failed",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // =========================================================
    // BOOKINGS UI
    // =========================================================

    private void showBookings(
            List<DocumentSnapshot> bookings) {

        container.removeAllViews();

        if (bookings.isEmpty()) {

            empty("No bookings found");
            return;
        }

        for (
                DocumentSnapshot booking :
                bookings
        ) {

            container.addView(
                    bookingCard(booking)
            );
        }
    }

    private View bookingCard(
            DocumentSnapshot booking) {

        LinearLayout card =
                verticalCard();

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

        String worker =
                getString(
                        booking,
                        "workerName",
                        "Worker"
                );

        String date =
                getString(
                        booking,
                        "date",
                        "Date unavailable"
                );

        String time =
                getString(
                        booking,
                        "time",
                        ""
                );

        String address =
                getString(
                        booking,
                        "address",
                        "Address unavailable"
                );

        String payment =
                getString(
                        booking,
                        "paymentMethod",
                        "Not specified"
                );

        String status =
                getString(
                        booking,
                        "status",
                        "pending"
                );

        LinearLayout top =
                new LinearLayout(this);

        top.setGravity(
                Gravity.CENTER_VERTICAL
        );

        top.addView(
                avatar(
                        "B",
                        "#5966C9"
                )
        );

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

        info.setLayoutParams(infoParams);

        info.addView(
                mainText(service)
        );

        info.addView(
                smallText(
                        "Customer: " + customer
                )
        );

        top.addView(info);

        top.addView(
                bookingStatus(status)
        );

        card.addView(top);

        card.addView(
                smallText(
                        "Worker: " + worker
                )
        );

        card.addView(
                smallText(
                        "Date  •  " + date +
                                (
                                        time.isEmpty()
                                                ? ""
                                                : "  •  " + time
                                )
                )
        );

        card.addView(
                smallText(
                        "Location  •  " + address
                )
        );

        card.addView(
                smallText(
                        "Payment  •  " + payment
                )
        );

        return card;
    }

    private TextView bookingStatus(
            String status) {

        if (
                status.equalsIgnoreCase(
                        "completed"
                )
        ) {

            return badge(
                    "Completed",
                    "#E0F6EA",
                    "#168050"
            );

        } else if (
                status.equalsIgnoreCase(
                        "accepted"
                )
        ) {

            return badge(
                    "Accepted",
                    "#E1F2FF",
                    "#2872A7"
            );

        } else if (
                status.equalsIgnoreCase(
                        "rejected"
                )
        ) {

            return badge(
                    "Rejected",
                    "#FFE7EA",
                    "#BE3A48"
            );

        } else {

            return badge(
                    "Pending",
                    "#FFF1D8",
                    "#B66A00"
            );
        }
    }

    // =========================================================
    // AREAS
    // =========================================================

    private void showAreas() {

        container.removeAllViews();

        LinearLayout section =
                new LinearLayout(this);

        section.setOrientation(
                LinearLayout.VERTICAL
        );

        section.setPadding(
                dp(2),
                dp(3),
                dp(2),
                dp(8)
        );

        TextView heading =
                mainText(
                        "Active Service Area"
                );

        heading.setTextSize(16);

        section.addView(heading);

        section.addView(
                smallText(
                        "Currently available for TapTask services"
                )
        );

        container.addView(section);

        LinearLayout card =
                horizontalCard();

        card.addView(
                avatar(
                        "⌖",
                        "#6658D9"
                )
        );

        LinearLayout areaInfo =
                new LinearLayout(this);

        areaInfo.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams areaParams =
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                );

        areaParams.leftMargin =
                dp(13);

        areaInfo.setLayoutParams(
                areaParams
        );

        areaInfo.addView(
                mainText(
                        "Tench Saddar"
                )
        );

        areaInfo.addView(
                smallText(
                        "Active TapTask service area"
                )
        );

        card.addView(areaInfo);

        card.addView(
                badge(
                        "Active",
                        "#E0F6EA",
                        "#168050"
                )
        );

        container.addView(card);
    }

    // =========================================================
    // STATISTICS
    // =========================================================

    private void showStatistics() {

        container.removeAllViews();

        addStatisticsIntro();

        // -----------------------------------------------------
        // USERS / WORKERS BAR GRAPH
        // -----------------------------------------------------

        LinearLayout comparisonCard =
                chartCard(
                        "Users & Workers",
                        "Live platform population comparison",
                        "▥"
                );

        usersWorkersChart =
                new BarChartView(this);

        LinearLayout.LayoutParams comparisonParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(255)
                );

        comparisonParams.topMargin =
                dp(14);

        usersWorkersChart.setLayoutParams(
                comparisonParams
        );

        comparisonCard.addView(
                usersWorkersChart
        );

        container.addView(comparisonCard);

        // -----------------------------------------------------
        // BOOKINGS AREA GRAPH
        // -----------------------------------------------------

        LinearLayout bookingCard =
                chartCard(
                        "Booking Trend",
                        "Bookings grouped by date",
                        "⌁"
                );

        bookingsTrendChart =
                new AreaChartView(
                        this,
                        "#5966C9"
                );

        LinearLayout.LayoutParams bookingParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(255)
                );

        bookingParams.topMargin =
                dp(14);

        bookingsTrendChart.setLayoutParams(
                bookingParams
        );

        bookingCard.addView(
                bookingsTrendChart
        );

        container.addView(bookingCard);

        // -----------------------------------------------------
        // BOOKING STATUS DONUT
        // -----------------------------------------------------

        LinearLayout statusCard =
                chartCard(
                        "Booking Status",
                        "Current distribution of bookings",
                        "◔"
                );

        bookingStatusChart =
                new DonutChartView(this);

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(290)
                );

        statusParams.topMargin =
                dp(12);

        bookingStatusChart.setLayoutParams(
                statusParams
        );

        statusCard.addView(
                bookingStatusChart
        );

        container.addView(statusCard);

        // -----------------------------------------------------
        // REVENUE AREA GRAPH
        // -----------------------------------------------------

        LinearLayout revenueCard =
                chartCard(
                        "Revenue Trend",
                        "Revenue generated from booking data",
                        "₨"
                );

        revenueTrendChart =
                new AreaChartView(
                        this,
                        "#A95576"
                );

        LinearLayout.LayoutParams revenueParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(255)
                );

        revenueParams.topMargin =
                dp(14);

        revenueTrendChart.setLayoutParams(
                revenueParams
        );

        revenueCard.addView(
                revenueTrendChart
        );

        container.addView(revenueCard);

        // IMPORTANT:
        // Firebase listeners are attached after all chart
        // views have been created.
        listenStatistics();
    }

    private void addStatisticsIntro() {

        LinearLayout intro =
                new LinearLayout(this);

        intro.setOrientation(
                LinearLayout.VERTICAL
        );

        intro.setPadding(
                dp(2),
                dp(3),
                dp(2),
                dp(15)
        );

        TextView heading =
                mainText(
                        "Platform Analytics"
                );

        heading.setTextSize(19);

        intro.addView(heading);

        TextView description =
                smallText(
                        "Live graphs based on your Firebase data"
                );

        description.setTextSize(12);

        intro.addView(description);

        container.addView(intro);
    }

    private LinearLayout chartCard(
            String heading,
            String description,
            String iconText) {

        LinearLayout card =
                verticalCard();

        LinearLayout header =
                new LinearLayout(this);

        header.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView icon =
                avatar(
                        iconText,
                        "#6658D9"
                );

        icon.setTextSize(17);

        // Subtle neon-style glow.
        icon.setLayerType(
                View.LAYER_TYPE_SOFTWARE,
                null
        );

        icon.setShadowLayer(
                dp(7),
                0,
                0,
                Color.parseColor("#8878FF")
        );

        header.addView(icon);

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

        info.setLayoutParams(infoParams);

        TextView titleView =
                mainText(heading);

        titleView.setTextSize(15);

        info.addView(titleView);

        TextView descriptionView =
                smallText(description);

        descriptionView.setTextSize(10);

        info.addView(descriptionView);

        header.addView(info);

        card.addView(header);

        return card;
    }

    // =========================================================
    // REAL-TIME STATISTICS
    // =========================================================

    private void listenStatistics() {

        clearStatisticsListeners();

        ListenerRegistration userRegistration =
                db.collection("users")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (
                                            error == null
                                                    &&
                                                    snapshot != null
                                    ) {

                                        statisticsUsersSnapshot =
                                                snapshot;

                                        refreshStatisticsCharts();
                                    }
                                }
                        );

        statisticsListeners.add(
                userRegistration
        );

        ListenerRegistration workerRegistration =
                db.collection("workers")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (
                                            error == null
                                                    &&
                                                    snapshot != null
                                    ) {

                                        statisticsWorkersSnapshot =
                                                snapshot;

                                        refreshStatisticsCharts();
                                    }
                                }
                        );

        statisticsListeners.add(
                workerRegistration
        );

        ListenerRegistration bookingRegistration =
                db.collection("bookings")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (
                                            error == null
                                                    &&
                                                    snapshot != null
                                    ) {

                                        statisticsBookingsSnapshot =
                                                snapshot;

                                        refreshStatisticsCharts();
                                    }
                                }
                        );

        statisticsListeners.add(
                bookingRegistration
        );
    }

    private void refreshStatisticsCharts() {

        if (isFinishing()) {
            return;
        }

        // -----------------------------------------------------
        // USERS / WORKERS
        // -----------------------------------------------------

        int users =
                statisticsUsersSnapshot == null
                        ? 0
                        : statisticsUsersSnapshot.size();

        int workers =
                statisticsWorkersSnapshot == null
                        ? 0
                        : statisticsWorkersSnapshot.size();

        int pending =
                0;

        if (statisticsWorkersSnapshot != null) {

            for (
                    DocumentSnapshot worker :
                    statisticsWorkersSnapshot.getDocuments()
            ) {

                String status =
                        getString(
                                worker,
                                "verificationStatus",
                                "pending"
                        );

                if (
                        status.equalsIgnoreCase(
                                "pending"
                        )
                ) {

                    pending++;
                }
            }
        }

        if (usersWorkersChart != null) {

            usersWorkersChart.setValues(
                    new double[]{
                            users,
                            workers,
                            pending
                    },
                    new String[]{
                            "Users",
                            "Workers",
                            "Pending"
                    }
            );
        }

        // -----------------------------------------------------
        // BOOKINGS
        // -----------------------------------------------------

        if (statisticsBookingsSnapshot != null) {

            updateBookingCharts(
                    statisticsBookingsSnapshot
            );
        } else {

            if (bookingsTrendChart != null) {

                bookingsTrendChart.setData(
                        new ArrayList<>(),
                        new ArrayList<>()
                );
            }

            if (revenueTrendChart != null) {

                revenueTrendChart.setData(
                        new ArrayList<>(),
                        new ArrayList<>()
                );
            }

            if (bookingStatusChart != null) {

                bookingStatusChart.setData(
                        new double[]{
                                0,
                                0,
                                0,
                                0
                        },
                        new String[]{
                                "Pending",
                                "Accepted",
                                "Completed",
                                "Rejected"
                        }
                );
            }
        }
    }

    private void updateBookingCharts(
            QuerySnapshot snapshot) {

        Map<String, Integer> bookingCounts =
                createLastSevenDayIntegerMap();

        Map<String, Double> revenueCounts =
                createLastSevenDayDoubleMap();

        int pending = 0;
        int accepted = 0;
        int completed = 0;
        int rejected = 0;

        boolean hasDateData = false;

        for (
                DocumentSnapshot booking :
                snapshot.getDocuments()
        ) {

            String status =
                    getString(
                            booking,
                            "status",
                            "pending"
                    );

            if (
                    status.equalsIgnoreCase(
                            "accepted"
                    )
            ) {

                accepted++;

            } else if (
                    status.equalsIgnoreCase(
                            "completed"
                    )
            ) {

                completed++;

            } else if (
                    status.equalsIgnoreCase(
                            "rejected"
                    )
                            ||
                            status.equalsIgnoreCase(
                                    "cancelled"
                            )
            ) {

                rejected++;

            } else {

                pending++;
            }

            Date bookingDate =
                    getBookingDate(booking);

            if (bookingDate != null) {

                String key =
                        dateKey(bookingDate);

                if (bookingCounts.containsKey(key)) {

                    bookingCounts.put(
                            key,
                            bookingCounts.get(key) + 1
                    );

                    double amount =
                            extractBookingAmount(
                                    booking
                            );

                    revenueCounts.put(
                            key,
                            revenueCounts.get(key) + amount
                    );

                    hasDateData = true;
                }
            }
        }

        List<String> labels =
                new ArrayList<>(
                        bookingCounts.keySet()
                );

        List<Double> bookingValues =
                new ArrayList<>();

        List<Double> revenueValues =
                new ArrayList<>();

        for (String key : labels) {

            bookingValues.add(
                    bookingCounts.get(key)
                            .doubleValue()
            );

            revenueValues.add(
                    revenueCounts.get(key)
            );
        }

        if (bookingsTrendChart != null) {

            if (hasDateData) {

                bookingsTrendChart.setData(
                        bookingValues,
                        createShortLabels(labels)
                );

            } else {

                bookingsTrendChart.setNoData(
                        "No usable booking dates"
                );
            }
        }

        if (revenueTrendChart != null) {

            if (hasDateData) {

                revenueTrendChart.setData(
                        revenueValues,
                        createShortLabels(labels)
                );

            } else {

                revenueTrendChart.setNoData(
                        "No usable booking dates"
                );
            }
        }

        if (bookingStatusChart != null) {

            bookingStatusChart.setData(
                    new double[]{
                            pending,
                            accepted,
                            completed,
                            rejected
                    },
                    new String[]{
                            "Pending",
                            "Accepted",
                            "Completed",
                            "Rejected"
                    }
            );
        }
    }

    // =========================================================
    // DATE HELPERS
    // =========================================================

    private Map<String, Integer>
    createLastSevenDayIntegerMap() {

        Map<String, Integer> map =
                new LinkedHashMap<>();

        Calendar calendar =
                Calendar.getInstance();

        calendar.set(
                Calendar.HOUR_OF_DAY,
                0
        );

        calendar.set(
                Calendar.MINUTE,
                0
        );

        calendar.set(
                Calendar.SECOND,
                0
        );

        calendar.set(
                Calendar.MILLISECOND,
                0
        );

        calendar.add(
                Calendar.DAY_OF_YEAR,
                -6
        );

        for (int i = 0; i < 7; i++) {

            map.put(
                    dateKey(calendar.getTime()),
                    0
            );

            calendar.add(
                    Calendar.DAY_OF_YEAR,
                    1
            );
        }

        return map;
    }

    private Map<String, Double>
    createLastSevenDayDoubleMap() {

        Map<String, Double> map =
                new LinkedHashMap<>();

        Calendar calendar =
                Calendar.getInstance();

        calendar.set(
                Calendar.HOUR_OF_DAY,
                0
        );

        calendar.set(
                Calendar.MINUTE,
                0
        );

        calendar.set(
                Calendar.SECOND,
                0
        );

        calendar.set(
                Calendar.MILLISECOND,
                0
        );

        calendar.add(
                Calendar.DAY_OF_YEAR,
                -6
        );

        for (int i = 0; i < 7; i++) {

            map.put(
                    dateKey(calendar.getTime()),
                    0.0
            );

            calendar.add(
                    Calendar.DAY_OF_YEAR,
                    1
            );
        }

        return map;
    }

    private String dateKey(Date date) {

        return new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(date);
    }

    private List<String> createShortLabels(
            List<String> keys) {

        List<String> labels =
                new ArrayList<>();

        SimpleDateFormat input =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                );

        SimpleDateFormat output =
                new SimpleDateFormat(
                        "dd MMM",
                        Locale.getDefault()
                );

        for (String key : keys) {

            try {

                Date date =
                        input.parse(key);

                if (date != null) {

                    labels.add(
                            output.format(date)
                    );

                } else {

                    labels.add(key);
                }

            } catch (Exception e) {

                labels.add(key);
            }
        }

        return labels;
    }

    private Date getBookingDate(
            DocumentSnapshot booking) {

        Object value =
                booking.get("date");

        Date date =
                convertToDate(value);

        if (date != null) {
            return date;
        }

        date =
                convertToDate(
                        booking.get("bookingDate")
                );

        if (date != null) {
            return date;
        }

        date =
                convertToDate(
                        booking.get("timestamp")
                );

        if (date != null) {
            return date;
        }

        date =
                convertToDate(
                        booking.get("createdAt")
                );

        return date;
    }

    private Date convertToDate(
            Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Timestamp) {

            return ((Timestamp) value).toDate();
        }

        if (value instanceof Date) {

            return (Date) value;
        }

        String text =
                String.valueOf(value).trim();

        if (text.isEmpty()) {
            return null;
        }

        String[] formats = {

                "dd/MM/yyyy",
                "dd-MM-yyyy",
                "yyyy-MM-dd",
                "MM/dd/yyyy",
                "dd MMM yyyy",
                "MMM dd, yyyy",
                "MMMM dd, yyyy"
        };

        for (String format : formats) {

            try {

                SimpleDateFormat sdf =
                        new SimpleDateFormat(
                                format,
                                Locale.getDefault()
                        );

                sdf.setLenient(false);

                Date parsed =
                        sdf.parse(text);

                if (parsed != null) {
                    return parsed;
                }

            } catch (ParseException ignored) {
            }
        }

        return null;
    }

    // =========================================================
    // REVENUE
    // =========================================================

    private double extractBookingAmount(
            DocumentSnapshot booking) {

        double amount =
                extractAmount(
                        booking,
                        "workerRate"
                );

        if (amount == 0) {

            amount =
                    extractAmount(
                            booking,
                            "amount"
                    );
        }

        if (amount == 0) {

            amount =
                    extractAmount(
                            booking,
                            "price"
                    );
        }

        return amount;
    }

    private double calculateRevenue(
            QuerySnapshot snapshot) {

        double total = 0;

        for (
                DocumentSnapshot booking :
                snapshot.getDocuments()
        ) {

            total +=
                    extractBookingAmount(
                            booking
                    );
        }

        return total;
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
                String.valueOf(value)
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

    // =========================================================
    // BAR CHART
    // =========================================================

    private class BarChartView extends View {

        private final Paint paint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        private double[] values =
                new double[]{0, 0, 0};

        private String[] labels =
                new String[]{
                        "Users",
                        "Workers",
                        "Pending"
                };

        private final int[] colors = {

                Color.parseColor("#6658D9"),
                Color.parseColor("#159B87"),
                Color.parseColor("#D28A22")
        };

        BarChartView(
                android.content.Context context) {

            super(context);

            paint.setTypeface(
                    Typeface.create(
                            Typeface.DEFAULT,
                            Typeface.BOLD
                    )
            );
        }

        void setValues(
                double[] values,
                String[] labels) {

            this.values = values;
            this.labels = labels;

            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            float width =
                    getWidth();

            float height =
                    getHeight();

            float left =
                    dpFloat(28);

            float right =
                    width - dpFloat(15);

            float top =
                    dpFloat(20);

            float bottom =
                    height - dpFloat(45);

            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(
                    dpFloat(1)
            );

            paint.setColor(
                    Color.parseColor("#E8E6F0")
            );

            for (int i = 0; i < 5; i++) {

                float y =
                        top +
                                (
                                        (bottom - top)
                                                * i / 4f
                                );

                canvas.drawLine(
                        left,
                        y,
                        right,
                        y,
                        paint
                );
            }

            double max =
                    1;

            for (double value : values) {

                if (value > max) {
                    max = value;
                }
            }

            float groupWidth =
                    (right - left)
                            / values.length;

            float barWidth =
                    Math.min(
                            dpFloat(55),
                            groupWidth * 0.55f
                    );

            for (int i = 0;
                 i < values.length;
                 i++) {

                float center =
                        left +
                                groupWidth * i +
                                groupWidth / 2;

                float barHeight =
                        (float)
                                (
                                        (bottom - top)
                                                *
                                                (values[i] / max)
                                );

                float barTop =
                        bottom - barHeight;

                paint.setStyle(
                        Paint.Style.FILL
                );

                paint.setColor(
                        colors[i]
                );

                RectF rect =
                        new RectF(
                                center - barWidth / 2,
                                barTop,
                                center + barWidth / 2,
                                bottom
                        );

                canvas.drawRoundRect(
                        rect,
                        dpFloat(10),
                        dpFloat(10),
                        paint
                );

                paint.setColor(
                        colors[i]
                );

                paint.setTextSize(
                        dpFloat(12)
                );

                paint.setTextAlign(
                        Paint.Align.CENTER
                );

                canvas.drawText(
                        formatNumber(
                                values[i]
                        ),
                        center,
                        Math.max(
                                top + dpFloat(15),
                                barTop - dpFloat(7)
                        ),
                        paint
                );

                paint.setTextSize(
                        dpFloat(10)
                );

                paint.setColor(
                        Color.parseColor("#777489")
                );

                canvas.drawText(
                        labels[i],
                        center,
                        bottom + dpFloat(27),
                        paint
                );
            }
        }
    }

    // =========================================================
    // AREA / POLYGON CHART
    // =========================================================

    private class AreaChartView extends View {

        private final Paint paint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        private final int lineColor;

        private List<Double> values =
                new ArrayList<>();

        private List<String> labels =
                new ArrayList<>();

        private String noDataText = "";

        AreaChartView(
                android.content.Context context,
                String color) {

            super(context);

            lineColor =
                    Color.parseColor(color);
        }

        void setData(
                List<Double> values,
                List<String> labels) {

            this.values =
                    new ArrayList<>(values);

            this.labels =
                    new ArrayList<>(labels);

            this.noDataText = "";

            invalidate();
        }

        void setNoData(
                String message) {

            values.clear();
            labels.clear();

            noDataText =
                    message;

            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            float width =
                    getWidth();

            float height =
                    getHeight();

            float left =
                    dpFloat(35);

            float right =
                    width - dpFloat(12);

            float top =
                    dpFloat(20);

            float bottom =
                    height - dpFloat(48);

            if (
                    !noDataText.isEmpty()
                            ||
                            values.isEmpty()
            ) {

                paint.setStyle(
                        Paint.Style.FILL
                );

                paint.setColor(
                        Color.parseColor("#8A8796")
                );

                paint.setTextSize(
                        dpFloat(12)
                );

                paint.setTextAlign(
                        Paint.Align.CENTER
                );

                canvas.drawText(
                        noDataText.isEmpty()
                                ? "No data available"
                                : noDataText,
                        width / 2,
                        height / 2,
                        paint
                );

                return;
            }

            double max =
                    1;

            for (double value : values) {

                if (value > max) {
                    max = value;
                }
            }

            // Grid
            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(
                    dpFloat(1)
            );

            paint.setColor(
                    Color.parseColor("#E8E6F0")
            );

            for (int i = 0; i < 5; i++) {

                float y =
                        top +
                                (
                                        (bottom - top)
                                                * i / 4f
                                );

                canvas.drawLine(
                        left,
                        y,
                        right,
                        y,
                        paint
                );
            }

            float step =
                    values.size() == 1
                            ? 0
                            : (right - left)
                            /
                            (values.size() - 1);

            Path line =
                    new Path();

            Path area =
                    new Path();

            for (
                    int i = 0;
                    i < values.size();
                    i++
            ) {

                float x =
                        values.size() == 1
                                ? (left + right) / 2
                                : left + step * i;

                float y =
                        bottom -
                                (float)
                                        (
                                                (bottom - top)
                                                        *
                                                        values.get(i)
                                                        / max
                                        );

                if (i == 0) {

                    line.moveTo(x, y);
                    area.moveTo(x, bottom);
                    area.lineTo(x, y);

                } else {

                    line.lineTo(x, y);
                    area.lineTo(x, y);
                }
            }

            float lastX =
                    values.size() == 1
                            ? (left + right) / 2
                            : left +
                            step *
                                    (values.size() - 1);

            area.lineTo(
                    lastX,
                    bottom
            );

            area.close();

            // Filled polygon area
            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setColor(
                    withAlpha(
                            lineColor,
                            55
                    )
            );

            canvas.drawPath(
                    area,
                    paint
            );

            // Main line
            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(
                    dpFloat(3)
            );

            paint.setColor(
                    lineColor
            );

            canvas.drawPath(
                    line,
                    paint
            );

            // Points + values
            for (
                    int i = 0;
                    i < values.size();
                    i++
            ) {

                float x =
                        values.size() == 1
                                ? (left + right) / 2
                                : left + step * i;

                float y =
                        bottom -
                                (float)
                                        (
                                                (bottom - top)
                                                        *
                                                        values.get(i)
                                                        / max
                                        );

                paint.setStyle(
                        Paint.Style.FILL
                );

                paint.setColor(
                        Color.WHITE
                );

                canvas.drawCircle(
                        x,
                        y,
                        dpFloat(5),
                        paint
                );

                paint.setColor(
                        lineColor
                );

                canvas.drawCircle(
                        x,
                        y,
                        dpFloat(3),
                        paint
                );

                paint.setTextSize(
                        dpFloat(9)
                );

                paint.setTextAlign(
                        Paint.Align.CENTER
                );

                canvas.drawText(
                        formatNumber(
                                values.get(i)
                        ),
                        x,
                        Math.max(
                                top + dpFloat(12),
                                y - dpFloat(10)
                        ),
                        paint
                );

                if (i < labels.size()) {

                    paint.setColor(
                            Color.parseColor(
                                    "#777489"
                            )
                    );

                    paint.setTextSize(
                            dpFloat(9)
                    );

                    canvas.drawText(
                            labels.get(i),
                            x,
                            bottom + dpFloat(28),
                            paint
                    );
                }
            }
        }
    }

    // =========================================================
    // DONUT CHART
    // =========================================================

    private class DonutChartView extends View {

        private final Paint paint =
                new Paint(Paint.ANTI_ALIAS_FLAG);

        private double[] values =
                new double[]{
                        0,
                        0,
                        0,
                        0
                };

        private String[] labels =
                new String[]{
                        "Pending",
                        "Accepted",
                        "Completed",
                        "Rejected"
                };

        private final int[] colors = {

                Color.parseColor("#D28A22"),
                Color.parseColor("#2872A7"),
                Color.parseColor("#168050"),
                Color.parseColor("#BE3A48")
        };

        DonutChartView(
                android.content.Context context) {

            super(context);
        }

        void setData(
                double[] values,
                String[] labels) {

            this.values = values;
            this.labels = labels;

            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {

            super.onDraw(canvas);

            float width =
                    getWidth();

            float height =
                    getHeight();

            float centerX =
                    width / 2;

            float centerY =
                    dpFloat(115);

            float radius =
                    dpFloat(75);

            double total = 0;

            for (double value : values) {
                total += value;
            }

            if (total <= 0) {

                paint.setStyle(
                        Paint.Style.STROKE
                );

                paint.setStrokeWidth(
                        dpFloat(32)
                );

                paint.setColor(
                        Color.parseColor(
                                "#ECEAF2"
                        )
                );

                canvas.drawCircle(
                        centerX,
                        centerY,
                        radius,
                        paint
                );

                paint.setStyle(
                        Paint.Style.FILL
                );

                paint.setColor(
                        Color.parseColor(
                                "#777489"
                        )
                );

                paint.setTextSize(
                        dpFloat(12)
                );

                paint.setTextAlign(
                        Paint.Align.CENTER
                );

                canvas.drawText(
                        "No bookings",
                        centerX,
                        centerY + dpFloat(4),
                        paint
                );

                drawLegend(
                        canvas,
                        width,
                        centerY + dpFloat(100)
                );

                return;
            }

            RectF oval =
                    new RectF(
                            centerX - radius,
                            centerY - radius,
                            centerX + radius,
                            centerY + radius
                    );

            float startAngle =
                    -90f;

            paint.setStyle(
                    Paint.Style.STROKE
            );

            paint.setStrokeWidth(
                    dpFloat(32)
            );

            paint.setStrokeCap(
                    Paint.Cap.BUTT
            );

            for (
                    int i = 0;
                    i < values.length;
                    i++
            ) {

                if (values[i] <= 0) {
                    continue;
                }

                float sweep =
                        (float)
                                (
                                        values[i]
                                                / total
                                                * 360
                                );

                paint.setColor(
                        colors[i]
                );

                canvas.drawArc(
                        oval,
                        startAngle,
                        sweep,
                        false,
                        paint
                );

                startAngle += sweep;
            }

            paint.setStyle(
                    Paint.Style.FILL
            );

            paint.setColor(
                    Color.parseColor(
                            "#292737"
                    )
            );

            paint.setTextSize(
                    dpFloat(20)
            );

            paint.setTypeface(
                    Typeface.DEFAULT_BOLD
            );

            paint.setTextAlign(
                    Paint.Align.CENTER
            );

            canvas.drawText(
                    formatNumber(total),
                    centerX,
                    centerY + dpFloat(2),
                    paint
            );

            paint.setTypeface(
                    Typeface.DEFAULT
            );

            paint.setTextSize(
                    dpFloat(9)
            );

            paint.setColor(
                    Color.parseColor(
                            "#777489"
                    )
            );

            canvas.drawText(
                    "Total bookings",
                    centerX,
                    centerY + dpFloat(19),
                    paint
            );

            drawLegend(
                    canvas,
                    width,
                    centerY + dpFloat(100)
            );
        }

        private void drawLegend(
                Canvas canvas,
                float width,
                float startY) {

            float columnWidth =
                    width / 2f;

            for (
                    int i = 0;
                    i < labels.length;
                    i++
            ) {

                int column =
                        i % 2;

                int row =
                        i / 2;

                float x =
                        column *
                                columnWidth +
                                dpFloat(30);

                float y =
                        startY +
                                row *
                                        dpFloat(38);

                paint.setStyle(
                        Paint.Style.FILL
                );

                paint.setColor(
                        colors[i]
                );

                canvas.drawCircle(
                        x,
                        y - dpFloat(4),
                        dpFloat(6),
                        paint
                );

                paint.setColor(
                        Color.parseColor(
                                "#4E4B5C"
                        )
                );

                paint.setTextSize(
                        dpFloat(10)
                );

                paint.setTextAlign(
                        Paint.Align.LEFT
                );

                canvas.drawText(
                        labels[i] +
                                "  " +
                                formatNumber(
                                        values[i]
                                ),
                        x + dpFloat(11),
                        y,
                        paint
                );
            }
        }
    }

    // =========================================================
    // COLOR
    // =========================================================

    private int withAlpha(
            int color,
            int alpha) {

        return Color.argb(
                alpha,
                Color.red(color),
                Color.green(color),
                Color.blue(color)
        );
    }

    // =========================================================
    // UI HELPERS
    // =========================================================

    private LinearLayout horizontalCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.HORIZONTAL
        );

        card.setGravity(
                Gravity.CENTER_VERTICAL
        );

        card.setPadding(
                dp(14),
                dp(14),
                dp(14),
                dp(14)
        );

        card.setBackground(
                cardBackground()
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.bottomMargin =
                dp(10);

        card.setLayoutParams(params);

        return card;
    }

    private LinearLayout verticalCard() {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                dp(15),
                dp(15),
                dp(15),
                dp(15)
        );

        card.setBackground(
                cardBackground()
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.bottomMargin =
                dp(10);

        card.setLayoutParams(params);

        return card;
    }

    private TextView avatar(
            String text,
            String color) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextColor(
                Color.WHITE
        );

        view.setTextSize(15);

        view.setTypeface(
                null,
                Typeface.BOLD
        );

        view.setGravity(
                Gravity.CENTER
        );

        view.setBackground(
                rounded(
                        Color.parseColor(color),
                        Color.TRANSPARENT,
                        50
                )
        );

        view.setLayoutParams(
                new LinearLayout.LayoutParams(
                        dp(46),
                        dp(46)
                )
        );

        return view;
    }

    private TextView mainText(
            String text) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextColor(
                Color.rgb(
                        40,
                        38,
                        54
                )
        );

        view.setTextSize(14);

        view.setTypeface(
                null,
                Typeface.BOLD
        );

        return view;
    }

    private TextView smallText(
            String text) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextColor(
                Color.rgb(
                        123,
                        120,
                        141
                )
        );

        view.setTextSize(11);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.topMargin =
                dp(4);

        view.setLayoutParams(params);

        return view;
    }

    private TextView badge(
            String text,
            String background,
            String textColor) {

        TextView view =
                new TextView(this);

        view.setText(text);

        view.setTextColor(
                Color.parseColor(textColor)
        );

        view.setTextSize(9);

        view.setTypeface(
                null,
                Typeface.BOLD
        );

        view.setGravity(
                Gravity.CENTER
        );

        view.setPadding(
                dp(9),
                dp(5),
                dp(9),
                dp(5)
        );

        view.setBackground(
                rounded(
                        Color.parseColor(background),
                        Color.TRANSPARENT,
                        20
                )
        );

        return view;
    }

    private TextView actionButton(
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
                rounded(
                        Color.parseColor(background),
                        Color.TRANSPARENT,
                        12
                )
        );

        return button;
    }

    private LinearLayout detailBox(
            String label,
            String value) {

        LinearLayout box =
                new LinearLayout(this);

        box.setOrientation(
                LinearLayout.VERTICAL
        );

        box.setPadding(
                dp(9),
                dp(7),
                dp(9),
                dp(7)
        );

        box.setBackground(
                rounded(
                        Color.rgb(
                                246,
                                245,
                                250
                        ),
                        Color.TRANSPARENT,
                        10
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        -2,
                        1
                );

        params.rightMargin =
                dp(5);

        box.setLayoutParams(params);

        box.addView(
                smallText(label)
        );

        TextView valueText =
                mainText(value);

        valueText.setTextSize(11);

        box.addView(valueText);

        return box;
    }

    private void empty(
            String message) {

        TextView view =
                new TextView(this);

        view.setText(message);

        view.setTextColor(
                Color.rgb(
                        125,
                        122,
                        143
                )
        );

        view.setTextSize(13);

        view.setGravity(
                Gravity.CENTER
        );

        view.setPadding(
                dp(20),
                dp(40),
                dp(20),
                dp(40)
        );

        container.addView(view);
    }

    private void showError() {

        container.removeAllViews();

        empty(
                "Unable to load data. Please try again."
        );
    }

    // =========================================================
    // SAFE FIRESTORE STRING
    // =========================================================

    private String getString(
            DocumentSnapshot document,
            String field,
            String fallback) {

        Object value =
                document.get(field);

        if (value == null) {
            return fallback;
        }

        String result =
                String.valueOf(value);

        if (
                result.trim().isEmpty()
        ) {

            return fallback;
        }

        return result;
    }

    // =========================================================
    // FORMAT NUMBER
    // =========================================================

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

    // =========================================================
    // INITIAL
    // =========================================================

    private String getInitial(
            String name) {

        if (
                name == null
                        ||
                        name.trim().isEmpty()
        ) {

            return "U";
        }

        return name
                .trim()
                .substring(0, 1)
                .toUpperCase(
                        Locale.getDefault()
                );
    }

    // =========================================================
    // FORMAT TEXT
    // =========================================================

    private String formatText(
            String text) {

        if (
                text == null
                        ||
                        text.trim().isEmpty()
        ) {

            return "";
        }

        String value =
                text.trim();

        return value
                .substring(0, 1)
                .toUpperCase(
                        Locale.getDefault()
                )
                +
                value.substring(1);
    }

    // =========================================================
    // CARD BACKGROUND
    // =========================================================

    private GradientDrawable cardBackground() {

        return rounded(
                Color.WHITE,
                Color.rgb(
                        224,
                        222,
                        237
                ),
                17
        );
    }

    private GradientDrawable rounded(
            int fill,
            int stroke,
            int radius) {

        GradientDrawable drawable =
                new GradientDrawable();

        drawable.setColor(fill);

        drawable.setCornerRadius(
                dp(radius)
        );

        if (
                stroke != Color.TRANSPARENT
        ) {

            drawable.setStroke(
                    dp(1),
                    stroke
            );
        }

        return drawable;
    }

    // =========================================================
    // DP
    // =========================================================

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int)
                (
                        value * density
                                + 0.5f
                );
    }

    private float dpFloat(int value) {

        return value *
                getResources()
                        .getDisplayMetrics()
                        .density;
    }

    // =========================================================
    // CLEANUP
    // =========================================================

    private void clearStatisticsListeners() {

        for (
                ListenerRegistration registration :
                statisticsListeners
        ) {

            if (registration != null) {
                registration.remove();
            }
        }

        statisticsListeners.clear();
    }

    @Override
    protected void onDestroy() {

        if (usersListener != null) {
            usersListener.remove();
        }

        if (workersListener != null) {
            workersListener.remove();
        }

        if (bookingsListener != null) {
            bookingsListener.remove();
        }

        clearStatisticsListeners();

        super.onDestroy();
    }
}