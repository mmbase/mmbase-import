/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util.xml;

import org.mmbase.bridge.Cloud;
import org.w3c.dom.Document;

import org.mmbase.util.logging.*;

/**
 * Tries to convert a given xml document to another document, using the Cloud if nessecary
 *
 * @author Michiel Meeuwissen
 * @author Eduard Witteveen
 * @version $Id: DocumentConverter.java,v 1.1 2002-04-05 11:47:06 eduard Exp $
 */
public  class DocumentConverter {
    private static Logger log = Logging.getLoggerInstance(DocumentConverter.class.getName());
    
    public DocumentConverter(String requiredDocType) {
    }
    
    public Document convert(Document doc, Cloud cloud) {
        return doc;
    }    
}
