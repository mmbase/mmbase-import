/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import nl.eo.chat.repository.*;

/**
 * The chat engine is responsible for reading messages from the incoming pool
 * and writing messages to the outgoing pool. The user repository and channel
 * repository will be used to read state information from. Changes in state
 * information will be writen back to the repositories.
 *
 * @author Jaco de Groot
 */
public class ChatEngine implements Runnable {
    private String propertiesFilename;
    private Repository repository;
    private UserRepository userRepository;
    private ChannelRepository channelRepository;
    public  Logger log; // Should only be used by code executed by the chat engine thread.
    private final static String version = "1.4.dev"; // Change this version also in build.xml
    private String servername;
    private String serverinfo;
    private String motd;
    private String date;
    private String defaultChannelModes = "n";
    private int defaultChannelUserLimit = -1;
    private boolean allowChannelCreationByUser = true;
    private boolean allowChannelOperationWhenNotOnChannel = true;
    private boolean kickOperatorAllowed = true;
    private boolean ignoreBanForOperator = false;
    private boolean channelOperatorMaySetRemoveChannelOperatorStatus = true;
    private final static String usermodes = "or";
    private final static String channelmodes = "blmnot";
    private Date checkSocketsAfter;
    private Date checkModeratedAfter;
    private Date checkChatOpenAfter;
    private boolean chatOpen = true;
    private boolean closeWarning15Done = false;
    private boolean closeWarning5Done = false;
    private boolean openWarning15Done = false;
    private boolean openWarning5Done = false;
    private Filter filter;
    private Filter nickFilter;
    protected IncomingMessagePool incomingMessagePool = new IncomingMessagePool();
    protected OutgoingMessagePool outgoingMessagePool = new OutgoingMessagePool();
    long freshFilters = 0;
    public boolean stop = false;

    protected ArrayList connectionBuilders = new ArrayList();
    protected ArrayList connectionBuilderThreads = new ArrayList();
    protected Pool incomingFlashTranslatorPool;
    protected Pool outgoingFlashTranslatorPool;
    protected Pool incomingIrcTranslatorPool;
    protected Pool outgoingIrcTranslatorPool;

    public void setLogger (Logger log) {
        this.log = log;
        if (incomingFlashTranslatorPool != null) {
            incomingFlashTranslatorPool.setLogger(log);
            outgoingFlashTranslatorPool.setLogger(log);
        }
        if (incomingIrcTranslatorPool != null) {
            incomingIrcTranslatorPool.setLogger(log);
            outgoingIrcTranslatorPool.setLogger(log);
        }
        incomingMessagePool.setLogger(log);
        outgoingMessagePool.setLogger(log);
    }

    public void setName( String name ) {
        servername = name;
    }

    public void setInfo( String info) {
        this.serverinfo = info;
    }

    public void setMotd( String motd) {
        this.motd = motd;
    }

    public void addFlashPort(String num) throws Exception {
        addFlashPort(Integer.parseInt(num));
    }

    
    public void addFlashPort( int portnum ) throws Exception {
        if (incomingFlashTranslatorPool == null) {
            incomingFlashTranslatorPool = new Pool(Class.forName("nl.eo.chat.IncomingFlashTranslator"), 5, -1, incomingMessagePool);
            outgoingFlashTranslatorPool = new Pool(Class.forName("nl.eo.chat.OutgoingFlashTranslator"), 5, -1, outgoingMessagePool);
        }      
        ConnectionBuilder connectionBuilder = new ConnectionBuilder(this);
        connectionBuilder.setPort(portnum);
        connectionBuilder.setIncomingTranslatorPool(incomingFlashTranslatorPool);
        connectionBuilder.setOutgoingTranslatorPool(outgoingFlashTranslatorPool);
        connectionBuilders.add(connectionBuilder);
    }

    
    public void addIrcPort(String num) throws Exception {
        addIrcPort(Integer.parseInt(num));
    }

    public void addIrcPort( int portnum ) throws Exception {
        if (incomingIrcTranslatorPool == null) {
            incomingIrcTranslatorPool = new Pool(Class.forName("nl.eo.chat.IncomingIrcTranslator"), 5, -1, incomingMessagePool);
            outgoingIrcTranslatorPool = new Pool(Class.forName("nl.eo.chat.OutgoingIrcTranslator"), 5, -1, outgoingMessagePool);
        }
        ConnectionBuilder connectionBuilder = new ConnectionBuilder(this);
        connectionBuilder.setPort(portnum);
        connectionBuilder.setIncomingTranslatorPool(incomingIrcTranslatorPool);
        connectionBuilder.setOutgoingTranslatorPool(outgoingIrcTranslatorPool);
        connectionBuilders.add(connectionBuilder);
    }

   

    public void setDefaultChannelModes(String m) {
        defaultChannelModes = m;
        if (defaultChannelModes == null) {
            defaultChannelModes = "d";
        }
    }

    public void setDefaultChannelUserLimit(String l) {
        defaultChannelUserLimit = Integer.parseInt(l);
    }
    
    public void setAllowChannelCreationByUser(boolean b) {
        allowChannelCreationByUser = b;
    }

    public void setAllowChannelOperationWhenNotOnChannel(boolean b) {
        allowChannelOperationWhenNotOnChannel = b;
    }

    public void setKickOperatorAllowed(boolean b) {
        kickOperatorAllowed = b;
    }
    
    public void setIgnoreBanForOperator(boolean b) {
        ignoreBanForOperator = b;
    }

    public void setChannelOperatorMaySetRemoveChannelOperatorStatus(boolean b) {
        channelOperatorMaySetRemoveChannelOperatorStatus = b;
    }

    public void setRepository(Repository r) throws Exception {
        repository = r;
   }


    protected synchronized void updateFilters() {
        long now = System.currentTimeMillis();
        if (now - freshFilters > 60 * 1000) {
            filter = repository.getFilter();
            nickFilter = repository.getNickFilter();
            freshFilters = System.currentTimeMillis();
        }
    }
    
