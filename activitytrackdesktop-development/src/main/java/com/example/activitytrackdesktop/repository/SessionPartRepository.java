package com.example.activitytrackdesktop.repository;

import com.example.activitytrackdesktop.model.Session;
import com.example.activitytrackdesktop.model.SessionPart;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SessionPartRepository extends CrudRepository<SessionPart, Integer> {
    List<SessionPart> findAllBySession(Session session);
    void deleteSessionPartsBySession(Session session);
}
