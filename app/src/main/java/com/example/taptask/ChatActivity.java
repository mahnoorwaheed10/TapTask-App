package com.example.taptask;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final String EXTRA_WORKER_NAME = "worker_name";

    private TextView btnBack;
    private TextView tvChatWorkerName;
    private LinearLayout chatMessagesContainer;
    private EditText etChatMessage;
    private TextView btnSendMessage;
    private Button btnMarkCompleted;

    private String workerName;
    private List<ChatMessageItem> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        workerName = getIntent().getStringExtra(EXTRA_WORKER_NAME);
        if (workerName == null) workerName = "Worker";

        bindViews();
        tvChatWorkerName.setText(workerName);

        loadMockMessages();
        renderMessages();

        btnBack.setOnClickListener(v -> finish());

        btnSendMessage.setOnClickListener(v -> sendMessage());

        btnMarkCompleted.setOnClickListener(v -> {
            Intent intent = new Intent(ChatActivity.this, InvoiceActivity.class);
            intent.putExtra(InvoiceActivity.EXTRA_WORKER_NAME, workerName);
            startActivity(intent);
        });
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        tvChatWorkerName = findViewById(R.id.tvChatWorkerName);
        chatMessagesContainer = findViewById(R.id.chatMessagesContainer);
        etChatMessage = findViewById(R.id.etChatMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnMarkCompleted = findViewById(R.id.btnMarkCompleted);
    }

    private void loadMockMessages() {
        messages.add(new ChatMessageItem("them", "Hello! I've received your booking request.", "10:30 AM"));
        messages.add(new ChatMessageItem("me", "Hi, thank you! When can you come?", "10:32 AM"));
        messages.add(new ChatMessageItem("them", "I can be there tomorrow at the scheduled time.", "10:33 AM"));
    }

    private void sendMessage() {
        String text = etChatMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) return;

        messages.add(new ChatMessageItem("me", text, "Now"));
        etChatMessage.setText("");
        renderMessages();
    }

    private void renderMessages() {
        chatMessagesContainer.removeAllViews();

        for (ChatMessageItem msg : messages) {
            chatMessagesContainer.addView(createMessageBubble(msg));
        }
    }

    private LinearLayout createMessageBubble(ChatMessageItem msg) {
        boolean isMe = msg.type.equals("me");

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(isMe ? Gravity.END : Gravity.START);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.bottomMargin = dp(10);
        row.setLayoutParams(rowParams);

        LinearLayout bubble = new LinearLayout(this);
        bubble.setOrientation(LinearLayout.VERTICAL);
        bubble.setBackgroundResource(isMe ? R.drawable.bg_my_bubble : R.drawable.bg_their_bubble);
        bubble.setPadding(dp(14), dp(10), dp(14), dp(10));

        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bubble.setLayoutParams(bubbleParams);

        TextView text = new TextView(this);
        text.setText(msg.text);
        text.setTextColor(isMe ? getResources().getColor(R.color.white) : getResources().getColor(R.color.text_main));
        text.setTextSize(14);
        bubble.addView(text);

        TextView time = new TextView(this);
        time.setText(msg.time);
        time.setTextColor(isMe ? android.graphics.Color.parseColor("#C7D2FE") : getResources().getColor(R.color.muted));
        time.setTextSize(10);
        LinearLayout.LayoutParams timeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        timeParams.topMargin = dp(4);
        time.setLayoutParams(timeParams);
        bubble.addView(time);

        row.addView(bubble);
        return row;
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }
}