    public void run() {
        if (log == null) {
            throw new RuntimeException("ChatEngine has no logger");
        }
        log.info("ChatEngine starting.");
        if (repository == null) {
            throw new RuntimeException("No repository loaded");
        }
        try {
            repository.init(this);
        }
        catch (Throwable e) {
            log.error(e.toString());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        userRepository = repository.getUserRepository();
        channelRepository = repository.getChannelRepository();
        checkSocketsAfter = new Date(System.currentTimeMillis());
        checkModeratedAfter = new Date(System.currentTimeMillis());
        checkChatOpenAfter = new Date(System.currentTimeMillis());
        SimpleDateFormat df = new SimpleDateFormat();
        date = df.format(new Date(System.currentTimeMillis()));
        filter = repository.getFilter();
        nickFilter = repository.getNickFilter();
        
        if (incomingFlashTranslatorPool != null) {
            incomingFlashTranslatorPool.setLogger(log);
            outgoingFlashTranslatorPool.setLogger(log);
        }
        if (incomingIrcTranslatorPool != null) {
            incomingIrcTranslatorPool.setLogger(log);
            outgoingIrcTranslatorPool.setLogger(log);
        }
        Iterator ci = connectionBuilders.iterator();
        while (ci.hasNext()) {
            ConnectionBuilder cb = (ConnectionBuilder) ci.next();
            Thread cbThread = new Thread(cb);
            cbThread.start();
            connectionBuilderThreads.add(cbThread);
        }
        
        while (!stop) {
            try {
                updateFilters();
                // Check "working hours" of channels.
                Date date = new Date(System.currentTimeMillis());
                if (date.after(checkChatOpenAfter)) {
                    log.debug("Check if chat server should be open or closed.");
                    checkChatOpenAfter = new Date(date.getTime() + 60000);
                    String message = null;
                    if (chatOpen) {
                        long millis = repository.close(date);
                        if (millis != -1) {
                            if (millis == 0) {
                                message = "This chat server is now closed";
                                chatOpen = false;
                                closeWarning15Done = false;
                                closeWarning5Done = false;
                            } else if (!closeWarning15Done && millis < 15 * 60 * 1000 && millis >= 14 * 60 * 1000) {
                                message = "This chat server will close in less than 15 minitues";
                                closeWarning15Done = true;
                            } else if (!closeWarning5Done && millis < 5 * 60 * 1000 && millis >= 4 * 60 * 1000) {
                                message = "This chat server will close in less than 5 minitues";
                                closeWarning5Done = true;
                            }
                        }
                    } else {
                        long millis = repository.open(date);
                        if (millis != -1) {
                            if (millis == 0) {
                                message = "This chat server is now open";
                                chatOpen = true;
                                openWarning15Done = false;
                                openWarning5Done = false;
                            } else if (!openWarning15Done && millis < 15 * 60 * 1000) {
                                message = "This chat server will open in less than 15 minitues";
                                openWarning15Done = true;
                            } else if (!openWarning5Done && millis < 5 * 60 * 1000) {
                                message = "This chat server will open in less than 5 minitues";
                                openWarning5Done = true;
                            }
                        }
                    }
                    if (message != null) {
                        Iterator iterator;
                        iterator = userRepository.getRegisteredSockets().iterator();
                        while (iterator.hasNext()) {
                            Socket s = (Socket)iterator.next();
                            User user = userRepository.getUser(s);
                            ServerMessage serverMessage = new ServerMessage();
                            serverMessage.setPrefix(servername);
                            serverMessage.setCommand(Message.NOTICE);
                            serverMessage.addParameter(user.getNick());
                            serverMessage.addParameter(message);
                            serverMessage.setRecipient(user.getSocket());
                            outgoingMessagePool.putMessage(serverMessage);
                        }
                    }
                    log.debug("Done.");
                }
                if (date.after(checkModeratedAfter)) {
                    log.debug("Check if channels should be moderated or not.");
                    checkModeratedAfter = new Date(date.getTime() + 60000);
                    Iterator i = channelRepository.getChannels().iterator();
                    while (i.hasNext()) {
                        Channel channel = (Channel)i.next();
                        String mode = null;
                        int result = channel.moderatedChange(date);
                        if (!channel.isModerated() && result == Channel.MODERATED_MODE_ON) {
                            channel.setModerated(true);
                            mode = "+m";
                        } else if (channel.isModerated() && result == Channel.MODERATED_MODE_OFF) {
                            channel.setModerated(false);
                            mode = "-m";
                        }
                        if (mode != null) {
                            Iterator iterator = channel.getUsers().iterator();
                            while (iterator.hasNext()) {
                                User u = (User)iterator.next();
                                ServerMessage serverMessage = new ServerMessage();
                                serverMessage.setPrefix(servername);
                                serverMessage.setCommand(Message.MODE);
                                serverMessage.addParameter(channel.getName());
                                serverMessage.addParameter(mode);
                                serverMessage.setRecipient(u.getSocket());
                                outgoingMessagePool.putMessage(serverMessage);
                            }
                        }
                    }
                    log.debug("Done.");
                }
                // Check all sockets.
                if (date.after(checkSocketsAfter)) {
                    log.debug("List all sockets:");
                    checkSocketsAfter = new Date(date.getTime() + 60000);
                    int i;
                    Iterator iterator;
                    i = 0;
                    iterator = userRepository.getRegisteredSockets().iterator();
                    while (iterator.hasNext()) {
                        i++;
                        Socket socket = (Socket)iterator.next();
                        User user = userRepository.getUser(socket);
                        log.debug(i + ". " + user.getNick() + "!"
                                  + user.getUsername() + "@"
                                  + user.getHostname());
                    }
                    log.debug("Listed " + i + " registered sockets.");
                    i = 0;
                    iterator = userRepository.getUnregisteredSockets().iterator();
                    while (iterator.hasNext()) {
                        i++;
                        Socket socket = (Socket)iterator.next();
                        User user = userRepository.getUser(socket);
                        log.debug(i + ". "
                                  + userRepository.getUnregisteredNick(socket)
                                  + "@" + userRepository.getHostname(socket));
                    }
                    log.debug("Listed " + i + " unregistered sockets.");
                }
                // Check for incoming messages.
                ClientMessage clientMessage = (ClientMessage)incomingMessagePool.getMessage();
                if (clientMessage == null) {
                    // No message found, check for connection timeouts.
                    Socket socket = userRepository.getInactiveSocket(false, new Date(System.currentTimeMillis() - 5 * 60 * 1000));
                    if (socket == null) {
                        socket =  userRepository.getInactiveSocket(true, new Date(System.currentTimeMillis() - 5 * 60 * 1000));
                        if (socket == null) {
                            try {
                                Thread.currentThread().sleep(10);
                            } catch(InterruptedException e) {
                            }
                        } else {
                            User user = userRepository.getUser(socket);
                            if (user == null) {
                                String hostname = userRepository.getHostname(socket);
                                log.disconnect(hostname, "Ping timeout");
                                closeSocket(socket, "[@" + hostname + "] (Ping timeout)");
                            } else {
                                leaveChannels(user, "Ping timeout.");
                                String hostname = user.getHostname();
                                log.disconnect(hostname, "Ping timeout", user.getNick());
                                closeSocket(socket, user.getNick() + "[@" + hostname + "] (Ping timeout)");
                            }
                        }
                    } else {
                        // Only send pings to registered users.
                        User user = userRepository.getUser(socket);
                        if (user == null) {
                            String hostname = userRepository.getHostname(socket);
                            log.disconnect(hostname, "Ping timeout");
                            closeSocket(socket, "[@" + hostname + "] (Ping timeout)");
                        } else {
                            sendCommandPING(socket);
                            userRepository.setLastPingSend(socket, new Date(System.currentTimeMillis()));
                        }
                    }
                } else {
                    // Handle the incoming message.
                    Socket socket = clientMessage.getSender();
                    User user = userRepository.getUser(socket);
                    log.debug("Incoming message (" + clientMessage.getCommand() + ").");
                    if (clientMessage.getCommand() == ClientMessage.STOP) {
                        if (user != null) {
                            leaveChannels(user, "Read error.");
                            String hostname = user.getHostname();
                            log.disconnect(hostname, "Read error", user.getNick());
                            closeSocket(socket, "[" + user.getNick() + "@" + hostname + "] (Read error)");
                        } else if (userRepository.getHostname(socket) != null) {
                            String hostname = userRepository.getHostname(socket);
                            log.disconnect(hostname, "Read error");
                            closeSocket(socket, "[@" + hostname + "] (Read error)");
                        }
                    } else {
                        if (user == null) {
                            if (clientMessage.getCommand() == ClientMessage.START) {
                                handleCommandSTART(clientMessage, socket);
                            } else if (clientMessage.getCommand() == ClientMessage.PASS) {
                                handleCommandPASS(clientMessage, socket);
                            } else if (clientMessage.getCommand() == ClientMessage.NICK) {
                                handleCommandNICK(clientMessage, socket);
                            } else if (clientMessage.getCommand() == ClientMessage.USER) {
                                handleCommandUSER(clientMessage, socket);
                            } else if (clientMessage.getCommand() == ClientMessage.QUIT) {
                                String hostname = userRepository.getHostname(socket);
                                log.disconnect(hostname, "Quit");
                                closeSocket(socket, "[@" + hostname + "] ()");
                            }
                        } else {
                            if (clientMessage.getCommand() == Message.PING) {
                                handleCommandPING(clientMessage, user, socket);
                            } else if (clientMessage.getCommand() == ClientMessage.PONG) {
                                // No further actions needed.
                            } else {
                                user.setLastCommandRecieved(new Date(System.currentTimeMillis()));
                                if (clientMessage.getCommand() == Message.PRIVMSG) {
                                    handleCommandPRIVMSG(clientMessage, user, socket);
                                } else if (clientMessage.getCommand() == Message.JOIN) {
                                    handleCommandJOIN(clientMessage, user, socket);
                                } else if (clientMessage.getCommand() == Message.PART) {
                                    handleCommandPART(clientMessage, user);
                                } else if (clientMessage.getCommand() == ClientMessage.MODE) {
                                    handleCommandMODE(clientMessage, user, socket);
                                } else if (clientMessage.getCommand() == ClientMessage.WHO) {
                                    handleCommandWHO(clientMessage, user, socket);
                                } else if (clientMessage.getCommand() == ClientMessage.NICK) {
                                    handleCommandNICK(clientMessage, socket, user);
                                } else if (clientMessage.getCommand() == ClientMessage.QUIT) {
                                    handleCommandQUIT(clientMessage, user, socket);
                                } else if (clientMessage.getCommand() == ClientMessage.LIST) {
                                    handleCommandLIST(clientMessage, user, socket);
                                } else if (clientMessage.getCommand() == ClientMessage.KICK) {
                                    handleCommandKICK(clientMessage, user, socket);
                                } else if (clientMessage.getCommand() == ClientMessage.TOPIC) {
                                    handleCommandTOPIC(clientMessage, user, socket);
                                } else if (clientMessage.getCommand() == ClientMessage.WHOIS) {
                                    handleCommandWHOIS(clientMessage, user, socket);
                                } else if (clientMessage.getCommand() == ClientMessage.OPER) {
                                    handleCommandOPER(clientMessage, user, socket);
                                } else if (clientMessage.getCommand() == ClientMessage.DIE) {
                                    handleCommandDIE(user);
                                } else if (clientMessage.getCommand() == ClientMessage.USERHOST) {
                                   handleCommandUSERHOST(clientMessage, user, socket);
                                } else if (clientMessage.getCommand() == ClientMessage.PASS
                                        || clientMessage.getCommand() == ClientMessage.USER) {
                                    sendErrorAlreadyRegistered(user);
                                } else {
                                    handleUnkownCommand(clientMessage, user, socket);
                                }
                            }
                        }
                        userRepository.setLastCommandRecieved(socket, new Date(System.currentTimeMillis()));
                    }
                }
            } catch(Exception e) {
                log.exception(e);
            }
        }
        Iterator iterator;
        iterator = userRepository.getRegisteredSockets().iterator();
        while (iterator.hasNext()) {
            Socket s = (Socket)iterator.next();
            User user = userRepository.getUser(s);
            String hostname = user.getHostname();
            log.disconnect(hostname, "Server shutdown", user.getNick());
            closeSocket(s, user.getNick() + "[" + hostname + "] (Server shutdown)");
            // Retrieve the iterator again to prevent a
            // ConcurrentModificationException
            iterator = userRepository.getRegisteredSockets().iterator();
        }
        iterator = userRepository.getUnregisteredSockets().iterator();
        while (iterator.hasNext()) {
            Socket s = (Socket)iterator.next();
            String hostname = userRepository.getHostname(s);
            log.disconnect(hostname, "Server shutdown");
            closeSocket(s, "[" + hostname + "] (Server shutdown)");
            // Retrieve the iterator again to prevent a
            // ConcurrentModificationException
            iterator = userRepository.getRegisteredSockets().iterator();
        }
    }

    private void handleCommandUSERHOST(ClientMessage clientMessage, User user, Socket socket) {
        if (clientMessage.getParameters().isEmpty()) {
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix(servername);
            serverMessage.setCommand(ServerMessage.ERR_NEEDMOREPARAMS);
            serverMessage.addParameter(user.getNick());
            serverMessage.addParameter("USERHOST");
            serverMessage.addParameter("Not enough parameters");
            serverMessage.setRecipient(socket);
            outgoingMessagePool.putMessage(serverMessage);
        } else {
            for (int i=0;i < 5;i++) {
                if (clientMessage.getParameter(i) != null) {
                    if (!clientMessage.getParameter(i).equals("")) {
                        User usr = userRepository.getUser(clientMessage.getParameter(i));
                        if (usr != null) {
                            ServerMessage serverMessage = new ServerMessage();
                            serverMessage.setCommand(ServerMessage.RPL_USERHOST);
                            serverMessage.setPrefix(servername);
                            serverMessage.addParameter(user.getNick());
                            serverMessage.addParameter(":" + usr.getNick() + "=+" + usr.getNick() + "@" + usr.getHostname());
                            serverMessage.setRecipient(socket);
                            outgoingMessagePool.putMessage(serverMessage);
                        }
                    }
                }
            }
        }
    }

    private void handleCommandSTART(ClientMessage clientMessage, Socket socket) {
        String hostname = clientMessage.getParameter(0);
        userRepository.connect(socket, hostname);
        log.connect(hostname);
    }

    // Only call this method for unregistered users.
    private void handleCommandPASS(ClientMessage clientMessage, Socket socket) {
        String pass = clientMessage.getParameter(0);
        if (pass == null) {
            String n = userRepository.getUnregisteredNick(socket);
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix(servername);
            serverMessage.setCommand(ServerMessage.ERR_NEEDMOREPARAMS);
            if (n == null) {
                serverMessage.addParameter("*");
            } else {
                serverMessage.addParameter(n);
            }
            serverMessage.addParameter("PASS");
            serverMessage.addParameter("Not enough parameters");
            serverMessage.setRecipient(socket);
            outgoingMessagePool.putMessage(serverMessage);
        } else {
            userRepository.setPass(socket, pass);
        }
    }

    // Only call this method for unregistered users.
    private void handleCommandNICK(ClientMessage clientMessage, Socket socket) {
        // ERR_NONICKNAMEGIVEN             ERR_ERRONEUSNICKNAME
        // ERR_NICKNAMEINUSE               ERR_NICKCOLLISION
        // ERR_UNAVAILRESOURCE             ERR_RESTRICTED
        String nick = clientMessage.getParameter(0);
        if (nick == null) {
            String n = userRepository.getUnregisteredNick(socket);
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix(servername);
            serverMessage.setCommand(ServerMessage.ERR_NONICKNAMEGIVEN);
            if (n == null) {
                serverMessage.addParameter("*");
            } else {
                serverMessage.addParameter(n);
            }
            serverMessage.addParameter("No nickname given");
            serverMessage.setRecipient(socket);
            outgoingMessagePool.putMessage(serverMessage);
        } else {
            if (!userRepository.isValidNick(nick) || (nickFilter != null && !nickFilter.allow(nick))) {
                String n = userRepository.getUnregisteredNick(socket);
                ServerMessage serverMessage = new ServerMessage();
                serverMessage.setPrefix(servername);
                serverMessage.setCommand(ServerMessage.ERR_ERRONEUSNICKNAME);
                if (n == null) {
                    serverMessage.addParameter("*");
                } else {
                    serverMessage.addParameter(n);
                }
                serverMessage.addParameter(nick);
                serverMessage.addParameter("Erroneous nickname");
                serverMessage.setRecipient(socket);
                outgoingMessagePool.putMessage(serverMessage);
            } else {
                if (userRepository.setNick(socket, nick)) {
                    // X-Chat 1.8.8 doesn't resend the USER message after a
                    // second NICK if first nick was already in use.
                    int result = userRepository.register(socket);
                    // Don't send a response if the USER command hasn't been send yet.
                    if (!(result == UserRepository.REGISTER_NEED_HOSTNAME
                            || result == UserRepository.REGISTER_NEED_USERNAME
                            || result == UserRepository.REGISTER_NEED_REALNAME)) {
                        register(socket, result);
                    }
                } else {
                    String n = userRepository.getUnregisteredNick(socket);
                    ServerMessage serverMessage = new ServerMessage();
                    serverMessage.setPrefix(servername);
                    serverMessage.setCommand(ServerMessage.ERR_NICKNAMEINUSE);
                    if (n == null) {
                        serverMessage.addParameter("*");
                    } else {
                        serverMessage.addParameter(n);
                    }
                    serverMessage.addParameter(nick);
                    serverMessage.addParameter("Nickname is already in use");
                    serverMessage.setRecipient(socket);
                    outgoingMessagePool.putMessage(serverMessage);
                }
            }
        }
    }

    // Only call this method for unregistered users.
    private void handleCommandUSER(ClientMessage clientMessage, Socket socket) {
        // ERR_NEEDMOREPARAMS              ERR_ALREADYREGISTRED
        String username = clientMessage.getParameter(0);
        String realname = clientMessage.getParameter(3);
        if (username == null || realname == null) {
            String n = userRepository.getUnregisteredNick(socket);
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix(servername);
            serverMessage.setCommand(ServerMessage.ERR_NEEDMOREPARAMS);
            if (n == null) {
                serverMessage.addParameter("*");
            } else {
                serverMessage.addParameter(n);
            }
            serverMessage.addParameter("USER");
            serverMessage.addParameter("Not enough parameters");
            serverMessage.setRecipient(socket);
            outgoingMessagePool.putMessage(serverMessage);
        } else {
            userRepository.setUsername(socket, username);
            userRepository.setRealname(socket, realname);
            int result = userRepository.register(socket);
            register(socket, result);
        }
    }

    // Only call this method for unregistered users.
    private void register(Socket socket, int result) {
        if (result == UserRepository.REGISTER_OK) {
            replyRegistered(socket);
            User user = userRepository.getUser(socket);
            user.setLastCommandRecieved(new Date(System.currentTimeMillis()));
            log.login(user.getNick(), user.getHostname());
        } else if (result == UserRepository.REGISTER_NEED_PASS
                || result == UserRepository.REGISTER_INCORRECT_PASSWORD) {
            String n = userRepository.getUnregisteredNick(socket);
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix(servername);
            serverMessage.setCommand(ServerMessage.ERR_PASSWDMISMATCH);
            if (n == null) {
                serverMessage.addParameter("*");
            } else {
                serverMessage.addParameter(n);
            }
            serverMessage.addParameter("Password incorrect");
            serverMessage.setRecipient(socket);
            outgoingMessagePool.putMessage(serverMessage);
        } else if (result == UserRepository.REGISTER_BANNED) {
            String n = userRepository.getUnregisteredNick(socket);
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix(servername);
            serverMessage.setCommand(ServerMessage.ERR_YOUREBANNEDCREEP);
            if (n == null) {
                serverMessage.addParameter("*");
            } else {
                serverMessage.addParameter(n);
            }
            serverMessage.addParameter("You are banned from this server");
            serverMessage.setRecipient(socket);
            outgoingMessagePool.putMessage(serverMessage);
        }
    }

    private void replyRegistered(Socket socket) {
        User user = userRepository.getUser(socket);

        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_WELCOME);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("Welcome to the Internet Relay Network "
                                   + user.getNick() + "!"
                                   + user.getUsername() + "@"
                                   + user.getHostname());
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);

        serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_YOURHOST);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("Your host is "
                                   + servername + ", running version  "
                                   + version);
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);

        serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_CREATED);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("This server was created "
                                   + date);
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);

        serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_MYINFO);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter(servername);
        serverMessage.addParameter(version);
        serverMessage.addParameter(usermodes);
        serverMessage.addParameter(channelmodes);
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);

        serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_LUSERCLIENT);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("There are " + userRepository.numberOfRegisteredUsers()
                                   + " users and 0 services on 1 servers");
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);

        serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_LUSEROP);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("" + userRepository.numberOfOperators());
        serverMessage.addParameter("operator(s) online");
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);

        serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_LUSERUNKNOWN);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("" + userRepository.numberOfUnregisteredUsers());
        serverMessage.addParameter("unknown connection(s)");
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);

        serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_LUSERCHANNELS);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("" + channelRepository.getChannels().size());
        serverMessage.addParameter("channels formed");
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);

        serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_LUSERME);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("I have " + (userRepository.numberOfUnregisteredUsers()
                                    + userRepository.numberOfRegisteredUsers())
                       + " clients and 1 servers");
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);

        serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_MOTDSTART);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("- " + servername + " Message of the day - ");
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);

        serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_MOTD);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("- " + motd);
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);

        serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_ENDOFMOTD);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("End of MOTD command");
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);
    }

    private void handleCommandPING(ClientMessage clientMessage, User user, Socket socket) {
        String param1 = clientMessage.getParameter(0);
        String param2 = clientMessage.getParameter(1);
        if (param2 != null && !param2.equals(servername)) {
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix(servername);
            serverMessage.setCommand(ServerMessage.ERR_NOSUCHSERVER);
            serverMessage.addParameter(user.getNick());
            serverMessage.addParameter(param2);
            serverMessage.addParameter("No such server");
            serverMessage.setRecipient(socket);
            outgoingMessagePool.putMessage(serverMessage);
        } else if (param1 == null) {
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix(servername);
            serverMessage.setCommand(ServerMessage.ERR_NOORIGIN);
            serverMessage.addParameter(user.getNick());
            serverMessage.addParameter("No origin specified");
            serverMessage.setRecipient(socket);
            outgoingMessagePool.putMessage(serverMessage);
        } else {
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix(servername);
            serverMessage.setCommand(Message.PONG);
            serverMessage.addParameter(servername);
            serverMessage.addParameter(param1);
            serverMessage.setUseLastParameterColon(true);
            serverMessage.setRecipient(socket);
            outgoingMessagePool.putMessage(serverMessage);
        }
    }

    private void sendCommandPING(Socket socket) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setCommand(Message.PING);
        serverMessage.addParameter(servername);
        serverMessage.setUseLastParameterColon(true);
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);
    }

    // Only call this method for registered users.
    private void handleCommandNICK(ClientMessage clientMessage, Socket socket, User user) {
        // ERR_NONICKNAMEGIVEN             ERR_ERRONEUSNICKNAME
        // ERR_NICKNAMEINUSE               ERR_NICKCOLLISION
        // ERR_UNAVAILRESOURCE             ERR_RESTRICTED
        String nick = clientMessage.getParameter(0);
        if (nick == null) {
            sendErrorNoNicknameGiven(user);
        } else {
            if (user.isRestricted()) {
                ServerMessage serverMessage = new ServerMessage();
                serverMessage.setPrefix(servername);
                serverMessage.setCommand(ServerMessage.ERR_RESTRICTED);
                serverMessage.addParameter(user.getNick());
                serverMessage.addParameter("Your connection is restricted!");
                serverMessage.setRecipient(socket);
                outgoingMessagePool.putMessage(serverMessage);
            } else {
                if (!userRepository.isValidNick(nick) || (nickFilter != null && !nickFilter.allow(nick))) {
                    ServerMessage serverMessage = new ServerMessage();
                    serverMessage.setPrefix(servername);
                    serverMessage.setCommand(ServerMessage.ERR_ERRONEUSNICKNAME);
                    serverMessage.addParameter(user.getNick());
                    serverMessage.addParameter(nick);
                    serverMessage.addParameter("Erroneous nickname");
                    serverMessage.setRecipient(socket);
                    outgoingMessagePool.putMessage(serverMessage);
                } else {
                    String oldNick = user.getNick();
                    if (userRepository.setNick(socket, nick)) {
                        Vector notifiedUsers = new Vector();
                        ServerMessage serverMessage;
                        serverMessage = new ServerMessage();
                        serverMessage.setPrefix(oldNick + "!" + user.getUsername() + "@" + user.getHostname());
                        serverMessage.setCommand(Message.NICK);
                        serverMessage.addParameter(nick);
                        serverMessage.setRecipient(user.getSocket());
                        outgoingMessagePool.putMessage(serverMessage);
                        notifiedUsers.add(user);
                        Iterator channelIterator = user.getChannels().iterator();
                        while (channelIterator.hasNext()) {
                            Channel channel = (Channel)channelIterator.next();
                            Iterator userIterator = channel.getUsers().iterator();
                            while (userIterator.hasNext()) {
                                User u = (User)userIterator.next();
                                if (!notifiedUsers.contains(u)) {
                                    serverMessage = new ServerMessage();
                                    serverMessage.setPrefix(oldNick + "!" + user.getUsername() + "@" + user.getHostname());
                                    serverMessage.setCommand(Message.NICK);
                                    serverMessage.addParameter(nick);
                                    serverMessage.setRecipient(u.getSocket());
                                    outgoingMessagePool.putMessage(serverMessage);
                                    notifiedUsers.add(u);
                                }
                            }
                        }
                    } else {
                        ServerMessage serverMessage = new ServerMessage();
                        serverMessage.setPrefix(servername);
                        serverMessage.setCommand(ServerMessage.ERR_NICKNAMEINUSE);
                        serverMessage.addParameter(user.getNick());
                        serverMessage.addParameter(nick);
                        serverMessage.addParameter("Nickname is already in use");
                        serverMessage.setRecipient(socket);
                        outgoingMessagePool.putMessage(serverMessage);
                    }
                }
            }
        }
    }

    private void handleCommandPRIVMSG(ClientMessage clientMessage, User user, Socket socket) {
        // ERR_NORECIPIENT                 ERR_NOTEXTTOSEND
        // ERR_CANNOTSENDTOCHAN            ERR_NOTOPLEVEL
        // ERR_WILDTOPLEVEL                ERR_TOOMANYTARGETS
        // ERR_NOSUCHNICK
        // RPL_AWAY
        if (chatOpen) {
            String target = clientMessage.getParameter(0);
            String text = clientMessage.getParameter(1);
            if (target == null) {
                ServerMessage serverMessage = new ServerMessage();
                serverMessage.setPrefix(servername);
                serverMessage.setCommand(ServerMessage.ERR_NORECIPIENT);
                serverMessage.addParameter(user.getNick());
                serverMessage.addParameter("No recipient given (PRIVMSG)");
                serverMessage.setRecipient(socket);
                outgoingMessagePool.putMessage(serverMessage);
            } else if (text == null) {
                ServerMessage serverMessage = new ServerMessage();
                serverMessage.setPrefix(servername);
                serverMessage.setCommand(ServerMessage.ERR_NOTEXTTOSEND);
                serverMessage.addParameter(user.getNick());
                serverMessage.addParameter("No text to send");
                serverMessage.setRecipient(socket);
                outgoingMessagePool.putMessage(serverMessage);
            } else if (target.startsWith("#")) {
                Channel channel = channelRepository.getChannel(target);
                if (channel == null) {
                    sendErrorNoSuchNick(user, user.getNick());
                } else {
                    if (filter != null && !filter.allow(text)) {
                        log.filteredMessage(user.getNick(), target, text);
                        ServerMessage serverMessage = new ServerMessage();
                        serverMessage.setPrefix(servername);
                        serverMessage.setCommand(Message.NOTICE);
                        serverMessage.addParameter(user.getNick());
                        serverMessage.addParameter("Your message to " + target + " has been filtered: " + text);
                        serverMessage.setRecipient(user.getSocket());
                        outgoingMessagePool.putMessage(serverMessage);
                    } else if ((channel.noOutsideMessages() && !channel.containsUser(user))
                          || (channel.isModerated() && !channel.isOperator(user))
                          || isBanned(channel, user)) {
                        ServerMessage serverMessage = new ServerMessage();
                        serverMessage.setPrefix(servername);
                        serverMessage.setCommand(ServerMessage.ERR_CANNOTSENDTOCHAN);
                        serverMessage.addParameter(user.getNick());
                        serverMessage.addParameter(target);
                        String reason = "(?)";
                        if (channel.isModerated() && !channel.isOperator(user)) {
                            reason = "(moderated)";
                        }
                        else if (channel.noOutsideMessages() && !channel.containsUser(user)) {
                            reason = "(not joined)";
                        }
                        else if (isBanned(channel, user)) {
                            reason = "(banned)";
                        }
                        serverMessage.addParameter("Cannot send to channel " + reason);
                        serverMessage.setRecipient(socket);
                        outgoingMessagePool.putMessage(serverMessage);
                    } else {
                        log.message(user.getNick(), target, text);
                        Vector users = channel.getUsers();
                        for (int i = 0; i < users.size(); i++) {
                            User u = (User)users.elementAt(i);
                            if (user != u) {
                                ServerMessage serverMessage = new ServerMessage();
                                serverMessage.setPrefix(user.getNick() + "!" + user.getUsername() + "@" + user.getHostname());
                                serverMessage.setCommand(Message.PRIVMSG);
                                serverMessage.addParameter(target);
                                serverMessage.addParameter(text);
                                serverMessage.setUseLastParameterColon(true);
                                serverMessage.setRecipient(u.getSocket());
                                outgoingMessagePool.putMessage(serverMessage);
                            }
                        }
                    }
                }
            } else {
                User u = userRepository.getUser(target);
                if (u == null) {
                    sendErrorNoSuchNick(user, target);
                } else {
                    if (filter != null && !filter.allow(text)) {
                        log.filteredMessage(user.getNick(), target, text);
                        ServerMessage serverMessage = new ServerMessage();
                        serverMessage.setPrefix(servername);
                        serverMessage.setCommand(Message.NOTICE);
                        serverMessage.addParameter(user.getNick());
                        serverMessage.addParameter("Your message to " + target + " has been filtered: " + text);
                        serverMessage.setRecipient(user.getSocket());
                        outgoingMessagePool.putMessage(serverMessage);
                    } else {
                        log.message(user.getNick(), target, text);
                        ServerMessage serverMessage = new ServerMessage();
                        serverMessage.setPrefix(user.getNick());
                        serverMessage.setCommand(Message.PRIVMSG);
                        serverMessage.addParameter(target);
                        serverMessage.addParameter(text);
                        serverMessage.setUseLastParameterColon(true);
                        serverMessage.setRecipient(u.getSocket());
                        outgoingMessagePool.putMessage(serverMessage);
                    }
                }
            }
        } else {
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix(servername);
            serverMessage.setCommand(Message.NOTICE);
            serverMessage.addParameter(user.getNick());
            serverMessage.addParameter("This chat server is closed");
            serverMessage.setRecipient(user.getSocket());
            outgoingMessagePool.putMessage(serverMessage);
        }
    }

    private boolean isBanned(Channel channel, User user) {
        if (channel.isBanned(user) && !(ignoreBanForOperator && (channel.isOperator(user) || user.isOperator()))) {
            return true;
        }
        return false;
    }

    private void handleCommandJOIN(ClientMessage clientMessage, User user, Socket socket) {
        // ERR_NEEDMOREPARAMS              ERR_BANNEDFROMCHAN
        // ERR_INVITEONLYCHAN              ERR_BADCHANNELKEY
        // ERR_CHANNELISFULL               ERR_BADCHANMASK
        // ERR_NOSUCHCHANNEL               ERR_TOOMANYCHANNELS
        // ERR_TOOMANYTARGETS              ERR_UNAVAILRESOURCE
        // RPL_TOPIC
        String param = clientMessage.getParameter(0);
        if (param == null) {
            return;
        }
        Vector channelNames = getChannelNames(param);
        for (int i = 0; i < channelNames.size(); i++) {
            String channelName = (String)channelNames.elementAt(i);
            Channel channel = channelRepository.getChannel(channelName);
            boolean setOperator = false;
            if (channel == null) {
                log.debug("No channel for "+channelName);
                // Create the channel if allowed.
                if (!allowChannelCreationByUser && !user.isOperator()) {
                    sendErrorNoSuchChannel(user, channelName);
                    return;
                } else {
                    channel = channelRepository.createChannel(channelName);
                    if (defaultChannelModes.indexOf('t') != -1) {
                        channel.setTopicProtection(true);
                    }
                    if (defaultChannelModes.indexOf('m') != -1) {
                        channel.setModerated(true);
                    }
                    if (defaultChannelModes.indexOf('n') != -1) {
                        channel.setNoOutsideMessages(true);
                    }
                    if (defaultChannelModes.indexOf('d') != -1) {
                        channel.setDeleteWhenLastUserLeaves(true);
                    }
                    if (defaultChannelUserLimit != -1) {
                        channel.setUserLimit(defaultChannelUserLimit);
                    }
                    setOperator = true;
                }
            }
            log.debug("Testing permissions for channel "+channelName);
            if (isBanned(channel, user)) {
                ServerMessage serverMessage = new ServerMessage();
                serverMessage.setPrefix(servername);
                serverMessage.setCommand(ServerMessage.ERR_BANNEDFROMCHAN);
                serverMessage.addParameter(user.getNick());
                serverMessage.addParameter("Cannot join channel (+b)");
                serverMessage.setRecipient(socket);
                outgoingMessagePool.putMessage(serverMessage);
            } else if (!channel.containsUser(user)) {
                Vector users = channel.getUsers();
                int userLimit = channel.getUserLimit();
                int channelUsers = -1;
                if (userLimit != -1 && !(channel.isOperator(user) || user.isOperator())) {
                    channelUsers ++;
                    for (int j = 0; j < users.size(); j++) {
                        if (!(channel.isOperator(user) || user.isOperator())) {
                            channelUsers++;
                        }
                    }
                }
                if (channelUsers != -1 && channelUsers == userLimit) {
                    ServerMessage serverMessage = new ServerMessage();
                    serverMessage.setPrefix(servername);
                    serverMessage.setCommand(ServerMessage.ERR_CHANNELISFULL);
                    serverMessage.addParameter(user.getNick());
                    serverMessage.addParameter(channelName);
                    serverMessage.addParameter("Cannot join channel (+l)");
                    serverMessage.setRecipient(user.getSocket());
                    outgoingMessagePool.putMessage(serverMessage);
                } else {
                    log.join(channelName, user.getNick());
                    channel.addUser(user);
                    if (setOperator && !channel.isOperator(user)) {
                        channel.setOperator(user, true);
                    }
                    ServerMessage serverMessage;
                    for (int j = 0; j < users.size(); j++) {
                        User u = (User)users.elementAt(j);
                        serverMessage = new ServerMessage();
                        serverMessage.setPrefix(user.getNick() + "!" + user.getUsername() + "@" + user.getHostname());
                        serverMessage.setCommand(Message.JOIN);
                        serverMessage.addParameter(channelName);
                        serverMessage.setUseLastParameterColon(true); // Needed by: ChatZilla 0.8.7
                        serverMessage.setRecipient(u.getSocket());
                        outgoingMessagePool.putMessage(serverMessage);
                    }

                    String topic = channel.getTopic();
                    if (topic != null) {
                        serverMessage = new ServerMessage();
                        serverMessage.setPrefix(servername);
                        serverMessage.setCommand(ServerMessage.RPL_TOPIC);
                        serverMessage.addParameter(user.getNick());
                        serverMessage.addParameter(channelName);
                        serverMessage.addParameter(topic);
                        serverMessage.setRecipient(socket);
                        outgoingMessagePool.putMessage(serverMessage);
                    }

                    serverMessage = new ServerMessage();
                    serverMessage.setPrefix(servername);
                    serverMessage.setCommand(ServerMessage.RPL_NAMREPLY);
                    serverMessage.addParameter(user.getNick());
                    serverMessage.addParameter("=");
                    serverMessage.addParameter(channel.getName());
                    StringBuffer sb = new StringBuffer();
                    for (int j = 0; j < users.size(); j++) {
                            User u = (User)users.elementAt(j);
                            if (sb.length() > 0) {
                                sb.append(" ");
                            }
                            if (channel.isOperator(u)) {
                                sb.append("@" + u.getNick());
                            } else {
                                // To be IRC compatible we should check if the max
                                // message length isn't exceeded. If the message
                                // would become to long we should send more than
                                // one message.
                                sb.append(u.getNick());
                            }
                    }
                    serverMessage.addParameter(sb.toString());
                    serverMessage.setUseLastParameterColon(true);
                    serverMessage.setRecipient(socket);
                    outgoingMessagePool.putMessage(serverMessage);

                    serverMessage = new ServerMessage();
                    serverMessage.setPrefix(servername);
                    serverMessage.setCommand(ServerMessage.RPL_ENDOFNAMES);
                    serverMessage.addParameter(user.getNick());
                    serverMessage.addParameter(channelName);
                    serverMessage.addParameter("End of NAMES list");
                    serverMessage.setRecipient(socket);
                    outgoingMessagePool.putMessage(serverMessage);

                    // If user is an operator for this channel set the o mode.
                    if (channel.isOperator(user)) {
                        Iterator iterator = users.iterator();
                        while (iterator.hasNext()) {
                            User u = (User)iterator.next();
                            serverMessage = new ServerMessage();
                            serverMessage.setPrefix(servername);
                            serverMessage.setCommand(Message.MODE);
                            serverMessage.addParameter(channelName);
                            serverMessage.addParameter("+o");
                            serverMessage.addParameter(user.getNick());
                            serverMessage.setRecipient(u.getSocket());
                            outgoingMessagePool.putMessage(serverMessage);
                        }
                    }
                }
            }
        }
    }

    private Vector getChannelNames(String param) {
        Vector channelNames = new Vector();
        StringTokenizer st = new StringTokenizer(param, ",");
        while (st.hasMoreTokens()) {
            String channelName = st.nextToken();
            if (channelName.startsWith("#") && channelName.length() > 1
                    && channelName.length() < 51
                    && channelName.indexOf(' ') == -1
                    && channelName.indexOf(0x07) == -1
                    && channelName.indexOf(':') == -1) {
                channelNames.add(channelName);
            }
        }
        return channelNames;
    }
    
    private void handleCommandPART(ClientMessage clientMessage, User user) {
        String param = clientMessage.getParameter(0);
        if (param == null) {
            return;
        }
        Vector channelNames = getChannelNames(param);
        for (int i = 0; i < channelNames.size(); i++) {
            String channelName = (String)channelNames.elementAt(i);
            Channel channel = channelRepository.getChannel(channelName);
            if (channel == null) {
                sendErrorNoSuchChannel(user, channelName);
                return;
            }
            if (!channel.containsUser(user)) {
                sendErrorNotOnChannel(user, channelName);
                return;
            }
            log.part(channelName, user.getNick());
            String message = clientMessage.getParameter(1);
            if (message == null) {
                message = user.getNick();
            }
            Vector users = channel.getUsers();
            for (int j = 0; j < users.size(); j++) {
                User u = (User)users.elementAt(j);
                ServerMessage serverMessage = new ServerMessage();
                serverMessage.setPrefix(user.getNick() + "!" + user.getUsername() + "@" + user.getHostname());
                serverMessage.setCommand(Message.PART);
                serverMessage.addParameter(channelName);
                serverMessage.addParameter(message);
                serverMessage.setRecipient(u.getSocket());
                outgoingMessagePool.putMessage(serverMessage);
            }
            removeUserFromChannel(channel, user);
        }
    }

    private void handleCommandMODE(ClientMessage clientMessage, User user, Socket socket) {
        String target = clientMessage.getParameter(0);
        if (target == null) {
            sendErrorNeedMoreParams(user, "MODE");
        } else {
            if (target.startsWith("#") || target.startsWith("&")
                    || target.startsWith("+") || target.startsWith("!")) {
                // Channel mode.
                // ERR_NEEDMOREPARAMS              ERR_KEYSET
                // ERR_NOCHANMODES                 ERR_CHANOPRIVSNEEDED
                // ERR_USERNOTINCHANNEL            ERR_UNKNOWNMODE
                // RPL_CHANNELMODEIS
                // RPL_BANLIST                     RPL_ENDOFBANLIST
                // RPL_EXCEPTLIST                  RPL_ENDOFEXCEPTLIST
                // RPL_INVITELIST                  RPL_ENDOFINVITELIST
                // RPL_UNIQOPIS
                Channel channel = channelRepository.getChannel(target);
                if (channel == null) {
                    sendErrorNoSuchChannel(user, target);
                    return;
                }
                String mode = clientMessage.getParameter(1);
                if (mode == null) {
                    // List all channel modes.
                    String channelModes = "+";
                    if (channel.noOutsideMessages()) {
                        channelModes = channelModes + "n";
                    }
                    if (channel.isModerated()) {
                        channelModes = channelModes + "m";
                    }
                    if (channel.topicProtection()) {
                        channelModes = channelModes + "t";
                    }
                    if (channel.deleteWhenLastUserLeaves()) {
                        channelModes = channelModes + "d";
                    }
                    int userLimit = channel.getUserLimit();
                    if (userLimit != -1) {
                        if (channel.containsUser(user)) {
                            channelModes = channelModes + "l " + userLimit;
                        } else {
                            channelModes = channelModes + "l";
                        }
                    }
                    ServerMessage serverMessage = new ServerMessage();
                    serverMessage.setPrefix(servername);
                    serverMessage.setCommand(ServerMessage.RPL_CHANNELMODEIS);
                    serverMessage.addParameter(user.getNick());
                    serverMessage.addParameter(target);
                    serverMessage.addParameter(channelModes);
                    serverMessage.setRecipient(socket);
                    outgoingMessagePool.putMessage(serverMessage);
                } else {
                    String param2 = clientMessage.getParameter(2);
                    if (mode.equals("+l") || mode.equals("-l")) {
                        if (user.isOperator() || channel.isOperator(user)) {
                            if (!allowChannelOperationWhenNotOnChannel && !channel.containsUser(user)) {
                                sendErrorNotOnChannel(user, target);
                            } else {
                                if (mode.equals("+l")) {
                                    int userLimit = -1;
                                    try {
                                        userLimit = Integer.parseInt(param2);
                                    } catch(Exception e) {
                                    }
                                    if (userLimit > -1 && userLimit != channel.getUserLimit()) {
                                        channel.setUserLimit(userLimit);
                                        Iterator iterator = channel.getUsers().iterator();
                                        while (iterator.hasNext()) {
                                            User u = (User)iterator.next();
                                            ServerMessage serverMessage = new ServerMessage();
                                            serverMessage.setPrefix(user.getNick() + "!" + user.getUsername() + "@" + user.getHostname());
                                            serverMessage.setCommand(Message.MODE);
                                            serverMessage.addParameter(target);
                                            serverMessage.addParameter(mode);
                                            serverMessage.addParameter(param2);
                                            serverMessage.setRecipient(u.getSocket());
                                            outgoingMessagePool.putMessage(serverMessage);
                                        }
                                    }
                                } else {
                                    if (channel.getUserLimit() > -1) {
                                        channel.setUserLimit(-1);
                                        Iterator iterator = channel.getUsers().iterator();
                                        while (iterator.hasNext()) {
                                            User u = (User)iterator.next();
                                            ServerMessage serverMessage = new ServerMessage();
                                            serverMessage.setPrefix(user.getNick() + "!" + user.getUsername() + "@" + user.getHostname());
                                            serverMessage.setCommand(Message.MODE);
                                            serverMessage.addParameter(target);
                                            serverMessage.addParameter(mode);
                                            serverMessage.setRecipient(u.getSocket());
                                            outgoingMessagePool.putMessage(serverMessage);
                                        }
                                    }
                                }
                            }
                        } else {
                            sendErrorChanOpPrivsNeeded(user, target);
                        }
                    } else if (param2 == null) {
                        if (mode.equals("+b")) {
                            // List bans on channel.
                            Iterator iterator = channel.getBans().iterator();
                            while (iterator.hasNext()) {
                                String ban = (String)iterator.next();
                                ServerMessage serverMessage = new ServerMessage();
                                serverMessage.setPrefix(servername);
                                serverMessage.setCommand(ServerMessage.RPL_BANLIST);
                                serverMessage.addParameter(user.getNick());
                                serverMessage.addParameter(channel.getName());
                                serverMessage.addParameter(ban);
                                serverMessage.setRecipient(user.getSocket());
                                outgoingMessagePool.putMessage(serverMessage);
                            }
                            ServerMessage serverMessage = new ServerMessage();
                            serverMessage.setPrefix(servername);
                            serverMessage.setCommand(ServerMessage.RPL_ENDOFBANLIST);
                            serverMessage.addParameter(user.getNick());
                            serverMessage.addParameter(channel.getName());
                            serverMessage.addParameter("End of channel ban list");
                            serverMessage.setRecipient(user.getSocket());
                            outgoingMessagePool.putMessage(serverMessage);
                        } else if (mode.equals("+t") || mode.equals("-t")
                                || mode.equals("+m") || mode.equals("-m")
                                || mode.equals("+n") || mode.equals("-n")
                                || mode.equals("+d") || mode.equals("-d")) {
                            if (user.isOperator() || channel.isOperator(user)) {
                                if (!allowChannelOperationWhenNotOnChannel && !channel.containsUser(user)) {
                                    sendErrorNotOnChannel(user, target);
                                } else {
                                    if (!((mode.equals("+t") && channel.topicProtection())
                                            || (mode.equals("-t") && !channel.topicProtection())
                                            || (mode.equals("+m") && channel.isModerated())
                                            || (mode.equals("-m") && !channel.isModerated())
                                            || (mode.equals("+n") && channel.noOutsideMessages())
                                            || (mode.equals("-n") && !channel.noOutsideMessages()))
                                            || (mode.equals("+d") && channel.deleteWhenLastUserLeaves())
                                            || (mode.equals("-d") && !channel.deleteWhenLastUserLeaves())) {
                                        if (mode.equals("+t")) {
                                            channel.setTopicProtection(true);
                                        } else if (mode.equals("-t")) {
                                            channel.setTopicProtection(false);
                                        } else if (mode.equals("+m")) {
                                            channel.setModerated(true);
                                        } else if (mode.equals("-m")) {
                                            channel.setModerated(false);
                                        } else if (mode.equals("+n")) {
                                            channel.setNoOutsideMessages(true);
                                        } else if (mode.equals("-n")) {
                                            channel.setNoOutsideMessages(false);
                                        } else if (mode.equals("+d")) {
                                            channel.setDeleteWhenLastUserLeaves(true);
                                        } else {
                                            channel.setDeleteWhenLastUserLeaves(false);
                                        }
                                        Iterator iterator = channel.getUsers().iterator();
                                        while (iterator.hasNext()) {
                                            User u = (User)iterator.next();
                                            ServerMessage serverMessage = new ServerMessage();
                                            serverMessage.setPrefix(user.getNick() + "!" + user.getUsername() + "@" + user.getHostname());
                                            serverMessage.setCommand(Message.MODE);
                                            serverMessage.addParameter(target);
                                            serverMessage.addParameter(mode);
                                            serverMessage.setRecipient(u.getSocket());
                                            outgoingMessagePool.putMessage(serverMessage);
                                        }
                                    }
                                }
                            } else {
                                sendErrorChanOpPrivsNeeded(user, target);
                            }
                        } else {
                            ServerMessage serverMessage = new ServerMessage();
                            serverMessage.setPrefix(servername);
                            serverMessage.setCommand(ServerMessage.ERR_UNKNOWNMODE);
                            serverMessage.addParameter(user.getNick());
                            if (mode.length() == 2
                                    && (mode.charAt(0) == '+' || mode.charAt(0) == '-')) {
                                serverMessage.addParameter(mode.substring(1));
                            } else {
                                serverMessage.addParameter(mode);
                            }
                            serverMessage.addParameter("is unknown mode char to me for " + target);
                            serverMessage.setRecipient(user.getSocket());
                            outgoingMessagePool.putMessage(serverMessage);
                        }
                    } else if (mode.equals("+b") || mode.equals("-b")
                            || mode.equals("+o") || mode.equals("-o")) {
                        if (user.isOperator() || channel.isOperator(user)) {
                            if (!allowChannelOperationWhenNotOnChannel && !channel.containsUser(user)) {
                                sendErrorNotOnChannel(user, target);
                            } else {
                                if ((mode.equals("+o") || mode.equals("-o")) && userRepository.getUser(param2) == null) {
                                    sendErrorUserNotInChannel(user, param2, target);
                                } else {
                                    boolean changed = false;
                                    if (mode.equals("+b")) {
                                        if (!channel.containsBan(param2)) {
                                            channel.addBan(param2);
                                            changed = true;
                                        }
                                    } else if (mode.equals("-b")) {
                                        if (channel.containsBan(param2)) {
                                            channel.removeBan(param2);
                                            changed = true;
                                        }
                                    } else {
                                        User u = userRepository.getUser(param2);
                                        if (user.isOperator() || channel.isOperator(user)) {
                                            if (!user.isOperator() && user != u && !channelOperatorMaySetRemoveChannelOperatorStatus) {
                                                ServerMessage serverMessage = new ServerMessage();
                                                serverMessage.setPrefix(servername);
                                                serverMessage.setCommand(Message.NOTICE);
                                                serverMessage.addParameter(user.getNick());
                                                serverMessage.addParameter("You're not allowed to set or remove channel operator status");
                                                serverMessage.setRecipient(user.getSocket());
                                                outgoingMessagePool.putMessage(serverMessage);
                                            } else {
                                                if (mode.equals("+o")) {
                                                    if (!channel.isOperator(u)) {
                                                        channel.setOperator(u, true);
                                                        changed = true;
                                                    }
                                                } else if (mode.equals("-o")) {
                                                    if (channel.isOperator(u)) {
                                                        channel.setOperator(u, false);
                                                        changed = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    if (changed) {
                                        Iterator iterator = channel.getUsers().iterator();
                                        while (iterator.hasNext()) {
                                            User u = (User)iterator.next();
                                            ServerMessage serverMessage = new ServerMessage();
                                            serverMessage.setPrefix(user.getNick() + "!" + user.getUsername() + "@" + user.getHostname());
                                            serverMessage.setCommand(Message.MODE);
                                            serverMessage.addParameter(target);
                                            serverMessage.addParameter(mode);
                                            serverMessage.addParameter(param2);
                                            serverMessage.setRecipient(u.getSocket());
                                            outgoingMessagePool.putMessage(serverMessage);
                                        }
                                    }
                                }
                            }
                        } else {
                            sendErrorChanOpPrivsNeeded(user, target);
                        }
                    }
                }
            } else {
                // User mode.
                // ERR_NEEDMOREPARAMS              ERR_USERSDONTMATCH
                // ERR_UMODEUNKNOWNFLAG            RPL_UMODEIS
                if (!target.equals(user.getNick())) {
                    ServerMessage serverMessage = new ServerMessage();
                    serverMessage.setPrefix(servername);
                    serverMessage.setCommand(ServerMessage.ERR_USERSDONTMATCH);
                    serverMessage.addParameter(user.getNick());
                    serverMessage.addParameter("Cannot change mode for other users");
                    serverMessage.setRecipient(socket);
                    outgoingMessagePool.putMessage(serverMessage);
                } else {
                    String mode = clientMessage.getParameter(1);
                    if (mode == null) {
                        // List user current modes.
                        mode = "+";
                        if (user.isOperator()) {
                            mode = mode + "o";
                        }
                        if (user.isRestricted()) {
                            mode = mode + "r";
                        }
                        ServerMessage serverMessage = new ServerMessage();
                        serverMessage.setPrefix(servername);
                        serverMessage.setCommand(ServerMessage.RPL_UMODEIS);
                        serverMessage.addParameter(user.getNick());
                        serverMessage.addParameter(mode);
                        serverMessage.setRecipient(socket);
                        outgoingMessagePool.putMessage(serverMessage);
                    } else if (mode.equals("-o") && user.isOperator()) {
                        user.setOperator(false);
                        ServerMessage serverMessage = new ServerMessage();
                        serverMessage.setPrefix(user.getNick());
                        serverMessage.setCommand(Message.MODE);
                        serverMessage.addParameter(user.getNick());
                        serverMessage.addParameter(mode);
                        serverMessage.setRecipient(socket);
                        outgoingMessagePool.putMessage(serverMessage);
                    }
                }
            }
        }
    }

    private void handleCommandWHO(ClientMessage clientMessage, User user, Socket socket) {
        // ERR_NOSUCHSERVER
        // RPL_WHOREPLY                  RPL_ENDOFWHO
        String channelName = clientMessage.getParameter(0);
        if (channelName != null) {
            Channel channel = channelRepository.getChannel(channelName);
            if (channel != null) {
                Vector users = channel.getUsers();
                for (int i = 0; i < users.size(); i++) {
                    User u = (User)users.elementAt(i);
                    ServerMessage serverMessage = new ServerMessage();
                    serverMessage.setPrefix(servername);
                    serverMessage.setCommand(ServerMessage.RPL_WHOREPLY);
                    serverMessage.addParameter(user.getNick());
                    serverMessage.addParameter(channelName);
                    serverMessage.addParameter(u.getUsername());
                    serverMessage.addParameter(u.getHostname());
                    serverMessage.addParameter(servername);
                    serverMessage.addParameter(u.getNick());
                    if (channel.isOperator(u)) {
                        serverMessage.addParameter("H@"); // What is H?
                    } else {
                        serverMessage.addParameter("H"); // What is H?
                    }
                    serverMessage.addParameter("0 " + u.getRealname());
                    serverMessage.setRecipient(socket);
                    outgoingMessagePool.putMessage(serverMessage);
                }
                ServerMessage serverMessage = new ServerMessage();
                serverMessage.setPrefix(servername);
                serverMessage.setCommand(ServerMessage.RPL_ENDOFWHO);
                serverMessage.addParameter(user.getNick());
                serverMessage.addParameter(channelName);
                serverMessage.addParameter("End of WHO list.");
                serverMessage.setRecipient(socket);
                outgoingMessagePool.putMessage(serverMessage);
            }
        }
    }

    private void handleCommandWHOIS(ClientMessage clientMessage, User user, Socket socket) {
        // ERR_NOSUCHSERVER              ERR_NONICKNAMEGIVEN
        // RPL_WHOISUSER                 RPL_WHOISCHANNELS
        // RPL_WHOISCHANNELS             RPL_WHOISSERVER
        // RPL_AWAY                      RPL_WHOISOPERATOR
        // RPL_WHOISIDLE                 ERR_NOSUCHNICK
        // RPL_ENDOFWHOIS
        String mask = clientMessage.getParameter(0);
        if (mask == null) {
            sendErrorNoNicknameGiven(user);
        } else {
            User u = userRepository.getUser(mask); // No real mask support yet.
            if (u == null) {
                sendErrorNoSuchNick(user, mask);
            } else {
                ServerMessage serverMessage;
                serverMessage = new ServerMessage();
                serverMessage.setPrefix(servername);
                serverMessage.setCommand(ServerMessage.RPL_WHOISUSER);
                serverMessage.addParameter(user.getNick());
                serverMessage.addParameter(u.getNick());
                serverMessage.addParameter(u.getUsername());
                serverMessage.addParameter(u.getHostname());
                serverMessage.addParameter("*");
                serverMessage.addParameter(u.getRealname());
                serverMessage.setUseLastParameterColon(true); // Needed for: X-Chat 1.8.8
                serverMessage.setRecipient(socket);
                outgoingMessagePool.putMessage(serverMessage);
                serverMessage = new ServerMessage();
                serverMessage.setPrefix(servername);
                serverMessage.setCommand(ServerMessage.RPL_WHOISSERVER);
                serverMessage.addParameter(user.getNick());
                serverMessage.addParameter(u.getNick());
                serverMessage.addParameter(servername);
                serverMessage.addParameter(serverinfo);
                serverMessage.setRecipient(socket);
                outgoingMessagePool.putMessage(serverMessage);
                if (u.isOperator()) {
                    serverMessage = new ServerMessage();
                    serverMessage.setPrefix(servername);
                    serverMessage.setCommand(ServerMessage.RPL_WHOISOPERATOR);
                    serverMessage.addParameter(user.getNick());
                    serverMessage.addParameter(u.getNick());
                    serverMessage.addParameter("is an IRC operator");
                    serverMessage.setRecipient(socket);
                    outgoingMessagePool.putMessage(serverMessage);
                }
                serverMessage = new ServerMessage();
                serverMessage.setPrefix(servername);
                serverMessage.setCommand(ServerMessage.RPL_WHOISIDLE);
                serverMessage.addParameter(user.getNick());
                serverMessage.addParameter(u.getNick());
                serverMessage.addParameter("" + ((System.currentTimeMillis() - u.getLastCommandRecieved().getTime()) / 1000));
                serverMessage.addParameter("seconds idle");
                serverMessage.setRecipient(socket);
                outgoingMessagePool.putMessage(serverMessage);
                serverMessage = new ServerMessage();
                serverMessage.setPrefix(servername);
                serverMessage.setCommand(ServerMessage.RPL_ENDOFWHOIS);
                serverMessage.addParameter(user.getNick());
                serverMessage.addParameter(mask);
                serverMessage.addParameter("End of WHOIS list");
                serverMessage.setRecipient(socket);
                outgoingMessagePool.putMessage(serverMessage);
            }
        }
    }

    private void handleCommandQUIT(ClientMessage clientMessage, User user, Socket socket) {
        String hostname = user.getHostname();
        String message = clientMessage.getParameter(0);
        if (message == null) {
            message = user.getNick();
        }
        leaveChannels(user, message);
        log.logout(user.getNick(), hostname);
        log.disconnect(hostname, "Quit", user.getNick());
        closeSocket(socket, user.getNick() + "[" + hostname + "] ()");
    }

    private void leaveChannels(User user, String message) {
        Iterator iterator = user.getChannels().iterator();
        while (iterator.hasNext()) {
            Channel channel = (Channel)iterator.next();
            log.part(channel.getName(), user.getNick());
            removeUserFromChannel(channel, user);
            Vector users = channel.getUsers();
            for (int j = 0; j < users.size(); j++) {
                User u = (User)users.elementAt(j);
                ServerMessage serverMessage = new ServerMessage();
                serverMessage.setPrefix(user.getNick() + "!" + user.getUsername() + "@" + user.getHostname());
                serverMessage.setCommand(Message.QUIT);
                serverMessage.addParameter(channel.getName());
                serverMessage.addParameter(message);
                serverMessage.setRecipient(u.getSocket());
                outgoingMessagePool.putMessage(serverMessage);
            }
            // Retrieve the iterator again because channel.removeUser(user)
            // changed the collection of channels associated with the user.
            iterator = user.getChannels().iterator();
        }
    }

    private void removeUserFromChannel(Channel channel, User user) {
        channel.removeUser(user);
        if (channel.deleteWhenLastUserLeaves() && channel.getNumberOfUsers() == 0) {
            channelRepository.removeChannel(channel.getName());
        }
    }

    private void closeSocket(Socket socket, String message) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setCommand(ServerMessage.ERROR_CLOSING_LINK);
        serverMessage.addParameter("Closing Link: " + message);
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);
        userRepository.disconnect(socket);
    }

    private void handleCommandLIST(ClientMessage clientMessage, User user, Socket socket) {
        String channelName = clientMessage.getParameter(0);
        if (channelName == null) {
            Collection collection = channelRepository.getChannels();
            for (Iterator i = collection.iterator(); i.hasNext(); ) {
                Channel channel = (Channel)i.next();
                sendChannelInfo(socket, user, channel);
            }
        } else {
            // Check if channel exists.
            Channel channel = channelRepository.getChannel(channelName);
            if (channel != null) {
                sendChannelInfo(socket, user, channel);
            }
        }
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_LISTEND);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("End of LIST");
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);
    }

    private void sendChannelInfo(Socket socket, User user, Channel channel) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.RPL_LIST);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter(channel.getName());
        serverMessage.addParameter("" + channel.getNumberOfVisibleUsers());
        String topic = channel.getTopic();
        if (topic == null) {
            serverMessage.addParameter("");
        } else {
            serverMessage.addParameter(topic);
        }
        serverMessage.setUseLastParameterColon(true);
        serverMessage.setRecipient(socket);
        outgoingMessagePool.putMessage(serverMessage);
    }

    private void handleCommandKICK(ClientMessage clientMessage, User user, Socket socket) {
        // ERR_NEEDMOREPARAMS              ERR_NOSUCHCHANNEL
        // ERR_BADCHANMASK                 ERR_CHANOPRIVSNEEDED
        // ERR_USERNOTINCHANNEL            ERR_NOTONCHANNEL
        String channelName = clientMessage.getParameter(0);
        String nickToKick = clientMessage.getParameter(1);
        String message = clientMessage.getParameter(2);
        if (channelName != null && nickToKick != null) {
            // Check if channel exists.
            Channel channel = channelRepository.getChannel(channelName);
            if (channel == null) {
                sendErrorNoSuchChannel(user, channelName);
            } else if (!allowChannelOperationWhenNotOnChannel && !channel.containsUser(user)) {
                sendErrorNotOnChannel(user, channelName);
            } else {
                // Check if user is operator.
                if (channel.isOperator(user) || user.isOperator()) {
                    // Check if user exists.
                    User userToKick = userRepository.getUser(nickToKick);
                    if (userToKick == null) {
                        sendErrorUserNotInChannel(user, nickToKick, channelName);
                    } else {
                        // Check if user to kick is operator.
                        if (!kickOperatorAllowed && (channel.isOperator(userToKick) || userToKick.isOperator())) {
                            ServerMessage serverMessage = new ServerMessage();
                            serverMessage.setPrefix(servername);
                            serverMessage.setCommand(Message.NOTICE);
                            serverMessage.addParameter(user.getNick());
                            serverMessage.addParameter("You're not allowed to kick an operator");
                            serverMessage.setRecipient(user.getSocket());
                            outgoingMessagePool.putMessage(serverMessage);
                        } else {
                            // Check if nick exists on channel.
                            if (message == null) {
                                message = user.getNick();
                            }
                            Vector users = channel.getUsers();
                            for (int j = 0; j < users.size(); j++) {
                                User u = (User)users.elementAt(j);
                                ServerMessage serverMessage = new ServerMessage();
                                serverMessage.setPrefix(user.getNick() + "!" + user.getUsername() + "@" + user.getHostname());
                                serverMessage.setCommand(Message.KICK);
                                serverMessage.addParameter(channelName);
                                serverMessage.addParameter(nickToKick);
                                serverMessage.addParameter(message);
                                serverMessage.setRecipient(u.getSocket());
                                outgoingMessagePool.putMessage(serverMessage);
                            }
                            removeUserFromChannel(channel, userToKick);
                        }
                    }
                } else {
                    sendErrorChanOpPrivsNeeded(user, channelName);
                }
            }
        }
    }

    private void handleCommandTOPIC(ClientMessage clientMessage, User user, Socket socket) {
        // ERR_NEEDMOREPARAMS              ERR_NOTONCHANNEL
        // RPL_NOTOPIC                     RPL_TOPIC
        // ERR_CHANOPRIVSNEEDED            ERR_NOCHANMODES
        if (chatOpen) {
            String channelName = clientMessage.getParameter(0);
            if (channelName == null) {
                sendErrorNeedMoreParams(user, "TOPIC");
            } else {
                Channel channel = channelRepository.getChannel(channelName);
                if (channel == null) {
                    return;
                } else {
                    if (!allowChannelOperationWhenNotOnChannel && !channel.containsUser(user)) {
                        sendErrorNotOnChannel(user, channelName);
                    } else {
                        if (channel.topicProtection()
                                && !(channel.isOperator(user) || user.isOperator())) {
                            sendErrorChanOpPrivsNeeded(user, channelName);
                        } else {
                            String topic = clientMessage.getParameter(1);
                            if (topic == null) {
                                topic = channel.getTopic();
                                if (topic == null) {
                                    ServerMessage serverMessage = new ServerMessage();
                                    serverMessage.setPrefix(servername);
                                    serverMessage.setCommand(ServerMessage.RPL_NOTOPIC);
                                    serverMessage.addParameter(user.getNick());
                                    serverMessage.addParameter(channelName);
                                    serverMessage.addParameter("No topic is set");
                                    serverMessage.setRecipient(user.getSocket());
                                    outgoingMessagePool.putMessage(serverMessage);
                                } else {
                                    ServerMessage serverMessage = new ServerMessage();
                                    serverMessage.setPrefix(servername);
                                    serverMessage.setCommand(ServerMessage.RPL_TOPIC);
                                    serverMessage.addParameter(user.getNick());
                                    serverMessage.addParameter(channelName);
                                    serverMessage.addParameter(topic);
                                    serverMessage.setUseLastParameterColon(true);
                                    serverMessage.setRecipient(user.getSocket());
                                    outgoingMessagePool.putMessage(serverMessage);
                                }
                            } else {
                                if (filter != null && !filter.allow(topic)) {
                                    log.filteredMessage(user.getNick(), channelName + " (topic)" , topic);
                                    ServerMessage serverMessage = new ServerMessage();
                                    serverMessage.setPrefix(servername);
                                    serverMessage.setCommand(Message.NOTICE);
                                    serverMessage.addParameter(user.getNick());
                                    serverMessage.addParameter("Your message to " + channelName + " has been filtered: " + topic);
                                    serverMessage.setRecipient(user.getSocket());
                                    outgoingMessagePool.putMessage(serverMessage);
                                } else {
                                    if (topic.equals("")) {
                                        channel.setTopic(null);
                                    } else {
                                        channel.setTopic(topic);
                                    }
                                    Iterator iterator = channel.getUsers().iterator();
                                    while (iterator.hasNext()) {
                                        User u = (User)iterator.next();
                                        ServerMessage serverMessage = new ServerMessage();
                                        serverMessage.setPrefix(user.getNick() + "!" + user.getUsername() + "@" + user.getHostname());
                                        serverMessage.setCommand(Message.TOPIC);
                                        serverMessage.addParameter(channelName);
                                        serverMessage.addParameter(topic);
                                        serverMessage.setRecipient(u.getSocket());
                                        outgoingMessagePool.putMessage(serverMessage);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix(servername);
            serverMessage.setCommand(Message.NOTICE);
            serverMessage.addParameter(user.getNick());
            serverMessage.addParameter("This chat server is closed");
            serverMessage.setRecipient(user.getSocket());
            outgoingMessagePool.putMessage(serverMessage);
        }
    }

    private void handleCommandOPER(ClientMessage clientMessage, User user, Socket socket) {
        // ERR_NEEDMOREPARAMS              RPL_YOUREOPER
        // ERR_NOOPERHOST                  ERR_PASSWDMISMATCH
        if (!user.isOperator()) {
            String username = clientMessage.getParameter(0);
            String password = clientMessage.getParameter(1);
            if (username == null || password == null) {
                sendErrorNeedMoreParams(user, "OPER");
            } else {
                int result = userRepository.registerAsOperator(user, username, password);
                if (result == UserRepository.REGISTER_AS_OPERATOR_OK
                        || result == UserRepository.REGISTER_AS_ADMINISTRATOR_OK) {
                    user.setOperator(true);
                    if (result == UserRepository.REGISTER_AS_ADMINISTRATOR_OK) {
                        user.setAdministrator(true);
                    }
                    ServerMessage serverMessage = new ServerMessage();
                    serverMessage.setPrefix(servername);
                    serverMessage.setCommand(ServerMessage.RPL_YOUREOPER);
                    serverMessage.addParameter(user.getNick());
                    serverMessage.addParameter("You are now an IRC operator");
                    serverMessage.setRecipient(user.getSocket());
                    outgoingMessagePool.putMessage(serverMessage);
                } else if (result == UserRepository.REGISTER_AS_OPERATOR_INCORRECT_PASSWORD) {
                    ServerMessage serverMessage = new ServerMessage();
                    serverMessage.setPrefix(servername);
                    serverMessage.setCommand(ServerMessage.ERR_PASSWDMISMATCH);
                    serverMessage.addParameter(user.getNick());
                    serverMessage.addParameter("Password incorrect");
                    serverMessage.setRecipient(user.getSocket());
                    outgoingMessagePool.putMessage(serverMessage);
                } else if (result == UserRepository.REGISTER_AS_OPERATOR_HOST_NOT_ALLOWED) {
                    ServerMessage serverMessage = new ServerMessage();
                    serverMessage.setPrefix(servername);
                    serverMessage.setCommand(ServerMessage.ERR_NOOPERHOST);
                    serverMessage.addParameter(user.getNick());
                    serverMessage.addParameter("No O-lines for your host");
                    serverMessage.setRecipient(user.getSocket());
                    outgoingMessagePool.putMessage(serverMessage);
                }
            }
        }
    }

    private void handleCommandDIE(User user) {
        // ERR_NOPRIVILEGES
        if (user.isAdministrator()) {
            stop = true;
        } else {
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix(servername);
            serverMessage.setCommand(ServerMessage.ERR_NOPRIVILEGES);
            serverMessage.addParameter(user.getNick());
            serverMessage.addParameter("Permission Denied- You're not an IRC operator");
            serverMessage.setRecipient(user.getSocket());
            outgoingMessagePool.putMessage(serverMessage);
        }
    }

    private void handleUnkownCommand(ClientMessage clientMessage, User user, Socket socket) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.ERR_UNKNOWNCOMMAND);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter(clientMessage.getUnkownCommand());
        serverMessage.addParameter("Unknown command");
        serverMessage.setRecipient(user.getSocket());
        outgoingMessagePool.putMessage(serverMessage);
    }

    /**
     * This is a callback that can be used to signify
     * that a channel was removed by other means than
     * through the normal command structure - i.e: if
     * it was removed from the cloud in case of an MMBaseChannel
     *
     * basically, it just kicks all users off the channel
     */
    
    public void channelRemoved(Channel channel) {
        Iterator i = channel.getUsers().iterator();
        while (i.hasNext()) {
            User userToKick = (User) i.next();
            String message = "server: channel was destroyed or renamed";
            Vector users = channel.getUsers();
            for (int j = 0; j < users.size(); j++) {
                User u = (User)users.elementAt(j);
                ServerMessage serverMessage = new ServerMessage();
                serverMessage.setPrefix("server!server@"+servername);
                serverMessage.setCommand(Message.KICK);
                serverMessage.addParameter(channel.getName());
                serverMessage.addParameter(userToKick.getNick());
                serverMessage.addParameter(message);
                serverMessage.setRecipient(u.getSocket());
                outgoingMessagePool.putMessage(serverMessage);
            }
        }
    }

    /**
     * This is a callback that can be used to signify
     * that a channel topic was changed by other means than
     * through the normal command structure - i.e: if
     * it was changed from the cloud in case of an MMBaseChannel
     */
    
    public void channelTopicChanged(Channel channel) {
        Iterator iterator = channel.getUsers().iterator();
        String topic = channel.getTopic();
        if (topic == null) {
            topic = "";
        }
        while (iterator.hasNext()) {
            User u = (User)iterator.next();
            ServerMessage serverMessage = new ServerMessage();
            serverMessage.setPrefix("server!server@" + servername);
            serverMessage.setCommand(Message.TOPIC);
            serverMessage.addParameter(channel.getName());
            serverMessage.addParameter(topic);
            serverMessage.setRecipient(u.getSocket());
            outgoingMessagePool.putMessage(serverMessage);
        }
    }



    
    private void sendErrorNeedMoreParams(User user, String command) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.ERR_NEEDMOREPARAMS);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter(command);
        serverMessage.addParameter("Not enough parameters");
        serverMessage.setRecipient(user.getSocket());
        outgoingMessagePool.putMessage(serverMessage);
    }

    private void sendErrorNoNicknameGiven(User user) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.ERR_NONICKNAMEGIVEN);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("No nickname given");
        serverMessage.setRecipient(user.getSocket());
        outgoingMessagePool.putMessage(serverMessage);
    }

    private void sendErrorAlreadyRegistered(User user) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.ERR_ALREADYREGISTRED);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter("Unauthorized command (already registered)");
        serverMessage.setRecipient(user.getSocket());
        outgoingMessagePool.putMessage(serverMessage);
    }

    private void sendErrorNoSuchChannel(User user, String channel) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.ERR_NOSUCHCHANNEL);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter(channel);
        serverMessage.addParameter("No such channel");
        serverMessage.setRecipient(user.getSocket());
        outgoingMessagePool.putMessage(serverMessage);
    }

    private void sendErrorNoSuchNick(User user, String nick) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.ERR_NOSUCHNICK);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter(nick);
        serverMessage.addParameter("No such nick/channel");
        serverMessage.setRecipient(user.getSocket());
        outgoingMessagePool.putMessage(serverMessage);
    }

    private void sendErrorNotOnChannel(User user, String channel) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.ERR_NOTONCHANNEL);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter(channel);
        serverMessage.addParameter("You're not on that channel");
        serverMessage.setRecipient(user.getSocket());
        outgoingMessagePool.putMessage(serverMessage);
    }

    private void sendErrorUserNotInChannel(User user, String nick, String channel) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.ERR_USERNOTINCHANNEL);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter(nick);
        serverMessage.addParameter(channel);
        serverMessage.addParameter("They aren't on that channel");
        serverMessage.setRecipient(user.getSocket());
        outgoingMessagePool.putMessage(serverMessage);
    }

    private void sendErrorChanOpPrivsNeeded(User user, String channel) {
        ServerMessage serverMessage = new ServerMessage();
        serverMessage.setPrefix(servername);
        serverMessage.setCommand(ServerMessage.ERR_CHANOPRIVSNEEDED);
        serverMessage.addParameter(user.getNick());
        serverMessage.addParameter(channel);
        serverMessage.addParameter("You're not channel operator");
        serverMessage.setRecipient(user.getSocket());
        outgoingMessagePool.putMessage(serverMessage);
    }

}

