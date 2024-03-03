package com.example.activitytrackdesktop.persistence.impl;

import com.example.activitytrackdesktop.generic.service.impl.CrudServiceImpl;
import com.example.activitytrackdesktop.model.Session;
import com.example.activitytrackdesktop.repository.SessionRepository;
import com.example.activitytrackdesktop.persistence.inter.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class SessionServiceImpl extends CrudServiceImpl<Session, Integer, SessionRepository> implements SessionService {
    @Autowired
    public void setRepository(SessionRepository repository){
        this.repository = repository;
    }
}
