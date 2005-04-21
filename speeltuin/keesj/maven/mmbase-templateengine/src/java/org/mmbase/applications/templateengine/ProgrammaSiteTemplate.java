/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;

import org.mmbase.applications.templateengine.jsp.JSPTemplate;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;
/**
 * @author keesj
 * @version $Id: ProgrammaSiteTemplate.java,v 1.1.1.1 2004-04-02 14:58:47 keesj Exp $
 */
public class ProgrammaSiteTemplate extends JSPTemplate implements Template {
    private static Logger log = Logging.getLoggerInstance(ProgrammaSiteTemplate.class);

    public ProgrammaSiteTemplate(String path, LayoutManager layoutManager) {
        super(path, layoutManager);
        log.debug("new programma site template");
    }

    public void render(WhiteBoard wb, PrintWriter writer) throws ServletException, IOException {
        log.debug("render");
        super.render(wb, writer);
    }

    public void renderRelative(String path, WhiteBoard wb) throws ServletException, IOException {
        log.debug("render relative");
        //one exception for vpro for backwards compatibility
        if (path.indexOf("index.shtml") != -1) {
            log.debug("detected old index.shtml trying automatic conversion");
            //get the objects from the params
            String querystring = wb.getHttpServletRequest().getQueryString();
            if (querystring != null) {
                StringTokenizer st = new StringTokenizer(querystring, "+");
                List list = new Vector();
                while (st.hasMoreElements()) {
                    String token = st.nextToken();
                    if (token.charAt(0) >= '0' && token.charAt(0) <= '9') {
                        int number = Integer.parseInt(token);
                        Cloud cloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
                        Node node = cloud.getNode(number);
                        if (node.getNodeManager().getName().equals("programs")
                            || node.getNodeManager().getName().equals("portals")
                            || node.getNodeManager().getName().equals("maps")) {
                            continue;
                        }
                        log.debug("SHTML PARAM {" + node.getNodeManager().getName() + "}{" + node.getNumber() + "}{" + node + "}");
                        list.add(node);
                    }
                }
                //log.debug("calling resolveURL with " + wb.getCurrentNavigation() + " " + list);
                String url = wb.getFacade().getNavigationControl().resoveURL(wb.getCurrentNavigation(), list);
                if (url != null) {
                    wb.getHttpServletResponse().sendRedirect(url);
                } else {
                    wb.getHttpServletResponse().sendRedirect(wb.getFacade().getEngineURL() + wb.getCurrentNavigation().getFullURLString() + "/");
                }
                return;
            }
        }
        super.renderRelative(path, wb);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() {
    	return new ProgrammaSiteTemplate(path,getLayoutManager());
    }
}
