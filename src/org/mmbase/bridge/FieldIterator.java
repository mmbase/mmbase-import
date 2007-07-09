/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.ListIterator;

/**
 * A list of nodes
 *
 * @author Pierre van Rooden
 * @version $Id: FieldIterator.java,v 1.7 2006-09-25 10:17:36 pierre Exp $
 */
public interface FieldIterator<E extends Field> extends ListIterator<E> {

    /**
     * Returns the next element in the iterator as a Field
     * @return next Field
     */
    public Field nextField();

    /**
     * Returns the previous element in the iterator as a Field
     * @return previous Field
     */
    public Field previousField();

}
