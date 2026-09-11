package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private LinearLayout bookingsContainer;
    private TextView btnBack;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        bookingsContainer =
                findViewById(R.id.bookingsContainer);

        btnBack =
                findViewById(R.id.btnBack);

        firebaseAuth =
                FirebaseAuth.getInstance();

        firestore =
                FirebaseFirestore.getInstance();

        btnBack.setOnClickListener(v -> finish());

        loadBookings();
    }

    private void loadBookings() {

        bookingsContainer.removeAllViews();

        FirebaseUser user =
                firebaseAuth.getCurrentUser();

        if (user == null) {

            showMessage("Please login first.");
            return;
        }

        TextView loading =
                new TextView(this);

        loading.setText(
                "Loading bookings..."
        );

        loading.setTextSize(14);

        loading.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        loading.setGravity(
                Gravity.CENTER
        );

        loading.setPadding(
                0,
                dp(30),
                0,
                dp(30)
        );

        bookingsContainer.addView(loading);

        firestore
                .collection("bookings")
                .whereEqualTo(
                        "customerId",
                        user.getUid()
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    bookingsContainer.removeAllViews();

                    List<BookingDisplayData> bookings =
                            new ArrayList<>();

                    for (QueryDocumentSnapshot document :
                            querySnapshot) {

                        String workerName =
                                document.getString(
                                        "workerName"
                                );

                        String serviceTitle =
                                document.getString(
                                        "serviceTitle"
                                );

                        String date =
                                document.getString(
                                        "date"
                                );

                        String status =
                                document.getString(
                                        "status"
                                );

                        if (workerName == null) {
                            workerName = "Worker";
                        }

                        if (serviceTitle == null) {
                            serviceTitle = "Service";
                        }

                        if (date == null) {
                            date = "";
                        }

                        if (status == null) {
                            status = "Pending";
                        }

                        String statusColor =
                                getStatusColor(status);

                        bookings.add(
                                new BookingDisplayData(
                                        workerName,
                                        serviceTitle,
                                        date,
                                        status,
                                        statusColor,
                                        document.getId()
                                )
                        );
                    }

                    if (bookings.isEmpty()) {

                        showMessage(
                                "You have no bookings yet."
                        );

                        return;
                    }

                    for (BookingDisplayData booking :
                            bookings) {

                        bookingsContainer.addView(
                                createBookingCard(
                                        booking
                                )
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    bookingsContainer.removeAllViews();

                    showMessage(
                            "Could not load bookings."
                    );

                    Toast.makeText(
                            MyBookingsActivity.this,
                            "Firebase error: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private String getStatusColor(String status) {

        if (status.equalsIgnoreCase("Confirmed")) {
            return "#3B82F6";
        }

        if (status.equalsIgnoreCase("Accepted")) {
            return "#3B82F6";
        }

        if (status.equalsIgnoreCase("Completed")) {
            return "#10B981";
        }

        if (status.equalsIgnoreCase("Cancelled")) {
            return "#EF4444";
        }

        if (status.equalsIgnoreCase("Rejected")) {
            return "#EF4444";
        }

        return "#F59E0B";
    }

    private void showMessage(String message) {

        TextView empty =
                new TextView(this);

        empty.setText(message);

        empty.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        empty.setTextSize(14);

        empty.setGravity(
                Gravity.CENTER
        );

        empty.setPadding(
                0,
                dp(30),
                0,
                dp(30)
        );

        bookingsContainer.addView(empty);
    }

    private LinearLayout createBookingCard(
            BookingDisplayData booking) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setBackgroundResource(
                R.drawable.bg_card
        );

        card.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.bottomMargin =
                dp(12);

        card.setLayoutParams(cardParams);

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

        LinearLayout nameContainer =
                new LinearLayout(this);

        nameContainer.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        nameContainer.setLayoutParams(
                nameParams
        );

        TextView name =
                new TextView(this);

        name.setText(
                booking.workerName
        );

        name.setTextColor(
                getResources().getColor(
                        R.color.text_main
                )
        );

        name.setTextSize(16);

        name.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        nameContainer.addView(name);

        TextView service =
                new TextView(this);

        service.setText(
                booking.serviceTitle
        );

        service.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        service.setTextSize(13);

        nameContainer.addView(service);

        topRow.addView(
                nameContainer
        );

        // =========================
        // STATUS
        // =========================

        TextView statusBadge =
                new TextView(this);

        statusBadge.setText(
                booking.status
        );

        statusBadge.setTextSize(11);

        statusBadge.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        statusBadge.setPadding(
                dp(10),
                dp(5),
                dp(10),
                dp(5)
        );

        statusBadge.setTextColor(
                android.graphics.Color.parseColor(
                        booking.statusColor
                )
        );

        topRow.addView(
                statusBadge
        );

        card.addView(topRow);

        // =========================
        // DATE
        // =========================

        TextView date =
                new TextView(this);

        date.setText(
                "📅 " + booking.date
        );

        date.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        date.setTextSize(12);

        LinearLayout.LayoutParams dateParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        dateParams.topMargin =
                dp(8);

        date.setLayoutParams(
                dateParams
        );

        card.addView(date);

        // =========================
        // BUTTON ROW
        // =========================

        LinearLayout buttonRow =
                new LinearLayout(this);

        buttonRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        LinearLayout.LayoutParams buttonRowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        buttonRowParams.topMargin =
                dp(12);

        buttonRow.setLayoutParams(
                buttonRowParams
        );

        // =========================
        // CHAT BUTTON
        // =========================

        TextView btnChat =
                new TextView(this);

        btnChat.setText(
                "💬 Chat"
        );

        btnChat.setTextSize(13);

        btnChat.setGravity(
                Gravity.CENTER
        );

        btnChat.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        btnChat.setTextColor(
                getResources().getColor(
                        R.color.white
                )
        );

        btnChat.setBackgroundResource(
                R.drawable.bg_btn_green
        );

        btnChat.setPadding(
                dp(14),
                dp(10),
                dp(14),
                dp(10)
        );

        LinearLayout.LayoutParams chatParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        btnChat.setLayoutParams(
                chatParams
        );

        btnChat.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MyBookingsActivity.this,
                            ChatActivity.class
                    );

            intent.putExtra(
                    ChatActivity.EXTRA_WORKER_NAME,
                    booking.workerName
            );

            startActivity(intent);
        });

        buttonRow.addView(btnChat);

        // =========================
        // INVOICE BUTTON
        // =========================

        if (booking.status.equalsIgnoreCase(
                "Completed"
        )) {

            TextView btnInvoice =
                    new TextView(this);

            btnInvoice.setText(
                    "🧾 Invoice"
            );

            btnInvoice.setTextSize(13);

            btnInvoice.setGravity(
                    Gravity.CENTER
            );

            btnInvoice.setTypeface(
                    null,
                    android.graphics.Typeface.BOLD
            );

            btnInvoice.setTextColor(
                    getResources().getColor(
                            R.color.primary
                    )
            );

            btnInvoice.setBackgroundResource(
                    R.drawable.bg_btn_outline
            );

            btnInvoice.setPadding(
                    dp(14),
                    dp(10),
                    dp(14),
                    dp(10)
            );

            LinearLayout.LayoutParams invoiceParams =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1f
                    );

            invoiceParams.setMarginStart(
                    dp(8)
            );

            btnInvoice.setLayoutParams(
                    invoiceParams
            );

            btnInvoice.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                MyBookingsActivity.this,
                                InvoiceActivity.class
                        );

                intent.putExtra(
                        InvoiceActivity.EXTRA_WORKER_NAME,
                        booking.workerName
                );

                intent.putExtra(
                        InvoiceActivity.EXTRA_SERVICE,
                        booking.serviceTitle
                );

                intent.putExtra(
                        InvoiceActivity.EXTRA_RATE,
                        "Rs. 0"
                );

                intent.putExtra(
                        InvoiceActivity.EXTRA_BOOKING_ID,
                        booking.bookingId
                );

                startActivity(intent);
            });

            buttonRow.addView(
                    btnInvoice
            );
        }

        card.addView(buttonRow);

        return card;
    }

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int)
                (value * density);
    }
}
