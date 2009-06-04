/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.module;

import org.mmbase.module.core.MMBaseContext;
import java.util.concurrent.*;
import java.util.*;
import java.net.*;
import java.lang.reflect.*;
import org.xml.sax.*;

import org.mmbase.util.*;
import org.mmbase.util.xml.ModuleReader;

import org.mmbase.util.functions.*;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * An MMBase Module is an extension of this class, which is configured by an XML file in the &lt;mmbase
 * config dir&gt;/modules directory. All modules whose xml configuration file defines them as 'active' are
 * automaticly loaded and initialized.
 *
 * There are several modules which are more or less compulsary in MMBase, like the 'mmbaseroot'
 * module, the actual core of MMBase implemented by {@link org.mmbase.module.core.MMBase}, and the
 * 'jdbc' module.
 *
 * @author Rico Jansen
 * @author Rob Vermeulen (securitypart)
 * @author Pierre van Rooden
 *
 * @version $Id$
 */
public abstract class Module extends DescribedFunctionProvider {

    /**
     * State identifier for module startup time.
     */
    public final String STATE_START_TIME = "Start Time";

    private static final Logger log = Logging.getLoggerInstance(Module.class);

    // A map containing all currently loaded modules by name.
    private static Map<String, Module> modules;

    /**
     * This function returns the Module's version number as an Integer.
     * It takes no parameters.
     * This function can be called through the function framework.
     * @since MMBase-1.8
     */
    protected Function<Integer> getVersionFunction = new AbstractFunction<Integer>("getVersion") {
        private static final long serialVersionUID = 0L;
        public Integer getFunctionValue(Parameters arguments) {
            return getVersion();
        }
    };

    /**
     * This function returns the Module's maintainer as a String.
     * It takes no parameters.
     * This function can be called through the function framework.
     * @since MMBase-1.8
     */
    protected Function<String> getMaintainerFunction = new AbstractFunction<String>("getMaintainer") {
        private static final long serialVersionUID = 0L;
        public String getFunctionValue(Parameters arguments) {
            return getMaintainer();
        }
    };

    /**
     * Properties (initparameters) set by reading (or re-reading) the module configuration.
     */
    protected Map<String, String> properties;

    // the path to the Module configuration (xml) file, without the xml extension, and without the modules dir
    protected String configurationPath;

    // the state map, containing runtime-generated information as name-value pairs.
    private Map<String, String> states = new Hashtable<String, String>();


    // the name of the module maintainer
    private String maintainer;

    // the module version
    private int version;

    // startup call.
    private boolean started = false;

    private ScheduledFuture future;
    /**
     * @deprecated
     */
    public Module() {
        this(null);
    }

