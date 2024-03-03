package com.example.activitytrackdesktop.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "screenshots")
@Data
public class Screenshot {
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

    @Column(name = "time")
    private Time time;

    @Column(name = "date")
    private Date date;

    @Column(name = "data")
    private String data;

    @Column(name = "hash")
    @JsonIgnore
    private int hash;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Screenshot that = (Screenshot) o;
        return id == that.id && user == that.user && project == that.project && time.equals(that.time) && date.equals(that.date) && data.equals(data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(user, project, time.toString(), date.toString());
        result = 31 * result + data.hashCode();
        return result;
    }
}
