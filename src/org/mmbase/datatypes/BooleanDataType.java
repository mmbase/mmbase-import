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
 * @author Pierre van Rooden
 * @version $Id: BooleanDataType.java,v 1.1 2005-07-22 12:35:47 pierre Exp $
 * @since MMBase-1.8
 */
public class BooleanDataType extends DataType {

    /**
     * Constructor for boolean field.
     * @param name the name of the data type
     */
    public BooleanDataType(String name) {
        super(name, Boolean.class);
    }

}
