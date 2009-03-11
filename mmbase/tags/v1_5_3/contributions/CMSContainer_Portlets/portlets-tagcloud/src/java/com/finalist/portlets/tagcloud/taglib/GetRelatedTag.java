package com.finalist.portlets.tagcloud.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.portlets.tagcloud.Tag;
import com.finalist.portlets.tagcloud.util.TagCloudUtil;

public class GetRelatedTag extends SimpleTagSupport {

	private String var;
	private Integer related;
	
	public void doTag() throws JspException, IOException {
		super.doTag();
		
		List<Tag> tags = TagCloudUtil.getRelatedTags(related);
		getJspContext().setAttribute(var, tags);
	}
	
	public void setRelated(String related) {
		this.related = Integer.parseInt(related);
	}


	public void setVar(String var) {
		this.var = var;
	}

	
}
