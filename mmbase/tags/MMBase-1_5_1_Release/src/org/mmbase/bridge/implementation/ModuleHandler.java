/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;

import java.util.*;
import java.lang.reflect.*;
import javax.servlet.*;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.ProcessorInterface;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * ModuleHandler
 * Creates a framework for calling modules.
 * Supports calls to the methods supported by the MMBase ProcessorModules.
 *
 * @author Pierre van Rooden
 * @author Rob Vermeulen
 * @version $Id: ModuleHandler.java,v 1.12 2002-01-31 10:05:13 pierre Exp $
 */
public class ModuleHandler implements Module {
    private static Logger log = Logging.getLoggerInstance(ModuleHandler.class.getName());

    //removed InvocationHandler because this is jdk1.3, and MMBase requires 1.2
    //
    //, InvocationHandler {
    // link to cloud context
    private CloudContext cloudContext = null;
    private org.mmbase.module.Module mmbase_module;

    private ModuleHandler(org.mmbase.module.Module mod, CloudContext cloudcontext) {
        mmbase_module=mod;
        cloudContext=cloudcontext;
    }

    public synchronized static Module getModule(org.mmbase.module.Module mod, CloudContext cloudcontext) {
/*
// turned off because it causes errors on compiling with JDK1.2

        Class[] objClasses=mod.getClass().getInterfaces();
        // check for allowable interface class
        // Package bridge = Package.getPackage("org.mmbase.bridge");
        Class otherintf = null;
        for (int i=0; i<objClasses.length; i++) {
            if (objClasses[i].getName().startsWith("org.mmbase.bridge")) {
                otherintf=objClasses[i];
            }
        }
        Class[] useintf;
        if (otherintf!=null) {
            System.out.println("alternateintf ="+otherintf.getName());
            useintf = new Class[] {Module.class, otherintf};
        } else {
            useintf = new Class[] {Module.class};
        }
        System.out.println("creating proxy for : "+mod.getName()+" = "+useintf);

        return (Module)Proxy.newProxyInstance(Module.class.getClassLoader(),
                        useintf,new ModuleHandler(mod,cloudcontext));
*/
        return new ModuleHandler(mod,cloudcontext);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Module.class)) {
            return method.invoke(this,args);
        } else {
            return method.invoke(mmbase_module,args);
        }
    }

    public CloudContext getCloudContext() {
        return cloudContext;
    }

    public String getName() {
        return mmbase_module.getName();
    }

    public String getDescription() {
        return mmbase_module.getModuleInfo();
    }

    public String getInfo(String command) {
        return getInfo(command, null,null);
    }

    public String getInfo(String command, ServletRequest req,  ServletResponse resp){
        if (mmbase_module instanceof ProcessorInterface) {
            return ((ProcessorInterface)mmbase_module).replace(BasicCloudContext.getScanPage(req, resp),command);
        } else {
            String message;
                message = "getInfo() is not supported by this module.";
                log.error(message);
            throw new BridgeException(message);
        }
    }

    public void process(String command, Object parameter) {
        process(command, parameter, null, null,null);
    }

    public void process(String command, Object parameter, Hashtable auxparameters) {
        process(command, parameter, auxparameters, null,null);
    }

    public void process(String command, Object parameter, Hashtable auxparameters, ServletRequest req,  ServletResponse resp){
        if (mmbase_module instanceof ProcessorInterface) {
                Hashtable cmds=new Hashtable();
                if (parameter==null) { parameter="-1"; }
                cmds.put(command,parameter);
            ((ProcessorInterface)mmbase_module).process(BasicCloudContext.getScanPage(req, resp),
                        cmds,auxparameters);
        } else {
            String message;
                message = "process() is not supported by this module.";
                log.error(message);
            throw new BridgeException(message);
        }
    }

    public NodeList getList(String command, Hashtable parameters){
        return getList(command,parameters,null,null);
    }

    public NodeList getList(String command, Hashtable parameters, ServletRequest req, ServletResponse resp){
        if (mmbase_module instanceof ProcessorInterface) {
            Cloud cloud=null;
            if (parameters!=null) {
                cloud=(Cloud)parameters.get("CLOUD");
            }
            if (cloud==null) {
                // anonymous access on the cloud....
                cloud=cloudContext.getCloud("mmbase"); // get cloud object so you can create a node list. doh.
            }
            try {
                Vector v=((ProcessorInterface)mmbase_module).getNodeList(BasicCloudContext.getScanPage(req, resp),command,parameters);
                MMObjectBuilder bul=((ProcessorInterface)mmbase_module).getListBuilder(command,parameters);
                NodeManager tempNodeManager = null;
                if (bul.isVirtual()) {
                   tempNodeManager = new VirtualNodeManager(bul,cloud);
                } else {
                   tempNodeManager = cloud.getNodeManager(bul.getTableName());
                }
                return new BasicNodeList(v,cloud,tempNodeManager);
            } catch (Exception e) {
                String message;
                message = e.getMessage();
                log.error(message);
                throw new BridgeException(message);
            }
        } else {
            String message;
            message = "getInfo() is not supported by this module.";
            log.error(message);
            throw new BridgeException(message);
        }
    }

    /**
     * Compares two modules, and returns true if they are equal.
     * @param o the object to compare it with
     */
    public boolean equals(Object o) {
        return (o instanceof Module) && (o.hashCode()==hashCode());
    };

    /**
     * Returns the module's hashCode.
     */
    public int hashCode() {
        return mmbase_module.hashCode();
    };
}
