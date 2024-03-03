package com.example.activitytrackdesktop.persistence.impl;

import com.example.activitytrackdesktop.model.Screenshot;
import com.example.activitytrackdesktop.model.Session;
import com.example.activitytrackdesktop.model.SessionInfo;
import com.example.activitytrackdesktop.model.SessionPart;
import com.example.activitytrackdesktop.persistence.inter.ScreenshotService;
import com.example.activitytrackdesktop.persistence.inter.SessionPartService;
import com.example.activitytrackdesktop.persistence.inter.SessionService;
import com.example.activitytrackdesktop.persistence.inter.SessionsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class SessionsInfoServiceImpl implements SessionsInfoService {
    private SessionService sessionService;
    private SessionPartService sessionPartService;
    private ScreenshotService screenshotService;

    public SessionsInfoServiceImpl(@Autowired SessionService sessionService,
                                   @Autowired SessionPartService sessionPartService,
                                   @Autowired ScreenshotService screenshotService) {
        this.sessionService = sessionService;
        this.sessionPartService = sessionPartService;
        this.screenshotService = screenshotService;
    }

    @Override
    public List<SessionInfo> getSessionInfoList() {
        List<SessionInfo> sessionInfoList = new LinkedList<>();
        List<Session> sessions = sessionService.readAll();
        for(Session session: sessions){
            if(session.getHash() != session.hashCode()){
                sessionPartService.deleteAllBySession(session);
                screenshotService.deleteAllBySession(session);
                sessionService.deleteById(session.getId());
                continue;
            }
            System.out.println(session.getId());
            SessionInfo info = new SessionInfo();
            info.setUserId(session.getUser());
            info.setProjectId(session.getProject());
            info.setSession(session);
            List<SessionPart> sessionParts = sessionPartService.findAllBySession(session);
            List<SessionPart> sp = new LinkedList<>();
            for(SessionPart sessionPart: sessionParts){
                if(sessionPart.getHash() != sessionPart.hashCode()){
                    System.out.println(sessionPart.getHash());
                    System.out.println(sessionPart.hashCode());
                    sp.add(sessionPart);
                }
            }
            sessionParts.removeAll(sp);
            sessionPartService.deleteAll(sp);
            info.setSessionParts(sessionParts);
            List<Screenshot> screenshots = screenshotService.findAllBySession(session);
            List<Screenshot> sc = new LinkedList<>();
            for(Screenshot screenshot: screenshots){
                if(screenshot.getHash() != screenshot.hashCode()){
                    System.out.println(screenshot.getHash());
                    System.out.println(screenshot.hashCode());
                    sc.add(screenshot);
                }
            }
            screenshots.removeAll(sc);
            screenshotService.deleteAll(sc);
            info.setScreenshots(screenshots);
            sessionInfoList.add(info);
        }
        return sessionInfoList;
    }

    @Override
    public void deleteSessionInfoList() {
        sessionPartService.deleteAll();
        screenshotService.deleteAll();
        sessionService.deleteAll();
    }
}
