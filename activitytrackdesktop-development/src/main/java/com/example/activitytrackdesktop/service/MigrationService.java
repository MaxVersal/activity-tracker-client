package com.example.activitytrackdesktop.service;

import com.example.activitytrackdesktop.model.Response;
import com.example.activitytrackdesktop.model.Session;
import com.example.activitytrackdesktop.model.SessionInfo;
import com.example.activitytrackdesktop.model.UserInfo;
import com.example.activitytrackdesktop.persistence.inter.SessionService;
import com.example.activitytrackdesktop.persistence.inter.SessionsInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MigrationService {
    private CurrentUserService currentUserService;
    private SessionsInfoService sessionsInfoService;

    public MigrationService(@Autowired CurrentUserService currentUserService,
                            @Autowired SessionsInfoService sessionsInfoService) {
        this.currentUserService = currentUserService;
        this.sessionsInfoService = sessionsInfoService;
    }

    public Response<Boolean> send() {
        HttpURLConnection httpcon = null;
        Response<Boolean> response = new Response<>();
        try {
            List<SessionInfo> info = sessionsInfoService.getSessionInfoList();
            URL url = new URL("http://localhost:8080/sessions");
            httpcon = (HttpURLConnection)url.openConnection();
            httpcon.setDoOutput(true);
            httpcon.setDoInput(true);
            httpcon.setRequestMethod("POST");
            httpcon.setRequestProperty("Accept", "*/*");
            httpcon.setRequestProperty("Content-Type", "application/json");
            httpcon.setRequestProperty("Authorization", "Bearer " + currentUserService.getToken());
            ObjectMapper requestMapper = new ObjectMapper();
            try(OutputStreamWriter wr= new OutputStreamWriter(httpcon.getOutputStream())){
                requestMapper.writeValue(wr, info);
            }
            String result;
            try(BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream()))) {
                result = br.lines().collect(Collectors.joining("\n"));
            }
            if(httpcon.getResponseCode() == 200){
                sessionsInfoService.deleteSessionInfoList();
                response.setStatus(httpcon.getResponseCode());
                response.setMessage(true);
                return response;
            } else {
                response.setStatus(httpcon.getResponseCode());
                response.setError(result);
                response.setMessage(false);
                return response;
            }
        } catch (IOException exc) {
            response.setError(exc.getMessage());
            try {
                response.setStatus(httpcon.getResponseCode());
            } catch (IOException e) {
                response.setError("Impossible to connect to the server!");
            }
            response.setMessage(false);
            return response;
        }
    }
}
