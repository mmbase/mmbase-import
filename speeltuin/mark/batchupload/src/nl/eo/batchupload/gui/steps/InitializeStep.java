package nl.eo.batchupload.gui.steps;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import nl.eo.batchupload.gui.StepsFrame;
import nl.eo.batchupload.preview.ImageRenderThread;
import nl.eo.batchupload.preview.ImageThumbnail;
import nl.eo.batchupload.utils.ImageFileFilter;

/**
 * Panel that implements the initialization step
 * All functionality for this step (listing the images, image manipulation, etc.) is implemented in
 * either this class or one of it's inner classes.
 * 
 * @author Johannes Verelst

 */
public class InitializeStep extends StepFrame {
	final JList list = new JList();
    final JFileChooser chooser = new JFileChooser();
    JButton nextButton;
    ImageRenderThread rt = new  ImageRenderThread ();
	final ImageThumbnail thumb = new ImageThumbnail(100, 100, rt);
	
//	final StepsFrame parent;
	
	public InitializeStep(StepsFrame parent) {
		super(parent, false);
//		this.parent = parent;
		setUpChooser();
		JScrollPane scrollList = new JScrollPane(list);
		
		JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollList, thumb);
	    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chooser, splitPane2);

        splitPane2.setOneTouchExpandable(false);
        splitPane.setOneTouchExpandable(false);
        
		nextButton = new JButton ("Next");
		nextButton.addActionListener(this);
		nextButton.setActionCommand("Next");

        rt.start();

		/* Set up the list */
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent arg0) {
                File path = chooser.getSelectedFile();
                if (path == null) {
                    path = chooser.getCurrentDirectory();
                }
                if (list.getSelectedValue() == null) {
                    thumb.clearImage();
                    thumb.repaint();
                } else {
    				String file = path.toString() + File.separator + list.getSelectedValue().toString();
    				thumb.loadImage(file);
                    thumb.repaint();
                }
            }
        });

		thumb.setMaximumSize(new Dimension(100,100));
		thumb.setMinimumSize(new Dimension(50,50));

		/* Add the two parts to the splitplan */
		splitPane.setDividerLocation(300);

        /* Fix all components to their current size */
        splitPane2.setMaximumSize(splitPane2.getSize());
        splitPane.setMaximumSize(splitPane.getSize());
        chooser.setMaximumSize(chooser.getSize());
        scrollList.setMaximumSize(scrollList.getSize());
        thumb.setMaximumSize(thumb.getSize());

        splitPane2.setMinimumSize(splitPane2.getSize());
        splitPane.setMinimumSize(splitPane.getSize());
        chooser.setMinimumSize(chooser.getSize());
        scrollList.setMinimumSize(scrollList.getSize());
        thumb.setMinimumSize(thumb.getSize());

		setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);

        add(navigationPane, BorderLayout.SOUTH);
	}

	private void setUpChooser() {
		/* Set up the filechooser */
        FileFilter f = new FileFilter() {
            /**
             * @see javax.swing.filechooser.FileFilter#getDescription()
             */
            public String getDescription() {
                return "Image Files (*.jpg, *.gif, *.png)";
            }
            /**
             * @see javax.swing.filechooser.FileFilter#accept(File)
             */
            public boolean accept(File arg0) {
                return true;
            }
        };
        chooser.setFileFilter(f);
                                                                                                                                                                                                                                                                                                                                                               	    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    chooser.setControlButtonsAreShown(false);
	    chooser.setMultiSelectionEnabled(false);
		chooser.setMinimumSize(chooser.getPreferredSize());

		chooser.addPropertyChangeListener(
			new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent arg0) {
					if (arg0.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
					   || arg0.getPropertyName().equals(JFileChooser.DIRECTORY_CHANGED_PROPERTY)
					) {
						String newPath = arg0.getNewValue().toString();
						parent.setImagepath(newPath);
						File f = new File(newPath);
	
						String[] files = f.list(new ImageFileFilter());
						Vector ve = new Vector();
						for (int i=0; i< files.length; i++){
							ve.add(files[i]);
						}
						list.setListData(ve);
					}
                }
            }
		);
		chooser.putClientProperty(JFileChooser.DIRECTORY_CHANGED_PROPERTY, chooser.getCurrentDirectory());
		
	}
}
