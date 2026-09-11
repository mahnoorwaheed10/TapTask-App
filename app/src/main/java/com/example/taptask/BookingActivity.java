package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    public static final String EXTRA_WORKER_NAME = "worker_name";
    public static final String EXTRA_WORKER_TITLE = "worker_title";
    public static final String EXTRA_WORKER_RATE = "worker_rate";

    private TextView btnBack;

    private TextView tvBookingWorkerName;
    private TextView tvBookingWorkerTitle;
    private TextView tvBookingWorkerRate;

    private EditText etBookingDate;
    private EditText etBookingTime;
    private EditText etBookingDesc;
    private EditText etBookingAddress;

    private LinearLayout paymentCash;
    private LinearLayout paymentEasypaisa;
    private LinearLayout paymentJazzcash;

    private String selectedPayment = "cash";

    private Button btnConfirmBooking;

    private String workerName;
    private String workerTitle;
    private String workerRate;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        workerName = getIntent().getStringExtra(
                EXTRA_WORKER_NAME
        );

        workerTitle = getIntent().getStringExtra(
                EXTRA_WORKER_TITLE
        );

        workerRate = getIntent().getStringExtra(
                EXTRA_WORKER_RATE
        );

        if (workerName == null) {
            workerName = "Worker";
        }

        if (workerTitle == null) {
            workerTitle = "";
        }

        if (workerRate == null) {
            workerRate = "";
        }

        bindViews();
        fillWorkerInfo();
        setupPaymentSelection();
        setupConfirmButton();

        btnBack.setOnClickListener(v -> finish());
    }

    private void bindViews() {

        btnBack = findViewById(R.id.btnBack);

        tvBookingWorkerName =
                findViewById(R.id.tvBookingWorkerName);

        tvBookingWorkerTitle =
                findViewById(R.id.tvBookingWorkerTitle);

        tvBookingWorkerRate =
                findViewById(R.id.tvBookingWorkerRate);

        etBookingDate =
                findViewById(R.id.etBookingDate);

        etBookingTime =
                findViewById(R.id.etBookingTime);

        etBookingDesc =
                findViewById(R.id.etBookingDesc);

        etBookingAddress =
                findViewById(R.id.etBookingAddress);

        paymentCash =
                findViewById(R.id.paymentCash);

        paymentEasypaisa =
                findViewById(R.id.paymentEasypaisa);

        paymentJazzcash =
                findViewById(R.id.paymentJazzcash);

        btnConfirmBooking =
                findViewById(R.id.btnConfirmBooking);
    }

    private void fillWorkerInfo() {

        tvBookingWorkerName.setText(workerName);

        tvBookingWorkerTitle.setText(workerTitle);

        tvBookingWorkerRate.setText(workerRate);
    }

    private void setupPaymentSelection() {

        paymentCash.setOnClickListener(v ->
                selectPayment("cash"));

        paymentEasypaisa.setOnClickListener(v ->
                selectPayment("easypaisa"));

        paymentJazzcash.setOnClickListener(v ->
                selectPayment("jazzcash"));

        selectPayment("cash");
    }

    private void selectPayment(String method) {

        selectedPayment = method;

        paymentCash.setBackgroundResource(
                R.drawable.bg_payment_card
        );

        paymentEasypaisa.setBackgroundResource(
                R.drawable.bg_payment_card
        );

        paymentJazzcash.setBackgroundResource(
                R.drawable.bg_payment_card
        );

        if (method.equals("easypaisa")) {

            paymentEasypaisa.setBackgroundResource(
                    R.drawable.bg_payment_card_selected
            );

        } else if (method.equals("jazzcash")) {

            paymentJazzcash.setBackgroundResource(
                    R.drawable.bg_payment_card_selected
            );

        } else {

            paymentCash.setBackgroundResource(
                    R.drawable.bg_payment_card_selected
            );
        }
    }

    private void setupConfirmButton() {

        btnConfirmBooking.setOnClickListener(
                v -> handleConfirm()
        );
    }

    private void handleConfirm() {

        String date =
                etBookingDate.getText()
                        .toString()
                        .trim();

        String time =
                etBookingTime.getText()
                        .toString()
                        .trim();

        String desc =
                etBookingDesc.getText()
                        .toString()
                        .trim();

        String address =
                etBookingAddress.getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(date)
                || TextUtils.isEmpty(time)
                || TextUtils.isEmpty(address)) {

            Toast.makeText(
                    this,
                    "Please fill date, time and address",
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

        btnConfirmBooking.setEnabled(false);

        String customerId =
                currentUser.getUid();

        String customerEmail =
                currentUser.getEmail() != null
                        ? currentUser.getEmail()
                        : "";

        Map<String, Object> booking =
                new HashMap<>();

        booking.put(
                "customerId",
                customerId
        );

        booking.put(
                "customerEmail",
                customerEmail
        );

        booking.put(
                "workerName",
                workerName
        );

        booking.put(
                "serviceTitle",
                workerTitle
        );

        booking.put(
                "workerRate",
                workerRate
        );

        booking.put(
                "date",
                date
        );

        booking.put(
                "time",
                time
        );

        booking.put(
                "description",
                desc
        );

        booking.put(
                "address",
                address
        );

        booking.put(
                "paymentMethod",
                selectedPayment
        );

        booking.put(
                "status",
                "Pending"
        );

        booking.put(
                "createdAt",
                System.currentTimeMillis()
        );

        firestore
                .collection("bookings")
                .add(booking)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(
                            BookingActivity.this,
                            "Booking confirmed successfully!",
                            Toast.LENGTH_LONG
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    btnConfirmBooking.setEnabled(true);

                    Toast.makeText(
                            BookingActivity.this,
                            "Booking failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}