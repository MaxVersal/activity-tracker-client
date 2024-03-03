package com.example.activitytrackdesktop;

import com.example.activitytrackdesktop.ui.LoginController;
import com.example.activitytrackdesktop.ui.MainController;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class ActivitydesktopCommandLine implements CommandLineRunner {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LoginController loginController;

    @Override
    public void run(String... args) throws Exception {
        jdbcTemplate.execute("create table if not exists sessions(\n" +
                "id integer primary key autoincrement not null, \n" +
                "user integer,\n" +
                "project integer, \n" +
                "start_time time, \n" +
                "end_time time, \n" +
                "date date,\n" +
                "duration integer, \n" +
                "average_activity float,\n" +
                "hash integer \n" +
                ")\n");

        jdbcTemplate.execute("create table if not exists session_parts(\n" +
                "id integer primary key autoincrement not null, \n" +
                "user integer,\n" +
                "project integer, \n" +
                "session_id integer,\n" +
                "start_time time, \n" +
                "end_time time, \n" +
                "date date,\n" +
                "duration integer, \n" +
                "mouse_click integer,\n" +
                "mouse_move integer,\n" +
                "key_click integer,\n" +
                "average_activity float," +
                "hash integer \n" +
                ")\n");

        jdbcTemplate.execute("create table if not exists screenshots(\n" +
                "id integer primary key autoincrement not null, \n" +
                "user integer,\n" +
                "project integer, \n" +
                "session_id integer,\n" +
                "time time, \n" +
                "date date,\n" +
                "data text,\n" +
                "hash integer\n" +
                ")\n");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch (NativeHookException e1) {
                    e1.printStackTrace();
                }
            }
        });
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    loginController.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
