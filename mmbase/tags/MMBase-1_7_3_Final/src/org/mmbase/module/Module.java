/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.module;

import java.util.*;
import java.io.*;
import java.net.*;

import javax.servlet.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * An MMBase Module is an extension of this class, which is configured by an XML in the &lt;mmbase
 * config dir &gt;/modules directory. All XML's (which are defined 'active') in this directory are
 * automaticly loaded, and all found 'Module's are then initialized.
 *
 * There are several Modules which are more or less compulsary in MMBase, like the 'mmbaseroot'
 * module, the actual core of MMBase implemented by {@see org.mmbase.module.core.MMBase}, and the
 * 'jdbc' module.
 *
 * @author Rico Jansen
 * @author Rob Vermeulen (securitypart)
 * @author Pierre van Rooden
 *
 * @version $Id: Module.java,v 1.52 2004-03-26 14:59:20 michiel Exp $
 */
public abstract class Module {

    private static final Logger log = Logging.getLoggerInstance(Module.class);
    
    static Map modules;
    static ModuleProbe mprobe;
    
    String moduleName = null;
    Hashtable state = new Hashtable();
    Hashtable mimetypes;
    Hashtable properties;
    String maintainer;
    int    version;
    
    // startup call.
    private boolean started = false;
    
    public Module() {
        String startedAt = (new Date(System.currentTimeMillis())).toString();
        state.put("Start Time", startedAt);        
    }
    
    public final void setName(String name) {
        if (moduleName == null) {
            moduleName = name;
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
    public abstract void init();
    
    /**
     * prepares the module when loaded.
     * Onload must be overridden to execute methods that need to be performed when the module
     * is loaded but before any other modules are initailized.
     * <br />
     * This method is called by {@link #startModules()}. You should not call onload() directly.
     * @scope protected
     */
    public abstract void onload();
    
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
            properties.put(key,value);
        }
    }
    
    /**
     * Gets an init-parameter  key-value pair
     */
    public String getInitParameter(String key) {
        if (properties != null) {
            String value=(String)properties.get(key);
            if (value == null) {
                key = key.toLowerCase();
                value = (String)properties.get(key);
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
     * getMimeType: Returns the mimetype using ServletContext.getServletContext which returns the servlet context
     * which is set when servscan is loaded.
     * Fixed on 22 December 1999 by daniel & davzev.
     * XXX: why is this in Module???
     * @param ext A String containing the extension.
     * @return The mimetype.
     */
    public String getMimeType(String ext) {
        return getMimeTypeFile("dummy."+ext);
    }
    
    public String getMimeTypeFile(String fileName) {
        ServletContext sx = MMBaseContext.getServletContext();
        String mimeType = sx.getMimeType(fileName);
        if (mimeType == null) {
            log.warn("getMimeType(" + fileName + "): Can't find mimetype retval=null -> setting mimetype to default text/html");
            mimeType = "text/html";
        }
        return mimeType;
    }
    
    /**
     * Calls shutdown of all registered modules.
     *
     * @since MMBase-1.6.2
     */
    public static synchronized final void shutdownModules() {
        Iterator i = getModules();
        while (i.hasNext()) {
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
            Module mod = (Module)i.next();
            if( log.isDebugEnabled() ) {
                log.debug("startModules(): modules.onload(" + mod + ")");
            }
            try {
                mod.onload();
            } catch (Exception f) {
                log.warn("startModules(): modules(" + mod + ") not found to 'onload'!");
                f.printStackTrace();
            }
        }
        // so now really give em their init
        if (log.isDebugEnabled()) {
            log.debug("startModules(): init the modules(" + modules + ")");
        }
        for (Iterator i = modules.values().iterator(); i.hasNext();) {
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
    
    /**
     * Retrieves a reference to a Module.
     * This call does not ensure that the requested module has been initialized.
     * XXX: return type Object in stead of Module?
     *
     * @param name the name of the module to retrieve
     * @return a refernce to a <code>Module</code>, or <code>null</code> if the
     *      module does not exist or is inactive.
     */
    public static Object getModule(String name) {
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
     * XXX: return type Object in stead of Module?
     *
     * @param name the name of the module to retrieve
     * @param startOnLoad whetehr to make sure the module has been started or not.
     * @return a reference to a <code>Module</code>, or <code>null</code> if the
     *      module does not exist or is inactive.
     */
    public static Object getModule(String name, boolean startOnLoad) {
        // are the modules loaded yet ? if not load them
        if (modules == null) {
            synchronized(Module.class) {
                if (modules == null) { // still null after obtaining lock
                    log.service("Loading MMBase modules...");
                    modules = loadModulesFromDisk();
                    if (log.isDebugEnabled()) {
                        log.debug("getModule(" + name + "): Modules not loaded, loading them..");
                    }
                    startModules();
                    // also start the maintaince thread that calls all modules 'maintanance' method every x seconds
                    mprobe = new ModuleProbe(modules);
                }
            }
        }
        // try to obtain the ref to the wanted module
        Object obj = modules.get(name.toLowerCase());
        if (obj == null) { // try case sensitivily as well?
            obj = modules.get(name);
        }
        
        if (obj != null) {
            // make sure the module is started, as this method could
            // have been called from the init() of another Module
            if (startOnLoad) { 
                ((Module) obj).startModule();
            }
            return obj;
        } else {
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
    
    public static synchronized Hashtable loadModulesFromDisk() {
        Hashtable results = new Hashtable();
        File dir = new File(MMBaseContext.getConfigPath(), "modules");
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                if (file.canRead() && file.isFile() && fileName.endsWith(".xml")) {
                    fileName = fileName.substring(0, fileName.length() - 4);
                    XMLModuleReader parser;
                    try {
                        parser = new XMLModuleReader(file.getAbsolutePath());
                    } catch (Throwable t) {
                        log.error("Could not load module with xml '" + file + "': " + t.getMessage());
                        continue;
                    }
                    if (parser.getStatus().equals("active")) {
                        String className = parser.getClassFile();
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
                            
                            mod.properties =  parser.getProperties();

                            // set the module name property using the module's filename
                            // maybe we need a parser.getModuleName() function to improve on this
                            mod.setName(fileName);

                            mod.setMaintainer(parser.getModuleMaintainer());
                            mod.setVersion(parser.getModuleVersion());
                        } catch (ClassNotFoundException cnfe) {
                            log.error("Could not load class with name '" + className + "', " +
                                      "which was specified in the module:'" + file + ".xml'(" + cnfe + ")" );
                        } catch (Exception e) {
                            log.error("Error while loading module class" + Logging.stackTrace(e));
                        }
                    }
                }
            }
        }
        return results;
    }
    
}
