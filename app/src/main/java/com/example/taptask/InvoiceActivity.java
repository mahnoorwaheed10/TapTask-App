package com.example.taptask;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class InvoiceActivity extends AppCompatActivity {

    public static final String EXTRA_WORKER_NAME = "worker_name";
    public static final String EXTRA_SERVICE = "service";
    public static final String EXTRA_RATE = "rate";

    private TextView btnBack;
    private TextView tvInvoiceWorker, tvInvoiceService, tvInvoiceDate, tvInvoicePayment, tvInvoiceTotal;
    private Button btnPrintInvoice, btnSubmitRating;
    private EditText etReviewText;

    private TextView star1, star2, star3, star4, star5;
    private int selectedStars = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        bindViews();
        fillInvoiceData();
        setupStars();
        setupButtons();

        btnBack.setOnClickListener(v -> finish());
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        tvInvoiceWorker = findViewById(R.id.tvInvoiceWorker);
        tvInvoiceService = findViewById(R.id.tvInvoiceService);
        tvInvoiceDate = findViewById(R.id.tvInvoiceDate);
        tvInvoicePayment = findViewById(R.id.tvInvoicePayment);
        tvInvoiceTotal = findViewById(R.id.tvInvoiceTotal);
        btnPrintInvoice = findViewById(R.id.btnPrintInvoice);
        btnSubmitRating = findViewById(R.id.btnSubmitRating);
        etReviewText = findViewById(R.id.etReviewText);

        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);
    }

    private void fillInvoiceData() {
        String workerName = getIntent().getStringExtra(EXTRA_WORKER_NAME);
        String service = getIntent().getStringExtra(EXTRA_SERVICE);
        String rate = getIntent().getStringExtra(EXTRA_RATE);

        if (workerName == null) workerName = "Worker";
        if (service == null) service = "Service";
        if (rate == null) rate = "Rs. 0";

        tvInvoiceWorker.setText(workerName);
        tvInvoiceService.setText(service);
        tvInvoiceDate.setText("Date: " + "30 June 2026");
        tvInvoicePayment.setText("Payment: Cash on Delivery");
        tvInvoiceTotal.setText("Total: " + rate);
    }

    private void setupStars() {
        star1.setOnClickListener(v -> setStars(1));
        star2.setOnClickListener(v -> setStars(2));
        star3.setOnClickListener(v -> setStars(3));
        star4.setOnClickListener(v -> setStars(4));
        star5.setOnClickListener(v -> setStars(5));
    }

    private void setStars(int count) {
        selectedStars = count;
        TextView[] stars = {star1, star2, star3, star4, star5};
        for (int i = 0; i < stars.length; i++) {
            stars[i].setText(i < count ? "★" : "☆");
        }
    }

    private void setupButtons() {
        btnPrintInvoice.setOnClickListener(v ->
                Toast.makeText(this, "Invoice download (PDF export coming with backend)", Toast.LENGTH_SHORT).show());

        btnSubmitRating.setOnClickListener(v -> {
            if (selectedStars == 0) {
                Toast.makeText(this, "Please select a star rating", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Thank you for your " + selectedStars + "-star rating!", Toast.LENGTH_SHORT).show();
        });
    }
}