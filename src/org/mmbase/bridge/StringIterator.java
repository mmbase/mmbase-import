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
 * @version $Id: StringIterator.java,v 1.7 2006-09-25 10:17:36 pierre Exp $
 */
public interface StringIterator<E extends String> extends ListIterator<E> {

    /**
     * Returns the next element in the iterator as a String
     * @return next String
     */
    public String nextString();

    /**
     * Returns the previous element in the iterator as a String
     * @return previous String
     * @since MMBase-1.7
     */
    public String previousString();

}
