/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * Event/changes interface for MMObjectNodes this is a callback  
 * interface thats need to be implemented when a object wants to add 
 * itself as a change listener on Builder to recieve signals if nodes change.
 *
 * @author Daniel Ockeloen
 * @version $Id: MMBaseObserver.java,v 1.7 2003-04-03 16:18:39 michiel Exp $
 */
public interface MMBaseObserver {
    /**
     * @javadoc
     */
    public boolean nodeRemoteChanged(String machine, String number, String builder, String ctype);

    /**
     * @javadoc
     */
    public boolean nodeLocalChanged(String machine, String number, String builder, String ctype);
}
