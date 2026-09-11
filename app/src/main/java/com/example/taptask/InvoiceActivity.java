package com.example.taptask;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InvoiceActivity extends AppCompatActivity {

    public static final String EXTRA_WORKER_NAME = "worker_name";
    public static final String EXTRA_SERVICE = "service";
    public static final String EXTRA_RATE = "rate";
    public static final String EXTRA_BOOKING_ID = "booking_id";

    private TextView btnBack;

    private TextView tvInvoiceWorker;
    private TextView tvInvoiceService;
    private TextView tvInvoiceDate;
    private TextView tvInvoicePayment;
    private TextView tvInvoiceTotal;

    private Button btnPrintInvoice;
    private Button btnSubmitRating;

    private EditText etReviewText;

    private TextView star1;
    private TextView star2;
    private TextView star3;
    private TextView star4;
    private TextView star5;

    private int selectedStars = 0;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private String bookingId;
    private String workerName;
    private String service;
    private String rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invoice);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        bookingId = getIntent().getStringExtra(
                EXTRA_BOOKING_ID
        );

        workerName = getIntent().getStringExtra(
                EXTRA_WORKER_NAME
        );

        service = getIntent().getStringExtra(
                EXTRA_SERVICE
        );

        rate = getIntent().getStringExtra(
                EXTRA_RATE
        );

        if (workerName == null) {
            workerName = "Worker";
        }

        if (service == null) {
            service = "Service";
        }

        if (rate == null) {
            rate = "Rs. 0";
        }

        bindViews();
        fillInvoiceData();
        setupStars();
        setupButtons();

        btnBack.setOnClickListener(v -> finish());
    }

    private void bindViews() {

        btnBack = findViewById(R.id.btnBack);

        tvInvoiceWorker =
                findViewById(R.id.tvInvoiceWorker);

        tvInvoiceService =
                findViewById(R.id.tvInvoiceService);

        tvInvoiceDate =
                findViewById(R.id.tvInvoiceDate);

        tvInvoicePayment =
                findViewById(R.id.tvInvoicePayment);

        tvInvoiceTotal =
                findViewById(R.id.tvInvoiceTotal);

        btnPrintInvoice =
                findViewById(R.id.btnPrintInvoice);

        btnSubmitRating =
                findViewById(R.id.btnSubmitRating);

        etReviewText =
                findViewById(R.id.etReviewText);

        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);
    }

    private void fillInvoiceData() {

        tvInvoiceWorker.setText(workerName);

        tvInvoiceService.setText(service);

        tvInvoiceDate.setText(
                "Booking Completed"
        );

        tvInvoicePayment.setText(
                "Payment: Booking Payment"
        );

        tvInvoiceTotal.setText(
                "Total: " + rate
        );
    }

    private void setupStars() {

        star1.setOnClickListener(
                v -> setStars(1)
        );

        star2.setOnClickListener(
                v -> setStars(2)
        );

        star3.setOnClickListener(
                v -> setStars(3)
        );

        star4.setOnClickListener(
                v -> setStars(4)
        );

        star5.setOnClickListener(
                v -> setStars(5)
        );
    }

    private void setStars(int count) {

        selectedStars = count;

        TextView[] stars = {
                star1,
                star2,
                star3,
                star4,
                star5
        };

        for (int i = 0; i < stars.length; i++) {

            stars[i].setText(
                    i < count ? "★" : "☆"
            );
        }
    }

    private void setupButtons() {

        btnPrintInvoice.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Invoice download coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );

        btnSubmitRating.setOnClickListener(
                v -> submitRating()
        );
    }

    private void submitRating() {

        if (selectedStars == 0) {

            Toast.makeText(
                    this,
                    "Please select a star rating",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String reviewText =
                etReviewText.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(reviewText)) {

            Toast.makeText(
                    this,
                    "Please write a review",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

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

        if (bookingId == null ||
                bookingId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Booking information not found",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        btnSubmitRating.setEnabled(false);

        Map<String, Object> review =
                new HashMap<>();

        review.put(
                "bookingId",
                bookingId
        );

        review.put(
                "customerId",
                currentUser.getUid()
        );

        review.put(
                "customerEmail",
                currentUser.getEmail() != null
                        ? currentUser.getEmail()
                        : ""
        );

        review.put(
                "workerName",
                workerName
        );

        review.put(
                "serviceTitle",
                service
        );

        review.put(
                "rating",
                selectedStars
        );

        review.put(
                "review",
                reviewText
        );

        review.put(
                "createdAt",
                System.currentTimeMillis()
        );

        firestore
                .collection("reviews")
                .add(review)
                .addOnSuccessListener(
                        documentReference -> {

                            updateWorkerRating();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            btnSubmitRating.setEnabled(
                                    true
                            );

                            Toast.makeText(
                                    InvoiceActivity.this,
                                    "Review failed: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    private void updateWorkerRating() {

        firestore
                .collection("workers")
                .whereEqualTo(
                        "name",
                        workerName
                )
                .get()
                .addOnSuccessListener(
                        workerSnapshot -> {

                            if (workerSnapshot.isEmpty()) {

                                showRatingSuccess();

                                return;
                            }

                            for (
                                    DocumentSnapshot workerDoc
                                    : workerSnapshot.getDocuments()
                            ) {

                                Object oldRatingObject =
                                        workerDoc.get("rating");

                                Object oldReviewsObject =
                                        workerDoc.get("reviews");

                                double oldRating = 0;

                                int oldReviews = 0;

                                if (oldRatingObject instanceof Number) {

                                    oldRating =
                                            ((Number) oldRatingObject)
                                                    .doubleValue();
                                }

                                if (oldReviewsObject instanceof Number) {

                                    oldReviews =
                                            ((Number) oldReviewsObject)
                                                    .intValue();
                                }

                                int newReviews =
                                        oldReviews + 1;

                                double newRating =
                                        (
                                                (oldRating * oldReviews)
                                                        + selectedStars
                                        )
                                                / newReviews;

                                firestore
                                        .collection("workers")
                                        .document(workerDoc.getId())
                                        .update(
                                                "rating",
                                                newRating,
                                                "reviews",
                                                newReviews
                                        );
                            }

                            showRatingSuccess();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            btnSubmitRating.setEnabled(
                                    true
                            );

                            Toast.makeText(
                                    InvoiceActivity.this,
                                    "Review saved, but worker rating update failed",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    private void showRatingSuccess() {

        Toast.makeText(
                InvoiceActivity.this,
                "Rating & review submitted successfully!",
                Toast.LENGTH_LONG
        ).show();

        btnSubmitRating.setEnabled(
                true
        );

        etReviewText.setText("");

        setStars(0);
    }
}