/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.mmbase.bridge.CloudContext;
import org.mmbase.bridge.LocalContext;
import org.mmbase.bridge.remote.RemoteCloudContext;
import org.mmbase.bridge.remote.rmi.RemoteCloudContext_Rmi;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.logging.*;

/**
 * RemoteMMCI is a MMBase module that starts a Remote Method Invocation
 * registry and binds a remote MMCI context to the server. Look a rmmci.xml for configuration
 * options. Note that in the configuration of mmbaseroot.xml the host should be a valid
 * host address if the RMIRegistryServer in rmmci.xml is no set.
 * @author Kees Jongenburger <keesj@dds.nl>
 * @version $Id: RemoteMMCI.java,v 1.22 2008-11-03 17:45:32 michiel Exp $
 * @since MMBase-1.5
 */
public class RemoteMMCI extends ProcessorModule {

    private Registry registry;

    //get an instance and initialize the logger
    private static final Logger log = Logging.getLoggerInstance(RemoteMMCI.class);

    /**
     * DEFAULT_RMIREGISTRY_PORT = 1111
     */
    public static final int DEFAULT_RMIREGISTRY_PORT = 1111;

    /**
     * DEFAULT_BIND_NAME = "remotecontext"
     */
    public static final String DEFAULT_BIND_NAME = "remotecontext";


    public RemoteMMCI(String name) {
        super(name);
    }
    /**
     * Method called by MMBase at startup
     * it calls the createRemoteMMCI based on the rmmci.xml configuration
     */
    @Override
    public void init() {
        super.init(); // is this required?
        log.debug("Module RemoteMMCI starting");

        int registryPort = getPort();
        if (registryPort == -1) {
            log.service("Not creating RemoteRMMCI, because registry port is -1");
        } else {
            String host = getHost();
            String bindName = getBindName();
            createRemoteMMCI(host, registryPort, bindName);
            log.info("RemoteMMCI module listening on rmi://" + host + ":" + registryPort + "/" + bindName);
            startChecker(this);
        }
    }


    public String getBindName() {
        String bindName = DEFAULT_BIND_NAME;
        String bindNameParam = getInitParameter("bindname");
        if (bindNameParam != null) {
            if (bindNameParam.equals("$MACHINENAME")) {
                // use machine name
                bindName = MMBase.getMMBase().getMachineName();
            } else {
                bindName = bindNameParam;
            }
        } else {
            log.warn("missing bindname init param, using default '" + bindName + "'");
        }
        return bindName;
    }

    public String getHost() {
        //read the rmi server host from the configuration
        String host = getInitParameter("RMIRegistryServer");
        //if RMIRegistryServer is null or "" use the mmbaseroot.xml host
        if (host == null || host.equals("")) {
            try {
                // load MMBase and make sure it is started first
                MMBase mmbase  = MMBase.getMMBase();
                host = mmbase.getHost();
                log.debug("using host FROM MMBASEROOT " + host);
                java.net.InetAddress.getByName(host);
            } catch (java.net.UnknownHostException uhn) {
                log.warn("property host in mmbaseroot.xml is not set correctly.");
                log.warn("Chances are big the Remote MMCI will not work");
            }
        } else {
            log.debug("RemoteMMCI is using the RMIRegistryServer{" + host + "} as hostname to create/connect to the RMI registry");
        }
        System.setProperty("java.rmi.server.hostname", host);
        return host;
    }

    public int getPort() {
        int registryPort = DEFAULT_RMIREGISTRY_PORT;

        //read the server port from the configuration
        String portString = getInitParameter("port");
        if (portString != null) {
            try {
                registryPort = Integer.parseInt(portString);
            } catch (NumberFormatException nfe) {
                log.warn("port parameter '" + portString + "' of rmmci.xml is not of type int.");
            };
        } else {
            log.service("Missing port init param, using default " + registryPort);
        }
        return registryPort;
    }

