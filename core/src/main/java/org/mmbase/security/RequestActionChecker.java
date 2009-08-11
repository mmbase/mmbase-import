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

/**
 * This action checker can deny a action based on property of the request made by the
 * user. E.g. access may be denied from certain IP's or e.g. you may want to allow a certain action
 * only when done via HTTPs.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.2
 * @todo EXPERIMENTAL
 */
public class  RequestActionChecker implements ActionChecker  {
    private static final long serialVersionUID = 0L;

    private static Parameter[] PARAMS = new Parameter[] { Parameter.REQUEST };

    Pattern allowedSchemes = Pattern.compile("http|https");
    Pattern allowedIps       = Pattern.compile(".*");

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

    public boolean check(UserContext user, Action ac, Parameters parameters) {
        if (! rank.check(user, ac, parameters)) return false;
        HttpServletRequest req = org.mmbase.framework.basic.BasicUrlConverter.getUserRequest(parameters.get(Parameter.REQUEST));
        if (! allowedSchemes.matcher(req.getScheme()).matches()) return false;
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || "".equals(ip)) {
            ip = req.getRemoteAddr();
        }
        if (! allowedIps.matcher(ip).matches()) return false;
        return true;



    }

    public Parameter[] getParameterDefinition() {
        return PARAMS;
    }



}
