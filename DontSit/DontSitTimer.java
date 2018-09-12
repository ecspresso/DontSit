package DontSit;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;   
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class DontSitTimer {

    private final static MenuItem STOP_ITEM = new MenuItem("Stop timer");  // Start timer
    
    private static TrayIcon trayIcon;
    
    final static ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);
    final static MessageFrame MESSAGE_FRAME = new MessageFrame();
    
    static int lastTimerLenght;
    static boolean running = false;
    static Runnable messagePopup;

    public static void main(String[] args) {
        //<editor-fold defaultstate="collapsed" desc="Check support">
        // Check support
        try {
            if (!SystemTray.isSupported()) {
                System.err.println("System tray is not supported.");
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error when checking system tray support: " + e);
            System.exit(2);
        }
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Variables">
        // Variables
        final SystemTray sysTray = SystemTray.getSystemTray();  // System's tray
        messagePopup = () -> {
            java.awt.EventQueue.invokeLater(() -> {
                MESSAGE_FRAME.setLocationRelativeTo(null);
                MESSAGE_FRAME.setVisible(true);
            });
        };
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Tray icon size">
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
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Main menu items">
        // "Right-click" menu
        final PopupMenu popupMenu = new PopupMenu();            // Popup menu

        // Sub menu
        final PopupMenu settingsMenu = new PopupMenu("Settings"); // Settings for timer
        
        // Sub items
        final MenuItem aboutItem = new MenuItem("About");       // About button
        final MenuItem exitItem = new MenuItem("Exit");         // Exit application button
        final MenuItem sixtyMinutes = new MenuItem("60 minutes");
        final MenuItem forthyMinutes = new MenuItem("40 minutes");
        final MenuItem thirtyMinutes = new MenuItem("30 minutes");
        final MenuItem twentyMinutes = new MenuItem("20 minutes");
        
        // Sub menu item
        final CheckboxMenuItem alwaysOnTop = new CheckboxMenuItem("Always on top", true);
        final CheckboxMenuItem autoRequestFocus = new CheckboxMenuItem("Request focus", true);
        final CheckboxMenuItem focusable = new CheckboxMenuItem("Focusable", true);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Add items to popupMenu">
        // Add items to popupMenu
        popupMenu.add(sixtyMinutes);
        popupMenu.add(forthyMinutes);
        popupMenu.add(thirtyMinutes);
        popupMenu.add(twentyMinutes);
        popupMenu.add(STOP_ITEM);
        popupMenu.addSeparator(); // Seperator
        popupMenu.add(settingsMenu);
        popupMenu.addSeparator(); // Seperator
        popupMenu.add(aboutItem);
        popupMenu.add(exitItem);
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Add items to popupMenu">
        // Add items to popupMenu
        settingsMenu.add(alwaysOnTop);
        settingsMenu.add(autoRequestFocus);
        settingsMenu.add(focusable);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Action events">
        // Action events
        
        // 60 min
        sixtyMinutes.addActionListener((ActionEvent ae) -> {
            trayIcon.displayMessage("60 minutes", "Timer set to 60 minutes.", TrayIcon.MessageType.INFO);
            stopTimer();
            startTimer(60);
        });
        
        // 40 min
        forthyMinutes.addActionListener((ActionEvent ae) -> {
            trayIcon.displayMessage("40 minutes", "Timer set to 40 minutes.", TrayIcon.MessageType.INFO);
            stopTimer();
            startTimer(40);
        });
        
        // 30 min
        thirtyMinutes.addActionListener((ActionEvent ae) -> {
            trayIcon.displayMessage("30 minutes", "Timer set to 30 minutes.", TrayIcon.MessageType.INFO);
            stopTimer();
            startTimer(30);
        });
        
        // 20 min
        twentyMinutes.addActionListener((ActionEvent ae) -> {
            trayIcon.displayMessage("20 minutes", "Timer set to 20 minutes.", TrayIcon.MessageType.INFO);
            stopTimer();
            startTimer(20);
        });
        
        // Stop timer
        STOP_ITEM.addActionListener((ActionEvent ae) -> {
            stopTimer();
        });

        //  -- Separator --
        
        // Always on top
        alwaysOnTop.addItemListener((ItemEvent ie) -> {
            if(alwaysOnTop.getState()) {
                MESSAGE_FRAME.setAlwaysOnTop(true);
            } else {
                MESSAGE_FRAME.setAlwaysOnTop(false);
            }
        });
        
        // Request focus
        autoRequestFocus.addItemListener((ItemEvent ie) -> {
            if(autoRequestFocus.getState()) {
                MESSAGE_FRAME.setAutoRequestFocus(true);
            } else {
                MESSAGE_FRAME.setAutoRequestFocus(false);
            }
        });
        
        // Request focus
        focusable.addItemListener((ItemEvent ie) -> {
            if(focusable.getState()) {
                MESSAGE_FRAME.setFocusable(true);
            } else {
                MESSAGE_FRAME.setFocusable(true);
            }
        });
        
        //  -- Separator --
        
        // About
        aboutItem.addActionListener((ActionEvent ae) -> { // About button action
            JOptionPane.showMessageDialog(null, "Made by Emile Priller", "Don't Sit!", JOptionPane.PLAIN_MESSAGE, new ImageIcon(DontSitTimer.class.getClassLoader().getResource("Images/icon32x32.png")));
        });
        
        // Exit button
        exitItem.addActionListener((ActionEvent ae) -> { // Exit application button action
            sysTray.remove(trayIcon);
            System.exit(0);
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Add items to trayIcon">
        // Add items to trayIcon
        trayIcon.setPopupMenu(popupMenu);
        trayIcon.setToolTip("Don't Sit\nVersion 1.0");
        //</editor-fold>
   
        //<editor-fold defaultstate="collapsed" desc="Set the Windows look and feel">
        try {
            // Set the Windows look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(DontSitTimer.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Make MESSAGE_FORM draggable">
        // Make MESSAGE_FORM draggable
        MESSAGE_FRAME.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent me) {
                MESSAGE_FRAME.setLocation(MESSAGE_FRAME.getLocation().x+me.getX(),MESSAGE_FRAME.getLocation().y+me.getY());
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="MESSAGE_FORM: set location, icon">
        // Hide MESSAGE_FORM
        java.awt.EventQueue.invokeLater(() -> {
            MESSAGE_FRAME.setLocationRelativeTo(null);
            
            try {
                List<Image> icons = new ArrayList<>();
                icons.add(ImageIO.read(DontSitTimer.class.getResourceAsStream("/Images/icon16x16.png")));
                icons.add(ImageIO.read(DontSitTimer.class.getResourceAsStream("/Images/icon24x24.png")));
                icons.add(ImageIO.read(DontSitTimer.class.getResourceAsStream("/Images/icon32x32.png")));
                icons.add(ImageIO.read(DontSitTimer.class.getResourceAsStream("/Images/icon48x48.png")));
                icons.add(ImageIO.read(DontSitTimer.class.getResourceAsStream("/Images/icon64x64.png")));
                //icons.add(ImageIO.read(DontSitTimer.class.getResourceAsStream("/Images/icon96x96.png")));
                //icons.add(ImageIO.read(DontSitTimer.class.getResourceAsStream("/Images/icon128x128.png")));
                //icons.add(ImageIO.read(DontSitTimer.class.getResourceAsStream("/Images/icon256x256.png")));
                //icons.add(ImageIO.read(DontSitTimer.class.getResourceAsStream("/Images/icon512x512.png")));
                
                MESSAGE_FRAME.setIconImages(icons);
            } catch (IOException ex) {
                Logger.getLogger(DontSitTimer.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        //</editor-fold>
        
        //<editor-fold defaultstate="collapsed" desc="Start GUI">
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
        //</editor-fold>
                
        //<editor-fold defaultstate="collapsed" desc="Start first timer">
        // Start first timer
        //startTimer(30);
        startTimer(1);
        //</editor-fold> 
   }

    static void startTimer() {
        startTimerSCHEDULER(lastTimerLenght);
    }
    
    private static void startTimer(int timerLenght) {
        running = true;
        STOP_ITEM.setEnabled(true);
        lastTimerLenght = timerLenght;
        startTimerSCHEDULER(timerLenght);
    }

    private static void startTimerSCHEDULER(int timerLenght) {
        //SCHEDULER.schedule(messagePopup, timerLenght, TimeUnit.MINUTES);
        SCHEDULER.schedule(messagePopup, timerLenght, TimeUnit.SECONDS);
    }

    private static void stopTimer() {
        if (running) {
            SCHEDULER.shutdownNow();
            STOP_ITEM.setEnabled(false);
            running = false;
        }
    }

    private static Image CreateIcon(String path, String desc) {
        URL ImageURL = DontSitTimer.class.getResource(path);
        return (new ImageIcon(ImageURL, desc).getImage());
    }
}