    /**
     * This method creates or locates the RMI registry at a specific port and host and binds a new RemoteContext
     * @param registryPort the registry port to start the RMI registry
     * @param bindName the name of the object (aka remotecontext)
     */
    private void createRemoteMMCI(String host, int registryPort, String bindName) {
        //System.setSecurityManager (new RMISecurityManager ());
        try {
            Registry reg = getRegistry(host, registryPort);
            if (reg != null) {
                register(reg, bindName);
                log.debug("Module RemoteMMCI Running on (tcp port,name)=(" + registryPort + "," + bindName + ")");
            }
            else {
                log.warn("Module RemoteMMCI MOT running and failed to bind " + bindName + ")");
            }
        } catch (RemoteException rex) {
            log.fatal("RMI Registry not started because of exception {" + rex.getMessage() + Logging.stackTrace(rex) + "}");
        }
    }

    public Registry getRegistry(String host, int registryPort) throws RemoteException {
        Registry reg = null;
        try {
            /* Note that a getRegistry call does not actually make a connection to the remote host.
             * It simply creates a local reference to the remote registry and will succeed even if
             * no registry is running on the remote host. Therefore, a subsequent method invocation
             * to a remote registry returned as a result of this method may fail.
             */
             reg = java.rmi.registry.LocateRegistry.getRegistry(host, registryPort);
            //try if the registry is running
             reg.list();
             //if no RemoteException is thrown we are probabely ok
             log.debug("using an existing RMI registry");
        } catch (RemoteException rex) {
            /*
             * Binding a stub to a local registry should be enough to keep it
             * from being cleaned up by DGC (Distributed Garbage Collection)
             * One case in which it currently isn't is when the registry
             * invocation is made on a registry remote object directly
             * instead of a stub for a registry (i.e. what's returned from
             * LocateRegistry.createRegistry instead of LocateRegsitry.getRegistry)
             * and the remote object's stub passed to the registry was returned from
             * RemoteObject.toStub or UnicastRemoteObject.exportObject; in this
             * situation, the following old bug thwarts reachability:
             *
             * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4114579
             *
             * But invoking a registry stub (like LocateRegistry.getRegistry returns)
             * should avoid that problem, because the remote object's stub will get
             * marshalled and unmarshalled and thus properly registered with DGC.
             *
             * Conclusion:
             * - createRegistry = Real registry instance
             * - getRegistry = Stub to registry instance
             *
             * Shutdown (unexportObject) requires the Real registry instance
             */
            registry = java.rmi.registry.LocateRegistry.createRegistry(registryPort);
            log.debug("creating a new RMI registry");
            if (registry != null) {
               reg = java.rmi.registry.LocateRegistry.getRegistry(host, registryPort);
            }
            else {
               log.fatal("RMI Registry not created.");
            }
        }
        return reg;
    }


    public void register(Registry reg, String bindName) throws RemoteException, AccessException {
        // Create the Database object
        // interface RemoteCloudContext ... implemented by RemoteCloudContext_Rmi .. using LocalContext
        RemoteCloudContext remoteCloudContext = new RemoteCloudContext_Rmi(LocalContext.getCloudContext());

        log.debug("bind RemoteCloudContext in the registry using name=" + bindName);

        //bind it to the registry.
        reg.rebind(bindName, remoteCloudContext);
    }

    public String[] getListOfNames(String host, int registryPort) {
        try {
           Registry reg = getRegistry(host, registryPort);
           if (reg != null) {
              return reg.list();
           }
        } catch (java.rmi.RemoteException rex) {
           log.fatal("RMI Registry not started because of exception {" + rex.getMessage() + "}");
        }
        return new String[0];
     }


    /**
     * unbinds the object bound to the registry in order to try to stop the registry
     * this usualy fails(the regsitry keeps running and prevents the webapp to shutdown)
     */
    @Override
    protected void shutdown() {
        if (registry != null) {
            stopRegistry();
        }
        super.shutdown();
    }

