package com.example.activitytrackdesktop.model;

import lombok.Data;

@Data
public class User {
    private int id;
    private String nickname;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
}