/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.net.Socket;
import java.util.Vector;

/**
 * A server message is send from a server to the client application.
 *
 * @author Jaco de Groot
 */
public class ServerMessage extends Message {
    Vector recipients;
    private boolean useLastParameterColon = false;
    
    public final static int RPL_WELCOME = 001;
    public final static int RPL_YOURHOST = 002;
    public final static int RPL_CREATED = 003;
    public final static int RPL_MYINFO = 004;

    public final static int RPL_UMODEIS = 221;

    public final static int RPL_LUSERCLIENT = 251;
    public final static int RPL_LUSEROP = 252;
    public final static int RPL_LUSERUNKNOWN = 253;
    public final static int RPL_LUSERCHANNELS = 254;
    public final static int RPL_LUSERME = 255;

    public final static int RPL_WHOISUSER = 311;
    public final static int RPL_WHOISSERVER = 312;
    public final static int RPL_WHOISOPERATOR = 313;
    public final static int RPL_WHOISIDLE = 317;
    public final static int RPL_ENDOFWHOIS = 318;

    public final static int RPL_ENDOFWHO = 315;

    public final static int RPL_LIST = 322;
    public final static int RPL_LISTEND = 323;
    public final static int RPL_CHANNELMODEIS = 324;
    public final static int RPL_NOTOPIC = 331;
    public final static int RPL_TOPIC = 332;
    public final static int RPL_NAMREPLY = 353;
    public final static int RPL_WHOREPLY = 352;

    public final static int RPL_ENDOFNAMES = 366;
    public final static int RPL_BANLIST = 367;
    public final static int RPL_ENDOFBANLIST = 368;

    public final static int RPL_MOTD = 372;
    public final static int RPL_MOTDSTART = 375;
    public final static int RPL_ENDOFMOTD = 376;

    public final static int RPL_YOUREOPER = 381;

    public final static int RPL_USERHOST = 302;

    public final static int ERR_NOSUCHSERVER = 402;
    public final static int ERR_NOSUCHCHANNEL = 403;
    public final static int ERR_CANNOTSENDTOCHAN = 404;
    public final static int ERR_NORECIPIENT = 411;
    public final static int ERR_NOSUCHNICK = 401;
    public final static int ERR_NOORIGIN = 409;
    public final static int ERR_NOTEXTTOSEND = 412;

    public final static int ERR_UNKNOWNCOMMAND = 421;

    public final static int ERR_NONICKNAMEGIVEN = 431;
    public final static int ERR_ERRONEUSNICKNAME = 432;
    public final static int ERR_NICKNAMEINUSE = 433;
    public final static int ERR_USERNOTINCHANNEL = 441;
    public final static int ERR_NOTONCHANNEL = 442;

    public final static int ERR_ALREADYREGISTRED = 462;
    
    public final static int ERR_RESTRICTED = 484;
    public final static int ERR_NEEDMOREPARAMS = 461;
    public final static int ERR_YOUREBANNEDCREEP = 465;

    public final static int ERR_PASSWDMISMATCH = 464;

    public final static int ERR_CHANNELISFULL = 471;
    public final static int ERR_UNKNOWNMODE = 472;
    public final static int ERR_BANNEDFROMCHAN = 474;

    public final static int ERR_NOPRIVILEGES = 481;
    public final static int ERR_CHANOPRIVSNEEDED = 482;
    
    public final static int ERR_NOOPERHOST = 491;

    public final static int ERR_USERSDONTMATCH = 502;

    public final static int ERROR_CLOSING_LINK = 1000;

    public void setRecipient(Socket recipient) {
        recipients = new Vector();
        recipients.add(recipient);
    }
    
    public void setRecipients(Vector recipients) {
        this.recipients = recipients;
    }

    public Vector getRecipients() {
        return recipients;
    }

    public static String getCommand(int commandId) {
        String command = Message.getCommand(commandId);
        if (command == null) {
            command = Integer.toString(commandId);
            if (command.length() == 1) {
                command = "00" + command;
            } else if (command.length() == 2) {
                command = "0" + command;
            }
        }
        return command;
    }
    
    // We need this IRC specific method because some IRC clients rely on the
    // colon to be present in the last parameter for some messages even if the
    // last parameter doesn't contain spaces.
    // Always adding a colon to the last parameter doesn't work either because
    // some clients rely on the colon not to be present in the last parameter
    // for some messages.
    public void setUseLastParameterColon(boolean useLastParameterColon) {
        this.useLastParameterColon = useLastParameterColon;
    }

    public boolean getUseLastParameterColon() {
        return useLastParameterColon;
    }
    
}
