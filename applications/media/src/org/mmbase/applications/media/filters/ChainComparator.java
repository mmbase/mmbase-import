 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import java.util.*;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;

/**
 * Chains some comparators to make one new comparator.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: ChainComparator.java,v 1.2 2003-02-05 14:28:49 michiel Exp $
 */
public class ChainComparator extends  ComparatorFilter {

    private List comparators;
    public  ChainComparator() {
        comparators = new ArrayList();
    }
    /**
     * Empties the chain
     */
    public void clear() {
        comparators.clear();
    }
    /**
     * Add one filter to the chain
     */
    public void add(ComparatorFilter ri) {
        comparators.add(ri);
    }

    public int size() {
        return comparators.size();
    }
    
    /**
     * Configure. Configures all elements on default.
     */
    public void configure(XMLBasicReader reader, Element e) {
        Iterator i = comparators.iterator();
        while (i.hasNext()) {
            ComparatorFilter ri = (ComparatorFilter) i.next();
            ri.configure(reader, e);
        }
    }

    public int compareURLComposer(URLComposer o1, URLComposer o2) {
        Iterator i = comparators.iterator();
        while (i.hasNext()) {
            int comp = ((ComparatorFilter) i.next()).compare(o1, o2); 
            if (comp != 0) return comp; 
        }
        return 0;
    }
}

