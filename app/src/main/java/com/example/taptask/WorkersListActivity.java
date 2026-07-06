package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class WorkersListActivity extends AppCompatActivity {

    public static final String EXTRA_SUB_CAT = "sub_cat";
    public static final String EXTRA_SUB_CAT_TITLE = "sub_cat_title";

    private LinearLayout workersContainer;
    private TextView tvCategoryTitle;
    private TextView btnBack;
    private String subCatKey;

    // Filter pills
    private TextView pillAll, pillTench, pillSaddar;
    private String selectedArea = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workers_list);

        workersContainer = findViewById(R.id.workersContainer);
        tvCategoryTitle = findViewById(R.id.tvCategoryTitle);
        btnBack = findViewById(R.id.btnBack);

        pillAll = findViewById(R.id.pillAll);
        pillTench = findViewById(R.id.pillTench);
        pillSaddar = findViewById(R.id.pillSaddar);

        subCatKey = getIntent().getStringExtra(EXTRA_SUB_CAT);
        String subCatTitle = getIntent().getStringExtra(EXTRA_SUB_CAT_TITLE);

        if (subCatKey == null) subCatKey = "electrician";
        if (subCatTitle == null) subCatTitle = "Workers";

        tvCategoryTitle.setText(subCatTitle);

        setupFilterPills();
        loadWorkers();

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupFilterPills() {
        pillAll.setOnClickListener(v -> selectArea("All"));
        pillTench.setOnClickListener(v -> selectArea("Tench Bhatta"));
        pillSaddar.setOnClickListener(v -> selectArea("Saddar"));
    }

    private void selectArea(String area) {
        selectedArea = area;

        // Reset all pills to inactive
        pillAll.setBackgroundResource(R.drawable.bg_role_btn_inactive);
        pillTench.setBackgroundResource(R.drawable.bg_role_btn_inactive);
        pillSaddar.setBackgroundResource(R.drawable.bg_role_btn_inactive);

        // Highlight selected pill
        if (area.equals("All")) {
            pillAll.setBackgroundResource(R.drawable.bg_role_btn_active);
        } else if (area.equals("Tench Bhatta")) {
            pillTench.setBackgroundResource(R.drawable.bg_role_btn_active);
        } else if (area.equals("Saddar")) {
            pillSaddar.setBackgroundResource(R.drawable.bg_role_btn_active);
        }

        loadWorkers();
    }

    private List<WorkerData> getWorkers() {
        List<WorkerData> list = new ArrayList<>();

        switch (subCatKey) {
            case "electrician":
                list.add(new WorkerData("Usman Tariq", "Certified Electrician", "Tench Bhatta", "7 years", "Rs. 800/visit", 4.9f, 142, true));
                list.add(new WorkerData("Bilal Hussain", "Electrician", "Saddar", "4 years", "Rs. 600/visit", 4.6f, 89, false));
                list.add(new WorkerData("Zaid Malik", "Senior Electrician", "Tench Bhatta", "5 years", "Rs. 700/visit", 4.7f, 113, true));
                break;
            case "plumber":
                list.add(new WorkerData("Kashif Raza", "Master Plumber", "Saddar", "8 years", "Rs. 700/visit", 4.8f, 201, true));
                list.add(new WorkerData("Imran Shah", "Plumber", "Tench Bhatta", "3 years", "Rs. 500/visit", 4.5f, 67, false));
                list.add(new WorkerData("Rizwan Ahmed", "Senior Plumber", "Saddar", "6 years", "Rs. 650/visit", 4.7f, 98, true));
                break;
            case "cleaner":
                list.add(new WorkerData("Nasreen Bibi", "Deep Cleaning Expert", "Tench Bhatta", "5 years", "Rs. 1,200/visit", 4.9f, 178, true));
                list.add(new WorkerData("Sadia Noor", "Home Cleaner", "Saddar", "2 years", "Rs. 900/visit", 4.4f, 54, true));
                list.add(new WorkerData("Rubina Kausar", "Professional Cleaner", "Tench Bhatta", "4 years", "Rs. 1,000/visit", 4.6f, 91, false));
                break;
            case "tailor":
                list.add(new WorkerData("Hira Textiles", "Master Tailor", "Saddar", "10 years", "Rs. 500/suit", 4.9f, 312, true));
                list.add(new WorkerData("Malik Darzi", "Tailor", "Saddar", "7 years", "Rs. 400/suit", 4.7f, 189, false));
                list.add(new WorkerData("Zara Stitches", "Fashion Tailor", "Tench Bhatta", "5 years", "Rs. 600/suit", 4.8f, 143, true));
                break;
            case "mechanic":
                list.add(new WorkerData("Ali Auto Works", "Senior Mechanic", "Tench Bhatta", "12 years", "Rs. 1,000/visit", 4.8f, 267, true));
                list.add(new WorkerData("Tariq Motors", "Auto Mechanic", "Saddar", "9 years", "Rs. 900/visit", 4.6f, 198, false));
                list.add(new WorkerData("Pak Garage", "General Mechanic", "Tench Bhatta", "6 years", "Rs. 800/visit", 4.5f, 112, true));
                break;
            case "tutor_shop":
                list.add(new WorkerData("Sir Hamid Khan", "Senior Tutor", "Saddar", "8 years", "Rs. 1,500/hr", 4.9f, 234, true));
                list.add(new WorkerData("Miss Ayesha Naz", "English & Biology Tutor", "Tench Bhatta", "5 years", "Rs. 1,200/hr", 4.8f, 167, true));
                list.add(new WorkerData("Sir Faisal Qureshi", "Entry Test Specialist", "Saddar", "10 years", "Rs. 1,800/hr", 4.9f, 289, false));
                break;
            case "home_tutor":
                list.add(new WorkerData("Sir Junaid Ali", "Online Math & Science Tutor", "Online", "6 years", "Rs. 800/hr", 4.9f, 198, true));
                list.add(new WorkerData("Miss Sana Fatima", "Online Language Tutor", "Online", "4 years", "Rs. 700/hr", 4.7f, 134, true));
                list.add(new WorkerData("Sir Asad Iqbal", "IB & Cambridge Tutor", "Online", "7 years", "Rs. 1,000/hr", 4.8f, 211, false));
                break;
            case "consultation":
                list.add(new WorkerData("Dr. Amna Siddiqui", "General Physician (Online)", "Online", "9 years", "Rs. 2,000/session", 5.0f, 445, true));
                list.add(new WorkerData("Dr. Rehan Baig", "Nutritionist", "Online", "11 years", "Rs. 2,500/session", 4.9f, 312, true));
                list.add(new WorkerData("Adv. Sara Malik", "Legal Consultant", "Online", "6 years", "Rs. 3,000/session", 4.8f, 178, false));
                break;
            case "freelancer":
                list.add(new WorkerData("Hamza Dev", "Full Stack Developer", "Online", "5 years", "Rs. 5,000/project", 4.9f, 156, true));
                list.add(new WorkerData("Zainab Designs", "Graphic Designer", "Online", "4 years", "Rs. 3,000/project", 4.8f, 223, false));
                list.add(new WorkerData("Ali Content", "Content Writer & SEO", "Online", "3 years", "Rs. 2,000/project", 4.7f, 89, true));
                break;
        }
        return list;
    }

    private void loadWorkers() {
        workersContainer.removeAllViews();

        List<WorkerData> allWorkers = getWorkers();
        List<WorkerData> filteredList = new ArrayList<>();

        for (WorkerData worker : allWorkers) {
            if (selectedArea.equals("All") || worker.area.equals(selectedArea)) {
                filteredList.add(worker);
            }
        }

        if (filteredList.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No workers found in " + selectedArea);
            emptyText.setTextColor(getResources().getColor(R.color.muted));
            emptyText.setTextSize(14);
            emptyText.setPadding(0, dp(20), 0, 0);
            emptyText.setGravity(android.view.Gravity.CENTER);
            workersContainer.addView(emptyText);
            return;
        }

        for (WorkerData worker : filteredList) {
            workersContainer.addView(createWorkerCard(worker));
        }
    }

    private LinearLayout createWorkerCard(WorkerData worker) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);

        // Name + Status badge row
        LinearLayout nameRow = new LinearLayout(this);
        nameRow.setOrientation(LinearLayout.HORIZONTAL);
        nameRow.setGravity(android.view.Gravity.CENTER_VERTICAL);

        TextView name = new TextView(this);
        name.setText(worker.name);
        name.setTextColor(getResources().getColor(R.color.text_main));
        name.setTextSize(16);
        name.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        name.setLayoutParams(nameParams);
        nameRow.addView(name);

        TextView statusBadge = new TextView(this);
        if (worker.isAvailable) {
            statusBadge.setText("🟢 Available");
            statusBadge.setTextColor(getResources().getColor(R.color.green));
        } else {
            statusBadge.setText("🔴 Busy");
            statusBadge.setTextColor(getResources().getColor(R.color.red));
        }
        statusBadge.setTextSize(12);
        statusBadge.setTypeface(null, android.graphics.Typeface.BOLD);
        nameRow.addView(statusBadge);

        card.addView(nameRow);

        TextView title = new TextView(this);
        title.setText(worker.title);
        title.setTextColor(getResources().getColor(R.color.primary));
        title.setTextSize(13);
        card.addView(title);

        TextView details = new TextView(this);
        details.setText("📍 " + worker.area + "  •  " + worker.experience + "  •  ⭐ " + worker.rating + " (" + worker.reviews + ")");
        details.setTextColor(getResources().getColor(R.color.muted));
        details.setTextSize(12);
        LinearLayout.LayoutParams detailsParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        detailsParams.topMargin = dp(6);
        details.setLayoutParams(detailsParams);
        card.addView(details);

        TextView rate = new TextView(this);
        rate.setText(worker.rate);
        rate.setTextColor(getResources().getColor(R.color.green));
        rate.setTextSize(14);
        rate.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams rateParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rateParams.topMargin = dp(8);
        rateParams.bottomMargin = dp(12);
        rate.setLayoutParams(rateParams);
        card.addView(rate);

        // Buttons row: View Profile + Send Request
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams btnRowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnRow.setLayoutParams(btnRowParams);

        TextView btnViewProfile = new TextView(this);
        btnViewProfile.setText("👁 View Profile");
        btnViewProfile.setTextColor(getResources().getColor(R.color.primary));
        btnViewProfile.setTextSize(13);
        btnViewProfile.setTypeface(null, android.graphics.Typeface.BOLD);
        btnViewProfile.setBackgroundResource(R.drawable.bg_btn_outline);
        btnViewProfile.setGravity(android.view.Gravity.CENTER);
        btnViewProfile.setPadding(dp(12), dp(10), dp(12), dp(10));
        LinearLayout.LayoutParams viewProfileParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        viewProfileParams.setMargins(0, 0, dp(8), 0);
        btnViewProfile.setLayoutParams(viewProfileParams);
        btnViewProfile.setOnClickListener(v -> {
            Intent intent = new Intent(WorkersListActivity.this, WorkerProfileViewActivity.class);
            intent.putExtra(WorkerProfileViewActivity.EXTRA_WORKER_NAME, worker.name);
            intent.putExtra(WorkerProfileViewActivity.EXTRA_WORKER_TITLE, worker.title);
            intent.putExtra(WorkerProfileViewActivity.EXTRA_WORKER_RATING, worker.rating);
            intent.putExtra(WorkerProfileViewActivity.EXTRA_WORKER_EXP, worker.experience);
            intent.putExtra(WorkerProfileViewActivity.EXTRA_WORKER_RATE, worker.rate);
            intent.putExtra(WorkerProfileViewActivity.EXTRA_WORKER_BIO, worker.title + " with " + worker.experience + " of experience.");
            intent.putExtra(WorkerProfileViewActivity.EXTRA_WORKER_AREA, worker.area);
            startActivity(intent);
        });
        btnRow.addView(btnViewProfile);

        // Send Request button
        TextView btnSendRequest = new TextView(this);
        btnSendRequest.setText("📅 Send Request");
        btnSendRequest.setTextSize(13);
        btnSendRequest.setTypeface(null, android.graphics.Typeface.BOLD);
        btnSendRequest.setGravity(android.view.Gravity.CENTER);
        btnSendRequest.setPadding(dp(12), dp(10), dp(12), dp(10));
        LinearLayout.LayoutParams sendReqParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnSendRequest.setLayoutParams(sendReqParams);

        if (worker.isAvailable) {
            btnSendRequest.setBackgroundResource(R.drawable.bg_btn_primary);
            btnSendRequest.setTextColor(getResources().getColor(R.color.white));
            btnSendRequest.setOnClickListener(v ->
                    Toast.makeText(this, "Request sent to " + worker.name, Toast.LENGTH_SHORT).show());
        } else {
            btnSendRequest.setBackgroundResource(R.drawable.bg_btn_outline);
            btnSendRequest.setTextColor(getResources().getColor(R.color.muted));
            btnSendRequest.setOnClickListener(v ->
                    Toast.makeText(this, worker.name + " is currently busy", Toast.LENGTH_SHORT).show());
        }
        btnRow.addView(btnSendRequest);

        card.addView(btnRow);

        return card;
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }
}