/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.implementation;
import org.mmbase.bridge.*;
import org.mmbase.security.*;
import org.mmbase.storage.search.SearchQueryHandler;
import org.mmbase.module.core.*;
import java.util.*;
import org.mmbase.util.logging.*;

/**
 * This is the base class for {@link org.mmbase.bridge.LocalContext} (which is probably its only
 * descendant). Some of the (static) members are package, and hence are easily accessible by other
 * implementors of the 'local' cloud in the current package org.mmbase.bridge.implementation.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 * @version $Id$
 */
public abstract class BasicCloudContext implements CloudContext {
    private static final Logger log = Logging.getLoggerInstance(BasicCloudContext.class);

    /**
    * Link to the mmbase root
    */
    static MMBase mmb = null;

    /**
    * Transaction Manager to keep track of transactions
    */
    static TransactionManager transactionManager = null;

    /**
     * @javadoc
     * Temporary Node Manager for storing node during edits
     */
    static TemporaryNodeManager tmpObjectManager = null;

    // map of clouds by name
    private static final Set<String> localClouds = new HashSet<String>();

    // map of modules by name
    private static final Map<String, Module> localModules = new HashMap<String, Module>();

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
        synchronized (org.mmbase.module.core.MMBase.class) {
            if (mmb == null) {
                Iterator<org.mmbase.module.Module> i = org.mmbase.module.Module.getModules();
                if (i == null) {
                    if (java.lang.System.getProperty("mmbase.config") == null) {
                        throw new NotFoundException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + " : no property mmbase.config found)");
                    }
                    try {
                        org.mmbase.module.core.MMBaseContext.init();
                        org.mmbase.module.core.MMBase.getMMBase();
                        i = org.mmbase.module.Module.getModules();
                    } catch (java.lang.Exception ex) {
                        log.error("Error while trying to start MMBase from the bridge: " + ex.getMessage(), ex);
                    }
                    if (i == null) {
                        return false;
                    }
                }
                MMBase m = org.mmbase.module.core.MMBase.getMMBase();
                while (i.hasNext()) {
                    Module mod = ModuleHandler.getModule(i.next(), this);
                    localModules.put(mod.getName(), mod);
                }
                transactionManager = TransactionManager.getInstance();
                tmpObjectManager = transactionManager.getTemporaryNodeManager();
                localClouds.add("mmbase");
                assert m != null;
                mmb = m;
            }
        }
        return true;
    }

    @Override
    public ModuleList getModules() {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        ModuleList ml = new BasicModuleList(localModules.values());
        return ml;
    }

    @Override
    public Module getModule(String moduleName) throws NotFoundException {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        Module mod = localModules.get(moduleName);
        if (mod == null) {
            throw new NotFoundException("Module '" + moduleName + "' does not exist.");
        }
        return mod;
    }

    @Override
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
    @Override
    public Cloud getCloud(String cloudName) {
        checkExists(cloudName);
        return getCloud(cloudName, "anonymous", null);
    }

    @Override
    public Cloud getCloud(String cloudName, String authenticationType, Map<String, ?> loginInfo) throws NotFoundException  {
        checkExists(cloudName);
        UserContext userContext = mmb.getMMBaseCop().getAuthentication().login(authenticationType, loginInfo, null);
        if (userContext == null) {
            log.debug("Login failed");
            throw new java.lang.SecurityException("Login invalid (login-module: " + authenticationType + "  on " + BasicCloudContext.mmb.getMMBaseCop().getAuthentication());
        }
        // end authentication...

        if (userContext.getAuthenticationType() == null) {
            log.warn("Security implementation did not set 'authentication type' in the user object.");
        }
        return getCloud(cloudName, userContext);
    }

    @Override
    public Cloud getCloud(String cloudName, UserContext user) throws NotFoundException {
        checkExists(cloudName);
        return new BasicCloud(cloudName, user, this);
    }

    @Override
    public StringList getCloudNames() {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        return new BasicStringList(localClouds);
    }

    /**
     * @return String describing the encoding.
     * @since MMBase-1.6
     */
    @Override
    public String getDefaultCharacterEncoding() {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        return mmb.getEncoding();
    }

    @Override
    public java.util.Locale getDefaultLocale() {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        return mmb.getLocale();
    }

    @Override
    public java.util.TimeZone getDefaultTimeZone() {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        return mmb.getTimeZone();
    }

    @Override
    public FieldList createFieldList() {
        return new BasicFieldList();
    }

    @Override
    public NodeList createNodeList() {
        return new BasicNodeList();
    }

    @Override
    public RelationList createRelationList() {
        return new BasicRelationList();
    }

    @Override
    public NodeManagerList createNodeManagerList() {
        return new BasicNodeManagerList();
    }

    @Override
    public RelationManagerList createRelationManagerList() {
        return new BasicRelationManagerList();
    }

    @Override
    public ModuleList createModuleList() {
        return new BasicModuleList();
    }

    @Override
    public StringList createStringList() {
        return new BasicStringList();
    }

    @Override
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

    @Override
    public ActionRepository getActionRepository() throws NotFoundException {
        if (!check()) throw new BridgeException("MMBase has not been started, and cannot be started by this Class. (" + getClass().getName() + ")");
        // checkExists(cloudName);
        MMBaseCop cop = mmb.getMMBaseCop();
        if (cop == null) {
            throw new NotFoundException("MMBase not yet initialized");
        } else {
            return cop.getActionRepository();
        }
    }


    @Override
    public boolean isUp() {
        return mmb != null && mmb.getState() && check();
    }

    @Override
    public CloudContext assertUp() {
        if (! isUp()) {
            synchronized(MMBase.class) {
                while (! isUp()) {
                    try {
                        MMBase.class.wait();
                    } catch(InterruptedException ie) {
                        throw new BridgeException(ie);
                    }
                }
            }
        }
        return this;
    }


    @Override
    public SearchQueryHandler getSearchQueryHandler() {
        return mmb.getSearchQueryHandler();
    }


}
