/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util.xml;

import org.mmbase.bridge.Cloud;
import org.w3c.dom.Document;

/**
 * Tries to convert a given xml document to another document, using the Cloud if nessecary
 *
 * @author Michiel Meeuwissen
 * @author Eduard Witteveen
 * @version $Id$
 */
public  class DocumentConverter {
    
    public static DocumentConverter getDocumentConverter(String requiredDocType) {
        return new DocumentConverter();
    }
    
    public Document convert(Document doc, Cloud cloud) {
        return doc;
    }    
}
