/* Licence:
*   Use this however/wherever you like, just don't blame me if it breaks anything.
*
* Credit:
*   If you're nice, you'll leave this bit:
*
*   Class by Pierre-Alexandre Losson -- http://www.telio.be/blog
*   email : plosson@users.sourceforge.net
*/

package org.mmbase.datatypes.handlers.html.upload;
/**
 *
 * @author Pierre-Alexandre Losson
 * @author Michiel Meeuwissen (adapted for MMBase)
 * @version $Id$
 */

public class UploadInfo {
    private long totalSize;
    private long bytesRead;
    private long elapsedTime;
    private String status;
    private int fileIndex;

    public UploadInfo(int fileIndex, long totalSize, long bytesRead, long elapsedTime, String status) {
        this.fileIndex = fileIndex;
        this.totalSize = totalSize;
        this.bytesRead = bytesRead;
        this.elapsedTime = elapsedTime;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public void setBytesRead(long bytesRead) {
        this.bytesRead = bytesRead;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public boolean isInProgress() {
        return "progress".equals(status) || "start".equals(status);
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }
    public float getFraction() {
        return isInProgress() ? ((float) bytesRead  / totalSize) : 1.0f;
    }
    public int getPercentage() {
        return isInProgress() ? (int) (bytesRead  * 100 / totalSize) : 100;
    }
    public String toString() {
        return status + ":" + fileIndex + ":" + bytesRead + "/" + totalSize + " (" + (getPercentage()) + "%,  " + elapsedTime + " ms)";
    }
}
