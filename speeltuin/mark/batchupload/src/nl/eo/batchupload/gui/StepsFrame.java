package nl.eo.batchupload.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.*;

import nl.eo.batchupload.connector.DoveWrapper;
import nl.eo.batchupload.gui.steps.*;
import nl.eo.batchupload.upload.UploadImage;
import nl.eo.batchupload.upload.UploadThread;

/**
 * This class contains the GUI information for the frame that contains the several steps. Both
 * GUI and functionality of these steps are located in the nl.eo.batchupload.gui.steps package.
 */
public class StepsFrame extends JInternalFrame implements ActionListener {
    JTabbedPane tabbedPane = new JTabbedPane();
	String imagepath = "";
	String titleTemplate = "";
	String descriptionTemplate = "";
	InitializeStep iniStep = new InitializeStep(this);
	InfoStep infStep = new InfoStep(this);
	SelectStep selStep = new SelectStep(this);
	RelateStep relStep = new RelateStep(this);
    ImportStep impStep = new ImportStep(this);
        	       
	Hashtable properties = new Hashtable();


	/**
	 * Public constructor, create the tabbed frame and add all the panels for the different step
	 */
    public StepsFrame() {
        super("Upload", true, false, false, true);
        setLocation(10,40);

        setMinimumSize(new Dimension(600,420));
        setSize(getMinimumSize());
        
        tabbedPane.addTab("Kies directory", null, iniStep, "Selecteer de directory waar de bestanden in staan die ge-upload moeten worden.");
        tabbedPane.setSelectedIndex(0);
        tabbedPane.setEnabledAt(0, true);

        tabbedPane.addTab("Geef informatie", null, infStep, "Vul beschrijvende informatie voor de plaatjes in.");
        tabbedPane.setSelectedIndex(0);
        tabbedPane.setEnabledAt(1, false);

        tabbedPane.addTab("Bewerk & Selecteer", null, selStep, "Selecteer de plaatjes die ge-upload moeten worden, en voer eventueel wat kleine bewerkingen uit");
        tabbedPane.setEnabledAt(2, false);

        tabbedPane.addTab("Koppeling", null, relStep, "Koppelen van de plaatjes aan een object dat gezocht wordt.");
        tabbedPane.setEnabledAt(3, false);

        tabbedPane.addTab("Import", null, impStep, "Import in MMBase");
        tabbedPane.setEnabledAt(4, false);

        setContentPane(tabbedPane);
        
    }

    protected Component makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

	public void setImagepath(String path) {
		imagepath = path;	
	}
	
	public void setTitleTemplate(String t) {
		titleTemplate = t;
	}
	
	public void setDescriptionTemplate(String t) {
		descriptionTemplate = t;
	}
	
	public String getImagepath() {
		return imagepath;
	}

	public int getNumberOfImages() {
		return selStep.getNrImages();
	}

	public void setProperty(String propertyName, Object value) {
		properties.put(propertyName, value);
	}

	public Object getProperty(String propertyName) {
		return properties.get(propertyName);
	}
	
	public void actionPerformed(ActionEvent e) {
        int currentIndex = tabbedPane.getSelectedIndex();
		if ("Next".equals(e.getActionCommand())) {
            if (currentIndex == 4) {
                UploadThread ut = BatchUploadMain.getUploadThread();
                Vector images = selStep.getSelectedImages();
                int relateto = -1;
                if ("new".equals(getClientProperty("relateaction"))) {
                    Hashtable newobject = new Hashtable();
                    newobject.put("title", getClientProperty("relate-title"));
                    newobject.put("subtitle", getClientProperty("relate-subtitle"));
                    newobject.put("intro", getClientProperty("relate-intro"));
                    newobject.put("body", getClientProperty("relate-body"));
                    //newobject.put("type", "" + getClientProperty("relate-type"));
                    newobject.put("_type", "imagegallery");
                                       
                    DoveWrapper d = BatchUploadMain.dove;
                    relateto = d.create(newobject);
                } else if ("existing".equals(getClientProperty("relateaction"))) {
                    relateto = Integer.parseInt("" + getClientProperty("relate-objectid"));
                } else {
                    // We do not relate to something else
                }

                for (int i=0; i < images.size(); i++) {
                    UploadImage img = (UploadImage)images.get(i);
                    img.setRelateTo(relateto);
                    ut.addImage(img);
                }
                
                ut.start();
                ((JPanel)tabbedPane.getComponentAt(2)).putClientProperty("imagepath", "");
                this.dispose();
            } else {
    			tabbedPane.setEnabledAt(currentIndex, false);
    			tabbedPane.setEnabledAt(currentIndex + 1, true);
    			tabbedPane.setSelectedIndex(currentIndex + 1);
                tabbedPane.repaint();
    			if (currentIndex == 0) {
    				((JPanel)tabbedPane.getComponentAt(2)).putClientProperty("imagepath", imagepath);
    			}
    			if (currentIndex == 1) {
    				((JPanel)tabbedPane.getComponentAt(2)).putClientProperty("titletemplate", titleTemplate);
    				((JPanel)tabbedPane.getComponentAt(2)).putClientProperty("descriptiontemplate", descriptionTemplate);
    			}
            }
		} else if ("Back".equals(e.getActionCommand())) {
            if (currentIndex > 0) {
                tabbedPane.setEnabledAt(currentIndex, false);
                tabbedPane.setEnabledAt(currentIndex - 1, true);
                tabbedPane.setSelectedIndex(currentIndex - 1);
            }
        }
	}
}
