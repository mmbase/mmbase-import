/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

/**
 * See rfc 1459
 * 
 * Client support from : http://www.irchelp.org/irchelp/ircd/numerics.html 
 * 		E=EFnet hybrid 5.3p2
 * 		e=EFNet hybrid 5.3p2 with #ifdef
 *		I=IRCnet 2.10.0p5
 * 		i=IRCnet, reserved according to Undernet (sic) numerics header
 * 		U=Undernet u2.10.04
 * 		u=Undernet u2.10.04 with #ifdef
 * 		D=DALnet 4.6.7.DF
 * 		d=DALnet 4.6.7.DF, redundant
 */

package org.mmbase.module.irc.communication.irc;

import java.util.Hashtable;
import org.mmbase.util.logging.*;

public class IrcReplies
{
    private static Logger log = Logging.getLoggerInstance(IrcReplies.class.getName());
	private String 		classname 	= getClass().getName();
	private boolean 	assert 		= true; 
	private Hashtable 	intReplies;
	private Hashtable	stringReplies;
	private int 		maxReplies;

	public IrcReplies()
	{
		intReplies 		= new Hashtable();
		stringReplies 	= new Hashtable();

	
	/**
	 * Command responses
	 */

		putReply( new Integer( 001 ), "RPL_WELCOME" 		); // IEUD
		putReply( new Integer( 002 ), "RPL_YOURHOST" 		); // .
		putReply( new Integer( 003 ), "RPL_CREATED" 		); // .
		putReply( new Integer( 004 ), "RPL_MYINFO" 			); //
		putReply( new Integer( 005 ), "RPL_MAP" 			); // U
//		putReply( new Integer( 005 ), "RPL_BOUNCE" 			); // I
//		putReply( new Integer( 005 ), "RPL_PROTOCL"			); // D
		putReply( new Integer( 006 ), "RPL_MAPMORE" 		); // U
		putReply( new Integer( 007 ), "RPL_MAPEND" 			); // .
		putReply( new Integer( 8 ), "RPL_SNOMASK" 		); // .
		putReply( new Integer( 9 ), "RPL_STATMEMTOT" 		); //
		putReply( new Integer( 010 ), "RPL_STATMEM" 		); //

		putReply( new Integer( 014 ), "RPL_YOURCOOKIE" 		); // I

		putReply( new Integer( 200 ), "RPL_TRACELINK" 		); // IEUD
		putReply( new Integer( 201 ), "RPL_TRACECONNECTING"	); // .
		putReply( new Integer( 202 ), "RPL_TRACEHANDSHAKE" 	); // .
		putReply( new Integer( 203 ), "RPL_TRACEUNKNOWN" 	); //
		putReply( new Integer( 204 ), "RPL_TRACEOPERATOR" 	); //
		putReply( new Integer( 205 ), "RPL_TRACEUSER" 		); //
		putReply( new Integer( 206 ), "RPL_TRACESERVER" 	); //
		putReply( new Integer( 207 ), "RPL_TRACESERVICE" 	); //
		putReply( new Integer( 208 ), "RPL_TRACENEWTYPE" 	); //
		putReply( new Integer( 209 ), "RPL_TRACECLASS" 		); // reserved
		putReply( new Integer( 210 ), "RPL_TRACERECONNECT" 	); // I 
		putReply( new Integer( 211 ), "RPL_STATSLINKINFO" 	); // IEUD
		putReply( new Integer( 212 ), "RPL_STATSCOMMANDS" 	); // .
		putReply( new Integer( 213 ), "RPL_STATSCLINE" 		); // .
		putReply( new Integer( 214 ), "RPL_STATSNLINE" 		); //
		putReply( new Integer( 215 ), "RPL_STATSILINE" 		); //
		putReply( new Integer( 216 ), "RPL_STATSKLINE" 		); //
		putReply( new Integer( 217 ), "RPL_STATSQLINE" 		); // reserved
		putReply( new Integer( 218 ), "RPL_STATSYLINE" 		); //
		putReply( new Integer( 219 ), "RPL_ENDOFSTATS" 		); //
		putReply( new Integer( 221 ), "RPL_UMODEIS" 		); //

		putReply( new Integer( 222 ), "RPL_STATSBLINE" 		); // E
//		putReply( new Integer( 222 ), "RPL_SQLINE_NICK"		); // D

		putReply( new Integer( 223 ), "RPL_STATSELINE" 		); // E
		putReply( new Integer( 224 ), "RPL_STATSFLINE" 		); // E
		putReply( new Integer( 225 ), "RPL_STATSDLINE" 		); // E
		putReply( new Integer( 231 ), "RPL_SERVICEINFO" 	); // IUD  reserved
		putReply( new Integer( 232 ), "RPL_ENDOFSERVICE" 	); // IUD  reserved
		putReply( new Integer( 233 ), "RPL_SERVICE" 		); // IUD  reserved
		putReply( new Integer( 234 ), "RPL_SERVLIST" 		); // IEUD reserved
		putReply( new Integer( 235 ), "RPL_SERVLISTEND" 	); // IEUD reserved
		putReply( new Integer( 239 ), "RPL_STATSIAUTH" 		); // I  
		putReply( new Integer( 240 ), "RPL_STATSVLINE" 		); // I  
		putReply( new Integer( 241 ), "RPL_STATSLINE" 		); // IEUD
		putReply( new Integer( 242 ), "RPL_STATSUPTIME" 	); // IEUD
		putReply( new Integer( 243 ), "RPL_STATSOLINE" 		); // IEUD
		putReply( new Integer( 244 ), "RPL_STATSHLINE" 		); // IEUD
		putReply( new Integer( 245 ), "RPL_STATSSLINE" 		); // IEUD

		putReply( new Integer( 246 ), "RPL_STATSPING" 		); // I
//		putReply( new Integer( 246 ), "RPL_STATSTLINE" 		); // U

		putReply( new Integer( 247 ), "RPL_STATSBLINE" 		); // I
//		putReply( new Integer( 247 ), "RPL_STATSGLINE" 		); // U
//		putReply( new Integer( 247 ), "RPL_STATSXLINE" 		); // D

		putReply( new Integer( 248 ), "RPL_STATSDEFINE" 	); // I
//		putReply( new Integer( 248 ), "RPL_STATSULINE" 		); // UD

		putReply( new Integer( 249 ), "RPL_STATSDEBUG" 		); // IEUD
		putReply( new Integer( 250 ), "RPL_STATSDLINE" 		); // I

		putReply( new Integer( 251 ), "RPL_LUSERSCLIENT" 	); // eUD 
		putReply( new Integer( 252 ), "RPL_LUSEROP" 		); // IEUD  
		putReply( new Integer( 253 ), "RPL_LUSERUNKNOWN" 	); // .
		putReply( new Integer( 254 ), "RPL_LUSERCHANNELS" 	); // .
		putReply( new Integer( 255 ), "RPL_LUSERME" 		); // 
		putReply( new Integer( 256 ), "RPL_ADMINME" 		); // 
		putReply( new Integer( 257 ), "RPL_ADMINLOC1" 		); // 
		putReply( new Integer( 258 ), "RPL_ADMINLOC2" 		); // 
		putReply( new Integer( 259 ), "RPL_ADMINEMAIL" 		); // 
		putReply( new Integer( 261 ), "RPL_TRACELOG" 		); //

		putReply( new Integer( 262 ), "RPL_TRACEEND" 		); // IE
//		putReply( new Integer( 262 ), "RPL_TRACEPING" 		); // U

		putReply( new Integer( 263 ), "RPL_TRYAGAIN" 		); // I
//		putReply( new Integer( 263 ), "RPL_LOAD2HI" 		); // E

		putReply( new Integer( 265 ), "RPL_LOCALUSERS" 		); // eD
		putReply( new Integer( 266 ), "RPL_GLOBALUSERS" 	); // eD
		putReply( new Integer( 271 ), "RPL_SILELIST" 		); // UD
		putReply( new Integer( 272 ), "RPL_ENDOFALLSILELIST"); // UD
		putReply( new Integer( 274 ), "RPL_STATSDELTA" 		); // i
		putReply( new Integer( 275 ), "RPL_STATSDLINE" 		); // UD
		putReply( new Integer( 280 ), "RPL_GLIST" 			); // U
		putReply( new Integer( 281 ), "RPL_ENDOFGLIST" 		); // U
		putReply( new Integer( 290 ), "RPL_HELPHDR" 		); // D
		putReply( new Integer( 291 ), "RPL_HELPOP" 			); // D
		putReply( new Integer( 292 ), "RPL_HELPTLR" 		); // D
		putReply( new Integer( 293 ), "RPL_HELPHLP" 		); // D
		putReply( new Integer( 294 ), "RPL_HELPFWD" 		); // D
		putReply( new Integer( 295 ), "RPL_HELPPIGN" 		); // D

		putReply( new Integer( 300 ), "RPL_NONE" 			); // IEUD 
		putReply( new Integer( 301 ), "RPL_AWAY" 			); // .
		putReply( new Integer( 302 ), "RPL_USERHOST" 		); // .
		putReply( new Integer( 303 ), "RPL_ISON" 			); // 
		putReply( new Integer( 304 ), "RPL_TEXT" 			); // 
		putReply( new Integer( 305 ), "RPL_UNAWAY" 			); // 
		putReply( new Integer( 306 ), "RPL_NOWAWAY" 		); // 

		putReply( new Integer( 307 ), "RPL_USERIP" 			); // U
//		putReply( new Integer( 307 ), "RPL_WHOISREGNICK"	); // D

		putReply( new Integer( 308 ), "RPL_WHOISADMIN" 		); // d 
		putReply( new Integer( 309 ), "RPL_WHOISSADMIN"		); // d 
		putReply( new Integer( 310 ), "RPL_WHOISHELPOP"		); // D

		putReply( new Integer( 311 ), "RPL_WHOISUSER"	 	); // 
		putReply( new Integer( 312 ), "RPL_WHOISSERVER" 	); // 
		putReply( new Integer( 313 ), "RPL_WHOISOPERATOR" 	); // 
		putReply( new Integer( 314 ), "RPL_WHOWASUSER" 		); // 
		putReply( new Integer( 315 ), "RPL_ENDOFWHO" 		); // 
		putReply( new Integer( 316 ), "RPL_WHOISCHANOP" 	); // reserved
		putReply( new Integer( 317 ), "RPL_WHOISIDLE" 		); // 
		putReply( new Integer( 318 ), "RPL_ENDOFWHOIS" 		); // 
		putReply( new Integer( 319 ), "RPL_WHOISCHANNELS" 	); // 
		putReply( new Integer( 321 ), "RPL_LISTSTART" 		); // 
		putReply( new Integer( 322 ), "RPL_LIST" 			); // 
		putReply( new Integer( 323 ), "RPL_LISTEND" 		); // 
		putReply( new Integer( 324 ), "RPL_CHANNELMODEIS" 	); // 
		putReply( new Integer( 325 ), "RPL_CHANNELPASSIS" 	); // i
		putReply( new Integer( 326 ), "RPL_NOCHANPASS" 		); // i
		putReply( new Integer( 327 ), "RPL_CHPASSUNKNOWN" 	); // i 
		putReply( new Integer( 329 ), "RPL_CREATIONTIME" 	); // EUD 

		putReply( new Integer( 331 ), "RPL_NOTOPIC" 		); // IEUD 
		putReply( new Integer( 332 ), "RPL_TOPIC" 			); // IEUD 

		putReply( new Integer( 333 ), "RPL_TOPICWHOTIME" 	); // eUD 
		putReply( new Integer( 334 ), "RPL_LISTUSAGE" 		); // U 
//		putReply( new Integer( 334 ), "RPL_LISTSYNTAX" 		); // D
 
		putReply( new Integer( 338 ), "RPL_CHANPASSOK" 		); // i 
		putReply( new Integer( 339 ), "RPL_BASCHANPASS" 	); // i 
		putReply( new Integer( 341 ), "RPL_INVITING" 		); // IEUD 
		putReply( new Integer( 342 ), "RPL_SUMMONING" 		); // IED 
		putReply( new Integer( 346 ), "RPL_INVITELIST" 		); // I
		putReply( new Integer( 347 ), "RPL_ENDOFINVITELIST"	); // I
		putReply( new Integer( 348 ), "RPL_EXCEPTLIST" 		); // I
		putReply( new Integer( 349 ), "RPL_ENDOFEXCEPTLIST"	); // I
		putReply( new Integer( 351 ), "RPL_VERSION" 		); // IEUD 
		putReply( new Integer( 352 ), "RPL_WHOREPLY" 		); // IEUD
		putReply( new Integer( 353 ), "RPL_NAMREPLY" 		); // IEUD 
		putReply( new Integer( 354 ), "RPL_WHOSPCRPL" 		); // U 
		putReply( new Integer( 361 ), "RPL_KILLDONE" 		); // IEUD reserved
		putReply( new Integer( 362 ), "RPL_CLOSING" 		); // IEUD reserved
		putReply( new Integer( 363 ), "RPL_CLOSEEND" 		); // IEUD reserved
		putReply( new Integer( 364 ), "RPL_LINKS" 			); // .
		putReply( new Integer( 365 ), "RPL_ENDOFLINKS" 		); // . 
		putReply( new Integer( 366 ), "RPL_ENDOFNAMES" 		); // 
		putReply( new Integer( 367 ), "RPL_BANLIST" 		); // 
		putReply( new Integer( 368 ), "RPL_ENDOFBANLIST" 	); // 
		putReply( new Integer( 369 ), "RPL_ENDOFWHOWAS" 	); // 
		putReply( new Integer( 371 ), "RPL_INFO" 			); // 
		putReply( new Integer( 372 ), "RPL_MOTD" 			); // 
		putReply( new Integer( 373 ), "RPL_INFOSTART" 		); //  // reserved
		putReply( new Integer( 374 ), "RPL_ENDOFINFO" 		); // 
		putReply( new Integer( 375 ), "RPL_MOTDSTART" 		); // 
		putReply( new Integer( 376 ), "RPL_ENDOFMOTD" 		); // 
		putReply( new Integer( 381 ), "RPL_YOUREOPER" 		); // 
		putReply( new Integer( 382 ), "RPL_REHASHING" 		); // 
		putReply( new Integer( 383 ), "RPL_YOURESERVICE"	); // ID
		putReply( new Integer( 384 ), "RPL_MYPORTIS" 		); // IEUD reserved
		putReply( new Integer( 385 ), "RPL_NOTOPERANYMORE"	); // IEUD reserved

		putReply( new Integer( 391 ), "RPL_TIME" 			); // IEUD 
		putReply( new Integer( 392 ), "RPL_USERSSTART" 		); // IED 
		putReply( new Integer( 393 ), "RPL_USERS" 			); // IED
		putReply( new Integer( 394 ), "RPL_ENDOFUSERS" 		); // IED 
		putReply( new Integer( 395 ), "RPL_NOUSERS" 		); // IED 
 
	 /**
	 * Error replies 
	 */

		putReply( new Integer( 401 ), "ERR_NOSUCHNICK" 		); // IEUD
		putReply( new Integer( 402 ), "ERR_NOSUCHSERVER" 	); // . 
		putReply( new Integer( 403 ), "ERR_NOSUCHCHANNEL" 	); // . 
		putReply( new Integer( 404 ), "ERR_CANNOTSENDTOCHAN"); // 
		putReply( new Integer( 405 ), "ERR_TOOMANYCHANNELS" ); // 
		putReply( new Integer( 406 ), "ERR_WASNOSUCHNICK" 	); // 
		putReply( new Integer( 407 ), "ERR_TOOMANYTARGETS" 	); // 
		putReply( new Integer( 408 ), "ERR_NOSUCHSERVICE"	); // ID
		putReply( new Integer( 409 ), "ERR_NOORIGIN" 		); // IEUD
		putReply( new Integer( 411 ), "ERR_NORECIPIENT" 	); // .
		putReply( new Integer( 412 ), "ERR_NOTEXTTOSEND" 	); // .
		putReply( new Integer( 413 ), "ERR_NOTOPLEVEL" 		); // 
		putReply( new Integer( 414 ), "ERR_WILDTOPLEVEL" 	); // 
		putReply( new Integer( 415 ), "ERR_BADMASK" 		); // I
		putReply( new Integer( 416 ), "ERR_TOOMANYMATCHES" 	); // I
//		putReply( new Integer( 416 ), "ERR_QUERYTOOLONG" 	); // U 

		putReply( new Integer( 421 ), "ERR_UNKNOWNCOMMAND" 	); // IEUD 
		putReply( new Integer( 422 ), "ERR_NOMOTD" 			); // .
		putReply( new Integer( 423 ), "ERR_NOADMININFO" 	); // .
		putReply( new Integer( 424 ), "ERR_FILEERROR" 		); // 
		putReply( new Integer( 431 ), "ERR_NONICKNAMEGIVEN" ); // 
		putReply( new Integer( 432 ), "ERR_ERRONEUSNICKNAME"); // 
		putReply( new Integer( 433 ), "ERR_NICKNAMEINUSE" 	); // 

		putReply( new Integer( 434 ), "ERR_SERVICENAMEINUSE"); // ID
		putReply( new Integer( 435 ), "ERR_SERVICECONFUSED" ); // ID
		putReply( new Integer( 436 ), "ERR_NICKCOLLISION" 	); // IEUD 
		putReply( new Integer( 437 ), "ERR_UNAVAILABLERESOURCE"); // I
//		putReply( new Integer( 437 ), "ERR_BANNICKCHANGE" 	); // UD 
		putReply( new Integer( 438 ), "ERR_NICKTOOFAST" 	); // U 
//		putReply( new Integer( 438 ), "ERR_NCHANGETOOFAST" 	); // D 
		putReply( new Integer( 439 ), "ERR_TARGETTOOFAST" 	); // UD 
		putReply( new Integer( 440 ), "ERR_SERVICEDOWN" 	); // D

		putReply( new Integer( 441 ), "ERR_USERNOTINCHANNEL"); // IEUD
		putReply( new Integer( 442 ), "ERR_NOTONCHANNEL" 	); // .
		putReply( new Integer( 443 ), "ERR_USERONCHANNEL" 	); // .
		putReply( new Integer( 444 ), "ERR_NOLOGIN" 		); // 
		putReply( new Integer( 445 ), "ERR_SUMMONDISABLED" 	); // 
		putReply( new Integer( 446 ), "ERR_USERSDISABLED" 	); // 
		putReply( new Integer( 451 ), "ERR_NOTREGISTERED" 	); // 
		putReply( new Integer( 452 ), "ERR_IDCOLLISION" 	); // i
		putReply( new Integer( 453 ), "ERR_NICKLOST" 		); // i
		putReply( new Integer( 455 ), "ERR_HOSTILENAME" 	); // D
		putReply( new Integer( 461 ), "ERR_NEEDMOREPARAMS" 	); // IEUD
		putReply( new Integer( 462 ), "ERR_ALREADYREGISTERED"); // .
		putReply( new Integer( 463 ), "ERR_NOPERMFORHOST" 	); // .
		putReply( new Integer( 464 ), "ERR_PASSWDMISMATCH" 	); // 
		putReply( new Integer( 465 ), "ERR_YOUREBANNEDCREEP"); // 
		putReply( new Integer( 466 ), "ERR_YOUWILLBEBANNED" ); // reserved
		putReply( new Integer( 467 ), "ERR_KEYSET" 			); // 

		putReply( new Integer( 468 ), "ERR_INVALIDUSERNAME"	); // U
//		putReply( new Integer( 468 ), "ERR_ONLYSERVERCANCHANGE"); // D 

		putReply( new Integer( 471 ), "ERR_CHANNELLISTFULL" ); // IEUD
		putReply( new Integer( 472 ), "ERR_UNKNOWNMODE" 	); // .
		putReply( new Integer( 473 ), "ERR_INVITEONLYCHANNEL"); // .
		putReply( new Integer( 474 ), "ERR_BANNEDFROMCHANNEL"); // 
		putReply( new Integer( 475 ), "ERR_BADCHANNELKEY" 	); // 
		putReply( new Integer( 476 ), "ERR_BADCHANMASK" 	); // reserved

		putReply( new Integer( 477 ), "ERR_MODELESS" 		); // U
//		putReply( new Integer( 477 ), "ERR_NEEDREGGEDNICK" 	); // D

		putReply( new Integer( 481 ), "ERR_NOPRIVILEGES" 	); // IEUD
		putReply( new Integer( 482 ), "ERR_CHANOPRIVSNEEDED"); // IEUD 
		putReply( new Integer( 483 ), "ERR_CANTKILLSERVER" 	); // EUD

		putReply( new Integer( 484 ), "ERR_DESYNC" 			); // E
//		putReply( new Integer( 484 ), "ERR_ISCHANSERVICE" 	); // U
		putReply( new Integer( 487 ), "ERR_CHANTOORECENT" 	); // i
		putReply( new Integer( 488 ), "ERR_TSLESSCHAN"		); // i 
 
		putReply( new Integer( 491 ), "ERR_NOOPERHOST" 		); // IEUD 
		putReply( new Integer( 492 ), "ERR_NOSERVICEHOST" 	); // ID reserved
		putReply( new Integer( 501 ), "ERR_UMODEUNKNOWNFLAG"); // IEUD 
		putReply( new Integer( 502 ), "ERR_USERSDONTMATCH" 	); // IEUD

		putReply( new Integer( 503 ), "ERR_GHOSTEDCLIENT" 	); // E 
		putReply( new Integer( 504 ), "ERR_LAST_ERR_MSG" 	); // E
		putReply( new Integer( 511 ), "ERR_SILELISTFULL" 	); // UD
		putReply( new Integer( 512 ), "ERR_NUSUCHGLINE" 	); // U
//		putReply( new Integer( 512 ), "ERR_TOOMANYWATCH" 	); // D

		putReply( new Integer( 513 ), "ERR_BADPING" 		); // U
//		putReply( new Integer( 513 ), "ERR_NEEDPONG" 		); // D
		putReply( new Integer( 521 ), "ERR_LISTSYNTAX" 		); // D

		putReply( new Integer( 600 ), "RPL_LOGON" 			); // D
		putReply( new Integer( 601 ), "RPL_LOGOFF" 			); // D
		putReply( new Integer( 602 ), "RPL_WATCHOFF" 		); // D
		putReply( new Integer( 603 ), "RPL_WATCHSTAT" 		); // D
		putReply( new Integer( 604 ), "RPL_NOWON" 			); // D
		putReply( new Integer( 605 ), "RPL_NOWOFF" 			); // D
		putReply( new Integer( 606 ), "RPL_WATCHLIST" 		); // D
		putReply( new Integer( 607 ), "RPL_ENDOFWATCHLIST" 	); // D

	 	maxReplies = 607;
	}

