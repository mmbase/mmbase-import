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
 * Implements a Filter as a Labeler. It only adds stuff to the URLComposer. It does not remove, it
 * does not change order.
 *
 * @author  Michiel Meeuwissen
 * @since MMBase-1.9.2
 * @version $Id: $
 */
abstract public class Labeler implements  Filter {

    /**
     * Implement this.
     */
    abstract  protected void label(URLComposer o);

    public void configure(DocumentReader reader, Element e) {
        // nothing to be configured on default.
    }


    final public List<URLComposer> filter(List<URLComposer> urlcomposers) {
        for (URLComposer uc : urlcomposers) {
            label(uc);
        }
        return urlcomposers;
    }
}

