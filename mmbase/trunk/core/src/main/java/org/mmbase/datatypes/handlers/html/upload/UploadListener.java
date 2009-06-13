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

    public static final String KEY = "org.mmbase.uploadInfo";
    private final HttpServletRequest request;
    private final UploadInfo uploadInfo;
    private final long delay;
    private final long startTime;

    public UploadListener(HttpServletRequest request) {
        this(request, 0);
    }
    public UploadListener(HttpServletRequest request, long debugDelay) {
        this.request = request;
        this.delay = debugDelay;
        this.startTime = System.currentTimeMillis();
        this.uploadInfo = new UploadInfo(request.getContentLength());
    }

    public void start() {
        uploadInfo.fileIndex++;
        updateUploadInfo("start");
    }

    public void bytesRead(int bytesRead) {
        uploadInfo.bytesRead += bytesRead;
        updateUploadInfo("progress");

        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                //
            }
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
        uploadInfo.setStatus(status);
        request.getSession().setAttribute(KEY, uploadInfo);
    }

}
