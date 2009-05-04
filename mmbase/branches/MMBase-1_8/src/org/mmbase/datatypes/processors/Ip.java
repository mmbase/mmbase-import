/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;

/**
 * This processor can be used as a 'commit' processor on a string field. The field will then be set
 * to the IP address of the current user. If at least that is possible.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.6
 */


public class Ip implements CommitProcessor {

    private static final long serialVersionUID = 1L;

    public void commit(Node node, Field field) {
        javax.servlet.http.HttpServletRequest req = (javax.servlet.http.HttpServletRequest) node.getCloud().getProperty("request");
        if (req != null) {
            String ip = req.getHeader("X-Forwarded-For");
            if (ip == null || "".equals(ip)) {
                ip = req.getRemoteAddr();
            }
            if (ip != null && ! "".equals(ip)) {
                node.setValueWithoutProcess(field.getName(), ip);
            }
        }
    }

    public String toString() {
        return "ip";
    }
}
