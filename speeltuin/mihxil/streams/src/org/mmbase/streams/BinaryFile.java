/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams;

import org.mmbase.bridge.*;
import org.mmbase.util.*;
import org.mmbase.datatypes.processors.*;
import java.util.*;
import java.io.*;
import org.mmbase.util.logging.*;
import org.mmbase.servlet.FileServlet;

import org.apache.commons.fileupload.FileItem;

/**
 * This class constains Setter and Getter method for 'binary' file fields. In such field you can set
 * a FileItem, and it is stored as a file, using the FileServlet to produce an URL. The (string)
 * field itself only contains a file name.
 *
 * @author Michiel Meeuwissen
 */

public class BinaryFile {

    private static final Logger log = Logging.getLoggerInstance(BinaryFile.class);

    public static class Setter implements Processor {

        private static final long serialVersionUID = 1L;

        public Object process(Node node, Field field, Object value) {
            ///SerializableInputStream is = Casting.toSerializableInputStream(value);
            StringBuilder buf = new StringBuilder();
            org.mmbase.storage.implementation.database.DatabaseStorageManager.appendDirectory(buf, node.getNumber(), "/");
            buf.append("/").append(node.getNumber()).append(".");
            if (value instanceof FileItem) {
                FileItem fi = (FileItem) value;
                buf.append(fi.getName());
                File f = new File(FileServlet.getDirectory(), buf.toString().replace("/", File.separator));
                try {
                    fi.write(f);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                buf.append(field.getName());
            }
            return buf.toString();
        }
    }

    public static class Getter implements Processor {
        private static final long serialVersionUID = 1L;

        public Object process(Node node, Field field, Object value) {
            return value;
        }
    }

}
