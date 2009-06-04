/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.media.filters;
import org.mmbase.applications.media.urlcomposers.URLComposer;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.Element;
import java.util.List;

/**
 *
 */
public interface Filter {
    
    public List<URLComposer> filter(List<URLComposer> urlcomposers);
    public void configure(DocumentReader reader, Element e);
}

