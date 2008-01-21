package com.finalist.cmsc.openoffice.forms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;

import java.io.File;

public class OODocDeleteAction extends OpenOfficeIntegrationBaseAction {

    @Override
    public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {
        // TODO Auto-generated method stub
        String channel = getParameter(request, "parent");
        String fileName = getParameter(request, "name");

        String path = getOdtFileStoreLocation(getBaseStoreLocation(), channel) + File.separator + fileName;
        new File(path).delete();

        return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?parent=" + channel);
    }

}
