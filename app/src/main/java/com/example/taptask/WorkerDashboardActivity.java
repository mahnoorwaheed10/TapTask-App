package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class WorkerDashboardActivity extends AppCompatActivity {

    // =========================================================
    // HEADER
    // =========================================================

    private TextView workerNameHeader;
    private TextView workerSpecializationHeader;
    private Button btnOnlineToggle;

    private boolean isOnline = true;

    // =========================================================
    // CONTENT
    // =========================================================

    private LinearLayout dashboardContent;
    private LinearLayout chatContent;
    private LinearLayout profileContent;

    private LinearLayout incomingRequestsContainer;
    private LinearLayout activeJobsContainer;
    private LinearLayout workerChatListContainer;

    // =========================================================
    // BOTTOM NAV
    // =========================================================

    private TextView navDashboard;
    private TextView navChat;
    private TextView navProfile;

    // =========================================================
    // PROFILE
    // =========================================================

    private TextView tvWorkerProfileName;
    private TextView tvWorkerProfileSpecialization;
    private TextView tvWorkerProfileJobs;
    private TextView tvWorkerProfileRating;

    private EditText etWorkerName;
    private EditText etWorkerSpecialization;
    private EditText etWorkerPhone;
    private EditText etWorkerArea;
    private EditText etWorkerRate;

    private Button btnSaveWorkerProfile;
    private Button btnWorkerLogout;

    // =========================================================
    // STATS
    // =========================================================

    private TextView tvTotalEarned;
    private TextView tvJobsCompleted;
    private TextView tvAvgRating;

    // =========================================================
    // FIREBASE
    // =========================================================

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String currentWorkerName = "";
    private String currentWorkerDocumentId = "";

    // =========================================================
    // LISTS
    // =========================================================

    private final List<WorkerRequestItem> incomingRequests =
            new ArrayList<>();

    private final List<WorkerRequestItem> activeJobs =
            new ArrayList<>();

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_worker_dashboard);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bindViews();
        setupBottomNav();

        renderIncomingRequests();
        renderActiveJobs();

        loadCurrentWorkerThenBookings();

        // =====================================================
        // ONLINE / BUSY
        // =====================================================

        btnOnlineToggle.setOnClickListener(v ->
                toggleOnlineStatus()
        );

        // =====================================================
        // SAVE PROFILE
        // =====================================================

        btnSaveWorkerProfile.setOnClickListener(v -> {

            String name = etWorkerName.getText()
                    .toString()
                    .trim();

            String specialization = etWorkerSpecialization.getText()
                    .toString()
                    .trim();

            String phone = etWorkerPhone.getText()
                    .toString()
                    .trim();

            String area = etWorkerArea.getText()
                    .toString()
                    .trim();

            String rate = etWorkerRate.getText()
                    .toString()
                    .trim();

            if (currentWorkerDocumentId.isEmpty()) {
                Toast.makeText(
                        this,
                        "Worker profile not found",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (name.isEmpty()) {
                Toast.makeText(
                        this,
                        "Please enter your name",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            db.collection("workers")
                    .document(currentWorkerDocumentId)
                    .update(
                            "name", name,
                            "title", specialization,
                            "profession", specialization,
                            "phone", phone,
                            "area", area,
                            "rate", rate
                    )
                    .addOnSuccessListener(unused -> {

                        currentWorkerName = name;

                        workerNameHeader.setText(name);

                        workerSpecializationHeader.setText(
                                specialization.isEmpty()
                                        ? "Professional Service Provider"
                                        : specialization
                        );

                        tvWorkerProfileName.setText(name);

                        tvWorkerProfileSpecialization.setText(
                                specialization.isEmpty()
                                        ? "Professional Service Provider"
                                        : specialization
                        );

                        Toast.makeText(
                                this,
                                "Profile updated successfully!",
                                Toast.LENGTH_SHORT
                        ).show();

                        loadPendingAndAcceptedBookings();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    this,
                                    "Update failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()
                    );
        });

        // =====================================================
        // LOGOUT
        // =====================================================

        btnWorkerLogout.setOnClickListener(v -> {

            updateWorkerAvailability(false, () -> {

                auth.signOut();

                Intent intent = new Intent(
                        WorkerDashboardActivity.this,
                        AuthActivity.class
                );

                intent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                );

                startActivity(intent);
            });
        });
    }

    // =========================================================
    // BIND VIEWS
    // =========================================================

    private void bindViews() {

        workerNameHeader =
                findViewById(R.id.workerNameHeader);

        workerSpecializationHeader =
                findViewById(R.id.workerSpecializationHeader);

        btnOnlineToggle =
                findViewById(R.id.btnOnlineToggle);

        dashboardContent =
                findViewById(R.id.dashboardContent);

        chatContent =
                findViewById(R.id.chatContent);

        profileContent =
                findViewById(R.id.profileContent);

        incomingRequestsContainer =
                findViewById(R.id.incomingRequestsContainer);

        activeJobsContainer =
                findViewById(R.id.activeJobsContainer);

        workerChatListContainer =
                findViewById(R.id.workerChatListContainer);

        navDashboard =
                findViewById(R.id.navDashboard);

        navChat =
                findViewById(R.id.navChat);

        navProfile =
                findViewById(R.id.navProfile);

        tvWorkerProfileName =
                findViewById(R.id.tvWorkerProfileName);

        tvWorkerProfileSpecialization =
                findViewById(R.id.tvWorkerProfileSpecialization);

        tvWorkerProfileJobs =
                findViewById(R.id.tvWorkerProfileJobs);

        tvWorkerProfileRating =
                findViewById(R.id.tvWorkerProfileRating);

        etWorkerName =
                findViewById(R.id.etWorkerName);

        etWorkerSpecialization =
                findViewById(R.id.etWorkerSpecialization);

        etWorkerPhone =
                findViewById(R.id.etWorkerPhone);

        etWorkerArea =
                findViewById(R.id.etWorkerArea);

        etWorkerRate =
                findViewById(R.id.etWorkerRate);

        btnSaveWorkerProfile =
                findViewById(R.id.btnSaveWorkerProfile);

        btnWorkerLogout =
                findViewById(R.id.btnWorkerLogout);

        tvTotalEarned =
                findViewById(R.id.tvTotalEarned);

        tvJobsCompleted =
                findViewById(R.id.tvJobsCompleted);

        tvAvgRating =
                findViewById(R.id.tvAvgRating);
    }

    // =========================================================
    // BOTTOM NAVIGATION
    // =========================================================

    private void setupBottomNav() {

        navDashboard.setOnClickListener(v ->
                switchTab(0)
        );

        navChat.setOnClickListener(v -> {
            switchTab(1);
            loadWorkerChats();
        });

        navProfile.setOnClickListener(v ->
                switchTab(2)
        );

        switchTab(0);
    }

    private void switchTab(int index) {

        dashboardContent.setVisibility(
                index == 0 ? View.VISIBLE : View.GONE
        );

        chatContent.setVisibility(
                index == 1 ? View.VISIBLE : View.GONE
        );

        profileContent.setVisibility(
                index == 2 ? View.VISIBLE : View.GONE
        );

        updateNavLabel(navDashboard, index == 0);
        updateNavLabel(navChat, index == 1);
        updateNavLabel(navProfile, index == 2);
    }

    private void updateNavLabel(
            TextView nav,
            boolean active
    ) {

        nav.setTextColor(
                getResources().getColor(
                        active
                                ? R.color.primary
                                : R.color.muted
                )
        );

        nav.setTypeface(
                null,
                active
                        ? android.graphics.Typeface.BOLD
                        : android.graphics.Typeface.NORMAL
        );
    }

    // =========================================================
    // ONLINE / BUSY
    // =========================================================

    private void toggleOnlineStatus() {

        isOnline = !isOnline;

        btnOnlineToggle.setText(
                isOnline
                        ? "●  Online"
                        : "●  Busy"
        );

        updateWorkerAvailability(
                isOnline,
                null
        );
    }

    // =========================================================
    // LOAD CURRENT WORKER
    // =========================================================

    private void loadCurrentWorkerThenBookings() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Not logged in.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String uid = user.getUid();

        // -----------------------------------------------------
        // 1. workers/{UID}
        // -----------------------------------------------------

        db.collection("workers")
                .document(uid)
                .get()
                .addOnSuccessListener(workerDocument -> {

                    if (workerDocument.exists()) {

                        currentWorkerDocumentId =
                                workerDocument.getId();

                        loadWorkerData(workerDocument);

                        loadPendingAndAcceptedBookings();

                        updateWorkerAvailability(
                                true,
                                null
                        );

                        return;
                    }

                    // -------------------------------------------------
                    // 2. authUid
                    // -------------------------------------------------

                    db.collection("workers")
                            .whereEqualTo("authUid", uid)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(snapshot -> {

                                if (!snapshot.isEmpty()) {

                                    DocumentSnapshot doc =
                                            snapshot.getDocuments().get(0);

                                    currentWorkerDocumentId =
                                            doc.getId();

                                    loadWorkerData(doc);

                                    loadPendingAndAcceptedBookings();

                                    updateWorkerAvailability(
                                            true,
                                            null
                                    );

                                    return;
                                }

                                // -------------------------------------
                                // 3. users/{UID}
                                // -------------------------------------

                                loadWorkerFromUsers(uid);
                            })
                            .addOnFailureListener(e ->
                                    loadWorkerFromUsers(uid)
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Could not load worker profile: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    // =========================================================
    // LOAD WORKER DATA
    // =========================================================

    private void loadWorkerData(
            DocumentSnapshot doc
    ) {

        currentWorkerDocumentId =
                doc.getId();

        String name = doc.getString("name");
        String profession = doc.getString("profession");
        String title = doc.getString("title");
        String category = doc.getString("category");

        String phone = doc.getString("phone");
        String area = doc.getString("area");
        String rate = doc.getString("rate");

        String displayProfession = profession;

        if (displayProfession == null ||
                displayProfession.trim().isEmpty()) {

            displayProfession = title;
        }

        if (displayProfession == null ||
                displayProfession.trim().isEmpty()) {

            displayProfession = category;
        }

        if (displayProfession == null ||
                displayProfession.trim().isEmpty()) {

            displayProfession =
                    "Professional Service Provider";
        }

        if (name == null ||
                name.trim().isEmpty()) {

            name = "Worker";
        }

        currentWorkerName = name;

        // =====================================================
        // HEADER
        // =====================================================

        workerNameHeader.setText(name);

        workerSpecializationHeader.setText(
                displayProfession
        );

        // =====================================================
        // PROFILE
        // =====================================================

        tvWorkerProfileName.setText(name);

        tvWorkerProfileSpecialization.setText(
                displayProfession
        );

        etWorkerName.setText(name);

        etWorkerSpecialization.setText(
                displayProfession
        );

        etWorkerPhone.setText(
                phone != null ? phone : ""
        );

        etWorkerArea.setText(
                area != null ? area : ""
        );

        etWorkerRate.setText(
                rate != null ? rate : ""
        );

        // =====================================================
        // AVAILABILITY
        // =====================================================

        Boolean available =
                doc.getBoolean("isAvailable");

        if (available != null) {

            isOnline = available;

            btnOnlineToggle.setText(
                    available
                            ? "●  Online"
                            : "●  Busy"
            );
        }

        // =====================================================
        // RATING
        // =====================================================

        Double rating =
                doc.getDouble("rating");

        if (rating != null) {

            tvWorkerProfileRating.setText(
                    String.format(
                            Locale.US,
                            "%.1f ⭐",
                            rating
                    )
            );

            tvAvgRating.setText(
                    String.format(
                            Locale.US,
                            "%.1f ⭐",
                            rating
                    )
            );

        } else {

            tvWorkerProfileRating.setText("0.0 ⭐");
            tvAvgRating.setText("0.0 ⭐");
        }

        tvWorkerProfileJobs.setText("0");
        tvJobsCompleted.setText("0");
        tvTotalEarned.setText("Rs. 0");
    }

    // =========================================================
    // USERS FALLBACK
    // =========================================================

    private void loadWorkerFromUsers(String uid) {

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(userDocument -> {

                    String name =
                            userDocument.getString("name");

                    if (name == null ||
                            name.trim().isEmpty()) {

                        name = "Worker";
                    }

                    currentWorkerName = name;

                    workerNameHeader.setText(name);

                    tvWorkerProfileName.setText(name);

                    etWorkerName.setText(name);

                    loadWorkerByName(name);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Could not load worker profile",
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void loadWorkerByName(String name) {

        db.collection("workers")
                .whereEqualTo("name", name)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.isEmpty()) {

                        loadWorkerData(
                                snapshot.getDocuments().get(0)
                        );
                    }

                    loadPendingAndAcceptedBookings();

                    updateWorkerAvailability(
                            true,
                            null
                    );
                });
    }

    // =========================================================
    // BOOKINGS
    // =========================================================

    private void loadPendingAndAcceptedBookings() {

        if (currentWorkerName == null ||
                currentWorkerName.trim().isEmpty()) {

            return;
        }

        db.collection("bookings")
                .whereEqualTo(
                        "workerName",
                        currentWorkerName
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    incomingRequests.clear();
                    activeJobs.clear();

                    int completedCount = 0;
                    double totalEarned = 0;

                    for (QueryDocumentSnapshot doc :
                            querySnapshot) {

                        String status =
                                doc.getString("status");

                        if ("Completed".equals(status)) {

                            completedCount++;

                            String rate =
                                    doc.getString("workerRate");

                            totalEarned +=
                                    parseRate(rate);

                            continue;
                        }

                        String customerId =
                                doc.getString("customerId");

                        String customerName =
                                doc.getString("customerName");

                        if (customerName == null ||
                                customerName.trim().isEmpty()) {

                            customerName = "Customer";
                        }

                        String serviceTitle =
                                doc.getString("serviceTitle");

                        if (serviceTitle == null ||
                                serviceTitle.trim().isEmpty()) {

                            serviceTitle =
                                    doc.getString("description");
                        }

                        if (serviceTitle == null) {
                            serviceTitle = "";
                        }

                        String address =
                                doc.getString("address");

                        if (address == null) {
                            address = "";
                        }

                        String date =
                                doc.getString("date");

                        String time =
                                doc.getString("time");

                        String timing =
                                (date != null ? date : "")
                                        +
                                        (time != null
                                                ? ", " + time
                                                : "");

                        WorkerRequestItem item =
                                new WorkerRequestItem(
                                        customerName,
                                        serviceTitle,
                                        address,
                                        timing,
                                        doc.getId()
                                );

                        item.customerId =
                                customerId;

                        if ("Pending".equals(status)) {

                            incomingRequests.add(item);

                        } else if ("Accepted".equals(status)) {

                            activeJobs.add(item);
                        }
                    }

                    tvWorkerProfileJobs.setText(
                            String.valueOf(completedCount)
                    );

                    tvJobsCompleted.setText(
                            String.valueOf(completedCount)
                    );

                    tvTotalEarned.setText(
                            "Rs. " +
                                    String.format(
                                            Locale.US,
                                            "%.0f",
                                            totalEarned
                                    )
                    );

                    renderIncomingRequests();
                    renderActiveJobs();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Could not load bookings: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    // =========================================================
    // WORKER CHATS
    // =========================================================

    private void loadWorkerChats() {

        workerChatListContainer.removeAllViews();

        if (currentWorkerName == null ||
                currentWorkerName.trim().isEmpty()) {

            workerChatListContainer.addView(
                    createEmptyText("Loading chats...")
            );

            return;
        }

        db.collection("bookings")
                .whereEqualTo(
                        "workerName",
                        currentWorkerName
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    workerChatListContainer.removeAllViews();

                    if (querySnapshot.isEmpty()) {

                        workerChatListContainer.addView(
                                createEmptyText("No chats yet.")
                        );

                        return;
                    }

                    Set<String> addedCustomerIds =
                            new HashSet<>();

                    for (QueryDocumentSnapshot doc :
                            querySnapshot) {

                        String status =
                                doc.getString("status");

                        if (!"Accepted".equals(status) &&
                                !"Completed".equals(status)) {

                            continue;
                        }

                        String customerId =
                                doc.getString("customerId");

                        if (customerId == null ||
                                customerId.trim().isEmpty()) {

                            continue;
                        }

                        if (addedCustomerIds.contains(
                                customerId
                        )) {

                            continue;
                        }

                        addedCustomerIds.add(customerId);

                        String serviceTitle =
                                doc.getString("serviceTitle");

                        if (serviceTitle == null ||
                                serviceTitle.trim().isEmpty()) {

                            serviceTitle =
                                    "Service booking";
                        }

                        loadCustomerNameForChat(
                                customerId,
                                serviceTitle
                        );
                    }

                    if (addedCustomerIds.isEmpty()) {

                        workerChatListContainer.addView(
                                createEmptyText(
                                        "No active chats yet."
                                )
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    workerChatListContainer.removeAllViews();

                    workerChatListContainer.addView(
                            createEmptyText(
                                    "Could not load chats."
                            )
                    );
                });
    }

    private void loadCustomerNameForChat(
            String customerId,
            String serviceTitle
    ) {

        db.collection("users")
                .document(customerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    String customerName =
                            documentSnapshot.getString("name");

                    if (customerName == null ||
                            customerName.trim().isEmpty()) {

                        customerName = "Customer";
                    }

                    createWorkerChatCard(
                            customerName,
                            customerId,
                            serviceTitle
                    );
                })
                .addOnFailureListener(e ->
                        createWorkerChatCard(
                                "Customer",
                                customerId,
                                serviceTitle
                        )
                );
    }
    // =========================================================
    // CREATE WORKER CHAT CARD
    // =========================================================

    private void createWorkerChatCard(
            String customerName,
            String customerId,
            String serviceTitle
    ) {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                Gravity.CENTER_VERTICAL
        );

        row.setBackgroundResource(
                R.drawable.bg_card
        );

        row.setPadding(
                dp(14),
                dp(14),
                dp(14),
                dp(14)
        );

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        rowParams.bottomMargin = dp(8);

        row.setLayoutParams(rowParams);

        TextView avatar =
                new TextView(this);

        avatar.setText("💬");
        avatar.setTextSize(24);
        avatar.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams avatarParams =
                new LinearLayout.LayoutParams(
                        dp(48),
                        dp(48)
                );

        avatarParams.setMarginEnd(dp(12));

        avatar.setLayoutParams(avatarParams);

        row.addView(avatar);

        LinearLayout textArea =
                new LinearLayout(this);

        textArea.setOrientation(
                LinearLayout.VERTICAL
        );

        textArea.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        TextView name =
                new TextView(this);

        name.setText(customerName);
        name.setTextColor(
                getResources().getColor(
                        R.color.text_main
                )
        );
        name.setTextSize(16);
        name.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        textArea.addView(name);

        TextView service =
                new TextView(this);

        service.setText(serviceTitle);
        service.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );
        service.setTextSize(12);

        textArea.addView(service);

        row.addView(textArea);

        TextView arrow =
                new TextView(this);

        arrow.setText("›");
        arrow.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );
        arrow.setTextSize(28);
        arrow.setGravity(Gravity.CENTER);

        row.addView(arrow);

        row.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WorkerDashboardActivity.this,
                            ChatActivity.class
                    );

            intent.putExtra(
                    ChatActivity.EXTRA_WORKER_NAME,
                    currentWorkerName
            );

            intent.putExtra(
                    ChatActivity.EXTRA_CUSTOMER_NAME,
                    customerName
            );

            intent.putExtra(
                    ChatActivity.EXTRA_CUSTOMER_ID,
                    customerId
            );

            startActivity(intent);
        });

        workerChatListContainer.addView(row);
    }

    // =========================================================
    // INCOMING REQUESTS
    // =========================================================

    private void renderIncomingRequests() {

        incomingRequestsContainer.removeAllViews();

        if (incomingRequests.isEmpty()) {

            incomingRequestsContainer.addView(
                    createEmptyText(
                            "No incoming requests right now."
                    )
            );

            return;
        }

        for (WorkerRequestItem item :
                incomingRequests) {

            incomingRequestsContainer.addView(
                    createIncomingRequestCard(item)
            );
        }
    }

    // =========================================================
    // ACTIVE JOBS
    // =========================================================

    private void renderActiveJobs() {

        activeJobsContainer.removeAllViews();

        if (activeJobs.isEmpty()) {

            activeJobsContainer.addView(
                    createEmptyText(
                            "No active jobs right now."
                    )
            );

            return;
        }

        for (WorkerRequestItem item :
                activeJobs) {

            activeJobsContainer.addView(
                    createActiveJobCard(item)
            );
        }
    }

    // =========================================================
    // EMPTY TEXT
    // =========================================================

    private TextView createEmptyText(String text) {

        TextView tv =
                new TextView(this);

        tv.setText(text);

        tv.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        tv.setTextSize(13);

        tv.setGravity(Gravity.CENTER);

        tv.setPadding(
                0,
                dp(15),
                0,
                dp(15)
        );

        return tv;
    }

    // =========================================================
    // INCOMING REQUEST CARD
    // =========================================================

    private LinearLayout createIncomingRequestCard(
            WorkerRequestItem item
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setBackgroundResource(
                R.drawable.bg_card
        );

        card.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.bottomMargin = dp(12);

        card.setLayoutParams(cardParams);

        TextView name =
                new TextView(this);

        name.setText(item.customerName);

        name.setTextColor(
                getResources().getColor(
                        R.color.text_main
                )
        );

        name.setTextSize(15);

        name.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        card.addView(name);

        TextView desc =
                new TextView(this);

        desc.setText(item.description);

        desc.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        desc.setTextSize(13);

        LinearLayout.LayoutParams descParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        descParams.topMargin = dp(5);

        desc.setLayoutParams(descParams);

        card.addView(desc);

        TextView details =
                new TextView(this);

        details.setText(
                "📍 " +
                        item.area +
                        "   •   🕐 " +
                        item.timing
        );

        details.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        details.setTextSize(12);

        LinearLayout.LayoutParams detailsParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        detailsParams.topMargin = dp(7);
        detailsParams.bottomMargin = dp(12);

        details.setLayoutParams(detailsParams);

        card.addView(details);

        LinearLayout buttonsRow =
                new LinearLayout(this);

        buttonsRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        // =====================================================
        // ACCEPT
        // =====================================================

        TextView btnAccept =
                new TextView(this);

        btnAccept.setText(
                getString(R.string.accept)
        );

        btnAccept.setTextColor(
                getResources().getColor(
                        R.color.white
                )
        );

        btnAccept.setTextSize(13);

        btnAccept.setGravity(Gravity.CENTER);

        btnAccept.setBackgroundResource(
                R.drawable.bg_btn_green
        );

        btnAccept.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(10)
        );

        LinearLayout.LayoutParams acceptParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        acceptParams.setMarginEnd(dp(8));

        btnAccept.setLayoutParams(
                acceptParams
        );

        btnAccept.setOnClickListener(v -> {

            db.collection("bookings")
                    .document(item.bookingId)
                    .update(
                            "status",
                            "Accepted"
                    )
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                "Request accepted!",
                                Toast.LENGTH_SHORT
                        ).show();

                        incomingRequests.remove(item);
                        activeJobs.add(item);

                        renderIncomingRequests();
                        renderActiveJobs();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    this,
                                    "Could not accept request: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()
                    );
        });

        buttonsRow.addView(btnAccept);

        // =====================================================
        // DECLINE
        // =====================================================

        TextView btnDecline =
                new TextView(this);

        btnDecline.setText(
                getString(R.string.decline)
        );

        btnDecline.setTextColor(
                getResources().getColor(
                        R.color.white
                )
        );

        btnDecline.setTextSize(13);

        btnDecline.setGravity(Gravity.CENTER);

        btnDecline.setBackgroundResource(
                R.drawable.bg_btn_red
        );

        btnDecline.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(10)
        );

        btnDecline.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        btnDecline.setOnClickListener(v -> {

            db.collection("bookings")
                    .document(item.bookingId)
                    .update(
                            "status",
                            "Rejected"
                    )
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                "Request declined.",
                                Toast.LENGTH_SHORT
                        ).show();

                        incomingRequests.remove(item);

                        renderIncomingRequests();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    this,
                                    "Could not decline request: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()
                    );
        });

        buttonsRow.addView(btnDecline);

        card.addView(buttonsRow);

        return card;
    }

    // =========================================================
    // ACTIVE JOB CARD
    // =========================================================

    private LinearLayout createActiveJobCard(
            WorkerRequestItem item
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setBackgroundResource(
                R.drawable.bg_card
        );

        card.setPadding(
                dp(16),
                dp(16),
                dp(16),
                dp(16)
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.bottomMargin = dp(12);

        card.setLayoutParams(cardParams);

        TextView name =
                new TextView(this);

        name.setText(item.customerName);

        name.setTextColor(
                getResources().getColor(
                        R.color.text_main
                )
        );

        name.setTextSize(15);

        name.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        card.addView(name);

        TextView desc =
                new TextView(this);

        desc.setText(
                item.description +
                        " — " +
                        item.timing
        );

        desc.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        desc.setTextSize(13);

        LinearLayout.LayoutParams descParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        descParams.bottomMargin = dp(12);

        desc.setLayoutParams(descParams);

        card.addView(desc);

        LinearLayout buttonsRow =
                new LinearLayout(this);

        buttonsRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

        // =====================================================
        // CHAT
        // =====================================================

        TextView btnChat =
                new TextView(this);

        btnChat.setText("💬 Chat");

        btnChat.setTextColor(
                getResources().getColor(
                        R.color.primary
                )
        );

        btnChat.setTextSize(13);

        btnChat.setGravity(Gravity.CENTER);

        btnChat.setBackgroundResource(
                R.drawable.bg_btn_outline
        );

        btnChat.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(10)
        );

        LinearLayout.LayoutParams chatParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        chatParams.setMarginEnd(dp(8));

        btnChat.setLayoutParams(chatParams);

        btnChat.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            WorkerDashboardActivity.this,
                            ChatActivity.class
                    );

            intent.putExtra(
                    ChatActivity.EXTRA_WORKER_NAME,
                    currentWorkerName
            );

            intent.putExtra(
                    ChatActivity.EXTRA_CUSTOMER_NAME,
                    item.customerName
            );

            intent.putExtra(
                    ChatActivity.EXTRA_CUSTOMER_ID,
                    item.customerId
            );

            startActivity(intent);
        });

        buttonsRow.addView(btnChat);

        // =====================================================
        // MARK DONE
        // =====================================================

        TextView btnMarkDone =
                new TextView(this);

        btnMarkDone.setText(
                "✅ Mark Done"
        );

        btnMarkDone.setTextColor(
                getResources().getColor(
                        R.color.white
                )
        );

        btnMarkDone.setTextSize(13);

        btnMarkDone.setGravity(Gravity.CENTER);

        btnMarkDone.setBackgroundResource(
                R.drawable.bg_btn_green
        );

        btnMarkDone.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(10)
        );

        btnMarkDone.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                )
        );

        btnMarkDone.setOnClickListener(v -> {

            if (item.bookingId == null ||
                    item.bookingId.isEmpty()) {

                Toast.makeText(
                        this,
                        "Booking ID not found",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            btnMarkDone.setEnabled(false);

            db.collection("bookings")
                    .document(item.bookingId)
                    .update(
                            "status",
                            "Completed"
                    )
                    .addOnSuccessListener(unused -> {

                        Toast.makeText(
                                this,
                                "Completed successfully!",
                                Toast.LENGTH_LONG
                        ).show();

                        activeJobs.remove(item);

                        renderActiveJobs();

                        loadPendingAndAcceptedBookings();
                    })
                    .addOnFailureListener(e -> {

                        btnMarkDone.setEnabled(true);

                        Toast.makeText(
                                this,
                                "Could not complete job: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });

        buttonsRow.addView(btnMarkDone);

        card.addView(buttonsRow);

        return card;
    }

    // =========================================================
    // AVAILABILITY
    // =========================================================

    private void updateWorkerAvailability(
            boolean available,
            Runnable afterDone
    ) {

        if (currentWorkerDocumentId != null &&
                !currentWorkerDocumentId.isEmpty()) {

            db.collection("workers")
                    .document(currentWorkerDocumentId)
                    .update(
                            "isAvailable",
                            available
                    )
                    .addOnCompleteListener(task -> {

                        if (afterDone != null) {
                            afterDone.run();
                        }
                    });

            return;
        }

        if (currentWorkerName == null ||
                currentWorkerName.trim().isEmpty()) {

            if (afterDone != null) {
                afterDone.run();
            }

            return;
        }

        db.collection("workers")
                .whereEqualTo(
                        "name",
                        currentWorkerName
                )
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!snapshot.isEmpty()) {

                        snapshot.getDocuments()
                                .get(0)
                                .getReference()
                                .update(
                                        "isAvailable",
                                        available
                                );
                    }

                    if (afterDone != null) {
                        afterDone.run();
                    }
                })
                .addOnFailureListener(e -> {

                    if (afterDone != null) {
                        afterDone.run();
                    }
                });
    }

    // =========================================================
    // RATE PARSER
    // =========================================================

    private double parseRate(String rate) {

        if (rate == null ||
                rate.trim().isEmpty()) {

            return 0;
        }

        try {

            String cleaned =
                    rate.replaceAll(
                            "[^0-9.]",
                            ""
                    );

            if (cleaned.isEmpty()) {
                return 0;
            }

            return Double.parseDouble(cleaned);

        } catch (Exception e) {

            return 0;
        }
    }

    // =========================================================
    // DP
    // =========================================================

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int) (
                value * density
        );
    }

    // =========================================================
    // WORKER REQUEST ITEM
    // =========================================================

    private static class WorkerRequestItem {

        String customerName;
        String description;
        String area;
        String timing;
        String bookingId;
        String customerId;

        WorkerRequestItem(
                String customerName,
                String description,
                String area,
                String timing,
                String bookingId
        ) {

            this.customerName = customerName;
            this.description = description;
            this.area = area;
            this.timing = timing;
            this.bookingId = bookingId;
            this.customerId = "";
        }
    }
}