package com.example.activitytrackdesktop.service;

import com.example.activitytrackdesktop.model.Project;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurrentProjectService {
    private List<Project> projects;
    private int[] times;
    private int index;
    private Project currentProjects;

    public Project getCurrentProject() {
        return currentProjects;
    }

    public boolean setCurrentProjectByName(String name) {
        Optional<Project> project = projects.stream().filter(x -> x.getName().equals(name)).findFirst();
        if(project.isEmpty()){
            return false;
        }else {
            currentProjects = project.get();
            index = projects.indexOf(currentProjects);
            return true;
        }
    }
    public void reset(List<Project> projects) {
        this.projects = projects;
        currentProjects = null;
        times = new int[projects.size()];
    }
    public int getWorkTime() {
        return times[index];
    }
    public void addWorkTime() {
        times[index]++;
    }

    public void resetWorkTime() {
        for (int i = 0; i < times.length; i++){
            times[i] = 0;
        }
    }
}
