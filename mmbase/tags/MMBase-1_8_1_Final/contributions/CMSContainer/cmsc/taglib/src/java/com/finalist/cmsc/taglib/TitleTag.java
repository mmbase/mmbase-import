/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * Tag to show the title of a Screen
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1 $
 */
public class TitleTag extends SimpleTagSupport {

	/**
	 * JSP variable name.
	 */
	public String var;

	public void doTag() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

		ScreenTag container = (ScreenTag) findAncestorWithClass(this, ScreenTag.class);
		if (container != null) {
			String title = container.getTitle();

			// handle result
			if (var != null) {
				// put in variable
				if (title != null) {
					request.setAttribute(var, title);
				} else {
					request.removeAttribute(var);
				}
			} else {
				// write
				ctx.getOut().print(title);
			}
		} else {
			throw new JspException("Couldn't find screen tag");
		}
	}

	public void setVar(String var) {
		this.var = var;
	}
}
