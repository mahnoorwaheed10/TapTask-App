package com.example.taptask;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class MyBookingsActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private LinearLayout bookingsContainer;
    private TextView tvEmpty;

    private final int PRIMARY = Color.parseColor("#6C63FF");
    private final int BLUE = Color.parseColor("#2563EB");
    private final int GREEN = Color.parseColor("#16A34A");
    private final int ORANGE = Color.parseColor("#F59E0B");
    private final int RED = Color.parseColor("#DC2626");
    private final int TEXT = Color.parseColor("#1F2937");
    private final int MUTED = Color.parseColor("#6B7280");
    private final int BG = Color.parseColor("#F5F7FF");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        createUI();
        loadBookings();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (auth != null && bookingsContainer != null) {
            loadBookings();
        }
    }

    private void createUI() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        // =========================
        // HEADER
        // =========================

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(20, 18, 20, 18);

        GradientDrawable headerBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.parseColor("#7C3AED"),
                        Color.parseColor("#4F46E5"),
                        Color.parseColor("#2563EB")
                }
        );

        headerBg.setCornerRadii(new float[]{
                0, 0,
                0, 0,
                22, 22,
                22, 22
        });

        header.setBackground(headerBg);

        TextView btnBack = new TextView(this);
        btnBack.setText("‹");
        btnBack.setTextColor(Color.WHITE);
        btnBack.setTextSize(36);
        btnBack.setGravity(Gravity.CENTER);
        btnBack.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        btnBack.setPadding(0, 0, 12, 0);

        btnBack.setOnClickListener(v -> finish());

        header.addView(
                btnBack,
                new LinearLayout.LayoutParams(50, 55)
        );

        LinearLayout titleBox = new LinearLayout(this);
        titleBox.setOrientation(LinearLayout.VERTICAL);

        TextView title = new TextView(this);
        title.setText("My Bookings");
        title.setTextColor(Color.WHITE);
        title.setTextSize(23);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        TextView subtitle = new TextView(this);
        subtitle.setText("Manage your service bookings");
        subtitle.setTextColor(Color.parseColor("#E0E7FF"));
        subtitle.setTextSize(13);

        titleBox.addView(title);
        titleBox.addView(subtitle);

        header.addView(
                titleBox,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        root.addView(header);

        // =========================
        // EMPTY MESSAGE
        // =========================

        tvEmpty = new TextView(this);
        tvEmpty.setText("No bookings found");
        tvEmpty.setTextColor(MUTED);
        tvEmpty.setTextSize(16);
        tvEmpty.setGravity(Gravity.CENTER);
        tvEmpty.setPadding(20, 40, 20, 20);
        tvEmpty.setVisibility(View.GONE);

        root.addView(
                tvEmpty,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );

        // =========================
        // BOOKINGS
        // =========================

        bookingsContainer = new LinearLayout(this);
        bookingsContainer.setOrientation(LinearLayout.VERTICAL);
        bookingsContainer.setPadding(16, 18, 16, 30);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.addView(bookingsContainer);

        root.addView(
                scrollView,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0,
                        1
                )
        );

        setContentView(root);
    }

    private void loadBookings() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        firestore.collection("bookings")
                .whereEqualTo("customerId", user.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    bookingsContainer.removeAllViews();

                    if (queryDocumentSnapshots.isEmpty()) {

                        tvEmpty.setVisibility(View.VISIBLE);
                        return;
                    }

                    tvEmpty.setVisibility(View.GONE);

                    for (com.google.firebase.firestore.DocumentSnapshot doc
                            : queryDocumentSnapshots.getDocuments()) {

                        Map<String, Object> data = doc.getData();

                        if (data == null) {
                            continue;
                        }

                        String bookingId = doc.getId();

                        String workerName =
                                getStringValue(
                                        data,
                                        "workerName",
                                        "Worker"
                                );

                        String serviceTitle =
                                getStringValue(
                                        data,
                                        "serviceTitle",
                                        "Service"
                                );

                        String workerRate =
                                getStringValue(
                                        data,
                                        "workerRate",
                                        "Rs. 0"
                                );

                        String date =
                                getStringValue(
                                        data,
                                        "date",
                                        "Not specified"
                                );

                        String time =
                                getStringValue(
                                        data,
                                        "time",
                                        "Not specified"
                                );

                        String address =
                                getStringValue(
                                        data,
                                        "address",
                                        "Not specified"
                                );

                        String paymentMethod =
                                getStringValue(
                                        data,
                                        "paymentMethod",
                                        "Cash"
                                );

                        String status =
                                getStringValue(
                                        data,
                                        "status",
                                        "Pending"
                                );

                        String paymentStatus =
                                getStringValue(
                                        data,
                                        "paymentStatus",
                                        "Pending"
                                );

                        addBookingCard(
                                bookingId,
                                workerName,
                                serviceTitle,
                                workerRate,
                                date,
                                time,
                                address,
                                paymentMethod,
                                status,
                                paymentStatus
                        );
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load bookings",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void addBookingCard(
            String bookingId,
            String workerName,
            String serviceTitle,
            String workerRate,
            String date,
            String time,
            String address,
            String paymentMethod,
            String status,
            String paymentStatus
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 18, 18, 18);

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(22);
        cardBg.setStroke(
                1,
                Color.parseColor("#E0E7FF")
        );

        card.setBackground(cardBg);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, 18);
        card.setLayoutParams(cardParams);

        // =========================
        // WORKER + SERVICE
        // =========================

        LinearLayout workerRow =
                new LinearLayout(this);

        workerRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        workerRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        TextView avatar =
                new TextView(this);

        avatar.setText(
                workerName.isEmpty()
                        ? "W"
                        : workerName
                        .substring(0, 1)
                        .toUpperCase()
        );

        avatar.setTextColor(
                Color.WHITE
        );

        avatar.setTextSize(18);

        avatar.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        avatar.setGravity(
                Gravity.CENTER
        );

        GradientDrawable avatarBg =
                new GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        new int[]{
                                PRIMARY,
                                Color.parseColor("#8B5CF6")
                        }
                );

        avatarBg.setShape(
                GradientDrawable.OVAL
        );

        avatar.setBackground(
                avatarBg
        );

        workerRow.addView(
                avatar,
                new LinearLayout.LayoutParams(
                        48,
                        48
                )
        );

        LinearLayout workerInfo =
                new LinearLayout(this);

        workerInfo.setOrientation(
                LinearLayout.VERTICAL
        );

        workerInfo.setPadding(
                12,
                0,
                8,
                0
        );

        TextView tvWorker =
                new TextView(this);

        tvWorker.setText(
                workerName
        );

        tvWorker.setTextSize(18);

        tvWorker.setTextColor(
                TEXT
        );

        tvWorker.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        TextView tvService =
                new TextView(this);

        tvService.setText(
                serviceTitle
        );

        tvService.setTextSize(13);

        tvService.setTextColor(
                PRIMARY
        );

        workerInfo.addView(
                tvWorker
        );

        workerInfo.addView(
                tvService
        );

        workerRow.addView(
                workerInfo,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        card.addView(
                workerRow
        );

        // =========================
        // DIVIDER
        // =========================

        View divider =
                new View(this);

        divider.setBackgroundColor(
                Color.parseColor("#EEF2FF")
        );

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1
                );

        dividerParams.setMargins(
                0,
                14,
                0,
                10
        );

        card.addView(
                divider,
                dividerParams
        );

        // =========================
        // INFO
        // =========================

        card.addView(
                createInfoText(
                        "💰  Rate: " + workerRate
                )
        );

        card.addView(
                createInfoText(
                        "📅  Date: " + date
                )
        );

        card.addView(
                createInfoText(
                        "🕐  Time: " + time
                )
        );

        card.addView(
                createInfoText(
                        "📍  Address: " + address
                )
        );

        card.addView(
                createInfoText(
                        "💳  Payment: " + paymentMethod
                )
        );

        // =========================
        // STATUS
        // =========================

        boolean isCompleted =
                status.equalsIgnoreCase(
                        "Completed"
                );

        boolean isAccepted =
                status.equalsIgnoreCase(
                        "Accepted"
                );

        boolean isPending =
                status.equalsIgnoreCase(
                        "Pending"
                );

        boolean isRejected =
                status.equalsIgnoreCase(
                        "Rejected"
                )
                        ||
                        status.equalsIgnoreCase(
                                "Cancelled"
                        );

        boolean isPaid =
                paymentStatus.equalsIgnoreCase(
                        "Paid"
                );

        TextView statusBadge =
                new TextView(this);

        statusBadge.setText(
                "●  " + status
        );

        statusBadge.setTextSize(13);

        statusBadge.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        statusBadge.setGravity(
                Gravity.CENTER
        );

        statusBadge.setPadding(
                14,
                8,
                14,
                8
        );

        GradientDrawable statusBg =
                new GradientDrawable();

        if (isCompleted) {

            statusBadge.setTextColor(
                    GREEN
            );

            statusBg.setColor(
                    Color.parseColor("#DCFCE7")
            );

        } else if (isAccepted) {

            statusBadge.setTextColor(
                    BLUE
            );

            statusBg.setColor(
                    Color.parseColor("#DBEAFE")
            );

        } else if (isRejected) {

            statusBadge.setTextColor(
                    RED
            );

            statusBg.setColor(
                    Color.parseColor("#FEE2E2")
            );

        } else if (isPending) {

            statusBadge.setTextColor(
                    ORANGE
            );

            statusBg.setColor(
                    Color.parseColor("#FFF7ED")
            );

        } else {

            statusBadge.setTextColor(
                    MUTED
            );

            statusBg.setColor(
                    Color.parseColor("#F3F4F6")
            );
        }

        statusBg.setCornerRadius(
                30
        );

        statusBadge.setBackground(
                statusBg
        );

        LinearLayout.LayoutParams statusParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        statusParams.setMargins(
                0,
                10,
                0,
                4
        );

        card.addView(
                statusBadge,
                statusParams
        );

        // =========================
        // PAYMENT STATUS
        // =========================

        TextView paymentBadge =
                new TextView(this);

        paymentBadge.setText(
                isPaid
                        ? "✓  Payment Paid"
                        : "○  Payment Pending"
        );

        paymentBadge.setTextSize(12);

        paymentBadge.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        paymentBadge.setGravity(
                Gravity.CENTER
        );

        paymentBadge.setPadding(
                12,
                7,
                12,
                7
        );

        GradientDrawable paymentBg =
                new GradientDrawable();

        if (isPaid) {

            paymentBadge.setTextColor(
                    GREEN
            );

            paymentBg.setColor(
                    Color.parseColor("#ECFDF5")
            );

        } else {

            paymentBadge.setTextColor(
                    ORANGE
            );

            paymentBg.setColor(
                    Color.parseColor("#FFF7ED")
            );
        }

        paymentBg.setCornerRadius(
                30
        );

        paymentBadge.setBackground(
                paymentBg
        );

        LinearLayout.LayoutParams paymentParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        paymentParams.setMargins(
                0,
                4,
                0,
                10
        );

        card.addView(
                paymentBadge,
                paymentParams
        );

        // =========================
        // TOP BUTTONS
        // =========================

        LinearLayout buttonsRow =
                new LinearLayout(this);

        buttonsRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        buttonsRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        // =====================================================
        // CHAT
        // =====================================================

        Button chatButton =
                createColoredButton(
                        "Chat",
                        BLUE
                );

        chatButton.setOnClickListener(v -> {

            FirebaseUser currentUser =
                    auth.getCurrentUser();

            if (currentUser == null) {

                Toast.makeText(
                        MyBookingsActivity.this,
                        "Please login first",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Intent intent =
                    new Intent(
                            MyBookingsActivity.this,
                            ChatActivity.class
                    );

            // Worker information
            intent.putExtra(
                    ChatActivity.EXTRA_WORKER_NAME,
                    workerName
            );

            // Customer information
            intent.putExtra(
                    ChatActivity.EXTRA_CUSTOMER_NAME,
                    getCurrentCustomerName()
            );

            intent.putExtra(
                    ChatActivity.EXTRA_CUSTOMER_ID,
                    currentUser.getUid()
            );

            // IMPORTANT:
            // This is CUSTOMER side.
            intent.putExtra(
                    ChatActivity.EXTRA_IS_WORKER,
                    false
            );

            // Keep booking ID for future booking-specific chat use.
            intent.putExtra(
                    "bookingId",
                    bookingId
            );

            startActivity(intent);
        });

        buttonsRow.addView(
                chatButton,
                createButtonParams()
        );

        // =========================
        // PAY NOW
        // ONLY COMPLETED + NOT PAID
        // =========================

        if (isCompleted && !isPaid) {

            Button payButton =
                    createColoredButton(
                            "Pay Now",
                            GREEN
                    );

            payButton.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                MyBookingsActivity.this,
                                PaymentActivity.class
                        );

                intent.putExtra(
                        PaymentActivity.EXTRA_BOOKING_ID,
                        bookingId
                );

                intent.putExtra(
                        PaymentActivity.EXTRA_PAYMENT_METHOD,
                        paymentMethod
                );

                startActivity(intent);
            });

            buttonsRow.addView(
                    payButton,
                    createButtonParams()
            );
        }

        card.addView(
                buttonsRow
        );

        // =========================
        // COMPLETED BUTTONS
        // =========================

        if (isCompleted) {

            LinearLayout completedRow =
                    new LinearLayout(this);

            completedRow.setOrientation(
                    LinearLayout.HORIZONTAL
            );

            completedRow.setPadding(
                    0,
                    8,
                    0,
                    0
            );

            // =========================
            // APP FEEDBACK
            // =========================

            Button feedbackButton =
                    createColoredButton(
                            "App Feedback",
                            PRIMARY
                    );

            feedbackButton.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                MyBookingsActivity.this,
                                AppFeedbackActivity.class
                        );

                startActivity(intent);
            });

            completedRow.addView(
                    feedbackButton,
                    createButtonParams()
            );

            // =========================
            // RATE & REVIEW
            // =========================

            Button rateButton =
                    createColoredButton(
                            "Rate & Review",
                            Color.parseColor("#8B5CF6")
                    );

            rateButton.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                MyBookingsActivity.this,
                                RateReviewActivity.class
                        );

                intent.putExtra(
                        RateReviewActivity.EXTRA_BOOKING_ID,
                        bookingId
                );

                intent.putExtra(
                        RateReviewActivity.EXTRA_WORKER_NAME,
                        workerName
                );

                intent.putExtra(
                        RateReviewActivity.EXTRA_SERVICE,
                        serviceTitle
                );

                intent.putExtra(
                        RateReviewActivity.EXTRA_RATE,
                        workerRate
                );

                startActivity(intent);
            });

            completedRow.addView(
                    rateButton,
                    createButtonParams()
            );

            card.addView(
                    completedRow
            );
        }

        bookingsContainer.addView(
                card
        );
    }

    // =========================================================
    // CURRENT CUSTOMER NAME
    // =========================================================

    private String getCurrentCustomerName() {

        FirebaseUser user =
                auth.getCurrentUser();

        if (user != null &&
                user.getDisplayName() != null &&
                !user.getDisplayName()
                        .trim()
                        .isEmpty()) {

            return user.getDisplayName().trim();
        }

        // ChatActivity users collection se actual
        // customer name dobara load kar legi.
        return "Customer";
    }

    // =========================================================
    // INFO TEXT
    // =========================================================

    private TextView createInfoText(
            String text) {

        TextView tv =
                new TextView(this);

        tv.setText(text);
        tv.setTextSize(14);
        tv.setTextColor(MUTED);
        tv.setPadding(
                0,
                4,
                0,
                4
        );

        return tv;
    }

    // =========================================================
    // COLORED BUTTON
    // =========================================================

    private Button createColoredButton(
            String text,
            int color) {

        Button button =
                new Button(this);

        button.setText(text);
        button.setTextColor(
                Color.WHITE
        );

        button.setTextSize(14);

        button.setTypeface(
                Typeface.DEFAULT,
                Typeface.BOLD
        );

        button.setAllCaps(false);

        button.setGravity(
                Gravity.CENTER
        );

        GradientDrawable bg =
                new GradientDrawable();

        bg.setColor(color);

        bg.setCornerRadius(
                18
        );

        button.setBackground(
                bg
        );

        button.setPadding(
                12,
                8,
                12,
                8
        );

        return button;
    }

    // =========================================================
    // BUTTON PARAMS
    // =========================================================

    private LinearLayout.LayoutParams createButtonParams() {

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        0,
                        56,
                        1
                );

        params.setMargins(
                5,
                0,
                5,
                0
        );

        return params;
    }

    // =========================================================
    // FIRESTORE STRING VALUE
    // =========================================================

    private String getStringValue(
            Map<String, Object> data,
            String key,
            String defaultValue) {

        Object value =
                data.get(key);

        if (value == null) {
            return defaultValue;
        }

        String result =
                String.valueOf(value);

        if (result.trim().isEmpty()) {
            return defaultValue;
        }

        return result;
    }
}