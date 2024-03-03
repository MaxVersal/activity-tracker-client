package com.example.activitytrackdesktop.persistence.impl;

import com.example.activitytrackdesktop.generic.service.impl.CrudServiceImpl;
import com.example.activitytrackdesktop.model.Session;
import com.example.activitytrackdesktop.persistence.inter.SessionPartService;
import com.example.activitytrackdesktop.model.SessionPart;
import com.example.activitytrackdesktop.repository.SessionPartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class SessionPartServiceImpl extends CrudServiceImpl<SessionPart, Integer, SessionPartRepository> implements SessionPartService {
    @Autowired
    public void setRepository(SessionPartRepository repository){
        this.repository = repository;
    }

    @Override
    public List<SessionPart> findAllBySession(Session session) {
        return repository.findAllBySession(session);
    }

    @Override
    public void deleteAllBySession(Session session) {
        repository.deleteSessionPartsBySession(session);
    }
}
