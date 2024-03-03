package com.example.activitytrackdesktop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

@Entity
@Table(name = "session_parts")
@Data
public class SessionPart {
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

    @ManyToOne
    @JoinColumn(name = "session_id")
    @JsonIgnore
    private Session session;

    @Column(name = "start_time")
    private Time startTime;

    @Column(name = "end_time")
    private Time endTime;

    @Column(name = "date")
    private Date date;

    @Column(name = "duration")
    private int duration;

    @Column(name = "mouse_click")
    private int mouseClick;

    @Column(name = "mouse_move")
    private int mouseMove;

    @Column(name = "key_click")
    private int keyClick;

    @Column(name = "average_activity")
    private float averageActivity;

    @Column(name = "hash")
    @JsonIgnore
    private int hash;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionPart that = (SessionPart) o;
        return id == that.id && user == that.user && project == that.project && duration == that.duration && mouseClick == that.mouseClick && mouseMove == that.mouseMove && keyClick == that.keyClick && Float.compare(that.averageActivity, averageActivity) == 0 && startTime.equals(that.startTime) && endTime.equals(that.endTime) && date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, project, startTime.toString(), endTime.toString(), date.toString(), duration, mouseClick, mouseMove, keyClick, averageActivity);
    }

    @Override
    public String toString() {
        return "session part " + id;
    }
}
