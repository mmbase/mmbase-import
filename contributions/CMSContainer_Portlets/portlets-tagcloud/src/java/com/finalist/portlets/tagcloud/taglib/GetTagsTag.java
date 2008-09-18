package com.finalist.portlets.tagcloud.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.finalist.portlets.tagcloud.Tag;
import com.finalist.portlets.tagcloud.util.TagCloudUtil;

public class GetTagsTag extends SimpleTagSupport {

	private String var;
	private Integer max;
	private String orderby = TagCloudUtil.ORDERBY_COUNT;
	private String direction;

	public void doTag() throws JspException, IOException {
		super.doTag();
		
		List<Tag> tags = TagCloudUtil.getTags(max, orderby, direction);
		getJspContext().setAttribute(var, tags);
	}
	

	public void setMax(String max) {
		this.max = Integer.parseInt(max);
	}


	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}


	public void setVar(String var) {
		this.var = var;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}
	
}
