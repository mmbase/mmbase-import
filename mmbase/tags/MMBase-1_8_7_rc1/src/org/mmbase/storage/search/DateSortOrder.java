/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

/**
 * A datesortorder specifies sorting of a single date field.
 * <p>
 * This corresponds to use of ORDER BY in SQL SELECT-syntax.
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.8.4
 */
public interface DateSortOrder extends SortOrder {

    /**
     * Returns the part of the date-field wich is to be compared.
     */
    int getPart();
}
