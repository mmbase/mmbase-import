/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.module;

//java classes
import java.rmi.*;
import java.rmi.registry.*;

//mmbase bridge classes
import org.mmbase.bridge.*;
import org.mmbase.bridge.remote.*;
import org.mmbase.bridge.remote.rmi.*;
import org.mmbase.bridge.remote.implementation.*;

//logging class
import org.mmbase.util.logging.*;

//legacy class
import org.mmbase.module.ProcessorModule;



/**
 * RemoteMMCI is a MMBase module that starts a Remote Method Invocation
 * registry and binds a remote MMCI to the server. Look a rmmci.xml for configuration
 * options
 * @Author Kees Jongenburger <keesj@framfab.nl>
 */
public class RemoteMMCI
extends ProcessorModule
implements Runnable {
    
    //get an instance initialize the logger
    private static Logger log = Logging.getLoggerInstance(RemoteMMCI.class.getName());
    
    /**
     * DEFAULT_RMIREGISTRY_PORT = 1111
     */
    public static final int DEFAULT_RMIREGISTRY_PORT = 1111;
    
    /**
     * DEFAULT_SLEEP_TIME =30 , seconds.
     * When MMBase is stated
     */
    public static final int DEFAULT_SLEEP_TIME= 30;
    
    /**
     * DEFAULT_BIND_NAME = "remotecontext"
     */
    public static final String DEFAULT_BIND_NAME = "remotecontext";
    
    private int registryPort = DEFAULT_RMIREGISTRY_PORT;
    private int sleepTime = DEFAULT_SLEEP_TIME;
    private String bindName = DEFAULT_BIND_NAME;
    
    
    public void init() {
        super.init(); // is this required?
        
        log.debug("Module RemoteMMCI starting");
        
        //read the server port from the xml configuration
        String portString  = getInitParameter("port");
        if (portString != null){
            try{
                registryPort = Integer.parseInt(portString);
            } catch (NumberFormatException nfe){ log.warn("port parameter of rmmci.xml if not ot type int");};
        } else {
            log.warn("missing port init param, using (default)=("+ registryPort +")");
        }
        
        String sleepTimeString = getInitParameter("sleeptime");
        if (sleepTimeString != null){
            try{
                sleepTime = Integer.parseInt(sleepTimeString);
            } catch (NumberFormatException nfe){ log.warn("sleeptime parameter of rmmci.xml if not ot type int");};
        } else {
            log.warn("missing sleeptime init param, using (default)=("+ sleepTime +")");
        }
        
        String bindNameParam = getInitParameter("bindname");
        if (bindNameParam != null){
            bindName = bindNameParam;
        } else {
            log.warn("missing bindname init param, using (default)=("+ bindName +")");
        }
        
        //start a new "kicker"
        new Thread(this,"RMIServerStarterThread").start();
    }
    
    /**
     * starter Thread. The Tread sleeps for a while. after that
     * a new rmi registry is created the RemoteCloudContext is bind the the rmi registry
     * and the Thread stops
     */
    public void run(){
        try {
            log.debug("Waiting for MMBase to startup. Sleeping " +  sleepTime + " seconds");
            //wait for a wile for MMBase to be started
            try {Thread.sleep(sleepTime * 1000);} catch (InterruptedException ie){
                log.warn("Module RemoteMMCI interrupted during startup sleep. still continuing");
            };
            
            
            try {
                ProcessorModule mmbase = (ProcessorModule)getModule("MMBASEROOT");
                String host = mmbase.getInitParameter("host");
                log.debug("using host FROM MMBASEROOT " + host);
                java.net.InetAddress.getByName(host);
                System.setProperty("java.rmi.server.hostname",host);
            } catch (java.net.UnknownHostException uhn){
                log.warn("property host in mmbaseroot.xml is not set correctly.");
                log.warn("Chances are big the Remote MMCI will nog work");
                log.warn("An other possibility is that the MMBase module MMBASEROOT is not started yet");
            }
            
            // Start up the registry, this sloud be optional to be able to run a single
            // registry for multiple mmbase clouds
            Registry reg = java.rmi.registry.LocateRegistry.createRegistry(registryPort);
            
            
            log.debug("Create the remote context");
            
            // Create the Database object
            //interace RemoteCloudContext ... implemented by RemoteCloudContext_Rmi .. using LocalContext
            RemoteCloudContext remoteCloudContext = new RemoteCloudContext_Rmi(LocalContext.getCloudContext());
            
            log.debug("bind RempoteCloudContext in the registry using (name)=("+ bindName +")");
            //bind it to the registry.
            reg.rebind(bindName,remoteCloudContext);
            log.info("Module RemoteMMCI Running on tcp (port,name)=("+ registryPort +","+ bindName +")");
        } catch (java.rmi.RemoteException rex) {
            log.fatal("RMI Registry not started because of exception {" + rex.getMessage() + "}");
        }
    }
}