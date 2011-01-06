/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * A list of field types Not that since MMBase-1.9/Java 1.5 this list simply implements
 * <code>BridgeList&ltField&gt;</code>.
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public interface FieldList extends BridgeList<Field> {

    /**
     * Returns the Field at the indicated position in the list
     * @param index the position of the Field to retrieve
     * @return Field at the indicated position
     */
    Field getField(int index);

    /**
     * Returns an type-specific iterator for this list.
     * @return Field iterator
     */
    FieldIterator fieldIterator();


}
