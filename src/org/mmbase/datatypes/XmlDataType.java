/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

/**
 * @javadoc
 *
 * @author Michiel Meeuwissen
 * @version $Id: XmlDataType.java,v 1.2 2005-10-06 23:02:03 michiel Exp $
 * @since MMBase-1.8
 */
public class XmlDataType extends BasicDataType {

    /**
     * Constructor for xml data type.
     * @param name the name of the data type
     */
    public XmlDataType(String name) {
        super(name, org.w3c.dom.Document.class);
    }

}
