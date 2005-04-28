/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.ListIterator;

/**
 * An iterator for a list of strings
 *
 * @author Pierre van Rooden
 * @version $Id: StringIterator.java,v 1.4 2003-03-04 09:19:05 pierre Exp $
 */
public interface StringIterator extends ListIterator {

    /**
     * Returns the next element in the iterator as a String
     */
    public String nextString();

    /**
     * Returns the previous element in the iterator as a String
     */
    public String previousString();

}
