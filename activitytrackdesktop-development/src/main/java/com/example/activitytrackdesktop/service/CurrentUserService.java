package com.example.activitytrackdesktop.service;

import com.example.activitytrackdesktop.model.User;
import com.example.activitytrackdesktop.model.UserInfo;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private User user;
    private String token;
    private String password;
    public User getCurrentUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setCurrentInfo(UserInfo userInfo, String password) {
        user = userInfo.getUser();
        token = userInfo.getToken();
        this.password = password;
    }
}
