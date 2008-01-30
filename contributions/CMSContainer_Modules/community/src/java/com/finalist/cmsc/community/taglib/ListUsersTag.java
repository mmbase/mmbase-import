package com.finalist.cmsc.community.taglib;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.finalist.cmsc.services.community.security.Authentication;
import com.finalist.cmsc.services.community.security.AuthenticationService;

public class ListUsersTag extends CommunityTagSupport {

	private static Log log = LogFactory.getLog(CommunityTagSupport.class);
	
	private String var;
	
	@Override
	protected void doTagLogic() throws JspException, IOException {
	
		
		AuthenticationService as = getAuthenticationService();
		List<Authentication> list = as.findAuthentications();

		log.info("========>size="+list.size());
		
		Iterator<Authentication> lIt = list.iterator();
		while (lIt.hasNext()) {
			Authentication auth = lIt.next();
			log.info("========>"+auth.getUserId());
		}
		
//		// handle result
//		if (var != null) {
//			// put in variable
//			if (list != null) {
//				req.setAttribute(var, list);
//			} else {
//				req.removeAttribute(var);
//			}
//		} else {
//			// write
//			ctx.getOut().print(list);
//		}
		
	}

}
