/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.builders;

import org.mmbase.security.implementation.cloudcontext.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.security.*;
import org.mmbase.security.SecurityException;
import org.mmbase.cache.Cache;
import org.mmbase.util.Encode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.*;

/**
 * This MMObjectBuilder implementation belongs to the object type
 * 'mmbaseusers' It contains functionality to MD5 encode passwords,
 * and so on.
 * 
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Users.java,v 1.16 2003-09-22 11:51:54 michiel Exp $
 * @since  MMBase-1.7
 */
public class Users extends MMObjectBuilder {

    private static final Logger log = Logging.getLoggerInstance(Users.class);


    public final static String FIELD_STATUS      = "status";
    public final static String FIELD_USERNAME    = "username";
    public final static String FIELD_PASSWORD    = "password";
    public final static String FIELD_DEFAULTCONTEXT    = "defaultcontext";

    public final static String STATUS_RESOURCE = "org.mmbase.security.status";

    protected static Cache rankCache = new Cache(20) {
            public String getName()        { return "CCS:SecurityRank"; }
            public String getDescription() { return "Caches the rank of users"; }
        };

    protected static Cache userCache = new Cache(20) {
            public String getName()        { return "CCS:SecurityUser"; }
            public String getDescription() { return "Caches the users"; }
        };

    // javadoc inherited
    public boolean init() {
        rankCache.putCache();
        CacheInvalidator.getInstance().addCache(rankCache);
        CacheInvalidator.getInstance().addCache(userCache);
        mmb.addLocalObserver(getTableName(), CacheInvalidator.getInstance());
        mmb.addRemoteObserver(getTableName(), CacheInvalidator.getInstance());

        String s = (String)getInitParameters().get("encoding");
        if (s == null) {
            log.warn("no property 'encoding' defined in '" + getTableName() + ".xml' using default encoding");
            encoder = new Encode("MD5");
        } else {
            encoder = new Encode(s);
        }
        log.info("Using " + encoder.getEncoding() + " as our encoding for password");

        return super.init();
    }



    /**
     * The user with rank administrator
     */
    static final String ADMIN_USERNAME = "admin";
    /**
     * The user with rank anonymous
     */
    static final String ANONYMOUS_USERNAME = "anonymous";

    private Encode encoder = null;

    /**
     * @javadoc
     */
    public static Users getBuilder() {
        return (Users) MMBase.getMMBase().getBuilder("mmbaseusers");
    }


    public Rank getRank(MMObjectNode userNode) {
        Rank rank = (Rank) rankCache.get(userNode);
        if (rank == null) {
            List ranks =  userNode.getRelatedNodes("mmbaseranks", ClusterBuilder.SEARCH_DESTINATION);
            if (ranks.size() > 1) {
                throw new SecurityException("More then one rank related to mmbase-user " + userNode.getNumber() + " (but " + ranks.size() + ")");
            }
            if (ranks.size() == 0) {
                log.debug("No ranks related to this user");
                rank = Rank.ANONYMOUS;
            } else {        
                Ranks rankBuilder = Ranks.getBuilder();
                rank = rankBuilder.getRank((MMObjectNode) ranks.get(0));
            }
            rankCache.put(userNode, rank);
        } 
        return rank;
    }        

    /**
     * Notify the cache that the rank of user node changed
     * this is fixed by CacheInvalidator alreayd ?
     public void rankChanged(MMObjectNode node) {
        rankCache.remove(node);
    }
    */


    //javadoc inherited
    public boolean setValue(MMObjectNode node, String field, Object originalValue) {
        if (field.equals(FIELD_USERNAME)) {
            Object value = node.values.get(field);
            if (node.getIntValue(FIELD_STATUS) >= 0) {
                if (originalValue != null && ! originalValue.equals(value)) {
                    node.values.put(field, value);
                    log.warn("Cannot change username (unless account is blocked)"); 
                    return false; // hmm?
                }
            } 
        } else if(field.equals(FIELD_PASSWORD)) {
            Object value = node.values.get(field);
            if (originalValue != null && ! originalValue.equals(value)) {
                node.values.put(field, encode((String) value));
            }
        }
        return true;
    }


    /**
     * @javadoc
     */
    public MMObjectNode getAnonymousUser() throws SecurityException {
        return getUser("anonymous", "");
    }

