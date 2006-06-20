/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.clustering.unicast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.mmbase.util.Queue;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * ChangesReceiver is a thread object that builds a Unicast Thread
 * to receive changes from other MMBase Servers.
 *
 * @author Nico Klasens
 * @version $Id: ChangesReceiver.java,v 1.4 2006-06-20 08:05:53 michiel Exp $
 */
public class ChangesReceiver implements Runnable {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(ChangesReceiver.class.getName());

    /** counter of incoming messages */
    private int incount = 0;

    /** Thread which sends the messages */
    private Thread kicker = null;

    /** Queue with messages received from other MMBase instances */
    private Queue nodesToSpawn;

    /** Port on which the talking between nodes take place.*/
    private int unicastPort = 4243;

    /**
     * Construct UniCast Receiver
     * @param unicastPort port of the unicast connections
     * @param nodesToSpawn Queue of received messages
     */
    ChangesReceiver(int unicastPort, Queue nodesToSpawn) {
        this.nodesToSpawn = nodesToSpawn;
        this.unicastPort = unicastPort;
        this.start();
    }

    private void start() {
        if (kicker == null) {
            kicker = new Thread(this, "UnicastReceiver");
            kicker.setDaemon(true);
            kicker.start();
            log.debug("UnicastReceiver started");
        }
    }

    void stop() {
        if (kicker != null) {
            kicker.setPriority(Thread.MIN_PRIORITY);
            kicker.interrupt();
            kicker = null;
        } else {
            log.service("Cannot stop thread, because it is null");
        }
    }

    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(unicastPort);
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
                    nodesToSpawn.append(message);
                } catch (Exception e) {
                    log.error(Logging.stackTrace(e));
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
                incount++;
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
