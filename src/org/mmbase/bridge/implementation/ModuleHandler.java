/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * ModuleHandler
 * @author Rob Vermeulen
 */
public class ModuleHandler implements Module, InvocationHandler {
    // link to cloud context
    private CloudContext cloudContext = null;
    private org.mmbase.module.Module mmbase_module;

    private ModuleHandler(org.mmbase.module.Module mod, CloudContext cloudcontext) {
        mmbase_module=mod;
        cloudContext=cloudcontext;
    }

    public synchronized static Module getModule(org.mmbase.module.Module mod, CloudContext cloudcontext) {
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
                                      useintf,
                                      new ModuleHandler(mod,cloudcontext));
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
}
