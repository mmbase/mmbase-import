/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.context;

import org.mmbase.security.*;
import org.mmbase.security.SecurityException; // must be imported explicity, because it is also in java.lang

import java.util.*;
import java.io.FileInputStream;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;

import org.xml.sax.InputSource;

import org.mmbase.module.core.MMObjectNode;

import org.apache.xpath.XPathAPI;
import org.apache.xerces.parsers.DOMParser;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/** authorization based on a config*/
public class ContextAuthorization extends Authorization {
    private static Logger   log=Logging.getLoggerInstance(ContextAuthorization.class.getName());
    private Document 	    document;
    private ContextCache    cache= new ContextCache();

    private HashMap 	    replaceNotFound = new HashMap();
    private HashMap 	    userDefaultContexts = new HashMap();

    protected void load() {
        log.debug("using: '" + configFile + "' as config file for authentication");
        try {
            InputSource in = new InputSource(new FileInputStream(configFile));
            // clear the cache of unfound contexts
            replaceNotFound.clear();
            // clear the cache of user default contexts
            userDefaultContexts.clear();
            // reload the security xml document
            document = org.mmbase.util.XMLBasicReader.getDocumentBuilder().parse(in);
        } catch(org.xml.sax.SAXException se) {
            log.error("error parsing file :"+configFile);
            String message = "error loading configfile :'" + configFile + "'("+se + "->"+se.getMessage()+"("+se.getMessage()+"))";
            log.error(message);
            log.error(Logging.stackTrace(se));
            throw new SecurityException(message);
        } catch(java.io.IOException ioe) {
            log.error("error parsing file :"+configFile);
            log.error(Logging.stackTrace(ioe));
            throw new SecurityException("error loading configfile :'"+configFile+"'("+ioe+")" );
        }
        log.debug("loaded: '" +  configFile + "' as config file for authorization");
    }

    public String getDefaultContext(UserContext user) throws SecurityException {
        String defaultContext = (String)userDefaultContexts.get(user);
        if (defaultContext==null) {
            String xpath = "/contextconfig/accounts/user[@name='"+user.getIdentifier()+"']";
            Node found;
            try {
                log.debug("gonna execute the query:" + xpath + " on file : " + configFile);
                found = XPathAPI.selectSingleNode(document, xpath);
            } catch(javax.xml.transform.TransformerException te) {
                log.error("error executing query: '"+xpath+"' on file: '"+configFile+"'" );
                log.error( Logging.stackTrace(te));
                throw new java.lang.SecurityException("error executing query: '"+xpath+"' on file: '"+configFile+"'");
            }
            NamedNodeMap nnm = found.getAttributes();
            Node contextNode = nnm.getNamedItem("context");
            defaultContext = contextNode.getNodeValue();
            userDefaultContexts.put(user,defaultContext);
        }
        if (log.isDebugEnabled()) {
            log.debug("user with name: " + user + " has the default context: " + defaultContext);
        }
        return defaultContext;
    }

    public void create(UserContext user, int nodeNumber) throws SecurityException {
        // notify, well actually we only have to set the context to the default of the user...
        log.info("create on node #"+nodeNumber+" by user: " +user);
        String defaultContext = getDefaultContext(user);
        setContext(user, nodeNumber, defaultContext);
    }

    public void update(UserContext user, int nodeNumber) throws SecurityException {
        // notify the log
        log.info("update on node #"+nodeNumber+" by user: " +user);
    }

    public void remove(UserContext user, int nodeNumber) throws SecurityException{
        // notify the log
        log.info("remove on node #"+nodeNumber+" by user: " +user);
    }

    public void setContext(UserContext user, int nodeNumber, String context) throws SecurityException {
        // notify the log
        if (log.isDebugEnabled()) {
            log.info("set context on node #"+nodeNumber+" by user: " +user + " to " + context );
        }
        // don't even bother if the context was already set.
        MMObjectNode node = getMMNode(nodeNumber);
        if (node.getStringValue("owner").equals(context)) return;

        // check if is a valid context for us..
        HashSet possible = getPossibleContexts(user, nodeNumber);
        if(!possible.contains(context)) {
            String msg = "could not set the context to "+context+" for node #"+nodeNumber+" by user: " +user;
            log.error(msg);
            throw new SecurityException(msg);
        }

        // check if this operation is allowed? (should also be done somewhere else, but we can never be sure enough)
        assert(user, nodeNumber, Operation.CHANGECONTEXT);

        // well now really set it...
        node.setValue("owner", context);
        node.commit();
        if (log.isDebugEnabled()) {
            log.info("changed context settings of node #"+nodeNumber+" to context: "+context+ " by user: " +user);
        }
    }

