/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.net.Socket;

/**
 * A client message is send from a client application to the server.
 *
 * @author Jaco de Groot
 */
public class ClientMessage extends Message {
    public final static int PASS = 2000;
    public final static int USER = 2001;
    public final static int WHO = 2002;
    public final static int LIST = 2003;
    public final static int WHOIS = 2004;
    public final static int OPER = 2005;
    public final static int DIE = 2006;
    public final static int USERHOST = 2007;
    public final static int START = 3000; // Only for incoming translator to server to make the chatengine aware of the new connection.
    public final static int STOP = 3001; // Only for incoming translator to server to make the chatengine aware of the closed socket.
    private String unkownCommand;

    private Socket sender;

    public void setSender(Socket sender) {
        this.sender = sender;
    }

    public void setUnkownCommand(String unkownCommand) {
        this.unkownCommand = unkownCommand;
    }

    public String getUnkownCommand() {
        return unkownCommand;
    }
    
    public Socket getSender() {
        return sender;
    }

    public static int getCommandId(String command) {
        int commandId = Message.getCommandId(command);
        if (commandId == -1) {
            if ("PASS".equalsIgnoreCase(command)) {
                commandId = ClientMessage.PASS;
            } else if ("USER".equalsIgnoreCase(command)) {
                commandId = ClientMessage.USER;
            } else if ("WHO".equalsIgnoreCase(command)) {
                commandId = ClientMessage.WHO;
            } else if ("LIST".equalsIgnoreCase(command)) {
                commandId = ClientMessage.LIST;
            } else if ("WHOIS".equalsIgnoreCase(command)) {
                commandId = ClientMessage.WHOIS;
            } else if ("OPER".equalsIgnoreCase(command)) {
                commandId = ClientMessage.OPER;
            } else if ("DIE".equalsIgnoreCase(command)) {
                commandId = ClientMessage.DIE;
            } else if ("USERHOST".equalsIgnoreCase(command)) {
                commandId = ClientMessage.USERHOST;
            }
        }
        return commandId;
    }

}
