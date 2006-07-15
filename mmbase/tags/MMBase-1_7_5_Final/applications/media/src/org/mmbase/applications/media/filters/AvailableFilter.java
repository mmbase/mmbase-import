 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import java.util.ListIterator;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;
import java.util.List;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This removes all URLComposers wich are not available.
 * @author  Michiel Meeuwissen
 * @version $Id: AvailableFilter.java,v 1.4 2003-07-15 12:50:24 vpro Exp $
 */
public class AvailableFilter implements Filter {
    private static Logger log = Logging.getLoggerInstance(AvailableFilter.class);

    public List filter(List urlcomposers) {
        ListIterator i = urlcomposers.listIterator();
        while (i.hasNext()) {
            URLComposer uc = (URLComposer) i.next();
            if (! uc.isAvailable()) {
                i.remove();
            }
        }
        return urlcomposers;
        
    }
    public void configure(XMLBasicReader reader, Element e) {
        // not needed
    }

}

