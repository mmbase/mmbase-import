package com.finalist.cmsc.openoffice.forms;

import java.util.List;
import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import com.finalist.cmsc.struts.MMBaseFormlessAction;
import com.finalist.cmsc.openoffice.model.OdtDocument;
import com.finalist.cmsc.openoffice.service.OODocUploadUtil;

public class UnSavedFilesDisplayAction extends MMBaseFormlessAction {

    @Override
    public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
        // TODO Auto-generated method stub

        String dir = servlet.getServletContext().getRealPath("/") + OODocUploadUtil.TEMP_PATH;
        //save channel number at client

        String channel = getParameter(request, "parent");

        addToRequest(request, "parent", channel);
        List<OdtDocument> odts = OODocUploadUtil.getInstance().getOdtDocuments(dir + File.separator + channel);
        request.setAttribute("binaries", odts);
        return mapping.findForward(SUCCESS);
    }

}
