/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine.jsp;

import java.io.*;

import javax.servlet.ServletException;

import org.mmbase.applications.templateengine.*;

/**
 * class to call a layout manager implemented in jsp
 * @author Kees Jongenburger
 */
public class JSPLayoutManager implements LayoutManager {
    JSPComponent jspTemplate;
    
    public JSPLayoutManager(String path, String name, String description) {
        jspTemplate = new JSPComponent(path);
		jspTemplate.setName(name);
		jspTemplate.setDescription(description);
		
    }

    public void render(WhiteBoard wb, Container container, PrintWriter pw) throws ServletException, IOException {
        wb.getHttpServletRequest().setAttribute("container", container);
        jspTemplate.render(wb, pw);
    }

    /* (non-Javadoc)
     * @see te.LayoutManager#getName()
     */
    public String getName() {
    	return jspTemplate.getName();
    }

    /* (non-Javadoc)
     * @see te.LayoutManager#getDescription()
     */
    public String getDescription() {
        return jspTemplate.getDescription();
    }
}
