/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import java.util.Map;

import org.mmbase.util.functions.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *  This class is an abstract implementation of user authentication in MMBase.
 *
 *  To make your own implementation of authentication, you have to extend this class.
 *
 * @author Eduard Witteveen
 * @author Michiel Meeuwissen (javadocs)
 * @version $Id$
 */
public abstract class Authentication extends Configurable implements AuthenticationData {
    private static final Logger log = Logging.getLoggerInstance(Authentication.class);


    protected final Map<String, Object> attributes = new java.util.concurrent.ConcurrentHashMap<String, Object>();
    static {
        try {
            PARAMETER_USERNAME.getLocalizedDescription().setBundle(STRINGS);
            PARAMETER_PASSWORD.getLocalizedDescription().setBundle(STRINGS);
            ((org.mmbase.datatypes.StringDataType) PARAMETER_PASSWORD.getDataType()).setPassword(true);
            PARAMETER_USERNAMES.getLocalizedDescription().setBundle(STRINGS);
            PARAMETER_RANK.getLocalizedDescription().setBundle(STRINGS);
            PARAMETER_SESSIONNAME.getLocalizedDescription().setBundle(STRINGS);
            PARAMETER_LOGOUT.getLocalizedDescription().setBundle(STRINGS);
            PARAMETER_AUTHENTICATE.getLocalizedDescription().setBundle(STRINGS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     *  This method will verify the login, and give a UserContext back if the login procedure was successful.
     *	@param application A String that further specifies the login method (one implementation could handle more then one methods)
     *                     A typical value might be 'username/password'.
     *                     Possible values are returned by {@link #getTypes}.
     *                     This is also called 'authentication', or '(authentication) type' in
     *                     several contextes.
     *
     *	@param loginInfo   A Map containing the credentials or other objects which might be used to obtain them (e.g. request/response objects).
     *                     It might also be 'null', in which case your implementation normally should return the 'anonymous' user (or null, if
     *                     no such user can be defined). This Map can (or must) be supplied by
     *                     {@link #createParameters} (using the setter-methods and the {@link
     *                     Parameters#toMap} method of the resulting Parameters object).
     *
     *	@param parameters  A list of optional parameters, may also (and will often) be null.
     *
     *	@return <code>null</code> if no valid credentials were supplied,  a (perhaps new) UserContext if login succeeded.
     *
     *	@exception SecurityException When something strange happened, or authentication was unsuccessful.
     */
    public abstract UserContext login(String application, Map<String, ?> loginInfo, Object[] parameters) throws SecurityException;

    /**
     * {@inheritDoc}
     * @since MMBase-1.9
     */
    public int getNode(UserContext userContext) throws SecurityException, UnsupportedOperationException {
        throw new UnsupportedOperationException("This security implementation (" + getClass() + ") does not support mapping from Security usercontexts to MMBase nodes");
    }

    /**
     * {@inheritDoc}
     * @since MMBase-1.9
     */
    public String getUserBuilder() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This security implementation (" + getClass() + ") has no builder associated with UserContexts");
    }

    /**
     * {@inheritDoc}
     * @since MMBase-1.8
     */
    public final int getMethod(String m) {
        if (m == null || m.equals("")) {
            return METHOD_UNSET;
        }
        m = m.toLowerCase();
        if ("http".equals(m)) {
            return METHOD_HTTP;
        } else if ("asis".equals(m)) {
            return METHOD_ASIS;
        } else if ("anonymous".equals(m)) {
            return METHOD_ANONYMOUS;
        } else if ("logout".equals(m)) {
            return METHOD_LOGOUT;
        } else if ("loginpage".equals(m)) {
            return METHOD_LOGINPAGE;
        } else if ("delegate".equals(m)) {
            return METHOD_DELEGATE;
        } else if ("sessiondelegate".equals(m)) {
            return METHOD_SESSIONDELEGATE;
        } else if ("pagelogon".equals(m)) {
            return METHOD_PAGELOGON;
        } else if ("sessionlogon".equals(m)) {
            return METHOD_SESSIONLOGON;
        } else if ("default".equals(m)) {
            return METHOD_DEFAULT;
            //} else if ("given_or_anonymous".equals(m)) {
            //    return METHOD_GIVEN_OR_ANONYMOUS;
        } else {
            throw new RuntimeException("Unknown value for 'method'  attribute (" + m + ")");
        }
    }


    /**
     *
     * @since MMBase-1.9
     */
    public static final String getMethod(int m) {
        switch(m) {
        case METHOD_HTTP: return "http";
        case METHOD_ASIS: return "asis";
        case METHOD_ANONYMOUS: return "anonymous";
        case METHOD_LOGOUT: return "logout";
        case METHOD_DELEGATE: return "delegate";
        case METHOD_SESSIONDELEGATE: return "sessiondelegate";
        case METHOD_PAGELOGON: return "pagelogon";
        case METHOD_SESSIONLOGON: return "sessionlogon";
        case METHOD_DEFAULT: return "default";
        default: return "unknown";
        }
    }

    /**
     * {@inheritDoc}
     * @since MMBase-1.8
     */
    public int getDefaultMethod(String protocol) {
        if (protocol == null || protocol.substring(0, 4).equalsIgnoreCase("HTTP")) {
            return METHOD_HTTP;
        } else {
            return METHOD_DELEGATE; // leave it completely to the implementation. (using the 'class' application or the request object or so)
        }
    }

    /**
     * {@inheritDoc}
     * @since MMBase-1.8
     */
    public String[] getTypes() {
        return getTypes(METHOD_UNSET);
    }
    /**
     * {@inheritDoc}
     * @since MMBase-1.8
     */
    public String[] getTypes(int method) {
        if (method == METHOD_ASIS) {
            return new String[] {"anonymous", "name/password", "class"};
        } else {
            return new String[] {"name/password", "class"};
        }
    }

    protected static final Parameter[] PARAMETERS_USERS         = new Parameter[] { PARAMETER_USERNAMES, PARAMETER_RANK };
    protected static final Parameter[] PARAMETERS_ANONYMOUS     = new Parameter[] { PARAMETER_LOGOUT, PARAMETER_AUTHENTICATE};
    protected static final Parameter[] PARAMETERS_NAME_PASSWORD = new Parameter[] { PARAMETER_USERNAME, PARAMETER_PASSWORD, new Parameter.Wrapper(PARAMETERS_USERS) };

    /**
     * {@inheritDoc}
     * @since MMBase-1.8
     */
    public Parameters createParameters(String application) {
        application = application.toLowerCase();
        if ("anonymous".equals(application)) {
            return new Parameters(PARAMETERS_ANONYMOUS);
        } else if ("class".equals(application)) {
            return Parameters.VOID;
        } else if ("name/password".equals(application)) {
            return new Parameters(PARAMETERS_NAME_PASSWORD);
        } else {
            return new AutodefiningParameters();
        }
    }

    long key = System.currentTimeMillis();

    /**
     *<p> Some unique key associated with this security configuration. It can be explicitly set with
     * the 'key' entry in security.xml. It falls back to the current time in milliseconds at the time of
     * initialization of authentication.</p>
     *
     * <p>The advantage of explicitly configuring it, is that serialized user-contextes remain valid
     * after a restart of MMBase, and users need not to log in again then.</p>
     *
     * @since MMBase-1.8
     */
    public long getKey() {
        return key;
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }
}
