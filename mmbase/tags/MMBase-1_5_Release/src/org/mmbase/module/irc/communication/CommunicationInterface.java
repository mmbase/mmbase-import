/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.irc.communication;

public interface CommunicationInterface
{
	public boolean	connect( String host, String name, String group, String password );
	public boolean	isconnected();
	public boolean	isaccepted();
	
	public boolean	reconnect();

	public void		sendPublic( String msg );
	public void		sendPrivate( String who, String msg );

	public void 	stopit();
}
