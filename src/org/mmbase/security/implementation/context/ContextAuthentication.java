package org.mmbase.security.implementation.context;

import org.mmbase.security.Rank;
import org.mmbase.security.UserContext;
import org.mmbase.security.Authentication;

import java.util.HashMap;
import java.io.FileInputStream;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Document;

import org.xml.sax.InputSource;

import org.apache.xpath.XPathAPI;
import org.apache.xerces.parsers.DOMParser;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Authentication based on a config..
 */
public class ContextAuthentication extends Authentication {
    private static Logger log=Logging.getLoggerInstance(ContextAuthentication.class.getName());
    private HashMap  loginModules = new HashMap();
    private Document document;
    private long validKey;
    
    public ContextAuthentication() {
    	validKey = System.currentTimeMillis();
    }

    protected void load() {
    	log.debug("using: '" + configFile + "' as config file for authentication");
    	try {	    
      	    InputSource in = new InputSource(new FileInputStream(configFile));
      	    document = org.mmbase.util.XMLBasicReader.getDocumentBuilder().parse(in);
      	}
	catch(org.xml.sax.SAXException se) {
	    log.error("error parsing file :"+configFile);	    
	    String message = "error loading configfile :'" + configFile + "'("+se + "->"+se.getMessage()+"("+se.getMessage()+"))";
	    log.error(message);	    	    
	    log.error(Logging.stackTrace(se));
	    throw new org.mmbase.security.SecurityException(message);
	}	
	catch(java.io.IOException ioe) {
	    log.error("error parsing file :"+configFile);
	    log.error(Logging.stackTrace(ioe));
	    throw new org.mmbase.security.SecurityException("error loading configfile :'"+configFile+"'("+ioe+")" );	
	}		
    	log.debug("loaded: '" +  configFile + "' as config file for authentication");	
	log.debug("gonna load the modules...");
	
	
    	// do the xpath query...
    	String xpath = "/config/loginmodules/module";
    	log.debug("gonna execute the query:" + xpath );
	NodeIterator found;
	try {
	    found = XPathAPI.selectNodeIterator(document, xpath);
    	}
	catch(javax.xml.transform.TransformerException te) {
	    log.error("error executing query: '"+xpath+"' ");
	    log.error( Logging.stackTrace(te));
	    throw new java.lang.SecurityException("error executing query: '"+xpath+"' ");
	}
	// we now have a list of login modules.. process them all, and load them...
    	for(Node contains = found.nextNode(); contains != null; contains = found.nextNode()) {
    	    NamedNodeMap nnm = contains.getAttributes();
	    String moduleName = nnm.getNamedItem("name").getNodeValue();
	    String className = nnm.getNamedItem("class").getNodeValue();
    	    
	    log.debug("gonna try to load module with the name:"+moduleName+ " with class:" + className);
            ContextLoginModule module;
            try {
                Class moduleClass = Class.forName(className);
    	    	module = (ContextLoginModule) moduleClass.newInstance();
            }
	    catch(Exception e) {
	    	String msg = "could not load module with the name:"+moduleName+ " with class:" + className;
    	        log.error(msg);
		log.error( Logging.stackTrace(e));
    	    	throw new SecurityException(msg);
            }
	    module.load(document, validKey, moduleName);
	    log.info("loaded module with the name:"+moduleName+ " with class:" + className);
	    loginModules.put(moduleName, module);
	}
	// set the reload on 10 seconds, maybe we want to put this inside the security.xml?
	fileWatcher.setDelay(10 * 1000);

	// add our config file...
	fileWatcher.add(configFile);	

	log.debug("done loading the modules...");	
    }


    public UserContext login(String moduleName, HashMap loginInfo, Object[] parameters) throws org.mmbase.security.SecurityException {
    	// look if we can find our login module...
	if(!loginModules.containsKey(moduleName)) {
	    String msg = "could not load module with name:" +  moduleName;
	    log.error(msg);
	    throw new java.lang.SecurityException(msg);	
	}
	ContextLoginModule module = (ContextLoginModule) loginModules.get(moduleName);
	// and we do the login...
	UserContext user = module.login(loginInfo, parameters);
	if(user == null) log.info("login on module with name:" + moduleName + "failed");
	else log.info("login on module with name:" + moduleName + " was succesfull for user with id:" + user.getIdentifier());
    	return user;
    }   
     
    /** 
     * this method does nothing..
     */        
    public boolean isValid(UserContext usercontext) throws org.mmbase.security.SecurityException {
    	return validKey == ((ContextUserContext)usercontext).getKey();
    }
}
