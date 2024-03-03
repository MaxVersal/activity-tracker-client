package com.example.activitytrackdesktop.persistence.inter;

import com.example.activitytrackdesktop.generic.service.inter.CrudService;
import com.example.activitytrackdesktop.model.Screenshot;
import com.example.activitytrackdesktop.model.Session;

import java.util.List;

public interface ScreenshotService extends CrudService<Screenshot, Integer> {
    List<Screenshot> findAllBySession(Session session);
    void deleteAllBySession(Session session);
}
