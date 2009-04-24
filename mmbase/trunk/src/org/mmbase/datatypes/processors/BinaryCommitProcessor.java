/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.apache.commons.fileupload.FileItem;
import org.mmbase.util.logging.*;
import org.mmbase.util.SerializableInputStream;

/**
 * Used as 'commitprocessor' on the 'binaries'. This automaticly fills associated 'filename' and
 * 'filesize' fields.
 *
 * @author Michiel Meeuwissen
 * @version $Id: BinaryCommitProcessor.java,v 1.1 2009-04-24 14:30:31 michiel Exp $
 * @since MMBase-1.9.1
 */

public class BinaryCommitProcessor implements CommitProcessor {

    private static final Logger log = Logging.getLoggerInstance(BinaryCommitProcessor.class);

    private static final long serialVersionUID = 1L;

    private String filenameField = "filename";
    private String filesizeField = "filesize";
    private String mimetypeField = "mimetype";

    public void setFilenameField(String fn) {
        filenameField = fn;
    }
    public void setFilesizeField(String fs) {
        filesizeField = fs;
    }
    public void setMimetypeField(String fs) {
        mimetypeField = fs;
    }

    private String getFileName(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof SerializableInputStream) {
            return ((SerializableInputStream)o).getName();
        } else if (o instanceof FileItem) {
            return ((FileItem)o).getName();
        }
        return null;

    }
    private String getMimeType(Object o) {
        if (o instanceof SerializableInputStream) {
            return ((SerializableInputStream)o).getContentType();
        } else if (o instanceof FileItem) {
            return ((FileItem)o).getContentType();
        } else {
            return "application/octet-stream";
        }
    }

    public void commit(Node node, Field field) {
        if (log.isDebugEnabled()) {
            log.debug("Committing" + node);
        }
        if (node.isChanged(field.getName())) {
            Object value = node.getObjectValue(field.getName());
            if (node.getNodeManager().hasField(filesizeField) &&
                (! node.isChanged(filesizeField) || node.isNull(filesizeField))) {
                log.debug("Setting filesize");
                node.setValue(filesizeField, node.getSize(field.getName()));
            } else {
                log.debug("Skipping filesize");
            }

            if (node.getNodeManager().hasField(filenameField) &&
                (! node.isChanged(filenameField) || node.isNull(filenameField))) {
                String fn = getFileName(value);
                if (fn != null) {
                    node.setValue(filenameField, fn);
                }
            }
            if (node.getNodeManager().hasField(mimetypeField) &&
                (! node.isChanged(mimetypeField) || node.isNull(mimetypeField))) {

                String fn = getMimeType(value);
                if (fn != null) {
                    node.setValue(mimetypeField, fn);
                }
            }
        }

    }

}


