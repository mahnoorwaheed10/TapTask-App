package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WorkerProfileViewActivity extends AppCompatActivity {

    public static final String EXTRA_WORKER_NAME = "worker_name";
    public static final String EXTRA_WORKER_TITLE = "worker_title";
    public static final String EXTRA_WORKER_RATING = "worker_rating";
    public static final String EXTRA_WORKER_EXP = "worker_exp";
    public static final String EXTRA_WORKER_RATE = "worker_rate";
    public static final String EXTRA_WORKER_BIO = "worker_bio";
    public static final String EXTRA_WORKER_AREA = "worker_area";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile_view);

        TextView btnBack = findViewById(R.id.btnBack);
        TextView tvViewWorkerName = findViewById(R.id.tvViewWorkerName);
        TextView tvViewWorkerTitle = findViewById(R.id.tvViewWorkerTitle);
        TextView tvViewWorkerRating = findViewById(R.id.tvViewWorkerRating);
        TextView tvViewWorkerExp = findViewById(R.id.tvViewWorkerExp);
        TextView tvViewWorkerRate = findViewById(R.id.tvViewWorkerRate);
        TextView tvViewWorkerBio = findViewById(R.id.tvViewWorkerBio);
        TextView tvViewWorkerArea = findViewById(R.id.tvViewWorkerArea);
        LinearLayout reviewsContainer = findViewById(R.id.reviewsContainer);
        Button btnBookNow = findViewById(R.id.btnBookNow);

        String name = getIntent().getStringExtra(EXTRA_WORKER_NAME);
        String title = getIntent().getStringExtra(EXTRA_WORKER_TITLE);
        float rating = getIntent().getFloatExtra(EXTRA_WORKER_RATING, 4.5f);
        String exp = getIntent().getStringExtra(EXTRA_WORKER_EXP);
        String rate = getIntent().getStringExtra(EXTRA_WORKER_RATE);
        String bio = getIntent().getStringExtra(EXTRA_WORKER_BIO);
        String area = getIntent().getStringExtra(EXTRA_WORKER_AREA);

        if (name == null) name = "Worker";
        if (title == null) title = "";
        if (exp == null) exp = "";
        if (rate == null) rate = "";
        if (bio == null) bio = "Professional service provider.";
        if (area == null) area = "Rawalpindi";

        tvViewWorkerName.setText(name);
        tvViewWorkerTitle.setText(title);
        tvViewWorkerRating.setText(rating + " ⭐");
        tvViewWorkerExp.setText(exp);
        tvViewWorkerRate.setText(rate);
        tvViewWorkerBio.setText(bio);
        tvViewWorkerArea.setText("📍 " + area);

        addMockReviews(reviewsContainer);

        final String finalName = name;
        final String finalTitle = title;
        final String finalRate = rate;

        btnBack.setOnClickListener(v -> finish());

        btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(WorkerProfileViewActivity.this, BookingActivity.class);
            intent.putExtra(BookingActivity.EXTRA_WORKER_NAME, finalName);
            intent.putExtra(BookingActivity.EXTRA_WORKER_TITLE, finalTitle);
            intent.putExtra(BookingActivity.EXTRA_WORKER_RATE, finalRate);
            startActivity(intent);
        });
    }

    private void addMockReviews(LinearLayout container) {
        String[][] reviews = {
                {"Ahmed K.", "★★★★★", "Excellent work, very professional and on time!"},
                {"Sara M.", "★★★★☆", "Good service, will definitely book again."},
                {"Bilal R.", "★★★★★", "Fixed the issue quickly, very knowledgeable."}
        };

        for (String[] review : reviews) {
            LinearLayout reviewCard = new LinearLayout(this);
            reviewCard.setOrientation(LinearLayout.VERTICAL);
            reviewCard.setBackgroundResource(R.drawable.bg_chip_light);
            reviewCard.setPadding(dp(12), dp(12), dp(12), dp(12));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = dp(10);
            reviewCard.setLayoutParams(params);

            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);

            TextView reviewerName = new TextView(this);
            reviewerName.setText(review[0]);
            reviewerName.setTextColor(getResources().getColor(R.color.text_main));
            reviewerName.setTextSize(13);
            reviewerName.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            reviewerName.setLayoutParams(nameParams);
            topRow.addView(reviewerName);

            TextView stars = new TextView(this);
            stars.setText(review[1]);
            stars.setTextColor(getResources().getColor(R.color.amber));
            stars.setTextSize(12);
            topRow.addView(stars);

            reviewCard.addView(topRow);

            TextView reviewText = new TextView(this);
            reviewText.setText(review[2]);
            reviewText.setTextColor(getResources().getColor(R.color.muted));
            reviewText.setTextSize(12);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textParams.topMargin = dp(6);
            reviewText.setLayoutParams(textParams);
            reviewCard.addView(reviewText);

            container.addView(reviewCard);
        }
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }
}