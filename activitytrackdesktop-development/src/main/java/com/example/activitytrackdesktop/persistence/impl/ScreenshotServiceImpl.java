package com.example.activitytrackdesktop.persistence.impl;

import com.example.activitytrackdesktop.generic.service.impl.CrudServiceImpl;
import com.example.activitytrackdesktop.model.Session;
import com.example.activitytrackdesktop.persistence.inter.ScreenshotService;
import com.example.activitytrackdesktop.model.Screenshot;
import com.example.activitytrackdesktop.repository.ScreenshotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ScreenshotServiceImpl extends CrudServiceImpl<Screenshot, Integer, ScreenshotRepository> implements ScreenshotService {
    @Autowired
    public void setRepository(ScreenshotRepository repository){
        this.repository = repository;
    }

    @Override
    public List<Screenshot> findAllBySession(Session session) {
        return repository.findAllBySession(session);
    }

    @Override
    public void deleteAllBySession(Session session) {
        repository.deleteScreenshotsBySession(session);
    }
}
