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

import org.apache.commons.fileupload.disk.DiskFileItem;

import java.io.File;
import java.io.OutputStream;
import java.io.IOException;

/**
 *
 * @author Pierre-Alexandre Losson
 * @author Michiel Meeuwissen (adapted for MMBase)
 * @version $Id$
 */
public class MonitoredDiskFileItem extends DiskFileItem {
    private static final long serialVersionUID = 0L;
    private MonitoredOutputStream mos = null;
    private final OutputStreamListener listener;

    public MonitoredDiskFileItem(String fieldName, String contentType, boolean isFormField, String fileName, int sizeThreshold, File repository, OutputStreamListener listener) {
        super(fieldName, contentType, isFormField, fileName, sizeThreshold, repository);
        this.listener = listener;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (mos == null) {
            mos = new MonitoredOutputStream(super.getOutputStream(), listener);
        }
        return mos;
    }
}
