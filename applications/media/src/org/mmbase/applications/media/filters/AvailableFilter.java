 /*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;

import org.mmbase.applications.media.urlcomposers.URLComposer;
import java.util.Iterator;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;
import java.util.List;

/**
 * This can sort a list of URLComposers with the available ones on top.
 * @author  Michiel Meeuwissen
 * @version $Id: AvailableFilter.java,v 1.1 2003-02-05 14:28:49 michiel Exp $
 */
public class AvailableFilter implements Filter {

    public List filter(List urlcomposers) {
        Iterator i = urlcomposers.iterator();
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

