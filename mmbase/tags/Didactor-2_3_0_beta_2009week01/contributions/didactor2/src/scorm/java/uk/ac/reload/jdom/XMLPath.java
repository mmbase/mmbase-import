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

import java.util.Iterator;
import java.util.StringTokenizer;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * This encapsulates the JDOM XPath of a given Element and Attribute
 *
 * @author Phillip Beauvoir
 * @version $Id: XMLPath.java,v 1.2 2005-08-29 00:06:51 azemskov Exp $
 */
public class XMLPath {
    /**
     * The Path separator used for xml elements
     */
    public static String XMLPATHSEPARATOR = "/";

    /**
     * The Path separator used for xml attributes
     */
    public static String XMLATTSEPARATOR = "@";

    /**
     * The actual Path String
     */
    private String _path;

    /**
     * Constructor
     * @param path
     */
    public XMLPath(String path) {
        _path = path;
    }

    /**
     * Constructor for existing XMLPath
     * @param xmlPath the XMLPath to create this one from its path
     */
    public XMLPath(XMLPath xmlPath) {
        _path = xmlPath.getPath();
    }

    /**
     * Get the Path String
     * @return the Path String
     */
    public String getPath() {
        return _path;
    }

    /**
     * Append an Element name to the Path
     * @param elementName
     * @return This XMLPath with new value set
     */
    public XMLPath appendElementName(String elementName) {
        if(elementName != null && !"".equals(elementName)) {
            _path += XMLPATHSEPARATOR + elementName;
        }
        return this;
    }

    /**
     * Prepend an Element name to the Path
     * @param elementName
     * @return This XMLPath with new value set
     */
    public XMLPath prependElementName(String elementName) {
        _path = elementName + XMLPATHSEPARATOR + _path;
        return this;
    }

    /**
     * Append an Attribute name to the Path
     * @param attributeName
     * @return This XMLPath with new value set
     */
    public XMLPath appendAttributeName(String attributeName) {
        _path += XMLATTSEPARATOR + attributeName;
        return this;
    }

    /**
     * @return an Enumeration of the Elements in the path as a StringTokenizer.
     * If the path represents an attribute then that part will be removed from the end of the path.
     * For example, given the path "root/sub1/element@att" this method will return:<p>
     * root<p>
     * sub1<p>
     * element<p>
     */
    public StringTokenizer getElements() {
        String path = getElementsPart();  // Don't return the attribute part if there is one
        return new StringTokenizer(path, XMLPATHSEPARATOR);
    }

    /**
     * @return true if this path describes an Attribute
     */
    public boolean isAttribute() {
        return _path.indexOf(XMLATTSEPARATOR) != -1;
    }

    /**
     * @return the Attribute part of this path if it contains one, or null
     */
    public String getAttributePart() {
        int index = _path.indexOf(XMLATTSEPARATOR);
        if(index != -1) {
            return _path.substring(index + 1);
        }
        else return null;
    }

    /**
     * @return the Elements part of this path
     */
    public String getElementsPart() {
        int index = _path.indexOf(XMLATTSEPARATOR);
        if(index != -1) {
            return _path.substring(0, index);
        }
        else return _path;
    }

    /**
     * @return the Root name of this Path
     */
    public String getRootPart() {
        int index = _path.indexOf(XMLPATHSEPARATOR);
        if(index != -1) {
            return _path.substring(0, index);
        }
        else return _path;
    }

    /**
     * @return the Last part of this Path - which is either the attribute part or the last element
     */
    public String getLastPart() {
        if(isAttribute()) {
            return getAttributePart();
        }
        
        int index = _path.lastIndexOf(XMLPATHSEPARATOR);
        if(index != -1) {
            return _path.substring(index + 1);
        }
        else return _path;
    }

    /**
     * @param xmlPath
     * @return true if the path part equals that of xmlPath
     */
    public boolean equals(XMLPath xmlPath) {
        return _path.equals(xmlPath.getPath());
    }

    /**
     * @param xmlPath
     * @return true if the path string ends with xmlPath's path string
     */
    public boolean endsWith(XMLPath xmlPath) {
        return _path.endsWith(xmlPath.getPath());
    }

    /**
     * @return toString
     */
    public String toString() {
        return _path;
    }

    /**
     * @param element
     * @return a XMLPath for a JDOM Element ignoring any Attributes.  If element is null will return null.
     * Different Namespaces are added as Namespace prefixes
     */
    public static XMLPath getXMLPathForElement(Element element) {
        if(element == null) {
            return null;
        }
        
        String name = element.getName();
        
        Element parent;
        while((parent = element.getParent()) != null) {
            /*
             * If the parent element has a different namespace to this element's,
             * then add a namespace prefix to this element at this point
             */ 
            if(!parent.getNamespace().equals(element.getNamespace())) {
                String prefix = element.getNamespacePrefix();
                /*
                 * If there is no namespace prefix found on this element
                 * it might be declared in the root element as an additional namespace
                 */
                if("".equals(prefix)) {
                    if(element.getDocument() != null) {
                        Iterator it = element.getDocument().getRootElement().getAdditionalNamespaces().iterator();
                        while(it.hasNext()) {
                            Namespace ns = (Namespace)it.next();
                            if(ns.equals(element.getNamespace())) {
                                prefix = ns.getPrefix();
                                if(!"".equals(prefix)) {
                                    name = prefix + ":" + name;
                                }
                            }
                        }
                    }
                }
                else {  // If we have a prefix
                    name = prefix + ":" + name;
                }
            }
            
            name = parent.getName() + XMLPATHSEPARATOR + name;
            element = parent;
        }

        XMLPath xmlPath = new XMLPath(name);
        return xmlPath;

        //XMLPath xmlPath = new XMLPath(element.getQualifiedName());
        //while((element = element.getParent()) != null) xmlPath.prependElementName(element.getQualifiedName());
        //System.out.println(xmlPath);
        //return xmlPath;
    }

    /**
     * @param attribute
     * @return a XMLPath for a JDOM Attribute or null if attribute is null
     * this will be the full path including any parent Elements such as root/element/sub@att
     */
    public static XMLPath getXMLPathForAttribute(Attribute attribute) {
        if(attribute == null) return null;
        
        Element parent = attribute.getParent();
        if(parent != null) {
            XMLPath xmlPath = getXMLPathForElement(attribute.getParent());
        	xmlPath.appendAttributeName(attribute.getQualifiedName());
        	return xmlPath;
        }
        else {
            XMLPath xmlPath = new XMLPath(attribute.getQualifiedName());
            return xmlPath;
        }
    }
}