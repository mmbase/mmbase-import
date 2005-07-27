/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat.repository.irc;

import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import nl.eo.chat.ChatEngine;
import nl.eo.chat.repository.*;

/**
 * Basic IRC implementation of the user repository.
 *
 * @author Jaco de Groot
 */
public class IrcUserRepository implements UserRepository {
    protected HashMap registeredSockets = new HashMap(); // socket -> user
    protected HashMap unregisteredSockets = new HashMap(); // socket -> user
    protected HashMap registeredNicks = new HashMap(); // nick (in lower case) -> user
    protected HashMap unregisteredNicks = new HashMap(); // nick (in lower case) -> user
    protected HashMap lastCommandRecieved = new HashMap(); // socket -> date
    protected HashMap lastPingSend = new HashMap(); // socket -> date
    private String operatorUsername;
    private String operatorPassword;
    private String administratorUsername;
    private String administratorPassword;
    protected ChatEngine engine;
    
    public void init(ChatEngine e) {
        engine = e;
    }

    public void connect(Socket socket, String hostname) {
        IrcUser user = new IrcUser();
        user.setSocket(socket);
        user.setHostname(hostname);
        unregisteredSockets.put(socket, user);
    }

    public String getHostname(Socket socket) {
        User user = (User)unregisteredSockets.get(socket);
        if (user == null) {
            return null;
        } else {
            return user.getHostname();
        }
    }

    public void setPass(Socket socket, String pass) {
        IrcUser user = (IrcUser)unregisteredSockets.get(socket);
        if (user == null) {
            user = new IrcUser();
            user.setSocket(socket);
            unregisteredSockets.put(socket, user);
        }
        user.setPass(pass);
    }
    
    public boolean setNick(Socket socket, String nick) {
        IrcUser user = (IrcUser)registeredNicks.get(nick.toLowerCase());
        if (user != null) {
            return false;
        }
        user = (IrcUser)unregisteredNicks.get(nick.toLowerCase());
        if (user != null) {
            return false;
        }
        user = (IrcUser)registeredSockets.get(socket);
        if (user != null) {
            String oldNick = user.getNick();
            registeredNicks.remove(oldNick.toLowerCase());
            user.setNick(nick);
            registeredNicks.put(nick.toLowerCase(), user);
            return true;
        }
        user = (IrcUser)unregisteredSockets.get(socket);
        if (user == null) {
            user = new IrcUser();
            user.setSocket(socket);
            unregisteredSockets.put(socket, user);
            user.setNick(nick);
            unregisteredNicks.put(nick.toLowerCase(), user);
            return true;
        } else {
            String oldNick = user.getNick();
            if (oldNick != null) {
                unregisteredNicks.remove(oldNick.toLowerCase());
            }
            user.setNick(nick);
            unregisteredNicks.put(nick.toLowerCase(), user);
            return true;
        }
    }
    
    public void setUsername(Socket socket, String username) {
        IrcUser user = (IrcUser)unregisteredSockets.get(socket);
        if (user == null) {
            user = new IrcUser();
            user.setSocket(socket);
            unregisteredSockets.put(socket, user);
        }
        user.setUsername(username);
    }
    
    public void setRealname(Socket socket, String realname) {
        IrcUser user = (IrcUser)unregisteredSockets.get(socket);
        if (user == null) {
            user = new IrcUser();
            user.setSocket(socket);
            unregisteredSockets.put(socket, user);
        }
        user.setRealname(realname);
    }
    
    public int register(Socket socket) {
        IrcUser user = (IrcUser)unregisteredSockets.get(socket);
        String nick = user.getNick();
        if (nick == null) {
            return UserRepository.REGISTER_NEED_NICK;
        }
        if (user.getHostname() == null) {
            return UserRepository.REGISTER_NEED_HOSTNAME;
        }
        if (user.getUsername() == null) {
            return UserRepository.REGISTER_NEED_USERNAME;
        }
        if (user.getRealname() == null) {
            return UserRepository.REGISTER_NEED_REALNAME;
        }
        unregisteredSockets.remove(socket);
        unregisteredNicks.remove(nick.toLowerCase());
        registeredSockets.put(socket, user);
        registeredNicks.put(nick.toLowerCase(), user);
        return UserRepository.REGISTER_OK;
    }

    public boolean isRegistered(Socket socket) {
        if (registeredSockets.get(socket) == null) {
            return false;
        } else {
            return true;
        }
    }

    public int registerAsOperator(User user, String username, String password) {
        if (username.equals(administratorUsername) && password.equals(administratorPassword)) {
            return UserRepository.REGISTER_AS_ADMINISTRATOR_OK;
        } else if (username.equals(operatorUsername) && password.equals(operatorPassword)) {
            return UserRepository.REGISTER_AS_OPERATOR_OK;
        } else {
            return UserRepository.REGISTER_AS_OPERATOR_INCORRECT_PASSWORD;
        }
    }

    public User getUser(String nick) {
        return (User)registeredNicks.get(nick.toLowerCase());
    }

    public User getUser(Socket socket) {
        return (User)registeredSockets.get(socket);
    }

