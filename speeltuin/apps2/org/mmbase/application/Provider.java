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
 * The provider provides information and applications to other MMBase systems using applications.  
 * @author Rob Vermeulen (VPRO)
 */
public class Provider{
    private static Logger log = Logging.getLoggerInstance(Provider.class.getName());
    private ApplicationManager appmanager = null;

    // users with their passwords
    private Hashtable users = new Hashtable();

    // all applications that are provided with the users that use the application
    private Hashtable providedapplications = new Hashtable();
    
    private String name = null; // name of this provider
    private String description = null; // description of this provider
    
    Provider(ApplicationManager manager) {
        appmanager = manager;
    }
    
    /**
     * get the name of the provider
     * @return the name of the provider
     */
    public String getName() {
        return name;
    }
    
    /**
     * set the name for the provider
     * @param newname the name of the provider
     */
    public void setName(String newname) {
        name = newname;
        writeConfiguration();
    }
    
    /**
     * get the description of the provider
     * @return the description of the provider
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * set the description of the provider
     * @param the description of the provider
     */
    public void setDescription(String newdescription) {
        description = newdescription;
        writeConfiguration();
    }
    
    /**
     * initialize the Provider. Read the configuration file.
     */
    public void init() {
        readConfiguration();
    }
    
    /**
     * get the password of a user
     */
    public String getPassword(String user) {
        return (String)users.get(user);
    }
    
    /**
     * delete an user
     */
    public void deleteUser(String user) {
        users.remove(user);
        writeConfiguration();
    }
    
    /**
     * get all applications provided by this provider
     */
    public Hashtable getProvidedApplications() {
        return providedapplications;
    }
    
    /**
     * add an application that is being provided
     */
    public Vector addProvidedApplication(String application) {
        Vector result = null;
        if(!providedapplications.containsKey(application)) {
            result = new Vector();
            providedapplications.put(application,result);
            writeConfiguration();
        }
        return result;
    }
    
    /**
     * get all users that use the provided application
     */
    public Vector getUsersOfApplication(String applicationname) {
        return (Vector)providedapplications.get(applicationname);
    }
    
    /**
     * disable an user of using a certain application
     */
    public void deleteAccessToApplication(String application, String user) {
        Vector allusers = (Vector)providedapplications.get(application);
        allusers.remove(user);
        writeConfiguration();
    }
    
    /**
     * give an user access to a certain application.
     */
    public void addUserToApplication(String applicationname, String user) {
        if(!users.containsKey(user)) {
            log.error("First create user "+user+" before linking it to an application");
        }
        if(!providedapplications.containsKey(applicationname)) {
            providedapplications.put(applicationname,new Vector());
        }
        Vector allusers = (Vector)providedapplications.get(applicationname);
        if(!allusers.contains(user))  {
            allusers.addElement(user);
            writeConfiguration();
        }
    }
    
    /**
     * add a user with a certain password
     */
    public void addUser(String user, String password) {
        if(!users.containsKey(user)) {
            users.put(user,password);
            writeConfiguration();
        }
    }
    
    /**
     * get the username and password information of all users
     */
    public Hashtable getUserInfo() {
        return users;
    }
    
    /**
     * read the configurationfile of the provider
     */
    public void readConfiguration() {
        File  file = new File(appmanager.getApplicationPath()+"import"+File.separator+"providerinfo.xml");
        if (!file.exists()) {
            log.info("No provider configuration file exists (file="+file+")");
            return;
        }
        
        XMLBasicReader reader = new XMLBasicReader(file.toString());
        
        // Read information about provider
        name = reader.getElementAttributeValue("provider.info","name");
        description = reader.getElementAttributeValue("provider.info","description");
        
        // Read user inforamtion
        for (Enumeration n = reader.getChildElements("provider","userinfo");n.hasMoreElements();) {
            Element element= (Element)n.nextElement();
            users.put(element.getAttribute("user") ,element.getAttribute("password"));
            log.error("inserting user "+element.getAttribute("user"));
        }
        
        // Read information about which users can access which applications
        for (Enumeration n = reader.getChildElements("provider","application");n.hasMoreElements();) {
            Element element= (Element)n.nextElement();
            Vector allusers = addProvidedApplication(element.getAttribute("name"));
            
            for(Enumeration u = reader.getChildElements(element,"user");u.hasMoreElements();) {
                Element user = (Element)u.nextElement();
                allusers.addElement(user.getAttribute("name"));
            }
        }
        
    }
   
    /**
     * write the configuration file of this provider
     */
    public void writeConfiguration() {
        File  file = new File(appmanager.getApplicationPath()+"import"+File.separator+"providerinfo.xml");
        String text = "";
        text+="<provider>\n";
        
        // Write information of provider, at this time only name and description
        text+="\t<info name=\""+getName()+"\" description=\""+getDescription()+"\" />\n\n";
        
        //Write information about which users can access which applications
        for(Enumeration e = providedapplications.keys();e.hasMoreElements();) {
            String application = (String)e.nextElement();
            text+="\t<application ";
            text+="name=\""+application+"\">\n";
            for (Enumeration u = getUsersOfApplication(application).elements();u.hasMoreElements();) {
                String user = (String)u.nextElement();
                text+="\t\t<user name\""+user+"\" />\n";
            }
            text+="\t</application>\n\n";
        }
        
        // Write userinformation (userid's and passwords
        for(Enumeration e = getUserInfo().keys();e.hasMoreElements();) {
            String user = (String)e.nextElement();
            text+="\t<userinfo user=\""+user+"\" password=\""+getPassword(user)+"\" />\n";
        }
        
        text+="</provider>\n";
        
        try {
            FileUtils.saveFile(file.toString(),text);
        } catch (Exception fileexception) {
            log.error("Cannot save RemoteProvider information to file "+file);
        }
    }
}