    public String getContext(UserContext user, int nodeNumber) throws SecurityException {
        // notify the log
        if (log.isDebugEnabled()) log.debug("get context on node #"+nodeNumber+" by user: " +user);

        // check if this operation is allowed? (should also be done somewhere else, but we can never be sure enough)
        assert(user, nodeNumber, Operation.READ);

        // and get the value...
        MMObjectNode node = getMMNode(nodeNumber);
        return node.getStringValue("owner");
    }

    public HashSet getPossibleContexts(UserContext user, int nodeNumber) throws SecurityException {
        // notify the log
        log.info("get possible context on node #"+nodeNumber+" by user: " +user);

        // check if this operation is allowed? (should also be done somewhere else, but we can never be sure enough)
        // TODO: research if we maybe better could use WRITE or CHANGECONTEXT as rights for this operation...
        assert(user, nodeNumber, Operation.READ);

        // retrieve the current context..
        String currentContext = getContext(user, nodeNumber);
        synchronized(replaceNotFound) {
            if(replaceNotFound.containsKey(currentContext)) {
                currentContext = (String)replaceNotFound.get(currentContext);
            }
        }

        HashSet list;
        synchronized(cache) {
            list = cache.contextGet(currentContext);
            if(list != null) {
                log.debug("cache hit");
                return list;
            }
            list = new HashSet();
        }

        // possible contextes are dependeding of the context they're in...
        String xpath = "/contextconfig/contexts/context[@name='"+currentContext+"']/possible";
        log.debug("gonna execute the query:" + xpath );
        NodeIterator found;
        try {
            found = XPathAPI.selectNodeIterator(document, xpath);
        } catch(javax.xml.transform.TransformerException te) {
            log.error("error executing query: '"+xpath+"' ");
            log.error( Logging.stackTrace(te));
            throw new java.lang.SecurityException("error executing query: '"+xpath+"' ");
        }
        Node context;
        for(context = found.nextNode(); context != null; context = found.nextNode()) {
            NamedNodeMap nnm = context.getAttributes();
            Node contextNameNode = nnm.getNamedItem("context");
            list.add(contextNameNode.getNodeValue());
            if (log.isDebugEnabled()) {
                log.debug("the context: "+contextNameNode.getNodeValue() +" is possible context for node #"+nodeNumber+" by user: " +user);
            }
        }
        synchronized(cache) {
            cache.contextAdd(currentContext, list);
        }
        return list;
    }

    public boolean check(UserContext user, int nodeNumber, Operation operation) throws SecurityException{
        if (log.isDebugEnabled()) {
            log.info("check on node #"+nodeNumber+" by user: " +user+ " for operation "+ operation);
        }

        // is our usercontext still valid?
        if(!manager.getAuthentication().isValid(user)) {
            String msg = "the usercontext was expired";
            log.error(msg);
            throw new java.lang.SecurityException(msg);
        }

        //  look which groups belong to this,...
        MMObjectNode node = getMMNode(nodeNumber);
        String context = node.getStringValue("owner");
        
        return check(user, context, operation.toString());
    }

