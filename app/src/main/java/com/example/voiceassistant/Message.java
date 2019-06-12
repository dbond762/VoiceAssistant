package com.example.voiceassistant;

import java.util.Date;

public class Message {
    public String text;
    public Date date;
    public Boolean isSent;

    public Message(String text, Boolean isSent) {
        this.text = text;
        this.date = new Date();
        this.isSent = isSent;
    }
}
