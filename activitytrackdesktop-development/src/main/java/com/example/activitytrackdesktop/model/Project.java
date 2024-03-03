package com.example.activitytrackdesktop.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.sql.Date;

@Data
//@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class Project {
    private int id;
    private User owner;
    private Date creationDate;
    private String name;
    private String description;
    private int sessionPartInterval = 10;
    private int screenshotInterval = 10;
}