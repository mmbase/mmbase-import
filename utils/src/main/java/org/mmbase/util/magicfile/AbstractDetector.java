/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.magicfile;
import java.util.*;
import org.w3c.dom.Element;
import org.mmbase.util.xml.*;
import org.mmbase.util.logging.*;

/**

 * @version $Id$
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
    @Override
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
    @Override
    public void setExtension(String extension) {
        extensions.add(0, extension);
    }
    @Override
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
    @Override
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
    @Override
    public String getDesignation() {
        return message;
    }

    @Override
    public void setValid(boolean v) {
        valid = v;
    }

    /**
     * @return Whether parsing of magic line for this detector succeeded
     */
    @Override
    public boolean valid() {
        return valid;
    }



    @Override
    public void configure(Element e) {

        {
            Element e1 = DocumentReader.getElementByPath(e, "detector.mimetype");
            if (e1 == null ) log.error("No mime type in " + XMLWriter.write(e));
            setMimeType(DocumentReader.getElementValue(e1));
        }

        {
            Element e1 = DocumentReader.getElementByPath(e, "detector.extension");
            setExtension(DocumentReader.getElementValue(e1));
        }
        {
            Element e1 = DocumentReader.getElementByPath(e, "detector.designation");
            setDesignation(DocumentReader.getElementValue(e1));
        }
    }


    @Override
    public String toString() {
        return getClass() + " " + getMimeType() + " " + extensions;
    }

}
