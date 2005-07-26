/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.xml;

import org.w3c.dom.*;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: AbstractObjectDefinition.java,v 1.2 2005-07-26 14:36:31 pierre Exp $
 **/
abstract public class AbstractObjectDefinition {

    /**
     * The namespace used in xml fragments that describe the object.
     */
    public String objectNameSpace = null;

    /**
     * Constructor.
     */
    protected AbstractObjectDefinition(String objectNameSpace) {
        this.objectNameSpace = objectNameSpace;
    }

    /**
     * Returns whether an element has a certain attribute, either an unqualified attribute or an attribute that fits in the
     * datatypes namespace
     */
    protected boolean hasAttribute(Element element, String localName) {
        return element.hasAttributeNS(objectNameSpace,localName) || element.hasAttribute(localName);
    }

    /**
     * Returns the value of a certain attribute, either an unqualified attribute or an attribute that fits in the
     * datatypes namespace
     */
    protected String getAttribute(Element element, String localName) {
        if (element.hasAttributeNS(objectNameSpace,localName)) {
            return element.getAttributeNS(objectNameSpace,localName);
        } else {
            return element.getAttribute(localName);
        }
    }

    /**
     * Returns the textual value (content) of a certain element
     */
    protected String getValue(Element element) {
        NodeList nl = element.getChildNodes();
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < nl.getLength(); i++) {
            Node textnode = nl.item(i);
            if (textnode.getNodeType() == Node.TEXT_NODE) {
                res.append(textnode.getNodeValue().trim());
            } else if (textnode.getNodeType() == Node.CDATA_SECTION_NODE) {
                res.append(textnode.getNodeValue());
            }
        }
        return res.toString();
    }
}

