package com.example.activitytrackdesktop.service;

import com.example.activitytrackdesktop.model.Project;
import com.example.activitytrackdesktop.model.Response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private CurrentUserService currentUserService;

    public ProjectService(@Autowired CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public Response<List<Project>> getProjectsWhereIConsistIn() {
        HttpURLConnection httpcon = null;
        Response<List<Project>> response = new Response<>();
        try {
            URL url=new URL("http://localhost:8080/projects/i_consist_in");
            httpcon = (HttpURLConnection)url.openConnection();
            httpcon.setDoOutput(true);
            httpcon.setRequestMethod("GET");
            httpcon.setRequestProperty("Accept", "*/*");
            httpcon.setRequestProperty("Authorization", "Bearer " + currentUserService.getToken());
            httpcon.connect();
            String result;
            try(BufferedReader br = new BufferedReader(new InputStreamReader(httpcon.getInputStream()))) {
                result = br.lines().collect(Collectors.joining("\n"));
            }
            System.out.println(result);
            //ObjectMapper mapper = new ObjectMapper();
            ObjectMapper mapper = getObjectMapper();
            List<Project> projects;
            if(httpcon.getResponseCode() == 200){
                TypeReference<List<Project>> type = new TypeReference<List<Project>>(){};
//                JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, Project.class);
                projects = mapper.readValue(result, type);
                response.setStatus(httpcon.getResponseCode());
                response.setMessage(projects);
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
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private abstract class IgnoreHibernatePropertiesInJackson{ }
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(Object.class, IgnoreHibernatePropertiesInJackson.class);
        return mapper;
    }
}