	private void putReply( Integer i, String s )
	{
		intReplies.put( s, i );
		stringReplies.put( i, s );
	}


	// from number to string methods

	/**
	 * Check if number equals to reply
	 */
	public boolean checkReply( int number, String reply )
	{
		if (assert)
		{
			if (number < 0 )
				log.debug( "checkReply("+number+"): Number is negative!"); 
			if (number > maxReplies)
				log.debug( "checkReply("+number+"): Number is greater than max ("+maxReplies+").");
		}

		boolean result = false;

		if (hasMoreStringReplies(number))
		{
			String replies[] = getMoreStringReplies(number); 
			for (int i = 0 ; i < replies.length; i++)
			{
				if (replies[i].equals( reply ))
					result = true;
			}
		}
		else
		{
			if (getStringReply( number ).equals(reply))
				result = true;
		}

		return result;		
	}

	public String getStringReply( String number )
	{
		String result = number;
		
		try
		{
			Integer i = new Integer( number );
			result = getStringReply(i.intValue());

		}
		catch( NumberFormatException e)
		{
			
		}
		return result;
	}
	

	public String getStringReply( int number )
	{
		if (assert)
		{
			if (number < 0 )
				log.debug( "getStringReply("+number+"): Number is negative!"); 
			if (number > maxReplies)
				log.debug( "getStringReply("+number+"): Number is greater than max ("+maxReplies+").");
		}

		Integer i = new Integer( number );

		if (stringReplies.containsKey(i)) 	
			return (String) stringReplies.get( i );
		else
		{	
			log.debug("getStringReply("+ number +"): Parameter reply not found in stringReplies! (Unknown reply from server)");
			return "";
		}
	}

