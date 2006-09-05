package com.finalist.cmsc.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.util.module.ModuleUtil;

/**
 * Check if a named CMSC feature is active or installed
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.2 $
 */
public class FeatureTag extends SimpleTagSupport {
	private static Log log = LogFactory.getLog(DumpDefaultsTag.class);

	/**
	 * Name of feature
	 */
	private String name;

	public void doTag() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();

		boolean hasFeature = ModuleUtil.checkFeature(name);
		if (hasFeature) {
			JspFragment frag = getJspBody();
			if (frag != null) {
				frag.invoke(null);
			}
		}
	}

	public void setName(String name) {
		this.name = name;
	}

}
