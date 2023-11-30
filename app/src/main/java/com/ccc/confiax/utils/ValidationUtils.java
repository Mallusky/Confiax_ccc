package com.ccc.confiax.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean isValidEmail(String stremail) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(stremail).matches();
    }

    public static boolean isValidPwd(String pwd) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\\\S+$).{4,}$");
        return !TextUtils.isEmpty(pwd) && PASSWORD_PATTERN.matcher(pwd).matches();
    }

}
