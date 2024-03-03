package com.example.activitytrackdesktop.persistence.inter;

import com.example.activitytrackdesktop.model.SessionInfo;

import java.util.List;

public interface SessionsInfoService {
    List<SessionInfo> getSessionInfoList();
    void deleteSessionInfoList();
}
