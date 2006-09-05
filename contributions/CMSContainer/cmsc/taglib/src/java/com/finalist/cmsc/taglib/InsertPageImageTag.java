package com.finalist.cmsc.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.services.sitemanagement.SiteManagement;

public class InsertPageImageTag extends CmscTag {
	
	private static Log log = LogFactory.getLog(InsertPageImageTag.class);
	
	// tag parameters
	private String var;
	private String name;
	
	public void setName(String name) {
		this.name = name;
	}
	public void setVar(String var) {
		this.var = var;
	}
	
	public void doTag() {
		PageContext ctx = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
		
		String image = SiteManagement.getPageImageForPage(name, getPath());
		
        // handle result
		if(image != null) {
			if(var != null) {
				request.setAttribute(var, image);
			}
			else {
				HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
				try {
					response.getWriter().print(image);
				} catch (IOException e) {
					e.printStackTrace();
					log.error("Unable to write image to the output: "+e);
				}
			}
		}
		else {
			log.debug("Image with name "+name+" not found for path: "+getPath());
		}
	}
}
