/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.packaging.util;

import javax.xml.parsers.DocumentBuilder;

import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.*;
import org.xml.sax.*;

/**
 * XMLBasicReader has two goals.
 *  <ul>
 *    <li>It provides a way for parsing XML</li>
 *    <li>It provides a way for searching in this XML, without the need for an XPath implementation, and without the hassle of org.w3c.dom alone.
 *    It uses dots to lay a path in the XML (XPath uses slashes).</li>
 *  </ul>
 *
 * @author Case Roule
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: ExtendedDocumentReader.java,v 1.7 2008-09-04 06:01:05 michiel Exp $
 */
public class ExtendedDocumentReader extends DocumentReader  {

    public ExtendedDocumentReader(String path) {
        super(getInputSource(path));
    }

    public ExtendedDocumentReader(String path, boolean validating) {
        super(getInputSource(path), validating, null);
    }

    public ExtendedDocumentReader(String path, Class resolveBase) {
        super(getInputSource(path), DocumentReader.validate(), resolveBase);
    }

    public ExtendedDocumentReader(InputSource source) {
        super(source, DocumentReader.validate(), null);
    }

    public ExtendedDocumentReader(InputSource source, boolean validating) {
        super(source, validating, null);
    }

    public ExtendedDocumentReader(InputSource source, Class resolveBase) {
        super(source, DocumentReader.validate(), resolveBase);
    }

    public ExtendedDocumentReader(InputSource source, boolean validating, Class resolveBase) {
        super(source, validating, resolveBase);
    }


    protected static InputSource getInputSource(String path) {
        try {
            return ResourceLoader.getConfigurationRoot().getInputSource(path);
        } catch (Exception ioe) {
            return null;
        }
    }

    public static DocumentBuilder getDocumentBuilder(boolean validating, ErrorHandler handler, EntityResolver resolver) {
        return DocumentReader.getDocumentBuilder(validating, handler, resolver);
    }

    public static DocumentBuilder getDocumentBuilder(Class<ExtendedDocumentReader> refer) {
        return DocumentReader.getDocumentBuilder(DocumentReader.validate(), null, new org.mmbase.util.xml.EntityResolver(DocumentReader.validate(), refer));
    }
}
