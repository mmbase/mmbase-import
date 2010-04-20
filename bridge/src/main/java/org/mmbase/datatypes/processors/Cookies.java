/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.*;
import javax.servlet.http.*;


/**
 * This processor can be used on a field to return a certain function value of the node, if the
 * field is empty. Noticeably, this can be used on 'virtual' field, to map their value to a function
 * value, which can come in handy sometimes.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.3
 */

public class Cookies  {

    private static final Logger LOG = Logging.getLoggerInstance(Cookies.class);


    protected static abstract class CookieBase  {
        private static final long serialVersionUID = 1L;

        protected String cookie;
        public void setCookie(String s) {
            cookie = s;
        }
    }

    public static class Getter extends CookieBase implements Processor {
        private static final long serialVersionUID = 1L;

        @Override
        public Object process(Node node, Field field, Object value) {
            LOG.debug("Getting default value for " + field);
            Cloud cloud = CloudThreadLocal.currentCloud();
            if (cloud == null) {
                LOG.debug("No cloud  using " + value);
                return value;
            } else {
                HttpServletRequest req = (HttpServletRequest) cloud.getProperty(Cloud.PROP_REQUEST);
                if (req == null) {
                    LOG.debug("No request  using " + value);
                    return value;
                }
                Cookie[] cookies = req.getCookies();
                if (cookies != null) {
                    for (Cookie c : cookies) {
                        LOG.debug("Considering  " + c.getName());
                        if (c.getName().equals(cookie)) {
                            LOG.debug("Found!  " + c.getValue());
                            return c.getValue();
                        }
                    }
                }
                LOG.debug("Cookie not found  using " + value);
                return value;
            }
        }
    }

    public static class Setter extends CookieBase implements Processor {
        private static final long serialVersionUID = 1L;
        @Override
        public Object process(Node node, Field field, Object value) {
            Cloud cloud = CloudThreadLocal.currentCloud();
            if (cloud == null) {
                return value;
            } else {
                HttpServletResponse res = (HttpServletResponse) cloud.getProperty(Cloud.PROP_RESPONSE);
                if (res == null) {
                    return value;
                }
                Cookie c = new Cookie(cookie, Casting.toString(value));
                c.setMaxAge(60 * 60 * 24 * 365);
                if (res.isCommitted()) {
                    LOG.warn("Cannot set cookie " + c);
                } else {
                    LOG.debug("Setting cookie " + c);
                    res.addCookie(c);
                }
                return value;
            }
        }
    }

}