	private boolean hasMoreStringReplies( int number )
	{
		if (assert)
		{
			if (number < 0 )
				log.debug( "hasMoreStringReplies("+number+"): Number is negative!"); 
			if (number > maxReplies)
				log.debug( "hasMoreStringReplies("+number+"): Number is greater than max ("+maxReplies+").");
		}

		switch( number )
		{
			case 	005	: return true; 
			case	222	: return true;
			case	246	: return true;
			case	247	: return true;
			case	248	: return true;
			case	262	: return true;
			case	263	: return true;
			case	307	: return true;
			case	334	: return true;
			case	416	: return true;
			case	437	: return true;
			case	438	: return true;
			case	468	: return true;
			case	477	: return true;
			case	484	: return true;
			case	512	: return true;
			case	513	: return true;

			default		: return false;
		}

	}

	private String[] getMoreStringReplies( int number )
	{
		String[] result = new String[2];

		if (assert)
		{
			if (number < 0 )
				log.debug( "getMoreStringReplies("+number+"): Number is negative!"); 
			if (number > maxReplies)
				log.debug( "getMoreStringReplies("+number+"): Number is greater than max ("+maxReplies+").");

			if ( !hasMoreStringReplies( number ) )
			{
				log.error("getMoreStringReplies(" + number + "): ERROR: This reply has only one reply (do not use this method!)" );
			}
		}

		
		if (	number == 005 )
			{
				result = new String[3];
				result[0] = "RPL_MAP";
				result[1] = "RPL_BOUNCE";	
				result[2] = "RPL_PROTOCL";	
			}
		else
			if ( number == 222 )
			{
				result[0] = "RPL_STATSBLINE";
				result[1] = "RPL_LINE_NICK";	
			}
		else
			if ( number == 246 )
			{
				result[0] = "RPL_STATSPING";
				result[1] = "RPL_STATSTLINE";	
			}
		else
			if ( number == 247 )
			{
				result = new String[3];
				result[0] = "RPL_STATSBLINE";
				result[1] = "RPL_STATSGLINE";	
				result[2] = "RPL_STATSXLINE";	
			}
		else
			if ( number == 248 )
			{
				result[0] = "RPL_STATSDEFINE";
				result[1] = "RPL_STATSULINE";	
			}
		else
			if ( number == 262 )
			{
				result[0] = "RPL_TRACEEND";
				result[1] = "RPL_TRACEPING";	
			}
		else
			if ( number == 263 )
			{
				result[0] = "RPL_TRYAGAIN";
				result[1] = "RPL_LOAD2HIGH";	
			}
		else
			if ( number == 307 )
			{
				result[0] = "RPL_USERIP";
				result[1] = "RPL_WHOISREGNICK";	
			}
		else
			if ( number == 334 )
			{
				result[0] = "RPL_LISTUSAGE";
				result[1] = "RPL_LISTSYNTAX";	
			}
		else
			if ( number == 416 )
			{
				result[0] = "ERR_TOOMANYMACHTES";
				result[1] = "ERR_QUERYTOOLONG";	
			}
		else
			if ( number == 437 )
			{
				result[0] = "ERR_UNAVAILABLERESOURCE";
				result[1] = "ERR_BANNICKCHANGE";	
			}
		else
			if ( number == 438 )
			{
				result[0] = "ERR_NICKTOOFAST";
				result[1] = "ERR_NCHANGETOOFAST";	
			}
		else
			if ( number == 468 )
			{
				result[0] = "ERR_INVALIDUSERNAME";
				result[1] = "ERR_ONLYSERVERCANCHANGE";	
			}
		else
			if ( number == 477 )
			{
				result[0] = "ERR_MODELESS";
				result[1] = "ERR_NEEDREGGEDNICK";	
			}
		else
			if ( number == 484 )
			{
				result[0] = "ERR_DESYNC";
				result[1] = "ERR_ISCHANSERVICE";	
			}
		else
			if ( number == 512 )
			{
				result[0] = "ERR_NOSUCHGLINE";
				result[1] = "ERR_TOOMANYWATCH";	
			}
		else
			if ( number == 513 )
			{
				result[0] = "ERR_BADPING";
				result[1] = "ERR_NEEDPONG";	
			}

		return result;		
	}


