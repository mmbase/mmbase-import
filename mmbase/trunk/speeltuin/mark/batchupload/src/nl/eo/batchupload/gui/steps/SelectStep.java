package nl.eo.batchupload.gui.steps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;

import nl.eo.batchupload.gui.BatchUploadMain;
import nl.eo.batchupload.gui.StepsFrame;
import nl.eo.batchupload.gui.cellrenderer.ImageCellRenderer;
import nl.eo.batchupload.gui.cellrenderer.TextAreaCellEditor;
import nl.eo.batchupload.gui.cellrenderer.TextAreaCellRenderer;
import nl.eo.batchupload.preview.ImageRenderThread;
import nl.eo.batchupload.preview.ImageThumbnail;
import nl.eo.batchupload.table.ImageTable;
import nl.eo.batchupload.upload.UploadImage;
import nl.eo.batchupload.utils.ImageFileFilter;

/**
 * Panel that implements the 'Select images for upload' Step.
 * All functionality for this step (listing the images, image manipulation, etc.) is implemented in
 * either this class or one of it's inner classes.
 * 
 * @author Johannes Verelst
 */
public class SelectStep extends StepFrame implements PropertyChangeListener {
	ImageTable imageTable;
//	StepsFrame parent;
	JTable tabel;
	
	final String CMD_ROTATE_90 = "Rotate 90" + (char)186 + " clockwise";
	final String CMD_ROTATE_180 = "Rotate 180" + (char)186 + " clockwise";
	final String CMD_ROTATE_270 = "Rotate 270" + (char)186 + " clockwise";
	final String CMD_SHOWRESULT = "Show result";
	private int thumbWidth = 100;
	private int thumbHeight = 100;
	private String titleTemplate = "";
	private String descriptionTemplate = "";

    private ImageRenderThread renderThread = new ImageRenderThread();

	
	/**
	 * Public constructor.
	 * Initialize all the components that exist within this panel.
	 * @param frame The @link{StepsFrame} object that is our parent.
	 */	
	public SelectStep(StepsFrame frame) {
        super(frame);
        imageTable = new ImageTable();
//		this.parent = frame;
		
        tabel = new JTable(imageTable);
        
		tabel.getColumnModel().getColumn(1).setCellRenderer(new ImageCellRenderer());
		tabel.getColumnModel().getColumn(2).setCellRenderer(new TextAreaCellRenderer());
		tabel.getColumnModel().getColumn(3).setCellRenderer(new TextAreaCellRenderer());
		
		tabel.getColumnModel().getColumn(2).setCellEditor(new TextAreaCellEditor());
		tabel.getColumnModel().getColumn(3).setCellEditor(new TextAreaCellEditor());
		
        tabel.getColumnModel().getColumn(0).setMaxWidth(10);
        tabel.getColumnModel().getColumn(2).setMinWidth(120);
        tabel.getColumnModel().getColumn(3).setMinWidth(120);

        tabel.getColumnModel().getColumn(1).setMaxWidth(thumbWidth);
        tabel.getColumnModel().getColumn(1).setMinWidth(thumbWidth);
        tabel.setRowHeight(thumbHeight);
        
        tabel.setColumnSelectionAllowed(false);
		tabel.getTableHeader().setReorderingAllowed(false);

		/* Build the Popup Menu */
		JPopupMenu popup = new JPopupMenu();
		PopupListener popupListener = new PopupListener(popup, tabel);		

	    JMenuItem menuItem = new JMenuItem(CMD_ROTATE_90);
	    popup.add(menuItem);
		menuItem.addActionListener(popupListener.getMouseActionListener());

	    menuItem = new JMenuItem(CMD_ROTATE_180);
	    popup.add(menuItem);
		menuItem.addActionListener(popupListener.getMouseActionListener());

	    menuItem = new JMenuItem(CMD_ROTATE_270);
	    popup.add(menuItem);
		menuItem.addActionListener(popupListener.getMouseActionListener());

	    popup.add(new JPopupMenu.Separator());

	    menuItem = new JMenuItem(CMD_SHOWRESULT);
	    popup.add(menuItem);
		menuItem.addActionListener(popupListener.getMouseActionListener());

		tabel.addMouseListener((MouseListener)popupListener);

        JScrollPane p = new JScrollPane(tabel);

		setLayout(new BorderLayout());
		add(p, BorderLayout.CENTER); 

        add(new NavigationPane(this), BorderLayout.SOUTH);

        p.setPreferredSize(new Dimension(570,280));
		super.addPropertyChangeListener(this);
	}

