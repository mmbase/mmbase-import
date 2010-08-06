/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.unicast;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import org.mmbase.util.ThreadPools;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * ChangesReceiver is a thread object that builds a Unicast Thread
 * to receive changes from other MMBase Servers.
 *
 * @author Nico Klasens
 * @version $Id$
 */
public class ChangesReceiver implements Runnable {

    private static final Logger log = Logging.getLoggerInstance(ChangesReceiver.class);

    private Thread kicker = null;

    /** Queue with messages received from other MMBase instances */
    private final BlockingQueue<byte[]> nodesToSpawn;

    private final ServerSocket serverSocket;

    private final int version;

    /**
     * Construct UniCast Receiver
     * @param unicastHost host of unicast connection
     * @param unicastPort port of the unicast connections
     * @param nodesToSpawn Queue of received messages
     * @throws IOException when server socket failrf to listen
     */
    public ChangesReceiver(final String unicastHost, int unicastPort, BlockingQueue<byte[]> nodesToSpawn, int version) throws IOException {
        this.nodesToSpawn = nodesToSpawn;
        this.serverSocket = new ServerSocket();
        final InetAddress ia;
        if (unicastHost.equals("*")) {
            ia = null;
        } else {
            ia = InetAddress.getByName(unicastHost);
        }
        SocketAddress address = new InetSocketAddress(ia, unicastPort);
        serverSocket.bind(address);
        this.version = version;
    }

    public void start() {
        if (kicker == null) {
            kicker = new Thread(ThreadPools.threadGroup, this, "UnicastReceiver");
            kicker.setDaemon(true);
            kicker.start();
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
        log.info("Unicast listening started on " + serverSocket + " (v:" + version + ")");
        try {
            while (true) {
                if (Thread.currentThread().isInterrupted()) break;
                Socket socket = null;
                DataInputStream reader = null;
                try {
                    socket = serverSocket.accept();
                    log.debug("" + socket);

                    reader = new DataInputStream(socket.getInputStream());

                    if (version > 1) {
                        int listSize = reader.readInt();
                        log.debug("Will read " + listSize + " events");

                        for (int i = 0; i < listSize; i++) {
                            int arraySize = reader.readInt();
                            log.debug("Size of event " + i + ": " + arraySize);
                            ByteArrayOutputStream writer = new ByteArrayOutputStream();
                            //this buffer has nothing to do with the OS buffer
                            byte[] buffer = new byte[arraySize];
                            reader.read(buffer);
                            if (writer != null) {
                                writer.write(buffer, 0, arraySize);
                                writer.flush();
                            }
                            // maybe we should use encoding here?
                            byte[] message = writer.toByteArray();
                            if (log.isDebugEnabled()) {
                                log.debug("unicast RECEIVED=>" + message);
                            }
                            nodesToSpawn.offer(message);
                        }
                    } else {
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
                        byte[] message = writer.toByteArray();
                        if (log.isDebugEnabled()) {
                            log.debug("RECEIVED=>" + message);
                        }
                        nodesToSpawn.offer(message);
                    }
                } catch (SocketException e) {
                    log.warn(e);
                    continue;
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
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
