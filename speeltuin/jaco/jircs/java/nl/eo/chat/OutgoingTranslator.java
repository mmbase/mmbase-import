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
 * Abstract class to be extended by translators for outgoing messages.
 *
 * @author Jaco de Groot
 */
public abstract class OutgoingTranslator extends PoolElement {
    protected Socket socket;
    protected byte[] buffer = new byte[2048];
    
    OutgoingTranslator() {
    }

    /**
     * Set the socket that will be used to read from.
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
            Server.log.debug("OutgoingTranslator " + number + " (" + Thread.currentThread().getName() + "): Start.");
            translate();
            Server.log.debug("OutgoingTranslator " + number + " (" + Thread.currentThread().getName() + "): Stop.");
        }
    }

    protected void translate() {
        OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(socket.getOutputStream());
        } catch(IOException e) {
            return;
        }
        ServerMessage message = (ServerMessage)OutgoingMessagePool.getMessage(socket);
        boolean quit = false;
        while (!quit) {
            if (message == null) {
                try {
                    Thread.currentThread().sleep(100);
                } catch(InterruptedException e) {
                }
            } else {
                try {
                    quit = writeMessage(writer, message);
                } catch(IOException e) {
                    return;
                }
            }
            message = (ServerMessage)OutgoingMessagePool.getMessage(socket);
        }
        try {
            socket.close();
        } catch (IOException e) {
            Server.log.error("Can't close socket.");
        }
    }

    protected abstract boolean writeMessage(Writer writer, ServerMessage message) throws IOException;

}
