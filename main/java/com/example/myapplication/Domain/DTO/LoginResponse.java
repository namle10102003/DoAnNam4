package com.example.myapplication.Domain.DTO;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private String userId;
    private String fullName;

    public String getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }
}
