package com.example.activitytrackdesktop.model;

import lombok.Data;

import java.util.List;

@Data
public class SessionInfo {
    private int userId;
    private int projectId;
    private Session session;
    private List<SessionPart> sessionParts;
    private List<Screenshot> screenshots;
}
