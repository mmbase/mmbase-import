 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.builders.media.ResponseInfo;
import java.util.Comparator;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;

/**
 * Just a Comparator but it adds two functions:
 * - compareResonseInfo for easy overriding without Casting
 * - configure can be overridden to add configuration for this Comparator to the mediasources.xml
 *
 * @author  Michiel Meeuwissen
 */
abstract public class ResponseInfoComparator implements Comparator {

    /**
     * Implement this.
     */

    abstract  protected int compareResponseInfo(ResponseInfo o1, ResponseInfo o2); 
    
    protected void configure(XMLBasicReader reader, Element e) {
        // nothing to be configured on default.
    }
         
    final public int compare(Object o1, Object o2) {
        ResponseInfo ri1  = (ResponseInfo) o1;
        ResponseInfo ri2  = (ResponseInfo) o2;
        return compareResponseInfo(ri1, ri2);
    }
}

