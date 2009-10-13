/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module;

import java.util.*;
import java.net.*;
import org.xml.sax.*;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.*;
import org.mmbase.util.xml.ModuleReader;

import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * An MMBase Module is an extension of this class, which is configured by an XML in the &lt;mmbase
 * config dir &gt;/modules directory. All XML's (which are defined 'active') in this directory are
 * automaticly loaded, and all found 'Module's are then initialized.
 *
 * There are several Modules which are more or less compulsary in MMBase, like the 'mmbaseroot'
 * module, the actual core of MMBase implemented by {@link org.mmbase.module.core.MMBase}, and the
 * 'jdbc' module.
 *
 * @author Rico Jansen
 * @author Rob Vermeulen (securitypart)
 * @author Pierre van Rooden
 *
 * @version $Id$
 */
public abstract class Module extends FunctionProvider {

    /**
     * @javadoc
     */
    static Map modules;

    private static final Logger log = Logging.getLoggerInstance(Module.class);

    /**
     * This function returns the Module's version number as an Integer.
     * It takes no parameters.
     * This function can be called through the function framework.
     * @since MMBase-1.8
     */
    protected Function getVersionFunction = new AbstractFunction("getVersion", Parameter.EMPTY, ReturnType.INTEGER) {
        public Object getFunctionValue(Parameters arguments) {
            return new Integer(getVersion());
        }
    };

    /**
     * This function returns the Module's maintainer as a String.
     * It takes no parameters.
     * This function can be called through the function framework.
     * @since MMBase-1.8
     */
    protected Function getMaintainerFunction = new AbstractFunction("getMaintainer", Parameter.EMPTY, ReturnType.STRING) {
        public Object getFunctionValue(Parameters arguments) {
            return getMaintainer();
        }
    };

    String moduleName = null;
    Hashtable state = new Hashtable();
    Hashtable properties; // would like this to be LinkedHashMap (predictable order)
    String maintainer;
    int version;

    // startup call.
    private boolean started = false;

    public Module() {
        this(null);
    }

    public Module(String name) {
        super();
        setName(name);
        addFunction(getVersionFunction);
        addFunction(getMaintainerFunction);
        String startedAt = (new Date(System.currentTimeMillis())).toString();
        state.put("Start Time", startedAt);
    }

    public final void setName(String name) {
        if (moduleName == null) {
            moduleName = name;
        }
    }

    /**
     * @since MMBase-1.8
     */
    public static ResourceLoader getModuleLoader() {
        return ResourceLoader.getConfigurationRoot().getChildResourceLoader("modules");
    }

