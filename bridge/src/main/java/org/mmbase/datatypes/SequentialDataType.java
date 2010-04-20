/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

/**
 * A sequential datatype is a datatype of which the values are 'sequential', i.e. every value has a well defined previous and next value.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-2.0
 */
public interface SequentialDataType<E extends Comparable> extends DataType<E> {


    /**
     * Given a certain value, returns the value with is 1 bigger. It can return <code>null</code> if there is no such value (and the given value is
     * the biggest one possible)
     */
    E increase(E pos);
    /**
     * Given a certain value, returns the value with is 1 smaller. It can return <code>null</code> if there is no such value (and the given value is
     * the smallest one possible)
     */
    E decrease(E pos);

    /**
     * Returns a natural 'first' value. This may return some kind of <code>0</code>
     * @since MMBase-2.0
     */
    E first();

}
