package com.finalist.cmsc.openoffice.forms;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.openoffice.model.OdtDocument;
import com.finalist.cmsc.openoffice.service.OODocUploadUtil;

public class OODocUploadAction extends OpenOfficeIntegrationBaseAction {

	private static Log log = LogFactory.getLog(OODocUploadAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping,
			HttpServletRequest request, Cloud cloud) throws Exception {
		// TODO Auto-generated method stub


		OODocUploadUtil docUpload = OODocUploadUtil.getInstance();
		boolean isOdtDoc = docUpload.upload(request, getBaseStoreLocation());

		/** channel number* */
		String channel = docUpload.getChannel();
		addToRequest(request, "dir", getBaseStoreLocation());
		// save channel number at client
		addToRequest(request, "parent", channel);

		List<OdtDocument> odts = docUpload.getOdtDocuments(getOdtFileStoreLocation(getBaseStoreLocation(),channel));
		request.setAttribute("binaries", odts);
		request.setAttribute("odtDoc", isOdtDoc);
		String forwardPath = mapping.findForward(SUCCESS).getPath() + "?parent"
				+ channel;

		return new ActionForward(forwardPath);
	}
}
