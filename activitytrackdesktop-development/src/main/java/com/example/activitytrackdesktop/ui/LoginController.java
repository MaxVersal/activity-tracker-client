package com.example.activitytrackdesktop.ui;

import com.example.activitytrackdesktop.model.Project;
import com.example.activitytrackdesktop.model.Response;
import com.example.activitytrackdesktop.model.UserInfo;
import com.example.activitytrackdesktop.service.AuthenticationService;
import com.example.activitytrackdesktop.service.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

@Controller
public class LoginController extends JDialog {
    private AuthenticationService authenticationService;
    private CurrentUserService currentUserService;
    private MainController mainController;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginController(@Autowired AuthenticationService authenticationService,
                           @Autowired MainController mainController,
                           @Autowired CurrentUserService currentUserService) {
        this.authenticationService = authenticationService;
        this.mainController = mainController;
        this.currentUserService = currentUserService;
    }

    @PostConstruct
    public void init() {
        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");

        usernameField = new JTextField(15);
        passwordField = new JPasswordField();

        JButton okButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");
        statusLabel = new JLabel(" ");

        GridLayout gridLayOut = new GridLayout(2, 1);
        gridLayOut.setHgap(25);
        gridLayOut.setVgap(10);
        JPanel p3 = new JPanel(gridLayOut);
        p3.add(usernameLabel);
        p3.add(passwordLabel);

        JPanel p4 = new JPanel(gridLayOut);
        p4.add(usernameField);
        p4.add(passwordField);

        JPanel p1 = new JPanel();
        p1.add(p3);
        p1.add(p4);

        JPanel p2 = new JPanel();
        p2.add(okButton);
        p2.add(cancelButton);

        JPanel p5 = new JPanel(new BorderLayout());
        p5.add(p2, BorderLayout.CENTER);
        p5.add(statusLabel, BorderLayout.NORTH);
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        setLayout(new BorderLayout());
        add(p1, BorderLayout.CENTER);
        add(p5, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(usernameField.getText().length() < 5){
                    statusLabel.setText("Login length less then 5!");
                    return;
                }
                if(usernameField.getText().length() > 20){
                    statusLabel.setText("Login length more then 20!");
                    return;
                }
                if(passwordField.getPassword().length < 5){
                    statusLabel.setText("Password length less then 5!");
                    return;
                }
                if(passwordField.getPassword().length > 20){
                    statusLabel.setText("Password length more then 20!");
                    return;
                }
                Response<UserInfo> res = authenticationService.auth(usernameField.getText(), new String(passwordField.getPassword()));
                if(res.getStatus() == 200){
                    currentUserService.setCurrentInfo(res.getMessage(), new String(passwordField.getPassword()));
                    mainController.update();
                    setVisible(false);
                } else if(res.getStatus() == 403) {
                    statusLabel.setText("Incorrect login or password!");
                } else {
                    statusLabel.setText(res.getError());
                }
            }
        });
    }

    public void update(){
        statusLabel.setText("");
    }
}