    public Module(String name) {
        super(name);
        addFunction(getVersionFunction);
        addFunction(getMaintainerFunction);
        String startedAt = (new Date(System.currentTimeMillis())).toString();
        setState(STATE_START_TIME, startedAt);

        // We used to call the 'maintaince' method of every module
        // every hour.
        // I doubt whether this is useful.
        // We could perhaps just as well remove all this, AFAIK  this
        // is never actually used.
        Calendar now = Calendar.getInstance();
        future =  ThreadPools.scheduler.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    Module.this.maintainance();
                }
            },
            3600 - (now.get(Calendar.MINUTE) * 60 + now.get(Calendar.SECOND)),  // some effort to run exactly at hour
            3600, TimeUnit.SECONDS);
        ThreadPools.identify(future, "Maintenance for '" + (name == null ? this.getClass().getName() : name) + "'");
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
    public static ModuleReader getModuleReader(String configurationPath) {
        try {
            InputSource is = getModuleLoader().getInputSource(configurationPath + ".xml");
            if (is == null) return null;
            return new ModuleReader(is);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * @since MMBase-1.9
     */
    public ModuleReader getModuleReader() {
        return Module.getModuleReader(configurationPath);
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
        future.cancel(true);
    }

    /**
     * Returns a state value by name.
     * @since MMBase-1.9
     */
    public String getState(String name) {
        return states.get(name);
    }

    /**
     * Sets a state value by name.
     * @since MMBase-1.9
     */
    public String setState(String name, String value) {
        return states.put(name, value);
    }

    /**
     * Returns the module's runtime-generated state information as a unmodifiable map with name-value pairs.
     * @since MMBase-1.9
     */
    public Map<String, String> getStates() {
        return Collections.unmodifiableMap(states);
    }

    /**
     * Sets an init-parameter key-value pair
     */
    public void setInitParameter(String key, String value) {
        if (properties != null) {
            properties.put(key, value);
        }
    }

    /**
     * Gets an init-parameter  key-value pair
     */
    public String getInitParameter(String key) {
        if (properties != null) {
            log.debug("Getting init parameter " + key + " for " + this);
            String value = properties.get(key);
            if (value == null) {
                key = key.toLowerCase();
                value = properties.get(key);
                // Can also set properties in web.xml/context.xml
                if (value == null && MMBaseContext.isInitialized() && MMBaseContext.getServletContext() != null) {
                    value = MMBaseContext.getServletContext().getInitParameter(getName() + "." + key);
                }
                // try the system property, set on the JVM commandline
                // i.e. you could provide a value for the mmbaseroot "machinename" property by specifying:
                // -Dmmbaseroot.machinename=myname
                if (value == null) {
                    value = System.getProperty(getName() + "." + key);
                }
            }
            return value;
        } else {
            log.error("getInitParameters(" + key + "): No properties found, called before they where loaded");
        }
        return null;
    }

    /**
     * Gets own modules properties
     */
    public Map<String, String> getInitParameters() {
        return properties;
    }

    /**
     * Override properties through application context
     * @since MMBase 1.8.5
     */
    public void loadInitParameters() {
        loadInitParameters("mmbase/" + getName());
    }

    /**
     * Override properties through application context
     * @param contextPath path in application context where properties are located
     * @since MMBase 1.8.5
     */
    protected void loadInitParameters(String contextPath) {
        try {
            Map<String, String> contextMap = ApplicationContextReader.getProperties(contextPath);
            properties.putAll(contextMap);
            log.info("Put for " + getName() + " " + contextMap);
        } catch (javax.naming.NamingException ne) {
            log.debug("Can't obtain properties from application context: " + ne.getMessage());
        }
    }

    /**
     * Returns an iterator of all the modules that are currently active.
     * This function <code>null</code> if no attempt has the modules have (not) yet been to loaded.
     * Unlike {@link #getModule}, this method does not automatically load modules if this hadn't occurred yet.
     * @return an <code>Iterator</code> with all active modules
     */
    public static final Iterator<Module> getModules() {
        if (modules == null) {
            return null;
        } else {
            return modules.values().iterator();
        }
    }


    /**
     * Provide some info on the module;
     * By default, this returns the module description for the default locale
     * @deprecated use getDescription
     */
    public String getModuleInfo() {
        String value = getDescription();
        if (value != null) {
            return value;
        } else {
            return "No module info provided";
        }
    }

    /**
     * maintenance call called by the admin module every 3600 seconds.
     *
     * @deprecated Method name is not correct english. And btw the complete
     * method is dubious. It is called once an hour for every
     * module. But I know of now modules which actually do something useful here,
     * because an hours is always either too short, or too long.
     */
    public void maintainance() {
    }

    /**
     * Calls shutdown of all registered modules.
     *
     * @since MMBase-1.6.2
     */
    public static synchronized final void shutdownModules() {
        if (modules != null) {
            for (Module m : modules.values()) {
                log.service("Shutting down " + m.getName());
                m.shutdown();
                log.service("Shut down " + m.getName());
            }
            modules.clear();
        }
    }

    public static synchronized final void startModules() {
        // call the onload to get properties
        log.service("Starting modules " + modules.keySet());
        for (Module mod : modules.values()) {
            if (Thread.currentThread().isInterrupted()) {
                log.info("Interrupted");
                return;
            }
            if( log.isDebugEnabled() ) {
                log.debug("Starting module " + mod + "");
            }
            try {
                mod.onload();
            } catch (Exception f) {
                log.warn("Exception in onload of module '" + mod + "' ! " + f.getMessage(), f);
            }
        }
        // so now really give em their init
        if (log.isDebugEnabled()) {
            log.debug("Initing the modules " + modules + "");
        }
        for (Module mod : modules.values()) {
            if (Thread.currentThread().isInterrupted()) {
                log.info("Interrupted");
                return;
            }
            log.info("Starting module " + mod.getName());
            try {
                mod.startModule();
            } catch (Exception f) {
                log.error("Exception in startModule of module '" + mod + "' ! " + f.getMessage(), f);
            }
        }
    }

    /**
     * @since MMBase-1.8.3
     */
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
     * Since modules normally all have a different class, you can also obtain a module by its
     * Class, in stead of by its name. The advantage is that you don't need to cast.
     * @param clazz The class of the desired Module
     * @return A Module instance or <code>null</code> if no such module.
     * @since MMBase-1.9
     */
    public static <C extends Module> C getModule(Class<C> clazz, boolean check) {
        if (check) checkModules(true);
        if (modules != null) {
            for (Module m : modules.values()) {
                if (clazz.isInstance(m)) {
                    return (C) m;
                }
            }
        }
        return null;
    }
    public static <C extends Module> C getModule(Class<C> clazz) {
        return getModule(clazz, true);
    }

    /**
     * Makes sure that modules are loaded and started.
     * @since MMBase-1.9
     */
    private static synchronized void checkModules(boolean startOnLoad) {
        // are the modules loaded yet ? if not load them
        if (modules == null) { // still null after obtaining lock
            log.service("Loading MMBase modules...");
            modules = loadModulesFromDisk();
            if (log.isDebugEnabled()) {
                log.debug("Modules not loaded, loading them..");
            }
            if (startOnLoad) {
                startModules();
            }


        }
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
        checkModules(startOnLoad);
        // try to obtain the ref to the wanted module
        Module obj = modules.get(name.toLowerCase());
        if (obj == null) { // try case sensitivily as well?
            obj =  modules.get(name);
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
     */
    @SuppressWarnings("deprecation")
    private static synchronized Map<String, Module>  loadModulesFromDisk() {
        Map<String, Module> results = Collections.synchronizedMap(new TreeMap<String, Module>());
        ResourceLoader moduleLoader = getModuleLoader();
        Collection<String> mods = moduleLoader.getResourcePaths(ResourceLoader.XML_PATTERN, false/* non-recursive*/);
        log.info("In " + moduleLoader + " the following module XML's were found " + mods);
        for (String file : mods) {
            ModuleReader parser = null;
            try {
                InputSource is = moduleLoader.getInputSource(file);
                if (is != null) parser = new ModuleReader(is);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            if (parser != null && parser.getStatus().equals("active")) {
                // obtain module name. Use the filename of the xml if the name property is not set
                String moduleName = parser.getName();
                if (moduleName == null) {
                    moduleName = ResourceLoader.getName(file);
                }
                String className = parser.getClassName();
                // try starting the module and give it its properties
                try {
                    log.service("Loading module " + moduleName + " with class " + className);
                    Module mod;
                    if (parser.getURLString() != null){
                        log.service("loading module from jar " + parser.getURLString());
                        URL url = new URL(parser.getURLString());
                        URLClassLoader c = new URLClassLoader(new URL[]{url}, Module.class.getClassLoader());
                        Class<?> newClass = c.loadClass(className);
                        try {
                            Constructor<?> constructor = newClass.getConstructor(String.class);
                            mod = (Module) constructor.newInstance(moduleName);
                        } catch (NoSuchMethodException nsme) {
                            log.warn(nsme);
                            mod = (Module) newClass.newInstance();
                            mod.setName(moduleName);
                        }
                    } else {
                        Class<?> newClass = Class.forName(className);
                        try {
                            Constructor<?> constructor = newClass.getConstructor(String.class);
                            mod =  (Module) constructor.newInstance(moduleName);
                        } catch (NoSuchMethodException nsme) {
                            log.service("Constructor with no name-argument is deprecated " + nsme.getMessage());
                            mod = (Module) newClass.newInstance();
                            mod.setName(moduleName); // I think a module has one name.
                        }
                    }

                    mod.configurationPath = ResourceLoader.getName(file);

                    results.put(moduleName, mod);

                    mod.setMaintainer(parser.getMaintainer());
                    mod.setVersion(parser.getVersion());
                    parser.getLocalizedDescription(mod.getLocalizedDescription());
                    parser.getLocalizedGUIName(mod.getLocalizedGUIName());

                    mod.properties = parser.getProperties();
                    mod.loadInitParameters();

                } catch (ClassNotFoundException cnfe) {
                    log.error("Could not load class with name '" + className + "', " +
                              "which was specified in the module:'" + file + " '(" + cnfe + ")" );
                } catch (Throwable e) {
                    log.error("Error while loading module class " + e.getClass() + ": " + e.getMessage(), e);
                }
            }
        }
        return results;
    }

}
