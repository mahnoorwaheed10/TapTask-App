package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class WorkerDashboardActivity extends AppCompatActivity {

    private TextView btnOnlineToggle;
    private boolean isOnline = true;

    private LinearLayout incomingRequestsContainer;
    private LinearLayout activeJobsContainer;

    private List<WorkerRequestItem> incomingRequests = new ArrayList<>();
    private List<WorkerRequestItem> activeJobs = new ArrayList<>();

    // Bottom nav
    private LinearLayout navDashboard, navChat, navProfile;
    private ScrollView tabDashboard, tabProfile;
    private LinearLayout tabChat;

    // Profile tab views
    private EditText etWorkerName, etWorkerSpecialization, etWorkerPhone, etWorkerArea, etWorkerRate;
    private Button btnSaveWorkerProfile, btnWorkerLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_dashboard);

        bindViews();
        loadMockData();
        renderIncomingRequests();
        renderActiveJobs();
        setupBottomNav();

        btnOnlineToggle.setOnClickListener(v -> toggleOnlineStatus());

        btnSaveWorkerProfile.setOnClickListener(v ->
                Toast.makeText(this, "Profile updated! (Will sync with backend once connected)", Toast.LENGTH_SHORT).show());

        btnWorkerLogout.setOnClickListener(v -> {
            Intent intent = new Intent(WorkerDashboardActivity.this, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void bindViews() {
        btnOnlineToggle = findViewById(R.id.btnOnlineToggle);
        incomingRequestsContainer = findViewById(R.id.incomingRequestsContainer);
        activeJobsContainer = findViewById(R.id.activeJobsContainer);

        navDashboard = findViewById(R.id.navDashboard);
        navChat = findViewById(R.id.navChat);
        navProfile = findViewById(R.id.navProfile);

        tabDashboard = findViewById(R.id.tabDashboard);
        tabChat = findViewById(R.id.tabChat);
        tabProfile = findViewById(R.id.tabProfile);

        etWorkerName = findViewById(R.id.etWorkerName);
        etWorkerSpecialization = findViewById(R.id.etWorkerSpecialization);
        etWorkerPhone = findViewById(R.id.etWorkerPhone);
        etWorkerArea = findViewById(R.id.etWorkerArea);
        etWorkerRate = findViewById(R.id.etWorkerRate);
        btnSaveWorkerProfile = findViewById(R.id.btnSaveWorkerProfile);
        btnWorkerLogout = findViewById(R.id.btnWorkerLogout);
    }

    private void setupBottomNav() {
        navDashboard.setOnClickListener(v -> switchTab(0));
        navChat.setOnClickListener(v -> switchTab(1));
        navProfile.setOnClickListener(v -> switchTab(2));
        switchTab(0);
    }

    private void switchTab(int index) {
        tabDashboard.setVisibility(index == 0 ? View.VISIBLE : View.GONE);
        tabChat.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
        tabProfile.setVisibility(index == 2 ? View.VISIBLE : View.GONE);

        updateNavLabel(navDashboard, index == 0);
        updateNavLabel(navChat, index == 1);
        updateNavLabel(navProfile, index == 2);
    }

    private void updateNavLabel(LinearLayout nav, boolean active) {
        TextView label = (TextView) nav.getChildAt(1);
        label.setTextColor(getResources().getColor(active ? R.color.primary : R.color.muted));
        if (active) {
            label.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            label.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    private void toggleOnlineStatus() {
        isOnline = !isOnline;
        btnOnlineToggle.setText(isOnline ? "🟢 Online" : "🔴 Busy");
        btnOnlineToggle.setTextColor(getResources().getColor(isOnline ? R.color.green : R.color.red));
    }

    private void loadMockData() {
        incomingRequests.add(new WorkerRequestItem("Ahmed Khan", "Electrical wiring repair", "Tench Bhatta", "5 July, 10:00 AM"));
        incomingRequests.add(new WorkerRequestItem("Sara Malik", "UPS installation", "Saddar", "6 July, 2:00 PM"));
        activeJobs.add(new WorkerRequestItem("Bilal Ahmed", "Fan installation", "Tench Bhatta", "In Progress"));
    }

    private void renderIncomingRequests() {
        incomingRequestsContainer.removeAllViews();
        if (incomingRequests.isEmpty()) {
            incomingRequestsContainer.addView(createEmptyText("No incoming requests right now."));
            return;
        }
        for (WorkerRequestItem item : incomingRequests) {
            incomingRequestsContainer.addView(createIncomingRequestCard(item));
        }
    }

    private void renderActiveJobs() {
        activeJobsContainer.removeAllViews();
        if (activeJobs.isEmpty()) {
            activeJobsContainer.addView(createEmptyText("No active jobs right now."));
            return;
        }
        for (WorkerRequestItem item : activeJobs) {
            activeJobsContainer.addView(createActiveJobCard(item));
        }
    }

    private TextView createEmptyText(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.muted));
        tv.setTextSize(13);
        tv.setGravity(android.view.Gravity.CENTER);
        tv.setPadding(0, dp(12), 0, dp(12));
        return tv;
    }

    private LinearLayout createIncomingRequestCard(WorkerRequestItem item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);

        TextView name = new TextView(this);
        name.setText(item.customerName);
        name.setTextColor(getResources().getColor(R.color.text_main));
        name.setTextSize(15);
        name.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(name);

        TextView desc = new TextView(this);
        desc.setText(item.description);
        desc.setTextColor(getResources().getColor(R.color.muted));
        desc.setTextSize(13);
        card.addView(desc);

        TextView details = new TextView(this);
        details.setText("📍 " + item.area + "  •  🕐 " + item.timing);
        details.setTextColor(getResources().getColor(R.color.muted));
        details.setTextSize(12);
        LinearLayout.LayoutParams detailsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        detailsParams.topMargin = dp(6);
        detailsParams.bottomMargin = dp(12);
        details.setLayoutParams(detailsParams);
        card.addView(details);

        LinearLayout buttonsRow = new LinearLayout(this);
        buttonsRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView btnAccept = new TextView(this);
        btnAccept.setText(getString(R.string.accept));
        btnAccept.setTextColor(getResources().getColor(R.color.white));
        btnAccept.setTextSize(13);
        btnAccept.setGravity(android.view.Gravity.CENTER);
        btnAccept.setBackgroundResource(R.drawable.bg_btn_green);
        btnAccept.setPadding(dp(12), dp(10), dp(12), dp(10));
        LinearLayout.LayoutParams acceptParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        acceptParams.setMarginEnd(dp(8));
        btnAccept.setLayoutParams(acceptParams);
        btnAccept.setOnClickListener(v -> {
            Toast.makeText(this, "Request accepted! Moved to Active Jobs.", Toast.LENGTH_SHORT).show();
            incomingRequests.remove(item);
            activeJobs.add(item);
            renderIncomingRequests();
            renderActiveJobs();
        });
        buttonsRow.addView(btnAccept);

        TextView btnDecline = new TextView(this);
        btnDecline.setText(getString(R.string.decline));
        btnDecline.setTextColor(getResources().getColor(R.color.white));
        btnDecline.setTextSize(13);
        btnDecline.setGravity(android.view.Gravity.CENTER);
        btnDecline.setBackgroundResource(R.drawable.bg_btn_red);
        btnDecline.setPadding(dp(12), dp(10), dp(12), dp(10));
        LinearLayout.LayoutParams declineParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnDecline.setLayoutParams(declineParams);
        btnDecline.setOnClickListener(v -> {
            Toast.makeText(this, "Request declined.", Toast.LENGTH_SHORT).show();
            incomingRequests.remove(item);
            renderIncomingRequests();
        });
        buttonsRow.addView(btnDecline);

        card.addView(buttonsRow);
        return card;
    }

    private LinearLayout createActiveJobCard(WorkerRequestItem item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);

        TextView name = new TextView(this);
        name.setText(item.customerName);
        name.setTextColor(getResources().getColor(R.color.text_main));
        name.setTextSize(15);
        name.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(name);

        TextView desc = new TextView(this);
        desc.setText(item.description + " — " + item.timing);
        desc.setTextColor(getResources().getColor(R.color.muted));
        desc.setTextSize(13);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descParams.bottomMargin = dp(12);
        desc.setLayoutParams(descParams);
        card.addView(desc);

        LinearLayout buttonsRow = new LinearLayout(this);
        buttonsRow.setOrientation(LinearLayout.HORIZONTAL);

        TextView btnChat = new TextView(this);
        btnChat.setText("💬 Chat");
        btnChat.setTextColor(getResources().getColor(R.color.primary));
        btnChat.setTextSize(13);
        btnChat.setGravity(android.view.Gravity.CENTER);
        btnChat.setBackgroundResource(R.drawable.bg_btn_outline);
        btnChat.setPadding(dp(12), dp(10), dp(12), dp(10));
        LinearLayout.LayoutParams chatParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        chatParams.setMarginEnd(dp(8));
        btnChat.setLayoutParams(chatParams);
        btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(WorkerDashboardActivity.this, ChatActivity.class);
            intent.putExtra(ChatActivity.EXTRA_WORKER_NAME, item.customerName);
            startActivity(intent);
        });
        buttonsRow.addView(btnChat);

        TextView btnMarkDone = new TextView(this);
        btnMarkDone.setText("✅ Mark Done");
        btnMarkDone.setTextColor(getResources().getColor(R.color.white));
        btnMarkDone.setTextSize(13);
        btnMarkDone.setGravity(android.view.Gravity.CENTER);
        btnMarkDone.setBackgroundResource(R.drawable.bg_btn_green);
        btnMarkDone.setPadding(dp(12), dp(10), dp(12), dp(10));
        LinearLayout.LayoutParams doneParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnMarkDone.setLayoutParams(doneParams);
        btnMarkDone.setOnClickListener(v -> {
            Toast.makeText(this, "Job marked as completed!", Toast.LENGTH_SHORT).show();
            activeJobs.remove(item);
            renderActiveJobs();
        });
        buttonsRow.addView(btnMarkDone);

        card.addView(buttonsRow);
        return card;
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }
}