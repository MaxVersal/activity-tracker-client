package com.example.activitytrackdesktop.service;

import com.example.activitytrackdesktop.model.Response;
import com.example.activitytrackdesktop.model.User;
import com.example.activitytrackdesktop.model.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

@Service
public class AuthenticationService{

    public Response<UserInfo> auth(String userName, String password) {
        HttpURLConnection httpcon = null;
        Response<UserInfo> response = new Response<>();
        try {
            URL url=new URL("http://localhost:8080/login?nickname=" + userName + "&password=" + password);
            httpcon = (HttpURLConnection)url.openConnection();
            httpcon.setDoOutput(true);
            httpcon.setRequestMethod("GET");
            httpcon.setRequestProperty("Accept", "*/*");
            httpcon.connect();
            String result;
            try(BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream()))) {
                result = br.lines().collect(Collectors.joining("\n"));
            }
            httpcon.disconnect();
            ObjectMapper mapper = new ObjectMapper();
            UserInfo info;
            if(httpcon.getResponseCode() == 200){
                info = mapper.readValue(result, UserInfo.class);
                response.setStatus(httpcon.getResponseCode());
                response.setMessage(info);
                return response;
            } else {
                response.setStatus(httpcon.getResponseCode());
                response.setError(result);
                return response;
            }
        } catch (IOException exc) {
            response.setError(exc.getMessage());
            try {
                response.setStatus(httpcon.getResponseCode());
            } catch (IOException e) {
                response.setError("Impossible to connect to the server!");
            }
            return response;
        }
    }
}
