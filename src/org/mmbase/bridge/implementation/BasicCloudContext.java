/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import java.util.*;

/**
 * The collection of clouds, and modules within a Java Virtual Machine.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class BasicCloudContext implements CloudContext {

    /**
    * Link to the mmbase root
    */
    static MMBase mmb = null;

    /**
    * Temporary Node Manager for storing node during edits
    */
    static TemporaryNodeManager tmpObjectManager = null;

    /**
    * Transaction Manager to keep track of transactions
    */
	static TransactionManager transactionManager = null;

    // map of clouds by name
    private static HashMap localClouds = new HashMap();

    // map of modules by name
    private static HashMap localModules = new HashMap();

    /**
     *  constructor to call from the MMBase class
     *  (protected, so cannot be reached from a script)
     */
    protected BasicCloudContext() {

        Iterator i=org.mmbase.module.Module.getModules();
        if (i!=null) {
            mmb = (MMBase)org.mmbase.module.Module.getModule("MMBASEROOT");
		
            // create transaction manager and temporary node manager
            tmpObjectManager = new TemporaryNodeManager(mmb);
		    transactionManager = new TransactionManager(mmb,tmpObjectManager);
		
		    // create module list
		    while(i.hasNext()) {
                Module mod = ModuleHandler.getModule((org.mmbase.module.Module)i.next(),this);
                localModules.put(mod.getName(),mod);
            }
            Cloud cloud = new BasicCloud("mmbase",this);

            localClouds.put(cloud.getName(),cloud);
        } else {
            throw new BridgeException("MMBase has not been started, and cannot be started by this Class");
        }
    }

	/**
	 * Retrieves all the modules available in this context
	 * @return a <code>List</code> of all available modules
	 */
	public ModuleList getModules() {
        ModuleList ml=new BasicModuleList(localModules.values(),this);
	    return ml;
	}

	
	/**
	 * Retrieves a Module
	 * @param modulename name of the module
	 * @return the requested module
	 */
	public Module getModule(String modulename) {
	    return (Module)localModules.get(modulename);
	}

	/**
	 * Retrieves all clouds within this context
	 * @return a <code>List</code> of all Clouds within this context
	 */
	public CloudList getClouds() {
        Vector v=new Vector();
        for (Iterator i=localClouds.values().iterator(); i.hasNext();) {
            v.add(((Cloud)i.next()).getName());
        }
	    return new BasicCloudList(v,this);
	}

	/**
	 * Retrieves a Cloud
	 * @param cloudName name of the Cloud
	 * @return the requested Cloud
	 */
	public Cloud getCloud(String cloudName) {
        Cloud cloud = (Cloud)localClouds.get(cloudName);
        Cloud newcloud = ((BasicCloud)cloud).getCopy();
	    return newcloud;
	}
}