    private boolean check(UserContext user, int nodeNumber, String operation) throws SecurityException {
        return check(user, getContext(user, nodeNumber), operation);
    }
    private boolean check(UserContext user, String context, String operation) throws SecurityException {
        // look if we have this one already inside the positive cache...
        synchronized(cache) {
            Boolean result = cache.rightGet(operation, context, user.getIdentifier());
            if(result != null) {
                log.debug("cache hit");
                return result.booleanValue();
            }
        }


        String xpath = "/contextconfig/contexts/context[@name='"+context+"']/operation[@type='"+operation+"']/grant";
        NodeList found;
        try {
            log.debug("gonna execute the query:" + xpath );
            found = XPathAPI.selectNodeList(document, xpath);
        } catch(javax.xml.transform.TransformerException te) {
            log.error("error executing query: '"+xpath+"' ");
            log.error( Logging.stackTrace(te));
            throw new java.lang.SecurityException("error executing query: '"+xpath+"' ");
        }



        // say our context isnt found... do the same query but now on the default context...
        if(found.getLength() == 0) {
            // well our context wasnt found, give a warning...
            log.warn("context with name :'"+context+"' was not found in the configuration.");

            // retrieve the default context...
            String defaultNodeXPath = "/contextconfig/contexts[@default]";
            Node foundDefaultNode;
            try {
                log.debug("gonna execute the query:" + defaultNodeXPath + " on file : " + configFile);
                foundDefaultNode = XPathAPI.selectSingleNode(document, defaultNodeXPath);
            } catch(javax.xml.transform.TransformerException te) {
                log.error("error executing query: '"+defaultNodeXPath+"' on file: '"+configFile+"'" );
                log.error( Logging.stackTrace(te));
                throw new java.lang.SecurityException("error executing query: '"+defaultNodeXPath+"' on file: '"+configFile+"'");
            }
            if (foundDefaultNode != null) {
                NamedNodeMap nnm = foundDefaultNode.getAttributes();
                Node defaultContextNode = nnm.getNamedItem("default");
                String defaultContext = defaultContextNode.getNodeValue();
                if (log.isDebugEnabled()) {
                    log.debug("context with name: " + context + " uses the default context: " + defaultContext);
                }
                // now do the same query with the default context...
                xpath = "/contextconfig/contexts/context[@name='"+defaultContext+"']/operation[@type='"+operation+"']/grant";
                try {
                    log.debug("gonna execute the query:" + xpath );
                    found = XPathAPI.selectNodeList(document, xpath);
                }
                catch(javax.xml.transform.TransformerException te) {
                    log.error("error executing query: '"+xpath+"' ");
                    log.error( Logging.stackTrace(te));
                    throw new java.lang.SecurityException("error executing query: '"+xpath+"' ");
                }
                // add it to the plave thing, so that it can be used within the getPossibleUserContexes..
                synchronized(replaceNotFound) {
                    replaceNotFound.put(context, defaultContext);
                }
            }
        }


        HashSet allowedGroups = new HashSet();
        for(int currentNode = 0; currentNode < found.getLength(); currentNode++) {
            Node contains = found.item(currentNode);
            NamedNodeMap nnm = contains.getAttributes();
            Node contextNameNode = nnm.getNamedItem("group");
            allowedGroups.add(contextNameNode.getNodeValue());
            if (log.isDebugEnabled()) {
                log.debug("the context: "+contextNameNode.getNodeValue() +" is possible context for node #"/*+nodeNumber*/+" by user: " +user);
            }
        }
        boolean allowed = userInGroups(user.getIdentifier(), allowedGroups, new HashSet());
        if (log.isDebugEnabled()) {
            if (allowed) {
                log.debug("operation " + operation + " was permitted for user with id " + user);
            } else {
                log.debug("operation " + operation + " was NOT permitted for user with id " + user);
            }
        }

        synchronized(cache) {
            cache.rightAdd(operation, context, user.getIdentifier(), allowed);
        }

    return allowed;
    }


    private boolean userInGroups(String user, HashSet groups, HashSet done) {
        // look if we have something to do...
        if(groups.size() == 0) {
            log.debug("entering userInGroups(recursive) with username: '"+user+"' without any groups, so user was not found..");
            return false;
        }
        log.debug("entering userInGroups(recursive) with username: '"+user+"' and look if the user is in the following groups:");

        if (log.isDebugEnabled()) {
            Iterator di = groups.iterator();
            while (di.hasNext() ) log.debug("\t -> group : "+di.next());
        }

        Iterator i = groups.iterator();
        HashSet fetchedGroups = new HashSet();
        while(i.hasNext()) {
            // get the group we are researching....
            String groupname = (String)i.next();
            // well, since we are already exploring ourselve, no need to do it again....
            done.add(groupname);
            log.debug("\tresearching group with name : "+groupname);

            // do the xpath query...
            String xpath = "/contextconfig/groups/group[@name='"+groupname+"']/contains";
            log.debug("\tgonna execute the query:" + xpath );
            NodeIterator found;
            try {
                found = XPathAPI.selectNodeIterator(document, xpath);
            } catch(javax.xml.transform.TransformerException te) {
                log.error("error executing query: '"+xpath+"' ");
                log.error( Logging.stackTrace(te));
                throw new java.lang.SecurityException("error executing query: '"+xpath+"' ");
            }
            // research the result...
            for(Node contains = found.nextNode(); contains != null; contains = found.nextNode()) {
                NamedNodeMap nnm = contains.getAttributes();
                String type = nnm.getNamedItem("type").getNodeValue();
                String named = nnm.getNamedItem("named").getNodeValue();
                log.debug("\t<contains type=\""+type+"\" named=\""+named+"\" />");
                if(type.equals("group")) {
                    // this is a group...
                    // when not already known, add it to our to fetch-list
                    if(!done.contains(named)) {
                        log.debug("\tfound a new group with name "+named+", which could contain our user, adding it to the to fetch list");
                        fetchedGroups.add(named);
                    }
                } else if(type.equals("user")) {
                    // oh, maybe its me !!
                    if(named.equals(user)){
                        log.debug("found the user with name " + named + " thus allowed." );
                        return true;
                    }
                    log.debug("\tdid found the user with name " + named + " but is not we are looking for.");
                } else {
                    String msg = "dont know the type:" + type;
                    log.error(msg);
                    throw new SecurityException(msg);
                }
            }
        }
        return userInGroups(user, fetchedGroups, done);
    }

