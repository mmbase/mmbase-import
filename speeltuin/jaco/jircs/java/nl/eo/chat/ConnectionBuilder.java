/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.io.*;
import java.net.*;

/**
 * The connection builder will listenen on a specified port for incoming
 * connections. Once a connection is established the connection builder will
 * retrieve an incoming and outgoing translator and pass the socket
 * object to these translators.
 *
 * @author Jaco de Groot
 */
class ConnectionBuilder implements Runnable {
    int port = -1;
    Pool incomingTranslatorPool;
    Pool outgoingTranslatorPool;
    private ServerSocket serverSocket;

    ConnectionBuilder() {
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public void setIncomingTranslatorPool(Pool incomingTranslatorPool) {
        this.incomingTranslatorPool = incomingTranslatorPool;
    }
    
    public void setOutgoingTranslatorPool(Pool outgoingTranslatorPool) {
        this.outgoingTranslatorPool = outgoingTranslatorPool;
    }

    public void run() {
        while (!Server.stop) {
            serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
            } catch(IOException e) {
                Server.log.error("Could not open port " + port + ": " + e.getMessage());
            }
            if (serverSocket == null) {
                try {
                    Thread.currentThread().sleep(1000);
                } catch(InterruptedException e) {
                }
            } else {
                Server.log.info("Port " + port + " opened.");
                // Set a timeout to be able to react on a server shutdown.
                try {
                    serverSocket.setSoTimeout(1000);
                } catch(SocketException e) {
                    Server.log.error("Could not set socket timeout.");
                }
                int socketNr = 0;
                while (!Server.stop) {
                    Socket socket = null;
                    boolean accepted = false;
                    while (!Server.stop && !accepted) {
                        try {
                            socket = serverSocket.accept();
                            accepted = true;
                        } catch(InterruptedIOException e) {
                            // Socket timeout. Gives a change to check for
                            // server shutdown.
                        } catch(IOException e) {
                            Server.log.error("Could not accept a socket: " + e.getMessage());
                        }
                    }
                    if (accepted) {
                        boolean ok = true;
                        try {
                            // Set a timeout to prevent the situation where read is called before the
                            // socket was closed and the read will stay blocked.
                            socket.setSoTimeout(1000);
                        } catch(SocketException e) {
                            Server.log.error("Can't set socket timeout.");
                            ok = false;
                        }
                        try {
                            socket.setTcpNoDelay(true);
                        } catch(SocketException e) {
                            Server.log.error("Can't set tcp no delay.");
                            ok = false;
                        }
                        if (ok) {
                            Server.log.debug("Accept a socket.");
                            socketNr++;
                            Server.log.debug("Accepted socket number " + socketNr + ".");
                            IncomingTranslator incomingTranslator = (IncomingTranslator)incomingTranslatorPool.getObject();
                            incomingTranslator.setSocket(socket);
                            incomingTranslatorPool.proceed(incomingTranslator);
                            OutgoingTranslator outgoingTranslator = (OutgoingTranslator)outgoingTranslatorPool.getObject();
                            outgoingTranslator.setSocket(socket);
                            outgoingTranslatorPool.proceed(outgoingTranslator);
                        }
                    }
                }
            }
        }
        Server.log.info("Port " + port + " closed.");
    }

}


