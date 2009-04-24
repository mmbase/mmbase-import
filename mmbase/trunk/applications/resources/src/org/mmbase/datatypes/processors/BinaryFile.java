/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.datatypes.processors.*;
import java.util.*;
import java.io.*;
import org.mmbase.util.logging.*;
import org.mmbase.servlet.FileServlet;


/**
 * This class constains Setter and Getter method for 'binary' file fields. In such field you can set
 * a FileItem, and it is stored as a file, using the FileServlet to produce an URL. The (string)
 * field itself only contains a file name.
 *
 * The file could (and currently is supposed to)  be served with {@link org.mmbase.servlet.FileServlet}.
 *
 * @author Michiel Meeuwissen
 */

public class BinaryFile {

    private static final Logger log = Logging.getLoggerInstance(BinaryFile.class);


    private static File getDirectory() {
        File servletDir = FileServlet.getDirectory();
        if (servletDir == null) throw new IllegalArgumentException("No FileServlet directory found (FileServlet not (yet) active)?");
        return servletDir;
    }


    private static File getFile(final Node node, final Field field, String fileName) {
        StringBuilder buf = new StringBuilder();
        org.mmbase.storage.implementation.database.DatabaseStorageManager.appendDirectory(buf, node.getNumber(), "/");
        buf.append("/").append(node.getNumber()).append(".");
        buf.append(fileName);
        return new File(getDirectory(), buf.toString().replace("/", File.separator));
    }


    public static class Setter implements Processor {

        private static final long serialVersionUID = 1L;

        public Object process(final Node node, final Field field, final Object value) {
            SerializableInputStream is = Casting.toSerializableInputStream(value);
            String name = is.getName();
            if (name != null) {
                String existing = (String) node.getValue(field.getName());
                if (existing != null) {
                    File ef = new File(getDirectory(), existing);
                    if (ef.exists()) {
                        log.debug("Removing existing field " + ef);
                        ef.delete();
                    } else {
                        log.warn("Could not fined " + ef + " so could not delete it");
                    }
                }
                File f = getFile(node, field, name);

                if (log.isDebugEnabled()) {
                    log.debug("" + value + " -> " + is + " -> " + f + " " + Logging.applicationStacktrace());
                }
                is.moveTo(f);
                return f.getName();
            } else {
                log.debug("No name given, ignoring this processor (not an upload)");
                return value;
            }
        }
    }

    public static class Getter implements Processor {
        private static final long serialVersionUID = 1L;

        public Object process(Node node, Field field, Object value) {
            return value;
        }
    }

}
