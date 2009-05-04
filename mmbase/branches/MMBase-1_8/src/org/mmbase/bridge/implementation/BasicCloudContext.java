/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.security.*;
import org.mmbase.module.core.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id$
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
    private static final Set localClouds = new HashSet();

    // map of modules by name
    private static Map localModules = new HashMap();

    /**
     *  constructor to call from the MMBase class
     *  (protected, so cannot be reached from a script)
     */
    protected BasicCloudContext() {
    }

    /**
     * @throws NotFoundException If mmbase not running and cannot be started because mmbase.config missing
     * @throws BridgeException   If mmbase not running and cannot be started (but mmbase.config was specified)
     */
    protected boolean check() {
        if(mmb == null) {
            synchronized(org.mmbase.module.core.MMBase.class) {
                // obtained lock
                if (mmb == null) { // if run in the mean time by other thread, then skip
                    Iterator i = org.mmbase.module.Module.getModules();
                    // check if MMBase is already running
                    if (i == null) {
                        // build the error message, since it has very litle overhead (only entered once incase of startup)
                        // MMBase may only be started from the bridge when the property mmbase.config was provided
                        if (java.lang.System.getProperty("mmbase.config") == null) {
                            // when mmbase.config is empty fill it with current working dir + /config
                            // this way there is no need to provide the info on the commandline
                            // java.lang.System.setProperty("mmbase.config", java.lang.System.getProperty("user.dir") + java.io.File.separatorChar + "config");
                            throw new NotFoundException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + " : no property mmbase.config found)");
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
                            return false;
                        }
                    }
                    // get the core module!
                    MMBase m = org.mmbase.module.core.MMBase.getMMBase();
                    // create transaction manager and temporary node manager
                    tmpObjectManager = new TemporaryNodeManager(m);
                    transactionManager = new TransactionManager(m, tmpObjectManager);
                    // create module list
                    while(i.hasNext()) {
                        Module mod = ModuleHandler.getModule((org.mmbase.module.Module)i.next(),this);
                        localModules.put(mod.getName(), mod);
                    }
                    // set all the names of all accessable clouds..
                    localClouds.add("mmbase");
                    mmb = m;
                }
            }
        }
        return true;
    }

    public ModuleList getModules() {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        ModuleList ml = new BasicModuleList(localModules.values());
        return ml;
    }

    public Module getModule(String moduleName) throws NotFoundException {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        Module mod = (Module)localModules.get(moduleName);
        if (mod == null) {
            throw new NotFoundException("Module '" + moduleName + "' does not exist.");
        }
        return mod;
    }

    public boolean hasModule(String moduleName) {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        return localModules.get(moduleName) != null;
    }


    protected void checkExists(String cloudName) throws NotFoundException  {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        if ( !localClouds.contains(cloudName) ) {
            throw new NotFoundException("Cloud '" + cloudName + "' does not exist.");
        }
        if (mmb == null || ! mmb.getState()) {
            throw new NotFoundException("MMBase is not yet initialized");
        }
    }
    public Cloud getCloud(String cloudName) {
        checkExists(cloudName);
        return getCloud(cloudName, "anonymous", null);
    }

    public Cloud getCloud(String cloudName, String authenticationType, Map loginInfo) throws NotFoundException  {
        checkExists(cloudName);
        return new BasicCloud(cloudName, authenticationType, loginInfo, this);
    }

    public Cloud getCloud(String cloudName, UserContext user) throws NotFoundException {
        checkExists(cloudName);
       return new BasicCloud(cloudName, user, this);
    }

    public StringList getCloudNames() {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        return new BasicStringList(localClouds);
    }

    /**
     * @return String describing the encoding.
     * @since MMBase-1.6
     */
    public String getDefaultCharacterEncoding() {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        return mmb.getEncoding();
    }

    public java.util.Locale getDefaultLocale() {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        return mmb.getLocale();
    }

    public java.util.TimeZone getDefaultTimeZone() {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        return mmb.getTimeZone();
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

    public AuthenticationData getAuthentication() throws NotFoundException {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        // checkExists(cloudName);
        MMBaseCop cop = mmb.getMMBaseCop();
        if (cop == null) {
            throw new NotFoundException("MMBase not yet initialized");
        } else {
            return cop.getAuthentication();
        }
    }

    public boolean isUp() {
        return mmb != null && mmb.getState() && check();
    }

    public void assertUp() {
        // TODO implement with some nice notify-mechanism.
        CloudContext ctx = LocalContext.getCloudContext();
        while (!MMBaseContext.isInitialized() || ! isUp()) {
            try {
                check();
                Thread.currentThread().sleep(10000);
                log.debug("Sleeping another 10 secs");
            } catch (Exception e) {
                // I hate java.
            }
        }
    }

}
