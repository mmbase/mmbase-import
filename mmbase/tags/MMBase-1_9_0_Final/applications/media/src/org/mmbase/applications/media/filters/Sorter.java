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
 * Implements a Filter as a Comparator. That means that it only sorts,
 * and the implementation is done by calling Collections.sort. You can
 * be sure that no URLComposers are removed during the filter process.
 *
 * @author  Michiel Meeuwissen
 */
abstract public class Sorter implements Comparator<URLComposer>, Filter {

    /**
     * Implement this.
     */

    abstract  protected int compareURLComposer(URLComposer o1, URLComposer o2); 
    
    public void configure(DocumentReader reader, Element e) {
        // nothing to be configured on default.
    }
         
    final public int compare(URLComposer ri1, URLComposer ri2) {
        return compareURLComposer(ri1, ri2);
    }

    final public List<URLComposer> filter(List<URLComposer> urlcomposers) {
        Collections.sort(urlcomposers, this);
        return urlcomposers;
    }
}

