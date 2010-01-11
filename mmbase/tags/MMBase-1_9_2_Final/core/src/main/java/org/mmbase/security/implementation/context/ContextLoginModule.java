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

import javax.xml.xpath.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Class ContextLoginModule
 * @javadoc
 *
 * @author Eduard Witteveen
 * @version $Id$
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

    public abstract ContextUserContext login(Map<String, ?> userLoginInfo, Object[] userParameters) throws SecurityException;

    protected ContextUserContext getValidUserContext(String username, Rank rank) throws SecurityException{
        return new ContextUserContext(username, rank, validKey, manager, name);
    }




    protected Rank getRank(String username) throws SecurityException {
        return getRank(username, name);
    }

    /**
     * @since MMBase-1.8
     */
    protected Rank getRank(final String username, final String identifyType) throws SecurityException {
        final String xpath;
        if (identifyType != null) {
            xpath = "/contextconfig/accounts/user[@name='" + username + "']/identify[@type='" + identifyType + "']";
        } else {
            xpath = "/contextconfig/accounts/user[@name='" + username + "']/identify";
        }
        if (log.isDebugEnabled()) {
            log.debug("going to execute the query: " + xpath);
        }
        Node found;
        try {
            XPath xp = XPathFactory.newInstance().newXPath();
            found = (Node) xp.evaluate(xpath, document, XPathConstants.NODE);
        } catch(XPathExpressionException xe) {
            throw new java.lang.SecurityException("error executing query: '" + xpath + "' ", xe);
        }
        if(found == null) {
            log.warn("user '" + username + "' was not found for module: " + name);
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
     * @deprecated Use {@link #getAccount}
     */
    protected String getModuleValue(String userName) throws SecurityException {
        Element node = getAccount(userName, name, null);
        if (node == null) return null;
        // now we have to retrieve the value of the node.
        return org.mmbase.util.xml.DocumentReader.getNodeTextValue(node);
    }

    /**
     * Search an account for a given user name and identify type (the 'module').
     * @return The user Element.
     * @since MMBase-1.8
     */
    protected Element getAccount(final String userName, final String identifyType, final String rank) throws SecurityException {
        String userCons = "";
        if (userName != null) {
            userCons = "[@name='" + userName + "']";
        }
        final String xpath;
        if (identifyType != null || (rank != null && ! "anonymous".equals(rank))) {
            StringBuffer identifyCons = new StringBuffer();
            if (identifyType != null) {
                identifyCons.append("@type='").append(identifyType).append("'");
            }
            if (rank != null) {
                if (identifyCons.length() > 0) identifyCons.append(" and ");
                identifyCons.append("@rank='").append(rank).append("'");
            }
            xpath = "/contextconfig/accounts/user" + userCons + "/identify[" + identifyCons + "]";
        } else {
            xpath = "/contextconfig/accounts/user" + userCons;
        }

        if (log.isDebugEnabled()) {
            log.debug("going to execute the query: " + xpath);
        }

        final Element found;
        XPath xp = XPathFactory.newInstance().newXPath();
        try {
            found = (Element) xp.evaluate(xpath, document, XPathConstants.NODE);
        } catch(XPathExpressionException xe) {
            throw new java.lang.SecurityException("error executing query: '" + xpath + "' ", xe);
        }

        if(found == null) {
            if (rank != null) {
                log.warn("No user with rank '" + rank + "' " + (userName != null ? "and username '" + userName + "'": "") + " was not found for identify type: '" + identifyType  + "'", new Exception());
            } else {
                log.warn("No user with username '" + userName + "' was found for identify type: '" + identifyType  + "'");
            }
            return null;
        }
        if (identifyType != null || rank != null) {
            return (Element) found.getParentNode();
        } else {
            return found;
        }
    }
}
