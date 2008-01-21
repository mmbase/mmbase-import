package com.finalist.cmsc.upload.forms;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;

import com.finalist.cmsc.repository.ContentElementUtil;
import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.upload.service.OODocUploadUtil;

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
