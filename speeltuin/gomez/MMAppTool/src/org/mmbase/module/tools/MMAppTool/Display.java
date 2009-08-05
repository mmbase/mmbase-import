/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module.tools.MMAppTool;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import org.mmbase.util.*;

/**
 * Class Display
 *
 * @application MMAppTool
 * @javadoc
 */
public class Display extends Frame implements WindowListener,ActionListener {

    private MenuBar bar;
    private AppCanvas can;
    private MMAppTool parent;
    private String filename;
    private FileDialog fileDialog;

    public Display(MMAppTool parent) {
        init(parent);
    }

    public Display(MMAppTool parent,String filename) {
        init(parent);
        this.filename=filename;
        XMLApplicationReader app=new XMLApplicationReader(filename);
        if (can!=null) {
            can.setApplication(app);
        }

        XMLAppToolReader con=new XMLAppToolReader(filename.substring(0,filename.length()-4)+"/tools/mmapptoolconfig.xml");
        if (con.ok) {
            can.setBackGroundColor(con.getColor("backgroundcolor"));
            can.setObjectColor(con.getColor("objectcolor"));
            can.setTextColor(con.getColor("textcolor"));
            can.setLineColor(con.getColor("linecolor"));
            can.setActiveColor(con.getColor("activecolor"));


            for (Enumeration e=can.getBuilderOvals().elements();e.hasMoreElements();) {
                BuilderOval b=(BuilderOval)e.nextElement();
                String name=b.getName();
                int x=con.getX(name);
                int y=con.getY(name);
                if (x!=-1 && y!=-1) {
                    b.setX(x);
                    b.setY(y);
                }
            }
        }
    }

    private void init(MMAppTool parent) {
        this.parent=parent;
        setSize(640,480);
        bar=createMenus();
        setMenuBar(bar);
        setTitle("MMAppTool - Display");
        can=new AppCanvas(this);
        ScrollPane sp = new ScrollPane();
        add(sp);
        sp.add(can);
        addWindowListener(this);
        fileDialog=new FileDialog(this,"Open Application xml",FileDialog.LOAD);
    }

    public void windowDeiconified(WindowEvent event) {
    }

    public void windowIconified(WindowEvent event) {
    }

    public void windowDeactivated(WindowEvent event) {
    }

    public void windowActivated(WindowEvent event) {
    }

    public void windowOpened(WindowEvent event) {
    }

    public void windowClosed(WindowEvent event) {
        System.out.println("Window Closed");
    }

    public void windowClosing(WindowEvent event) {
        System.out.println("Window Closing");
        parent.doExit();
    }

    private MenuBar	createMenus() {
        MenuBar bar=new MenuBar();

        // create filemenu
        Menu m = new Menu("File");
        m.add(new MenuItem("Open",new MenuShortcut('O')));
        m.add(new MenuItem("Save",new MenuShortcut('S')));
        m.add(new MenuItem("Exit",new MenuShortcut('E')));

        m.addActionListener(this);
        bar.add(m);

        return(bar);
    }

    public void actionPerformed(ActionEvent evt) {
        String cmd = evt.getActionCommand();
        if (cmd.equals("Exit")) {
            parent.doExit();
        } else if (cmd.equals("Open")) {
            doOpen();
        } else if (cmd.equals("Save")) {
            doSave();
        } else {
            System.out.println("Unknown="+cmd);
        }
    }

    private void doSave() {
        if (can!=null) {
            if (filename!=null) {
                // create the dir for the Data & resource files
                File file = new File(filename.substring(0,filename.length()-4)+"/tools/");
                try {
                    file.mkdirs();
                } catch(Exception e) {
                    System.out.println("Can't create dir : "+filename.substring(0,filename.length()-4)+"/tools/");
                }


                XMLAppToolWriter.writeXMLFile(can,filename.substring(0,filename.length()-4)+"/tools/mmapptoolconfig.xml");
                System.out.println("Save = "+filename.substring(0,filename.length()-4)+"/tools/mmapptoolconfig.xml");
            }
        }
    }

    private void doOpen() {
        fileDialog.show();
        filename=fileDialog.getDirectory()+fileDialog.getFile();

        XMLApplicationReader app=new XMLApplicationReader(filename);
        if (can!=null) {
            can.setApplication(app);
        }

        XMLAppToolReader con=new XMLAppToolReader(filename.substring(0,filename.length()-4)+"/tools/mmapptoolconfig.xml");
        if (con.ok) {
            can.setBackGroundColor(con.getColor("backgroundcolor"));
            can.setObjectColor(con.getColor("objectcolor"));
            can.setTextColor(con.getColor("textcolor"));
            can.setLineColor(con.getColor("linecolor"));
            can.setActiveColor(con.getColor("activecolor"));


            for (Enumeration e=can.getBuilderOvals().elements();e.hasMoreElements();) {
                BuilderOval b=(BuilderOval)e.nextElement();
                String name=b.getName();
                int x=con.getX(name);
                int y=con.getY(name);
                if (x!=-1 && y!=-1) {
                    b.setX(x);
                    b.setY(y);
                }
            }
        }
        can.repaint();
    }

    public String getFilename() {
        return(filename);
    }
}
