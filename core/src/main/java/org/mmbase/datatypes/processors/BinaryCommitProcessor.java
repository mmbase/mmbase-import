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
 * @version $Id$
 * @since MMBase-1.9.1
 */

public class BinaryCommitProcessor implements CommitProcessor {

    private static final Logger log = Logging.getLoggerInstance(BinaryCommitProcessor.class);

    private static final long serialVersionUID = 1L;

    private String filenameField = "filename";
    private String filesizeField = "filesize";
    private String contenttypeField = "mimetype";
    private boolean itypeField = false;

    public void setFilenameField(String fn) {
        filenameField = fn;
    }
    public void setFilesizeField(String fs) {
        filesizeField = fs;
    }
    public void setContenttypeField(String fs) {
        contenttypeField = fs;
    }

    public void setItype(boolean i) {
        itypeField = i;
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
    private String getContentType(Object o) {
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

            if (node.getNodeManager().hasField(filesizeField) &&
                (! node.isChanged(filesizeField) || node.isNull(filesizeField))) {
                long size = node.getSize(field.getName());
                log.debug("Setting filesize to " + size);
                node.setValue(filesizeField, size);
            } else {
                log.debug("Skipping filesize");
            }

            Object value = node.getObjectValue(field.getName());

            if (node.getNodeManager().hasField(filenameField) &&
                (! node.isChanged(filenameField) || node.isNull(filenameField))) {
                String fn = getFileName(value);
                if (fn != null) {
                    node.setValue(filenameField, fn);
                }
            }
            if (node.getNodeManager().hasField(contenttypeField) &&
                (! node.isChanged(contenttypeField) || node.isNull(contenttypeField))) {

                String fn = getContentType(value);
                if (fn != null) {
                    if (itypeField) {
                        int slash = fn.indexOf('/');
                        if (slash > -1)  {
                            fn = fn.substring(slash + 1);
                        }
                    }
                    node.setValue(contenttypeField, fn);
                }
            }
        }

    }

}


