 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.builders.media.ResponseInfo;
import java.util.*;

/**
 * Groups some comparators
 * @author  Michiel Meeuwissen
 * @version $Id: GroupComparator.java,v 1.2 2003-01-08 08:50:18 michiel Exp $
 */
public class GroupComparator extends  ResponseInfoComparator {

    private List comparators;
    public  GroupComparator() {
        comparators = new ArrayList();
    }
    public void clear() {
        comparators.clear();
    }
    public void add(ResponseInfoComparator ri) {
        comparators.add(ri);
    }

    public int compareResponseInfo(ResponseInfo o1, ResponseInfo o2) {
        Iterator i = comparators.iterator();
        while (i.hasNext()) {
            int comp = ((ResponseInfoComparator) i.next()).compare(o1, o2); 
            if (comp != 0) return comp; 
        }
        return 0;
    }
}

