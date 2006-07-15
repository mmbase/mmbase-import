/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.context;

import org.mmbase.security.*;
import org.mmbase.security.SecurityException;

import java.util.Map;

import org.w3c.dom.*;

import org.apache.xpath.XPathAPI;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Class ContextLoginModule
 * @javadoc
 *
 * @author Eduard Witteveen
 * @version $Id: ContextLoginModule.java,v 1.10 2003-08-27 19:37:12 michiel Exp $
 */

public abstract class ContextLoginModule {
    private static final Logger log = Logging.getLoggerInstance(ContextLoginModule.class);

    private Document document;
    private long validKey;
    private String name;
    private MMBaseCop manager;

    public void load(Document document, long validKey, String name, MMBaseCop manager) throws SecurityException{
        this.document = document;
        this.validKey = validKey;
        this.name = name;
        this.manager =manager;
    }

    public abstract ContextUserContext login(Map userLoginInfo, Object[] userParameters) throws SecurityException;

    protected ContextUserContext getValidUserContext(String username, Rank rank) throws SecurityException{
        return new ContextUserContext(username, rank, validKey, manager);
    }

    protected Rank getRank(String username) throws SecurityException {
        String xpath = "/contextconfig/accounts/user[@name='"+username+"']/identify[@type='"+name+"']";
        if (log.isDebugEnabled()) log.debug("going to execute the query:" + xpath);
        Node found;
        try {
            found = XPathAPI.selectSingleNode(document, xpath);
        } catch(javax.xml.transform.TransformerException te) {
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
        if (log.isDebugEnabled()) log.debug("retrieved the rank for user:" + username + " in module: " + name + " rank: " + rank);
        return rank;
    }

    protected String getModuleValue(String username) throws SecurityException {
        String xpath = "/contextconfig/accounts/user[@name='" + username + "']/identify[@type='" + name + "']";
        if (log.isDebugEnabled()) log.debug("going to execute the query:" + xpath);
        Node found;
        try {
            found = XPathAPI.selectSingleNode(document, xpath);
        } catch(javax.xml.transform.TransformerException te) {
            String msg = "error executing query: '"+xpath+"'";
            log.error(msg);
            log.error( Logging.stackTrace(te));
            throw new java.lang.SecurityException(msg);
        }
        if(found == null) {
            log.warn("user :" + username + " was not found for module: " + name);
            return null;
        }
        // now we have to retrieve the value of the node.
        NodeList nl = found.getChildNodes();
        for (int i=0;i<nl.getLength();i++) {
            Node n = nl.item(i);
            if (n.getNodeType() == Node.TEXT_NODE) {
                String value = n.getNodeValue();
                if (log.isDebugEnabled()) {
                    log.debug("retrieved the value for user:" + username + " in module: " + name + " value: " + value);
                }
                return value;
            }
        }
        return null;
    }
}
