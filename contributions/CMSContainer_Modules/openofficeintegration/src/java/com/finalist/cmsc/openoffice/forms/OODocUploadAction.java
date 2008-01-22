package com.finalist.cmsc.openoffice.forms;

import com.finalist.cmsc.openoffice.model.OdtDocument;
import com.finalist.cmsc.openoffice.service.OODocUploadUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.util.List;

public class OODocUploadAction extends OpenOfficeIntegrationBaseAction {

    private static Log log = LogFactory.getLog(OODocUploadAction.class);

    @Override
    public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
        // TODO Auto-generated method stub

        String dir = servlet.getServletContext().getRealPath("/") + OODocUploadUtil.TEMP_PATH;

        OODocUploadUtil docUpload = OODocUploadUtil.getInstance();
        boolean isOdtDoc =  docUpload.upload(request, dir);

        /**channel number**/
        String channel = docUpload.getChannel();
        if (StringUtils.isBlank(channel) || channel.equals("null"))
            channel = docUpload.getChannelbak();
        request.setAttribute("dir", dir);

        //save channel number at client
        addToRequest(request, "parent", channel);

        List<OdtDocument> odts = docUpload.getOdtDocuments(dir+ File.separator + channel);
        request.setAttribute("binaries", odts);
        request.setAttribute("odtDoc", isOdtDoc);
        String forwardPath = mapping.findForward(SUCCESS).getPath()+"?parent"+channel;
        
        return new ActionForward(forwardPath);
    }
}
