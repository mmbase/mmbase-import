/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import org.mmbase.module.core.MMBase;
import javax.servlet.ServletException;

/**
 * Used in combo with MMBaseStartThread, which uses the methods of this interface to inform its starter about the results.
 * 
 * @version $Id$
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 */
public interface MMBaseStarter {

    /**
     * Returns the currently set MMBase object.
     * @return the MMBase instance or null.
     */
    MMBase getMMBase();

    /**
     * Set MMBase object after initializion, in the caller.
     */
    void   setMMBase(MMBase mmb);

    /**
     * If something went wrong (an exception occured), the caller may is informed by a call to this
     * method. (It may ignore it).
     */
    void   setInitException(ServletException e);

}
    
