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
 * @version $Id: BinaryDataType.java,v 1.2 2005-10-06 23:02:03 michiel Exp $
 * @since MMBase-1.8
 */
public class BinaryDataType extends AbstractLengthDataType {

    /**
     * Constructor for binary field.
     * @param name the name of the data type
     */
    public BinaryDataType(String name) {
        super(name, byte[].class);
    }

    public long getLength(Object value) {
        return ((byte[]) value).length;
    }


}
