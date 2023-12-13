package com.example.roomsms.activities;

public class StringIntegerModel {
    private String message;
    private int integer;
    public StringIntegerModel(String Message, int Integer) {
        this.integer = Integer;
        this.message = Message;
    }

    public void setString(String string) {
        this.message = string;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public int getInteger() {
        return integer;
    }

    public String getString() {
        return message;
    }
}
