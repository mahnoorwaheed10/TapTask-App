package com.example.taptask;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    // =========================================================
    // INTENT EXTRAS
    // =========================================================

    public static final String EXTRA_WORKER_NAME = "worker_name";
    public static final String EXTRA_CUSTOMER_NAME = "customer_name";
    public static final String EXTRA_CUSTOMER_ID = "customer_id";

    // IMPORTANT:
    // Explicitly tells ChatActivity whether this is worker side.
    public static final String EXTRA_IS_WORKER = "is_worker";

    private static final int LOCATION_PERMISSION_REQUEST = 1001;

    // =========================================================
    // VIEWS
    // =========================================================

    private TextView btnBack;
    private TextView tvChatWorkerName;
    private LinearLayout chatMessagesContainer;
    private EditText etChatMessage;
    private TextView btnSendMessage;
    private TextView btnSendLocation;

    // =========================================================
    // CHAT DATA
    // =========================================================

    private String workerName = "Worker";
    private String customerName = "Customer";
    private String customerId;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private FusedLocationProviderClient fusedLocationClient;

    private String currentUserId;
    private String currentUserName = "User";

    private String chatId;

    // IMPORTANT:
    // Do NOT calculate this from customerId.
    // It must come from EXTRA_IS_WORKER.
    private boolean isWorkerSide = false;

    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Location
        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        // =====================================================
        // GET INTENT DATA
        // =====================================================

        workerName =
                getIntent().getStringExtra(
                        EXTRA_WORKER_NAME
                );

        customerName =
                getIntent().getStringExtra(
                        EXTRA_CUSTOMER_NAME
                );

        customerId =
                getIntent().getStringExtra(
                        EXTRA_CUSTOMER_ID
                );

        // IMPORTANT FIX:
        // Explicit worker/customer side.
        isWorkerSide =
                getIntent().getBooleanExtra(
                        EXTRA_IS_WORKER,
                        false
                );

        // =====================================================
        // DEFAULT VALUES
        // =====================================================

        if (workerName == null ||
                workerName.trim().isEmpty()) {

            workerName = "Worker";
        }

        if (customerName == null ||
                customerName.trim().isEmpty()) {

            customerName = "Customer";
        }

        // =====================================================
        // BIND VIEWS
        // =====================================================

        bindViews();

        // =====================================================
        // CHECK LOGIN
        // =====================================================

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        currentUserId =
                user.getUid();

        // =====================================================
        // BUTTONS
        // =====================================================

        btnBack.setOnClickListener(
                v -> finish()
        );

        btnSendMessage.setOnClickListener(
                v -> sendMessage()
        );

        // IMPORTANT:
        // Location is ONLY requested when this button is pressed.
        btnSendLocation.setOnClickListener(
                v -> checkLocationPermission()
        );

        // =====================================================
        // LOAD CURRENT USER
        // =====================================================

        loadCurrentUserName();
    }

    // =========================================================
    // BIND VIEWS
    // =========================================================

    private void bindViews() {

        btnBack =
                findViewById(R.id.btnBack);

        tvChatWorkerName =
                findViewById(R.id.tvChatWorkerName);

        chatMessagesContainer =
                findViewById(
                        R.id.chatMessagesContainer
                );

        etChatMessage =
                findViewById(
                        R.id.etChatMessage
                );

        btnSendMessage =
                findViewById(
                        R.id.btnSendMessage
                );

        btnSendLocation =
                findViewById(
                        R.id.btnSendLocation
                );
    }

    // =========================================================
    // LOAD CURRENT USER NAME
    // =========================================================

    private void loadCurrentUserName() {

        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    String name =
                            documentSnapshot.getString(
                                    "name"
                            );

                    if (name != null &&
                            !name.trim().isEmpty()) {

                        currentUserName =
                                name.trim();
                    }

                    continueChatSetup();
                })
                .addOnFailureListener(e -> {

                    continueChatSetup();
                });
    }

    // =========================================================
    // CONTINUE CHAT SETUP
    // =========================================================

    private void continueChatSetup() {

        // =====================================================
        // WORKER SIDE
        // =====================================================

        if (isWorkerSide) {

            loadCustomerNameAndCreateChat();

        }
        // =====================================================
        // CUSTOMER SIDE
        // =====================================================
        else {

            tvChatWorkerName.setText(
                    workerName
            );

            findWorkerAndCreateChatId();
        }
    }

    // =========================================================
    // WORKER SIDE:
    // LOAD CUSTOMER NAME
    // =========================================================

    private void loadCustomerNameAndCreateChat() {

        if (customerId == null ||
                customerId.trim().isEmpty()) {

            tvChatWorkerName.setText(
                    customerName
            );

            createWorkerChatId();

            return;
        }

        db.collection("users")
                .document(customerId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    String name =
                            documentSnapshot.getString(
                                    "name"
                            );

                    if (name != null &&
                            !name.trim().isEmpty()) {

                        customerName =
                                name.trim();
                    }

                    tvChatWorkerName.setText(
                            customerName
                    );

                    createWorkerChatId();
                })
                .addOnFailureListener(e -> {

                    tvChatWorkerName.setText(
                            customerName
                    );

                    createWorkerChatId();
                });
    }

    // =========================================================
    // CUSTOMER SIDE:
    // FIND WORKER DOCUMENT
    // =========================================================

    private void findWorkerAndCreateChatId() {

        db.collection("workers")
                .whereEqualTo(
                        "name",
                        workerName
                )
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (!querySnapshot.isEmpty()) {

                        String workerDocId =
                                querySnapshot
                                        .getDocuments()
                                        .get(0)
                                        .getId();

                        createCustomerChatId(
                                workerDocId
                        );

                    } else {

                        createCustomerFallbackChatId();
                    }
                })
                .addOnFailureListener(e ->
                        createCustomerFallbackChatId()
                );
    }

    // =========================================================
    // CUSTOMER CHAT ID
    // =========================================================

    private void createCustomerChatId(
            String workerDocId) {

        String first =
                currentUserId;

        String second =
                workerDocId;

        if (first.compareTo(second) < 0) {

            chatId =
                    first + "_" + second;

        } else {

            chatId =
                    second + "_" + first;
        }

        listenForMessages();
    }

    // =========================================================
    // CUSTOMER FALLBACK CHAT ID
    // =========================================================

    private void createCustomerFallbackChatId() {

        String workerKey =
                workerName
                        .trim()
                        .toLowerCase()
                        .replace(" ", "_");

        String first =
                currentUserId;

        String second =
                workerKey;

        if (first.compareTo(second) < 0) {

            chatId =
                    first + "_" + second;

        } else {

            chatId =
                    second + "_" + first;
        }

        listenForMessages();
    }

    // =========================================================
    // WORKER SIDE:
    // FIND WORKER DOCUMENT
    // =========================================================

    private void createWorkerChatId() {

        db.collection("workers")
                .whereEqualTo(
                        "name",
                        workerName
                )
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    if (!querySnapshot.isEmpty()) {

                        String workerDocId =
                                querySnapshot
                                        .getDocuments()
                                        .get(0)
                                        .getId();

                        createWorkerChatIdWithDoc(
                                workerDocId
                        );

                    } else {

                        createWorkerFallbackChatId();
                    }
                })
                .addOnFailureListener(e ->
                        createWorkerFallbackChatId()
                );
    }

    // =========================================================
    // WORKER CHAT ID
    // =========================================================

    private void createWorkerChatIdWithDoc(
            String workerDocId) {

        // Worker side uses customerId.
        // Customer side uses currentUserId.
        //
        // Therefore both sides produce the SAME chatId.

        if (customerId == null ||
                customerId.trim().isEmpty()) {

            createWorkerFallbackChatId();
            return;
        }

        String first =
                customerId;

        String second =
                workerDocId;

        if (first.compareTo(second) < 0) {

            chatId =
                    first + "_" + second;

        } else {

            chatId =
                    second + "_" + first;
        }

        listenForMessages();
    }

    // =========================================================
    // WORKER FALLBACK CHAT ID
    // =========================================================

    private void createWorkerFallbackChatId() {

        if (customerId == null ||
                customerId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Customer information missing",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String workerKey =
                workerName
                        .trim()
                        .toLowerCase()
                        .replace(" ", "_");

        String first =
                customerId;

        String second =
                workerKey;

        if (first.compareTo(second) < 0) {

            chatId =
                    first + "_" + second;

        } else {

            chatId =
                    second + "_" + first;
        }

        listenForMessages();
    }

    // =========================================================
    // LISTEN FOR MESSAGES
    // =========================================================

    private void listenForMessages() {

        if (chatId == null ||
                chatId.trim().isEmpty()) {

            return;
        }

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy(
                        "timestamp",
                        Query.Direction.ASCENDING
                )
                .addSnapshotListener(
                        (querySnapshot, error) -> {

                            if (error != null) {

                                Toast.makeText(
                                        this,
                                        "Could not load messages",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }

                            chatMessagesContainer
                                    .removeAllViews();

                            if (querySnapshot == null) {
                                return;
                            }

                            for (QueryDocumentSnapshot doc :
                                    querySnapshot) {

                                String senderId =
                                        doc.getString(
                                                "senderId"
                                        );

                                String senderName =
                                        doc.getString(
                                                "senderName"
                                        );

                                String type =
                                        doc.getString(
                                                "type"
                                        );

                                if (senderName == null ||
                                        senderName.trim().isEmpty()) {

                                    senderName = "User";
                                }

                                boolean isMe =
                                        currentUserId != null &&
                                                currentUserId.equals(
                                                        senderId
                                                );

                                // =================================================
                                // LOCATION MESSAGE
                                // =================================================

                                if ("location".equals(type)) {

                                    Double latitude =
                                            doc.getDouble(
                                                    "latitude"
                                            );

                                    Double longitude =
                                            doc.getDouble(
                                                    "longitude"
                                            );

                                    if (latitude != null &&
                                            longitude != null) {

                                        createLocationBubble(
                                                latitude,
                                                longitude,
                                                senderName,
                                                isMe
                                        );
                                    }

                                }
                                // =================================================
                                // NORMAL TEXT MESSAGE
                                // =================================================
                                else {

                                    String message =
                                            doc.getString(
                                                    "message"
                                            );

                                    if (message == null) {
                                        message = "";
                                    }

                                    createMessageBubble(
                                            message,
                                            senderName,
                                            isMe
                                    );
                                }
                            }
                        });
    }

    // =========================================================
    // SEND TEXT MESSAGE
    // =========================================================

    private void sendMessage() {

        String text =
                etChatMessage
                        .getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(text)) {
            return;
        }

        if (chatId == null ||
                chatId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Chat is loading...",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String receiverName;

        if (isWorkerSide) {

            receiverName =
                    customerName;

        } else {

            receiverName =
                    workerName;
        }

        Map<String, Object> message =
                new HashMap<>();

        message.put(
                "senderId",
                currentUserId
        );

        message.put(
                "senderName",
                currentUserName
        );

        message.put(
                "receiverName",
                receiverName
        );

        message.put(
                "message",
                text
        );

        message.put(
                "type",
                "text"
        );

        message.put(
                "timestamp",
                System.currentTimeMillis()
        );

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message)
                .addOnSuccessListener(
                        documentReference -> {

                            etChatMessage
                                    .setText("");
                        })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Message failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // LOCATION PERMISSION
    // =========================================================

    private void checkLocationPermission() {

        // IMPORTANT:
        // This method ONLY runs when user presses 📍.

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {

            sendCurrentLocation();

        } else {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST
            );
        }
    }

    // =========================================================
    // PERMISSION RESULT
    // =========================================================

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode ==
                LOCATION_PERMISSION_REQUEST) {

            boolean granted = false;

            for (int result : grantResults) {

                if (result ==
                        PackageManager.PERMISSION_GRANTED) {

                    granted = true;
                    break;
                }
            }

            if (granted) {

                sendCurrentLocation();

            } else {

                Toast.makeText(
                        this,
                        "Location permission is required to send your location.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    // =========================================================
    // GET CURRENT LOCATION
    // =========================================================

    private void sendCurrentLocation() {

        if (chatId == null ||
                chatId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Chat is loading...",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Toast.makeText(
                this,
                "Getting your location...",
                Toast.LENGTH_SHORT
        ).show();

        fusedLocationClient
                .getCurrentLocation(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                        null
                )
                .addOnSuccessListener(location -> {

                    if (location == null) {

                        Toast.makeText(
                                this,
                                "Could not get your location. Please turn on GPS.",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    saveLocationMessage(
                            location.getLatitude(),
                            location.getLongitude()
                    );
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Location failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // SAVE LOCATION MESSAGE
    // =========================================================

    private void saveLocationMessage(
            double latitude,
            double longitude) {

        String receiverName;

        if (isWorkerSide) {

            receiverName =
                    customerName;

        } else {

            receiverName =
                    workerName;
        }

        Map<String, Object> locationMessage =
                new HashMap<>();

        locationMessage.put(
                "senderId",
                currentUserId
        );

        locationMessage.put(
                "senderName",
                currentUserName
        );

        locationMessage.put(
                "receiverName",
                receiverName
        );

        locationMessage.put(
                "type",
                "location"
        );

        locationMessage.put(
                "message",
                "Location"
        );

        locationMessage.put(
                "latitude",
                latitude
        );

        locationMessage.put(
                "longitude",
                longitude
        );

        locationMessage.put(
                "timestamp",
                System.currentTimeMillis()
        );

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(locationMessage)
                .addOnSuccessListener(
                        documentReference -> {

                            Toast.makeText(
                                    this,
                                    "Location sent",
                                    Toast.LENGTH_SHORT
                            ).show();
                        })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Location failed: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // NORMAL MESSAGE BUBBLE
    // =========================================================

    private void createMessageBubble(
            String message,
            String senderName,
            boolean isMe) {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                isMe
                        ? Gravity.END
                        : Gravity.START
        );

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        rowParams.bottomMargin =
                dp(10);

        row.setLayoutParams(
                rowParams
        );

        LinearLayout bubble =
                new LinearLayout(this);

        bubble.setOrientation(
                LinearLayout.VERTICAL
        );

        bubble.setBackgroundResource(
                isMe
                        ? R.drawable.bg_my_bubble
                        : R.drawable.bg_their_bubble
        );

        bubble.setPadding(
                dp(14),
                dp(10),
                dp(14),
                dp(10)
        );

        TextView name =
                new TextView(this);

        name.setText(
                senderName
        );

        name.setTextSize(11);

        name.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        name.setTextColor(
                isMe
                        ? android.graphics.Color.parseColor(
                        "#C7D2FE"
                )
                        : getResources().getColor(
                        R.color.primary
                )
        );

        bubble.addView(
                name
        );

        TextView text =
                new TextView(this);

        text.setText(
                message
        );

        text.setTextSize(14);

        text.setTextColor(
                isMe
                        ? getResources().getColor(
                        R.color.white
                )
                        : getResources().getColor(
                        R.color.text_main
                )
        );

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        textParams.topMargin =
                dp(3);

        text.setLayoutParams(
                textParams
        );

        bubble.addView(
                text
        );

        row.addView(
                bubble
        );

        chatMessagesContainer.addView(
                row
        );
    }

    // =========================================================
    // LOCATION BUBBLE
    // =========================================================

    private void createLocationBubble(
            double latitude,
            double longitude,
            String senderName,
            boolean isMe) {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                isMe
                        ? Gravity.END
                        : Gravity.START
        );

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        rowParams.bottomMargin =
                dp(10);

        row.setLayoutParams(
                rowParams
        );

        LinearLayout bubble =
                new LinearLayout(this);

        bubble.setOrientation(
                LinearLayout.VERTICAL
        );

        bubble.setGravity(
                Gravity.CENTER_HORIZONTAL
        );

        bubble.setBackgroundResource(
                isMe
                        ? R.drawable.bg_my_bubble
                        : R.drawable.bg_their_bubble
        );

        bubble.setPadding(
                dp(14),
                dp(12),
                dp(14),
                dp(12)
        );

        TextView name =
                new TextView(this);

        name.setText(
                senderName
        );

        name.setTextSize(11);

        name.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        name.setTextColor(
                isMe
                        ? android.graphics.Color.parseColor(
                        "#C7D2FE"
                )
                        : getResources().getColor(
                        R.color.primary
                )
        );

        bubble.addView(
                name
        );

        TextView locationIcon =
                new TextView(this);

        locationIcon.setText("📍");
        locationIcon.setTextSize(30);
        locationIcon.setGravity(
                Gravity.CENTER
        );

        bubble.addView(
                locationIcon
        );

        TextView locationText =
                new TextView(this);

        locationText.setText(
                "Location"
        );

        locationText.setTextSize(15);

        locationText.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        locationText.setGravity(
                Gravity.CENTER
        );

        locationText.setTextColor(
                isMe
                        ? getResources().getColor(
                        R.color.white
                )
                        : getResources().getColor(
                        R.color.text_main
                )
        );

        bubble.addView(
                locationText
        );

        TextView openMap =
                new TextView(this);

        openMap.setText(
                "Tap to open in Google Maps"
        );

        openMap.setTextSize(12);

        openMap.setGravity(
                Gravity.CENTER
        );

        openMap.setPadding(
                0,
                dp(5),
                0,
                0
        );

        openMap.setTextColor(
                isMe
                        ? android.graphics.Color.parseColor(
                        "#C7D2FE"
                )
                        : getResources().getColor(
                        R.color.primary
                )
        );

        bubble.addView(
                openMap
        );

        bubble.setOnClickListener(
                v -> openLocationInMaps(
                        latitude,
                        longitude
                )
        );

        row.addView(
                bubble
        );

        chatMessagesContainer.addView(
                row
        );
    }

    // =========================================================
    // OPEN GOOGLE MAPS
    // =========================================================

    private void openLocationInMaps(
            double latitude,
            double longitude) {

        Uri geoUri =
                Uri.parse(
                        "geo:"
                                + latitude
                                + ","
                                + longitude
                                + "?q="
                                + latitude
                                + ","
                                + longitude
                );

        Intent mapIntent =
                new Intent(
                        Intent.ACTION_VIEW,
                        geoUri
                );

        mapIntent.setPackage(
                "com.google.android.apps.maps"
        );

        try {

            startActivity(mapIntent);

        } catch (Exception e) {

            Intent browserIntent =
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                    "https://www.google.com/maps/search/?api=1&query="
                                            + latitude
                                            + ","
                                            + longitude
                            )
                    );

            startActivity(browserIntent);
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

        return (int)
                (value * density);
    }
}