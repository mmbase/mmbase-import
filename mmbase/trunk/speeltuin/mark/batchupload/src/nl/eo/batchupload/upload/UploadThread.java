package nl.eo.batchupload.upload;
import java.util.ArrayList;
import java.util.Hashtable;

import nl.eo.batchupload.connector.ConnectorInterface;
import nl.eo.batchupload.connector.DoveWrapper;
import nl.eo.batchupload.table.UploadTable;

/**
 * While there are images in the UploadQueue that are not uploaded yet, this thread will
 * upload them to MMBase using Dove. 
 * @author jverelst
 */
public class UploadThread extends Thread {
	private UploadTable uploadTable;
	private int tableIndex = 0;
	private ArrayList  imageQueue = new ArrayList();
	private int currentUpload = 0;
	private ConnectorInterface dove;

	/**
	 * Main method of this class. While there are still images in the image queue, they will
	 * be uploaded using dove. If all images are uploaded, the thread terminates.
	 */
    public void run() {
		while(true) {
			statusChanged();

			if (currentUpload >= imageQueue.size()) {
				return;
			} else {
                UploadImage i = (UploadImage)imageQueue.get(currentUpload);
                Hashtable object = new Hashtable();
                uploadTable.setValueAt("Uploading file ...", tableIndex, 3);

                object.put("title", i.getTitle());
                object.put("description", i.getDescription());
                object.put("handle", i.getBytes());
                object.put("_type", "images");
                int newnumber = dove.create(object);
                int relateto = i.getRelateTo();
                if (relateto != -1) {
                    uploadTable.setValueAt("Relating object ...", tableIndex, 3);
                    dove.relate(newnumber, relateto, "related");
                }
                
				currentUpload++; 
			}
		}
    }

	/**
	 * Public constructor
	 * Add this Thread to the given threadgroup. A reference to the UploadTable is given so that
	 * this thread may update the table when necessary
	 * @param tg Threadgroup to insert this thread into
	 * @param ut UploadTable that may be updated when changes occur
	 * @param name name of the upload thread
	 */
	public UploadThread(ThreadGroup tg, UploadTable ut, String name) {
		super(tg, null, name); 
		uploadTable = ut;
        tableIndex = uploadTable.addRow();
	}
	
	/**
	 * Set the DoveWrapper for this class to use
	 */
	public void setDove(ConnectorInterface dove) {
		this.dove = dove;
	}
	
	/**
	 * Add an image to the upload queue
	 * @param i Image to upload
	 */
	public void addImage(UploadImage i) {
		imageQueue.add(i);
	}
	
	/**
	 * Event that occurs when there is a change in the upload status. The UploadTable will
	 * be changed accordingly.
	 */
	private void statusChanged() {
		if (currentUpload < imageQueue.size()) {
			UploadImage ui = (UploadImage)imageQueue.get(currentUpload);
			uploadTable.setValueAt(ui.getUser(), tableIndex, 0);
			uploadTable.setValueAt(ui.getFilename(), tableIndex, 1);
			uploadTable.setValueAt(new Double((double)currentUpload / (double)imageQueue.size()), tableIndex, 2);
		} else {
			UploadImage ui = (UploadImage)imageQueue.get(0);
			uploadTable.setValueAt(ui.getUser(), tableIndex, 0);
			uploadTable.setValueAt("Completed", tableIndex, 1);
			uploadTable.setValueAt(new Double(1), tableIndex, 2);
            uploadTable.setValueAt("", tableIndex, 3);
		}			
	}			
}
