package com.example.roomsms.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatModel {
    private String message;
    private int senderId;
    private Date date;

    private static final String patternDate = "MM/dd/yyyy - HH:mm";

    public ChatModel(String message, int senderId, Date date) {
        this.message = message;
        this.senderId = senderId;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public Date getDate() {
        return date;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getDateString()
    {
        return new SimpleDateFormat(patternDate).format(date);
    }
}
