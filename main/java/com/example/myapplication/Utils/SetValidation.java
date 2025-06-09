package com.example.myapplication.Utils;

import com.example.myapplication.Domain.Set;

public class SetValidation {
    public static String Validate(Set set) {
        if (set.getReps() < 1 || set.getWeight() < 0 || set.getRestTime() < 1) {
            return "Invalid input";
        }
        return null;
    }
}
