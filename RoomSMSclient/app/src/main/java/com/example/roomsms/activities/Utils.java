package com.example.roomsms.activities;

import java.util.regex.Pattern;

public class Utils {
    static public boolean IsBlankString(String string) {
        return Pattern.compile("^\\s*$").matcher(string).matches();
    }
}
