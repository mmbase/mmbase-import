package com.finalist.cmsc.tasks.forms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.mmapps.commons.util.StringUtil;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseAction;

public class ShowTaskAction extends MMBaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			Cloud cloud) throws Exception {
		// TODO Auto-generated method stub
		ShowTaskForm showTaskForm = (ShowTaskForm)form;
		String taskShowType=showTaskForm.getTaskShowType();
		if(!StringUtil.isEmptyOrWhitespace(taskShowType)){
			request.setAttribute("taskShowType", taskShowType);
		}
		return mapping.findForward("success");
	}

}
