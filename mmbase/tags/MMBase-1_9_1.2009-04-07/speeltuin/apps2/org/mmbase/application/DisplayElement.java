/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

import java.util.*;
import java.io.*;
import java.lang.*;

import org.w3c.dom.*;

/**
 * A display part contains all information of a part of a display component.
 * It's able to perform all actions related to the part, such as update the componentpart.
 * Or check if the dependencies are correct.
 */
public class DisplayElement extends ComponentElement implements ElementInterface {
    
    private DisplayComponent displayComponent = null;
    private MD5Diff md5diff = null;
    private String defaultwebpath = null;
    
    DisplayElement(Hashtable info, DisplayComponent dc, Application app) {
        super(info,app,"display");
        displayComponent = dc;
        
        File file = new File(getElementPath()+"description.xml");
        if(file.exists()) {
            reader = new XMLBasicReader(getElementPath()+"description.xml");
            description = reader.getElementValue("display.description");
            version = Integer.parseInt(reader.getElementAttributeValue("display","version"));
            creationdate = reader.getElementAttributeValue("display","create-date");
            // check if MD5 of the source code exists, otherwise it's removed or the branch is new.
            checkMD5();
        } else {
            setDescription("");
            setCreationDate();
            dependencies= new Vector();
            // ### setVersion("1");
        }
    }
    
    /**
     * create MD5 for the source files. You probably just created the application. Or somebody deleted the MD5.xml
     */
    private void checkMD5() {
        File file = new File(getElementPath()+"MD5.xml");
        if (!file.exists()) {
            displayComponent.createMD5(getElementPath()+"MD5.xml", getDataPath());
        }
    }
    
    /**
     * check differences in the MD5 files of the webpages and the origional source files.
     * @return true, if there is a differnce, false otherwise
     */
    public boolean checkUpdate(){
        // create MD5 of the web pages
        displayComponent.createMD5(getElementPath()+"installed.md5.xml",getWebPath());
        // create MD5 of application web pages
        displayComponent.createMD5(getElementPath()+"MD5.xml",getDataPath());
        // check difference between web pages and originals
        md5diff = new MD5Diff(getElementPath()+"MD5.xml",getElementPath()+"installed.md5.xml");
        return md5diff.isChanged();
    }
    
    /**
     * show the files that exist on the website but not in the source branch
     */
    public Vector getAddedFiles() {
        if(md5diff==null) {
            checkUpdate();
        }
        return md5diff.getAdded();
    }
    
    /**
     * show the files that are not on the website but are in the source branch
     */
    public Vector getDeletedFiles() {
        if(md5diff==null) {
            checkUpdate();
        }
        return md5diff.getDeleted();
    }
    
    /**
     * show files that exist on the webserver and in the source branch, that have the same filename but different MD5.
     */
    public Vector getChangedFiles() {
        if(md5diff==null) {
            checkUpdate();
        }
        return md5diff.getChanged();
    }
    
    
    public void save() {
        try {
            if (ApplicationManager.getKioskmode()) {
                throw new InstallationException("MMBase runs in Kiosk mode, it's not possible to install applications");
            }
            
            File file = new File(getElementPath());
            if(!file.exists()) {
                file.mkdirs();
            }
            
            // verwijder files uit applicatie
            FileUtils.deleteFiles(getDataPath());
            // copy files to application
            FileUtils.copyFiles(getWebPath(),getDataPath());
            // create MD5 of the data files
            displayComponent.createMD5(getElementPath()+"MD5.xml", getDataPath());
            // Delete not needed files
            file = new File(getElementPath()+"installed.md5.xml");
            if (file.exists()) {
                file.delete();
            }
            setCreationDate();
            // write config file
            DisplayElementWriter dcw = new DisplayElementWriter(getElementPath()+"description.xml",this);
            
            
        } catch (Exception e) {
            log.error("savehier mag ik niets afvangen"+e);
        }
    }
    
