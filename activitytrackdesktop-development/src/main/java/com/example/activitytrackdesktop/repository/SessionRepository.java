package com.example.activitytrackdesktop.repository;

import com.example.activitytrackdesktop.model.Session;
import com.example.activitytrackdesktop.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

public interface SessionRepository extends CrudRepository<Session, Integer> {

}
