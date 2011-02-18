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
 * @version $Id$
 * @since MMBase-1.9.1
 */
public class CommandExecutor {

    private static final Logger LOG = Logging.getLoggerInstance(CommandExecutor.class);

    public static enum Type {
        /**
         * A command executor of this type, simpy call System.exec itself to lauch the external command itself.
         */
        LAUNCHER,
        /**
         * A command executor of this type, makes a tcp connection to a 'commandserver'
         * (See <a href="http://www.mmbase.org/api/trunk/mmbase-commandserver/org/mmbase/util/CommandServer.html">Command Server</a>)
         */
        CONNECTOR
    }

    public static class Method implements Serializable {
        private static final long serialVersionUID = 0L;
        private final String host;
        private final int port;
        private final Type type;
        private final int timeout;
        private boolean inUse = false;
        public Method() {
            host = "localhost";
            port = -1;
            timeout = -1;
            type = Type.LAUNCHER;
        }
        public Method(String host, int port) {
            this(host, port, 10000);
        }
        public Method(String host, int port, int timeout) {
            this.host = host;
            this.port = port;
            this.timeout = timeout;
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

    public static long execute(OutputStream outputStream,
                               Method method,
                               String command, String... args) throws ProcessException, InterruptedException {
        return execute(outputStream, outputStream, method, command, args);
    }

    public static long execute(OutputStream outputStream,
                               OutputStream errorStream,
                               Method method,
                               String command, String... args) throws ProcessException, InterruptedException {
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        return execute(inputStream, outputStream, errorStream, method, EMPTY, command, args);
    }

    /**
     * @since MMBase-1.9.6
     */
    public static long execute(InputStream inputStream,
                               OutputStream outputStream,
                               OutputStream errorStream,
                               Method method,
                               String [] env,
                               String command, String... args) throws ProcessException, InterruptedException {
        method.setInUse(true);
        try {
            switch(method.type) {
            case LAUNCHER:
                CommandLauncher launcher = new CommandLauncher(command);
                launcher.execute(command, args, env);
                ProcessClosure reader = launcher.waitAndWrite(inputStream, outputStream, errorStream);
                return reader.getCount();
            case CONNECTOR:
                try {
                    // errorStream is ignored.
                    //
                    java.net.Socket socket = new java.net.Socket();
                    socket.connect(new InetSocketAddress(method.host, method.port),  method.timeout);
                    final OutputStream os = socket.getOutputStream();
                    os.write(0); // version
                    final ObjectOutputStream stream = new ObjectOutputStream(os);
                    List<String> cmd = new ArrayList<String>();
                    cmd.add(command);
                    for (String arg : args) {
                        cmd.add(arg);
                    }
                    stream.writeObject((cmd.toArray(new String[cmd.size()])));
                    stream.writeObject(env);
                    Copier copier = new Copier(inputStream, os, ".file -> socket");
                    org.mmbase.util.ThreadPools.jobsExecutor.execute(copier);

                    Copier copier2 = new Copier(socket.getInputStream(), outputStream, ";socket -> cout");
                    org.mmbase.util.ThreadPools.jobsExecutor.execute(copier2);

                    copier.waitFor();
                    socket.shutdownOutput();
                    copier2.waitFor();
                    socket.close();
                    return copier.getCount();
                } catch (IOException ioe) {
                    throw new ProcessException(ioe);
                }
            default:
                throw new IllegalArgumentException();

            }
        } finally {
            method.inUse = false;
        }
    }

    private static final String[] EMPTY = new String[] {};
    // copy job
    public static class Copier implements Runnable {
        private boolean ready;
        private long count = 0;
        private final InputStream in;
        private final OutputStream out;
        private final String name;
        public  boolean debug = false;

        public Copier(InputStream i, OutputStream o, String n) {
            in = i; out = o; name = n;
        }
        public void run() {
            try {
                count = org.mmbase.util.IOUtil.copy(in, out);
            } catch (Throwable t) {
                LOG.error("Connector " + toString() +  ": " + t.getClass() + " " + t.getMessage());
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

        /**
         * @since MMBase-1.9.6
         */
        public long getCount() {
            return count;
        }

    }



}

