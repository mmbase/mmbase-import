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
 * The message class contains information and methods common for both the client
 * message class and the server message class.
 *
 * @author Jaco de Groot
 */
public class Message {
    public final static int PING = 4000;
    public final static int PONG = 4001;
    public final static int PRIVMSG = 4002;
    public final static int NOTICE = 4003;
    public final static int JOIN = 4004;
    public final static int PART = 4005;
    public final static int QUIT = 4006;
    public final static int KICK = 4007;
    public final static int NICK = 4008;
    public final static int MODE = 4009;
    public final static int TOPIC = 4010;

    private String prefix;
    private int command;
    private Vector parameters = new Vector();

    Message() {
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public int getCommand() {
        return command;
    }

    public void addParameter(String parameter) {
        parameters.add(parameter);
    }

    public Vector getParameters() {
        return parameters;
    }

    /**
     * Returns the parameter at the specified index or null if the specified
     * parameter doesn't exist.
     *
     * @return  the parameter at the specified index or null if the specified
     *          parameter doesn't exist.
     */
    public String getParameter(int number) {
        if (number < parameters.size()) {
            return (String)parameters.elementAt(number);
        } else {
            return null;
        }
    }

    public static int getCommandId(String command) {
        int commandId = -1;
        if ("PING".equalsIgnoreCase(command)) {
            commandId = Message.PING;
        } else if ("PONG".equalsIgnoreCase(command)) {
            commandId = Message.PONG;
        } else if ("PRIVMSG".equalsIgnoreCase(command)) {
            commandId = Message.PRIVMSG;
        } else if ("NOTICE".equalsIgnoreCase(command)) {
            commandId = Message.NOTICE;
        } else if ("JOIN".equalsIgnoreCase(command)) {
            commandId = Message.JOIN;
        } else if ("PART".equalsIgnoreCase(command)) {
            commandId = Message.PART;
        } else if ("QUIT".equalsIgnoreCase(command)) {
            commandId = Message.QUIT;
        } else if ("KICK".equalsIgnoreCase(command)) {
            commandId = Message.KICK;
        } else if ("NICK".equalsIgnoreCase(command)) {
            commandId = Message.NICK;
        } else if ("MODE".equalsIgnoreCase(command)) {
            commandId = Message.MODE;
        } else if ("TOPIC".equalsIgnoreCase(command)) {
            commandId = Message.TOPIC;
        }
        return commandId;
    }

    public static String getCommand(int commandId) {
        if (commandId == Message.PING) {
            return "PING";
        } else if (commandId == Message.PONG) {
            return "PONG";
        } else if (commandId == Message.PRIVMSG) {
            return "PRIVMSG";
        } else if (commandId == Message.NOTICE) {
            return "NOTICE";
        } else if (commandId == Message.JOIN) {
            return "JOIN";
        } else if (commandId == Message.PART) {
            return "PART";
        } else if (commandId == Message.QUIT) {
            return "QUIT";
        } else if (commandId == Message.KICK) {
            return "KICK";
        } else if (commandId == Message.NICK) {
            return "NICK";
        } else if (commandId == Message.MODE) {
            return "MODE";
        } else if (commandId == Message.TOPIC) {
            return "TOPIC";
        } else {
            return null;
        }
    }

}
