/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.implementation.datatypes;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.datatypes.BinaryDataType;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicBinaryDataType.java,v 1.4 2005-07-12 15:03:35 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.BinaryDataType
 * @since MMBase-1.8
 */
public class BasicBinaryDataType extends BasicBigDataType implements BinaryDataType {

    /**
     * Constructor for binary field.
     */
    public BasicBinaryDataType(String name) {
        super(name, byte[].class);
    }

    public int getBaseType() {
        return Field.TYPE_BINARY;
    }

}
