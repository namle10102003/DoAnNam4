package com.example.myapplication.Utils;

import com.example.myapplication.Domain.BodyTrack;

public class TrackValidation {
    public static String Validate(BodyTrack track) {
        if (track.Weight < 0 || track.Height < 0) {
            return "Invalid input";
        }
        return null;
    }
}
