/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.irc.communication;

/**
 * @javadoc
 * @author vpro
 * @version $Id: CommunicationInterface.java,v 1.3 2002-03-04 14:07:46 pierre Exp $
 */
public interface CommunicationInterface{
    /**
     * @javadoc
     */
    public boolean	connect( String host, String name, String group, String password );
    /**
     * @javadoc
     */
    public boolean	isconnected();
    /**
     * @javadoc
     */
    public boolean	isaccepted();

    /**
     * @javadoc
     */
    public boolean	reconnect();

    /**
     * @javadoc
     */
    public void		sendPublic( String msg );
    /**
     * @javadoc
     */
    public void		sendPrivate( String who, String msg );

    /**
     * @javadoc
     */
    public void 	stopit();
}
