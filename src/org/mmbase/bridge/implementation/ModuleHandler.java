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

 	/**
     * Retrieves the Cloud to which this module belongs
     */
    public CloudContext getCloudContext() {
        return cloudContext;
    }

	/**
     * Retrieve the name of the module
     */
    public String getName() {
        return mmbase_module.getName();
    }

	/**
	 * Retrieve the description of the module.
	 */
	public String getDescription() {
        return mmbase_module.getModuleInfo();
    }

	/**
	 * Retrieve info from a module based on a command string.
	 * Similar to the $MOD command in SCAN.
	 * @param command the info to obtain, i.e. "USER-OS".
	 */
	public String getInfo(String command) {
	    return getInfo(command, null,null);
	}

	/**
	 * Retrieve info from a module based on a command string
	 * Similar to the $MOD command in SCAN.
	 * @param command the info to obtain, i.e. "USER-OS".
	 * @param req the Request item to use for obtaining user information. For backward compatibility.
	 * @param resp the Response item to use for redirecting users. For backward compatibility.
	 */
	public String getInfo(String command, ServletRequest req,  ServletResponse resp){
	    if (mmbase_module instanceof ProcessorInterface) {
	        return ((ProcessorInterface)mmbase_module).replace(BasicCloudContext.getScanPage(req, resp),command);
	    } else {
	        throw new BasicBridgeException("getInfo() is not supported by this module.");
	    }
	}
	
	/**
	 * Retrieve info (as a list of virtual nodes) from a module based on a command string.
	 * Similar to the LIST command in SCAN.
	 * The values retrieved are represented as fields of a virtual node, named following the fieldnames listed in the fields paramaters..
	 * @param command the info to obtain, i.e. "USER-OS".
	 * @param parameters a hashtable containing the named parameters of the list.
	 */
	public NodeList getList(String command, Hashtable parameters){
	    return getList(command,parameters,null,null);
	}

	/**
	 * Retrieve info from a module based on a command string
	 * Similar to the LIST command in SCAN.
	 * The values retrieved are represented as fields of a virtual node, named following the fieldnames listed in the fields paramaters..
	 * @param command the info to obtain, i.e. "USER-OS".
	 * @param parameters a hashtable containing the named parameters of the list.
	 * @param req the Request item to use for obtaining user information. For backward compatibility.
	 * @param resp the Response item to use for redirecting users. For backward compatibility.
	 */
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
       		        cloud=cloudContext.getCloud("mmbase",true); // get cloud object so you can create a node list. doh.
       		    }
  		        if (res.size()>0) {
  		            tempNodeManager = new VirtualNodeManager((MMObjectNode)res.get(0),cloud);
      		    }
      		    return new BasicNodeList(res,cloud,tempNodeManager);
    	    } catch (Exception e) {
    	        throw new BasicBridgeException(""+e);
    	    }
	
	    } else {
 	        throw new BasicBridgeException("getInfo() is not supported by this module.");
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
