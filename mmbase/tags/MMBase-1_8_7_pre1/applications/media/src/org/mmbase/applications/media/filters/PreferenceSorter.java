 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;

/**
 * Facilitates implementing Sorter by defining the 'preference' of one
 * object. A preference is just an int. The higher, the better. Only
 * for ease of implementation of descendants. 
 *
 * @author  Michiel Meeuwissen
 */
abstract public class PreferenceSorter extends Sorter {
    /**
     *  Implement a preference for this URLComposer
     */

    abstract protected int getPreference(URLComposer o); 
    
    
    final protected int compareURLComposer(URLComposer o1, URLComposer o2) {
        return getPreference(o2) - getPreference(o1);
    }

}

