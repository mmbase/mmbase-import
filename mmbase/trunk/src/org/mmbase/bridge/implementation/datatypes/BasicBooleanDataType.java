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
import org.mmbase.bridge.datatypes.BooleanDataType;
import org.mmbase.bridge.implementation.AbstractDataType;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicBooleanDataType.java,v 1.5 2005-07-14 11:37:53 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.BooleanDataType
 * @since MMBase-1.8
 */
public class BasicBooleanDataType extends AbstractDataType implements BooleanDataType {

    /**
     * Constructor for boolean field.
     */
    public BasicBooleanDataType(String name) {
        super(name, Boolean.class);
    }

}
