
package com.example.taptask;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AppFeedbackActivity extends AppCompatActivity {

    private TextView btnBack;

    private TextView star1;
    private TextView star2;
    private TextView star3;
    private TextView star4;
    private TextView star5;
    private TextView tvRatingText;

    private RadioGroup rgBooking;
    private RadioGroup rgEase;
    private RadioGroup rgPayment;

    private CheckBox cbWorkerProfessional;
    private CheckBox cbWorkerOnTime;
    private CheckBox cbGoodQuality;
    private CheckBox cbGoodCommunication;

    private EditText etImprovement;
    private EditText etFeatureRequest;
    private EditText etProblem;

    private Button btnSubmitFeedback;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private int selectedRating = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_feedback);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initializeViews();
        setupStars();
        setupBackButton();
        setupSubmitButton();
    }

    private void initializeViews() {

        btnBack = findViewById(R.id.btnBack);

        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);

        tvRatingText = findViewById(R.id.tvRatingText);

        rgBooking = findViewById(R.id.rgBooking);
        rgEase = findViewById(R.id.rgEase);
        rgPayment = findViewById(R.id.rgPayment);

        cbWorkerProfessional = findViewById(R.id.cbWorkerProfessional);
        cbWorkerOnTime = findViewById(R.id.cbWorkerOnTime);
        cbGoodQuality = findViewById(R.id.cbGoodQuality);
        cbGoodCommunication = findViewById(R.id.cbGoodCommunication);

        etImprovement = findViewById(R.id.etImprovement);
        etFeatureRequest = findViewById(R.id.etFeatureRequest);
        etProblem = findViewById(R.id.etProblem);

        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);
    }

    private void setupBackButton() {

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupStars() {

        star1.setOnClickListener(v -> setRating(1));
        star2.setOnClickListener(v -> setRating(2));
        star3.setOnClickListener(v -> setRating(3));
        star4.setOnClickListener(v -> setRating(4));
        star5.setOnClickListener(v -> setRating(5));
    }

    private void setRating(int rating) {

        selectedRating = rating;

        TextView[] stars = {
                star1,
                star2,
                star3,
                star4,
                star5
        };

        for (int i = 0; i < stars.length; i++) {

            if (i < rating) {
                stars[i].setText("★");
            } else {
                stars[i].setText("☆");
            }
        }

        String ratingText;

        switch (rating) {

            case 1:
                ratingText = "Very Poor";
                break;

            case 2:
                ratingText = "Needs Improvement";
                break;

            case 3:
                ratingText = "Good";
                break;

            case 4:
                ratingText = "Very Good";
                break;

            case 5:
                ratingText = "Excellent!";
                break;

            default:
                ratingText = "Tap a star to rate";
                break;
        }

        tvRatingText.setText(ratingText);
    }

    private void setupSubmitButton() {

        btnSubmitFeedback.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {

        if (selectedRating == 0) {

            Toast.makeText(
                    this,
                    "Please select an overall rating.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Please login first.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        btnSubmitFeedback.setEnabled(false);
        btnSubmitFeedback.setText("SUBMITTING...");

        String customerId = auth.getCurrentUser().getUid();

        String customerEmail = auth.getCurrentUser().getEmail();

        String bookingExperience =
                getSelectedRadioText(rgBooking);

        String appEase =
                getSelectedRadioText(rgEase);

        String paymentExperience =
                getSelectedRadioText(rgPayment);

        Map<String, Object> feedback = new HashMap<>();

        feedback.put("customerId", customerId);
        feedback.put("customerEmail", customerEmail);

        feedback.put("rating", selectedRating);

        feedback.put(
                "bookingExperience",
                bookingExperience
        );

        feedback.put(
                "appEase",
                appEase
        );

        feedback.put(
                "paymentExperience",
                paymentExperience
        );

        feedback.put(
                "workerProfessional",
                cbWorkerProfessional.isChecked()
        );

        feedback.put(
                "workerOnTime",
                cbWorkerOnTime.isChecked()
        );

        feedback.put(
                "goodServiceQuality",
                cbGoodQuality.isChecked()
        );

        feedback.put(
                "goodCommunication",
                cbGoodCommunication.isChecked()
        );

        feedback.put(
                "improvement",
                getText(etImprovement)
        );

        feedback.put(
                "featureRequest",
                getText(etFeatureRequest)
        );

        feedback.put(
                "problem",
                getText(etProblem)
        );

        feedback.put(
                "createdAt",
                com.google.firebase.firestore.FieldValue.serverTimestamp()
        );

        firestore
                .collection("app_feedback")
                .add(feedback)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            AppFeedbackActivity.this,
                            "Thank you! Your feedback has been submitted.",
                            Toast.LENGTH_LONG
                    ).show();

                    finish();

                })
                .addOnFailureListener(e -> {

                    btnSubmitFeedback.setEnabled(true);
                    btnSubmitFeedback.setText("SUBMIT FEEDBACK");

                    Toast.makeText(
                            AppFeedbackActivity.this,
                            "Failed to submit feedback. Please try again.",
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private String getText(EditText editText) {

        if (editText == null) {
            return "";
        }

        return editText
                .getText()
                .toString()
                .trim();
    }

    private String getSelectedRadioText(RadioGroup radioGroup) {

        if (radioGroup == null) {
            return "";
        }

        int selectedId = radioGroup.getCheckedRadioButtonId();

        if (selectedId == -1) {
            return "";
        }

        View selectedView = findViewById(selectedId);

        if (selectedView instanceof android.widget.RadioButton) {

            return ((android.widget.RadioButton) selectedView)
                    .getText()
                    .toString();
        }

        return "";
    }
}