package com.example.taptask;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    // =========================================================
    // INTENT CONSTANTS
    // =========================================================

    public static final String EXTRA_BOOKING_ID = "booking_id";
    public static final String EXTRA_PAYMENT_METHOD = "payment_method";

    // =========================================================
    // VIEWS
    // =========================================================

    private Button btnBack;
    private Button btnCopyNumber;
    private LinearLayout btnUploadScreenshot;
    private Button btnConfirmPaymentSent;

    private TextView tvPaymentMethodIcon;
    private TextView tvPaymentMethodName;
    private TextView tvPaymentNumber;
    private TextView tvPaymentInstruction;
    private TextView tvUploadStatus;

    // =========================================================
    // DATA
    // =========================================================

    private String bookingId;
    private String paymentMethod;

    private FirebaseFirestore firestore;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_payment);

        firestore = FirebaseFirestore.getInstance();

        bookingId =
                getIntent().getStringExtra(
                        EXTRA_BOOKING_ID
                );

        paymentMethod =
                getIntent().getStringExtra(
                        EXTRA_PAYMENT_METHOD
                );

        if (bookingId == null
                || bookingId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Booking not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        if (paymentMethod == null
                || paymentMethod.trim().isEmpty()) {

            paymentMethod = "cash";
        }

        initializeViews();
        loadBooking();
    }

    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        btnBack =
                findViewById(R.id.btnBack);

        btnCopyNumber =
                findViewById(R.id.btnCopyNumber);

        btnUploadScreenshot =
                findViewById(R.id.btnUploadScreenshot);

        btnConfirmPaymentSent =
                findViewById(
                        R.id.btnConfirmPaymentSent
                );

        tvPaymentMethodIcon =
                findViewById(
                        R.id.tvPaymentMethodIcon
                );

        tvPaymentMethodName =
                findViewById(
                        R.id.tvPaymentMethodName
                );

        tvPaymentNumber =
                findViewById(
                        R.id.tvPaymentNumber
                );

        tvPaymentInstruction =
                findViewById(
                        R.id.tvPaymentInstruction
                );

        tvUploadStatus =
                findViewById(
                        R.id.tvUploadStatus
                );

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Copy button
        btnCopyNumber.setOnClickListener(v -> {

            String number =
                    tvPaymentNumber
                            .getText()
                            .toString();

            ClipboardManager clipboard =
                    (ClipboardManager)
                            getSystemService(
                                    Context.CLIPBOARD_SERVICE
                            );

            ClipData clip =
                    ClipData.newPlainText(
                            "Payment Number",
                            number
                    );

            clipboard.setPrimaryClip(clip);

            Toast.makeText(
                    PaymentActivity.this,
                    "Payment number copied",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Screenshot area
        btnUploadScreenshot.setOnClickListener(v -> {

            Toast.makeText(
                    PaymentActivity.this,
                    "Screenshot upload is not required in TEST MODE",
                    Toast.LENGTH_SHORT
            ).show();

            tvUploadStatus.setText(
                    "TEST MODE"
            );
        });

        // Main payment button
        btnConfirmPaymentSent.setOnClickListener(v -> {

            if (isCashPayment()) {

                showCashPaymentDialog();

            } else {

                showOnlinePaymentDialog();
            }
        });
    }

    // =========================================================
    // LOAD EXISTING BOOKING
    // =========================================================

    private void loadBooking() {

        firestore.collection("bookings")
                .document(bookingId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {

                        Toast.makeText(
                                PaymentActivity.this,
                                "Booking not found",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                        return;
                    }

                    // Existing payment status
                    String savedPaymentStatus =
                            documentSnapshot.getString(
                                    "paymentStatus"
                            );

                    // Already paid
                    if ("Paid".equalsIgnoreCase(
                            savedPaymentStatus
                    )) {

                        new AlertDialog.Builder(
                                PaymentActivity.this
                        )
                                .setTitle(
                                        "Payment Already Completed"
                                )
                                .setMessage(
                                        "This booking has already been paid."
                                )
                                .setPositiveButton(
                                        "OK",
                                        (dialog, which) ->
                                                finish()
                                )
                                .show();

                        return;
                    }

                    // Get payment method from Firebase
                    String savedPaymentMethod =
                            documentSnapshot.getString(
                                    "paymentMethod"
                            );

                    if (savedPaymentMethod != null
                            && !savedPaymentMethod
                            .trim()
                            .isEmpty()) {

                        paymentMethod =
                                savedPaymentMethod;
                    }

                    fillPaymentInformation(
                            documentSnapshot.getString(
                                    "rate"
                            )
                    );
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PaymentActivity.this,
                            "Unable to load booking",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                });
    }

    // =========================================================
    // PAYMENT INFORMATION
    // =========================================================

    private void fillPaymentInformation(
            String rate
    ) {

        if (rate == null
                || rate.trim().isEmpty()) {

            rate = "Rs. 0";
        }

        // =====================================================
        // CASH PAYMENT
        // =====================================================

        if (isCashPayment()) {

            tvPaymentMethodIcon.setText(
                    "💵"
            );

            tvPaymentMethodName.setText(
                    "Cash Payment"
            );

            tvPaymentNumber.setText(
                    "Pay Worker in Cash"
            );

            tvPaymentInstruction.setText(
                    "Confirm whether you have paid the worker in cash."
            );

            btnCopyNumber.setVisibility(
                    View.GONE
            );

            btnUploadScreenshot.setVisibility(
                    View.GONE
            );

            btnConfirmPaymentSent.setText(
                    "CONFIRM CASH PAYMENT"
            );

        }

        // =====================================================
        // ONLINE PAYMENT
        // =====================================================

        else {

            tvPaymentMethodIcon.setText(
                    "💳"
            );

            tvPaymentMethodName.setText(
                    "Online Payment"
            );

            tvPaymentNumber.setText(
                    "TEST MODE"
            );

            tvPaymentInstruction.setText(
                    "This is a test payment. Use the test card details shown below."
            );

            btnCopyNumber.setVisibility(
                    View.GONE
            );

            btnUploadScreenshot.setVisibility(
                    View.VISIBLE
            );

            btnConfirmPaymentSent.setText(
                    "PAY WITH STRIPE"
            );
        }
    }

    // =========================================================
    // CHECK CASH PAYMENT
    // =========================================================

    private boolean isCashPayment() {

        if (paymentMethod == null) {
            return false;
        }

        String method =
                paymentMethod
                        .trim()
                        .toLowerCase();

        return method.equals("cash")
                || method.equals("cash payment");
    }

    // =========================================================
    // CASH PAYMENT
    // =========================================================

    private void showCashPaymentDialog() {

        new AlertDialog.Builder(this)

                .setTitle(
                        "💵 Cash Payment"
                )

                .setMessage(
                        "Have you paid the worker in cash?"
                )

                .setNegativeButton(
                        "NOT YET",
                        (dialog, which) -> {

                            // Do NOTHING.
                            // Payment remains Pending.
                            // Pay Now will remain in My Bookings.
                            dialog.dismiss();
                        }
                )

                .setPositiveButton(
                        "YES, PAID",
                        (dialog, which) -> {

                            // Only now mark the booking Paid.
                            markPaymentAsPaid(
                                    "Cash payment confirmed"
                            );
                        }
                )

                .show();
    }

    // =========================================================
    // ONLINE PAYMENT
    // =========================================================

    private void showOnlinePaymentDialog() {

        LinearLayout container =
                new LinearLayout(this);

        container.setOrientation(
                LinearLayout.VERTICAL
        );

        container.setPadding(
                40,
                10,
                40,
                5
        );

        // Test card information
        TextView testModeText =
                new TextView(this);

        testModeText.setText(
                "TEST MODE\n\n"
                        + "Use the following test card:\n"
                        + "Card: 4242 4242 4242 4242\n"
                        + "Expiry: 12/30\n"
                        + "CVV: 123"
        );

        testModeText.setTextSize(
                14
        );

        testModeText.setPadding(
                0,
                0,
                0,
                20
        );

        container.addView(
                testModeText
        );

        // Card number
        EditText cardNumber =
                new EditText(this);

        cardNumber.setHint(
                "Card Number"
        );

        cardNumber.setInputType(
                InputType.TYPE_CLASS_NUMBER
        );

        container.addView(
                cardNumber
        );

        // Expiry
        EditText expiry =
                new EditText(this);

        expiry.setHint(
                "Expiry MM/YY"
        );

        expiry.setInputType(
                InputType.TYPE_CLASS_DATETIME
        );

        container.addView(
                expiry
        );

        // CVV
        EditText cvv =
                new EditText(this);

        cvv.setHint(
                "CVV"
        );

        cvv.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType
                        .TYPE_NUMBER_VARIATION_PASSWORD
        );

        container.addView(
                cvv
        );

        AlertDialog dialog =
                new AlertDialog.Builder(this)

                        .setTitle(
                                "💳 Test Payment"
                        )

                        .setView(
                                container
                        )

                        .setNegativeButton(
                                "CANCEL",
                                null
                        )

                        .setPositiveButton(
                                "PAY NOW",
                                null
                        )

                        .create();

        dialog.setOnShowListener(d -> {

            Button payButton =
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    );

            payButton.setOnClickListener(v -> {

                String card =
                        cardNumber
                                .getText()
                                .toString()
                                .trim();

                String exp =
                        expiry
                                .getText()
                                .toString()
                                .trim();

                String cvvText =
                        cvv
                                .getText()
                                .toString()
                                .trim();

                // Card empty
                if (card.isEmpty()) {

                    cardNumber.setError(
                            "Enter card number"
                    );

                    return;
                }

                // Expiry empty
                if (exp.isEmpty()) {

                    expiry.setError(
                            "Enter expiry"
                    );

                    return;
                }

                // CVV empty
                if (cvvText.isEmpty()) {

                    cvv.setError(
                            "Enter CVV"
                    );

                    return;
                }

                // Test card
                if (!card
                        .replace(" ", "")
                        .equals(
                                "4242424242424242"
                        )) {

                    cardNumber.setError(
                            "Use test card 4242 4242 4242 4242"
                    );

                    return;
                }

                // Test expiry
                if (!exp.equals("12/30")
                        && !exp.equals("1230")) {

                    expiry.setError(
                            "Use expiry 12/30"
                    );

                    return;
                }

                // Test CVV
                if (!cvvText.equals("123")) {

                    cvv.setError(
                            "Use CVV 123"
                    );

                    return;
                }

                dialog.dismiss();

                markPaymentAsPaid(
                        "Online payment successful"
                );
            });
        });

        dialog.show();
    }

    // =========================================================
    // MARK EXISTING BOOKING AS PAID
    // =========================================================

    private void markPaymentAsPaid(
            String message
    ) {

        if (bookingId == null
                || bookingId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Booking ID missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Map<String, Object> paymentUpdate =
                new HashMap<>();

        // IMPORTANT:
        // Only called after YES, PAID for cash
        // or successful online payment.
        paymentUpdate.put(
                "paymentStatus",
                "Paid"
        );

        firestore.collection("bookings")
                .document(bookingId)
                .update(paymentUpdate)

                .addOnSuccessListener(unused -> {

                    if (isCashPayment()) {

                        showPaymentSuccessDialog(
                                "Cash Payment Confirmed",
                                "Payment status has been updated to Paid."
                        );

                    } else {

                        showPaymentSuccessDialog(
                                "Payment Successful",
                                "Your online payment has been completed."
                        );
                    }
                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            PaymentActivity.this,
                            "Payment update failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // SUCCESS
    // =========================================================

    private void showPaymentSuccessDialog(
            String title,
            String message
    ) {

        new AlertDialog.Builder(this)

                .setTitle(
                        "✓  " + title
                )

                .setMessage(
                        message
                )

                .setPositiveButton(
                        "DONE",
                        (dialog, which) -> {

                            // Back to My Bookings.
                            // MyBookingsActivity.onResume()
                            // reloads Firebase data.
                            finish();
                        }
                )

                .setCancelable(false)

                .show();
    }
}