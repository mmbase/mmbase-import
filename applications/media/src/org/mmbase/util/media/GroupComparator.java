 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;

import org.mmbase.module.builders.media.ResponseInfo;
import java.util.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;

/**
 * Groups some comparators
 * @author  Michiel Meeuwissen
 * @version $Id: GroupComparator.java,v 1.3 2003-01-08 14:48:40 michiel Exp $
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
    
    protected void configure(XMLBasicReader reader, Element e) {
        Iterator i = comparators.iterator();
        while (i.hasNext()) {
            ResponseInfoComparator ri = (ResponseInfoComparator) i.next();
            ri.configure(reader, e);
        }
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

