/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.irc.communication.irc;

public class IrcCommands {

	public static IrcMessage pass( String server, String from, String password ) {
		return new IrcMessage( server, from, "PASS", "", password ,"");
	}

	public static IrcMessage nick( String server, String from, String nickname ) {
		return new IrcMessage( server, from, "NICK", "", nickname, "");
	}

	public static IrcMessage user( String server, String from, String username, String hostname, String servername, String realname ) {
		return new IrcMessage( server, from, "USER", "", username + " " + hostname + " " + servername + " ", realname);
	}

	public static IrcMessage server( String server, String from, String servername, String hopcount, String info ) {
		return new IrcMessage( server, from, "SERVER", servername, hopcount, info );
	} 

	public static IrcMessage oper( String server, String from, String user, String password ) {
		return new IrcMessage( server, from, "OPER", "", user + " " + password, "");
	}

	public static IrcMessage quit( String server, String from, String msg ) {
		return new IrcMessage( server, from, "QUIT", "", "", msg );
	 }

	public static IrcMessage squit( String server, String from, String servername, String msg ) {
		return new IrcMessage( server, from, "SQUIT", "", servername, msg );
	}

	public static IrcMessage join( String server, String from, String channelname, String channelkey ) {
		return new IrcMessage( server, from, "JOIN", "", channelname + " " + channelkey, "" );
	}

	public static IrcMessage part( String server, String from, String channelname ) {
		return new IrcMessage( server, from, "PART", "", channelname, "" );
	}

	public static IrcMessage mode( String server, String from, String channel, String modeflags ) {
		return new IrcMessage( server, from, "MODE", channel, modeflags, "");
	}

	public static IrcMessage topic( String server, String from, String channel, String topic ) {
		return new IrcMessage( server, from, "TOPIC", channel, "", topic );
	}

	public static IrcMessage names( String server, String from, String channel ) {
		return new IrcMessage( server, from, "NAMES", channel, "", "");
	}
	
	public static IrcMessage list( String server, String from, String channel ) {
		return new IrcMessage( server, from, "LIST", channel, "", "");
	}

	public static IrcMessage invite( String server, String from, String nickname, String channel ) {
		return new IrcMessage( server, from, "INVITE", nickname, channel, "");
	}

	public static IrcMessage kick( String server, String from, String channel, String user, String info ) {
		return new IrcMessage( server, from, "KICK", channel, user, info );
	}

	public static IrcMessage version( String server, String from, String servername ) {
		return new IrcMessage( server, from, "VERSION", servername, "", "");
	}

	public static IrcMessage stats( String server, String from, String servername, String query ) {
		return new IrcMessage( server, from, "STATS", query, servername, "");
	}

	public static IrcMessage links( String server, String from, String servername, String mask ) {
		return new IrcMessage( server, from, "LINKS", servername, mask, "");
	}

	public static IrcMessage time( String server, String from, String servername ) {
		return new IrcMessage( server, from, "TIME", servername, "", "");
	}

	public static IrcMessage connect( String server, String from, String targetserver, String port, String remoteserver ) {
		return new IrcMessage( server, from, "CONNECT", targetserver + " " + port, remoteserver, "");
	}

	public static IrcMessage trace( String server, String from, String servername ) {
		return new IrcMessage( server, from, "TRACE", servername, "", "");
	}

	public static IrcMessage admin( String server, String from, String servername ) {
		return new IrcMessage( server, from, "ADMIN", servername, "", "");
	}

	public static IrcMessage info( String server, String from, String servername ) {
		return new IrcMessage( server, from, "INFO", servername, "", "");
	}

	public static IrcMessage privmsg( String server, String from, String to, String msg ) {
		return new IrcMessage( server, from, "PRIVMSG", to, "", msg );
	}

	public static IrcMessage notice(String server, String from, String nickname, String text ) {
		return new IrcMessage( server, from, "NOTICE", nickname, "", text);
	}

	public static IrcMessage who(String server, String from, String name ) {
		return new IrcMessage( server, from, "WHO", name, "", "");
	}

	public static IrcMessage whois(String server, String from, String servername, String nickmask ) {
		return new IrcMessage( server, from, "WHOIS", servername, nickmask, "");
	}

	/**
	 * count & servername may be ommited
	 */
	public static IrcMessage whowas(String server, String from, String nickname, String count, String servername ) {
		return new IrcMessage( server, from, "WHOWAS", nickname, count + " " + servername, "");
	}

	public static IrcMessage kill(String server, String from, String nickname, String comment) {
		return new IrcMessage( server, from, "KILL", nickname, "", comment);
	}

	public static IrcMessage ping(String server, String from, String servername, String time) {
		return new IrcMessage( server, from, "PING", servername, "", time);
	}

	public static IrcMessage pong(String server, String from, String time) {
		return new IrcMessage( server, from, "PONG", time, "", "");
	}

	
	public static IrcMessage away(String server, String from, String msg) {
		return new IrcMessage( server, from, "AWAY", "", "", msg);
	}

	public static IrcMessage rehash(String server, String from ) {
		return new IrcMessage( server, from, "REHASH", "", "", "");
	}

	public static IrcMessage restart(String server, String from ) {
		return new IrcMessage( server, from, "RESTART", "", "", "");
	}

	public static IrcMessage summon(String server, String from, String user, String servername) {
		return new IrcMessage( server, from, "SUMMON", user, servername, "");
	}

	public static IrcMessage users(String server, String from, String servername) {
		return new IrcMessage( server, from, "USERS", servername, "", "");
	}

	public static IrcMessage wallops(String server, String from, String msg) {
		return new IrcMessage( server, from, "WALLOPS", "", "", msg);
	}

	public static IrcMessage userhost(String server, String from, String username ) {
		return new IrcMessage( server, from, "USERHOST", "", username, "");
	}

	public static IrcMessage ison(String server, String from, String nickname) {
		return new IrcMessage( server, from, "ISON", "", nickname, "");
	}
}
