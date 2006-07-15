/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;
import java.util.List;

/**
 *
 */
public interface Filter {
    
    public List filter(List urlcomposers);
    public void configure(XMLBasicReader reader, Element e);
}

