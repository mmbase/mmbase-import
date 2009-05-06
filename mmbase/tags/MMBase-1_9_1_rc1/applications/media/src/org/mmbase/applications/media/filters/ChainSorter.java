 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import java.util.*;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;

/**
 * Chains some comparators to make one new comparator.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: ChainSorter.java,v 1.5 2007-07-10 12:11:45 michiel Exp $
 */
public class ChainSorter extends  Sorter {

    private final List<Sorter> comparators = new ArrayList<Sorter>();
    public  ChainSorter() {
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
    public void add(Sorter ri) {
        comparators.add(ri);
    }

    public int size() {
        return comparators.size();
    }
    
    /**
     * Configure. Configures all elements on default.
     */
    public void configure(DocumentReader reader, Element e) {
        for (Sorter ri : comparators) {
            ri.configure(reader, e);
        }
    }

    public int compareURLComposer(URLComposer o1, URLComposer o2) {
        for (Sorter sorter : comparators) {
            int comp = sorter.compare(o1, o2); 
            if (comp != 0) return comp; 
        }
        return 0;
    }
}

