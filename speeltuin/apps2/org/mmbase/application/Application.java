/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.Versions;

import java.io.File;
import java.util.*;
import java.util.jar.*;

import org.w3c.dom.*;

/**
 * An application contains some functionality for a MMBase website. The application can
 * consist of: web pages, editors, documentation, builders, relations, and content.
 * Applications support by installing, removing, updating, and synchronization of sites.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class Application {
    private static Logger log = Logging.getLoggerInstance(Application.class.getName());
    private MMBase mmbase = null;
    private ApplicationManager appManager = null;
    
    // The reader for the application configuration file
    private XMLBasicReader reader = null;
    // Builder for version information
    private Versions versions = null;
    // The components of the application
    private Hashtable components = new Hashtable();
    // Gives information to the human interface
    private Hashtable status = new Hashtable();
    // the location of an application
    private String applicationLocation = null;
    
    // all application variables
    private String name = null;
    private Integer installedversion = null;
    private Boolean autodeploy = null;
    private Integer configurationversion = null;
    private String description = null;
    private String maintainername = null;
    private String maintaineremail = null;
    private String maintainercompany = null;
    private String maintainerinfo = null;
    private String maintainerhomebase = null;
    
    /**
     * initialize application
     * @param name name of the application
     * @param manager application manager
     * @param mmbase MMBase
     */
    public Application(String name, ApplicationManager manager, MMBase mmb) {
        mmbase = mmb;
        appManager = manager;
        applicationLocation = ApplicationManager.getDeployedPath()+name+File.separator;
        versions = ApplicationManager.getVersionBuilder();
        
        File file = new File(applicationLocation+File.separator+"description.xml");
        if(file.exists()) {
            reader = new XMLBasicReader(applicationLocation+File.separator+"description.xml");
            if(reader!=null) {
                log.info("\tStarting application "+name);
                setComponents();
                checkAutodeploy();
            } else {
                log.error("Application reader error for application "+name);
            }
        } else {
            log.info("Creating application "+name);
            // set all variable to default or empty.
            // this prevents the getMethods to try to read from the ApplicationReader
            setName(name);
            setAutoDeploy(false);
            setConfigurationVersion(1);
            setDescription("");
            setMaintainerName("");
            setMaintainerEmail("");
            setMaintainerCompany("");
            setMaintainerInfo("");
            setMaintainerHomebase("");
            setComponents();
        }
    }
   
    /**
     * write the configuration of the Application
     */
    public void writeConfiguration() {
        File file = new File(applicationLocation);
        if(!file.exists()) {
            file.mkdirs();
        }
        try {
            ApplicationConfigWriter acw = new ApplicationConfigWriter(applicationLocation+File.separator+"description.xml",this);
        } catch (Exception e) {
            log.error("Cannot write configuration of application "+getName()+". "+Logging.stackTrace(e));
        }
    }
    
    /**
     * if the application has to be autodeployed than autodeploy on all elements will be checked.
     */
    private void checkAutodeploy() {
        if(getAutoDeploy()) {
            
            try {
                // Components have to be installed/autoDeployed in a certain order
                ComponentInterface component = (ComponentInterface)components.get("CloudLayouts");
                component.autoDeploy();
                component = (ComponentInterface)components.get("DataSets");
                component.autoDeploy();
                component = (ComponentInterface)components.get("Displays");
                component.autoDeploy();
            	log.info("\tApplication "+getName()+" is succesfully autodeployed");
            } catch (Exception excep) {
                log.error("Autodeploy error - "+excep+"\n"+ Logging.stackTrace(excep));
            }
            if (getConfigurationVersion()>getInstalledVersion()) {
		    setInstalledVersion(getConfigurationVersion());
            }
        }
    }
    
    /**
     * set and initialize all components of an application
     */
    private void setComponents() {
        // Components are hard loaded now, this has to be dynamical
        components.put("CloudLayouts", new CloudLayoutComponent(this,mmbase));
        components.put("DataSets", new DataSetComponent(this,mmbase));
        components.put("Displays", new DisplayComponent(this,mmbase));
    }
    
    /**
     * add an application component
     */
    public void addComponent(String componentname) {
        if(!components.containsKey(componentname)) {
            if(componentname.equals("CloudLayouts")) {
                components.put("CloudLayouts", new CloudLayoutComponent(this,mmbase));
            }
            if(componentname.equals("DataSets")) {
                components.put("DataSets", new DataSetComponent(this,mmbase));
            }
            if(componentname.equals("Displays")) {
                components.put("Displays", new DisplayComponent(this,mmbase));
            }
        }
    }
    
    /**
     * remove an application component
     * this method has to signal the compont first, i think ;)
     */
    public void removeComponent(String componentname) {
        if(components.containsKey(componentname)) {
            components.remove(componentname);
        }
    }
    
    /**
     * get all components. The key is the name of the component. The value is the component itself.
     * @return all components
     */
    public Hashtable getComponents() {
        return components;
    }
    
    /**
     * get a component.
     * @return the component
     */
    public Component getComponent(String componentname) {
        return (Component)components.get(componentname);
    }
    
    /**
     * check if the application can be installed with no dependencies conflicts.
     * @return true if all dependencies are correct, false otherwise
     */
    public void checkDependencies() throws Exception {
        
        for (Enumeration e = components.elements();e.hasMoreElements();) {
            ComponentInterface ci = (ComponentInterface)e.nextElement();
            ci.checkDependencies();
        }
    }
    
    public void delete() throws Exception {
        FileUtils.deleteFiles(getApplicationLocation());
    }
    
    /**
     * Installs the application, this method will invoke install() on all components.
     * Which parts of the components have to be installed can be set in the components itself.
     * If nothing is set, the default things will be installed. This will be used e.g. when an
     * application autodeploys (no human interaction).
     */
    public void install() {
        log.info("Installing application: "+getName()+" version "+getConfigurationVersion());
        
        try {
            if (ApplicationManager.getKioskmode()) {
                throw new InstallationException("MMBase runs in Kiosk mode, it's not possible to install applications");
            }
            
            // If a version already exists, than the versionnumber has to be bigger of the one to install.
            if (getInstalledVersion()!=-1 && getConfigurationVersion()<=getInstalledVersion()) {
                log.info("Skipping installing Application '"+getName()+" version"+getConfigurationVersion()+"', because it (or a newer version) is already installed.");
                return;
            }
            
            checkDependencies();
            
            // Components have to be installed in a certain order
            ComponentInterface component = (ComponentInterface)components.get("CloudLayouts");
            component.install();
            component = (ComponentInterface)components.get("DataSets");
            component.install();
            component = (ComponentInterface)components.get("Displays");
            component.install();
            
            setInstalledVersion(getConfigurationVersion());
            
        } catch (Exception e) {
            log.error("Problem installing "+getName()+" message:"+e);
            setStatus("install",""+e.getMessage());
        }
        
    }
    
    /**
     * save the application. 
     */
    public void save() throws Exception {
        if(ApplicationManager.getKioskmode()) {
            setStatus("save","Refused to save application, MMBase runs in kiosk mode");
            throw new Exception("Refused to save application, MMBase runs in kiosk mode");
        }

	// save all components (save DATA, save webpages etc.)
	Hashtable components = getComponents();
	for (Enumeration e = components.elements();e.hasMoreElements(); ) {
		ComponentInterface component = (ComponentInterface)e.nextElement();
		component.save();
	}

	// save configuration information of the application itself
	writeConfiguration();
    }
    
    /**
     * give the name of the application.
     * @return the application name
     */
    public String getName() {
        if(name==null) {
            name = reader.getElementAttributeValue("application","name");
        }
        return name;
    }
    
    /**
     * set the name for the application.
     * @param newname the new application name
     */
    public void setName(String newname) {
        name = newname;
    }
    
    /**
     * give the version of this running application.
     * @return the version, -1 if the application is not installed
     */
    public int getInstalledVersion() {
        if(installedversion==null) {
            installedversion = new Integer(versions.getInstalledVersion(getName(),"application"));
        }
        return installedversion.intValue();
    }
    
    /**
     * set the version of this running application.
     * @param newversion the version number of the application
     */
    public void setInstalledVersion(int newinstalledversion) {
        installedversion = new Integer(newinstalledversion);

	if (versions.getInstalledVersion(getName(),"application")==-1) {
		versions.setInstalledVersion(getName(),"application",getMaintainerName(),newinstalledversion);
	} else {
                versions.updateInstalledVersion(getName(),"application",getMaintainerName(),newinstalledversion);
	}
    }
    
    /**
     * tell if the application has to be installed automatically during startup.
     */
    public boolean getAutoDeploy() {
        if(autodeploy==null) {
            if(reader.getElementAttributeValue("application","auto-deploy").equals("true")) {
                autodeploy = new Boolean("true");
            } else {
                autodeploy = new Boolean("false");
            }
        }
        return autodeploy.booleanValue();
    }
    
    /**
     * set if the application has to be installed during startup.
     * @param newautodeploy true if the application has to be installed druing startup, false otherwise.
     */
    public void setAutoDeploy(boolean newautodeploy) {
        autodeploy = new Boolean(newautodeploy);
    }
    
    /**
     * give the version of the application from the configuration file.
     * @return the version
     */
    public int getConfigurationVersion() {
        if(configurationversion==null) {
            configurationversion = new Integer(reader.getElementAttributeValue("application","version"));
        }
        return configurationversion.intValue();
    }
    
    /**
     * set the configuration number in the application config file
     */
    public void setConfigurationVersion(int newconfigurationversion) {
        configurationversion = new Integer(newconfigurationversion);
    }
   
    /**
     * give the name of the maintainer of the application
     */
    public String getMaintainerName() {
        if(maintainername==null) {
            maintainername = reader.getElementValue("application.maintainer.name");
        }
        return maintainername;
    }
   
    /**
     * set the name of the maintainer of the application
     */
    public void setMaintainerName(String newmaintainername) {
        maintainername = newmaintainername;
    }
    
    /**
     * get the email of the maintainer of the application
     */
    public String getMaintainerEmail() {
        if (maintaineremail==null) {
            maintaineremail =  reader.getElementValue("application.maintainer.email");
        }
        return maintaineremail;
    }
    
    /**
     * set the email of the maintainer of this application
     * @param newmaintaineremail the email of the maintainer
     */
    public void setMaintainerEmail(String newmaintaineremail) {
        maintaineremail = newmaintaineremail;
    }
   
    /**
     * get the companyname of the maintainer of the application
     */
    public String getMaintainerCompany() {
        if(maintainercompany==null) {
            maintainercompany =  reader.getElementValue("application.maintainer.company");
        }
        return maintainercompany;
    }
    
    /**
     * set the companyname of the maintainer of the application
     */
    public void setMaintainerCompany(String newmaintainercompany) {
        maintainercompany = newmaintainercompany;
    }
    
    /**
     * get some information from the maintainer of the application
     */
    public String getMaintainerInfo() {
        if(maintainerinfo==null) {
            maintainerinfo =  reader.getElementValue("application.maintainer.info");
        }
        return maintainerinfo;
    }
    
    /**
     * set some inforamtion of the maintainer of the application
     */
    public void setMaintainerInfo(String newmaintainerinfo) {
        maintainerinfo = newmaintainerinfo;
    }
    
    public String getMaintainerHomebase() {
        if(maintainerhomebase==null) {
            maintainerhomebase =  reader.getElementValue("application.maintainer.homebase");
        }
        return maintainerhomebase;
    }
    
    public void setMaintainerHomebase(String newmaintainerhomebase) {
        maintainerhomebase = newmaintainerhomebase;
    }
    
    /**
     * give the description of the application.
     * @return the description
     */
    public String getDescription() {
        if(description==null) {
            description =  reader.getElementValue("application.description");
        }
        return description;
    }
   
    /**
     * set the description of the application
     * @param newdescription the description of the application
     */
    public void setDescription(String newdescription) {
        description = newdescription;
    }
    
    /**
     * get all the Display Elements (for the DisplayComponent).
     * @return all display elements
     */
    public Vector getDisplays() {
        Vector displays = new Vector();
        if(reader!=null) {
            for (Enumeration n = reader.getChildElements("application.displays","display");n.hasMoreElements();) {
                Element element= (Element)n.nextElement();
                Hashtable bset = new Hashtable();
                bset.put("name",element.getAttribute("name"));
                bset.put("autoDeploy",element.getAttribute("auto-deploy"));
                bset.put("creationTime",element.getAttribute("create-date"));
                bset.put("MD5",element.getAttribute("MD5"));
                displays.addElement(bset);
            }
        }
        return displays;
    }
    
    /**
     * get all the CloudLayout Elements (for the CloudLayoutComponent).
     * @return all cloudlayout elements
     */
    public Vector getCloudLayouts() {
        Vector cloudlayouts = new Vector();
        if(reader!=null) {
            for (Enumeration n = reader.getChildElements("application.cloudlayouts","cloudlayout");n.hasMoreElements();) {
                Element element= (Element)n.nextElement();
                Hashtable bset = new Hashtable();
                bset.put("name",element.getAttribute("name"));
                bset.put("autoDeploy",element.getAttribute("auto-deploy"));
                bset.put("creationTime",element.getAttribute("create-date"));
                bset.put("MD5",element.getAttribute("MD5"));
                cloudlayouts.addElement(bset);
            }	}
        return cloudlayouts;
    }
    
    /**
     * get all the Dataset Elements (for the DatasetComponent).
     * @return all dataset elements
     */
    public Vector getDataSets() {
        Vector datasets = new Vector();
        if(reader!=null) {
            for (Enumeration n = reader.getChildElements("application.datasets","dataset");n.hasMoreElements();) {
                Element element= (Element)n.nextElement();
                Hashtable bset = new Hashtable();
                bset.put("name",element.getAttribute("name"));
                bset.put("autoDeploy",element.getAttribute("auto-deploy"));
                bset.put("creationTime",element.getAttribute("create-date"));
                bset.put("MD5",element.getAttribute("MD5"));
                datasets.addElement(bset);
            }}
        return datasets;
    }
    
    /**
     * set information for an application
     */
    private void setStatus(String key, String value) {
        status.put(key,value);
    }
    
    /**
     * get information from the application.
     * @param key indicates which information
     * @return the information
     */
    public String getStatus(String key) {
        return (String)status.get(key);
    }
    
    /**
     * get information from all components. e.g. key=install will result in install information from all components.
     * @param key the status information to return
     * @return the status information
     */
    public String getStatusAllComponents(String key) {
        String status = "";
        for (Enumeration e = components.elements();e.hasMoreElements();) {
            ComponentInterface ci = (ComponentInterface)e.nextElement();
            status+=ci.getStatus(key);
        }
        return status;
    }
    
    /**
     * gets a certain status from a certain component.
     * @param component the component
     * @param key the status information to return
     * return the status information of a certain component
     */
    public String getStatus(String component, String key) {
        if(!components.containsKey(component)) {
            log.error("component "+component+" does not exists");
        }
        ComponentInterface ci = (ComponentInterface)components.get(component);
        return ci.getStatus(key);
    }
    
    /**
     * give the directory were the applications are installed
     * @return the directory of the applications
     */
    public String getApplicationLocation() {
        return applicationLocation;
    }
    
    /**
     * tell if a certain element exists
     */
    public boolean exists(String component, String element) {
        if (components.containsKey(component)) {
            ComponentInterface ci = (ComponentInterface)components.get(component);
            return ci.exists(element);
        }
        return false;
    }
}

