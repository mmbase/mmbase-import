/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package nl.eo.chat;

import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * The server class is responsible for initializing and starting the needed
 * threads like incoming and outgoing translators and the chat engine
 * according to the server properties file.
 *
 * @author Jaco de Groot
 */
public class Server extends HttpServlet implements Runnable {
    protected static ChatEngine chatEngine;
    protected static Thread chatEngineThread;
    protected static Thread flashConnectionBuilderThread;
    protected static Thread ircConnectionBuilderThread;
    protected static Pool incomingFlashTranslatorPool;
    protected static Pool outgoingFlashTranslatorPool;
    protected static Pool incomingIrcTranslatorPool;
    protected static Pool outgoingIrcTranslatorPool;
    private static Properties properties;
    private static String propertiesFilename;
    private static File propertiesFile;
    private static boolean propertiesFileOk = true;
    private static long lastModified = Long.MIN_VALUE;
    private static int ircPort = -1;
    private static int flashPort = -1;
    private static String repository;
    protected static InetAddress serverInetAddress;
    protected static boolean stop = false;
    private static String servername;
    private static String serverinfo;
    private static String motd;
    public static Logger log;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        propertiesFilename = getInitParameter("propertiesfile");
        if (propertiesFilename == null) {
            throw new ServletException("Parameter 'propertiesfile' not specified.");
        }
        String root = config.getServletContext().getRealPath("/").toString();
        propertiesFilename = root + "WEB-INF/" + propertiesFilename;
        Thread serverThread = new Thread(this);
        serverThread.start();
    }

    public void destroy() {
        Server.log.info("Servlet destroy begin.");
        shutdown();
        Server.log.info("Servlet destroy end.");
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: chat.jar <properties file>");
            return;
        }
        Server server = new Server();
        server.propertiesFilename = args[0];
        server.run();
    }

    public void run() {
        log = new SynchronizedLogger(propertiesFilename, "server.");
        propertiesFile = new File(propertiesFilename);
        checkProperties();
        if (propertiesFileOk) {
            chatEngine = new ChatEngine();
            chatEngine.setPropertiesFilename(propertiesFilename);
            try {
                chatEngine.init();
            } catch(InitializationException e) {
                Server.log.exception(e);
                System.err.println(e.getMessage());
                return;
            }
            if (createPools()) {
                chatEngineThread = new Thread(chatEngine);
                chatEngineThread.start();
                ConnectionBuilder flashConnectionBuilder = new ConnectionBuilder();
                flashConnectionBuilder.setPort(flashPort);
                flashConnectionBuilder.setIncomingTranslatorPool(incomingFlashTranslatorPool);
                flashConnectionBuilder.setOutgoingTranslatorPool(outgoingFlashTranslatorPool);
                flashConnectionBuilderThread = new Thread(flashConnectionBuilder);
                flashConnectionBuilderThread.start();
                ConnectionBuilder ircConnectionBuilder = new ConnectionBuilder();
                ircConnectionBuilder.setPort(ircPort);
                ircConnectionBuilder.setIncomingTranslatorPool(incomingIrcTranslatorPool);
                ircConnectionBuilder.setOutgoingTranslatorPool(outgoingIrcTranslatorPool);
                ircConnectionBuilderThread = new Thread(ircConnectionBuilder);
                ircConnectionBuilderThread.start();
                ShutdownThread shutdownThread = new ShutdownThread();
                Runtime.getRuntime().addShutdownHook(shutdownThread);
             } else {
                log.error("Could not create pools.");
            }
        }
        while (!stop) {
            try {
                Thread.currentThread().sleep(1000);
            } catch(InterruptedException e) {
            }
        }
        Runtime.getRuntime().exit(0);
    }

    public static void shutdown() {
        log.info("Shutdown started.");
        stop = true;
        int attempts = 10;
        boolean chatEngineIsAlive = chatEngineThread.isAlive();
        boolean flashConnectionBuilderIsAlive = flashConnectionBuilderThread.isAlive();
        boolean ircConnectionBuilderIsAlive = ircConnectionBuilderThread.isAlive();
        int busyIncomingFlashTranslators = incomingFlashTranslatorPool.getBusyThreads();
        int busyOutgoingFlashTranslators = outgoingFlashTranslatorPool.getBusyThreads();
        int busyIncomingIrcTranslators = incomingIrcTranslatorPool.getBusyThreads();
        int busyOutgoingIrcTranslators = outgoingIrcTranslatorPool.getBusyThreads();
        while (chatEngineIsAlive || flashConnectionBuilderIsAlive || ircConnectionBuilderIsAlive
                || busyIncomingFlashTranslators > 0 || busyOutgoingFlashTranslators > 0
                || busyIncomingIrcTranslators > 0 || busyOutgoingIrcTranslators > 0) {
            log.info("Waiting max " + attempts + " attempts for the following threads to stop:");
            if (chatEngineIsAlive) {
                log.info("Chat engine.");
            }
            if (flashConnectionBuilderIsAlive) {
                log.info("Flash connection builder.");
            }
            if (ircConnectionBuilderIsAlive) {
                log.info("IRC connection builder.");
            }
            if (busyIncomingFlashTranslators > 0) {
                log.info(busyIncomingFlashTranslators + " incoming Flash translators.");
            }
            if (busyOutgoingFlashTranslators > 0) {
                log.info(busyOutgoingFlashTranslators + " outgoing Flash translators.");
            }
            if (busyIncomingIrcTranslators > 0) {
                log.info(busyIncomingIrcTranslators + " incoming IRC translators.");
            }
            if (busyOutgoingIrcTranslators > 0) {
                log.info(busyOutgoingIrcTranslators + " outgoing IRC translators.");
            }
            try {
                 Thread.currentThread().sleep(5000);
            } catch(InterruptedException e) {
            }
            chatEngineIsAlive = chatEngineThread.isAlive();
            flashConnectionBuilderIsAlive = flashConnectionBuilderThread.isAlive();
            ircConnectionBuilderIsAlive = ircConnectionBuilderThread.isAlive();
            busyIncomingFlashTranslators = incomingFlashTranslatorPool.getBusyThreads();
            busyOutgoingFlashTranslators = outgoingFlashTranslatorPool.getBusyThreads();
            busyIncomingIrcTranslators = incomingIrcTranslatorPool.getBusyThreads();
            busyOutgoingIrcTranslators = outgoingIrcTranslatorPool.getBusyThreads();
            attempts--;
            if (attempts == 0) {
                Runtime.getRuntime().halt(0);
            }
        }
        log.info("All threads are done.");
    }

    private static boolean createPools() {
        boolean done = true;
        try {
            incomingFlashTranslatorPool = new Pool(Class.forName("nl.eo.chat.IncomingFlashTranslator"), 5, -1);
            outgoingFlashTranslatorPool = new Pool(Class.forName("nl.eo.chat.OutgoingFlashTranslator"), 5, -1);
            incomingIrcTranslatorPool = new Pool(Class.forName("nl.eo.chat.IncomingIrcTranslator"), 5, -1);
            outgoingIrcTranslatorPool = new Pool(Class.forName("nl.eo.chat.OutgoingIrcTranslator"), 5, -1);
        } catch(ClassNotFoundException e) {
            done = false;            
        }
        return done;
    }

    private static void checkProperties() {
        long l = Server.propertiesFile.lastModified();
        if (lastModified < l) {
            try {
                properties = new Properties();
                properties.load(new FileInputStream(Server.propertiesFile));
                String s;
                s = properties.getProperty("ircport");
                if (s != null) {
                    try {
                        int i = Integer.parseInt(s);
                        ircPort = i;
                    } catch(NumberFormatException e) {
                    }
                }
                if (ircPort <= 0) {
                    propertiesFileOk = false;
                    log.error("Could not read property ircport.");
                }
                s = properties.getProperty("flashport");
                if (s != null) {
                    try {
                        int i = Integer.parseInt(s);
                        flashPort = i;
                    } catch(NumberFormatException e) {
                        propertiesFileOk = false;
                        log.error("Could not read property flashport.");
                    }
                }
                if (flashPort <= 0) {
                    propertiesFileOk = false;
                    log.error("Could not read property flashport.");
                }
            } catch(FileNotFoundException e) {
                log.error("Could not find properties file: " + propertiesFilename);
            } catch(IOException e) {
                log.error("Could not read properties file: " + propertiesFilename);
            }
            lastModified = l;
        }
    }

}


class ShutdownThread extends Thread {
    
    ShutdownThread() {
    }

    public void run() {
        Server.log.info("Shutdown hook begin.");
        Server.shutdown();
        Server.log.info("Shutdown hook end.");
    }

}
