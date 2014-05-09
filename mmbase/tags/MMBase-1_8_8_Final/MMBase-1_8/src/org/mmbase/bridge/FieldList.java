/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * A list of field types
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public interface FieldList extends BridgeList {

    /**
     * Returns the Field at the indicated postion in the list
     * @param index the position of the Field to retrieve
     * @return Field at the indicated postion
     */
    public Field getField(int index);

    /**
     * Returns an type-specific iterator for this list.
     * @return Field iterator
     */
    public FieldIterator fieldIterator();

}
