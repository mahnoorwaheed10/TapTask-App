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

public class BookingActivity extends AppCompatActivity {

    public static final String EXTRA_WORKER_NAME = "worker_name";
    public static final String EXTRA_WORKER_TITLE = "worker_title";
    public static final String EXTRA_WORKER_RATE = "worker_rate";

    private TextView btnBack;
    private TextView tvBookingWorkerName, tvBookingWorkerTitle, tvBookingWorkerRate;
    private EditText etBookingDate, etBookingTime, etBookingDesc, etBookingAddress;

    private LinearLayout paymentCash, paymentEasypaisa, paymentJazzcash;
    private String selectedPayment = "cash";

    private Button btnConfirmBooking;

    private String workerName, workerTitle, workerRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        workerName = getIntent().getStringExtra(EXTRA_WORKER_NAME);
        workerTitle = getIntent().getStringExtra(EXTRA_WORKER_TITLE);
        workerRate = getIntent().getStringExtra(EXTRA_WORKER_RATE);

        if (workerName == null) workerName = "Worker";
        if (workerTitle == null) workerTitle = "";
        if (workerRate == null) workerRate = "";

        bindViews();
        fillWorkerInfo();
        setupPaymentSelection();
        setupConfirmButton();

        btnBack.setOnClickListener(v -> finish());
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        tvBookingWorkerName = findViewById(R.id.tvBookingWorkerName);
        tvBookingWorkerTitle = findViewById(R.id.tvBookingWorkerTitle);
        tvBookingWorkerRate = findViewById(R.id.tvBookingWorkerRate);

        etBookingDate = findViewById(R.id.etBookingDate);
        etBookingTime = findViewById(R.id.etBookingTime);
        etBookingDesc = findViewById(R.id.etBookingDesc);
        etBookingAddress = findViewById(R.id.etBookingAddress);

        paymentCash = findViewById(R.id.paymentCash);
        paymentEasypaisa = findViewById(R.id.paymentEasypaisa);
        paymentJazzcash = findViewById(R.id.paymentJazzcash);

        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
    }

    private void fillWorkerInfo() {
        tvBookingWorkerName.setText(workerName);
        tvBookingWorkerTitle.setText(workerTitle);
        tvBookingWorkerRate.setText(workerRate);
    }

    private void setupPaymentSelection() {
        paymentCash.setOnClickListener(v -> selectPayment("cash"));
        paymentEasypaisa.setOnClickListener(v -> selectPayment("easypaisa"));
        paymentJazzcash.setOnClickListener(v -> selectPayment("jazzcash"));
    }

    private void selectPayment(String method) {
        selectedPayment = method;

        paymentCash.setBackgroundResource(R.drawable.bg_payment_card);
        paymentEasypaisa.setBackgroundResource(R.drawable.bg_payment_card);
        paymentJazzcash.setBackgroundResource(R.drawable.bg_payment_card);

        switch (method) {
            case "easypaisa":
                paymentEasypaisa.setBackgroundResource(R.drawable.bg_payment_card_selected);
                break;
            case "jazzcash":
                paymentJazzcash.setBackgroundResource(R.drawable.bg_payment_card_selected);
                break;
            default:
                paymentCash.setBackgroundResource(R.drawable.bg_payment_card_selected);
                break;
        }
    }

    private void setupConfirmButton() {
        btnConfirmBooking.setOnClickListener(v -> handleConfirm());
    }

    private void handleConfirm() {
        String date = etBookingDate.getText().toString().trim();
        String time = etBookingTime.getText().toString().trim();
        String desc = etBookingDesc.getText().toString().trim();
        String address = etBookingAddress.getText().toString().trim();

        if (TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please fill date, time and address", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPayment.equals("cash")) {
            Toast.makeText(this, "Booking confirmed! Pay cash on service completion.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Intent intent = new Intent(BookingActivity.this, PaymentActivity.class);
            intent.putExtra(PaymentActivity.EXTRA_PAYMENT_METHOD, selectedPayment);
            startActivity(intent);
        }
    }
}
