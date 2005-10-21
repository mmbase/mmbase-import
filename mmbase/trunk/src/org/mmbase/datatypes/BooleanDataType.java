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
 * @version $Id: BooleanDataType.java,v 1.3 2005-10-21 10:20:28 michiel Exp $
 * @since MMBase-1.8
 */
public class BooleanDataType extends BasicDataType {

    /**
     * Constructor for boolean field.
     * @param name the name of the data type
     */
    public BooleanDataType(String name) {
        super(name, Boolean.class);
    }

}
