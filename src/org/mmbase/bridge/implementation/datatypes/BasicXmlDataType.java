/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.DataType;
import org.mmbase.bridge.datatypes.XmlDataType;

/**
 * @javadoc
 *
 * @author Michiel Meeuwissen
 * @version $Id: BasicXmlDataType.java,v 1.3 2005-07-11 14:42:52 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.BooleanDataType
 * @since MMBase-1.8
 */
public class BasicXmlDataType extends BasicBigDataType implements XmlDataType {

    public BasicXmlDataType(String name) {
        super(name, org.w3c.dom.Document.class);
    }

    /**
     * @param name the name of the data type
     * @param type the class of the data type's possible value
     */
    public BasicXmlDataType(String name, DataType dataType) {
        super(name,dataType);
    }

    public int getBaseType() {
        return Field.TYPE_XML;
    }

}
