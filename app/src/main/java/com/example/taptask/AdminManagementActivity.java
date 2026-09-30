package com.example.taptask;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
    private ListenerRegistration areasListener;
    private ListenerRegistration feedbackListener;

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
        setupBottomNavigation();
        loadData();
    }

    // =========================================================
    // BOTTOM NAVIGATION
    // =========================================================

    private void setupBottomNavigation() {

        View home =
                findViewById(R.id.btnNavHome);

        View statistics =
                findViewById(R.id.btnNavStatistics);

        View feedback =
                findViewById(R.id.btnNavFeedback);

        // --------------------------------------------------------
        // HOME
        // --------------------------------------------------------

        if (home != null) {

            home.setOnClickListener(v -> finish());
        }

        // --------------------------------------------------------
        // STATISTICS
        // --------------------------------------------------------

        if (statistics != null) {

            statistics.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                AdminManagementActivity.this,
                                AdminManagementActivity.class
                        );

                intent.putExtra(
                        "MODE",
                        "STATISTICS"
                );

                startActivity(intent);
            });
        }

        // --------------------------------------------------------
        // APP FEEDBACK
        // --------------------------------------------------------

        if (feedback != null) {

            feedback.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                AdminManagementActivity.this,
                                AdminManagementActivity.class
                        );

                intent.putExtra(
                        "MODE",
                        "APP_FEEDBACK"
                );

                startActivity(intent);
            });
        }
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
                    "Tench and Saddar service coverage"
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

        } else if (mode.equals("APP_FEEDBACK")) {

            title.setText("App Feedback");
            subtitle.setText(
                    "Customer feedback and suggestions"
            );

            searchBox.setVisibility(View.VISIBLE);
            searchBox.setHint("Search feedback");
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

        } else if (mode.equals("APP_FEEDBACK")) {

            listenFeedback();
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
    // APP FEEDBACK
    // =========================================================

    private void listenFeedback() {

        if (feedbackListener != null) {
            feedbackListener.remove();
        }

        feedbackListener =
                db.collection("app_feedback")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (error != null
                                            || snapshot == null) {

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

        } else if (mode.equals("APP_FEEDBACK")) {

            List<DocumentSnapshot> filtered =
                    new ArrayList<>();

            for (
                    DocumentSnapshot feedback :
                    items
            ) {

                String customerEmail =
                        getString(
                                feedback,
                                "customerEmail",
                                ""
                        );

                String customerId =
                        getString(
                                feedback,
                                "customerId",
                                ""
                        );

                String improvement =
                        getString(
                                feedback,
                                "improvement",
                                ""
                        );

                String featureRequest =
                        getString(
                                feedback,
                                "featureRequest",
                                ""
                        );

                String problem =
                        getString(
                                feedback,
                                "problem",
                                ""
                        );

                String bookingExperience =
                        getString(
                                feedback,
                                "bookingExperience",
                                ""
                        );

                String appEase =
                        getString(
                                feedback,
                                "appEase",
                                ""
                        );

                String paymentExperience =
                        getString(
                                feedback,
                                "paymentExperience",
                                ""
                        );

                String rating =
                        getString(
                                feedback,
                                "rating",
                                ""
                        );

                String searchable =
                        (customerEmail + " "
                                + customerId + " "
                                + improvement + " "
                                + featureRequest + " "
                                + problem + " "
                                + bookingExperience + " "
                                + appEase + " "
                                + paymentExperience + " "
                                + rating)
                                .toLowerCase(
                                        Locale.getDefault()
                                );

                if (searchable.contains(q)) {
                    filtered.add(feedback);
                }
            }

            showFeedback(filtered);
        }
    }

    // =========================================================
    // APP FEEDBACK UI
    // =========================================================

    private void showFeedback(
            List<DocumentSnapshot> feedbackList) {

        container.removeAllViews();

        if (feedbackList.isEmpty()) {
            empty("No app feedback found");
            return;
        }

        for (
                DocumentSnapshot feedback :
                feedbackList
        ) {
            container.addView(
                    feedbackCard(feedback)
            );
        }
    }

    private View feedbackCard(
            DocumentSnapshot feedback) {

        LinearLayout card =
                verticalCard();

        String email =
                getString(
                        feedback,
                        "customerEmail",
                        ""
                );

        String customerId =
                getString(
                        feedback,
                        "customerId",
                        ""
                );

        String displayCustomer =
                email.trim().isEmpty()
                        ? (customerId.trim().isEmpty()
                        ? "Customer"
                        : customerId)
                        : email;

        LinearLayout top =
                new LinearLayout(this);

        top.setGravity(
                Gravity.CENTER_VERTICAL
        );

        top.addView(
                avatar(
                        getInitial(displayCustomer),
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

        infoParams.leftMargin = dp(12);
        info.setLayoutParams(infoParams);

        info.addView(
                mainText("Customer Feedback")
        );

        info.addView(
                smallText(displayCustomer)
        );

        if (!customerId.trim().isEmpty()) {
            info.addView(
                    smallText(
                            "Customer ID  •  " + customerId
                    )
            );
        }

        top.addView(info);

        String rating =
                getString(
                        feedback,
                        "rating",
                        "0"
                );

        top.addView(
                badge(
                        "★ " + rating + "/5",
                        "#EAE7FF",
                        "#5144B7"
                )
        );

        card.addView(top);

        LinearLayout divider =
                new LinearLayout(this);

        divider.setBackgroundColor(
                Color.parseColor("#EEEAF8")
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(1)
                );

        dividerParams.topMargin = dp(12);
        dividerParams.bottomMargin = dp(12);
        divider.setLayoutParams(dividerParams);
        card.addView(divider);

        String bookingExperience =
                getString(
                        feedback,
                        "bookingExperience",
                        "Not answered"
                );

        String appEase =
                getString(
                        feedback,
                        "appEase",
                        "Not answered"
                );

        String paymentExperience =
                getString(
                        feedback,
                        "paymentExperience",
                        "Not answered"
                );

        LinearLayout detailsRow =
                new LinearLayout(this);

        detailsRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        detailsRow.addView(
                detailBox(
                        "Booking",
                        bookingExperience
                )
        );

        detailsRow.addView(
                detailBox(
                        "Easy to use",
                        appEase
                )
        );

        detailsRow.addView(
                detailBox(
                        "Payment",
                        paymentExperience
                )
        );

        card.addView(detailsRow);

        LinearLayout serviceTitle =
                new LinearLayout(this);

        serviceTitle.setPadding(
                0,
                dp(12),
                0,
                dp(5)
        );

        serviceTitle.addView(
                smallText("Service Experience")
        );

        card.addView(serviceTitle);

        addFeedbackChipIfTrue(
                card,
                feedback,
                "workerProfessional",
                "Worker was professional"
        );

        addFeedbackChipIfTrue(
                card,
                feedback,
                "workerOnTime",
                "Worker arrived on time"
        );

        addFeedbackChipIfTrue(
                card,
                feedback,
                "goodServiceQuality",
                "Good service quality"
        );

        addFeedbackChipIfTrue(
                card,
                feedback,
                "goodCommunication",
                "Good communication"
        );

        addFeedbackSection(
                card,
                "What can we improve?",
                getString(
                        feedback,
                        "improvement",
                        ""
                ),
                "#F5F3FF",
                "#5144B7"
        );

        addFeedbackSection(
                card,
                "Feature Request",
                getString(
                        feedback,
                        "featureRequest",
                        ""
                ),
                "#EFF6FF",
                "#1D4ED8"
        );

        addFeedbackSection(
                card,
                "Problem Report",
                getString(
                        feedback,
                        "problem",
                        ""
                ),
                "#FFF1F2",
                "#BE123C"
        );

        TextView submitted =
                smallText(
                        "Submitted  •  "
                                + formatFeedbackDate(
                                feedback
                        )
                );

        LinearLayout.LayoutParams submittedParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        submittedParams.topMargin = dp(12);
        submitted.setLayoutParams(submittedParams);
        card.addView(submitted);

        return card;
    }

    private void addFeedbackChipIfTrue(
            LinearLayout card,
            DocumentSnapshot feedback,
            String field,
            String text) {

        Object value = feedback.get(field);

        boolean checked =
                value instanceof Boolean
                        && (Boolean) value;

        if (!checked) {
            return;
        }

        TextView chip =
                badge(
                        "✓  " + text,
                        "#DCFCE7",
                        "#15803D"
                );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -2,
                        -2
                );

        params.topMargin = dp(4);
        chip.setLayoutParams(params);
        card.addView(chip);
    }

    private void addFeedbackSection(
            LinearLayout card,
            String titleText,
            String value,
            String background,
            String foreground) {

        if (value == null
                || value.trim().isEmpty()) {
            return;
        }

        LinearLayout section =
                new LinearLayout(this);

        section.setOrientation(
                LinearLayout.VERTICAL
        );

        section.setPadding(
                dp(11),
                dp(9),
                dp(11),
                dp(9)
        );

        section.setBackground(
                rounded(
                        Color.parseColor(background),
                        Color.TRANSPARENT,
                        11
                )
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );

        params.topMargin = dp(9);
        section.setLayoutParams(params);

        TextView heading =
                smallText(titleText);
        heading.setTextColor(
                Color.parseColor(foreground)
        );
        heading.setTypeface(
                null,
                Typeface.BOLD
        );

        section.addView(heading);

        TextView body =
                smallText(value);
        body.setTextColor(
                Color.parseColor("#374151")
        );
        body.setTextSize(12);

        LinearLayout.LayoutParams bodyParams =
                new LinearLayout.LayoutParams(
                        -1,
                        -2
                );
        bodyParams.topMargin = dp(4);
        body.setLayoutParams(bodyParams);

        section.addView(body);
        card.addView(section);
    }

    private String formatFeedbackDate(
            DocumentSnapshot feedback) {

        Date date =
                convertToDate(
                        feedback.get("createdAt")
                );

        if (date == null) {
            return "Date unavailable";
        }

        return new SimpleDateFormat(
                "dd MMM yyyy, hh:mm a",
                Locale.getDefault()
        ).format(date);
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

        info.setLayoutParams(infoParams);

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
                dp(10)
        );

        TextView heading =
                mainText(
                        "Service Areas"
                );

        heading.setTextSize(18);

        section.addView(heading);

        section.addView(
                smallText(
                        "Manage the service areas available in TapTask"
                )
        );

        container.addView(section);

        // -----------------------------------------------------
        // ADD AREA BUTTON
        // -----------------------------------------------------

        Button addButton =
                new Button(this);

        addButton.setText(
                "+  Add Service Area"
        );

        addButton.setTextColor(
                Color.WHITE
        );

        addButton.setTextSize(12);

        addButton.setTypeface(
                null,
                Typeface.BOLD
        );

        addButton.setAllCaps(false);

        addButton.setBackground(
                rounded(
                        Color.parseColor("#6658D9"),
                        Color.TRANSPARENT,
                        14
                )
        );

        LinearLayout.LayoutParams addParams =
                new LinearLayout.LayoutParams(
                        -1,
                        dp(48)
                );

        addParams.bottomMargin =
                dp(15);

        addButton.setLayoutParams(
                addParams
        );

        addButton.setOnClickListener(
                v -> showAddAreaDialog()
        );

        container.addView(addButton);

        // -----------------------------------------------------
        // FIREBASE AREAS
        // -----------------------------------------------------

        listenAreas();
    }

    private void listenAreas() {

        if (areasListener != null) {
            areasListener.remove();
        }

        areasListener =
                db.collection("areas")
                        .addSnapshotListener(
                                (snapshot, error) -> {

                                    if (
                                            error != null
                                                    ||
                                                    snapshot == null
                                    ) {

                                        showAreaCards(
                                                new ArrayList<>()
                                        );

                                        return;
                                    }

                                    showAreaCards(
                                            snapshot.getDocuments()
                                    );
                                }
                        );
    }

    private void showAreaCards(
            List<DocumentSnapshot> documents) {

        /*
         * The first two views are:
         *
         * 0 = section heading
         * 1 = Add Service Area button
         *
         * Everything after that is an area card.
         */
        while (container.getChildCount() > 2) {

            container.removeViewAt(2);
        }

        boolean tenchFound = false;
        boolean saddarFound = false;

        for (
                DocumentSnapshot document :
                documents
        ) {

            String name =
                    getString(
                            document,
                            "name",
                            ""
                    );

            if (
                    name.equalsIgnoreCase(
                            "Tench"
                    )
            ) {

                tenchFound = true;

                addAreaCard(
                        document,
                        "Tench"
                );

            } else if (
                    name.equalsIgnoreCase(
                            "Saddar"
                    )
            ) {

                saddarFound = true;

                addAreaCard(
                        document,
                        "Saddar"
                );
            }
        }

        /*
         * If Firebase does not contain an area yet,
         * still show both fixed areas as "Not Added".
         *
         * This makes the admin page useful immediately.
         */

        if (!tenchFound) {

            addDefaultAreaCard(
                    "Tench"
            );
        }

        if (!saddarFound) {

            addDefaultAreaCard(
                    "Saddar"
            );
        }
    }

    private void addDefaultAreaCard(
            String areaName) {

        LinearLayout card =
                horizontalCard();

        card.setClickable(true);
        card.setFocusable(true);

        card.addView(
                avatar(
                        areaName.equalsIgnoreCase("Tench")
                                ? "T"
                                : "S",
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

        info.setLayoutParams(
                infoParams
        );

        info.addView(
                mainText(areaName)
        );

        info.addView(
                smallText(
                        "Service area is not added yet"
                )
        );

        card.addView(info);

        card.addView(
                badge(
                        "Not Added",
                        "#F1EFF7",
                        "#777489"
                )
        );

        card.setOnClickListener(
                v ->
                        showAddSpecificAreaDialog(
                                areaName
                        )
        );

        container.addView(card);
    }

    private void addAreaCard(
            DocumentSnapshot document,
            String areaName) {

        LinearLayout card =
                horizontalCard();

        card.setClickable(true);
        card.setFocusable(true);

        card.addView(
                avatar(
                        areaName.equalsIgnoreCase("Tench")
                                ? "T"
                                : "S",
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

        info.setLayoutParams(
                infoParams
        );

        info.addView(
                mainText(areaName)
        );

        boolean active =
                document.getBoolean("active") == null
                        ||
                        Boolean.TRUE.equals(
                                document.getBoolean("active")
                        );

        info.addView(
                smallText(
                        active
                                ? "Available for TapTask services"
                                : "Currently unavailable for services"
                )
        );

        card.addView(info);

        card.addView(
                badge(
                        active
                                ? "Active"
                                : "Inactive",
                        active
                                ? "#E0F6EA"
                                : "#FFE7EA",
                        active
                                ? "#168050"
                                : "#BE3A48"
                )
        );

        card.setOnClickListener(
                v ->
                        showEditAreaDialog(
                                document,
                                areaName,
                                active
                        )
        );

        container.addView(card);
    }

    private void showAddSpecificAreaDialog(
            String areaName) {

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                "Add " + areaName
                        )
                        .setMessage(
                                "Do you want to add "
                                        + areaName
                                        + " as an active TapTask service area?"
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Add Area",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                d -> {

                    Button positive =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    if (positive != null) {

                        positive.setTextColor(
                                Color.rgb(
                                        102,
                                        88,
                                        217
                                )
                        );

                        positive.setOnClickListener(
                                v -> {

                                    saveArea(
                                            areaName
                                    );

                                    dialog.dismiss();
                                }
                        );
                    }
                }
        );

        dialog.show();
    }

    private void showAddAreaDialog() {

        final String[] options = {
                "Tench",
                "Saddar"
        };

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                "Add Service Area"
                        )
                        .setSingleChoiceItems(
                                options,
                                -1,
                                null
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Add Area",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                d -> {

                    Button positive =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    if (positive != null) {

                        positive.setTextColor(
                                Color.rgb(
                                        102,
                                        88,
                                        217
                                )
                        );

                        positive.setOnClickListener(
                                v -> {

                                    int selected =
                                            dialog
                                                    .getListView()
                                                    .getCheckedItemPosition();

                                    if (
                                            selected < 0
                                    ) {

                                        Toast.makeText(
                                                this,
                                                "Please select an area",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        return;
                                    }

                                    saveArea(
                                            options[selected]
                                    );

                                    dialog.dismiss();
                                }
                        );
                    }
                }
        );

        dialog.show();
    }

    private void saveArea(
            String areaName) {

        if (
                !areaName.equalsIgnoreCase(
                        "Tench"
                )
                        &&
                        !areaName.equalsIgnoreCase(
                                "Saddar"
                        )
        ) {

            Toast.makeText(
                    this,
                    "Only Tench and Saddar are allowed",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Map<String, Object> data =
                new LinkedHashMap<>();

        data.put(
                "name",
                areaName
        );

        data.put(
                "active",
                true
        );

        data.put(
                "createdAt",
                FieldValue.serverTimestamp()
        );

        db.collection("areas")
                .document(
                        areaName.toLowerCase(
                                Locale.getDefault()
                        )
                )
                .set(data)
                .addOnSuccessListener(v -> {

                    Toast.makeText(
                            this,
                            areaName
                                    + " added successfully",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Unable to save area",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void showEditAreaDialog(
            DocumentSnapshot document,
            String areaName,
            boolean active) {

        String[] options = {
                "Active",
                "Inactive"
        };

        int selected =
                active ? 0 : 1;

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                "Manage " + areaName
                        )
                        .setSingleChoiceItems(
                                options,
                                selected,
                                null
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setNeutralButton(
                                "Delete",
                                null
                        )
                        .setPositiveButton(
                                "Save",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                d -> {

                    Button positive =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    Button neutral =
                            dialog.getButton(
                                    AlertDialog.BUTTON_NEUTRAL
                            );

                    if (positive != null) {

                        positive.setTextColor(
                                Color.rgb(
                                        102,
                                        88,
                                        217
                                )
                        );

                        positive.setOnClickListener(
                                v -> {

                                    int selectedItem =
                                            dialog
                                                    .getListView()
                                                    .getCheckedItemPosition();

                                    boolean newActive =
                                            selectedItem == 0;

                                    document.getReference()
                                            .update(
                                                    "active",
                                                    newActive
                                            )
                                            .addOnSuccessListener(
                                                    success -> {

                                                        Toast.makeText(
                                                                this,
                                                                areaName
                                                                        + " updated",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                        dialog.dismiss();
                                                    }
                                            )
                                            .addOnFailureListener(
                                                    error -> {

                                                        Toast.makeText(
                                                                this,
                                                                "Update failed",
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                    }
                                            );
                                }
                        );
                    }

                    if (neutral != null) {

                        neutral.setTextColor(
                                Color.rgb(
                                        190,
                                        58,
                                        72
                                )
                        );

                        neutral.setOnClickListener(
                                v -> {

                                    dialog.dismiss();

                                    confirmDeleteArea(
                                            document,
                                            areaName
                                    );
                                }
                        );
                    }
                }
        );

        dialog.show();
    }

    private void confirmDeleteArea(
            DocumentSnapshot document,
            String areaName) {

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle(
                                "Delete " + areaName + "?"
                        )
                        .setMessage(
                                "This will remove "
                                        + areaName
                                        + " from the admin service area list."
                        )
                        .setNegativeButton(
                                "Cancel",
                                null
                        )
                        .setPositiveButton(
                                "Delete",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                d -> {

                    Button positive =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    if (positive != null) {

                        positive.setTextColor(
                                Color.rgb(
                                        190,
                                        58,
                                        72
                                )
                        );

                        positive.setOnClickListener(
                                v -> {

                                    document.getReference()
                                            .delete()
                                            .addOnSuccessListener(
                                                    success -> {

                                                        Toast.makeText(
                                                                this,
                                                                areaName
                                                                        + " deleted",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                        dialog.dismiss();
                                                    }
                                            )
                                            .addOnFailureListener(
                                                    error -> {

                                                        Toast.makeText(
                                                                this,
                                                                "Delete failed",
                                                                Toast.LENGTH_SHORT
                                                        ).show();
                                                    }
                                            );
                                }
                        );
                    }
                }
        );

        dialog.show();
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

    // =========================================================
    // BOOKING CHART DATA
    // =========================================================

    private void updateBookingCharts(
            QuerySnapshot snapshot) {

        Map<String, Integer> bookingCounts =
                new LinkedHashMap<>();

        Map<String, Double> revenueCounts =
                new LinkedHashMap<>();

        int pending = 0;
        int accepted = 0;
        int completed = 0;
        int rejected = 0;

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

            if (bookingDate == null) {
                continue;
            }

            String key =
                    dateKey(bookingDate);

            if (!bookingCounts.containsKey(key)) {

                bookingCounts.put(
                        key,
                        0
                );

                revenueCounts.put(
                        key,
                        0.0
                );
            }

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
        }

        List<String> labels =
                new ArrayList<>(
                        bookingCounts.keySet()
                );

        Collections.sort(
                labels,
                (first, second) ->
                        first.compareTo(second)
        );

        if (labels.size() > 7) {

            labels =
                    new ArrayList<>(
                            labels.subList(
                                    labels.size() - 7,
                                    labels.size()
                            )
                    );
        }

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

            if (!labels.isEmpty()) {

                bookingsTrendChart.setData(
                        bookingValues,
                        createShortLabels(labels)
                );

            } else {

                bookingsTrendChart.setNoData(
                        "No booking data available"
                );
            }
        }

        if (revenueTrendChart != null) {

            if (!labels.isEmpty()) {

                revenueTrendChart.setData(
                        revenueValues,
                        createShortLabels(labels)
                );

            } else {

                revenueTrendChart.setNoData(
                        "No revenue data available"
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

        Date date =
                convertToDate(
                        booking.get("date")
                );

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

        return convertToDate(
                booking.get("createdAt")
        );
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

        if (value instanceof Number) {

            long number =
                    ((Number) value).longValue();

            if (number > 100000000000L) {

                return new Date(number);
            }

            if (number > 1000000000L) {

                return new Date(
                        number * 1000L
                );
            }
        }

        String text =
                String.valueOf(value)
                        .trim();

        if (text.isEmpty()) {
            return null;
        }

        String normalized =
                text.replace(
                        "T",
                        " "
                );

        if (normalized.endsWith("Z")) {

            normalized =
                    normalized.substring(
                            0,
                            normalized.length() - 1
                    );
        }

        String[] formats = {

                "yyyy-MM-dd",
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss",
                "dd/MM/yyyy",
                "dd/MM/yyyy HH:mm",
                "dd-MM-yyyy",
                "dd-MM-yyyy HH:mm",
                "MM/dd/yyyy",
                "MM-dd-yyyy",
                "dd.MM.yyyy",
                "yyyy/MM/dd",
                "dd MMM yyyy",
                "dd MMMM yyyy",
                "MMM dd, yyyy",
                "MMMM dd, yyyy",
                "MMM dd yyyy",
                "MMMM dd yyyy"
        };

        for (String format : formats) {

            try {

                SimpleDateFormat sdf =
                        new SimpleDateFormat(
                                format,
                                Locale.ENGLISH
                        );

                sdf.setLenient(false);

                Date parsed =
                        sdf.parse(normalized);

                if (parsed != null) {

                    return parsed;
                }

            } catch (ParseException ignored) {
            }
        }

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
    // AREA CHART
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

        if (areasListener != null) {
            areasListener.remove();
        }

        if (feedbackListener != null) {
            feedbackListener.remove();
        }

        clearStatisticsListeners();

        super.onDestroy();
    }
}