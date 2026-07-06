package com.example.taptask;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView btnManageUsers, btnManageWorkers, btnManageBookings, btnManageAreas;
    private LinearLayout recentActivityContainer;
    private LinearLayout pendingWorkersContainer;

    // Simple model for pending worker verification
    private static class PendingWorker {
        String name, title, area;
        PendingWorker(String name, String title, String area) {
            this.name = name;
            this.title = title;
            this.area = area;
        }
    }

    private List<PendingWorker> pendingWorkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        bindViews();
        loadRecentActivity();
        loadPendingWorkers();
        setupClicks();
    }

    private void bindViews() {
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageWorkers = findViewById(R.id.btnManageWorkers);
        btnManageBookings = findViewById(R.id.btnManageBookings);
        btnManageAreas = findViewById(R.id.btnManageAreas);
        recentActivityContainer = findViewById(R.id.recentActivityContainer);
        pendingWorkersContainer = findViewById(R.id.pendingWorkersContainer);
    }

    private void setupClicks() {
        btnManageUsers.setOnClickListener(v ->
                Toast.makeText(this, "User list view (connects to backend later)", Toast.LENGTH_SHORT).show());

        btnManageWorkers.setOnClickListener(v ->
                Toast.makeText(this, "Worker list view (connects to backend later)", Toast.LENGTH_SHORT).show());

        btnManageBookings.setOnClickListener(v ->
                Toast.makeText(this, "All bookings view (connects to backend later)", Toast.LENGTH_SHORT).show());

        btnManageAreas.setOnClickListener(v ->
                Toast.makeText(this, "Areas: Tench Bhatta, Saddar", Toast.LENGTH_SHORT).show());
    }

    private void loadRecentActivity() {
        recentActivityContainer.removeAllViews();

        String[] activities = {
                "New booking: Usman Tariq (Electrician) — Tench Bhatta",
                "New worker registered: Hira Textiles (Tailor)",
                "Booking completed: Nasreen Bibi (Cleaner)",
                "New user signed up: Ahmed Khan"
        };

        for (String activity : activities) {
            TextView tv = new TextView(this);
            tv.setText("• " + activity);
            tv.setTextColor(getResources().getColor(R.color.muted));
            tv.setTextSize(13);
            tv.setPadding(0, dp(6), 0, dp(6));
            recentActivityContainer.addView(tv);
        }
    }

    private void loadPendingWorkers() {
        pendingWorkers = new ArrayList<>();
        pendingWorkers.add(new PendingWorker("Faisal Nadeem", "Electrician", "Tench Bhatta"));
        pendingWorkers.add(new PendingWorker("Sundas Iqbal", "Home Cleaner", "Saddar"));
        pendingWorkers.add(new PendingWorker("Waqas Ahmed", "Mechanic", "Tench Bhatta"));

        renderPendingWorkers();
    }

    private void renderPendingWorkers() {
        pendingWorkersContainer.removeAllViews();

        if (pendingWorkers.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No pending verifications 🎉");
            emptyText.setTextColor(getResources().getColor(R.color.muted));
            emptyText.setTextSize(13);
            emptyText.setPadding(0, dp(8), 0, dp(8));
            pendingWorkersContainer.addView(emptyText);
            return;
        }

        for (PendingWorker worker : pendingWorkers) {
            pendingWorkersContainer.addView(createPendingWorkerCard(worker));
        }
    }

    private LinearLayout createPendingWorkerCard(PendingWorker worker) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);

        TextView name = new TextView(this);
        name.setText(worker.name);
        name.setTextColor(getResources().getColor(R.color.text_main));
        name.setTextSize(15);
        name.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(name);

        TextView details = new TextView(this);
        details.setText(worker.title + "  •  📍 " + worker.area);
        details.setTextColor(getResources().getColor(R.color.muted));
        details.setTextSize(12);
        LinearLayout.LayoutParams detailsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        detailsParams.topMargin = dp(4);
        detailsParams.bottomMargin = dp(12);
        details.setLayoutParams(detailsParams);
        card.addView(details);

        // Buttons row: Verify + Reject
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView btnVerify = new TextView(this);
        btnVerify.setText("✅ Verify");
        btnVerify.setTextColor(getResources().getColor(R.color.white));
        btnVerify.setTextSize(13);
        btnVerify.setTypeface(null, android.graphics.Typeface.BOLD);
        btnVerify.setGravity(android.view.Gravity.CENTER);
        btnVerify.setBackgroundResource(R.drawable.bg_btn_green);
        btnVerify.setPadding(dp(12), dp(10), dp(12), dp(10));
        LinearLayout.LayoutParams verifyParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        verifyParams.setMargins(0, 0, dp(8), 0);
        btnVerify.setLayoutParams(verifyParams);
        btnVerify.setOnClickListener(v -> {
            Toast.makeText(this, worker.name + " verified ✅", Toast.LENGTH_SHORT).show();
            pendingWorkers.remove(worker);
            renderPendingWorkers();
        });
        btnRow.addView(btnVerify);

        TextView btnReject = new TextView(this);
        btnReject.setText("❌ Reject");
        btnReject.setTextColor(getResources().getColor(R.color.white));
        btnReject.setTextSize(13);
        btnReject.setTypeface(null, android.graphics.Typeface.BOLD);
        btnReject.setGravity(android.view.Gravity.CENTER);
        btnReject.setBackgroundResource(R.drawable.bg_btn_red);
        btnReject.setPadding(dp(12), dp(10), dp(12), dp(10));
        LinearLayout.LayoutParams rejectParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnReject.setLayoutParams(rejectParams);
        btnReject.setOnClickListener(v -> {
            Toast.makeText(this, worker.name + " rejected ❌", Toast.LENGTH_SHORT).show();
            pendingWorkers.remove(worker);
            renderPendingWorkers();
        });
        btnRow.addView(btnReject);

        card.addView(btnRow);

        return card;
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }
}