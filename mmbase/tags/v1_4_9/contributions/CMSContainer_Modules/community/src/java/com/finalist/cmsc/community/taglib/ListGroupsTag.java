package com.finalist.cmsc.community.taglib;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import com.finalist.cmsc.services.community.security.AuthorityService;

/**
 * @author Wouter Heijke
 */
public class ListGroupsTag extends CommunityTagSupport {
	private String var;

	@Override
	protected void doTagLogic() throws JspException, IOException {
		PageContext ctx = (PageContext) getJspContext();
		HttpServletRequest req = (HttpServletRequest) ctx.getRequest();

		AuthorityService as = getAuthorityService();
		Set<String> list = as.getAuthorityNames();
		
		if (var != null) {
			if (list != null) {
				req.setAttribute(var, list);
			} else {
				req.removeAttribute(var);
			}
		} else {
			ctx.getOut().print(list);
		}

	}

	public void setVar(String var) {
		this.var = var;
	}
}
