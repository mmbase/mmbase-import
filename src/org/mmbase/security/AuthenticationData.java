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
 * @version $Id: AuthenticationData.java,v 1.2 2005-03-07 15:13:15 michiel Exp $
 * @since MMBase-1.8
 */
public interface  AuthenticationData {

    static final int METHOD_UNSET     = -1;

    // general methods
    static final int METHOD_ANONYMOUS       = 0;
    static final int METHOD_DELEGATE        = 1;
    static final int METHOD_PAGELOGON       = 2;


    // http methods
    static final int METHOD_HTTP            = 100;
    static final int METHOD_ASIS            = 101;
    static final int METHOD_LOGOUT          = 102;
    static final int METHOD_LOGINPAGE       = 103;
    static final int METHOD_SESSIONDELEGATE = 104;
    static final int METHOD_SESSIONLOGON    = 105;



    //static final int METHOD_GIVEN_OR_ANONYMOUS = 5;


    static final int METHOD_DEFAULT = Integer.MAX_VALUE;


    /**
     * Common parameters for logon-info
     */
    static final Parameter PARAMETER_USERNAME  = new Parameter("username", String.class, true);
    static final Parameter PARAMETER_PASSWORD  = new Parameter("password", String.class, true);
    static final Parameter PARAMETER_USERNAMES = new Parameter("usernames", java.util.List.class);
    static final Parameter PARAMETER_RANK      = new Parameter("rank",     Rank.class);

    static final Parameter PARAMETER_SESSIONNAME    = new Parameter("sessionname",  String.class);

    // parameters used for logout
    static final Parameter PARAMETER_LOGOUT        = new Parameter("logout",  Boolean.class);
    static final Parameter PARAMETER_AUTHENTICATE  = new Parameter("authenticate", String.class);

    /**
     *	The method returns wether the UserContext has become invalid for some reason (change in security config?)
     *	@param userContext The UserContext of which we want to know the rights
     *	@return <code>true</code> when valid, otherwise <code>false</code>
     *	@exception SecurityException When something strang happend
     */
    boolean isValid(UserContext userContext) throws SecurityException;

    /**
     * Several 'methods' to authenticate could be available.
     * This method converts a user-friendly string describing the 'method' to a integer constant which can be used in 
     * {@link getApplications(int)}.
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
