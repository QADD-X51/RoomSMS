package com.example.roomsms.activities;

public class StringIntegerModel {
    private String string;
    private int integer;
    public StringIntegerModel(String string, int integer) {
        this.integer = integer;
        this.string = string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public int getInteger() {
        return integer;
    }

    public String getString() {
        return string;
    }
}
