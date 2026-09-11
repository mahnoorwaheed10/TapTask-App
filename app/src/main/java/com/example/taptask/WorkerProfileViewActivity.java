package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WorkerProfileViewActivity extends AppCompatActivity {

    public static final String EXTRA_WORKER_NAME = "worker_name";
    public static final String EXTRA_WORKER_TITLE = "worker_title";
    public static final String EXTRA_WORKER_RATING = "worker_rating";
    public static final String EXTRA_WORKER_EXP = "worker_exp";
    public static final String EXTRA_WORKER_RATE = "worker_rate";
    public static final String EXTRA_WORKER_BIO = "worker_bio";
    public static final String EXTRA_WORKER_AREA = "worker_area";

    private FirebaseFirestore db;

    private LinearLayout reviewsContainer;

    private TextView tvViewWorkerRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_worker_profile_view);

        db = FirebaseFirestore.getInstance();

        TextView btnBack =
                findViewById(R.id.btnBack);

        TextView tvViewWorkerName =
                findViewById(R.id.tvViewWorkerName);

        TextView tvViewWorkerTitle =
                findViewById(R.id.tvViewWorkerTitle);

        tvViewWorkerRating =
                findViewById(R.id.tvViewWorkerRating);

        TextView tvViewWorkerExp =
                findViewById(R.id.tvViewWorkerExp);

        TextView tvViewWorkerRate =
                findViewById(R.id.tvViewWorkerRate);

        TextView tvViewWorkerBio =
                findViewById(R.id.tvViewWorkerBio);

        TextView tvViewWorkerArea =
                findViewById(R.id.tvViewWorkerArea);

        reviewsContainer =
                findViewById(R.id.reviewsContainer);

        Button btnBookNow =
                findViewById(R.id.btnBookNow);

        String name =
                getIntent().getStringExtra(
                        EXTRA_WORKER_NAME
                );

        String title =
                getIntent().getStringExtra(
                        EXTRA_WORKER_TITLE
                );

        float rating =
                getIntent().getFloatExtra(
                        EXTRA_WORKER_RATING,
                        0f
                );

        String exp =
                getIntent().getStringExtra(
                        EXTRA_WORKER_EXP
                );

        String rate =
                getIntent().getStringExtra(
                        EXTRA_WORKER_RATE
                );

        String bio =
                getIntent().getStringExtra(
                        EXTRA_WORKER_BIO
                );

        String area =
                getIntent().getStringExtra(
                        EXTRA_WORKER_AREA
                );

        if (name == null) {
            name = "Worker";
        }

        if (title == null) {
            title = "";
        }

        if (exp == null) {
            exp = "";
        }

        if (rate == null) {
            rate = "";
        }

        if (bio == null) {
            bio = "Professional service provider.";
        }

        if (area == null) {
            area = "Rawalpindi";
        }

        tvViewWorkerName.setText(name);

        tvViewWorkerTitle.setText(title);

        tvViewWorkerRating.setText(
                String.format(
                        "%.1f ⭐",
                        rating
                )
        );

        tvViewWorkerExp.setText(exp);

        tvViewWorkerRate.setText(
                "Rs. " + rate
        );

        tvViewWorkerBio.setText(bio);

        tvViewWorkerArea.setText(
                "📍 " + area
        );

        // ========================================================
        // LOAD REAL REVIEWS FROM FIREBASE
        // ========================================================

        loadWorkerReviews(name);

        // ========================================================
        // FINAL VALUES FOR BOOKING
        // ========================================================

        final String finalName = name;
        final String finalTitle = title;
        final String finalRate = rate;

        // ========================================================
        // BACK
        // ========================================================

        btnBack.setOnClickListener(
                v -> finish()
        );

        // ========================================================
        // BOOK NOW
        // ========================================================

        btnBookNow.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WorkerProfileViewActivity.this,
                            BookingActivity.class
                    );

            intent.putExtra(
                    BookingActivity.EXTRA_WORKER_NAME,
                    finalName
            );

            intent.putExtra(
                    BookingActivity.EXTRA_WORKER_TITLE,
                    finalTitle
            );

            intent.putExtra(
                    BookingActivity.EXTRA_WORKER_RATE,
                    finalRate
            );

            startActivity(intent);
        });
    }

    // ============================================================
    // LOAD WORKER REVIEWS
    // ============================================================

    private void loadWorkerReviews(
            String workerName
    ) {

        reviewsContainer.removeAllViews();

        TextView loading =
                new TextView(this);

        loading.setText(
                "Loading reviews..."
        );

        loading.setTextSize(13);

        loading.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        reviewsContainer.addView(
                loading
        );

        db.collection("reviews")
                .whereEqualTo(
                        "workerName",
                        workerName
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            reviewsContainer.removeAllViews();

                            if (querySnapshot.isEmpty()) {

                                showNoReviews();

                                return;
                            }

                            int totalRating = 0;
                            int reviewCount = 0;

                            for (
                                    DocumentSnapshot document
                                    : querySnapshot.getDocuments()
                            ) {

                                int rating =
                                        getRating(document);

                                String reviewText =
                                        document.getString(
                                                "review"
                                        );

                                String customerName =
                                        document.getString(
                                                "customerEmail"
                                        );

                                if (reviewText == null) {
                                    reviewText = "";
                                }

                                if (customerName == null ||
                                        customerName.trim().isEmpty()) {

                                    customerName =
                                            "Customer";
                                }

                                totalRating =
                                        totalRating + rating;

                                reviewCount++;

                                addReviewCard(
                                        customerName,
                                        rating,
                                        reviewText
                                );
                            }

                            // ====================================================
                            // CALCULATE REAL AVERAGE RATING
                            // ====================================================

                            if (reviewCount > 0) {

                                float averageRating =
                                        (float) totalRating
                                                / reviewCount;

                                tvViewWorkerRating.setText(
                                        String.format(
                                                "%.1f ⭐ (%d reviews)",
                                                averageRating,
                                                reviewCount
                                        )
                                );
                            }
                        }
                )
                .addOnFailureListener(
                        e -> {

                            reviewsContainer.removeAllViews();

                            showNoReviews();

                            Toast.makeText(
                                    this,
                                    "Could not load reviews",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                );
    }

    // ============================================================
    // GET RATING
    // ============================================================

    private int getRating(
            DocumentSnapshot document
    ) {

        Object value =
                document.get("rating");

        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {

            return ((Number) value)
                    .intValue();
        }

        try {

            return Integer.parseInt(
                    String.valueOf(value)
            );

        } catch (Exception e) {

            return 0;
        }
    }

    // ============================================================
    // ADD REAL REVIEW CARD
    // ============================================================

    private void addReviewCard(
            String customerName,
            int rating,
            String reviewText
    ) {

        LinearLayout reviewCard =
                new LinearLayout(this);

        reviewCard.setOrientation(
                LinearLayout.VERTICAL
        );

        reviewCard.setBackgroundResource(
                R.drawable.bg_chip_light
        );

        reviewCard.setPadding(
                dp(12),
                dp(12),
                dp(12),
                dp(12)
        );

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.bottomMargin =
                dp(10);

        reviewCard.setLayoutParams(
                params
        );

        // ========================================================
        // TOP ROW
        // ========================================================

        LinearLayout topRow =
                new LinearLayout(this);

        topRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        // ========================================================
        // CUSTOMER NAME
        // ========================================================

        TextView reviewerName =
                new TextView(this);

        reviewerName.setText(
                customerName
        );

        reviewerName.setTextColor(
                getResources().getColor(
                        R.color.text_main
                )
        );

        reviewerName.setTextSize(13);

        reviewerName.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        LinearLayout.LayoutParams nameParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        reviewerName.setLayoutParams(
                nameParams
        );

        topRow.addView(
                reviewerName
        );

        // ========================================================
        // STARS
        // ========================================================

        TextView stars =
                new TextView(this);

        stars.setText(
                createStars(rating)
        );

        stars.setTextColor(
                getResources().getColor(
                        R.color.amber
                )
        );

        stars.setTextSize(12);

        topRow.addView(
                stars
        );

        reviewCard.addView(
                topRow
        );

        // ========================================================
        // REVIEW TEXT
        // ========================================================

        TextView reviewTextView =
                new TextView(this);

        reviewTextView.setText(
                reviewText
        );

        reviewTextView.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        reviewTextView.setTextSize(12);

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        textParams.topMargin =
                dp(6);

        reviewTextView.setLayoutParams(
                textParams
        );

        reviewCard.addView(
                reviewTextView
        );

        reviewsContainer.addView(
                reviewCard
        );
    }

    // ============================================================
    // CREATE STAR TEXT
    // ============================================================

    private String createStars(
            int rating
    ) {

        StringBuilder stars =
                new StringBuilder();

        for (int i = 1; i <= 5; i++) {

            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }

        return stars.toString();
    }

    // ============================================================
    // NO REVIEWS
    // ============================================================

    private void showNoReviews() {

        TextView noReviews =
                new TextView(this);

        noReviews.setText(
                "No reviews yet."
        );

        noReviews.setTextSize(13);

        noReviews.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        noReviews.setPadding(
                0,
                dp(10),
                0,
                dp(10)
        );

        reviewsContainer.addView(
                noReviews
        );
    }

    // ============================================================
    // DP
    // ============================================================

    private int dp(
            int value
    ) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int)
                (value * density);
    }
}