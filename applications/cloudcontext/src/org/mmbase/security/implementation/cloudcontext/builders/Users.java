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
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.cache.Cache;
import org.mmbase.cache.QueryResultCache;
import org.mmbase.util.Encode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.functions.*;

/**
 * This MMObjectBuilder implementation belongs to the object type
 * 'mmbaseusers' It contains functionality to MD5 encode passwords,
 * and so on.
 *
 *
 * @author Eduard Witteveen
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id: Users.java,v 1.61 2009-01-06 11:45:25 michiel Exp $
 * @since  MMBase-1.7
 */
public class Users extends MMObjectBuilder {

    private static final Logger log = Logging.getLoggerInstance(Users.class);


    public final static String FIELD_STATUS      = "status";
    public final static String FIELD_USERNAME    = "username";
    public final static String FIELD_PASSWORD    = "password";
    public final static String FIELD_DEFAULTCONTEXT    = "defaultcontext";
    public final static String FIELD_VALID_FROM    = "validfrom";
    public final static String FIELD_VALID_TO      = "validto";
    public final static String FIELD_LAST_LOGON    = "lastlogon";

    public final static long VALID_TO_DEFAULT      = 4102441200L; // 2100-1-1


    public final static String STATUS_RESOURCE = "org.mmbase.security.status";



    protected Function<String> encodeFunction = new AbstractFunction<String>("encode", new Parameter[] {new Parameter<String>("password", String.class, true) }, ReturnType.STRING) {
            {
                setDescription("Encodes a string like it would happen with a password, when it's stored in the database.");
            }
            public String getFunctionValue(Parameters parameters) {
                String e = (String)parameters.get(0);
                return Users.this.provider.encode(e);
            }
    };

    protected Function<Rank> rankFunction = new NodeFunction<Rank>("rank", Parameter.emptyArray(), new ReturnType<Rank>(Rank.class, "Rank")) {
            {
                setDescription("Returns the rank of an mmbaseusers node");
            }
            public Rank getFunctionValue(org.mmbase.bridge.Node node, Parameters parameters) {
                return Users.this.getRank(getCoreNode(Users.this, node));
            }
    };
    {
        addFunction(encodeFunction);
        addFunction(rankFunction);
    }

    private boolean userNameCaseSensitive = false;

    private final UserProvider provider = new BasicUserProvider(Users.this) {
            @Override protected boolean getUserNameCaseSensitive() {
                return Users.this.userNameCaseSensitive;
            }
        };

