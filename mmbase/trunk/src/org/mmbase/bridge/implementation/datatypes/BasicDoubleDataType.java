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
import org.mmbase.bridge.datatypes.DoubleDataType;

/**
 * @javadoc
 *
 * @author Pierre van Rooden
 * @version $Id: BasicDoubleDataType.java,v 1.6 2005-07-14 11:37:53 pierre Exp $
 * @see org.mmbase.bridge.DataType
 * @see org.mmbase.bridge.datatypes.DoubleDataType
 * @since MMBase-1.8
 */
public class BasicDoubleDataType extends BasicNumberDataType implements DoubleDataType {

    /**
     * Constructor for Double field.
     */
    public BasicDoubleDataType(String name) {
        super(name, Double.class);
    }

    public Double getMin() {
        Number min = getMinValue();
        if (min instanceof Double) {
            return (Double)min;
        } else {
            return new Double(min.doubleValue());
        }
    }

    public Double getMax() {
        Number max = getMaxValue();
        if (max instanceof Double) {
            return (Double)max;
        } else {
            return new Double(max.doubleValue());
        }
    }

}
