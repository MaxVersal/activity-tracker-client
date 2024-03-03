package com.example.activitytrackdesktop.persistence.inter;

import com.example.activitytrackdesktop.generic.service.inter.CrudService;
import com.example.activitytrackdesktop.model.Session;
import com.example.activitytrackdesktop.model.SessionPart;

import java.util.List;

public interface SessionPartService extends CrudService<SessionPart, Integer> {
    List<SessionPart> findAllBySession(Session session);
    void deleteAllBySession(Session session);
}
