/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import org.mmbase.util.logging.*;

/**
 * Wraps an {@link org.w3c.dom.Document} to be certainly serializable (and cloneable). If it is not by itself (IIRC
 * the Xerces implementation is serializable), then this class serializes to a stringification.
 * 
 * This can be used if a Serializable class needs an Document member. Choose for a
 * DocumentSerializable member in stead, and use {@link #getDocument}.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class DocumentSerializable implements Serializable, org.mmbase.util.PublicCloneable {
    private static final Logger log = Logging.getLoggerInstance(DocumentSerializable.class);
    private static final long serialVersionUID = 1L; 

    private Document document;
    // implementation of serializable
    private void writeObject(ObjectOutputStream out) throws IOException {
        if (document instanceof Serializable) {
            out.writeObject(document);
        } else {
            String string = XMLWriter.write(document, false);
            out.writeObject(string);
        }
    }
    // implementation of serializable
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object o = in.readObject();
        if (o instanceof Document) {
            document = (Document) o;
        } else {
            try {
                DocumentBuilder documentBuilder = DocumentReader.getDocumentBuilder(false, null, null);
                document = documentBuilder.parse(new InputSource(new StringReader("" + o)));
            } catch (SAXException e) {
                log.warn(e);
            }
        }
    }



    public DocumentSerializable(Document d) {
        document = d;
    }

    public final Document getDocument() {
        return document;
    }

    public String toString() {
        return XMLWriter.write(document, false, true);
    }

    public int hashCode() {
        return document.hashCode();
    }
    public boolean equals(Object o) {
        return 
            o != null &&
            o instanceof DocumentSerializable &&
            document.equals(((DocumentSerializable)o).document);            
    }

    public Object clone() {
        Document newDocument = DocumentReader.getDocumentBuilder(false, null, null).newDocument();        
        Node root = newDocument.importNode(document.getDocumentElement(), true);
        newDocument.appendChild(root);
        return new DocumentSerializable(newDocument);
    }

}
