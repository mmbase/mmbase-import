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
 * according to the server configuration file.
 *
 * @author Jaco de Groot
 */
public class Server implements Runnable {
    protected ArrayList engines = new ArrayList();
    protected ArrayList threads = new ArrayList();
    protected boolean stop = false;
    public Logger log;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: chat.jar <configuration file>");
            return;
        }
        try {
            Server server = ServerConstructor.createServer(args[0]);
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLogger (Logger log) {
        this.log = log;
    }
   
    public void addChatEngine (ChatEngine engine) {
        log.info("Adding engine "+engines.size());
        engines.add(engine);
        engine.setLogger(log);
    }

   
    public void run() {
        log.info("Server starting.");
        if (engines.size() == 0) {
            log.error("No chatengine configured! Not starting.");
            return;
        }
        for (int i=0; i < engines.size(); i++) {
            Thread chatEngineThread = new Thread( (ChatEngine) engines.get(i));
            chatEngineThread.start();
            threads.add(chatEngineThread);
        }
        
        ShutdownThread shutdownThread = new ShutdownThread(this);
        Runtime.getRuntime().addShutdownHook(shutdownThread);
        log.info("Server started.");
        
       
        while (!stop) {
            try {
                Thread.currentThread().sleep(1000);
            } catch(InterruptedException e) {
            }
        }
        Runtime.getRuntime().exit(0);
    }

    public void shutdown() {
        log.info("Shutdown started.");
        stop = true;
        for (int i = 0; i <threads.size(); i++) {
            int attempts = 10;
            Thread chatEngineThread = (Thread) threads.get(i);
            while (chatEngineThread.isAlive()) {
                log.info("Waiting max " + attempts + " attempts for chatengine thread "+i+" to stop");
                attempts--;
                if (attempts == 0) {
                    Runtime.getRuntime().halt(0);
                }
            }
        }
        log.info("All threads are done.");
    }
}


class ShutdownThread extends Thread {
    private Server server;
    
    ShutdownThread(Server server) {
        this.server = server;
    }

    public void run() {
        server.shutdown();
    }

}
