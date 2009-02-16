package com.finalist.cmsc.openoffice.forms;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.openoffice.model.OdtDocument;
import com.finalist.cmsc.openoffice.service.OdtFileTransfer;
import com.finalist.cmsc.openoffice.service.OODocUploadUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

public class OODocStoreAction extends OpenOfficeIntegrationBaseAction {

    @Override
    public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

        /*retrieve channel number*/
        String channelId = getChannelId(request);
        
        if(StringUtils.isEmpty(channelId) && request.getParameter("root") != null) {
        	channelId = OODocUploadUtil.SINGLE_FILE_PATH;
        	
        }

        	String odtStoreLocation = getOdtFileStoreLocation(getBaseStoreLocation(),channelId);

        if (StringUtils.isEmpty(channelId)) {
            channelId = RepositoryUtil.getRoot(cloud);
        }
        
        if(request.getParameter("root") != null) {
        	channelId = request.getParameter("root");        	
        }
        String requestContext = request.getScheme()+"://"+request.getServerName()+":"
        +request.getServerPort()+request.getContextPath()+"/mmbase/images/";
        int nodenumber = store(cloud, odtStoreLocation, channelId,requestContext);

        if (-1 != nodenumber) {
            //there is only one document exist. 
            return new ActionForward(mapping.findForward("edit").getPath() + "?objectnumber=" + nodenumber);
        } else {
            return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?parentchannel=" + channelId + "&direction=down");
        }
    }

    /**
     * storage the documents of odt
     */
    public int store(Cloud cloud, String odtStoreLocation, String channelId,String requestContext) {

        OdtFileTransfer.WORKINGFOLDER = getBaseStoreLocation()+ File.separator + "work";

        NodeManager manager = cloud.getNodeManager("article");

//		WorkflowService service = new WorkflowServiceMMBaseImpl();

        int firstNodeId = 0;

        File[] files = getAllOdtFiles(odtStoreLocation);
        for (File file : files) {
        	
            OdtDocument doc;
            try {
               doc = OdtFileTransfer.process(file,requestContext);
               Node node = manager.createNode();
               node.setValue("title", doc.getTitle());
               node.setValue("body", doc.getBody());
               node.commit();
               RepositoryUtil.addContentToChannel(node, channelId);
               // RepositoryUtil.addCreationChannel(node, channel);
               // RepositoryUtil.addDeletionRelation(node, channel);
               //			addRelToWorkFlow(service, node);
               firstNodeId = node.getNumber();
            } catch (IOException e) {
               //Could not save file
               e.printStackTrace();
            }
        }

        clearupFinishedFiles(odtStoreLocation);

        return files.length > 1 ? -1 : firstNodeId;
    }

    private void clearupFinishedFiles(String odtStoreLocation) {
        for (File file : getAllOdtFiles(odtStoreLocation)) {
            file.delete();
        }
    }
}