    public void assert(UserContext user, int nodeNumber, Operation operation) throws SecurityException {
        log.info("assert on node #"+nodeNumber+" by user: " +user+ " for operation "+ operation);
        if (!check(user, nodeNumber, operation) ) {
            String msg = "Operation '" + operation + "' on " + nodeNumber + " was NOT permitted to " + user.getIdentifier();
            log.error(msg);
            throw new SecurityException(msg);
        }
    }

    
    public boolean check(UserContext user, int nodeNumber, int srcNodeNumber, int dstNodeNumber, Operation operation) throws SecurityException {
        if (operation == Operation.CREATE) {
            // may link on both nodes
            return check(user, srcNodeNumber, "link") && check(user, dstNodeNumber, "link");
        } else if (operation == Operation.CHANGE_RELATION) {
            return check(user, nodeNumber, Operation.WRITE.toString()) &&
                check(user, srcNodeNumber, "link") && check(user, dstNodeNumber, "link");
        } else {
            throw new RuntimeException("Called check with wrong operation " + operation);
        }
    }

    public void assert(UserContext user, int nodeNumber, int srcNodeNumber, int dstNodeNumber, Operation operation) throws SecurityException {
        if (operation == Operation.CREATE) {
            // may link on both nodes
            if(!check(user, srcNodeNumber, "link")) {
                String msg = "Operation 'link' on " + srcNodeNumber + " was NOT permitted to " + user.getIdentifier();
                log.error(msg);
                throw new SecurityException(msg);                
            }  
            if (! check(user, dstNodeNumber, "link")) {
                String msg = "Operation 'link' on " + srcNodeNumber + " was NOT permitted to " + user.getIdentifier();
                log.error(msg);
                throw new SecurityException(msg);                                
            }
        } else if (operation == Operation.CHANGE_RELATION) {
            if(!check(user, srcNodeNumber, "link")) {
                String msg = "Operation 'link' on " + srcNodeNumber + " was NOT permitted to " + user.getIdentifier();
                log.error(msg);
                throw new SecurityException(msg);                
            }  
            if (! check(user, dstNodeNumber, "link")) {
                String msg = "Operation 'link' on " + dstNodeNumber + " was NOT permitted to " + user.getIdentifier();
                log.error(msg);
                throw new SecurityException(msg);                                
            }
            assert(user, nodeNumber, Operation.WRITE);
        } else {
            throw new RuntimeException("Called check with wrong operation " + operation);
        }
    }

    private static org.mmbase.module.core.MMObjectBuilder builder = null;

    private MMObjectNode getMMNode(int n) {
        if(builder == null) {
            org.mmbase.module.core.MMBase mmb = (org.mmbase.module.core.MMBase)org.mmbase.module.Module.getModule("mmbaseroot");
            builder =  mmb.getMMObject("typedef");
            if(builder == null) {
                String msg = "builder 'typedef' not found";
                log.error(msg);
                throw new SecurityException(msg);
            }
        }
        MMObjectNode node = builder.getNode(n);
        if(node == null) {
            String msg = "node " + n + " not found";
            log.error(msg);
            throw new SecurityException(msg);
        }
        return node;
    }
}
