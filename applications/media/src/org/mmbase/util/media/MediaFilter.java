/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.util.media;
import org.mmbase.util.XMLBasicReader;
import org.w3c.dom.Element;
import java.util.List;

/**
 *
 */
public interface MediaFilter {
    
    public List filter(List responseInfos);
    public void configure(XMLBasicReader reader, Element e);
}

