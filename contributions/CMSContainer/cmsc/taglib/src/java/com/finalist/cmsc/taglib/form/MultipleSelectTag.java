/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package com.finalist.cmsc.taglib.form;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class MultipleSelectTag extends SimpleTagSupport {

	private String var;
	private String[] selected;
	private int size = 0;

	@Override
	public void doTag() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
		
		
		Object values = request.getAttribute(var);
		if ( values != null ) {
		selected = (String[]) values;
		}
		
		ctx.getOut().print("<select name=\"" + var + "\"");

		if (size > 0) {
			ctx.getOut().print(" size=\"" + size + "\"");
		}

		ctx.getOut().print(" multiple=\"multiple\">");

		JspFragment frag = getJspBody();
		if (frag != null) {
			frag.invoke(null);
		}
		ctx.getOut().print("</select>");
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public boolean isSelected(String key) {
		if (selected != null) {
			for (int i = 0; i < selected.length; i++) {
				if (selected[i].equals(key)) {
					return (true);
				}
			}
		}
		return (false);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		if (size > 0) {
			this.size = size;
		} else {
			this.size = 0;
		}
	}
}