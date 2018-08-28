package DontSit;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class DontSitTimer {

    private final static ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    private static TrayIcon trayIcon;
    private final static MenuItem STOP_ITEM = new MenuItem("Stop timer");  // Start timer
    private static int lastTimerLenght;
    private static boolean keepRunning = false;
    private static boolean running = false;

    public static void main(String[] args) {
        // Check support.
        try {
            if (!SystemTray.isSupported()) {
                System.err.println("System tray is not supported.");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error when checking system tray support: " + e);
            System.exit(2);
        }

        // Variables
        final SystemTray sysTray = SystemTray.getSystemTray();  // System's tray

        // Tray icon size
        int trayIconWidth = sysTray.getTrayIconSize().width;
        switch (trayIconWidth) {
            case 16:
                trayIcon = new TrayIcon(CreateIcon("/Images/icon16x16.png", "Tray Icon"));
                break;
            case 24:
                trayIcon = new TrayIcon(CreateIcon("/Images/icon24x24.png", "Tray Icon"));
                break;
            case 32:
                trayIcon = new TrayIcon(CreateIcon("/Images/icon32x32.png", "Tray Icon"));
                break;
            case 48:
                trayIcon = new TrayIcon(CreateIcon("/Images/icon48x48.png", "Tray Icon"));
                break;
            case 64:
                trayIcon = new TrayIcon(CreateIcon("/Images/icon64x64.png", "Tray Icon"));
                break;
            default:
                try {
                    BufferedImage trayIconImage = ImageIO.read(DontSitTimer.class.getResource("/Images/icon64x64.png.png"));
                    trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));
                } catch (IOException e) {
                    System.err.println("Could not find the image: " + e);
                    System.exit(4);
                }
                break;

        }

        // Main menu items
        //final MenuItem showItem = new MenuItem("Show");         // Show button
        final MenuItem aboutItem = new MenuItem("About");       // About button
        final MenuItem exitItem = new MenuItem("Exit");         // Exit application button
        //final Menu timerMenu = new Menu("Timer");               // Settings for timer
        final PopupMenu popupMenu = new PopupMenu();            // Popup menu
        // Sub menu items
        final MenuItem sixtyMinutes = new MenuItem("60 minutes");
        final MenuItem forthyMinutes = new MenuItem("40 minutes");
        final MenuItem thirtyMinutes = new MenuItem("30 minutes");
        final MenuItem twentyMinutes = new MenuItem("20 minutes");

        // Action events
        sixtyMinutes.addActionListener((ActionEvent ae) -> {
            trayIcon.displayMessage("60 minutes", "Timer set to 60 minutes.", TrayIcon.MessageType.INFO);
            stopTimer();
            startTimer(60, "Det har gått 60 minuter.\nDags att röra på sig.", true);
        });
        forthyMinutes.addActionListener((ActionEvent ae) -> {
            trayIcon.displayMessage("40 minutes", "Timer set to 40 minutes.", TrayIcon.MessageType.INFO);
            stopTimer();
            startTimer(40, "Det har gått 40 minuter.\nDags att röra på sig.", true);
        });
        thirtyMinutes.addActionListener((ActionEvent ae) -> {
            trayIcon.displayMessage("30 minutes", "Timer set to 30 minutes.", TrayIcon.MessageType.INFO);
            stopTimer();
            startTimer(30, "Det har gått 30 minuter.\nDags att röra på sig.", true);
        });
        twentyMinutes.addActionListener((ActionEvent ae) -> {
            trayIcon.displayMessage("20 minutes", "Timer set to 20 minutes.", TrayIcon.MessageType.INFO);
            stopTimer();
            startTimer(20, "Det har gått 20 minuter.\nDags att röra på sig.", true);
        });
        STOP_ITEM.addActionListener((ActionEvent ae) -> {
            stopTimer();
        });

        aboutItem.addActionListener((ActionEvent ae) -> { // About button action
            JOptionPane.showMessageDialog(null, "Made by Emile Priller", "Don't Sit!", JOptionPane.PLAIN_MESSAGE, new ImageIcon(DontSitTimer.class.getClassLoader().getResource("Images/icon32x32.png")));
        });
        exitItem.addActionListener((ActionEvent ae) -> { // Exit application button action
            sysTray.remove(trayIcon);
            System.exit(0);
        });

        popupMenu.add(sixtyMinutes);
        popupMenu.add(thirtyMinutes);
        popupMenu.add(forthyMinutes);
        popupMenu.add(twentyMinutes);
        popupMenu.add(STOP_ITEM);
        popupMenu.addSeparator(); // Seperator
        popupMenu.add(aboutItem);
        popupMenu.add(exitItem);

        trayIcon.setPopupMenu(popupMenu);
        trayIcon.setToolTip("Don't Sit\nVersion 1.0");

        // Start GUI
        Runnable gui = () -> {
            // Get system tray and trayIcon to it.
            try {
                sysTray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("Error when adding icon to system tray: " + e);
                System.exit(3);
            }
        };
        Thread guiThread = new Thread(gui);
        guiThread.setName("guiThread");
        guiThread.start();

        // Start first timer
        startTimer(30, "Det har gått 30 minuter.\nDags att röra på sig.", true);
    }

    private static void startTimer(int timerLenght, String message, boolean keepRun) {
        keepRunning = keepRun;
        running = true;
        STOP_ITEM.setEnabled(true);
        lastTimerLenght = timerLenght;
        startTimer(timerLenght, message);
    }

    private static void startTimer(int timerLenght, String message) {
        Runnable messagePopup = () -> {
            JOptionPane.showMessageDialog(null, message);
            if (keepRunning) {
                startTimer(lastTimerLenght, message);
            }
        };

        SCHEDULER.schedule(messagePopup, timerLenght, TimeUnit.MINUTES);
    }

    private static void stopTimer() {
        if (running) {
            keepRunning = false;
            SCHEDULER.shutdownNow();
            STOP_ITEM.setEnabled(false);
        }
    }

    private static Image CreateIcon(String path, String desc) {
        URL ImageURL = DontSitTimer.class.getResource(path);
        return (new ImageIcon(ImageURL, desc).getImage());
    }
}
