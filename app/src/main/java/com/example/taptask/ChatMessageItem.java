package com.example.taptask;

public class ChatMessageItem {
    public String type; // "me" or "them"
    public String text;
    public String time;

    public ChatMessageItem(String type, String text, String time) {
        this.type = type;
        this.text = text;
        this.time = time;
    }
}