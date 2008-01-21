package com.finalist.cmsc.openoffice.forms;

import com.finalist.cmsc.struts.MMBaseFormlessAction;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public abstract class OpenOfficeIntegrationBaseAction extends MMBaseFormlessAction {

    public static final String TEMP_PATH = "tempDir";

    protected String getBaseStoreLocation(){
        return servlet.getServletContext().getRealPath("/") + TEMP_PATH;
    }
    protected String getOdtFileStoreLocation(String base,String channelId) {
        return base + File.separator + channelId;
    }

    protected String getChannelId(HttpServletRequest request) {
        return request.getParameter("parent");
    }

    protected File[] getAllOdtFiles(String dir) {
        File[] files = new File[]{};

        File directory = new File(dir);

        if (directory.exists() && directory.isDirectory()) {
            files = directory.listFiles();
        }
        return files;

    }

}
