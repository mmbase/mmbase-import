package com.finalist.cmsc.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.util.version.VersionUtil;


public class VersionTag extends SimpleTagSupport {

	public void doTag() throws JspException, IOException {
		
		PageContext ctx = (PageContext) getJspContext();
		
		String version = VersionUtil.getVersion(ctx.getServletContext());

		ctx.getOut().write(version);
	}
}
