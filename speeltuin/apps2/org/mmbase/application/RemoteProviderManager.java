/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

import org.mmbase.util.logging.*;
import org.mmbase.util.*;

import java.util.*;
import java.io.File;

import org.w3c.dom.*;

 /**
  * The remote provider manager manages all remote providers. It contains information about which remote providers you want to use.
  *
  * @author Rob Vermeulen (VPRO)
  */
public class RemoteProviderManager {
    private static Logger log = Logging.getLoggerInstance(RemoteProviderManager.class.getName());
    // List with providers
    private Hashtable remoteproviders = new Hashtable();
    private ApplicationManager appmanager = null;
    
    RemoteProviderManager(ApplicationManager applicationmanager) {
        log.info("Remote Provider Manager is loaded");
        appmanager = applicationmanager;
    }
   
    /**
     * initialize the manager
     */
    public void init() {
        readConfiguration();
        writeConfiguration();
    }
    
    /**
     * create a new remote provider
     */
    public RemoteProvider addRemoteProvider(String url, String username, String password) {
        if(remoteproviders.containsKey(url)) {
            log.error("Cannot add remote provider "+url+" because it's already available");
            return (RemoteProvider)remoteproviders.get(url);
        }
        RemoteProvider rp = new RemoteProvider(url, username, password, appmanager);
        remoteproviders.put(rp.getLocation(),rp);
        writeConfiguration();
        return rp;
    }
    
    /**
     * delete a remote provider
     */
    public void deleteRemoteProvider(String url) {
        remoteproviders.remove(url);
        writeConfiguration();
    }
    
    /**
     * shows all remote providers you are using.
     */
    public Hashtable getRemoteProviders() {
        return remoteproviders;
    }
    
    /**
     * shows all applications made available by all remote providers
     */
    public Hashtable getAvailableApplications() {
        Hashtable allAvailableApplications = new Hashtable();
        for(Enumeration e = remoteproviders.elements();e.hasMoreElements();) {
            RemoteProvider rp = (RemoteProvider)e.nextElement();
            Hashtable aas = rp.getAvailableApplications();
            for(Enumeration ee = aas.elements(); ee.hasMoreElements();) {
                AvailableApplication aa = (AvailableApplication)ee.nextElement();
                allAvailableApplications.put(aa.getName(),aa);
            }
        }
        return allAvailableApplications;
    }
    
    /** 
     * read information about which providers you want to use
     */
    public void readConfiguration() {
        remoteproviders = new Hashtable();
        File  file = new File(appmanager.getApplicationPath()+"import"+File.separator+"remoteproviders.xml");
        if (!file.exists()) {
            log.info("No remoteprovider configuration file exists");
            return;
        }
        XMLBasicReader reader = new XMLBasicReader(file.toString());
        for (Enumeration n = reader.getChildElements("remoteproviders","provider");n.hasMoreElements();) {
            Element element= (Element)n.nextElement();
            RemoteProvider provider = new RemoteProvider(appmanager);
            provider.setName(element.getAttribute("name"));
            provider.setLocation(element.getAttribute("location"));
            provider.setUserId(element.getAttribute("userid"));
            provider.setPassword(element.getAttribute("password"));
            remoteproviders.put(provider.getLocation(),provider);
        }
        log.error("Read "+remoteproviders.size()+" remote provider(s) from the remote provider configuration file");
    }
    
    /**
     * saves information about which remote providers you want to use
     */
    public void writeConfiguration() {
        File  file = new File(appmanager.getApplicationPath()+"import"+File.separator+"remoteproviders.xml");
        String text = "";
        text+="<remoteproviders>\n";
        for(Enumeration e = remoteproviders.elements();e.hasMoreElements();) {
            RemoteProvider pr = (RemoteProvider)e.nextElement();
            text+="\t<provider ";
            text+="name=\"";
            if(pr.getName()!=null) {
                text+=pr.getName();
            }
            text+="\" ";
            text+="location=\""+pr.getLocation()+"\" ";
            text+="userid=\""+pr.getUserId()+"\" ";
            text+="password=\""+pr.getPassword()+"\" />\n";
        }
        text+="</remoteproviders>\n";
        
        try {
            FileUtils.saveFile(file.toString(),text);
        } catch (Exception fileexception) {
            log.error("Cannot save RemoteProvider information to file "+file);
        }
        log.debug("Wrote "+remoteproviders.size()+" remote provider(s) to the remote provider configuration file");
    }
}