    // javadoc inherited
    public boolean init() {

        String s = getInitParameters().get("userNameCaseSensitive");
        if (s != null) {
            userNameCaseSensitive = "true".equals(s);
            log.debug("property 'userNameCaseSensitive' set to '" +userNameCaseSensitive);
        }

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

    /**
     * @javadoc
     */
    public static Users getBuilder() {
        return (Users) MMBase.getMMBase().getBuilder("mmbaseusers");
    }

    public UserProvider getProvider() {
        return provider;
    }

    /**
     * @deprecated
     */
    public Rank getRank(MMObjectNode userNode) {
        return provider.getRank(userNode);
    }


    @Override public boolean setValue(MMObjectNode node, String field, Object originalValue) {
        if (field.equals(FIELD_USERNAME)) {
            Object value = node.getValue(field);
            if (node.getIntValue(FIELD_STATUS) >= 0) {
                if (originalValue != null && ! originalValue.equals(value)) {
                    /*
                    node.values.put(field, value);
                    log.warn("Cannot change username (unless account is blocked)");
                    return false; // hmm?
                    */
                    log.debug("Changing account '" + originalValue + "' to '" + value + "'");
                }
            }
        }
        return true;
    }


    public MMObjectNode getAnonymousUser() throws SecurityException {
        return provider.getAnonymousUser();
    }

    /**
     * Gets the usernode and check its credential (password only, currently)
     *
     * @return the authenticated user, or null
     * @throws SecurityException
     */
    public MMObjectNode getUser(String userName, String password)  {
        return getUser(userName, password, true);
    }

    public MMObjectNode getUser(String userName, String password, boolean encode) {
        return provider.getUser(userName, password, encode);
    }
    /**
     * Gets the usernode by userName (the 'identifier'). Or a securityException if not found.
     */
    public MMObjectNode getUser(String userName)  {
        return provider.getUser(userName);
    }

    /**
     * @param rank Rank to be searched. Never <code>null</code>.
     * @param userName Username to match or <code>null</code>
     * @since MMBase-1.8
     */
    public MMObjectNode getUserByRank(String rank, String userName) {
        return provider.getUserByRank(rank, userName);
    }

    /**
     * UserName must be unique, check it also here (to throw nicer exceptions)
     */
    public int insert(String owner, MMObjectNode node) {
        String userName = node.getStringValue(FIELD_USERNAME);
        if (!userNameCaseSensitive && userName!=null) {
            userName = userName.toLowerCase();
            node.setValue(FIELD_USERNAME,userName);
        }
        int res = super.insert(owner, node);
        NodeSearchQuery nsq = new NodeSearchQuery(this);
        StepField sf        = nsq.getField(getField(FIELD_USERNAME));
        BasicFieldValueConstraint cons = new BasicFieldValueConstraint(sf, userName);
        cons.setCaseSensitive(userNameCaseSensitive);
        nsq.setConstraint(cons);
        try {
            Iterator<MMObjectNode> i = getNodes(nsq).iterator();
            while(i.hasNext()) {
                MMObjectNode n = i.next();
                if (n.getNumber() == node.getNumber()) continue;
                removeNode(node);
                throw new SecurityException("Cannot insert user '" + userName + "', because there is already is a user with that name");
            }
        } catch (SearchQueryException sqe) {
            throw new SecurityException("Cannot insert user '" + userName + "', because check-query failed:" + sqe.getMessage() ,sqe );
        }
        Caches.getUserCache().clear();
        return res;
    }

    /**
     * @see org.mmbase.security.implementation.cloudcontext.User#getOwnerField
     */
    public String getDefaultContext(MMObjectNode node)  {
        return provider.getDefaultContext(node);
    }

    /**
     * @return The string representation the username of the User node.
     */
    public String getUserName(MMObjectNode node) {
        if (node.getBuilder().hasField(FIELD_USERNAME)) {
            return node.getStringValue(FIELD_USERNAME);
        } else {
            return null;
        }
    }


    /**
     * @javadoc
     */
    public boolean isValid(MMObjectNode node)  {
        return provider.isValid(node);
    }

    /**
     * Makes sure unique values and not-null's are filed
     */
    /*
    public void setDefaults(MMObjectNode node) {
        super.setDefaults(node);
        MMObjectNode defaultDefaultContext = Contexts.getBuilder().getContextNode(node.getStringValue("owner"));
        node.setValue(FIELD_DEFAULTCONTEXT, defaultDefaultContext);
        node.setValue(FIELD_PASSWORD, "");
        node.setValue(FIELD_STATUS, 0);
        String currentUserName = node.getStringValue(FIELD_USERNAME);
        if (currentUserName.equals("")) {
            currentUserName = "user";
        }
        setUniqueValue(node, FIELD_USERNAME, currentUserName);

     }
    */

   protected Object executeFunction(MMObjectNode node, String function, List<?> args) {
        if (function.equals("info")) {
            List<Object> empty = new ArrayList<Object>();
            java.util.Map<String,String> info =
                (java.util.Map<String,String>) super.executeFunction(node, function, empty);
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
                    Parameters pars = Functions.buildParameters(GUI_PARAMETERS, args);
                    Locale locale = pars.get(Parameter.LOCALE);
                    if (locale == null) {
                        String lang = pars.get(Parameter.LANGUAGE);
                        if (lang != null){
                            locale = new Locale(lang, "");
                        }
                    }
                    if (locale == null) {
                        locale = mmb.getLocale();
                    }
                    bundle = ResourceBundle.getBundle(STATUS_RESOURCE,  locale, getClass().getClassLoader());

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
        return n.getStringValue(FIELD_USERNAME);
    }


    /**
     * @since MMBase-1.8.7
     */
    public MMObjectBuilder getUserBuilder() {
        return this;
    }


}
