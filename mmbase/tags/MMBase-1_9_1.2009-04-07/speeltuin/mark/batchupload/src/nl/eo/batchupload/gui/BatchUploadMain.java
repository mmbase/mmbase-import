package nl.eo.batchupload.gui;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import javax.media.jai.widget.ImageCanvas;
import javax.swing.*;

import nl.eo.batchupload.Config;
import nl.eo.batchupload.connector.DoveWrapper;
import nl.eo.batchupload.upload.UploadImage;
import nl.eo.batchupload.upload.UploadQueue;
import nl.eo.batchupload.upload.UploadThread;

/**
 * Batchupload main class.<br>
 * <br>
 * This class represents the 'desktop' environment containing the internal
 * frames.
 */
public class BatchUploadMain {
    static JFrame mainFrame = new JFrame("Batch Upload");
    static JDesktopPane desktop = new JDesktopPane();
    static JLabel statusBar = new JLabel("Status: not logged in");

    static JInternalFrame uploadFrame = new UploadQueueFrame();
    static UploadQueue uploadQueue = new UploadQueue(((UploadQueueFrame)uploadFrame).getTable());

    static LoginFrame loginFrame;
	static DoveWrapper dove;

    static boolean loggedin = false;
    static String currentuser = "";
		
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
               UIManager.getCrossPlatformLookAndFeelClassName()); 
        } catch (Exception e) { 
            e.printStackTrace();
        }

        desktop.setPreferredSize(new Dimension(800,600));
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

        uploadFrame.setVisible(true);
