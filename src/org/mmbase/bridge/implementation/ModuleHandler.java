/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.ProcessorInterface;
import org.mmbase.module.core.*;
import java.lang.reflect.*;
import org.mmbase.util.*;
import java.util.*;
import javax.servlet.*;

/**
 * ModuleHandler
 * @author Rob Vermeulen
 */
public class ModuleHandler implements Module {
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
	        throw new BridgeException("getInfo() is not supported by this "
                                          + "module.");
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
	        throw new BridgeException("process() is not supported by this "
                                          + "module.");
	    }
	}

	public NodeList getList(String command, Hashtable parameters){
	    return getList(command,parameters,null,null);
	}

	public NodeList getList(String command, Hashtable parameters, ServletRequest req, ServletResponse resp){
	    if (mmbase_module instanceof ProcessorInterface) {
	        Cloud cloud=null;
	        StringTagger params= new StringTagger("");
	        if (parameters!=null) {
	            cloud=(Cloud)parameters.get("CLOUD");
    	        for (Enumeration keys=parameters.keys(); keys.hasMoreElements(); ) {
	                String key=(String)keys.nextElement();
	                Object o = parameters.get(key);
	                if (o instanceof Vector) {
	                    params.setValues(key,(Vector)o);
    	            } else {
	                    params.setValue(key,""+o);
	                }
	            }
	        }

	        try {
    	        Vector v=((ProcessorInterface)mmbase_module).getList(BasicCloudContext.getScanPage(req, resp),params,command);
    	        if (v==null) { v=new Vector(); }
                int items=1;
    	        try { items=Integer.parseInt(params.Value("ITEMS")); } catch (Exception e) {}
	            Vector fieldlist=params.Values("FIELDS");


	            Vector res=new Vector(v.size() / items);
    	        MMObjectBuilder bul= ((BasicCloudContext)cloudContext).mmb.getMMObject("multirelations");
    	        for(int i= 0; i<v.size(); i+=items) {
    	            MMObjectNode node = new MMObjectNode(bul);
    	            for(int j= 0; (j<items) && (j<v.size()); j++) {
    	                if ((fieldlist!=null) && (j<fieldlist.size())) {
        	                node.setValue((String)fieldlist.get(j),v.get(i+j));
    	                } else {
        	                node.setValue("item"+(j+1),v.get(i+j));
        	            }
    	            }
    	            res.add(node);
    	        }
   		        NodeManager tempNodeManager = null;
   		        if (cloud==null) {
			// anonymous access on the cloud....
       		        cloud=cloudContext.getCloud("mmbase"); // get cloud object so you can create a node list. doh.
       		    }
  		        if (res.size()>0) {
  		            tempNodeManager = new VirtualNodeManager((MMObjectNode)res.get(0),cloud);
      		    }
      		    return new BasicNodeList(res,cloud,tempNodeManager);
    	    } catch (Exception e) {
    	        throw new BridgeException(e.getMessage());
    	    }

	    } else {
 	        throw new BridgeException("getInfo() is not supported by this "
                                          + "module.");
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
