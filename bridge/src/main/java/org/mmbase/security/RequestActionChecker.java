/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import java.util.regex.*;
import javax.servlet.http.*;
import org.mmbase.util.functions.Parameters;
import org.mmbase.util.functions.Parameter;
import org.mmbase.util.logging.*;

/**
 * This action checker can deny an action based on properties of the request made by the
 * user. E.g. access may be denied from certain IP's or e.g. you may want to allow a certain action
 * only when done via HTTPS.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.2
 * @todo EXPERIMENTAL
 */
public class  RequestActionChecker implements ActionChecker  {
    private static final long serialVersionUID = 0L;

    private static Parameter[] PARAMS = new Parameter[] { Parameter.REQUEST };
    private static final Logger log   = Logging.getLoggerInstance(RequestActionChecker.class);

    Pattern allowedSchemes = Pattern.compile("http|https");
    Pattern allowedIps       = Pattern.compile(".*");
    Pattern users            = null;

    ActionChecker rank       = ActionChecker.ALLOWS;

    public void setAllowedSchemes(String s) {
        allowedSchemes = Pattern.compile(s);
    }

    public void setAllowedIps(String i) {
        allowedIps = Pattern.compile(i);
    }

    public void setRank(String r) {
        rank = new ActionChecker.Rank(org.mmbase.security.Rank.getRank(r));
    }

    public void setUsers(String u) {
        users = Pattern.compile(u);
    }

    @Override
    public boolean check(UserContext user, Action ac, Parameters parameters) {
        if (users == null) {
            if (! rank.check(user, ac, parameters)) {
                // only a rank configured
                log.debug("Users rank does not match " + rank);
                return false;
            } else {
                log.debug(" " + user + " matches " + rank);
            }
        } else {
            if (rank == ActionChecker.ALLOWS) {
                // only a user configured
                if (! users.matcher(user.getIdentifier()).matches()) {
                    log.debug("user name does not match " + users);
                    return false;
                } else {
                    log.debug(" " + user + " matches " + users);
                }
            } else {
                // both rank and user configured. If both don't match, deny access
                if (! rank.check(user, ac, parameters) && ! users.matcher(user.getIdentifier()).matches()) {
                    log.debug("Users rank does not match " + rank + "and user name does not match " + users);
                    return false;
                } else {
                    log.debug(" " + user + " matches " + rank + " and " + users);
                }
            }
        }

        HttpServletRequest req = org.mmbase.framework.basic.BasicUrlConverter.getUserRequest(parameters.get(Parameter.REQUEST));
        if (! allowedSchemes.matcher(req.getScheme()).matches()) {
            return false;
        }
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || "".equals(ip)) {
            ip = req.getRemoteAddr();
        }
        return allowedIps.matcher(ip).matches();


    }

    @Override
    public Parameter[] getParameterDefinition() {
        return PARAMS;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (rank != ActionChecker.ALLOWS) {
            buf.append(rank.toString());
        }
        if (users != null) {
            if (buf.length() > 0) {
                buf.append(" | ");
            }
            buf.append(users.toString());
        }

        buf.append('@').append(allowedSchemes.toString()).append("://").append(allowedIps.toString());
        return buf.toString();
    }



}