	// String to number methods

	public int getIntReply( String reply )
	{
		reply = reply.trim();

		if (assert)
		{
			if (reply == null)
				log.debug( "getIntReply("+reply+"): Parameter reply is null!" );
			if (reply.equals(""))
				log.debug( "getIntReply("+reply+"): Parameter reply is empty!");
		}

		if (intReplies.containsKey(reply))
			return ((Integer)intReplies.get( reply )).intValue();
		else
		{
					if ( reply.equals( "RPL_MAP" 					) || reply.equals( "RPL_BOUNCE" 			) || reply.equals( "RPL_PROTOCL" 	) 	) return 005;
			else	if ( reply.equals( "RPL_STATSBLINE" 			) || reply.equals( "RPL_SQLINE_NICK" 		) 										) return 222;
			else	if ( reply.equals( "RPL_STATSPING" 				) || reply.equals( "RPL_STATSTLINE" 		) 										) return 246;
			else	if ( reply.equals( "RPL_STATSBLINE" 			) || reply.equals( "RPL_STATSGLINE" 		) || reply.equals( "RPL_STATSXLINE" ) 	) return 247;
			else	if ( reply.equals( "RPL_STATSDEFINE"			) || reply.equals( "RPL_STATSULINE" 		) 										) return 248;
			else	if ( reply.equals( "RPL_TRACEEND" 				) || reply.equals( "RPL_TRACEPING" 			)	 									) return 262;
			else	if ( reply.equals( "RPL_TRYAGAIN" 				) || reply.equals( "RPL_LOAD2HIGH" 			) 										) return 263;
			else	if ( reply.equals( "RPL_USERIP" 				) || reply.equals( "RPL_WHOISREGNICK"		) 										) return 307;
			else	if ( reply.equals( "RPL_LISTUSAGE" 				) || reply.equals( "RPL_LISTSYNTAX"			) 										) return 334;
			else	if ( reply.equals( "ERR_TOOMANYMATCHES" 		) || reply.equals( "ERR_QUERYTOOLONG" 		) 										) return 416;
			else	if ( reply.equals( "ERR_UNAVAILABLERESOURCE"	) || reply.equals( "ERR_BANNICKCHANGE" 		) 										) return 437;
			else	if ( reply.equals( "ERR_NICKTOOFAST" 			) || reply.equals( "ERR_NCHANGETOOFAST" 	) 										) return 438;
			else	if ( reply.equals( "ERR_INVALIDUSERNAME" 		) || reply.equals( "ERR_ONLYSERVERCANCHANGE") 										) return 468;
			else	if ( reply.equals( "ERR_MODELESS" 				) || reply.equals( "ERR_NEEDREGGEDNICK" 	) 										) return 477;
			else	if ( reply.equals( "ERR_DESYNC" 				) || reply.equals( "ERR_ISCHANSERVICE" 		) 										) return 484;
			else	if ( reply.equals( "ERR_NOSUCHGLINE" 			) || reply.equals( "ERR_TOOMANYWATCH" 		) 										) return 512;
			else	if ( reply.equals( "ERR_BADPING" 				) || reply.equals( "ERR_NEEDPONG" 			) 										) return 513;
			else	{ log.warn( "getIntReply("+reply+"): Parameter reply not found in intReplies! (Unknown reply from server)"); return 0; } 
		}
	}

	public static void main( String args[] )
	{
		IrcReplies ip = new IrcReplies();
		System.out.println("301 = AWAY?	 = " + ip.checkReply( 301 ,"RPL_AWAY" ));
		System.out.println("RPL_SERVICE  = " + ip.getIntReply( "RPL_SERVICE" ));
		System.out.println("RPL_NEEDPONG = " + ip.getIntReply( "ERR_NEEDPONG" ));
	}
}
