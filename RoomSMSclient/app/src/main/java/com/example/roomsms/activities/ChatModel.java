package com.example.roomsms.activities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatModel {
    private String message;
    private String senderUsername;
    //private Date sentDate;
    private String sentDate;

    private static final String patternDate = "MM/dd/yyyy - HH:mm";

    public ChatModel(String message, String senderName, String date) {
        this.message = message;
        this.senderUsername = senderName;
        this.sentDate = date;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return sentDate;
    }

    public String getSenderId() {
        return senderUsername;
    }

    public String getDateString() {
        return sentDate;
        //return new SimpleDateFormat(patternDate).format(sentDate);
    }
}