    public String getUnregisteredNick(Socket socket) {
        User user = (User)unregisteredSockets.get(socket);
        if (user == null) {
            return null;
        } else {
            return user.getNick();
        }
    }

    public void disconnect(Socket socket) {
        engine.log.debug("<disconnect-pre>");
        engine.log.debug("REGISTERED SOCKETS: " + registeredSockets.size());
        engine.log.debug("REGISTERED NICKS: " + registeredNicks.size());
        engine.log.debug("UNREGISTERED SOCKETS: " + unregisteredSockets.size());
        engine.log.debug("UNREGISTERED NICKS: " + unregisteredNicks.size());
        engine.log.debug("LAST COMMAND RECIEVED: " + lastCommandRecieved.size());
        engine.log.debug("LAST PING SEND: " + lastPingSend.size());
        engine.log.debug("</disconnect-pre>");
        if (lastPingSend.remove(socket) == null) {
            lastCommandRecieved.remove(socket);
        }
        IrcUser user = (IrcUser)registeredSockets.get(socket);
        if (user == null) {
            user = (IrcUser)unregisteredSockets.get(socket);
            if (user != null) {
                String nick = user.getNick();
                if (nick != null) {
                    unregisteredNicks.remove(nick.toLowerCase());
                }
            }
            unregisteredSockets.remove(socket);
        } else {
            registeredNicks.remove(user.getNick().toLowerCase());
            registeredSockets.remove(socket);
        }
        engine.log.debug("<disconnect-post>");
        engine.log.debug("REGISTERED SOCKETS: " + registeredSockets.size());
        engine.log.debug("REGISTERED NICKS: " + registeredNicks.size());
        engine.log.debug("UNREGISTERED SOCKETS: " + unregisteredSockets.size());
        engine.log.debug("UNREGISTERED NICKS: " + unregisteredNicks.size());
        engine.log.debug("LAST COMMAND RECIEVED: " + lastCommandRecieved.size());
        engine.log.debug("LAST PING SEND: " + lastPingSend.size());
        engine.log.debug("</disconnect-post>");
    }

    public int numberOfRegisteredUsers() {
        return registeredSockets.size();
    }

    public int numberOfUnregisteredUsers() {
        return unregisteredSockets.size();
    }

    public int numberOfOperators() {
        int numberOfOperators = 0;
        Iterator i = registeredSockets.values().iterator();
        while (i.hasNext()) {
            User u = (User)i.next();
            if (u.isOperator()) {
                numberOfOperators++;
            }
        }
        return numberOfOperators;
    }

    public Date getLastCommandRecieved(Socket socket) {
        return (Date)lastCommandRecieved.get(socket);
    }
    
    public void setLastCommandRecieved(Socket socket, Date date) {
        lastPingSend.remove(socket);
        lastCommandRecieved.put(socket, date);
    }

    public void setLastPingSend(Socket socket, Date date) {
        lastCommandRecieved.remove(socket);
        lastPingSend.put(socket, date);
    }
    
    public Socket getInactiveSocket(boolean waitingForPong, Date since) {
        Socket socket = null;
        if (waitingForPong) {
            boolean stop = false;
            for (Iterator i = lastPingSend.keySet().iterator(); i.hasNext() && !stop; ) {
                socket = (Socket)i.next();
                Date date = (Date)lastPingSend.get(socket);
                if (date.before(since)) {
                    stop = true;
                } else {
                    socket = null;
                }
            }
        } else {
            boolean stop = false;
            for (Iterator i = lastCommandRecieved.keySet().iterator(); i.hasNext() && !stop; ) {
                socket = (Socket)i.next();
                Date date = (Date)lastCommandRecieved.get(socket);
                if (date.before(since)) {
                    stop = true;
                } else {
                    socket = null;
                }
            }
        }
        return socket;
    }
    
    public Set getRegisteredSockets() {
        return registeredSockets.keySet();
    }

    public Set getUnregisteredSockets() {
        return unregisteredSockets.keySet();
    }

    public void setOperatorUsername(String username) {
        operatorUsername = username;
    }
    
    public void setOperatorPassword(String password) {
        operatorPassword = password;
    }

    public void setAdministratorUsername(String username) {
        administratorUsername = username;
    }
    
    public void setAdministratorPassword(String password) {
        administratorPassword = password;
    }

    public boolean isValidNick(String nick) {
        // ( letter / special ) *8( letter / digit / special / "-" )
        if (nick.length() < 1 || nick.length() > 9) {
            return false;
        }
        char c = nick.charAt(0);
        if (!(Character.isLetter(c) || isSpecialChar(c)) || c == '@') {
            return false;
        }
        for (int i = 1; i < nick.length(); i++) {
            c = nick.charAt(i);
            if (!(Character.isLetterOrDigit(c) || isSpecialChar(c) || c == '-')) {
                return false;
            }
        }
        return true;
    }

    private boolean isSpecialChar(char c) {
        // %x5B-60 / %x7B-7D
        if ((c > '\u005A' && c < '\u0061') || (c > '\u007A' && c < '\u007E')) {
            return true;
        } else {
            return false;
        }
    }
    
}

