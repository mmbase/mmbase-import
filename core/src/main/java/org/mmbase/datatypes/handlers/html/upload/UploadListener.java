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
import javax.servlet.http.HttpServletRequest;
/**
 *
 * @author Pierre-Alexandre Losson
 * @author Michiel Meeuwissen (adapted for MMBase)
 * @version $Id$
 */

public class UploadListener implements OutputStreamListener {
    private final HttpServletRequest request;
    private final long delay;
    private final long startTime;
    private final int totalToRead;
    private int totalBytesRead = 0;
    private int totalFiles = -1;

    public UploadListener(HttpServletRequest request) {
        this(request, 0);
    }
    public UploadListener(HttpServletRequest request, long debugDelay) {
        this.request = request;
        this.delay = debugDelay;
        this.totalToRead = request.getContentLength();
        this.startTime = System.currentTimeMillis();
    }

    public void start() {
        totalFiles ++;
        updateUploadInfo("start");
    }

    public void bytesRead(int bytesRead) {
        totalBytesRead = totalBytesRead + bytesRead;
        updateUploadInfo("progress");

        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            //
        }
    }

    public void error(String message) {
        updateUploadInfo("error");
    }

    public void done() {
        updateUploadInfo("done");
    }

    private long getDelta() {
        return System.currentTimeMillis() - startTime;
    }

    private void updateUploadInfo(String status) {
        request.getSession().setAttribute("uploadInfo", new UploadInfo(totalFiles, totalToRead, totalBytesRead, getDelta(), status));
    }

}
