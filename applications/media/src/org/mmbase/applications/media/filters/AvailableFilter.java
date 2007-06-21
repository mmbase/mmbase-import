 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import java.util.ListIterator;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;
import java.util.List;

/**
 * This removes all URLComposers wich are not available.
 * @author  Michiel Meeuwissen
 * @version $Id: AvailableFilter.java,v 1.7 2007-06-21 15:50:21 nklasens Exp $
 */
public class AvailableFilter implements Filter {

    public List<URLComposer> filter(List<URLComposer> urlcomposers) {
        ListIterator<URLComposer> i = urlcomposers.listIterator();
        while (i.hasNext()) {
            URLComposer uc = i.next();
            if (! uc.isAvailable()) {
                i.remove();
            }
        }
        return urlcomposers;
        
    }
    public void configure(DocumentReader reader, Element e) {
        // not needed
    }

}

