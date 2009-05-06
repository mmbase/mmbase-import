/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.unicast;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;

import org.mmbase.core.util.DaemonThread;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * ChangesReceiver is a thread object that builds a Unicast Thread
 * to receive changes from other MMBase Servers.
 *
 * @author Nico Klasens
 * @version $Id: ChangesReceiver.java,v 1.10 2008-05-09 11:33:54 nklasens Exp $
 */
public class ChangesReceiver implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ChangesReceiver.class);

    /** Thread which sends the messages */
    private Thread kicker = null;

    /** Queue with messages received from other MMBase instances */
    private final BlockingQueue<byte[]> nodesToSpawn;

    private final ServerSocket serverSocket;

    /**
     * Construct UniCast Receiver
     * @param unicastHost host of unicast connection
     * @param unicastPort port of the unicast connections
     * @param nodesToSpawn Queue of received messages
     * @throws IOException when server socket failrf to listen
     */
    ChangesReceiver(String unicastHost, int unicastPort, BlockingQueue<byte[]> nodesToSpawn) throws IOException {
        this.nodesToSpawn = nodesToSpawn;
        this.serverSocket = new ServerSocket();
        SocketAddress address = new InetSocketAddress(unicastHost, unicastPort);
        serverSocket.bind(address);
        log.info("Listening to " + address);
        this.start();
    }

    private void start() {
        if (kicker == null) {
            kicker = new DaemonThread(this, "UnicastReceiver");
            kicker.start();
            log.debug("UnicastReceiver started");
        }
    }

    void stop() {
        if (kicker != null) {
            try {
                kicker.interrupt();
                kicker.setPriority(Thread.MIN_PRIORITY);
                kicker = null;
            } catch (Throwable t) {
            }
            try {
                serverSocket.close();
            } catch (IOException ioe) {
                log.warn(ioe);
            }
        } else {
            log.service("Cannot stop thread, because it is null");
        }
    }

    public void run() {
        try {
            while (kicker!=null) {
                Socket socket = null;
                InputStream reader = null;
                try {
                    socket = serverSocket.accept();
                    reader = new BufferedInputStream(socket.getInputStream());
                    ByteArrayOutputStream writer = new ByteArrayOutputStream();
                    int size = 0;
                    //this buffer has nothing to do with the OS buffer
                    byte[] buffer = new byte[1024];

                    while ((size = reader.read(buffer)) != -1) {
                        if (writer != null) {
                          writer.write(buffer, 0, size);
                          writer.flush();
                       }
                    }
                    // maybe we should use encoding here?
                    byte[] message = writer.toByteArray();
                    if (log.isDebugEnabled()) {
                        log.debug("RECEIVED=>" + message);
                    }
                    nodesToSpawn.offer(message);
                } catch (SocketException e) {
                    log.warn(e);
                    continue;
                } catch (Exception e) {
                    log.error(e);
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                        }
                    }
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error(e);
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
