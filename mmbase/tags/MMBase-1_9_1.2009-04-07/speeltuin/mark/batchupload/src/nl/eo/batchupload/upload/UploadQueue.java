package nl.eo.batchupload.upload;
import nl.eo.batchupload.table.UploadTable;

/**
 * Implements the Upload Queue. This exists of UploadThreads, every UploadThread has a
 * list of images it is uploading. There can be multiple UploadThreads busy at the same time, 
 * but one UploadThread is always uploading it's images sequencially.
 *
 * @author Johannes Verelst
 */
public class UploadQueue {
	private UploadTable uploadTable;
	private ThreadGroup tg = new ThreadGroup("UploadThreads");

	/**
	 * Public constructor
	 * @param tabl The UploadTable where this UploadQueue is connected to
	 */
	public UploadQueue(UploadTable tabl) {
		this.uploadTable = tabl;		
	}

	/**
	 * Return a new thread that is registered as an upload thread in the upload queue
	 * @return UploadThread
	 */
	public UploadThread getNewThread() {
		UploadThread upl = new UploadThread(tg, uploadTable, "UploadThread-" + tg.activeCount());
		return upl;
	}
    
    /**
     * Return whether or not there are active threads busy
     */
    public boolean busy() {
        for (int i=0; i < uploadTable.getRowCount(); i++) {
            double status = ((Double)uploadTable.getValueAt(i, 2)).doubleValue();
            System.out.println(status);
            if (status != 1.0) {
                return true;
            }
        }
        return false;
    }
}
