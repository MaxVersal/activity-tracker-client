package com.example.activitytrackdesktop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "sessions")
@Data
public class Session {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @Column(name = "user")
    private int user;

    @JsonIgnore
    @Column(name = "project")
    private int project;

    @Column(name = "start_time")
    private Time startTime;

    @Column(name = "end_time")
    private Time endTime;

    @Column(name = "date")
    private Date date;

    @Column(name = "duration")
    private int duration;

    @Column(name = "average_activity")
    private float averageActivity;

    @OneToMany(mappedBy="session")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private List<SessionPart> sessionParts;

    @OneToMany(mappedBy="session")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    private List<Screenshot> screenshots;

    @Column(name = "hash")
    @JsonIgnore
    private int hash;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return id == session.id && user == session.user && project == session.project && duration == session.duration && Float.compare(session.averageActivity, averageActivity) == 0 && startTime.equals(session.startTime) && endTime.equals(session.endTime) && date.equals(session.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, project, startTime.toString(), endTime.toString(), date.toString(), duration, averageActivity);
    }

    @Override
    public String toString() {
        return "session " + id;
    }
}
