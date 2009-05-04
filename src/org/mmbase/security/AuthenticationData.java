/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;
import org.mmbase.util.functions.*;


/**
 * This interface represents information about the authentication implemtentation.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public interface  AuthenticationData {

    static final int METHOD_UNSET     = -1;

    // general methods
    /**
     * Requests an 'anonymous' cloud, with a user with no credentials. This can only be used if the
     * security implementation provides the 'anonymous' authentication application.
     */
    static final int METHOD_ANONYMOUS       = 0;
    /**
     * Delegates authentication completely to the authentication implementation. When using http, request and response
     * objects are added to the credentials which can be used for user-interaction.
     */
    static final int METHOD_DELEGATE        = 1;
    /**
     * Logon with given credentials (only Strings), and don't store this any where (except for the current 'page').
     */
    static final int METHOD_PAGELOGON       = 2;


    // http methods
    /**
     * Use Http 'Basic' authentication. This only provides username / password and is not very safe,
     * because http basic authentication sends those unencrypted.
     */
    static final int METHOD_HTTP            = 100;
    /**
     * Use the authenticated user which is stored in the session, or if no such user can be found,
     * try to supply 'anonymous'.
     */
    static final int METHOD_ASIS            = 101;
    /**
     * Remove the authenticated user from the session, and otherwise invalidate the user if
     * necessary (e.g. notify an authentication service).
     */
    static final int METHOD_LOGOUT          = 102;
    /**
     * Use a dedicated 'login' jsp, to log in.
     */
    static final int METHOD_LOGINPAGE       = 103;
    /**
     * Delegates authentication comletely to the authentication implementation {@link
     * #METHOD_DELEGATE}, but stores the authenticated in the session then. A second request with
     * this method will simply use the session.
     */
    static final int METHOD_SESSIONDELEGATE = 104;
    /**
     * Logon with given credentials (only Strings), and don't store this in the session.
     */
    static final int METHOD_SESSIONLOGON    = 105;



    //static final int METHOD_GIVEN_OR_ANONYMOUS = 5;


    static final int METHOD_DEFAULT = Integer.MAX_VALUE;

    static final String    STRINGS = "org.mmbase.security.resources.parameters";

    /**
     * Common parameters for logon-info
     */
    static final Parameter PARAMETER_USERNAME   = new Parameter("username", String.class, true);
    static final Parameter PARAMETER_PASSWORD   = new Parameter("password", String.class, true);
    static final Parameter PARAMETER_USERNAMES  = new Parameter("usernames", java.util.List.class);
    static final Parameter PARAMETER_RANK       = new Parameter("rank",     Rank.class);
    //    static final Parameter PARAMETER_REMOTEADDR = new Parameter("remoteaddr",   String.class);

    static final Parameter PARAMETER_SESSIONNAME    = new Parameter("sessionname",  String.class);

    // parameters used for logout
    static final Parameter PARAMETER_LOGOUT            = new Parameter("logout",  Boolean.class);
    static final Parameter PARAMETER_AUTHENTICATE  = new Parameter("authenticate", String.class);


    /**
     *	The method returns wether the UserContext has become invalid for some reason (change in security config?)
     *	@param userContext The UserContext of which we want to know the rights
     *	@return <code>true</code> when valid, otherwise <code>false</code>
     *	@exception SecurityException When something strange happened
     */
    boolean isValid(UserContext userContext) throws SecurityException;

    /**
     * Several 'methods' to authenticate could be available.
     * This method converts a user-friendly string describing the 'method' to a integer constant which can be used in
     * {@link #getTypes(int)}.
     * @param m A String like 'http', 'anonymous', 'loginpage', or 'delegatesession'.
     * @return An integer contant.
     */
    int getMethod(String m);

    /**
     * The security implementation can override a default method. The default default method (as
     * implemented in {@link org.mmbase.security.Authentication} for the 'http' protocol is HTTP
     * (which means that basic authentication of the http protocol can be used), but may not be
     * feasible for every implementation (it is e.g. useless if the security implementation does not have
     * name/password authentication).
     * @param protocol For which protocol or <code>null</code>, which means 'HTTP/1.1'.
     */
    int getDefaultMethod(String protocol);

    /**
     * Gives all availabe authentication types. The first one can be used as the default.
     */
    String[] getTypes();

    /**
     * For a given method, returns the available 'applications'. The first one can be used as the default.
     */
    String[] getTypes(int method);

    /**
     * For a given authentication type returns a parameters object to fill with credentials. {@link Parameters#toMap} can be used as the second argument
     * for {@link org.mmbase.security.Authentication#login}
     */

    Parameters createParameters(String application);
}
