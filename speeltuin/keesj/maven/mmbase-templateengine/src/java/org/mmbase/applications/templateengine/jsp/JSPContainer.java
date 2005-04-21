/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine.jsp;

import java.io.*;
import java.io.PrintWriter;

import javax.servlet.ServletException;

import org.mmbase.applications.templateengine.*;

/**
 * @author Kees Jongenburger
 */
public class JSPContainer extends JSPComponent implements Container {
        
    public JSPContainer(String path, LayoutManager layoutManager) {
    	super(path);
        setLayoutManager(layoutManager);
    }

    public void render(WhiteBoard wb, PrintWriter writer) throws ServletException, IOException {
        wb.getHttpServletRequest().setAttribute("container", this);
        super.render(wb, writer);
    }

    public void renderRelative(String path, WhiteBoard wb) throws ServletException, IOException {
        super.renderRelative(path, wb);
    }
    
    public Object clone(){
    	JSPContainer c =  new JSPContainer(path,getLayoutManager());
    	c.setName(getName());
    	c.setDescription(getDescription());
    	c.setProperties(getProperties());
    	return c;
    }
}
