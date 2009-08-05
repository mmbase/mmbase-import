/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.io.*;
import java.net.*;
import java.util.Vector;

/**
 * Abstract class to be extended by translators for incoming messages.
 *
 * @author Jaco de Groot
 */
public abstract class IncomingTranslator extends PoolElement {
    protected Socket socket;
    protected InputStream inputStream;
    protected byte[] buffer = new byte[2048];
    protected String s;
    protected long messageTimer;
    protected IncomingMessagePool incomingMessagePool;

    public void setPool(MessagePool pool) {
        incomingMessagePool = (IncomingMessagePool) pool;
    }

    
    /**
     * Set the socket that will be used to read incoming messages from.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        while (true) {
            try{
                synchronized(this) {
                    suspended = true;
                    wait();
                }
            } catch (InterruptedException e) {
            }
            log.debug("IncomingTranslator " + number + " (" + Thread.currentThread().getName() + "): Start.");
            translate();
            log.debug("IncomingTranslator " + number + " (" + Thread.currentThread().getName() + "): Stop.");
        }
    }
    
    protected void translate() {
        // Lookup hostname in the translator to prevent hostname lookup to
        // slow down the chat engine.
        String hostname = socket.getInetAddress().getHostName();
        // Make the chatengine aware of this new connection. 
        ClientMessage message = new ClientMessage();
        message.setCommand(ClientMessage.START);
        message.addParameter(hostname);
        message.setSender(socket);
        incomingMessagePool.putMessage(message);
        try {
            inputStream = socket.getInputStream();
        } catch(IOException e) {
            message = new ClientMessage();
            message.setCommand(ClientMessage.STOP);
            message.addParameter("Connection could not be initialized.");
            message.setSender(socket);
            incomingMessagePool.putMessage(message);
            return;
        }
        int available = -1;
        try {
            available = read();
        } catch(IOException e) {
            message = new ClientMessage();
            message.setCommand(ClientMessage.STOP);
            message.addParameter("Connection aborted.");
            message.setSender(socket);
            incomingMessagePool.putMessage(message);
            return;
        }
        messageTimer = System.currentTimeMillis();
        s = "";
        while (available != -1) {
            if (available == 0) {
                // Is this possible?
                log.debug(Thread.currentThread().getName() + ": Going to sleep.");
                try {
                    Thread.currentThread().sleep(1000);
                } catch(InterruptedException e) {
                }
            } else {
                for (int i = 0; i < available; i++) {
                    if (messageSeparatorFound(i)) {
                        parseMessage(i);
                        s = "";
                        // Flood control like described in section 5.8 of
                        // RFC2813.
                        messageTimer = messageTimer + 2000;
                        long currentTime = System.currentTimeMillis();
                        if (messageTimer < currentTime) {
                            messageTimer = currentTime;
                        } else if (messageTimer > currentTime + 10000) {
                            try {
                                Thread.currentThread().sleep(messageTimer - (currentTime + 10000));
                            } catch(InterruptedException e) {
                            }
                        }
                    } else {
                        if (s.length() < 512) {
                            s = s + (char)buffer[i];
                        } else {
                            // A client should not send messages longer than
                            // 512 chars. If it does we will slow down the
                            // process of reading to prevent long messages
                            // from slowing down the server.
                            try {
                                Thread.currentThread().sleep(10);
                            } catch(InterruptedException e) {
                            }
                        }
                    }
                }
            }
            try {
                available = read();
            } catch(IOException e) {
                message = new ClientMessage();
                message.setCommand(ClientMessage.STOP);
                message.addParameter("Connection aborted.");
                message.setSender(socket);
                incomingMessagePool.putMessage(message);
                return;
            }
        }
        message = new ClientMessage();
        message.setCommand(ClientMessage.STOP);
        message.addParameter("Connection closed.");
        message.setSender(socket);
        incomingMessagePool.putMessage(message);
    }
    
    protected void setCommand(ClientMessage message, String command) {
        int commandId = ClientMessage.getCommandId(command);
        if (commandId != -1) {
            message.setCommand(commandId);
        } else {
            log.debug("UNKNOWN COMMAND '" + command + "'.");
            message.setUnkownCommand(command);
        }
    }
    
    protected int read() throws IOException {
        int available = -2;
        while (available == -2) {
            try{
                available = inputStream.read(buffer, 0, buffer.length);
            } catch(InterruptedIOException e) {
            }
        }
        return available;
    }
    
    protected abstract boolean messageSeparatorFound(int i);
    
    protected abstract void parseMessage(int i);

}
