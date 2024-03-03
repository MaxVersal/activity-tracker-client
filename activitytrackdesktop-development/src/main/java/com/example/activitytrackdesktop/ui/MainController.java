package com.example.activitytrackdesktop.ui;

import com.example.activitytrackdesktop.model.*;
import com.example.activitytrackdesktop.persistence.inter.ScreenshotService;
import com.example.activitytrackdesktop.persistence.inter.SessionPartService;
import com.example.activitytrackdesktop.service.*;
import com.example.activitytrackdesktop.persistence.inter.SessionService;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Time;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Controller
public class MainController extends JFrame {
    private CurrentUserService currentUserService;
    private ProjectService projectService;
    private CurrentProjectService currentProjectService;
    private SessionService sessionService;
    private SessionPartService sessionPartService;
    private ScreenshotService screenshotService;
    private MigrationService migrationService;
    private AuthenticationService authenticationService;

    private JLabel helloLabel;
    private JLabel timerLabel;
    private JLabel projectLabel;
    private JButton startButton;
    private JButton sendButton;
    private JComboBox<String> projectsComboBox;

    private Timer clockTimer = new Timer("clockTimer");
    private TimerTask timerTask;
    private Timer sessionPartTimer = new Timer("sessionPartTimer");
    private TimerTask sessionPartTask;
    private Timer screenshotTimer = new Timer("screenshotTimer");
    private TimerTask screenshotTask;
    private Timer screenshotMakerTimer = new Timer("screenshotMakerTimer");
    private TimerTask screenshotMakingTask;
    private Timer sendingTimer = new Timer("sendingTimer");
    private TimerTask sendingTask;

    private int totalKeyClick = 0;
    private int totalMouseMove = 0;
    private int totalMouseClick = 0;

    private boolean startTracking = false;

    private Session session;
    private int duration = 0;

    private LinkedList<SessionPart> sessionParts;
    private SessionPart sessionPart;
    private int sessionPartDuration = 0;

    private Object lock = new Object();

    @Autowired
    public void setCurrentUserService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Autowired
    public void setCurrentProjectService(CurrentProjectService currentProjectService) {
        this.currentProjectService = currentProjectService;
    }

    @Autowired
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Autowired
    public void setSessionPartService(SessionPartService sessionPartService) {
        this.sessionPartService = sessionPartService;
    }

    @Autowired
    public void setScreenshotService(ScreenshotService screenshotService) {
        this.screenshotService = screenshotService;
    }

    @Autowired
    public void setMigrationService(MigrationService migrationService) {
        this.migrationService = migrationService;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostConstruct
    public void init(){
        setTitle("Activity Tracking");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(260, 260);
        setLocation(650, 390);
        setResizable(false);

        GridLayout gridLayout = new GridLayout(6, 1);
        gridLayout.setHgap(10);
        gridLayout.setVgap(15);
        JPanel panel = new JPanel(gridLayout);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));

        helloLabel = new JLabel(" ");
        helloLabel.setAlignmentY(0.5f);
        helloLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        helloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(helloLabel);

        projectLabel = new JLabel("Choose your project:");
        projectLabel.setAlignmentX(0.5f);
        projectLabel.setHorizontalTextPosition(JLabel.CENTER);
        projectLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(projectLabel);

        projectsComboBox = new JComboBox();
        projectsComboBox.setAlignmentX(0.5f);
        projectsComboBox.setSize(250, 30);
        panel.add(projectsComboBox);

        timerLabel = new JLabel("00:00:00");
        timerLabel.setAlignmentX(0.5f);
        timerLabel.setHorizontalTextPosition(JLabel.CENTER);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(timerLabel);

        startButton = new JButton("Start");
        panel.add(startButton);



        sendButton = new JButton("Send info to the server");
        panel.add(sendButton);
        sendButton.setVisible(false);

