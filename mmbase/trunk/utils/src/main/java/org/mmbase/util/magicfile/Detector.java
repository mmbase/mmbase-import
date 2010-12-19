/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.magicfile;
import java.util.*;
import org.w3c.dom.Element;

/**
 * A Detector stores one entry from the magic.xml file, and contains
 * the functionality to determines if a certain byte[] satisfies it.
 *
 * @version $Id$
 */

public interface Detector {
    void setExtension(String extension);
    String getExtension();
    List<String> getExtensions();
    void setMimeType(String mimetype);
    String getMimeType();
    String getDesignation();
    void addChild(Detector detector, int level);
    boolean test(byte[] lithmus, java.io.InputStream input);

    void setValid(boolean v);
    boolean valid();


    void configure(Element el);


}
