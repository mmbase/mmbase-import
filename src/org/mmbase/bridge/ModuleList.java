/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.List;

/**
 * A list of modules
 *
 * @author Pierre van Rooden
 * @version $Id: ModuleList.java,v 1.4 2002-09-23 14:31:02 pierre Exp $
 */
public interface ModuleList extends BridgeList {

    /**
     * Returns the Module at the indicated postion in the list
     * @param index the position of the Module to retrieve
     */
    public Module getModule(int index);

    /**
     * Returns an type-specific iterator for this list.
     */
    public ModuleIterator moduleIterator();

}
