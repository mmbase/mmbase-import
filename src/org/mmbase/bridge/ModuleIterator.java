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
 * @version $Id: ModuleIterator.java,v 1.5 2004-05-06 12:34:40 keesj Exp $
 */
public interface ModuleIterator extends ListIterator {

    /**
     * Returns the next element in the iterator as a Module
     */
    public Module nextModule();

    /**
     * Returns the previous element in the iterator as a Module
     * @since MMBase-1.7
     */
    public Module previousModule();

}