    /**
     * @since MMBase-1.8
     */
    public static ModuleReader getModuleReader(String moduleName) {
        try {
            InputSource is = getModuleLoader().getInputSource(moduleName + ".xml");
            if (is == null) return null;
            return new ModuleReader(is);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    /**
     * Starts the module.
     * This module calls the {@link #init()} of a module exactly once.
     * In other words, once the init() is called, it does not call it again.
     * This method is final and cannot be overridden.
     * It is used to safely initialize modules during startup, and allows other modules
     * to force the 'startup' of another module without risk.
     */
    public final void startModule() {
        if (started) return;
        synchronized(Module.class) {
            init();
            started = true;
        }
    }

    /**
     * Returns whether the module has started (has been initialized or is in
     * its initialization fase).
     */
    public final boolean hasStarted() {
        return started;
    }

    /**
     * Initializes the module.
     * Init must be overridden to read the environment variables it needs.
     * <br />
     * This method is called by {@link #startModule()}, which makes sure it is not called
     * more than once. You should not call init() directly, call startModule() instead.
     */
    public void init() {
    }

    /**
     * prepares the module when loaded.
     * Onload must be overridden to execute methods that need to be performed when the module
     * is loaded but before any other modules are initailized.
     * <br />
     * This method is called by {@link #startModules()}. You should not call onload() directly.
     * @scope protected
     */
    public void onload() {
    }

    /**
     * Shuts down the module. This method is called by shutdownModules.
     *
     * @since MMBase-1.6.2
     */
    protected void shutdown() {
        // on default, nothing needs to be done.
    }

    /**
     * state, returns the state hashtable that is/can be used to debug. Should
     * be overridden when live state should be done.
     */
    public Hashtable state() {
        return state;
    }

    /**
     * Sets an init-parameter key-value pair
     */
    public void setInitParameter(String key,String value) {
        if (properties != null) {
            properties.put(key, value);
        }
    }

    /**
     * Gets an init-parameter  key-value pair
     */
    public String getInitParameter(String key) {
        if (properties != null) {
            String value=(String) properties.get(key);
            if (value == null) {
                key = key.toLowerCase();
                value = (String) properties.get(key);
                if (value == null && MMBaseContext.isInitialized() && MMBaseContext.getServletContext() != null) {
                    value = MMBaseContext.getServletContext().getInitParameter(getName() + "." + key);
                }
                // try the system property, set on the JVM commandline
                // i.e. you could provide a value for the mmbaseroot "machinename" property by specifying:
                // -Dmmbaseroot.machinename=myname
                if (value == null) {
                    value = System.getProperty(moduleName+"."+key);
                }
            }
            return value;
        } else {
            log.error("getInitParameters(" + key + "): No properties found, called before they where loaded");
        }
        return null;
    }

    /**
     * Returns the properties to the subclass.
     */
    protected Hashtable getProperties(String propertytable) {
        //String filename="/usr/local/vpro/james/adminopen/modules/";
        //return results;
        return null;
        //return Environment.getProperties(this,propertytable);
    }

    /**
     * Returns one propertyvalue to the subclass.
     */
    protected String getProperty(String name, String var) {
        return "";
    }

    /**
     * Gets own modules properties
     */
    public Hashtable getInitParameters() {
        return properties;
    }

    /**
     * override properties through application context
     * @param contextPath path in application context where properties are located
     * @since MMBase 1.8.2
     * @deprecated Just use {@link #loadInitParameters()}
     */
    public void loadInitParameters(String contextPath) {
        try {
            Map contextMap = ApplicationContextReader.getProperties(contextPath);
            log.service("Loading parameters " + contextPath + " from " + contextPath);
            if (!contextMap.isEmpty()) {
                properties.putAll(contextMap);
            }

        } catch (javax.naming.NamingException ne) {
            log.debug("Can't obtain properties from application context: " + ne.getMessage());
        }
    }

    /**
     * override properties through application context
     * @since MMBase 1.8.5
     */
    public void loadInitParameters() {
        loadInitParameters("mmbase/" + getName());

    }

    /**
     * Returns an iterator of all the modules that are currently active.
     * This function <code>null</code> if no attempt has the modules have (not) yet been to loaded.
     * Unlike {@link #getModule}, this method does not automatically load modules if this hadn't occurred yet.
     * @return an <code>Iterator</code> with all active modules
     */
    public static final Iterator getModules() {
        if (modules == null) {
            return null;
        } else {
            return modules.values().iterator();
        }
    }

    /**
     *  Returns the name of the module
     * @return the module name
     */
    public final String getName() {
        return moduleName; // org.mmbase
    }

    /**
     * provide some info on the module
     */
    public String getModuleInfo() {
        return "No module info provided";
    }

    /**
     * maintainance call called by the admin module every x seconds.
     */
    public void maintainance() {
    }


    /**
     * Calls shutdown of all registered modules.
     *
     * @since MMBase-1.6.2
     */
    public static synchronized final void shutdownModules() {
        //log.debug("Shutting down modules by ", new Exception());
        Iterator i = getModules();
        while (i != null && i.hasNext()) {
            Module m = (Module) i.next();
            log.service("Shutting down " + m.getName());
            m.shutdown();
            log.service("Shut down " + m.getName());
        }
        modules = null;
    }

    public static synchronized final void startModules() {
        // call the onload to get properties
        log.service("Starting modules " + modules.keySet());
        for (Iterator i = modules.values().iterator(); i.hasNext();) {
            if (Thread.currentThread().isInterrupted()) {
                log.info("Interrupted");
                return;
            }
            Module mod = (Module)i.next();
            if( log.isDebugEnabled() ) {
                log.debug("startModules(): modules.onload(" + mod + ")");
            }
            try {
                mod.onload();
            } catch (Exception f) {
                log.warn("startModules(): modules(" + mod + ") not found to 'onload'!", f);
            }
        }
        // so now really give em their init
        if (log.isDebugEnabled()) {
            log.debug("startModules(): init the modules(" + modules + ")");
        }
        for (Iterator i = modules.values().iterator(); i.hasNext();) {
            if (Thread.currentThread().isInterrupted()) {
                log.info("Interrupted");
                return;
            }
            Module mod = (Module) i.next();
            log.info("Starting module " + mod.getName());
            if ( log.isDebugEnabled()) {
                log.debug("startModules(): mod.startModule(" + mod + ")");
            }
            try {
                mod.startModule();
            } catch (Exception f) {
                log.error("startModules(): module(" + mod + ") not found to 'init'!: " + f.getClass() + ": " + f.getMessage());
                log.error(Logging.stackTrace(f));
            }
        }
    }

    public static boolean hasModule(String name) {
        boolean check = modules.containsKey(name.toLowerCase());
        if (!check) {
            check = modules.containsKey(name);
        }
        return check;
    }

    /**
     * Retrieves a reference to a Module.
     * This call does not ensure that the requested module has been initialized.
     *
     * @param name the name of the module to retrieve
     * @return a refernce to a <code>Module</code>, or <code>null</code> if the
     *      module does not exist or is inactive.
     */
    public static Module getModule(String name) {
        return getModule(name, false);
    }

    /**
     * Retrieves a reference to a Module.
     * If you set the <code>startOnLoad</code> to <code>true</code>,
     * this call ensures that the requested module has been initialized by
     * calling the {@link #startModule()} method.
     * This is needed if you need to call Module methods from the init() of
     * another module.
     *
     *
     * @param name the name of the module to retrieve
     * @param startOnLoad if true, the code makes sure the module has been started
     * @return a reference to a <code>Module</code>, or <code>null</code> if the
     *      module does not exist or is inactive.
     */
    public static Module getModule(String name, boolean startOnLoad) {
        // are the modules loaded yet ? if not load them
            synchronized(Module.class) {
                if (modules == null) { // still null after obtaining lock
                    log.service("Loading MMBase modules...");
                    modules = loadModulesFromDisk();
                    if (log.isDebugEnabled()) {
                        log.debug("getModule(" + name + "): Modules not loaded, loading them..");
                    }
                    startModules();
                    // also start the maintaince thread that calls all modules 'maintanance' method every x seconds
                    new ModuleProbe().start();
                }
            }
        // try to obtain the ref to the wanted module
        Module obj = (Module) modules.get(name.toLowerCase());
        if (obj == null) { // try case sensitivily as well?
            obj = (Module) modules.get(name);
        }
        if (obj != null) {
            // make sure the module is started, as this method could
            // have been called from the init() of another Module
            if (startOnLoad) {
                obj.startModule();
            }
            return obj;
        } else {
            log.warn("The module '" + name + "' could not be found!");
            return null;
        }
    }

    public String getMaintainer() {
        return maintainer;
    }

    public void setMaintainer(String m) {
        maintainer = m;
    }

    public void setVersion(int v) {
        version = v;
    }

    public int getVersion() {
        return version;
    }

    /**
     * Loads all module-xml present in <mmbase-config-dir>/modules.
     * @return A HashTable with <module-name> --> Module-instance
     * @scope  private (only called from getModule)
     */
    private static synchronized Hashtable loadModulesFromDisk() {
        Hashtable results = new Hashtable();
        ResourceLoader moduleLoader = getModuleLoader();
        Collection modules = moduleLoader.getResourcePaths(ResourceLoader.XML_PATTERN, false/* non-recursive*/);
        log.info("In " + moduleLoader + " the following module XML's were found " + modules);
        Iterator i = modules.iterator();
        while (i.hasNext()) {
            String file = (String) i.next();
            String fileName = ResourceLoader.getName(file);
            ModuleReader parser = null;
            try {
                InputSource is = moduleLoader.getInputSource(file);
                if (is != null) parser = new ModuleReader(is);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            if (parser != null && parser.getStatus().equals("active")) {
                String className = parser.getClassName();
                // try starting the module and give it its properties
                try {
                    log.service("Loading module " + fileName + " with class " + className);
                    Module mod;
                    if (parser.getURLString() != null){
                        log.service("loading module from jar " + parser.getURLString());
                        URL url = new URL(parser.getURLString());
                        URLClassLoader c = new URLClassLoader(new URL[]{url}, Module.class.getClassLoader());
                        Class newClass = c.loadClass(className);
                        mod = (Module) newClass.newInstance();
                    } else {
                        Class newClass = Class.forName(className);
                        mod = (Module) newClass.newInstance();
                    }

                    results.put(fileName, mod);


                    mod.properties = new Hashtable(parser.getProperties());

                    // set the module name property using the module's filename
                    // maybe we need a parser.getName() function to improve on this
                    mod.setName(fileName);

                    mod.setMaintainer(parser.getMaintainer());
                    mod.setVersion(parser.getVersion());

                    mod.loadInitParameters();

                } catch (ClassNotFoundException cnfe) {
                    log.error("Could not load class with name '" + className + "', " +
                              "which was specified in the module:'" + file + " '(" + cnfe + ")" );
                } catch (Throwable e) {
                    log.error("Error while loading module class" + Logging.stackTrace(e));
                }
            }
        }
        return results;
    }

}