//        uploadFrame.setEnabled(false);
        desktop.add(uploadFrame);

        mainFrame.setDefaultCloseOperation(mainFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                tryExit();
            }
        });

        JMenuBar menubar = new JMenuBar();
        JMenu actionMenu = new JMenu("Actions");
        actionMenu.setMnemonic(KeyEvent.VK_A);

        actionMenu.add("Login ...");
        actionMenu.add("Logout ...");
        actionMenu.addSeparator();
        actionMenu.add("New upload ...");
        actionMenu.addSeparator();
        actionMenu.add("Quit");

         ActionListener menuAction = new ActionListener() {
            /**
             * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
             */
            public void actionPerformed(ActionEvent arg0) {
                if (arg0.getActionCommand().equals("Login ...")) {
                    if (loginFrame != null) {
                        loginFrame.dispose();
                    }
                    loginFrame = new LoginFrame ();
                    loginFrame.setVisible(true);
                    desktop.add(loginFrame);
                    try {
                        loginFrame.setSelected(true);
                        loginFrame.focusUsername();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (arg0.getActionCommand().equals ("Logout ...")) {
                    if (loginFrame != null) {
                        loginFrame.dispose();
                    }
                    
                    JInternalFrame[] framelist = desktop.getAllFrames();
                    for (int i=0; i<framelist.length; i++) {
                        if (framelist[i] instanceof StepsFrame) {
                           JOptionPane.showMessageDialog(desktop,
                           "You cannot logout when you are working on uploads\nPlease close all upload windows first.",
                            "Logout error",
                            JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    
                    mainFrame.getJMenuBar().getMenu(0).getItem(0).setEnabled(true);
                    mainFrame.getJMenuBar().getMenu(0).getItem(1).setEnabled(false);
                    mainFrame.getJMenuBar().getMenu(0).getItem(3).setEnabled(false);
                    loggedin = false;
                    statusBar.setText("Status: not logged in");
                } else if (arg0.getActionCommand().equals("New upload ...")) {
                    StepsFrame stepsFrame;
                    
                    stepsFrame = new StepsFrame();
                    stepsFrame.setVisible(true);
                    desktop.add(stepsFrame);
                    try {
                        stepsFrame.setSelected(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (arg0.getActionCommand().equals("Quit")) {
                    tryExit();
                } else {

                }
            }
        };
        
        menubar.add(actionMenu);
        menubar.getMenu(0).getItem(1).setEnabled(false);
        menubar.getMenu(0).getItem(3).setEnabled(false);

        menubar.getMenu(0).getItem(0).addActionListener(menuAction);
        menubar.getMenu(0).getItem(1).addActionListener(menuAction);
        menubar.getMenu(0).getItem(3).addActionListener(menuAction);
        menubar.getMenu(0).getItem(5).addActionListener(menuAction);

        statusBar.setBackground(Color.lightGray);
        statusBar.setForeground(Color.black);
        statusBar.setOpaque(true);

        JDesktopPane realdesktop = new JDesktopPane();
        realdesktop.setLayout(new BorderLayout());
        realdesktop.add(statusBar, BorderLayout.SOUTH);
        realdesktop.add(desktop, BorderLayout.CENTER);
        
        mainFrame.setContentPane(realdesktop);

        mainFrame.setJMenuBar(menubar);
        mainFrame.pack();
        mainFrame.setVisible(true);

        if (!Config.exists()) {
            if (System.getProperty("dovelocation")!=null) {
                  Config.setValue("dovelocation",System.getProperty("dovelocation"));
            } else {
               String doveLocation = JOptionPane.showInputDialog(
                    desktop,
                    "Please supply location of Dove Servlet:",
                    "No config file found!",
                    JOptionPane.ERROR_MESSAGE);
                Config.setValue("dovelocation", doveLocation);
                Config.store();
            }
        } else {
            Config.open();
        }

        loginFrame = new LoginFrame ();
        loginFrame.setVisible(true);
        desktop.add(loginFrame);
        try {
            loginFrame.setSelected(true);
            loginFrame.focusUsername();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static JDesktopPane getDesktop() {
        return desktop;
    }

	/**
	 * Show a frame for an image where all renderings are applied
	 * @param img UploadImage that describes the image + renderings that will be shown
	 */
	public static void showPreview(UploadImage img) {
		JInternalFrame jif = new JInternalFrame("Preview", false, true, false, false);

		JPanel p = new JPanel();
		ImageCanvas c = new ImageCanvas(img.getImage());

		p.add(c);
		jif.setContentPane(p);
		jif.pack();
        jif.setVisible(true);

		desktop.add(jif);
		jif.requestFocus();
		jif.grabFocus();

		try {
			jif.setSelected(true);
		} catch (Exception e) {
            e.printStackTrace();
        }
	}

    public static UploadThread getUploadThread() {
        UploadThread d = uploadQueue.getNewThread();
        d.setDove(dove);
        return d;
    }
    
    /**
     * Login method, check if the user is known within the storage server
     * with the supplied password.
     */
    public static String login(String username, String password) {
        dove = new DoveWrapper();
		String result = dove.validate(username, password);
		if (result.equals("")) {
            loginFrame.dispose();
            loggedin = true;
            statusBar.setText("Status: Logged in as user '" + username + "'");
            mainFrame.getJMenuBar().getMenu(0).getItem(0).setEnabled(false);
            mainFrame.getJMenuBar().getMenu(0).getItem(1).setEnabled(true);
            mainFrame.getJMenuBar().getMenu(0).getItem(3).setEnabled(true);
            currentuser = username;

           JOptionPane.showMessageDialog(desktop,
               "Successfully logged in!",
                "Login",
                JOptionPane.INFORMATION_MESSAGE);
            
            StepsFrame stepsFrame;
            
            stepsFrame = new StepsFrame();
            stepsFrame.setVisible(true);
            desktop.add(stepsFrame);
            try {
                stepsFrame.setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
        return result;
    }
    
    public static String getCurrentUser() {
        return currentuser;
    }
    
    public static void setStatus(String newStatus) {
        statusBar.setText(newStatus);
    }
    
    /**
     * Search method, dispatch the search to Dove and return the result
     */
    public static Vector search(String criteria) {
    	return dove.search(criteria);
    }
    
    private static void tryExit() {
        if (uploadQueue.busy()) {
           JOptionPane.showMessageDialog(desktop,
               "You cannot quit when uploads are pending",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } else {
            System.exit(0);   
        }
    }
}
