package com.example.activitytrackdesktop.model;

import lombok.Data;

@Data
public class Response<T> {
    private int status;
    private String error = "";
    private T message;
}