    public void autoDeploy() throws Exception {
        if (getAutoDeploy().equals("true"))
            if (getInstalledVersion()==-1 || getConfigurationVersion()>getInstalledVersion()) {
                log.debug("Autodeploying "+getName());
                install();
            }
    }
    
    /**
     * install a display element
     */
    public void install() throws Exception {
        if (ApplicationManager.getKioskmode()) {
            throw new InstallationException("MMBase runs in Kiosk mode, it's not possible to install applications");
        }
        
        // If a version already exists, than the versionnumber has to be bigger of the one to install.
        if (getInstalledVersion()!=-1 && getConfigurationVersion()<=getInstalledVersion()) {
            log.info("Skipping installing Display Element '"+getName()+" version"+getConfigurationVersion()+"', because it (or a newer version) is already installed.");
            return;
        }
        
        checkDependencies();
        
        //ApplicationManager.getVersionBuilder().setInstalledVersion(application.getName()+"/"+getName(),"application/display",getMaintainer(),getConfigurationVersion());
        if(getInstalledVersion()==-1) {
            ApplicationManager.getVersionBuilder().setInstalledVersion(application.getName()+"/"+getName(),"application/display","test",getConfigurationVersion());
        } else {
            ApplicationManager.getVersionBuilder().updateInstalledVersion(application.getName()+"/"+getName(),"application/display","test",getConfigurationVersion());
        }
        
        log.service("Copying files from "+getElementPath()+"data -> "+getWebPath());
        FileUtils.copyFiles(getDataPath(),getWebPath());
    }
    
    /**
     * deinstall a display element
     */
    public void uninstall() {
        try {
            if (ApplicationManager.getKioskmode()) {
                throw new InstallationException("MMBase runs in Kiosk mode, it's not possible to install applications");
            }
            
            if (getInstalledVersion()==-1) {
                throw new InstallationException("Cannot uninstall application '"+getName());
            }
            
            ApplicationManager.getVersionBuilder().updateInstalledVersion(application.getName()+"/"+getName(),"application/display","test",0);
            log.service("Removing all files from "+getWebPath());
            FileUtils.deleteFiles(getWebPath());
        } catch (Exception e) {
            log.error("$$$hier mag ik niets afvangen"+e);
        }
    }
    
    /**
     * get the default path were the web pages are.
     */
    public String getDefaultWebPath() {
        if(defaultwebpath==null) {
            defaultwebpath =  reader.getElementValue("display.default-web-path");
        }
        return defaultwebpath;
    }
    
    /**
     * set the default path were the webpages are.
     */
    public void setDefaultWebPath(String newdefaultwebpath) {
        defaultwebpath=newdefaultwebpath;
    }
    
    /**
     * zal de dependencies gaan ophalen
     */
    public Vector getNeeded() {
        Vector results = new Vector();
        for (Enumeration n = reader.getChildElements("display.needs","display");n.hasMoreElements();) {
            Element element= (Element)n.nextElement();
            Hashtable bset = new Hashtable();
            bset.put("name",element.getAttribute("name"));
            bset.put("autoDeploy",element.getAttribute("auto-deploy"));
            bset.put("creationTime",element.getAttribute("create-date"));
            bset.put("MD5",element.getAttribute("MD5"));
            results.addElement(bset);
        }
        return results;
    }
    
    /**
     * give the version of the (installed) application.
     * @return the version, -1 if the application is not installed
     */
    public int getInstalledVersion() {
        return ApplicationManager.getVersionBuilder().getInstalledVersion(application.getName()+"/"+getName(),"application/display");
    }
    
    /**
     * give the path were the data files are
     */
    private String getDataPath() {
        String dataPath = getElementPath()+"data"+File.separator;
        return dataPath;
    }
    
    /**
     * give the path were the webfiles are
     */
    private String getWebPath() {
        String webPath = MMBaseContext.getHtmlRoot()+File.separator+getDefaultWebPath()+File.separator;
        return webPath;
    }
}