        panel.setAlignmentX(0.5f);
        panel.setAlignmentY(0.5f);
        getContentPane().add(panel);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                synchronized (lock){
                    setEnabled(false);
                    Response<Boolean> response = migrationService.send();
                    if(response.getStatus() == 403){
                        Response<UserInfo> res = authenticationService.auth(currentUserService.getCurrentUser().getNickname(), currentUserService.getPassword());
                        currentUserService.setToken(res.getMessage().getToken());
                        response = migrationService.send();
                    }
                    if(response.getMessage()){
                        JOptionPane.showInternalMessageDialog(null, "Info was upload to server", "Success!", TrayIcon.MessageType.INFO.ordinal());
                    }else {
                        JOptionPane.showInternalMessageDialog(null, response.getError(), "Error", TrayIcon.MessageType.ERROR.ordinal());
                    }
                    setEnabled(true);
                }
            }
        });

        projectsComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                synchronized (lock){
                    currentProjectService.setCurrentProjectByName(itemEvent.getItem().toString());
                    projectLabel.setText(currentProjectService.getCurrentProject().getName());
                    timerLabel.setText(getTime(currentProjectService.getWorkTime()));
                }
            }
        });

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                synchronized (lock){
                    if(startTracking){
                        stopAction();
                    } else {
                        startAction();
                    }
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                synchronized (lock){
                    //test
                    if(startTracking){
                        stopAction();
                    }
                    System.exit(0);
                }
            }
        });
    }

    public void sendInfo() {
        Response<Boolean> response = migrationService.send();
        if(response.getStatus() == 403){
            Response<UserInfo> res = authenticationService.auth(currentUserService.getCurrentUser().getNickname(), currentUserService.getPassword());
            currentUserService.setToken(res.getMessage().getToken());
            response = migrationService.send();
        }
        if(response.getMessage()){
            JOptionPane.showInternalMessageDialog(null, "Info was upload to server", "Success!", TrayIcon.MessageType.INFO.ordinal());
        }else {
            JOptionPane.showInternalMessageDialog(null, response.getError(), "Error", TrayIcon.MessageType.ERROR.ordinal());
        }
        startAction();
    }
    public void update(){

        helloLabel.setText("Hello, " + currentUserService.getCurrentUser().getNickname() + "!");
        projectsComboBox.removeAllItems();
        List<Project> projects = projectService.getProjectsWhereIConsistIn().getMessage();
        System.out.println(projects);
        currentProjectService.reset(projects);

        projects.forEach(x -> projectsComboBox.addItem(x.getName()));
        setVisible(true);
    }

    private void startAction(){
        try {
            setupNativeHook();
        }catch (NativeHookException ex){
            JOptionPane.showInternalMessageDialog(null, ex.getMessage(), "Error", TrayIcon.MessageType.ERROR.ordinal());
        }
        startTracking = true;
        startButton.setText("Stop");
        projectsComboBox.setEnabled(false);
        sendButton.setEnabled(false);
        session = new Session();
        session.setUser(currentUserService.getCurrentUser().getId());
        session.setProject(currentProjectService.getCurrentProject().getId());
        session.setStartTime(Time.valueOf(LocalTime.now()));
        session.setDate(Date.valueOf(LocalDate.now()));
        session = sessionService.save(session);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                synchronized (lock){
                    if(!startTracking){
                        cancel();
                        return;
                    }
                    currentProjectService.addWorkTime();
                    duration++;
                    sessionPartDuration++;
                    timerLabel.setText(getTime(currentProjectService.getWorkTime()));
                    if(!LocalTime.now().isBefore(LocalTime.of(23,59,59)) && startTracking){
                        setEnabled(false);
                        stopAction();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startAction();
                        setEnabled(true);
                        cancel();
                    }
                }
            }
        };
        clockTimer.scheduleAtFixedRate(timerTask, 1000, 1000);

        if(sessionParts == null){
            sessionParts = new LinkedList<>();
        } else {
            sessionParts.clear();
        }
        sessionPart = new SessionPart();
        sessionPart.setUser(currentUserService.getCurrentUser().getId());
        sessionPart.setProject(currentProjectService.getCurrentProject().getId());
        sessionPart.setStartTime(Time.valueOf(LocalTime.now()));
        sessionPart.setDate(Date.valueOf(LocalDate.now()));
        sessionPart.setSession(session);
        int sessionPartRate = currentProjectService.getCurrentProject().getSessionPartInterval();
        System.out.println(sessionPartRate);
        sessionPartTask = new TimerTask() {
            @Override
            public void run() {
                synchronized (lock){
                    if(startTracking){
                        addSessionPart(sessionPartRate);
                    } else {
                        cancel();
                    }
                }
            }
        };
        sessionPartTimer.scheduleAtFixedRate(sessionPartTask, 60 * 1000, 60 * 1000);

        int screenshotRate = currentProjectService.getCurrentProject().getScreenshotInterval();
        screenshotTask = new TimerTask() {
            @Override
            public void run() {
                synchronized (lock){
                    if(!startTracking){
                        cancel();
                        return;
                    }
                    screenshotMakingTask = new TimerTask() {
                        @Override
                        public void run() {
                            synchronized (lock){
                                if(!startTracking){
                                    cancel();
                                    return;
                                }
                                Screenshot screenshot = new Screenshot();
                                screenshot.setUser(currentUserService.getCurrentUser().getId());
                                screenshot.setProject(currentProjectService.getCurrentProject().getId());
                                screenshot.setSession(session);
                                screenshot.setDate(Date.valueOf(LocalDate.now()));
                                screenshot.setTime(Time.valueOf(LocalTime.now()));
                                String screenName = screenshot.getUser() + "_" + screenshot.getProject() + "_" + screenshot.getSession().getId() + "_" +
                                        new SimpleDateFormat("HHmmss").format(screenshot.getTime()) + "_" + screenshot.getDate();
                                makeScreenshot(screenName);
                                try {
                                    screenName = ScreenshotUploading.uploadScreenshot(screenName);
                                } catch (IOException | GeneralSecurityException e) {
                                    e.printStackTrace();
                                }
                                screenshot.setData(screenName);
                                System.out.println(screenName);
                                screenshot.setHash(screenshot.hashCode());
                                screenshotService.save(screenshot);
                            }
                        }
                    };
                    int delay = new Random().nextInt(screenshotRate);
                    screenshotMakerTimer.schedule(screenshotMakingTask ,delay * 1000);
                }
            }
        };
        screenshotTimer.scheduleAtFixedRate(screenshotTask, 5 * 1000, screenshotRate * 1000);

        sendingTask = new TimerTask() {
            @Override
            public void run() {
                synchronized (lock){
                    if(startTracking){
                        saveLastSession();
                        sendInfo();
                    } else {
                        cancel();
                    }
                }
            }
        };
        sendingTimer.scheduleAtFixedRate(sendingTask, 2 * 60 * 1000, 2 * 60 * 1000);
    }

    private void stopAction(){
        startTracking = false;
        startButton.setText("Start");
        projectsComboBox.setEnabled(true);
        sendButton.setEnabled(true);
        saveLastSession();
        timerTask.cancel();
        sessionPartTask.cancel();
        screenshotTask.cancel();
        screenshotMakingTask.cancel();
    }

    private void saveLastSession() {
        session.setEndTime(Time.valueOf(LocalTime.now()));
        session.setDuration(duration);
        duration = 0;
        addSessionPart(sessionPartDuration);
        session.setAverageActivity((float) sessionParts.stream().map(x -> x.getAverageActivity()).collect(Collectors.summarizingDouble(x -> x)).getAverage());
        session.setHash(session.hashCode());
        sessionService.save(session);
    }

    private void makeScreenshot(String screenName){
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] screens = ge.getScreenDevices();

            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle();

            for (GraphicsDevice screen : screens) {
                Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
                screenRect.width += screenBounds.width;
                screenRect.height = Math.max(screenRect.height, screenBounds.height);
            }

            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            File imageFile = new File("screenshots/"  +screenName + ".jpg");
            imageFile.createNewFile();
            ImageIO.write(screenFullImage, "jpg", imageFile);
        } catch (Exception e){
            System.out.println(e.getMessage());
            JOptionPane.showInternalMessageDialog(null, e.getMessage(), "Error", TrayIcon.MessageType.ERROR.ordinal());
        }
    }

    private void setupNativeHook() throws NativeHookException {
        GlobalScreen.registerNativeHook();
        LogManager.getLogManager().reset();

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent event) {
                if (startTracking) {
                    totalKeyClick += 1;
                }
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent event) { }

            @Override
            public void nativeKeyTyped(NativeKeyEvent event) { }
        });
        GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
            @Override
            public void nativeMouseClicked(NativeMouseEvent event) { }

            @Override
            public void nativeMousePressed(NativeMouseEvent event) {
                if (startTracking) {
                    totalMouseClick += 1;
                }
            }

            @Override
            public void nativeMouseReleased(NativeMouseEvent event) { }

        });
        GlobalScreen.addNativeMouseWheelListener(new NativeMouseWheelListener() {
            @Override
            public void nativeMouseWheelMoved(NativeMouseWheelEvent arg0) { }

        });
        GlobalScreen.addNativeMouseMotionListener(new NativeMouseMotionListener() {
            @Override
            public void nativeMouseMoved(NativeMouseEvent arg0) {
                if (startTracking) {
                    totalMouseMove += 1;
                }
            }

            @Override
            public void nativeMouseDragged(NativeMouseEvent arg0) { }
        });
    }

    private String getTime(int sec){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3; i++){
            builder.append(sec % 10 + "");
            sec /= 10;
            builder.append(sec % 6 + "");
            sec /= 6;
            if(i != 2){
                builder.append(":");
            }
        }
        return builder.reverse().toString();
    }

    private void addSessionPart(int interval){
        sessionPart.setEndTime(Time.valueOf(LocalTime.now()));
        sessionPart.setDuration(interval);
        if (interval > 0) {
            totalMouseMove = totalMouseMove / interval;
            if (totalMouseMove + totalMouseClick + totalKeyClick > interval) {
                sessionPart.setAverageActivity(1);
            } else {
                sessionPart.setAverageActivity((float) ((totalMouseMove + totalMouseClick + totalKeyClick) * 1.0 / interval));
            }
        }
        else {
            sessionPart.setAverageActivity(0);
        }
        sessionPart.setKeyClick(totalKeyClick);
        totalKeyClick = 0;
        sessionPart.setMouseClick(totalMouseClick);
        totalMouseClick = 0;
        sessionPart.setMouseMove(totalMouseMove);
        totalMouseMove = 0;
        sessionPart.setHash(sessionPart.hashCode());
        sessionPartService.save(sessionPart);
        sessionParts.add(sessionPart);
        sessionPartDuration = 0;
        if(startTracking){
            sessionPart = new SessionPart();
            sessionPart.setUser(currentUserService.getCurrentUser().getId());
            sessionPart.setProject(currentProjectService.getCurrentProject().getId());
            sessionPart.setStartTime(Time.valueOf(LocalTime.now()));
            sessionPart.setDate(Date.valueOf(LocalDate.now()));
            sessionPart.setSession(session);
        }
    }
}
