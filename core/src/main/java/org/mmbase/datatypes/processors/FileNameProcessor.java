/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.apache.commons.fileupload.FileItem;

/**
 * Some browers provide directory information (IE on Windows), and {@link
 * org.apache.commons.fileupload.FileItem#getName()} still includes that. This processors removes
 * it.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public class FileNameProcessor implements Processor {

    private static final long serialVersionUID = 1L;

    public final Object process(Node node, Field field, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof FileItem) {
            value = ((FileItem) value).getName();
        }
        String fileName = Casting.toString(value);
        int pos = fileName.lastIndexOf("\\");
        if (pos > 0) {
            fileName = fileName.substring(pos + 1);
        }
        pos = fileName.lastIndexOf("/");
        if (pos > 0) {
            fileName = fileName.substring(pos + 1);
        }
        return fileName;
    }

    public String toString() {
        return "FILENAME";
    }
}


