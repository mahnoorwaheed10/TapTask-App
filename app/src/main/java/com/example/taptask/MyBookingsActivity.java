package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private LinearLayout bookingsContainer;
    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        bookingsContainer = findViewById(R.id.bookingsContainer);
        btnBack = findViewById(R.id.btnBack);

        loadBookings();

        btnBack.setOnClickListener(v -> finish());
    }

    private List<BookingDisplayData> getMockBookings() {
        List<BookingDisplayData> list = new ArrayList<>();
        list.add(new BookingDisplayData("Usman Tariq", "Electrician", "5 July 2026", "Pending", "#F59E0B"));
        list.add(new BookingDisplayData("Hira Textiles", "Tailor", "2 July 2026", "Confirmed", "#3B82F6"));
        list.add(new BookingDisplayData("Nasreen Bibi", "Cleaner", "28 June 2026", "Completed", "#10B981"));
        return list;
    }

    private void loadBookings() {
        bookingsContainer.removeAllViews();

        List<BookingDisplayData> bookings = getMockBookings();

        if (bookings.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("You have no bookings yet.");
            empty.setTextColor(getResources().getColor(R.color.muted));
            empty.setTextSize(14);
            empty.setGravity(android.view.Gravity.CENTER);
            bookingsContainer.addView(empty);
            return;
        }

        for (BookingDisplayData booking : bookings) {
            bookingsContainer.addView(createBookingCard(booking));
        }
    }

    private LinearLayout createBookingCard(BookingDisplayData booking) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

        LinearLayout nameContainer = new LinearLayout(this);
        nameContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        nameContainer.setLayoutParams(nameParams);

        TextView name = new TextView(this);
        name.setText(booking.workerName);
        name.setTextColor(getResources().getColor(R.color.text_main));
        name.setTextSize(16);
        name.setTypeface(null, android.graphics.Typeface.BOLD);
        nameContainer.addView(name);

        TextView service = new TextView(this);
        service.setText(booking.serviceTitle);
        service.setTextColor(getResources().getColor(R.color.muted));
        service.setTextSize(13);
        nameContainer.addView(service);

        topRow.addView(nameContainer);

        TextView statusBadge = new TextView(this);
        statusBadge.setText(booking.status);
        statusBadge.setTextSize(11);
        statusBadge.setTypeface(null, android.graphics.Typeface.BOLD);
        statusBadge.setPadding(dp(10), dp(5), dp(10), dp(5));
        statusBadge.setTextColor(android.graphics.Color.parseColor(booking.statusColor));
        topRow.addView(statusBadge);

        card.addView(topRow);

        TextView date = new TextView(this);
        date.setText("📅 " + booking.date);
        date.setTextColor(getResources().getColor(R.color.muted));
        date.setTextSize(12);
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dateParams.topMargin = dp(8);
        date.setLayoutParams(dateParams);
        card.addView(date);

        // Status-based navigation:
        // Pending / Confirmed -> Chat
        // Completed -> Invoice (directly)
        card.setOnClickListener(v -> {
            if (booking.status.equals("Completed")) {
                Intent intent = new Intent(MyBookingsActivity.this, InvoiceActivity.class);
                intent.putExtra(InvoiceActivity.EXTRA_WORKER_NAME, booking.workerName);
                intent.putExtra(InvoiceActivity.EXTRA_SERVICE, booking.serviceTitle);
                intent.putExtra(InvoiceActivity.EXTRA_RATE, "Rs. 0");
                startActivity(intent);
            } else {
                Intent intent = new Intent(MyBookingsActivity.this, ChatActivity.class);
                intent.putExtra(ChatActivity.EXTRA_WORKER_NAME, booking.workerName);
                startActivity(intent);
            }
        });

        return card;
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }
}