/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.externalprocess;

import java.io.*;
import java.net.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The command executor provides a way to perform external commands. Using either {@link
 * CommandLauncher} or a connection to a CommandServer.
 *
 * @author Michiel Meeuwissen
 * @version $Id: $
 * @since MMBase-1.9.1
 */
public class CommandExecutor {

    public static enum Type {
        LAUNCHER,
        CONNECTOR;
    }

    public static class Method {
        private final String host;
        private final int port;
        private final Type type;
        private boolean inUse = false;
        public Method() {
            host = "localhost";
            port = -1;
            type = Type.LAUNCHER;
        }
        public Method(String host, int port) {
            this.host = host;
            this.port = port;
            type = Type.CONNECTOR;
        }
        public void setInUse(boolean b) {
            inUse = b;
        }
        public boolean isInUse() {
            return inUse;
        }
        public String toString() {
            return (type == Type.LAUNCHER ? "LAUNCHER" : (host + ":" + port)) + (inUse ? " (in use)" : "");
        }
    }

    public static void execute(OutputStream outputStream,
                               Method method,
                               String command, String... args) throws ProcessException, InterruptedException {
        execute(outputStream, outputStream, method, command, args);
    }

    public static void execute(OutputStream outputStream,
                               OutputStream errorStream,
                               Method method,
                               String command, String... args) throws ProcessException, InterruptedException {
        method.setInUse(true);
        try {
            switch(method.type) {
            case LAUNCHER:
                CommandLauncher launcher = new CommandLauncher(command);
                launcher.execute(command, args);
                launcher.waitAndRead(outputStream, errorStream);
                return;
            case CONNECTOR:
                try {
                    // errorStream is ignored.
                    //
                    java.net.Socket socket = new java.net.Socket(method.host, method.port);
                    final OutputStream os = socket.getOutputStream();
                    os.write(0); // version
                    final ObjectOutputStream stream = new ObjectOutputStream(os);
                    List<String> cmd = new ArrayList<String>();
                    cmd.add(command);
                    for (String arg : args) {
                        cmd.add(arg);
                    }
                    stream.writeObject((cmd.toArray(EMPTY)));
                    stream.writeObject(EMPTY);
                    Copier copier = new Copier(new ByteArrayInputStream(new byte[0]), os, ".file -> socket");
                    org.mmbase.util.ThreadPools.jobsExecutor.execute(copier);

                    Copier copier2 = new Copier(socket.getInputStream(), outputStream, ";socket -> cout");
                    org.mmbase.util.ThreadPools.jobsExecutor.execute(copier2);

                    copier.waitFor();
                    socket.shutdownOutput();
                    copier2.waitFor();
                    socket.close();
                } catch (IOException ioe) {
                    throw new ProcessException(ioe);
                }
                return;
            }
        } finally {
            method.inUse = false;
        }
    }

    private static final String[] EMPTY = new String[] {};
    // copy job
    public static class Copier implements Runnable {
        private boolean ready;
        private int count = 0;
        private final InputStream in;
        private final OutputStream out;
        private final String name;
        public  boolean debug = false;

        public Copier(InputStream i, OutputStream o, String n) {
            in = i; out = o; name = n;
        }
        public void run() {
            int size = 0;
            try {
                byte[] buffer = new byte[1024];
                while ((size = in.read(buffer)) != -1) {
                    out.write(buffer, 0, size);
                    count+= size;
                }
            } catch (Throwable t) {
                System.err.println("Connector " + toString() +  ": " + t.getClass() + " " + t.getMessage());
            }
            synchronized(this) {
                notifyAll();
                ready = true;
            }
        }
        public  boolean ready() {
            return ready;
        }
        public void  waitFor() throws InterruptedException {
            if (! ready ) {
                synchronized(this) {
                    if (! ready) wait();
                }
            }
        }
        public String toString() {
            return name;
        }

    }



}

