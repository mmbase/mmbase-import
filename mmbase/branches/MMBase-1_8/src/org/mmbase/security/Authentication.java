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
 *  This class is a abstract implementation of the Authentication.
 *
 *  To make your own implementation of authentication, you have to extend this class.
 *
 * @author Eduard Witteveen
 * @author Michiel Meeuwissen (javadocs)
 * @version $Id$
 */
public abstract class Authentication extends Configurable implements AuthenticationData {
    private static final Logger log = Logging.getLoggerInstance(Authentication.class);

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
            log.error(e);
        }
    }


    /**
     *  This method will verify the login, and give a UserContext back if the login procedure was successful.
     *	@param application A String that further specifies the login method (one implementation could handle more then one methods)
     *                     A typical value might be 'username/password'.
     *
     *	@param loginInfo   A Map containing the credentials or other objects which might be used to obtain them (e.g. request/response objects).
     *                     It might also be 'null', in which case your implementation normally should return the 'anonymous' user (or null, if
     *                     no such user can be defined).
     *
     *	@param parameters  A list of optional parameters, may also (and will often) be null.
     *
     *	@return <code>null</code if no valid credentials were supplied,  a (perhaps new) UserContext if login succeeded.
     *
     *	@exception SecurityException When something strang happened
     */
    public abstract UserContext login(String application, Map loginInfo, Object[] parameters) throws SecurityException;

    /**
     * @since MMBase-1.8
     */
    public int getMethod(String m) {
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
     * Some unique key associated with this security configuration. It can be explicitely set with
     * the 'key' entry in security.xml. It falls back to the current time in millis at the time of
     * initialization of authentication.
     *
     * @since MMBase-1.8
     */
    public long getKey() {
        return key;
    }
}
