/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import java.util.*;
import org.apache.lucene.document.Document;
/**
 *
 * @version $Id: IndexEntry.java,v 1.1 2005-12-27 15:45:06 michiel Exp $
 **/
public interface IndexEntry {

    void       index(Document document);
    Collection getSubDefinitions();
    String     getIdentifier(); 
}


