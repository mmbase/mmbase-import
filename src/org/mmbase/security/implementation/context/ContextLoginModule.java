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
 * @version $Id: ContextLoginModule.java,v 1.13 2005-10-02 16:43:55 michiel Exp $
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
        return new ContextUserContext(username, rank, validKey, manager, name);
    }




    protected Rank getRank(String username) throws SecurityException {
        return getRank(username, name);
    }

    /**
     * @since MMBase-1.8
     */
    protected Rank getRank(String username, String identifyType) throws SecurityException {
        String xpath;
        if (identifyType != null) {
            xpath = "/contextconfig/accounts/user[@name='" + username + "']/identify[@type='" + identifyType + "']";
        } else {
            xpath = "/contextconfig/accounts/user[@name='" + username + "']/identify";
        }
        if (log.isDebugEnabled()) {
            log.debug("going to execute the query:" + xpath);
        }
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

    /**
     * Gets accounts for this authentication module
     * @since MMBase-1.8
     */
    protected Element getAccount(String userName) throws SecurityException {
        return getAccount(userName, name, null);
    }
    /**
     * @deprecated Use {@link #getAccountValue}
     */
    protected String getModuleValue(String userName) throws SecurityException {
        Element node = getAccount(userName, name, null);
        if (node == null) return null;
        // now we have to retrieve the value of the node.
        return org.mmbase.util.xml.DocumentReader.getNodeTextValue(node);
    }

    /**
     * Search an account for a given user name and identify type (the 'module'). Returns the value of the element
     * (which is often the empty string or the password)
     * @since MMBase-1.8
     */
    protected Element getAccount(String userName, String identifyType, String rank) throws SecurityException {
        String xpath;
        StringBuffer userCons = new StringBuffer(userName != null ? "@name='" + userName + "'" : "");
        if (rank != null) {
            if (userCons.length() > 0) userCons.append(" and ");
            userCons.append("@rank='" + rank + "'");
        }
        if (identifyType != null) {
            xpath = "/contextconfig/accounts/user[" + userCons + "]/identify[@type='" + identifyType + "']";
        } else {
            xpath = "/contextconfig/accounts/user[" + userCons + "]/identify";
        }
        if (log.isDebugEnabled()) {
            log.debug("going to execute the query:" + xpath);
        }
        Element found;
        try {
            found = (Element) XPathAPI.selectSingleNode(document, xpath);
        } catch(javax.xml.transform.TransformerException te) {
            String msg = "error executing query: '"+xpath+"'";
            log.error(msg);
            log.error(Logging.stackTrace(te));
            throw new java.lang.SecurityException(msg);
        }
        if(found == null) {
            log.warn("user :" + userName + " was not found for identify type: " + identifyType);
            return null;
        }
        return (Element) found.getParentNode();
    }
}
