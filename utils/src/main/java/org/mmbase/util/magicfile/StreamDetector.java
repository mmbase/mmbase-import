/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util.magicfile;
import org.mmbase.util.SerializableInputStream;
import java.util.*;
import org.w3c.dom.Element;

/**
 * @version $Id: Detector.java 43405 2010-09-19 20:16:06Z michiel $
 */

public interface StreamDetector extends Detector {
    boolean test(SerializableInputStream input);
}
