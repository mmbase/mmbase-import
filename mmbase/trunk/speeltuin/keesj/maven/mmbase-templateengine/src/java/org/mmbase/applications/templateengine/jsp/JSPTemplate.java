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
 * @author Kees Jongenburger
 */
public class JSPTemplate extends JSPContainer implements Template {
	
    public JSPTemplate(String path, LayoutManager layout) {
        super(path, layout);
    }

    public void render(WhiteBoard wb, PrintWriter writer) throws ServletException, IOException  {
        wb.getHttpServletRequest().setAttribute("template", this);
        super.render(wb, writer);
    }
    
    public Object clone(){
    	JSPTemplate t =  new JSPTemplate(path,getLayoutManager());
    	t.setName(getName());
    	t.setDescription(getDescription());
    	t.setProperties(getProperties());
    	return t;
    }
}
