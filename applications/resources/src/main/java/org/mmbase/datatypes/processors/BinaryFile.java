/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
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
        return new File(getDirectory(), getFileName(node, field, fileName).replace("/", File.separator));
    }

    public static String getFileName(final Node node, final Field field, String fileName) {
        StringBuilder buf = new StringBuilder();
        org.mmbase.storage.implementation.database.DatabaseStorageManager.appendDirectory(buf, node.getNumber(), "/");
        buf.append("/").append(node.getNumber()).append(".");
        buf.append(fileName);
        return  buf.toString();
    }

    public static class Delete implements CommitProcessor {

        protected String searchFields = "";

        public void setSetFields(String f) {
            searchFields = f;
        }
        public void commit(final Node node, final Field field) {
            String existing = (String) node.getValue(field.getName());
            if (existing != null && ! "".equals(existing)) {
                File ef = new File(getDirectory(), existing);
                if (ef.exists() && ef.isFile()) {
                    // check whether not in use by some other node
                    boolean inUse = false;
                    if (searchFields.length() == 0) searchFields = field.getNodeManager().getName() + ":" + field.getName();
                    FIELDS:
                    for (String fd : searchFields.split(",")) {
                        String[] fdef = fd.split(":", 2);
                        NodeList otherNodes = SearchUtil.findNodeList(node.getCloud(), fdef[0], fdef[1], existing);
                        for (Node otherNode : otherNodes) {
                            if (otherNode.getNumber() != node.getNumber()) {
                                inUse = true;
                                break FIELDS;
                            }
                        }
                    }
                    if (! inUse) {
                        // no? Then d lete the file.
                        ef.delete();
                        log.service("Deleted " + ef);
                    } else {
                        log.service("Not deleted " + ef + " because still in use");
                    }
                } else {
                    log.warn("Could not find " + ef + " so could not delete it");
                }
            }
        }
    }


    public static class Setter implements Processor {

        private static final long serialVersionUID = 1L;

        public Object process(final Node node, final Field field, final Object value) {
            SerializableInputStream is = Casting.toSerializableInputStream(value);
            String name = is.getName();
            if (name != null) {
                String existing = (String) node.getValue(field.getName());
                if (existing != null && ! "".equals(existing)) {
                    File ef = new File(getDirectory(), existing);
                    if (ef.exists() && ef.isFile()) {
                        log.service("Removing existing field " + ef);
                        ef.delete();
                    } else {
                        log.warn("Could not find " + ef + " so could not delete it");
                    }
                }
                File f = getFile(node, field, name);

                if (log.isDebugEnabled()) {
                    log.debug("" + value + " -> " + is + " -> " + f + " " + Logging.applicationStacktrace());
                }
                is.moveTo(f);
                log.debug("Set a file " + f.getName());
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

    /**
     * A bit of a hack, used if the file was originally saved in a transaction, and hence as a
     * negative number prefix. If possible this processor corrects that.
     */
    public static class StringGetter implements Processor {
        private static final long serialVersionUID = 1L;

        public Object process(final Node node, final Field field, final Object value) {
            if (value == null) return null;
            String fileName = (String) value;
            log.debug("String processing " + fileName);
            if (fileName.startsWith("-") && node.getNumber() > 0) {
                String[] parts = fileName.split("\\.", 2);
                File file = new File(getDirectory(), fileName);
                File to = getFile(node, field, parts[1]);
                to.getParentFile().mkdirs();
                log.debug("Fixing file");
                file.renameTo(to);
                fileName = to.getName();
                log.debug("Setting file name to " + fileName);
                node.setValueWithoutProcess(field.getName(), fileName);
                log.debug("Canched " + node.getChanged() + " " + node.getCloud());
                node.commit();
            }

            return fileName;
        }
    }

}
