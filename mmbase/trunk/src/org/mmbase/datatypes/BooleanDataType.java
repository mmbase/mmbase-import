/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

/**
 * The DataType associated with a boolean values. 
 *
 * @author Pierre van Rooden
 * @version $Id: BooleanDataType.java,v 1.4 2005-10-27 17:12:19 simon Exp $
 * @since MMBase-1.8
 */
public class BooleanDataType extends BasicDataType {

    /**
     * Constructor for boolean field that takes a class as argument, this
     * should be the 'true' class: either boolean.class or Boolean.class
     *
     * @param name the name of the data type
     * @param primitive indicate if a primitive type should be used
     */
    public BooleanDataType(String name, boolean primitive) {
        super(name, primitive ? Boolean.TYPE : Boolean.class);
    }

}
