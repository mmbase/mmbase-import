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
import java.io.*;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.w3c.dom.traversal.*;
import org.xml.sax.helpers.DefaultHandler;

 /**
  * A remote provider gives acces to a provider of another MMBase system. With the remote provider 
  * you can access information of the remote provider. You can also retrieve information which application the remote
  * provider is providing.
  *
  * @author Rob Vermeulen (VPRO)
  */
public class RemoteProvider{
    private static Logger log = Logging.getLoggerInstance(RemoteProvider.class.getName());
   
    // The location (url) of the provider
    private String providerlocation = null;
    // What is your username that you use for this provider
    private String username = null;
    // The password that belongs to the username
    private String password = null;
    // where can i find a nice logo of the provider
    private String imagelocation = null;
    // What is the name of the provider
    private String providername = null;
    // A reference to the application manager
    private ApplicationManager appmanager = null;
    // All applications that the other provider is making available
    private Hashtable applications = null;
    // Maybe introduce a polling mechanism for retrieving new application information.
    
    RemoteProvider(ApplicationManager manager) {
        appmanager = manager;
    }
    
    RemoteProvider(String url, String userid, String passwd, ApplicationManager manager) {
        providerlocation = url;
        username = userid;
        password = passwd;
        appmanager = manager;
    }
   
    /** 
     * get the location 
     */
    public String getLocation() {
        return providerlocation;
    }
    
    /**
     * get the name of the remote provider
     */
    public String getName() {
        if(providername==null) {
            providername = getNameRemote();
        }
        return providername;
    }
   
    /**
     * get the location were to find the logo of the provider
     */
    public String getImageLocation() {
        if(imagelocation==null) {
            //haal deze remote op.
        }
        return imagelocation;
    }
    
    /**
     * your userid with which you authenticate by the other provider
     */
    public String getUserId() {
        return username;
    }
    
    /**
     * the password that is used to login by the other provider
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * get all the applications made available by the remote provider
     */
    public Hashtable getAvailableApplications() {
        if(applications==null) {
            applications = getAvailableApplicationsRemote();
        }
        return applications;
    }
   
    /**
     * uhmmm, normally the remote provider gives his name, but if for some reasons the remote provider is not accessible, than you can give it a name yourself.
     */
    public void setName(String name) {
        providername = name;
    }
   
    /**
     *
     */
    public void setLocation(String url) {
        providerlocation = url;
    }
   
    /**
     * set the userid with which you login in the remote provider
     */
    public void setUserId(String name) {
        username = name;
    }
    
    /**
     * set the password with which you login in the remote provider
     */
    public void setPassword(String passwd) {
        password = passwd;
    }
    
    /**
     *
     */
    private String getRemoteName() {
        return "";
    }
    
    /**
     * this call will ask the name of the remote provider.
     * Probably this call will get a standard set of information and not only the name
     */
    private String getNameRemote() {
        String response = "<providerinfo name=\"MMBase\" />";
        String name = null ;
        
        Document document = getDocument(response);
        Node providerinfo = document.getFirstChild();
        NamedNodeMap nm = providerinfo.getAttributes();
        if (nm != null) {
            name= nm.getNamedItem("name").getNodeValue();
            // ## Ook willen we natuurlijk een omschrijving en een lokatie waar we een plaatje kunnen opvragen.
        }
        return name;
    }
    
    /**
     * this call gets all remotely available applications (available by this remote provider).
     */
    private Hashtable  getAvailableApplicationsRemote() {
        Hashtable apps = new Hashtable();
        String response = "<applications><application name=\"Tetris\" version=\"2.0\" description=\"";
        response+="a asd fasdf asd asd fas fsda fsa\" location=\"www.mmbase.org\\apps\\Tetris\\2.0\" /></applications>";
        
        
        Document document = getDocument(response);
        Node applicationsTag = document.getFirstChild();
        NodeList applicationTags = applicationsTag.getChildNodes();
        
        for (int i = 0; i < applicationTags.getLength(); i++) {
            Node applicationTag = applicationTags.item(i);
            NamedNodeMap nm = applicationTag.getAttributes();
            if (nm != null) {
                String name= nm.getNamedItem("name").getNodeValue();
                String description= nm.getNamedItem("description").getNodeValue();
                String version= nm.getNamedItem("version").getNodeValue();
                String location= nm.getNamedItem("location").getNodeValue();
                AvailableApplication aa = new AvailableApplication(name, description, version, location, appmanager);
                apps.put(name, aa);
            }
        }
        return apps;
    }
    
    /**
     *
     */
    private Document getDocument(String xmlstring) {
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xmlstring));
        DOMParser parser = new DOMParser();
        XMLCheckErrorHandler XEH = null;
        try {
            parser.setFeature("http://xml.org/sax/features/validation", true);
            XEH = new XMLCheckErrorHandler();
            parser.setErrorHandler(XEH);
        } catch (Exception parsException) {
            log.error("No DTD validation supported");
        }
        try {
            parser.parse(is);
        } catch (Exception e) {
            log.error("Somethings went wrong in parsing: "+xmlstring);
        }
        return parser.getDocument();
    }
}
