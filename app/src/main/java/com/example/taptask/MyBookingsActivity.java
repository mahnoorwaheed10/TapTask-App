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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Map;

public class MyBookingsActivity extends AppCompatActivity {

    private TextView btnBack;
    private LinearLayout bookingsContainer;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_bookings);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btnBack);
        bookingsContainer = findViewById(R.id.bookingsContainer);

        btnBack.setOnClickListener(v -> finish());

        loadMyBookings();
    }

    private void loadMyBookings() {

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

        String customerId =
                currentUser.getUid();

        firestore.collection("bookings")
                .whereEqualTo(
                        "customerId",
                        customerId
                )
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    bookingsContainer.removeAllViews();

                    if (queryDocumentSnapshots.isEmpty()) {

                        TextView emptyText =
                                new TextView(this);

                        emptyText.setText(
                                "No bookings yet"
                        );

                        emptyText.setTextSize(16);

                        emptyText.setTextColor(
                                Color.GRAY
                        );

                        emptyText.setGravity(
                                Gravity.CENTER
                        );

                        emptyText.setPadding(
                                20,
                                80,
                                20,
                                20
                        );

                        bookingsContainer.addView(
                                emptyText
                        );

                        return;
                    }

                    queryDocumentSnapshots.forEach(
                            documentSnapshot -> {

                                Map<String, Object> booking =
                                        documentSnapshot.getData();

                                String bookingId =
                                        documentSnapshot.getId();

                                String workerName =
                                        getStringValue(
                                                booking,
                                                "workerName"
                                        );

                                String serviceTitle =
                                        getStringValue(
                                                booking,
                                                "serviceTitle"
                                        );

                                String workerRate =
                                        getStringValue(
                                                booking,
                                                "workerRate"
                                        );

                                String date =
                                        getStringValue(
                                                booking,
                                                "date"
                                        );

                                String time =
                                        getStringValue(
                                                booking,
                                                "time"
                                        );

                                String address =
                                        getStringValue(
                                                booking,
                                                "address"
                                        );

                                String paymentMethod =
                                        getStringValue(
                                                booking,
                                                "paymentMethod"
                                        );

                                String status =
                                        getStringValue(
                                                booking,
                                                "status"
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
                                        status
                                );
                            }
                    );
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Failed to load bookings: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
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
            String status) {

        // =========================
        // MAIN CARD
        // =========================

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(
                20,
                20,
                20,
                20
        );

        GradientDrawable cardBackground =
                new GradientDrawable();

        cardBackground.setColor(
                Color.WHITE
        );

        cardBackground.setCornerRadius(
                24
        );

        cardBackground.setStroke(
                1,
                Color.rgb(225, 229, 238)
        );

        card.setBackground(
                cardBackground
        );

        card.setElevation(5);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(
                0,
                0,
                0,
                18
        );

        card.setLayoutParams(
                cardParams
        );

        // =========================
        // TOP ROW
        // =========================

        LinearLayout topRow =
                new LinearLayout(this);

        topRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        topRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        LinearLayout workerInfo =
                new LinearLayout(this);

        workerInfo.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams workerInfoParams =
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1
                );

        workerInfo.setLayoutParams(
                workerInfoParams
        );

        TextView workerText =
                new TextView(this);

        workerText.setText(
                workerName
        );

        workerText.setTextSize(18);

        workerText.setTextColor(
                Color.rgb(30, 41, 59)
        );

        workerText.setTypeface(
                null,
                Typeface.BOLD
        );

        TextView serviceText =
                new TextView(this);

        serviceText.setText(
                serviceTitle
        );

        serviceText.setTextSize(13);

        serviceText.setTextColor(
                Color.rgb(79, 70, 229)
        );

        serviceText.setPadding(
                0,
                4,
                0,
                0
        );

        workerInfo.addView(
                workerText
        );

        workerInfo.addView(
                serviceText
        );

        // =========================
        // STATUS BADGE
        // =========================

        TextView statusBadge =
                new TextView(this);

        statusBadge.setText(
                status
        );

        statusBadge.setTextSize(12);

        statusBadge.setTypeface(
                null,
                Typeface.BOLD
        );

        statusBadge.setGravity(
                Gravity.CENTER
        );

        statusBadge.setPadding(
                14,
                7,
                14,
                7
        );

        GradientDrawable statusBackground =
                new GradientDrawable();

        statusBackground.setCornerRadius(
                30
        );

        if ("Completed".equalsIgnoreCase(status)) {

            statusBadge.setTextColor(
                    Color.rgb(22, 101, 52)
            );

            statusBackground.setColor(
                    Color.rgb(220, 252, 231)
            );

        } else if ("Accepted".equalsIgnoreCase(status)
                || "Confirmed".equalsIgnoreCase(status)) {

            statusBadge.setTextColor(
                    Color.rgb(30, 64, 175)
            );

            statusBackground.setColor(
                    Color.rgb(219, 234, 254)
            );

        } else if ("Rejected".equalsIgnoreCase(status)) {

            statusBadge.setTextColor(
                    Color.rgb(185, 28, 28)
            );

            statusBackground.setColor(
                    Color.rgb(254, 226, 226)
            );

        } else {

            statusBadge.setTextColor(
                    Color.rgb(146, 64, 14)
            );

            statusBackground.setColor(
                    Color.rgb(255, 237, 213)
            );
        }

        statusBadge.setBackground(
                statusBackground
        );

        topRow.addView(
                workerInfo
        );

        topRow.addView(
                statusBadge
        );

        card.addView(
                topRow
        );

        // =========================
        // DIVIDER
        // =========================

        View divider =
                new View(this);

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1
                );

        dividerParams.setMargins(
                0,
                16,
                0,
                14
        );

        divider.setLayoutParams(
                dividerParams
        );

        divider.setBackgroundColor(
                Color.rgb(235, 238, 245)
        );

        card.addView(
                divider
        );

        // =========================
        // BOOKING INFORMATION
        // =========================

        TextView rateText =
                createInfoText(
                        "💰  Rate: " + workerRate,
                        true
                );

        TextView dateText =
                createInfoText(
                        "📅  Date: " + date,
                        false
                );

        TextView timeText =
                createInfoText(
                        "🕐  Time: " + time,
                        false
                );

        TextView addressText =
                createInfoText(
                        "📍  " + address,
                        false
                );

        String paymentDisplay =
                paymentMethod;

        if ("cash".equalsIgnoreCase(
                paymentMethod)) {

            paymentDisplay =
                    "Cash on Delivery";

        } else if ("stripe".equalsIgnoreCase(
                paymentMethod)) {

            paymentDisplay =
                    "Stripe";
        }

        TextView paymentText =
                createInfoText(
                        "💳  Payment: "
                                + paymentDisplay,
                        false
                );

        card.addView(rateText);
        card.addView(dateText);
        card.addView(timeText);
        card.addView(addressText);
        card.addView(paymentText);

        // =========================
        // BUTTON CONTAINER
        // =========================

        LinearLayout buttonRow =
                new LinearLayout(this);

        buttonRow.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams buttonRowParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        buttonRowParams.setMargins(
                0,
                14,
                0,
                0
        );

        buttonRow.setLayoutParams(
                buttonRowParams
        );

        // =========================
        // CHAT BUTTON
        // =========================

        Button chatButton =
                new Button(this);

        chatButton.setText(
                "💬  Chat with Worker"
        );

        chatButton.setTextSize(14);

        chatButton.setTextColor(
                Color.rgb(79, 70, 229)
        );

        chatButton.setAllCaps(false);

        GradientDrawable chatBackground =
                new GradientDrawable();

        chatBackground.setColor(
                Color.rgb(238, 242, 255)
        );

        chatBackground.setCornerRadius(
                40
        );

        chatBackground.setStroke(
                1,
                Color.rgb(199, 210, 254)
        );

        chatButton.setBackground(
                chatBackground
        );

        LinearLayout.LayoutParams chatParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        chatButton.setLayoutParams(
                chatParams
        );

        chatButton.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MyBookingsActivity.this,
                            ChatActivity.class
                    );

            intent.putExtra(
                    ChatActivity.EXTRA_WORKER_NAME,
                    workerName
            );

            startActivity(intent);
        });

        buttonRow.addView(
                chatButton
        );

        // =========================
        // COMPLETED BOOKING BUTTONS
        // =========================

        if ("Completed".equalsIgnoreCase(status)) {

            // -------------------------
            // VIEW INVOICE
            // -------------------------

            Button invoiceButton =
                    new Button(this);

            invoiceButton.setText(
                    "🧾  View Invoice"
            );

            invoiceButton.setTextSize(14);

            invoiceButton.setTextColor(
                    Color.rgb(51, 65, 85)
            );

            invoiceButton.setAllCaps(false);

            GradientDrawable invoiceBackground =
                    new GradientDrawable();

            invoiceBackground.setColor(
                    Color.rgb(248, 250, 252)
            );

            invoiceBackground.setCornerRadius(
                    40
            );

            invoiceBackground.setStroke(
                    1,
                    Color.rgb(203, 213, 225)
            );

            invoiceButton.setBackground(
                    invoiceBackground
            );

            LinearLayout.LayoutParams invoiceParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            invoiceParams.setMargins(
                    0,
                    8,
                    0,
                    0
            );

            invoiceButton.setLayoutParams(
                    invoiceParams
            );

            invoiceButton.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                MyBookingsActivity.this,
                                InvoiceActivity.class
                        );

                intent.putExtra(
                        InvoiceActivity.EXTRA_BOOKING_ID,
                        bookingId
                );

                intent.putExtra(
                        InvoiceActivity.EXTRA_WORKER_NAME,
                        workerName
                );

                intent.putExtra(
                        InvoiceActivity.EXTRA_SERVICE,
                        serviceTitle
                );

                intent.putExtra(
                        InvoiceActivity.EXTRA_RATE,
                        workerRate
                );

                startActivity(intent);
            });

            buttonRow.addView(
                    invoiceButton
            );

            // -------------------------
            // RATE & REVIEW
            // -------------------------

            Button rateButton =
                    new Button(this);

            rateButton.setText(
                    "⭐  Rate & Review"
            );

            rateButton.setTextSize(14);

            rateButton.setTextColor(
                    Color.rgb(79, 70, 229)
            );

            rateButton.setAllCaps(false);

            GradientDrawable rateBackground =
                    new GradientDrawable();

            rateBackground.setColor(
                    Color.rgb(245, 243, 255)
            );

            rateBackground.setCornerRadius(
                    40
            );

            rateBackground.setStroke(
                    1,
                    Color.rgb(221, 214, 254)
            );

            rateButton.setBackground(
                    rateBackground
            );

            LinearLayout.LayoutParams rateParams =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

            rateParams.setMargins(
                    0,
                    8,
                    0,
                    0
            );

            rateButton.setLayoutParams(
                    rateParams
            );

            rateButton.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                MyBookingsActivity.this,
                                InvoiceActivity.class
                        );

                intent.putExtra(
                        InvoiceActivity.EXTRA_BOOKING_ID,
                        bookingId
                );

                intent.putExtra(
                        InvoiceActivity.EXTRA_WORKER_NAME,
                        workerName
                );

                intent.putExtra(
                        InvoiceActivity.EXTRA_SERVICE,
                        serviceTitle
                );

                intent.putExtra(
                        InvoiceActivity.EXTRA_RATE,
                        workerRate
                );

                startActivity(intent);
            });

            buttonRow.addView(
                    rateButton
            );
        }

        card.addView(
                buttonRow
        );

        bookingsContainer.addView(
                card
        );
    }

    private TextView createInfoText(
            String text,
            boolean bold) {

        TextView textView =
                new TextView(this);

        textView.setText(
                text
        );

        textView.setTextSize(14);

        textView.setTextColor(
                Color.rgb(71, 85, 105)
        );

        if (bold) {

            textView.setTypeface(
                    null,
                    Typeface.BOLD
            );

            textView.setTextColor(
                    Color.rgb(22, 101, 52)
            );
        }

        textView.setPadding(
                0,
                5,
                0,
                5
        );

        return textView;
    }

    private String getStringValue(
            Map<String, Object> data,
            String key) {

        Object value =
                data.get(key);

        if (value == null) {
            return "";
        }

        return String.valueOf(value);
    }
}