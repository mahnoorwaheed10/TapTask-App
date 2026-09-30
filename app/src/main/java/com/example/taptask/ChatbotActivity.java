package com.example.taptask;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChatbotActivity extends AppCompatActivity {

    private TextView btnBack;
    private EditText etChatMessage;
    private TextView btnSendMessage;
    private LinearLayout chatMessagesContainer;
    private ScrollView chatScrollView;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String customerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chatbot);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bindViews();

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        customerId = user.getUid();

        btnBack.setOnClickListener(v -> finish());

        btnSendMessage.setOnClickListener(v -> sendMessage());

        loadChat();
    }

    private void bindViews() {

        btnBack = findViewById(R.id.btnBack);

        etChatMessage =
                findViewById(R.id.etChatMessage);

        btnSendMessage =
                findViewById(R.id.btnSendMessage);

        chatMessagesContainer =
                findViewById(R.id.chatMessagesContainer);

        chatScrollView =
                findViewById(R.id.chatScrollView);
    }

    private void sendMessage() {

        String message =
                etChatMessage
                        .getText()
                        .toString()
                        .trim();

        if (message.isEmpty()) {
            return;
        }

        addMessageBubble(
                message,
                true
        );

        etChatMessage.setText("");

        saveMessage(
                message,
                "user"
        );

        callGroqApi(message);
    }

    private void saveMessage(
            String message,
            String sender
    ) {

        Map<String, Object> data =
                new HashMap<>();

        data.put(
                "message",
                message
        );

        data.put(
                "sender",
                sender
        );

        data.put(
                "timestamp",
                System.currentTimeMillis()
        );

        db.collection("chatbotChats")
                .document(customerId)
                .collection("messages")
                .add(data);
    }

    private void loadChat() {

        db.collection("chatbotChats")
                .document(customerId)
                .collection("messages")
                .orderBy(
                        "timestamp",
                        Query.Direction.ASCENDING
                )
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    chatMessagesContainer
                            .removeAllViews();

                    for (
                            QueryDocumentSnapshot doc
                            : querySnapshot
                    ) {

                        String message =
                                doc.getString(
                                        "message"
                                );

                        String sender =
                                doc.getString(
                                        "sender"
                                );

                        if (message == null) {
                            message = "";
                        }

                        addMessageBubble(
                                message,
                                "user".equals(sender)
                        );
                    }

                    scrollToBottom();
                });
    }

    private void callGroqApi(
            String userMessage
    ) {

        new Thread(() -> {

            try {

                /*
                 * IMPORTANT:
                 * Yahan apni NEW Groq API key paste karo.
                 */
                String apiKey =
                        "";

                URL url =
                        new URL(
                                "https://api.groq.com/openai/v1/chat/completions"
                        );

                HttpURLConnection conn =
                        (HttpURLConnection)
                                url.openConnection();

                conn.setRequestMethod(
                        "POST"
                );

                conn.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                conn.setRequestProperty(
                        "Authorization",
                        "Bearer " + apiKey
                );

                conn.setDoOutput(true);

                JSONObject systemMessage =
                        new JSONObject();

                systemMessage.put(
                        "role",
                        "system"
                );

                systemMessage.put(
                        "content",
                        "You are TapTask Assistant, a helpful assistant for a home services booking app called TapTask. Answer briefly, clearly and helpfully. Help users with services, workers, bookings, payments, ratings, reviews and general TapTask questions."
                );

                JSONObject userMsg =
                        new JSONObject();

                userMsg.put(
                        "role",
                        "user"
                );

                userMsg.put(
                        "content",
                        userMessage
                );

                JSONArray messagesArray =
                        new JSONArray();

                messagesArray.put(
                        systemMessage
                );

                messagesArray.put(
                        userMsg
                );

                JSONObject body =
                        new JSONObject();

                body.put(
                        "model",
                        "openai/gpt-oss-20b"
                );

                body.put(
                        "messages",
                        messagesArray
                );

                OutputStream os =
                        conn.getOutputStream();

                os.write(
                        body.toString()
                                .getBytes()
                );

                os.flush();
                os.close();

                int responseCode =
                        conn.getResponseCode();

                java.io.InputStream is;

                if (responseCode == 200) {

                    is = conn.getInputStream();

                } else {

                    is = conn.getErrorStream();
                }

                java.util.Scanner scanner =
                        new java.util.Scanner(is)
                                .useDelimiter(
                                        "\\A"
                                );

                String responseText =
                        scanner.hasNext()
                                ? scanner.next()
                                : "";

                String finalReply;

                if (responseCode == 200) {

                    JSONObject responseJson =
                            new JSONObject(
                                    responseText
                            );

                    finalReply =
                            responseJson
                                    .getJSONArray(
                                            "choices"
                                    )
                                    .getJSONObject(0)
                                    .getJSONObject(
                                            "message"
                                    )
                                    .getString(
                                            "content"
                                    );

                } else {

                    finalReply =
                            "Groq Error Code: "
                                    + responseCode
                                    + "\n"
                                    + responseText;
                }

                String finalReplyToShow =
                        finalReply;

                runOnUiThread(() -> {

                    addMessageBubble(
                            finalReplyToShow,
                            false
                    );

                    saveMessage(
                            finalReplyToShow,
                            "bot"
                    );

                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    String errorMsg =
                            "Sorry, kuch ghalat ho gaya. Internet check karo.";

                    addMessageBubble(
                            errorMsg,
                            false
                    );

                    saveMessage(
                            errorMsg,
                            "bot"
                    );

                });

            }

        }).start();
    }

    private void addMessageBubble(
            String message,
            boolean isUser
    ) {

        LinearLayout row =
                new LinearLayout(this);

        row.setOrientation(
                LinearLayout.HORIZONTAL
        );

        row.setGravity(
                isUser
                        ? Gravity.END
                        : Gravity.START
        );

        LinearLayout.LayoutParams rowParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        rowParams.bottomMargin =
                dp(8);

        row.setLayoutParams(
                rowParams
        );

        TextView bubble =
                new TextView(this);

        // Remove unwanted Markdown symbols from Groq response
        String cleanMessage =
                message.replace("**", "")
                        .replace("-----", "")
                        .replace("|", "");

        bubble.setText(
                cleanMessage
        );

        bubble.setTextSize(
                14
        );

        bubble.setPadding(
                dp(14),
                dp(10),
                dp(14),
                dp(10)
        );

        if (isUser) {

            bubble.setTextColor(
                    Color.WHITE
            );

            bubble.setBackgroundResource(
                    R.drawable.bg_my_bubble
            );

        } else {

            bubble.setTextColor(
                    getResources().getColor(
                            R.color.text_main
                    )
            );

            bubble.setBackgroundResource(
                    R.drawable.bg_their_bubble
            );
        }

        LinearLayout.LayoutParams bubbleParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        bubbleParams.width =
                (int) (
                        getResources()
                                .getDisplayMetrics()
                                .widthPixels
                                * 0.75
                );

        bubble.setLayoutParams(
                bubbleParams
        );

        row.addView(
                bubble
        );

        chatMessagesContainer.addView(
                row
        );

        scrollToBottom();
    }

    private void scrollToBottom() {

        chatScrollView.post(() ->
                chatScrollView.fullScroll(
                        ScrollView.FOCUS_DOWN
                )
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
};