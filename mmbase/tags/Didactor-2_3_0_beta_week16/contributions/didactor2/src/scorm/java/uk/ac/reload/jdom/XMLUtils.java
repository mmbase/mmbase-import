/**
 *  RELOAD TOOLS
 *
 *  Copyright (c) 2003 Oleg Liber, Bill Olivier, Phillip Beauvoir
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  Project Management Contact:
 *
 *  Oleg Liber
 *  Bolton Institute of Higher Education
 *  Deane Road
 *  Bolton BL3 5AB
 *  UK
 *
 *  e-mail:   o.liber@bolton.ac.uk
 *
 *
 *  Technical Contact:
 *
 *  Phillip Beauvoir
 *  e-mail:   p.beauvoir@bolton.ac.uk
 *
 *  Web:      http://www.reload.ac.uk
 *
 */

package uk.ac.reload.jdom;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;


/**
 * Some useful XML Utilities that leverage the JDOM Package<br>
 *
 * @author Phillip Beauvoir
 * @version $Id: XMLUtils.java,v 1.2 2005-08-29 00:06:51 azemskov Exp $
 */
public final class XMLUtils {
	
    /**
     * The XSI Namespace
     */
    public static Namespace XSI_Namespace = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

    /**
     * The Old XSI Namespace
     */
    public static Namespace XSI_NamespaceOLD = Namespace.getNamespace("xsi", "http://www.w3.org/2000/10/XMLSchema-instance");

    /**
     * The schemaLocation String
     */
    public static String XSI_SchemaLocation = "schemaLocation";
	
	/**
	 * Writes a JDOM Document to file
	 * @param doc The JDOM Document to write
	 * @param file The file to write to
	 * @throws IOException
	 */
	public static void write2XMLFile(Document doc, File file) throws IOException {
		XMLOutputter outputter = new XMLOutputter("  ", true);
		// This gets rid of junk characters (but also carriage returns...)
		outputter.setTextNormalize(true);
		
		// Create parent folder if it doesn't exist
		File parent = file.getParentFile();
		if(parent != null) {
		    parent.mkdirs();
		}
		
		FileOutputStream out = new FileOutputStream(file);
		outputter.output(doc, out);
		out.close();
	}
	
	/**
	 * Convert a JDOM Document to a String format
	 * @param doc The JDOM Document
	 * @return The resulting String
	 * @throws IOException
	 */
	public static String write2XMLString(Document doc) throws IOException {
		XMLOutputter outputter = new XMLOutputter("  ", true);
		// This gets rid of junk characters (but also carriage returns...)
		outputter.setTextNormalize(true);
		StringWriter out = new StringWriter();
		outputter.output(doc, out);
		out.close();
		return out.toString();
	}
	
	/**
	 * Reads and returns a JDOM Document from file with Schema validation
	 * @param file The XML File
	 * @param schemaNamespace The Schema Target Namespace
	 * @param schemaLocation The Schema Location
	 * @return The JDOM Document or null if not found
	 * @throws FileNotFoundException
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Document readXMLFile(File file, String schemaNamespace, String schemaLocation) throws IOException, JDOMException {
		Document doc = null;
		SAXBuilder builder = new SAXBuilder(true);
		builder.setFeature("http://apache.org/xml/features/validation/schema", true);
		builder.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation",
				schemaNamespace + " " + schemaLocation);
		// This allows UNC mapped locations to load
		doc = builder.build(new FileInputStream(file));
		return doc;
	}
	
	/**
	 * Reads and returns a JDOM Document from file without Schema Validation
	 * @param file The XML File
	 * @return The JDOM Document or null if not found
	 * @throws FileNotFoundException
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Document readXMLFile(File file) throws IOException, JDOMException {
		Document doc = null;
		SAXBuilder builder = new SAXBuilder();
		// This allows UNC mapped locations to load
		doc = builder.build(new FileInputStream(file));
		return doc;
	}
	
	/**
	 * Reads and returns a JDOM Document from String without Schema Validation
	 * @param xmlString
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Document readXMLString(String xmlString) throws JDOMException, IOException {
	    SAXBuilder builder = new SAXBuilder();
	    return builder.build(new StringReader(xmlString));
	}

    /**
     * Replace a given Namespace with another one.  This can be used for removing Namespace prefixes.
     * This is recursive and traverses the whole JDOM tree.
     * @param element The starting Element
     */
    public static void replaceNamespaces(Element element, Namespace oldNamespace, Namespace newNamespace) {
        if(element.getNamespace().equals(oldNamespace)) {
        	element.setNamespace(newNamespace);
        }
    
        Iterator children = element.getChildren().iterator();
        while(children.hasNext()) {
            Element child = (Element)children.next();
            replaceNamespaces(child, oldNamespace, newNamespace);
        }
    }

