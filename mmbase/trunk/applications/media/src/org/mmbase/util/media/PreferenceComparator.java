 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.builders.media.ResponseInfo;

/**
 * Facilitates implementing Comparator by defining the 'preference' of one object.
 *
 * @author  Michiel Meeuwissen
 */
abstract public class PreferenceComparator extends ResponseInfoComparator {
    /**
     *  Implement a preference for this ResponseInfo
     */

    abstract protected int getPreference(ResponseInfo o); 
    
    
    final protected int compareResponseInfo(ResponseInfo o1, ResponseInfo o2) {
        return getPreference(o2) - getPreference(o1);
    }

}

