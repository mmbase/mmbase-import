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
 * @version $Id: BasicCloudContext.java,v 1.33 2004-02-24 12:19:35 michiel Exp $
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
        if (i != null) {
            mmb = (MMBase)org.mmbase.module.Module.getModule("MMBASEROOT");


            // create transaction manager and temporary node manager
            tmpObjectManager = new TemporaryNodeManager(mmb);
            transactionManager = new TransactionManager(mmb, tmpObjectManager);

            // create module list
            while(i.hasNext()) {
                Module mod = ModuleHandler.getModule((org.mmbase.module.Module)i.next(),this);
                localModules.put(mod.getName(),mod);
            }

            // set all the names of all accessable clouds..
            localClouds.add("mmbase");
            
        } else {
            // why dont we start mmbase, when there isnt a running instance, just change the check...
            String message = "MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")";
            log.error(message);
            throw new BridgeException(message);
        }
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
