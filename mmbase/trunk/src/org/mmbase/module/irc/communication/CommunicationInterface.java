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