    /**
     * Gets the usernode and check its credential (password only, currently)
     * 
     * @return the authenticated user, or null
     * @throws SecurityException
     */
    public MMObjectNode getUser(String userName, String password)   {
        if (log.isDebugEnabled()) {
            log.debug("username: '" + userName + "' password: '" + password + "'");
        }
        MMObjectNode user = getUser(userName);

        if (userName.equals("anonymous")) {
            log.debug("an anonymous username");
            if (user == null) {
                throw new SecurityException("no node for anonymous user"); // odd.
            }
            return user;
        }

        if (user == null) {
            log.debug("username: '" + userName + "' --> USERNAME NOT CORRECT");
            return null;
        } 


        if (encode(password).equals(user.getStringValue(FIELD_PASSWORD))) {
            if (log.isDebugEnabled()) {
                log.debug("username: '" + userName + "' password: '" + password + "' found in node #" + user.getNumber());
            }
            if (getField(FIELD_STATUS) != null && getRank(user).getInt() < Rank.ADMIN.getInt()) {
                if (user.getIntValue(FIELD_STATUS) == -1) {
                    throw new SecurityException("account for '" + userName + "' is blocked");
                }
            }

            return user;
        } else {
            if (log.isDebugEnabled()) {
                log.debug("username: '" + userName + "' found in node #" + user.getNumber() + " --> PASSWORDS NOT EQUAL");
            }
            return null;
        }

    }
    /**
     * Gets the usernode by userName (the 'identifier'). Or 'null' if not found.
     */
    protected  MMObjectNode getUser(String userName)   {
        MMObjectNode user = (MMObjectNode) userCache.get(userName);
        if (user == null) {
            Enumeration enumeration = searchWithWhere(" username = '" + userName + "'"); 
            while(enumeration.hasMoreElements()) {
                user = (MMObjectNode) enumeration.nextElement();
            }            
            userCache.put(userName, user);
        }
        return user;
    }

    /**
     * UserName must be unique, check it also here (to throw nicer exceptions)    
     */
    public int insert(String owner, MMObjectNode node) {
        int res = super.insert(owner, node);
        String userName = node.getStringValue("username");

        Enumeration e = searchWithWhere(" username = '" + userName + "'");
        while (e.hasMoreElements()) {
            MMObjectNode n = (MMObjectNode) e.nextElement();
            if (n.getNumber() == node.getNumber()) continue;
            removeNode(node);
            throw new SecurityException("Cannot insert user '" + userName + "', because there is already is a user with that name");
        }
        userCache.clear();
        return res;
    }



    /**
     * @see org.mmbase.security.implementation.cloudcontext.User#getOwnerField
     */

    public String getDefaultContext(MMObjectNode node)  {
        return node.getNodeValue(FIELD_DEFAULTCONTEXT).getStringValue("name");
    }

    /**
     * @return The string representation the username of the User node.
     */
    public String getUserName(MMObjectNode node) {
         return node.getStringValue(FIELD_USERNAME);
    }

    /**
     * Encodes a password for storage (to avoid plain text passwords).
     */
    protected String encode(String s)  {
        return encoder.encode(s);
    }

    /**
     * @javadoc
     */
    public boolean isValid(MMObjectNode node)  {
        return true;
        /*
        if (node == null) { // if this is the case in the previous line you would have had a nullpointerexception.
            log.debug("node was null!");
            return false;
        }
        log.debug("original node #" + mmobjectnode.getNumber() + ": " + mmobjectnode.hashCode() + " current node #" + mmobjectnode1.getNumber() + " : " + mmobjectnode1.hashCode());

               
        if (mmobjectnode1 == mmobjectnode) {
            return true;
        } else {
            log.debug("hashcode's were different, comparing the number fields");
            return mmobjectnode.getNumber() == mmobjectnode1.getNumber();
        }
        */
    }


    /**
     * Makes sure unique values and not-null's are filed
     */
    public void setDefaults(MMObjectNode node) {

        MMObjectNode defaultDefaultContext = Contexts.getBuilder().getContextNode(node.getStringValue("owner"));
        node.setValue(FIELD_DEFAULTCONTEXT, defaultDefaultContext);

        node.setValue(FIELD_PASSWORD, "");

        setUniqueValue(node, FIELD_USERNAME, "user");
        
    }


    /**
     * @javadoc
     */
    public boolean check() {
        return true;
    }
    protected Object executeFunction(MMObjectNode node, String function, List args) {
        if (function.equals("info")) {
            List empty = new ArrayList();
            java.util.Map info = (java.util.Map) super.executeFunction(node, function, empty);
            info.put("gui", "(status..) Gui representation of this object.");            
            if (args == null || args.size() == 0) {
                return info;
            } else {
                return info.get(args.get(0));
            }
        } else if (args != null && args.size() > 0) {
            if (function.equals("gui")) {
                String field = (String) args.get(0);
                
                if (FIELD_STATUS.equals(field)) {
                    // THIS KIND OF STUFF SHOULD BE AVAILEBLE IN MMOBJECTBUILDER.
                    String val = node.getStringValue(field);
                    ResourceBundle bundle;
                    if (args.size() > 1) {
                        bundle = ResourceBundle.getBundle(STATUS_RESOURCE,  new Locale((String) args.get(1), ""), getClass().getClassLoader());
                    } else {
                        bundle = ResourceBundle.getBundle(STATUS_RESOURCE, new Locale(mmb.getLanguage(), ""), getClass().getClassLoader());
                    }
                    try {
                        return bundle.getString(val);
                    } catch (MissingResourceException e) {
                        return val;
                    }
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("Function '" + function + "'  not matched in users");
        }
        return super.executeFunction(node, function, args);

    }

    public boolean equals(MMObjectNode o1, MMObjectNode o2) {
        return o1.getNumber() == o2.getNumber();
    }

    public String toString(MMObjectNode n) {
        return n.getStringValue("username");
    }



}
