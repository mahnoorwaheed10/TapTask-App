package com.example.taptask;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    private String bookingId;
    private String workerName;
    private String service;
    private String rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invoice);

        // Get booking information
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

        // Safe default values
        if (workerName == null ||
                workerName.trim().isEmpty()) {

            workerName = "Worker";
        }

        if (service == null ||
                service.trim().isEmpty()) {

            service = "Service";
        }

        if (rate == null ||
                rate.trim().isEmpty()) {

            rate = "Rs. 0";
        }

        bindViews();
        fillInvoiceData();
        setupButtons();
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
    }

    private void fillInvoiceData() {

        tvInvoiceWorker.setText(
                workerName
        );

        tvInvoiceService.setText(
                service
        );

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

    private void setupButtons() {

        btnBack.setOnClickListener(
                v -> finish()
        );

        btnPrintInvoice.setOnClickListener(
                v -> Toast.makeText(
                        InvoiceActivity.this,
                        "Invoice download coming soon",
                        Toast.LENGTH_SHORT
                ).show()
        );
    }
}