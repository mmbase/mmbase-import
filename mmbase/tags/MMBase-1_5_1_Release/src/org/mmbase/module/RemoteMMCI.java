/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.module;

import java.rmi.*;
import java.rmi.registry.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.remote.*;
import org.mmbase.bridge.remote.rmi.*;
import org.mmbase.bridge.remote.implementation.*;

import org.mmbase.util.logging.*;

import org.mmbase.module.ProcessorModule;



/**
 * RemoteMMCI is a MMBase module that starts a Remote Method Invocation
 * registry and binds a remote MMCI to the server. Look a rmmci.xml for configuration
 * options. Note in the configuration of mmbaseroot.xml the host should be a valid
 * host address if the RMIRegistryServer in rmmci.xml is no set.
 * @Author Kees Jongenburger <keesj@framfab.nl>
 * @version $Id: RemoteMMCI.java,v 1.3 2002-01-09 13:20:31 kees Exp $
 * @since MMBase-1.5
 */
public class RemoteMMCI extends ProcessorModule {
    
    //get an instance and initialize the logger
    private static Logger log = Logging.getLoggerInstance(RemoteMMCI.class.getName());
    
    /**
     * DEFAULT_RMIREGISTRY_PORT = 1111
     */
    public static final int DEFAULT_RMIREGISTRY_PORT = 1111;
    
    /**
     * DEFAULT_BIND_NAME = "remotecontext"
     */
    public static final String DEFAULT_BIND_NAME = "remotecontext";
    

    /**
     * Method called by MMBase at startup
     * it calls the createRemoteMMCI based on the rmmci.xml configuration
     */
    public void init() {
        super.init(); // is this required?
        
        log.debug("Module RemoteMMCI starting");
        
        //set the class default hard coded start values
        int registryPort = DEFAULT_RMIREGISTRY_PORT;
        String bindName = DEFAULT_BIND_NAME;
        
        //read the server port from the configuration
        String portString  = getInitParameter("port");
        if (portString != null){
            try{
                registryPort = Integer.parseInt(portString);
            } catch (NumberFormatException nfe){ log.warn("port parameter of rmmci.xml if not ot type int");};
        } else {
            log.warn("missing port init param, using (default)=("+ registryPort +")");
        }
        
        //read the rmi server host from the configuration
        String host  = getInitParameter("RMIRegistryServer");
        //if RMIRegistryServer is null or "" use the mmbaseroot.xml host
        if (host == null || host.equals("")){
            try {
                // load MMBase and make sure it is started first
                ProcessorModule mmbase = (ProcessorModule)getModule("MMBASEROOT",true);
                host = mmbase.getInitParameter("host");
                log.debug("using host FROM MMBASEROOT " + host);
                java.net.InetAddress.getByName(host);
                System.setProperty("java.rmi.server.hostname",host);
            } catch (java.net.UnknownHostException uhn){
                log.warn("property host in mmbaseroot.xml is not set correctly.");
                log.warn("Chances are big the Remote MMCI will nog work");
            }
        } else {
	    log.debug("RemoteMMCI is using the RMIRegistryServer{"+host +"} as hostname to create/connect to the RMI registry");
        }

        String bindNameParam = getInitParameter("bindname");
        if (bindNameParam != null){
            bindName = bindNameParam;
        } else {
            log.warn("missing bindname init param, using (default)=("+ bindName +")");
        }
        createRemoteMMCI(host,registryPort,bindName);
    }
    
    
    
    /**
     * This method creates or locates the RMI registry at a specific port and host and binds a new RemoteContext
     * @param registryPort the registry port to start the RMI registry
     * @param bindName the name of the object (aka remotecontext)
     */
    private void createRemoteMMCI(String host,int registryPort,String bindName){
        //System.setSecurityManager (new RMISecurityManager ());
        try {
            Registry reg = null;
	    try {
		//Note that a getRegistry call does not actually make a connection to the remote host. 
		//It simply creates a local reference to the remote registry and will succeed even if 
		//no registry is running on the remote host. Therefore, a subsequent method invocation 
		//to a remote registry returned as a result of this method may fail. 
		reg = java.rmi.registry.LocateRegistry.getRegistry(host,registryPort);
		//try if the registry is running
		reg.list();
                //if no RemoteException is thrown we are probabely ok
                log.debug("using an existing RMI registry");
	    } catch (RemoteException rex){
             	reg = java.rmi.registry.LocateRegistry.createRegistry(registryPort);
		log.debug("creating a new RMI registry");
	    } 
            
            // Create the Database object
            //interface RemoteCloudContext ... implemented by RemoteCloudContext_Rmi .. using LocalContext
            RemoteCloudContext remoteCloudContext = new RemoteCloudContext_Rmi(LocalContext.getCloudContext());
            
            log.debug("bind RempoteCloudContext in the registry using (name)=("+ bindName +")");
            
            //bind it to the registry.
            reg.rebind(bindName,remoteCloudContext);
            log.info("Module RemoteMMCI Running on tcp (port,name)=("+ registryPort +","+ bindName +")");
        } catch (java.rmi.RemoteException rex) {
            log.fatal("RMI Registry not started because of exception {" + rex.getMessage() + "}");
	    return;
        }
    }
}
