package com.finalist.cmsc.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.cmsc.mmbase.PropertiesUtil;


public class PropertyTag extends SimpleTagSupport {


	private String key;
	
	public void setKey(String key) {
		this.key = key;
	}



	public void doTag() throws JspException, IOException {
		
		PageContext ctx = (PageContext) getJspContext();
		
		String property = PropertiesUtil.getProperty(key);

		ctx.getOut().write(property);
	}

}
