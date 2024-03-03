package com.example.activitytrackdesktop.repository;

import com.example.activitytrackdesktop.model.Screenshot;
import com.example.activitytrackdesktop.model.Session;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScreenshotRepository extends CrudRepository<Screenshot, Integer> {
    List<Screenshot> findAllBySession(Session session);
    void deleteScreenshotsBySession(Session session);
}