    private void stopRegistry() {
        log.info("Stopping the RMI registry");
        try {

            String[] names = registry.list();
            for (String element : names) {
                try {
                    log.service("Unbind " + element);
                    registry.unbind(element);
                } catch (NotBoundException e1) {
                    log.warn(e1.getMessage(), e1);
                }
            }
            if (!UnicastRemoteObject.unexportObject(registry, true)) {
                log.warn("Could not unexport " + registry);
            } else {
                log.service("Unexported " + registry);
            }
        } catch (AccessException e) {
            log.warn(e.getMessage(), e);
        } catch (RemoteException e) {
            log.warn(e.getMessage(), e);
        }
        registry = null;
        // Explicitely calling the garbage collector here helps tomcat to stop faster.
        // It can take several minutes otherwise for the RMI Reaper thread to stop.
        log.debug("Reading unexporting objects. Now gc-en, to speed up shut down");
        Runtime.getRuntime().gc();
    }



    public boolean test(String host, int registryPort, String bindName) {
        try {
           String uri = "rmi://" + host + ":" + registryPort + "/" + bindName;
           Object remoteCloudContext = Naming.lookup(uri);
           if (remoteCloudContext != null) {
              log.debug("RMI lookup ok");
              try {
                    Class<?> clazz = Class.forName("org.mmbase.bridge.remote.implementation.RemoteCloudContext_Impl");
                    Constructor<?> constr =  clazz.getConstructor(new Class [] { Class.forName("org.mmbase.bridge.remote.RemoteCloudContext") });
                    CloudContext cloudContext = (CloudContext) constr.newInstance(new Object[] { remoteCloudContext } );

                    cloudContext.getCloud("mmbase");
                    log.debug("RMI cloud object found");
              } catch (Exception e){
                 log.warn("RMI cloud object failure");
                 log.warn(Logging.stackTrace(e));
                 return false;
              }
           }
           else {
              log.warn("RMI lookup failed " + bindName);
              return false;
           }
        } catch (AccessException e) {
           log.warn(Logging.stackTrace(e));
        } catch (RemoteException e) {
           log.warn(Logging.stackTrace(e));
           return false;
        } catch (NotBoundException e) {
           log.warn(Logging.stackTrace(e));
           return false;
        } catch (MalformedURLException e) {
           log.warn(Logging.stackTrace(e));
        }
        return true;
     }

    public void resetBind(String host, int registryPort, String bindName) throws RemoteException, AccessException {
        Registry reg = getRegistry(host, registryPort);
        try {
            reg.unbind(bindName);
        } catch (AccessException e) {
            log.warn(e.getMessage(), e);
        } catch (RemoteException e) {
            log.warn(e.getMessage(), e);
        } catch (NotBoundException e) {
            log.info("Unbind failed for " + bindName + " in RMIregistry " + host + ":" + registryPort);
        }
        register(reg, bindName);
    }

     private void startChecker(RemoteMMCI remoteMMCI) {
        String checkConnection  = getInitParameter("checkconnection");
        if (checkConnection != null && !checkConnection.equals("")){
           log.info("RemoteMMCI will check connection every " + checkConnection + " ms");

           RemoteChecker runnable = new RemoteChecker(remoteMMCI, Integer.parseInt(checkConnection));

           Thread checker = new Thread(runnable, "RMICloudChecker");
           checker.setDaemon(true);
           checker.start();
        }
    }

     static class RemoteChecker implements Runnable {

        int interval = 60 * 1000;
        RemoteMMCI remoteMMCI = null;

        public RemoteChecker(RemoteMMCI remoteMMCI, int interval) {
            this.remoteMMCI = remoteMMCI;
            this.interval = interval;
        }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(interval);
                }
                catch (InterruptedException e) {
                }
                testRMI();
            }
        }

        private void testRMI() {
            try {
                String bindName = remoteMMCI.getBindName();
                int port = remoteMMCI.getPort();
                String host = remoteMMCI.getHost();
                if (!remoteMMCI.test(host, port, bindName)) {
                    remoteMMCI.resetBind(host, port, bindName);
                }
            } catch (RemoteException e) {
                log.warn(e.getClass().getName() + ": " + e.getMessage(), e);
            }
        }
    }

}
