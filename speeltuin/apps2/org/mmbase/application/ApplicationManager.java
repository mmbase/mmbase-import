/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.builders.Versions;

import java.io.File;
import java.util.*;

/**
 * The application manager, manages all MMBase applications.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class ApplicationManager {
    private static Logger log = Logging.getLoggerInstance(ApplicationManager.class.getName());
    private MMBase mmbase = null;
    private Cloud cloud = null;
    
    // The versions builder is used to check application's version numbers
    static private Versions versions = null;
    // This is the location where the deployed applications are
    static private String deployedPath = null;
    // This is the location where the applications are
    static private String applicationpath = null;
    // This is the location where the MMBase builders are
    static private String MMBaseBuilderPath = null;
    // If MMBase is running in kiosk mode, the functionality will be restricted
    static private boolean kioskmode = false;
    // Contains all applications key=applicationname value=reference to application
    private Hashtable applications = new Hashtable();
    // Contains all local available applications key=applicationname value=reference to AvailableApplication
    private Hashtable localApplications = new Hashtable();
    
    // manager for all running application tasks
    private ApplicationTaskManager ataskmanager = null;

    // Remote Provider Manager
    private RemoteProviderManager rpm = null;
    
    // Provider
    private Provider provider = null;
    
    
    /**
     * creates a new application manager
     * @param mmbase reference to MMBase
     */
    public ApplicationManager(MMBase mmbase) {
        log.info("Starting Application Manager");
        this.mmbase=mmbase;
        ataskmanager = new ApplicationTaskManager(this,mmbase);
        
        // set the locations where applications and builders can be found.
        applicationpath = MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator;
        deployedPath = applicationpath+"deployed"+File.separator;
        MMBaseBuilderPath = MMBaseContext.getConfigPath()+File.separator+"builders"+File.separator;
        
        // Use the MMCI to communicate with MMBase
        CloudContext cloudcontext = LocalContext.getCloudContext();
        cloud = cloudcontext.getCloud("mmbase");
        
        // Get the versions builder
        versions = (Versions) mmbase.getMMObject("versions");
        if(versions==null) {
            log.error("Versions builder not installed, Applications will not be available.");
            // throw exception
        }
    }
    
    public void init() {
        // Also good to see in the logfile which applications are loaded
	loadApplications();
        showApplications();

	//load provider
	provider = new Provider(this);
	// load remoteprovidermanager
	rpm = new RemoteProviderManager(this);

	// init everything
	provider.init();
	rpm.init();
    }
    
    /**
     * give an application.
     * @param name the name of the application to return
     * @return the application
     */
    public Application getApplication(String name) {
        return (Application)applications.get(name);
    }
    
    /**
     * give all applications. If applications are not loaded yet, they will be now.
     * the key of the hashtable contains the name of the application.
     * the value is a reference to the actual application.
     * @return all applications
     */
    public Hashtable getApplications() {
        loadApplications();
        return applications;
    }
    
    /**
     * load all applications. Only the first time when this method is invoked all applications will be loaded.
     * If the method is invoked a second time, not loaded applications wil be loaded.
     */
    public void  loadApplications() {
        File appDir = new File(deployedPath);
        
        if(!appDir.isDirectory()) {
            log.error(deployedPath+" the place where applications should be is not a directory.");
        }
        
        String files[] = appDir.list();
        if (files==null) {
            log.info("The application directory "+deployedPath+" is empty");
        }
        
        for (int i=0; i<files.length;i++) {
            File app = new File(deployedPath+files[i]+File.separator+"description.xml");
            if(app.exists() && !applications.containsKey(files[i])) {
                Application application = new Application(files[i],this,mmbase);
                applications.put(files[i],application);
            }
        }
    }
    
    /**
     * create a new application
     */
    public Application createApplication(String name) {
	    if (applications.containsKey(name)) {
		    log.warn("Application "+name+" does already exists");
		    return null;
	    }
       	Application application = new Application(name,this,mmbase);
        applications.put(name,application);
        return application;
    }
    
    /**
     * remove an application. Also from disk, the application will not exist anymore
     */
    public void removeApplication(String name) {
        try {
            if(!applications.containsKey(name)) {
                throw new Exception("Application "+name+" does not exist");
            }
            Application application = getApplication(name);
            application.delete();
            applications.remove(name);
        } catch (Exception e) {
            log.error("Cannot remove application "+name);
        }
    }
    
    /**
     * tell if MMBase runs in kiosk mode.
     * If MMBase runs in kiosk mode, applications can not be written, loaded, or changed.
     * @return true if MMBase runs in kiosk mode, false otherwise
     */
    public static boolean getKioskmode() {
        return kioskmode;
    }
    
    /**
     * give the version builder.
     * @return the version builder
     */
    public static Versions getVersionBuilder() {
        return versions;
    }
    
    /**
     * give the path were the deployed applications are installed.
     * @return the deployed application path
     */
    public static String getDeployedPath() {
        return deployedPath;
    }
    
    /**
     * give the path were the applications are installed.
     * @return the application path
     */
    public static String getApplicationPath() {
        return applicationpath;
    }
    
    /**
     * give the path were the MMBase builders are installed.
     * @return the builder path
     */
    public static String getMMBaseBuilderPath() {
        return MMBaseBuilderPath;
    }
    
    /**
     * show all applications. This method was added for testing purposes.
     */
    private void showApplications() {
        loadApplications();
        log.info("Applications available:");
        for (Enumeration e = getApplications().keys();e.hasMoreElements();) {
            log.info(""+e.nextElement());
        }
    }
    
    public ApplicationTaskManager getApplicationTaskManager() {
        return(ataskmanager);
    }
    
    public RemoteProviderManager getRemoteProviderManager() {
        return(rpm);
    }
   
    /**
     * get the provider 
     */
    public Provider getProvider() {
        return(provider);
    }
    
    /**
     * get applications that are remotely available.
     * @return all remotely available applications
     */
    public Hashtable getRemoteApplications() {
	    log.error("getRemoteApplicatoins()");
	   return rpm.getAvailableApplications(); 
    }

    /**
     * get applications that are localy available.
     * @return all local available applications
     */
    public Hashtable getLocalApplications() {
        String localApplicationLocation = MMBaseContext.getConfigPath()+File.separator+"applications"+File.separator+"import"+File.separator;
        File appDir = new File(localApplicationLocation);
        
        if(!appDir.isDirectory()) {
            log.error(localApplicationLocation+" the place where local applictions should be is not a directory.");
        }
        
        String files[] = appDir.list();
        if (files==null) {
            log.info("The local application directory "+localApplicationLocation+" is empty");
        }
        
        for (int i=0; i<files.length;i++) {
            File app = new File(localApplicationLocation+files[i]+File.separator+"description.xml");
            if(app.exists() && !localApplications.containsKey(files[i])) {
                AvailableApplication availableApplication = new AvailableApplication(files[i],"local",this);
                localApplications.put(files[i],availableApplication);
            }
        }
        return localApplications;
    }
}
