/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.security.*;
import java.util.*;
import java.io.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
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
        } 
	else {
	    // why dont we start mmbase, when there isnt a running instance, just change the check...
            throw new BasicBridgeException("MMBase has not been started, and cannot be started by this Class");
        }
    }

    public ModuleList getModules() {
        ModuleList ml=new BasicModuleList(localModules.values(),this);
	    return ml;
    }

    public Module getModule(String moduleName) {
    	Module mod = (Module)localModules.get(moduleName);
        if (mod==null) {
            throw new BasicBridgeException("Module "+moduleName+" does not exist.");
        }
        return mod;
    }

    public CloudList getClouds() {
        Vector v=new Vector();
        for (Iterator i=localClouds.values().iterator(); i.hasNext();) {
            v.add((Cloud)i.next());
        }
	return new BasicCloudList(v,this);
    }

    public Cloud getCloud(String cloudName) {
    	return getCloud(cloudName,false);
    }
    
    public Cloud getCloud(String name, String application, User user) {
    	throw new BasicBridgeException("Not yet implemented");
	//  this will set the new User supplied by the authentication
	//  and set it as the User of the Cloud, it is not certain if
	//  this new user object contains certain value's which it con-
	//  taned before,.. This shouldn't be nessecary, since there is
	//  only information which is related to the authentication/
	//  authorization stored inside this object.
	
    	//  return getCloud(cloudName,false);    
    }
    
    public Cloud getCloud(String cloudName, boolean readonly) {
        Cloud cloud = (Cloud)localClouds.get(cloudName);
        if (cloud==null) {
            throw new BasicBridgeException("Cloud "+cloudName+" does not exist.");
        }
	if (!readonly) {
            cloud = ((BasicCloud)cloud).getCopy();
	}
	return cloud;
    }

    /**
     * Create a temporary scanpage object.
     */
    static scanpage getScanPage(ServletRequest rq, ServletResponse resp) {
	scanpage sp = new scanpage();
        if (rq instanceof HttpServletRequest) {
            HttpServletRequest req=(HttpServletRequest)rq;
            sp.setReq(req);
	    sp.setRes((HttpServletResponse)resp);
    	    if (req!=null) {
	        sp.req_line=req.getRequestURI();
    	        sp.querystring=req.getQueryString();
            }
    	}
	return sp;
    }
}
