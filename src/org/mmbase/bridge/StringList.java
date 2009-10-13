/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * A list of Strings
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public interface StringList extends BridgeList {

    /**
     * Returns the string at the indicated postion in the list
     * @param index the position of the string to retrieve
     * @return string at the indicated postion
     */
    public String getString(int index);

    /**
     * Returns an type-specific iterator for this list.
     * @return String iterator
     */
    public StringIterator stringIterator();

}
