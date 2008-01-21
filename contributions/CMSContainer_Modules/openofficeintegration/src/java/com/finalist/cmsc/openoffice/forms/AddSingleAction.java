package com.finalist.cmsc.openoffice.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.openoffice.service.OODocUploadUtil;

public class AddSingleAction extends MMBaseFormlessAction {

	
	
	
	@Override
	public ActionForward execute(ActionMapping mapping,
			HttpServletRequest request, Cloud cloud) throws Exception {

		String dir = servlet.getServletContext().getRealPath("/")+OODocUploadUtil.TEMP_PATH;
		
		OODocUploadUtil docUpload = OODocUploadUtil.getInstance(); 
		docUpload.upload(request,dir);
		
		return mapping.findForward(SUCCESS);
	}

}
