/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * @javadoc
 * @author Michiel Meeuwissen
 * @version $Id: FieldValueDateConstraint.java,v 1.2 2004-11-30 14:06:55 pierre Exp $
 * @since MMBase-1.8
 */
public interface FieldValueDateConstraint extends FieldValueConstraint {

    static final int CENTURY      = 0;
    static final int YEAR         = 1;
    static final int MONTH        = 2;
    static final int QUARTER      = 3;
    static final int WEEK         = 4;
    static final int DAY_OF_YEAR  = 5;
    static final int DAY_OF_MONTH = 6;
    static final int DAY_OF_WEEK  = 7;

    static final int HOUR         = 8;
    static final int MINUTE       = 9;
    static final int SECOND       = 10;

    /**
     * Returns the part of the date-field wich is to be compared.
     */
    int getPart();

}
