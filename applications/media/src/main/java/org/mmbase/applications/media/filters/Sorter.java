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
import org.w3c.dom.*;
import org.mmbase.util.logging.*;

/**
 * Implements a Filter as a Comparator. That means that it only sorts,
 * and the implementation is done by calling Collections.sort. You can
 * be sure that no URLComposers are removed during the filter process.
 *
 * @author  Michiel Meeuwissen
 */
abstract public class Sorter implements Comparator<URLComposer>, Filter {
    private static final Logger log = Logging.getLoggerInstance(Sorter.class);

    /**
     * Implement this.
     */

    abstract  protected int compareURLComposer(URLComposer o1, URLComposer o2);

    /**
     * Configure with setters on default
     */
    @Override
    public void configure(DocumentReader reader, Element e) {
        FilterUtils.propertiesConfigure(this, reader, e);
    }

    @Override
    final public int compare(URLComposer ri1, URLComposer ri2) {
        return compareURLComposer(ri1, ri2);
    }

    @Override
    final public List<URLComposer> filter(List<URLComposer> urlcomposers) {
        Collections.sort(urlcomposers, this);
        return urlcomposers;
    }
}

