/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;

import java.io.File;


/**
 * @author Rob Vermeulen (VPRO)
 */
public class AvailableApplication {
    private static Logger log = Logging.getLoggerInstance(AvailableApplication.class.getName());
    
    private MMBase mmbase = null;
    private ApplicationManager appManager = null;
    private XMLBasicReader reader = null;
    
    private String name = null;
    private String version = null;
    private String description = null;
    private String location = null;
    private String localApplicationPath = null;
   
    /**
     * this constructor is for local available applications
     */
    public AvailableApplication(String name, String location, ApplicationManager manager) {
        this.name = name;
        this.location = location;
        appManager = manager;

        // ### maak gebruik van een appliactionroot
        String localApplicationPath = MMBaseContext.getConfigPath()+File.separator+"applications"+
					File.separator+"import"+File.separator+name+File.separator;
        String descriptionfile = localApplicationPath+"description.xml";
        
        reader = new XMLBasicReader(descriptionfile);
        if(reader==null) {
            log.error("cannot read the configurationfile for "+descriptionfile);
        }
        
        name = reader.getElementAttributeValue("application","name");
        version = reader.getElementAttributeValue("application","version");
        description = reader.getElementValue("application.description");
        
    }

    /**
     * this constructor is for remote available applications
     */
    public AvailableApplication(String name, String description, String version, String location, ApplicationManager manager) {
	    this.name = name;
	    this.location = location;
	    this.description = description;
	    this.version = version;
	    appManager = manager;
    }

    /**
     * the available application is located remote.
     * get all needed information
     */
    private void setRemote() {
    }
   
    /**
     * get the name of the application
     */
    public String getName() {
        return name;
    }
   
    /**
     * get the version of the application
     */
    public String getConfigurationVersion() {
        return version;
    }
   
    /**
     * get the description of the application
     */
    public String getDescription() {
        return description;
    }
   
    /**
     * get the location of the application. local means that the application is located in /imported/. An url will indicate if the 
     * application is located remote.
     */
    public String getLocation() {
        return location;
    }

    /**
     * copies the application files to the deployed applications directory. Files have to be unjared and if 
     * location is remote they have to be catched remotely.
     */
    public void install() throws Exception {
	    if(location.equals("local")) {
	    	FileUtils.copyFiles(localApplicationPath,appManager.getDeployedPath()+getName());
	    } else { 
		    // download de applicatie
	    } 
    }

    /**
     * delete the local available application
     */
    public void delete() throws Exception {
	    if(location.equals("local")) {
	    	FileUtils.deleteFiles(localApplicationPath);
	    }
    }

    public String toString() {
	    return "AvailableApplication name=\""+name+"\" version=\""+version+"\" location=\""+location+"\"";
    }
}
