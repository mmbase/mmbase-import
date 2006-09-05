package com.finalist.cmsc.resources.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import com.finalist.cmsc.struts.MMBaseAction;

public class DeleteSecondaryContentAction extends MMBaseAction {

    private static transient Logger log = Logging.getLoggerInstance(DeleteSecondaryContentAction.class.getName());

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, Cloud cloud) throws Exception {
		DeleteSecondaryContentForm deleteForm = (DeleteSecondaryContentForm) form;
		
		String number = deleteForm.getObjectnumber();
		if(MMBaseAction.ADMINISTRATOR.equals(cloud.getUser().getRank().toString())) {
			log.debug("deleting secondary content: "+number);
			cloud.getNode(number).delete(true);
		}
		else {
			log.warn("did not delete secondary content because user was not administrator: "+number+" ("+cloud.getUser()+":"+cloud.getUser().getRank()+")");
		}
		
		String returnurl = deleteForm.getReturnurl();
		return new ActionForward(returnurl);
	}

}
