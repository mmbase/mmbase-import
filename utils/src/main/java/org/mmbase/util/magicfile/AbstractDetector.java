/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.magicfile;
import java.util.*;
import java.io.*;
import org.mmbase.util.logging.*;

/**

 * @version $Id: Detector.java 41036 2010-02-15 22:30:54Z michiel $
 */

public abstract class AbstractDetector implements Detector {
    private static final Logger log = Logging.getLoggerInstance(BasicDetector.class);

    /**
     * Designation for this type in 'magic' file
     */
    protected String message = "Unknown";
    /**
     * Possible file extensions for this type
     */
    private final List<String> extensions = new ArrayList<String>();

    private String mimetype = "application/octet-stream";

    protected  final List<Detector> childList= new ArrayList<Detector>();

    /**
     *  Set this if parsing of magic file fails
     */
    protected boolean valid = true;

    /**
     * Add an embedded detector object that searches for more details after an initial match.
     */
    public void addChild(Detector detector, int level) {
        if (level == 1) {
            childList.add(detector);
        } else if (level > 1) {
            if (childList.size() == 0) {
                log.debug("Hm. level = " + level + ", but childList is empty");
            } else {
                (childList.get(childList.size() - 1)).addChild(detector, level - 1);
            }
        }
    }

    /**
     * Adds a possible extension. The last added one is the default (returned by 'getExtension').
     */
    public void setExtension(String extension) {
        extensions.add(0, extension);
    }
    public String getExtension() {
        if (extensions.size() == 0) {
            return "";
        }
        return extensions.get(0);
    }
    public List<String> getExtensions() {
        return extensions;
    }

    public void setMimeType(String mimetype) {
        this.mimetype = mimetype;
    }
    public String getMimeType() {
        if (mimetype.equals("???")) {
            return "application/octet-stream";
        } else {
            return mimetype;
        }
    }
    public void setDesignation(String designation) {
        this.message = designation;
    }

    public void configure(org.w3c.dom.Element el) {
        // nothing to do.
    }
}
