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
import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * @javadoc
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id: BasicCloudContext.java,v 1.34 2004-06-12 11:00:38 eduard Exp $
 */
public class BasicCloudContext implements CloudContext {
    private static final Logger log = Logging.getLoggerInstance(BasicCloudContext.class);

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
    private static Set localClouds = new HashSet();

    // map of modules by name
    private static Map localModules = new HashMap();

    /**
     *  constructor to call from the MMBase class
     *  (protected, so cannot be reached from a script)
     */
    protected BasicCloudContext() {
        Iterator i = org.mmbase.module.Module.getModules();
        // check if MMBase is already running
        if (i == null) {
        	// build the error message, since it has very less overhead (only entered once incase of startup)
        	String message = "MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + " : no propery mmbase.config found)"; 
        	// MMBase may only be started from the bridge when the property mmbase.config was provided
        	if (java.lang.System.getProperty("mmbase.config") == null) {
        		// when mmbase.config is empty fill it with current working dir + /config
        		// this way there is no need to provide the info on the commandline
        		// java.lang.System.setProperty("mmbase.config", java.lang.System.getProperty("user.dir") + java.io.File.separatorChar + "config");
        		log.error(message);
        		throw new BridgeException(message); 
        	} 
        	// when MMBase is not running, try to start it!
        	try {
        		// init the MMBaseContext,... 
        		org.mmbase.module.core.MMBaseContext.init();
        		// try to start MMBase now,...
        		org.mmbase.module.core.MMBase.getMMBase();
        		// now re-assign the values agina
        		i = org.mmbase.module.Module.getModules();
        	}
        	catch(java.lang.Exception ex) {
        		log.error("Error while trying to start MMBase from the bridge:" + Logging.stackTrace(ex));
        	}
        	// if still null,.. give error!
        	if(i == null) {
        		log.error(message);
        		throw new BridgeException(message);
        	}
        } 
        // get the core module!
        mmb = org.mmbase.module.core.MMBase.getMMBase();
        // create transaction manager and temporary node manager
        tmpObjectManager = new TemporaryNodeManager(mmb);
        transactionManager = new TransactionManager(mmb,tmpObjectManager);
        // create module list
        while(i.hasNext()) {
        	Module mod = ModuleHandler.getModule((org.mmbase.module.Module)i.next(),this);
        	localModules.put(mod.getName(),mod);
        }
        // set all the names of all accessable clouds..
        localClouds.add("mmbase");        
    }

    public ModuleList getModules() {
        ModuleList ml = new BasicModuleList(localModules.values());
        return ml;
    }

    public Module getModule(String moduleName) throws NotFoundException {
        Module mod = (Module)localModules.get(moduleName);
        if (mod == null) {
            throw new NotFoundException("Module '" + moduleName + "' does not exist.");
        }
        return mod;
    }

    public boolean hasModule(String moduleName) {
        return (localModules.get(moduleName)!=null);
    }

    public Cloud getCloud(String cloudName) {
        return getCloud(cloudName, "anonymous", null);
    }

    public Cloud getCloud(String name, String authenticationType, Map loginInfo) throws NotFoundException  {
        if ( !localClouds.contains(name) ) {
            throw new NotFoundException("Cloud '" + name + "' does not exist.");
        }
        return new BasicCloud(name, authenticationType, loginInfo, this);
    }

    public StringList getCloudNames() {
        return new BasicStringList(localClouds);
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
    /**
     * @return String describing the encoding.
     * @since MMBase-1.6
     */

    public String getDefaultCharacterEncoding() {
        return mmb.getEncoding();
    }

    public java.util.Locale getDefaultLocale() {
        return new java.util.Locale(mmb.getLanguage(), "");
    }

    public FieldList createFieldList() {
        return new BasicFieldList();
    }

    public NodeList createNodeList() {
        return new BasicNodeList();
    }

    public RelationList createRelationList() {
        return new BasicRelationList();
    }

    public NodeManagerList createNodeManagerList() {
        return new BasicNodeManagerList();
    }

    public RelationManagerList createRelationManagerList() {
        return new BasicRelationManagerList();
    }

    public ModuleList createModuleList() {
        return new BasicModuleList();
    }

    public StringList createStringList() {
        return new BasicStringList();
    }
}
