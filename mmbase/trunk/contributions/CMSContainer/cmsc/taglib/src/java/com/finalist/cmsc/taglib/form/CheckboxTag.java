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

import net.sf.mmapps.commons.util.StringUtil;

public class CheckboxTag extends SimpleTagSupport {

	public String var;
	public String value;
	public String[] selected;

	@Override
	public void doTag() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

		selected = (String[]) request.getParameterValues(var);


		boolean isSelected = false;
		if (selected != null) {
			for (int i = 0; i < selected.length; i++) {
				if (selected[i].equals(value)) {
					isSelected = true;
					continue;
				}
			}
		}

		ctx.getOut().print("<input type=\"checkbox\" name=\"" + var + "\" value=\"" + value + "\" ");
		if (isSelected) {
			ctx.getOut().print("selected");
		}
		ctx.getOut().print(">");
		JspFragment frag = getJspBody();
		if (frag != null) {
			frag.invoke(null);
		}
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}