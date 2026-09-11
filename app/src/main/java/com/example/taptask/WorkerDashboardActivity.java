package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkerDashboardActivity extends AppCompatActivity {

    private TextView btnOnlineToggle;
    private boolean isOnline = true;

    private LinearLayout incomingRequestsContainer;
    private LinearLayout activeJobsContainer;
    private LinearLayout workerChatListContainer;

    private List<WorkerRequestItem> incomingRequests = new ArrayList<>();
    private List<WorkerRequestItem> activeJobs = new ArrayList<>();

    private LinearLayout navDashboard, navChat, navProfile;
    private ScrollView tabDashboard, tabProfile;
    private LinearLayout tabChat;

    private EditText etWorkerName, etWorkerSpecialization, etWorkerPhone,
            etWorkerArea, etWorkerRate;

    private Button btnSaveWorkerProfile, btnWorkerLogout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String currentWorkerName = "";
    private String currentWorkerDocumentId = "";

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

        btnOnlineToggle.setOnClickListener(v -> toggleOnlineStatus());

        btnSaveWorkerProfile.setOnClickListener(v -> {

            String name = etWorkerName.getText().toString().trim();
            String specialization = etWorkerSpecialization.getText().toString().trim();
            String phone = etWorkerPhone.getText().toString().trim();
            String area = etWorkerArea.getText().toString().trim();
            String rate = etWorkerRate.getText().toString().trim();

            if (currentWorkerDocumentId.isEmpty()) {
                Toast.makeText(
                        this,
                        "Worker profile not found",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            db.collection("workers")
                    .document(currentWorkerDocumentId)
                    .update(
                            "name", name,
                            "title", specialization,
                            "phone", phone,
                            "area", area,
                            "rate", rate
                    )
                    .addOnSuccessListener(unused -> {

                        currentWorkerName = name;

                        Toast.makeText(
                                this,
                                "Profile updated successfully!",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(
                                    this,
                                    "Update failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()
                    );
        });

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

    private void bindViews() {

        btnOnlineToggle = findViewById(R.id.btnOnlineToggle);

        incomingRequestsContainer =
                findViewById(R.id.incomingRequestsContainer);

        activeJobsContainer =
                findViewById(R.id.activeJobsContainer);

        workerChatListContainer =
                findViewById(R.id.workerChatListContainer);

        navDashboard = findViewById(R.id.navDashboard);
        navChat = findViewById(R.id.navChat);
        navProfile = findViewById(R.id.navProfile);

        tabDashboard = findViewById(R.id.tabDashboard);
        tabChat = findViewById(R.id.tabChat);
        tabProfile = findViewById(R.id.tabProfile);

        etWorkerName = findViewById(R.id.etWorkerName);

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
    }

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

        tabDashboard.setVisibility(
                index == 0 ? View.VISIBLE : View.GONE
        );

        tabChat.setVisibility(
                index == 1 ? View.VISIBLE : View.GONE
        );

        tabProfile.setVisibility(
                index == 2 ? View.VISIBLE : View.GONE
        );

        updateNavLabel(
                navDashboard,
                index == 0
        );

        updateNavLabel(
                navChat,
                index == 1
        );

        updateNavLabel(
                navProfile,
                index == 2
        );
    }

    private void updateNavLabel(
            LinearLayout nav,
            boolean active
    ) {

        TextView label =
                (TextView) nav.getChildAt(1);

        label.setTextColor(
                getResources().getColor(
                        active
                                ? R.color.primary
                                : R.color.muted
                )
        );

        label.setTypeface(
                null,
                active
                        ? android.graphics.Typeface.BOLD
                        : android.graphics.Typeface.NORMAL
        );
    }

    private void toggleOnlineStatus() {

        isOnline = !isOnline;

        btnOnlineToggle.setText(
                isOnline
                        ? "🟢 Online"
                        : "🔴 Busy"
        );

        btnOnlineToggle.setTextColor(
                getResources().getColor(
                        isOnline
                                ? R.color.green
                                : R.color.red
                )
        );

        updateWorkerAvailability(
                isOnline,
                null
        );
    }

    // =========================================================
    // UPDATED WORKER PROFILE LOADING
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

        // Worker document ID is the same as Firebase Auth UID
        db.collection("workers")
                .document(uid)
                .get()
                .addOnSuccessListener(workerDocument -> {

                    if (workerDocument.exists()) {

                        currentWorkerDocumentId =
                                workerDocument.getId();

                        String name =
                                workerDocument.getString("name");

                        String title =
                                workerDocument.getString("title");

                        String phone =
                                workerDocument.getString("phone");

                        String area =
                                workerDocument.getString("area");

                        String rate =
                                workerDocument.getString("rate");

                        currentWorkerName =
                                name != null
                                        ? name
                                        : "";

                        // NAME
                        if (etWorkerName != null) {

                            etWorkerName.setText(
                                    name != null
                                            ? name
                                            : ""
                            );
                        }

                        // SPECIALIZATION / TITLE
                        if (etWorkerSpecialization != null) {

                            etWorkerSpecialization.setText(
                                    title != null
                                            ? title
                                            : ""
                            );
                        }

                        // PHONE
                        if (etWorkerPhone != null) {

                            etWorkerPhone.setText(
                                    phone != null
                                            ? phone
                                            : ""
                            );
                        }

                        // AREA
                        if (etWorkerArea != null) {

                            etWorkerArea.setText(
                                    area != null
                                            ? area
                                            : ""
                            );
                        }

                        // RATE
                        if (etWorkerRate != null) {

                            etWorkerRate.setText(
                                    rate != null
                                            ? rate
                                            : ""
                            );
                        }

                        // Keep existing booking logic
                        loadPendingAndAcceptedBookings();

                        // Keep existing availability logic
                        updateWorkerAvailability(
                                true,
                                null
                        );

                        return;
                    }

                    // Fallback: load worker name from users collection
                    db.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(userDocument -> {

                                String name =
                                        userDocument.getString("name");

                                currentWorkerName =
                                        name != null
                                                ? name
                                                : "";

                                if (etWorkerName != null) {

                                    etWorkerName.setText(
                                            currentWorkerName
                                    );
                                }

                                loadPendingAndAcceptedBookings();

                                updateWorkerAvailability(
                                        true,
                                        null
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

    private void loadPendingAndAcceptedBookings() {

        db.collection("bookings")
                .whereEqualTo(
                        "workerName",
                        currentWorkerName
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    incomingRequests.clear();
                    activeJobs.clear();

                    for (QueryDocumentSnapshot doc :
                            querySnapshot) {

                        String status =
                                doc.getString("status");

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

                        if (serviceTitle == null) {

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
                                (date != null
                                        ? date
                                        : "")
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
                                createEmptyText(
                                        "No chats yet."
                                )
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

                        addedCustomerIds.add(
                                customerId
                        );

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

                    Toast.makeText(
                            this,
                            "Could not load chats: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
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
                android.view.Gravity.CENTER_VERTICAL
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

        avatar.setGravity(
                android.view.Gravity.CENTER
        );

        LinearLayout.LayoutParams avatarParams =
                new LinearLayout.LayoutParams(
                        dp(48),
                        dp(48)
                );

        avatarParams.setMarginEnd(dp(12));

        avatar.setLayoutParams(
                avatarParams
        );

        row.addView(avatar);

        LinearLayout textArea =
                new LinearLayout(this);

        textArea.setOrientation(
                LinearLayout.VERTICAL
        );

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        textArea.setLayoutParams(
                textParams
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

        LinearLayout.LayoutParams serviceParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        serviceParams.topMargin = dp(3);

        service.setLayoutParams(
                serviceParams
        );

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

        arrow.setGravity(
                android.view.Gravity.CENTER
        );

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

    private TextView createEmptyText(
            String text
    ) {

        TextView tv =
                new TextView(this);

        tv.setText(text);

        tv.setTextColor(
                getResources().getColor(
                        R.color.muted
                )
        );

        tv.setTextSize(13);

        tv.setGravity(
                android.view.Gravity.CENTER
        );

        tv.setPadding(
                0,
                dp(12),
                0,
                dp(12)
        );

        return tv;
    }

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

        card.addView(desc);

        TextView details =
                new TextView(this);

        details.setText(
                "📍 "
                        + item.area
                        + "  •  🕐 "
                        + item.timing
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

        detailsParams.topMargin = dp(6);
        detailsParams.bottomMargin = dp(12);

        details.setLayoutParams(
                detailsParams
        );

        card.addView(details);

        LinearLayout buttonsRow =
                new LinearLayout(this);

        buttonsRow.setOrientation(
                LinearLayout.HORIZONTAL
        );

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

        btnAccept.setGravity(
                android.view.Gravity.CENTER
        );

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

        btnDecline.setGravity(
                android.view.Gravity.CENTER
        );

        btnDecline.setBackgroundResource(
                R.drawable.bg_btn_red
        );

        btnDecline.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(10)
        );

        LinearLayout.LayoutParams declineParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        btnDecline.setLayoutParams(
                declineParams
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
                item.description
                        + " — "
                        + item.timing
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

        TextView btnChat =
                new TextView(this);

        btnChat.setText("💬 Chat");

        btnChat.setTextColor(
                getResources().getColor(
                        R.color.primary
                )
        );

        btnChat.setTextSize(13);

        btnChat.setGravity(
                android.view.Gravity.CENTER
        );

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

        TextView btnMarkDone =
                new TextView(this);

        btnMarkDone.setText("✅ Mark Done");

        btnMarkDone.setTextColor(
                getResources().getColor(
                        R.color.white
                )
        );

        btnMarkDone.setTextSize(13);

        btnMarkDone.setGravity(
                android.view.Gravity.CENTER
        );

        btnMarkDone.setBackgroundResource(
                R.drawable.bg_btn_green
        );

        btnMarkDone.setPadding(
                dp(12),
                dp(10),
                dp(12),
                dp(10)
        );

        LinearLayout.LayoutParams doneParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                );

        btnMarkDone.setLayoutParams(doneParams);

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

    private void updateWorkerAvailability(
            boolean available,
            Runnable afterDone
    ) {

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
                                )
                                .addOnCompleteListener(task -> {

                                    if (afterDone != null) {
                                        afterDone.run();
                                    }
                                });

                    } else {

                        if (afterDone != null) {
                            afterDone.run();
                        }
                    }
                })
                .addOnFailureListener(e -> {

                    if (afterDone != null) {
                        afterDone.run();
                    }
                });
    }

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int) (
                value * density
        );
    }
}