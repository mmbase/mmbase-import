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
import javax.servlet.http.*;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.module.ProcessorInterface;
import org.mmbase.util.PageInfo;
import org.mmbase.util.functions.*;
import org.mmbase.util.logging.*;

/**
 * ModuleHandler
 * Creates a framework for calling modules.
 * Supports calls to the methods supported by the MMBase ProcessorModules.
 *
 * @author Pierre van Rooden
 * @author Rob Vermeulen
 * @version $Id$
 */
public class ModuleHandler implements Module, Comparable, InvocationHandler {
    private static final Logger log = Logging.getLoggerInstance(ModuleHandler.class);


    // link to cloud context
    private CloudContext cloudContext = null;
    private org.mmbase.module.Module mmbaseModule;

    private ModuleHandler(org.mmbase.module.Module mod, CloudContext cloudContext) {
        mmbaseModule = mod;
        this.cloudContext = cloudContext;
    }

    public synchronized static Module getModule(org.mmbase.module.Module mod, CloudContext cloudcontext) {
        // turned off because it causes errors on compiling with JDK1.2
        
        Class[] objClasses = mod.getClass().getInterfaces();
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
            log.debug("alternateintf =" + otherintf.getName());
            useintf = new Class[] {Module.class, otherintf};
        } else {
            useintf = new Class[] {Module.class};
        }
        log.debug("creating proxy for : " + mod.getName() + " = " + useintf);

        return (Module)Proxy.newProxyInstance(Module.class.getClassLoader(),
                                              useintf, new ModuleHandler(mod, cloudcontext));
        //return new ModuleHandler(mod,cloudcontext);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().equals(Module.class)) {
            return method.invoke(this,args);
        } else {
            return method.invoke(mmbaseModule,args);
        }
    }

    public CloudContext getCloudContext() {
        return cloudContext;
    }

    protected Cloud getCloud(Map parameters) {
        Cloud cloud = null;
        if (parameters != null) {
            cloud = (Cloud) parameters.get("CLOUD");
        }
        if (cloud == null) {
            // anonymous access on the cloud....
            cloud = cloudContext.getCloud("mmbase"); // get cloud object so you can create a node list. doh.
        }
        return cloud;
    }

    public String getName() {
        return mmbaseModule.getName();
    }

    public String getProperty(String name) {
        return mmbaseModule.getInitParameter(name);
    }

    public Map getProperties() {
        return new HashMap(mmbaseModule.getInitParameters());
    }

    public String getDescription() {
        return mmbaseModule.getModuleInfo();
    }

    public String getInfo(String command) {
        return getInfo(command, null,null);
    }

    public String getInfo(String command, ServletRequest req,  ServletResponse resp){
        if (mmbaseModule instanceof ProcessorInterface) {
            return ((ProcessorInterface)mmbaseModule).replace(new PageInfo((HttpServletRequest)req, (HttpServletResponse)resp, getCloud(null)), command);
        } else {
            throw new BridgeException("getInfo() is not supported by this module.");
        }
    }

    public void process(String command, Object parameter) {
        process(command, parameter, null, null,null);
    }

    public void process(String command, Object parameter, Map auxparameters) {
        process(command, parameter, auxparameters, null,null);
    }

    public void process(String command, Object parameter, Map auxparameters, ServletRequest req,  ServletResponse resp){
        if (mmbaseModule instanceof ProcessorInterface) {
                Hashtable cmds = new Hashtable();
                if (parameter == null) { parameter = "-1"; }
                cmds.put(command,parameter);
                // weird change. should be fixed soon in Module.process
                Hashtable partab = null;
                if (auxparameters != null) {
                    partab = new Hashtable(auxparameters);
                } else {
                    partab = new Hashtable();
                }
                ((ProcessorInterface)mmbaseModule).process(new PageInfo((HttpServletRequest)req, (HttpServletResponse)resp, getCloud(auxparameters)),cmds,partab);
                if (auxparameters != null) auxparameters.putAll(partab);
        } else {
            throw new BridgeException("process() is not supported by this module.");
        }
    }

    public NodeList getList(String command, Map parameters){
        return getList(command, parameters,null,null);
    }

    public NodeList getList(String command, Map parameters, ServletRequest req, ServletResponse resp){
        if (mmbaseModule instanceof ProcessorInterface) {
            Cloud cloud = getCloud(parameters);
            log.info("Found " + cloud + " " + (cloud != null ? "" + cloud.getUser() : ""));
            try {
                List v = ((ProcessorInterface)mmbaseModule).getNodeList(new PageInfo((HttpServletRequest)req, (HttpServletResponse)resp, cloud), command, parameters);
                log.info("Got list " + v);
                if (v.size() == 0) {
                    return cloud.createNodeList();
                } else {
                    MMObjectNode node = (MMObjectNode) v.get(0);
                    if (node instanceof org.mmbase.module.core.VirtualNode) {
                        VirtualNodeManager tempNodeManager = new VirtualNodeManager((org.mmbase.module.core.VirtualNode) node, cloud);
                        return new BasicNodeList(v, tempNodeManager);
                    } else {
                        return new BasicNodeList(v, cloud.getNodeManager(node.getBuilder().getTableName()));
                    }
                }
            } catch (Exception e) {
                throw new BridgeException(e.getMessage(), e);
            }
        } else {
            throw new BridgeException("getList() is not supported by this module.");
        }
    }

    /**
     * Compares this module to the passed object.
     * Returns 0 if they are equal, -1 if the object passed is a NodeManager and larger than this manager,
     * and +1 if the object passed is a NodeManager and smaller than this manager.
     * A module is 'larger' than another module if its name is larger (alphabetically, case sensitive)
     * than that of the other module. If names are the same, the modules are compared on cloud context.
     *
     * @param o the object to compare it with
     */
    public int compareTo(Object o) {
        Module m= (Module)o;
        int res=getName().compareTo(m.getName());
        if (res!=0) {
            return res;
        } else {
            int h1=((Cloud)o).getCloudContext().hashCode();
            int h2=cloudContext.hashCode();
            if (h1>h2) {
                return -1;
            } else if (h1<h2) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     * Compares two modules, and returns true if they are equal.
     * @param o the object to compare it with
     */
    public boolean equals(Object o) {
        return (o instanceof Module) &&
               getName().equals(((Module)o).getName()) &&
               cloudContext.equals(((Module)o).getCloudContext());
    };

    public Collection getFunctions() {
        return  mmbaseModule.getFunctions();
    }

    public Function getFunction(String functionName) {
        Function function = mmbaseModule.getFunction(functionName);
        if (function == null) {
            throw new NotFoundException("Function with name " + functionName + " does not exist.");
        }
        return function;
    }

    public Parameters createParameters(String functionName) {
        return getFunction(functionName).createParameters();
    }

    public FieldValue getFunctionValue(String functionName, List parameters) {
        return (FieldValue)getFunction(functionName).getFunctionValueWithList(parameters);
    }

}
