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
    protected OutgoingMessagePool outgoingMessagePool;
    protected byte[] buffer = new byte[2048];
    
    /**
     * Set the socket that will be used to read from.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setPool(MessagePool pool) {
        outgoingMessagePool = (OutgoingMessagePool) pool;
    }


    public void run() {
        if (outgoingMessagePool == null) {
            throw new RuntimeException("outgoingMessagePool is null!");
        }
        while (true) {
            try{
                synchronized(this) {
                    suspended = true;
                    wait();
                }
            } catch (InterruptedException e) {
            }
            log.debug("OutgoingTranslator " + number + " (" + Thread.currentThread().getName() + "): Start.");
            translate();
            log.debug("OutgoingTranslator " + number + " (" + Thread.currentThread().getName() + "): Stop.");
        }
    }

    protected void translate() {
        OutputStreamWriter writer;
        try {
            writer = new OutputStreamWriter(socket.getOutputStream());
        } catch(IOException e) {
            log.debug("IOException in translate(): "+e.toString());
            return;
        }
        ServerMessage message = (ServerMessage)outgoingMessagePool.getMessage(socket);
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
                    log.debug("IOException in translate(): "+e.toString());
                    return;
                }
            }
            message = (ServerMessage)outgoingMessagePool.getMessage(socket);
        }
        try {
            socket.close();
        } catch (IOException e) {
            log.error("Can't close socket: "+e.toString());
        }
    }

    protected abstract boolean writeMessage(Writer writer, ServerMessage message) throws IOException;

}
