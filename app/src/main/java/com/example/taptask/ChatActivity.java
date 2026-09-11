package com.example.taptask;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_WORKER_NAME = "worker_name";
    public static final String EXTRA_CUSTOMER_NAME = "customer_name";
    public static final String EXTRA_CUSTOMER_ID = "customer_id";

    private TextView btnBack;
    private TextView tvChatWorkerName;
    private LinearLayout chatMessagesContainer;
    private EditText etChatMessage;
    private TextView btnSendMessage;

    private String workerName = "Worker";
    private String customerName = "Customer";
    private String customerId;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String currentUserId;
    private String currentUserName = "User";

    private String chatId;

    private boolean isWorkerSide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        workerName =
                getIntent().getStringExtra(EXTRA_WORKER_NAME);

        customerName =
                getIntent().getStringExtra(EXTRA_CUSTOMER_NAME);

        customerId =
                getIntent().getStringExtra(EXTRA_CUSTOMER_ID);

        if (workerName == null ||
                workerName.trim().isEmpty()) {

            workerName = "Worker";
        }

        if (customerName == null ||
                customerName.trim().isEmpty()) {

            customerName = "Customer";
        }

        isWorkerSide =
                customerId != null &&
                        !customerId.trim().isEmpty();

        bindViews();

        FirebaseUser user =
                auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        currentUserId = user.getUid();

        btnBack.setOnClickListener(
                v -> finish()
        );

        btnSendMessage.setOnClickListener(
                v -> sendMessage()
        );

        loadCurrentUserName();
    }

    private void bindViews() {

        btnBack =
                findViewById(R.id.btnBack);

        tvChatWorkerName =
                findViewById(R.id.tvChatWorkerName);

        chatMessagesContainer =
                findViewById(R.id.chatMessagesContainer);

        etChatMessage =
                findViewById(R.id.etChatMessage);

        btnSendMessage =
                findViewById(R.id.btnSendMessage);
    }

    private void loadCurrentUserName() {

        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    String name =
                            documentSnapshot.getString("name");

                    if (name != null &&
                            !name.trim().isEmpty()) {

                        currentUserName =
                                name.trim();
                    }

                    if (isWorkerSide) {

                        loadCustomerNameAndCreateChat();

                    } else {

                        tvChatWorkerName.setText(
                                workerName
                        );

                        findWorkerAndCreateChatId();
                    }
                })
                .addOnFailureListener(e -> {

                    if (isWorkerSide) {

                        loadCustomerNameAndCreateChat();

                    } else {

                        tvChatWorkerName.setText(
                                workerName
                        );

                        findWorkerAndCreateChatId();
                    }
                });
    }

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
                            documentSnapshot.getString("name");

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

    private void createWorkerChatIdWithDoc(
            String workerDocId) {

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

    private void createWorkerFallbackChatId() {

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

    private void listenForMessages() {

        if (chatId == null) {
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
                                        doc.getString("senderId");

                                String message =
                                        doc.getString("message");

                                String senderName =
                                        doc.getString("senderName");

                                if (message == null) {
                                    message = "";
                                }

                                if (senderName == null ||
                                        senderName.trim().isEmpty()) {

                                    senderName = "User";
                                }

                                boolean isMe =
                                        currentUserId.equals(
                                                senderId
                                        );

                                createMessageBubble(
                                        message,
                                        senderName,
                                        isMe
                                );
                            }
                        });
    }

    private void sendMessage() {

        String text =
                etChatMessage
                        .getText()
                        .toString()
                        .trim();

        if (TextUtils.isEmpty(text)) {
            return;
        }

        if (chatId == null) {

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

    private int dp(int value) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int)
                (value * density);
    }
}