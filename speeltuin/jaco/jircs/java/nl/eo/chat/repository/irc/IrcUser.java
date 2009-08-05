/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.irc;

import java.net.Socket;
import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import nl.eo.chat.repository.*;

/**
 * Basic IRC implementation of a user.
 *
 * @author Jaco de Groot
 */
public class IrcUser implements User {
    protected String pass;
    protected String hostname;
    protected String nick;
    protected String username;
    protected String realname;
    protected Vector channels = new Vector();
    protected Socket socket;
    protected String mode = "";
    protected boolean administrator = false;
    protected Date lastCommandRecieved;
    
    protected IrcUser() {
    }

    public String getUsername() {
        return username;
    }

    public String getRealname() {
        return realname;
    }
    
    public String getNick() {
        return nick;
    }

    public String getHostname() {
        return hostname;
    }
    
    public Collection getChannels() {
        return channels;
    }

    public Socket getSocket() {
        return socket;
    }

    public boolean isRestricted() {
        return false;
    }

    public boolean isOperator() {
        if (mode.indexOf('o') != -1) {
            return true;
        } else {
            return false;
        }
    }
    
    public void setOperator(boolean operator) {
        if (operator) {
            mode = mode + "o";
        } else {
            int i = mode.indexOf('o');
            if (i < mode.length() - 1) {
                mode = mode.substring(0, i) + mode.substring(i + 1);
            } else {
                mode = mode.substring(0, i);
            }
        }
    }

    public boolean isAdministrator() {
        return administrator;
    }
    
    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }
    
    protected void setPass(String pass) {
        this.pass = pass;
    }

    public String getPass() {
        return pass;
    }

    public void setLastCommandRecieved(Date date) {
        lastCommandRecieved = date;
    }

    public Date getLastCommandRecieved() {
        return lastCommandRecieved;
    }
    
    protected void setHostname(String hostname) {
        this.hostname = hostname;
    }

    protected void setNick(String nick) {
        this.nick = nick;
    }

    protected void setUsername(String username) {
        this.username = username;
    }
    
    protected void setRealname(String realname) {
        this.realname = realname;
    }

    protected void addChannel(Channel channel) {
        channels.add(channel);
    }

    protected void removeChannel(Channel channel) {
        channels.remove(channel);
    }
    
    protected void setSocket(Socket socket) {
        this.socket = socket;
    }

}

