/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.irc.communication;

/**
 * @obsolete
 * @author vpro
 * @version $Id: CommunicationUserInterface.java,v 1.4 2003-01-07 11:49:32 kees Exp $
 */
public interface CommunicationUserInterface {
    /**
     * @javadoc
     */
    public void receive(String msg);
}
