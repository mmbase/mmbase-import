package org.mmbase.security.implementation.context;

import org.mmbase.security.Rank;
import org.mmbase.security.UserContext;
import org.mmbase.security.Authentication;

import java.util.HashMap;
import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilderFactory;

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

public abstract class ContextLoginModule {
    private static Logger log=Logging.getLoggerInstance(ContextLoginModule.class.getName());
        
    private Document 	document;
    private long    	validKey;
    private String  	name;

    public void load(Document document, long validKey, String name) throws org.mmbase.security.SecurityException{
    	this.document = document;
	this.validKey = validKey;
	this.name = name;
    }
    
    public abstract ContextUserContext login(HashMap userLoginInfo, Object[] userParameters) throws org.mmbase.security.SecurityException;
    
    protected ContextUserContext getValidUserContext(String username, Rank rank) throws org.mmbase.security.SecurityException{
    	return new ContextUserContext(username, rank, validKey);
    }
    
    protected Rank getRank(String username) throws org.mmbase.security.SecurityException {
    	String xpath = "/contextconfig/accounts/user[@name='"+username+"']/identify[@type='"+name+"']";
	log.debug("gonna execute the query:" + xpath);
	Node found;
	try {
	    found = XPathAPI.selectSingleNode(document, xpath);
	}
	catch(javax.xml.transform.TransformerException te) {
	    String msg = "error executing query: '"+xpath+"'"; 
	    log.error(msg);
	    log.error( Logging.stackTrace(te));
	    throw new java.lang.SecurityException(msg);
	}
	if(found == null) {
	    log.warn("user :" + username + " was not found for module: " + name);
	    return null;
	}		

	// retrieve the rank...
	NamedNodeMap nnm = found.getAttributes();
	Node rankNode = nnm.getNamedItem("rank");
	Rank rank = Rank.getRank(rankNode.getNodeValue());
	log.debug("retrieved the rank for user:" + username + " in module: " + name + " rank: " + rank);
    	return rank;	
    }        
    
    protected String getModuleValue(String username) throws org.mmbase.security.SecurityException {
    	String xpath = "/contextconfig/accounts/user[@name='"+username+"']/identify[@type='"+name+"']";
	log.debug("gonna execute the query:" + xpath);
	Node found;
	try {
	    found = XPathAPI.selectSingleNode(document, xpath);
	}
	catch(javax.xml.transform.TransformerException te) {
	    String msg = "error executing query: '"+xpath+"'";
	    log.error(msg);
	    log.error( Logging.stackTrace(te));
	    throw new java.lang.SecurityException(msg);
	}
	if(found == null) {
	    log.warn("user :" + username + " was not found for module: " + name);
	    return null;
	}		
	
	// k, now we have to retrieve the value of the node.
        NodeList nl = found.getChildNodes();
    	for (int i=0;i<nl.getLength();i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == n.TEXT_NODE) {
	    	String value = n.getNodeValue();
	    	log.debug("retrieved the value for user:" + username + " in module: " + name + " value: " + value);
	    	return value;
            }
        }
	return null;        
    }        
}
