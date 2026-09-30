package com.example.taptask;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

    // ============================================================
    // INTENT EXTRAS
    // ============================================================

    public static final String EXTRA_WORKER_NAME = "worker_name";
    public static final String EXTRA_WORKER_TITLE = "worker_title";
    public static final String EXTRA_WORKER_RATE = "worker_rate";

    // Home / Shop / Online
    public static final String EXTRA_MAIN_CAT = "main_cat";

    // ============================================================
    // VIEWS
    // ============================================================

    private TextView btnBack;

    private TextView tvBookingWorkerName;
    private TextView tvBookingWorkerTitle;
    private TextView tvBookingWorkerRate;
    private TextView tvBookingAddressLabel;

    private EditText etBookingDate;
    private EditText etBookingTime;
    private EditText etBookingDesc;
    private EditText etBookingAddress;

    private LinearLayout paymentCash;
    private LinearLayout paymentSafepay;

    private Button btnConfirmBooking;

    // ============================================================
    // PAYMENT PREFERENCE
    // ============================================================

    /*
     * This is ONLY the customer's payment preference.
     * No payment happens on the booking screen.
     */
    private String selectedPayment = "cash";

    // ============================================================
    // WORKER / CATEGORY DATA
    // ============================================================

    private String workerName;
    private String workerTitle;
    private String workerRate;

    // Home / Shop / Online
    private String mainCatKey;

    // ============================================================
    // FIREBASE
    // ============================================================

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    // ============================================================
    // ON CREATE
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_booking);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // ========================================================
        // GET WORKER DATA
        // ========================================================

        workerName = getIntent().getStringExtra(
                EXTRA_WORKER_NAME
        );

        workerTitle = getIntent().getStringExtra(
                EXTRA_WORKER_TITLE
        );

        workerRate = getIntent().getStringExtra(
                EXTRA_WORKER_RATE
        );

        // ========================================================
        // GET MAIN CATEGORY
        // ========================================================

        mainCatKey = getIntent().getStringExtra(
                EXTRA_MAIN_CAT
        );

        // If main category is missing, Home is default
        if (mainCatKey == null ||
                mainCatKey.trim().isEmpty()) {

            mainCatKey = "home";
        }

        // ========================================================
        // DEFAULT VALUES
        // ========================================================

        if (workerName == null) {
            workerName = "Worker";
        }

        if (workerTitle == null) {
            workerTitle = "";
        }

        if (workerRate == null) {
            workerRate = "";
        }

        // ========================================================
        // SETUP
        // ========================================================

        bindViews();

        fillWorkerInfo();

        setupAddressForServiceType();

        setupPaymentSelection();

        setupConfirmButton();

        btnBack.setOnClickListener(
                v -> finish()
        );
    }

    // ============================================================
    // BIND VIEWS
    // ============================================================

    private void bindViews() {

        btnBack = findViewById(
                R.id.btnBack
        );

        tvBookingWorkerName =
                findViewById(
                        R.id.tvBookingWorkerName
                );

        tvBookingWorkerTitle =
                findViewById(
                        R.id.tvBookingWorkerTitle
                );

        tvBookingWorkerRate =
                findViewById(
                        R.id.tvBookingWorkerRate
                );

        tvBookingAddressLabel =
                findViewById(
                        R.id.tvBookingAddressLabel
                );

        etBookingDate =
                findViewById(
                        R.id.etBookingDate
                );

        etBookingTime =
                findViewById(
                        R.id.etBookingTime
                );

        etBookingDesc =
                findViewById(
                        R.id.etBookingDesc
                );

        etBookingAddress =
                findViewById(
                        R.id.etBookingAddress
                );

        paymentCash =
                findViewById(
                        R.id.paymentCash
                );

        paymentSafepay =
                findViewById(
                        R.id.paymentSafepay
                );

        btnConfirmBooking =
                findViewById(
                        R.id.btnConfirmBooking
                );
    }

    // ============================================================
    // WORKER INFO
    // ============================================================

    private void fillWorkerInfo() {

        tvBookingWorkerName.setText(
                workerName
        );

        tvBookingWorkerTitle.setText(
                workerTitle
        );

        tvBookingWorkerRate.setText(
                workerRate
        );
    }

    // ============================================================
    // ADDRESS LOGIC
    // ============================================================

    private void setupAddressForServiceType() {

        /*
         * HOME:
         * Address is required.
         */
        if ("home".equalsIgnoreCase(mainCatKey)) {

            tvBookingAddressLabel.setVisibility(
                    View.VISIBLE
            );

            etBookingAddress.setVisibility(
                    View.VISIBLE
            );

        }

        /*
         * SHOP / ONLINE:
         * Customer address is not required.
         */
        else {

            tvBookingAddressLabel.setVisibility(
                    View.GONE
            );

            etBookingAddress.setVisibility(
                    View.GONE
            );

            // Make sure no old address is accidentally saved
            etBookingAddress.setText("");
        }
    }

    // ============================================================
    // PAYMENT SELECTION
    // ============================================================

    private void setupPaymentSelection() {

        paymentCash.setOnClickListener(
                v -> selectPayment("cash")
        );

        paymentSafepay.setOnClickListener(
                v -> selectPayment("online")
        );

        /*
         * Default preference is Cash.
         *
         * This does NOT make a payment.
         */
        selectPayment("cash");
    }

    // ============================================================
    // SELECT PAYMENT
    // ============================================================

    private void selectPayment(String method) {

        selectedPayment = method;

        paymentCash.setBackgroundResource(
                R.drawable.bg_payment_card
        );

        paymentSafepay.setBackgroundResource(
                R.drawable.bg_payment_card
        );

        if (method.equals("online")) {

            paymentSafepay.setBackgroundResource(
                    R.drawable.bg_payment_card_selected
            );

        } else {

            paymentCash.setBackgroundResource(
                    R.drawable.bg_payment_card_selected
            );
        }
    }

    // ============================================================
    // CONFIRM BUTTON
    // ============================================================

    private void setupConfirmButton() {

        btnConfirmBooking.setOnClickListener(
                v -> handleConfirm()
        );
    }

    // ============================================================
    // HANDLE CONFIRM
    // ============================================================

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

        // ========================================================
        // DATE + TIME ALWAYS REQUIRED
        // ========================================================

        if (TextUtils.isEmpty(date)
                || TextUtils.isEmpty(time)) {

            Toast.makeText(
                    this,
                    "Please fill date and time",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ========================================================
        // ADDRESS ONLY REQUIRED FOR HOME
        // ========================================================

        if ("home".equalsIgnoreCase(mainCatKey)
                && TextUtils.isEmpty(address)) {

            Toast.makeText(
                    this,
                    "Please fill your address",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ========================================================
        // LOGIN CHECK
        // ========================================================

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

        // ========================================================
        // SAVE BOOKING
        // ========================================================

        /*
         * Online and Cash are ONLY preferences here.
         *
         * No payment dialog.
         * No card details.
         * No payment processing.
         *
         * Both methods create the booking with
         * paymentStatus = Pending.
         */
        saveBooking();
    }

    // ============================================================
    // SAVE BOOKING
    // ============================================================

    private void saveBooking() {

        btnConfirmBooking.setEnabled(false);

        FirebaseUser currentUser =
                firebaseAuth.getCurrentUser();

        if (currentUser == null) {

            btnConfirmBooking.setEnabled(true);

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ========================================================
        // GET FORM DATA
        // ========================================================

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

        // ========================================================
        // CUSTOMER
        // ========================================================

        String customerId =
                currentUser.getUid();

        String customerEmail =
                currentUser.getEmail() != null
                        ? currentUser.getEmail()
                        : "";

        // ========================================================
        // BOOKING MAP
        // ========================================================

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

        // ========================================================
        // ADDRESS
        // ========================================================

        /*
         * Home:
         * actual customer address is saved.
         *
         * Shop / Online:
         * address is saved as empty because it is not required.
         */
        booking.put(
                "address",
                address
        );

        // ========================================================
        // PAYMENT METHOD
        // ========================================================

        /*
         * cash   = Cash preference
         * online = Online preference
         */
        booking.put(
                "paymentMethod",
                selectedPayment
        );

        // ========================================================
        // PAYMENT STATUS
        // ========================================================

        /*
         * Actual payment has NOT happened yet.
         *
         * Therefore both Cash and Online start
         * with Pending payment.
         */
        booking.put(
                "paymentStatus",
                "Pending"
        );

        // ========================================================
        // BOOKING STATUS
        // ========================================================

        /*
         * Booking status is separate from payment status.
         */
        booking.put(
                "status",
                "Pending"
        );

        // ========================================================
        // CREATED TIME
        // ========================================================

        booking.put(
                "createdAt",
                System.currentTimeMillis()
        );

        // ========================================================
        // FIRESTORE
        // ========================================================

        firestore
                .collection("bookings")
                .add(booking)
                .addOnSuccessListener(
                        documentReference -> {

                            Toast.makeText(
                                    BookingActivity.this,
                                    "Booking confirmed successfully!",
                                    Toast.LENGTH_LONG
                            ).show();

                            finish();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            btnConfirmBooking.setEnabled(
                                    true
                            );

                            Toast.makeText(
                                    BookingActivity.this,
                                    "Booking failed: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }
}