    /**
     * @return The schema location for a given Namespace in a JDOM Document or null if not found
     */
    public static String getSchemaLocation(Document doc, Namespace ns) {
        if(doc.hasRootElement() && ns != null) {
            String namespaceURI = ns.getURI();
            if(namespaceURI != null) {
                Element root = doc.getRootElement();
    
                // Get Schema Location
                String str = root.getAttributeValue(XSI_SchemaLocation, XSI_Namespace);
                // Try an older one
                if(str == null) {
                    str = root.getAttributeValue(XSI_SchemaLocation, XSI_NamespaceOLD);
                }
    
                if(str != null) {
                    int index = str.indexOf(namespaceURI);
                    if(index != -1) {
                        str = str.substring(index + namespaceURI.length());
                        StringTokenizer t = new StringTokenizer(str, " ");
                        try {
                            return t.nextToken();
                        }
                        catch(NoSuchElementException ex) {
                            return null;
                        }
                    }
                }
            }
        }
    
        return null;
    }
	
    
    /**
     * @return The root Namespace in the Document or null if not found
     */
    public static Namespace getDocumentNamespace(Document doc) {
        Namespace ns = null;
        if(doc.hasRootElement()) {
            ns = doc.getRootElement().getNamespace();
        }
        return ns;
    }

    /**
     * @return A given Namespace in the Document root given the Namespace's prefix or null if not found
     */
    public static Namespace getDocumentNamespace(Document doc, String prefix) {
        Namespace ns = null;
        if(doc.hasRootElement()) {
            ns = doc.getRootElement().getNamespace(prefix);
        }
        return ns;
    }
    
    /**
     * Hunt for a Namespace in the Document searching all additional Namespaces and
     * Elements in case the Namespace is declared "in-line" at the Element level
     * @param doc
     * @param ns
     * @return true if found
     */
    public static boolean containsNamespace(Document doc, Namespace ns) {
        return containsNamespace(doc.getRootElement(), ns);
    }
    
    /**
     * Hunt for a Namespace in the Element searching all sub-Elements in case the Namespace
     * is declared "in-line" at the Element level
     * @param element
     * @param ns
     * @return true if found
     */
    public static boolean containsNamespace(Element element, Namespace ns) {
        // Element Namespace?
        if(ns.equals(element.getNamespace())) {
            return true;
        }

        // Additional Namespace?
        Iterator it = element.getAdditionalNamespaces().iterator();
        while(it.hasNext()) {
            Namespace ns1 = (Namespace)it.next();
            if(ns1.equals(ns)) {
                return true;
            }
        }

        // Recurse children
        Iterator i = element.getChildren().iterator();
        while(i.hasNext()) {
            Element child = (Element) i.next();
            boolean found = containsNamespace(child, ns);
            if(found) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * @return true if element1 and element2 belong to the same Document
     */
    public static boolean isMemberOfSameDocument(Element element1, Element element2) {
        Document doc1 = element1.getDocument();
        Document doc2 = element2.getDocument();
        if(doc1 == null || doc2 == null) {
            return false;
        }
        return doc1.equals(doc2);
    }
	
}