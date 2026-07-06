package com.example.taptask;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    public static final String EXTRA_PAYMENT_METHOD = "payment_method";

    private TextView btnBack;
    private TextView tvPaymentMethodIcon, tvPaymentMethodName, tvPaymentNumber, tvPaymentInstruction;
    private TextView btnCopyNumber;
    private LinearLayout btnUploadScreenshot;
    private TextView tvUploadStatus;
    private ImageView ivScreenshotPreview;
    private Button btnConfirmPaymentSent;

    private String paymentMethod = "easypaisa";
    private boolean screenshotUploaded = false;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paymentMethod = getIntent().getStringExtra(EXTRA_PAYMENT_METHOD);
        if (paymentMethod == null) paymentMethod = "easypaisa";

        bindViews();
        setupImagePicker();
        fillPaymentInfo();
        setupButtons();
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        tvPaymentMethodIcon = findViewById(R.id.tvPaymentMethodIcon);
        tvPaymentMethodName = findViewById(R.id.tvPaymentMethodName);
        tvPaymentNumber = findViewById(R.id.tvPaymentNumber);
        tvPaymentInstruction = findViewById(R.id.tvPaymentInstruction);
        btnCopyNumber = findViewById(R.id.btnCopyNumber);
        btnUploadScreenshot = findViewById(R.id.btnUploadScreenshot);
        tvUploadStatus = findViewById(R.id.tvUploadStatus);
        ivScreenshotPreview = findViewById(R.id.ivScreenshotPreview);
        btnConfirmPaymentSent = findViewById(R.id.btnConfirmPaymentSent);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        ivScreenshotPreview.setImageURI(uri);
                        ivScreenshotPreview.setVisibility(android.view.View.VISIBLE);
                        tvUploadStatus.setText("✅ Screenshot Uploaded");
                        screenshotUploaded = true;
                    }
                });
    }

    private void fillPaymentInfo() {
        if (paymentMethod.equals("jazzcash")) {
            tvPaymentMethodName.setText("JazzCash");
            tvPaymentNumber.setText("0312-9876543");
            tvPaymentInstruction.setText(String.format(getString(R.string.send_payment_instruction), "JazzCash"));
        } else {
            tvPaymentMethodName.setText("EasyPaisa");
            tvPaymentNumber.setText("0300-1234567");
            tvPaymentInstruction.setText(String.format(getString(R.string.send_payment_instruction), "EasyPaisa"));
        }
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());

        btnCopyNumber.setOnClickListener(v -> {
            android.content.ClipboardManager clipboard =
                    (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            android.content.ClipData clip =
                    android.content.ClipData.newPlainText("Payment Number", tvPaymentNumber.getText().toString());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Number copied!", Toast.LENGTH_SHORT).show();
        });

        btnUploadScreenshot.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        btnConfirmPaymentSent.setOnClickListener(v -> {
            if (!screenshotUploaded) {
                Toast.makeText(this, "Please upload a payment screenshot first", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Payment confirmation sent! Booking is now pending approval.", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}