 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.builders.media.ResponseInfo;

/**
 * This can sort a list with the available urls on top.
 * @author  Michiel Meeuwissen
 * @version $Id: AvailableComparator.java,v 1.1 2003-01-07 22:21:02 michiel Exp $
 */
public class AvailableComparator extends  PreferenceComparator {

    public  AvailableComparator() {
    }
    
    public int getPreference(ResponseInfo ri) {        
        if (! ri.isAvailable()) {
            return -1; // very bad choice.
        } else {
            return 0;
        }
    }
}