    public Vector getSelectedImages() {
        Vector result = new Vector();
        ArrayList al = imageTable.getAllData();
        for (int i=0; i < al.size(); i++) {
            UploadImage ui = (UploadImage)al.get(i);
            if (ui.isEnabled()) {
                result.add(ui);
            }
        }
        return result;
    }

	public int getNrImages() {
        return getSelectedImages().size();
	}

	class PopupListener extends MouseAdapter {
		JPopupMenu mPopup;
		JTable mTable;
		int row;
		int column;
		public PopupListener(JPopupMenu popup, JTable table) {
			mPopup = popup;
			mTable = table;
		}
	    public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }
	
	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }
	
		public ActionListener getMouseActionListener() {
			return new ActionListener() {
        		public void actionPerformed(ActionEvent arg0) {
        		
        			UploadImage img = (UploadImage)mTable.getValueAt(row, -1);
        			String actionCommand = arg0.getActionCommand();
                	if (actionCommand.equals(CMD_ROTATE_90)) {
                		img.doRotate(90.0);
                	} else if (actionCommand.equals(CMD_ROTATE_180)) {
                		img.doRotate(180.0);
                	} else if (actionCommand.equals(CMD_ROTATE_270)) {
                		img.doRotate(270.0);
                	} else if (actionCommand.equals(CMD_SHOWRESULT)) {
                		BatchUploadMain.showPreview(img);
                   	} else {

                   	}
               }
            };	
		}
	
	    private void maybeShowPopup(MouseEvent e) {
	        if (e.isPopupTrigger()) {
	        	Point p = new Point(e.getX(), e.getY());
	   	        row = mTable.rowAtPoint(p);
	    	    column = mTable.columnAtPoint(p);
	         	if (row >= 0 && column == 1)  {
		            mPopup.show(e.getComponent(), e.getX(), e.getY());
	         	}
	        }
	    }	
	}

	/**
	 * Event that occurs when a property is changed. Usually, this will be the 'imagepath' property.
	 * The moment that this property changes, a new list of images will be created.
	 */
    public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getPropertyName().equals("imagepath")) {
            if (renderThread != null) {
                renderThread.stopRendering();
                renderThread = null;
            }
            if (!arg0.getNewValue().toString().equals("")) {
    			File f = new File(arg0.getNewValue().toString());
    			String[] files = f.list(new ImageFileFilter());
                imageTable.removeAll();
                renderThread = new ImageRenderThread();
    		
    			for (int i=0; i< files.length; i++){
    				File d = new File(files[i]);

    				UploadImage img = new UploadImage(BatchUploadMain.getCurrentUser(), f.getPath(), d.getName(), renderThread);
    				int newrow = imageTable.addRow(img);
    				imageTable.setValueAt(titleTemplate, newrow, 2);
    				imageTable.setValueAt(descriptionTemplate, newrow, 3);				
    			}
                renderThread.start();
            }
		}

		if (arg0.getPropertyName().equals("titletemplate")) {
			titleTemplate = arg0.getNewValue().toString();
			for (int i=0; i< imageTable.getRowCount(); i++) {
				imageTable.setValueAt(titleTemplate, i, 2);
			}
		}

		if (arg0.getPropertyName().equals("descriptiontemplate")) {
			descriptionTemplate = arg0.getNewValue().toString();
			for (int i=0; i< imageTable.getRowCount(); i++) {
				imageTable.setValueAt(descriptionTemplate, i, 3);
			}
		}		
    }
}