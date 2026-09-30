package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WorkersListActivity extends AppCompatActivity {

    // ============================================================
    // INTENT EXTRAS
    // ============================================================

    public static final String EXTRA_SUB_CAT = "sub_cat";
    public static final String EXTRA_SUB_CAT_TITLE = "sub_cat_title";

    // NEW: Home / Shop / Online
    public static final String EXTRA_MAIN_CAT = "main_cat";

    // ============================================================
    // VIEWS
    // ============================================================

    private LinearLayout workersContainer;
    private TextView tvCategoryTitle;
    private TextView btnBack;

    private TextView pillAll;
    private TextView pillTench;
    private TextView pillSaddar;

    // ============================================================
    // AREA
    // ============================================================

    private String selectedArea = "All";

    // ============================================================
    // FIREBASE
    // ============================================================

    private FirebaseFirestore db;

    // ============================================================
    // CATEGORY
    // ============================================================

    private String subCatKey;
    private String subCatTitle;

    // NEW: Home / Shop / Online
    private String mainCatKey;

    // ============================================================
    // ON CREATE
    // ============================================================

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

        db = FirebaseFirestore.getInstance();

        // ========================================================
        // GET CATEGORY
        // ========================================================

        subCatKey =
                getIntent().getStringExtra(EXTRA_SUB_CAT);

        subCatTitle =
                getIntent().getStringExtra(EXTRA_SUB_CAT_TITLE);

        // NEW:
        // Home / Shop / Online receive karo
        mainCatKey =
                getIntent().getStringExtra(EXTRA_MAIN_CAT);

        if (subCatKey == null ||
                subCatKey.trim().isEmpty()) {

            subCatKey = "electrician";
        }

        if (subCatTitle == null ||
                subCatTitle.trim().isEmpty()) {

            subCatTitle = "Workers";
        }

        // NEW:
        // Agar kisi purane flow se main category na aaye
        // to Home default rahega.
        if (mainCatKey == null ||
                mainCatKey.trim().isEmpty()) {

            mainCatKey = "home";
        }

        tvCategoryTitle.setText(subCatTitle);

        // ========================================================
        // SETUP
        // ========================================================

        setupFilterPills();

        btnBack.setOnClickListener(
                v -> finish()
        );

        // ========================================================
        // DEFAULT AREA
        // ========================================================

        selectArea("All");
    }

    // ============================================================
    // AREA FILTERS
    // ============================================================

    private void setupFilterPills() {

        pillAll.setOnClickListener(
                v -> selectArea("All")
        );

        pillTench.setOnClickListener(
                v -> selectArea("Tench Bhatta")
        );

        pillSaddar.setOnClickListener(
                v -> selectArea("Saddar")
        );
    }

    private void selectArea(String area) {

        selectedArea = area;

        // RESET ALL
        pillAll.setBackgroundResource(
                R.drawable.bg_role_btn_inactive
        );

        pillTench.setBackgroundResource(
                R.drawable.bg_role_btn_inactive
        );

        pillSaddar.setBackgroundResource(
                R.drawable.bg_role_btn_inactive
        );

        // SELECTED
        if ("Tench Bhatta".equalsIgnoreCase(area)) {

            pillTench.setBackgroundResource(
                    R.drawable.bg_role_btn_active
            );

        } else if ("Saddar".equalsIgnoreCase(area)) {

            pillSaddar.setBackgroundResource(
                    R.drawable.bg_role_btn_active
            );

        } else {

            pillAll.setBackgroundResource(
                    R.drawable.bg_role_btn_active
            );
        }

        loadWorkersFromFirebase();
    }

    // ============================================================
    // LOAD WORKERS FROM FIREBASE
    // ============================================================

    private void loadWorkersFromFirebase() {

        workersContainer.removeAllViews();

        TextView loading = new TextView(this);

        loading.setText("Loading workers...");
        loading.setTextSize(14);

        loading.setTextColor(
                getResources().getColor(R.color.muted)
        );

        loading.setGravity(Gravity.CENTER);

        loading.setPadding(
                0,
                dp(30),
                0,
                dp(30)
        );

        workersContainer.addView(loading);

        db.collection("workers")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    workersContainer.removeAllViews();

                    List<WorkerData> workers =
                            new ArrayList<>();

                    String selectedCategory =
                            normalizeCategory(subCatKey);

                    // ====================================================
                    // READ EVERY WORKER
                    // ====================================================

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        // ====================================================
                        // ADMIN VERIFICATION CHECK
                        // ====================================================

                        String verificationStatus =
                                getField(
                                        document,
                                        "verificationStatus"
                                );

                        /*
                         * Existing workers which do not have this field
                         * are treated as already verified.
                         *
                         * New workers are saved as "pending" by AuthActivity.
                         *
                         * Pending and rejected workers will NOT appear
                         * on customer side.
                         */

                        if (!verificationStatus.isEmpty() &&
                                !verificationStatus.equalsIgnoreCase("verified")) {

                            continue;
                        }

                        // ====================================================
                        // CATEGORY
                        // ====================================================

                        String category =
                                getField(
                                        document,
                                        "category",
                                        "serviceCategory",
                                        "service_category"
                                );

                        String subCategory =
                                getField(
                                        document,
                                        "subCategory",
                                        "subcategory",
                                        "sub_category"
                                );

                        category =
                                normalizeCategory(category);

                        subCategory =
                                normalizeCategory(subCategory);

                        // ====================================================
                        // CATEGORY MATCH
                        // ====================================================

                        boolean categoryMatches =
                                category.equals(selectedCategory)
                                        ||
                                        subCategory.equals(selectedCategory);

                        if (!categoryMatches) {
                            continue;
                        }

                        // ====================================================
                        // BASIC INFORMATION
                        // ====================================================

                        String name =
                                getField(
                                        document,
                                        "name"
                                );

                        String title =
                                getField(
                                        document,
                                        "title"
                                );

                        // ====================================================
                        // AREA
                        // ====================================================

                        String area =
                                getField(
                                        document,
                                        "area",
                                        "location",
                                        "serviceArea",
                                        "service_area"
                                );

                        // ====================================================
                        // EXPERIENCE
                        // ====================================================

                        String experience =
                                getField(
                                        document,
                                        "experience",
                                        "workExperience",
                                        "work_experience",
                                        "yearsExperience",
                                        "years_experience"
                                );

                        // ====================================================
                        // RATE
                        // ====================================================

                        String rate =
                                getField(
                                        document,
                                        "rate",
                                        "Rate",
                                        "RATE",
                                        "ratePerHour",
                                        "hourlyRate",
                                        "price"
                                );

                        // ====================================================
                        // DEFAULT VALUES
                        // ====================================================

                        if (name.isEmpty()) {
                            name = "Unknown Worker";
                        }

                        if (title.isEmpty()) {
                            title = "Worker";
                        }

                        if (area.isEmpty()) {
                            area = "Unknown";
                        }

                        // ====================================================
                        // AREA FILTER
                        // ====================================================

                        boolean areaMatches =
                                "All".equalsIgnoreCase(selectedArea)
                                        ||
                                        area.equalsIgnoreCase(selectedArea);

                        if (!areaMatches) {
                            continue;
                        }

                        // ====================================================
                        // RATING
                        // ====================================================

                        float rating =
                                getFloatField(
                                        document,
                                        "rating"
                                );

                        // ====================================================
                        // REVIEWS
                        // ====================================================

                        int reviews =
                                getIntField(
                                        document,
                                        "reviews"
                                );

                        // ====================================================
                        // AVAILABILITY
                        // ====================================================

                        boolean isAvailable =
                                getBooleanField(
                                        document,
                                        "isAvailable",
                                        "available",
                                        "availability"
                                );

                        // ====================================================
                        // ADD WORKER
                        // ====================================================

                        workers.add(
                                new WorkerData(
                                        name,
                                        title,
                                        area,
                                        experience,
                                        rate,
                                        rating,
                                        reviews,
                                        isAvailable
                                )
                        );
                    }

                    // ====================================================
                    // NO WORKERS
                    // ====================================================

                    if (workers.isEmpty()) {

                        showEmptyMessage();
                        return;
                    }

                    // ====================================================
                    // DISPLAY ALL VERIFIED WORKERS
                    // ====================================================

                    for (WorkerData worker : workers) {

                        workersContainer.addView(
                                createWorkerCard(worker)
                        );
                    }

                })
                .addOnFailureListener(e -> {

                    workersContainer.removeAllViews();

                    TextView errorText =
                            new TextView(this);

                    errorText.setText(
                            "Could not load workers"
                    );

                    errorText.setTextSize(14);

                    errorText.setTextColor(
                            getResources().getColor(
                                    R.color.red
                            )
                    );

                    errorText.setGravity(
                            Gravity.CENTER
                    );

                    errorText.setPadding(
                            0,
                            dp(30),
                            0,
                            dp(30)
                    );

                    workersContainer.addView(
                            errorText
                    );

                    Toast.makeText(
                            this,
                            "Firebase error: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // ============================================================
    // GET FIELD
    // ============================================================

    private String getField(
            DocumentSnapshot document,
            String... fieldNames
    ) {

        // FIRST: EXACT FIELD NAMES
        for (String fieldName : fieldNames) {

            Object value =
                    document.get(fieldName);

            if (value != null) {

                String result =
                        getFirestoreValueAsString(value);

                if (!result.isEmpty()) {
                    return result;
                }
            }
        }

        // SECOND: CASE-INSENSITIVE SEARCH
        Map<String, Object> data =
                document.getData();

        if (data != null) {

            for (String wantedField :
                    fieldNames) {

                for (Map.Entry<String, Object> entry :
                        data.entrySet()) {

                    String actualField =
                            entry.getKey();

                    if (actualField == null) {
                        continue;
                    }

                    if (actualField.trim()
                            .equalsIgnoreCase(
                                    wantedField.trim()
                            )) {

                        Object value =
                                entry.getValue();

                        if (value != null) {

                            String result =
                                    getFirestoreValueAsString(
                                            value
                                    );

                            if (!result.isEmpty()) {
                                return result;
                            }
                        }
                    }
                }
            }
        }

        return "";
    }

    // ============================================================
    // FIRESTORE VALUE → STRING
    // ============================================================

    private String getFirestoreValueAsString(
            Object value
    ) {

        if (value == null) {
            return "";
        }

        if (value instanceof Number) {

            Number number =
                    (Number) value;

            double doubleValue =
                    number.doubleValue();

            if (doubleValue ==
                    Math.floor(doubleValue)) {

                return String.valueOf(
                        (long) doubleValue
                );
            }

            return String.valueOf(
                    doubleValue
            ).trim();
        }

        return String.valueOf(value)
                .trim();
    }

    // ============================================================
    // FLOAT FIELD
    // ============================================================

    private float getFloatField(
            DocumentSnapshot document,
            String... fieldNames
    ) {

        String value =
                getField(
                        document,
                        fieldNames
                );

        if (value.isEmpty()) {
            return 0f;
        }

        try {

            return Float.parseFloat(
                    value.trim()
            );

        } catch (Exception e) {

            return 0f;
        }
    }

    // ============================================================
    // INTEGER FIELD
    // ============================================================

    private int getIntField(
            DocumentSnapshot document,
            String... fieldNames
    ) {

        String value =
                getField(
                        document,
                        fieldNames
                );

        if (value.isEmpty()) {
            return 0;
        }

        try {

            return Integer.parseInt(
                    value.trim()
            );

        } catch (Exception e) {

            try {

                return (int) Float.parseFloat(
                        value.trim()
                );

            } catch (Exception ignored) {

                return 0;
            }
        }
    }

    // ============================================================
    // BOOLEAN FIELD
    // ============================================================

    private boolean getBooleanField(
            DocumentSnapshot document,
            String... fieldNames
    ) {

        String value =
                getField(
                        document,
                        fieldNames
                );

        if (value.isEmpty()) {
            return false;
        }

        return Boolean.parseBoolean(
                value.trim()
        );
    }

    // ============================================================
    // CATEGORY NORMALIZATION
    // ============================================================

    private String normalizeCategory(
            String category
    ) {

        if (category == null) {
            return "";
        }

        String value =
                category
                        .trim()
                        .toLowerCase(Locale.US);

        value = value.replace(
                "&",
                "and"
        );

        value = value.replaceAll(
                "[^a-z0-9]+",
                "_"
        );

        value = value.replaceAll(
                "_+",
                "_"
        );

        if (value.startsWith("_")) {
            value = value.substring(1);
        }

        if (value.endsWith("_")) {
            value = value.substring(
                    0,
                    value.length() - 1
            );
        }

        // ========================================================
        // CATEGORY ALIASES
        // ========================================================

        if (value.equals("mechanic_shop")) {
            value = "mechanic";
        }

        if (value.equals("shop_mechanic")) {
            value = "mechanic";
        }

        if (value.equals("tutor")) {
            value = "tutor_shop";
        }

        if (value.equals("shop_tutor")) {
            value = "tutor_shop";
        }

        if (value.equals("home_tutoring")) {
            value = "home_tutor";
        }

        if (value.equals("online_tutor")) {
            value = "home_tutor";
        }

        if (value.equals("legal_consultation")) {
            value = "consultation";
        }

        if (value.equals("consultant")) {
            value = "consultation";
        }

        if (value.equals("freelancing")) {
            value = "freelancer";
        }

        if (value.equals("freelance")) {
            value = "freelancer";
        }

        return value;
    }

    // ============================================================
    // EMPTY MESSAGE
    // ============================================================

    private void showEmptyMessage() {

        TextView emptyText =
                new TextView(this);

        emptyText.setText(
                "No workers found for this category"
        );

        emptyText.setTextSize(15);

        emptyText.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        emptyText.setGravity(
                Gravity.CENTER
        );

        emptyText.setPadding(
                0,
                dp(40),
                0,
                dp(40)
        );

        workersContainer.addView(
                emptyText
        );
    }

    // ============================================================
    // WORKER CARD
    // ============================================================

    private LinearLayout createWorkerCard(
            WorkerData worker
    ) {

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
                dp(14);

        card.setLayoutParams(
                cardParams
        );

        // ========================================================
        // TOP ROW
        // ========================================================

        LinearLayout topRow =
                new LinearLayout(this);

        topRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        topRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        // ========================================================
        // NAME
        // ========================================================

        TextView name =
                new TextView(this);

        name.setText(
                worker.name
        );

        name.setTextSize(20);

        name.setTextColor(
                getResources().getColor(
                        R.color.text_main
                )
        );

        name.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        name.setLayoutParams(
                nameParams
        );

        topRow.addView(name);

        // ========================================================
        // AVAILABILITY
        // ========================================================

        TextView availability =
                new TextView(this);

        if (worker.isAvailable) {

            availability.setText(
                    "🟢 Available"
            );

            availability.setTextColor(
                    getResources().getColor(
                            R.color.green
                    )
            );

        } else {

            availability.setText(
                    "🔴 Busy"
            );

            availability.setTextColor(
                    getResources().getColor(
                            R.color.red
                    )
            );
        }

        availability.setTextSize(14);

        availability.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        topRow.addView(
                availability
        );

        card.addView(
                topRow
        );

        // ========================================================
        // TITLE
        // ========================================================

        TextView title =
                new TextView(this);

        title.setText(
                worker.title
        );

        title.setTextSize(16);

        title.setTextColor(
                getResources().getColor(
                        R.color.primary
                )
        );

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        titleParams.topMargin =
                dp(4);

        title.setLayoutParams(
                titleParams
        );

        card.addView(
                title
        );

        // ========================================================
        // INFO
        // ========================================================

        TextView info =
                new TextView(this);

        String experienceText;

        if (worker.experience == null ||
                worker.experience.trim().isEmpty()) {

            experienceText =
                    "Experience not specified";

        } else {

            experienceText =
                    worker.experience;
        }

        info.setText(
                "📍 " +
                        worker.area +
                        "  •  " +
                        experienceText +
                        "  •  ⭐ " +
                        worker.rating +
                        " (" +
                        worker.reviews +
                        ")"
        );

        info.setTextSize(14);

        info.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        infoParams.topMargin =
                dp(10);

        info.setLayoutParams(
                infoParams
        );

        card.addView(info);

        // ========================================================
        // RATE
        // ========================================================

        TextView rate =
                new TextView(this);

        if (worker.rate == null ||
                worker.rate.trim().isEmpty()) {

            rate.setText(
                    "Rate not specified"
            );

        } else {

            rate.setText(
                    "Rs. " +
                            worker.rate
            );
        }

        rate.setTextSize(16);

        rate.setTextColor(
                getResources().getColor(
                        R.color.green
                )
        );

        rate.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        LinearLayout.LayoutParams rateParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        rateParams.topMargin =
                dp(10);

        rate.setLayoutParams(
                rateParams
        );

        card.addView(rate);

        // ========================================================
        // BUTTON ROW
        // ========================================================

        LinearLayout buttonRow =
                new LinearLayout(this);

        buttonRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        buttonRow.setGravity(
                Gravity.CENTER_VERTICAL
        );

        LinearLayout.LayoutParams buttonRowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        buttonRowParams.topMargin =
                dp(14);

        buttonRow.setLayoutParams(
                buttonRowParams
        );

        // ========================================================
        // VIEW PROFILE BUTTON
        // ========================================================

        TextView viewProfile =
                new TextView(this);

        viewProfile.setText(
                "👁 View Profile"
        );

        viewProfile.setTextSize(15);

        viewProfile.setTextColor(
                getResources().getColor(
                        R.color.primary
                )
        );

        viewProfile.setGravity(
                Gravity.CENTER
        );

        viewProfile.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        // ORIGINAL OUTLINE STYLE
        viewProfile.setBackgroundResource(
                R.drawable.bg_btn_outline
        );

        LinearLayout.LayoutParams profileParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(54),
                        1f
                );

        profileParams.setMarginEnd(
                dp(8)
        );

        viewProfile.setLayoutParams(
                profileParams
        );

        buttonRow.addView(
                viewProfile
        );

        // ========================================================
        // SEND REQUEST BUTTON
        // ========================================================

        TextView sendRequest =
                new TextView(this);

        sendRequest.setText(
                "📅 Send Request"
        );

        sendRequest.setTextSize(15);

        sendRequest.setGravity(
                Gravity.CENTER
        );

        sendRequest.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        // ========================================================
        // ORIGINAL SEND REQUEST COLORS
        // ========================================================

        if (worker.isAvailable) {

            sendRequest.setTextColor(
                    getResources().getColor(
                            R.color.white
                    )
            );

            sendRequest.setBackgroundResource(
                    R.drawable.bg_btn_primary
            );

        } else {

            sendRequest.setTextColor(
                    getResources().getColor(
                            R.color.muted
                    )
            );

            sendRequest.setBackgroundResource(
                    R.drawable.bg_btn_outline
            );
        }

        LinearLayout.LayoutParams requestParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(54),
                        1f
                );

        sendRequest.setLayoutParams(
                requestParams
        );

        buttonRow.addView(
                sendRequest
        );

        card.addView(
                buttonRow
        );

        // ========================================================
        // VIEW PROFILE CLICK
        // ========================================================

        viewProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    WorkersListActivity.this,
                    WorkerProfileViewActivity.class
            );

            intent.putExtra(
                    WorkerProfileViewActivity.EXTRA_WORKER_NAME,
                    worker.name
            );

            intent.putExtra(
                    WorkerProfileViewActivity.EXTRA_WORKER_TITLE,
                    worker.title
            );

            intent.putExtra(
                    WorkerProfileViewActivity.EXTRA_WORKER_AREA,
                    worker.area
            );

            intent.putExtra(
                    WorkerProfileViewActivity.EXTRA_WORKER_EXP,
                    worker.experience
            );

            intent.putExtra(
                    WorkerProfileViewActivity.EXTRA_WORKER_RATE,
                    worker.rate
            );

            intent.putExtra(
                    WorkerProfileViewActivity.EXTRA_WORKER_RATING,
                    worker.rating
            );

            intent.putExtra(
                    WorkerProfileViewActivity.EXTRA_WORKER_BIO,
                    worker.title
            );

            startActivity(intent);
        });

        // ========================================================
        // SEND REQUEST CLICK
        // ========================================================

        sendRequest.setOnClickListener(
                v -> {

                    if (!worker.isAvailable) {

                        Toast.makeText(
                                this,
                                "This worker is currently busy",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    Intent intent =
                            new Intent(
                                    WorkersListActivity.this,
                                    BookingActivity.class
                            );

                    // EXISTING WORKER DATA
                    intent.putExtra(
                            BookingActivity.EXTRA_WORKER_NAME,
                            worker.name
                    );

                    intent.putExtra(
                            BookingActivity.EXTRA_WORKER_TITLE,
                            worker.title
                    );

                    intent.putExtra(
                            BookingActivity.EXTRA_WORKER_RATE,
                            worker.rate
                    );

                    // NEW:
                    // Home / Shop / Online BookingActivity ko pass
                    intent.putExtra(
                            BookingActivity.EXTRA_MAIN_CAT,
                            mainCatKey
                    );

                    startActivity(intent);
                }
        );

        return card;
    }

    // ============================================================
    // DP HELPER
    // ============================================================

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int)
                (value * density);
    }
}