/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import javax.servlet.ServletException;
import org.mmbase.bridge.*;

/**
 * BridgeServlet is an MMBaseServlet with a bridge Cloud in it. Extending from this makes it easy to 
 * implement servlet implemented with the MMBase bridge interfaces.
 *
 * @version $Id: BridgeServlet.java,v 1.1 2002-06-27 14:03:11 michiel Exp $
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */
public abstract class BridgeServlet extends  MMBaseServlet {

    protected Cloud cloud;

    protected String getCloudName() {
        return "mmbase";
    }

    /**
     */

    public void init() throws ServletException {
        super.init();
        cloud = LocalContext.getCloudContext().getCloud(getCloudName());
    }

}
