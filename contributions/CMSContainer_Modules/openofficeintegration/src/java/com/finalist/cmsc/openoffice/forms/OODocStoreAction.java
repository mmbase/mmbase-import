package com.finalist.cmsc.openoffice.forms;

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.cmsc.openoffice.model.OdtDocument;
import com.finalist.cmsc.openoffice.service.OdtFileTranster;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public class OODocStoreAction extends OpenOfficeIntegrationBaseAction {

    private static Log log = LogFactory.getLog(OODocStoreAction.class);

    @Override
    public ActionForward execute(ActionMapping mapping, HttpServletRequest request, Cloud cloud) throws Exception {

        /*retrieve channel number*/
        String channelId = getChannelId(request);
        String odtStoreLocation = getOdtFileStoreLocation(getBaseStoreLocation(),channelId);

        if (StringUtils.isEmpty(channelId)) {
            channelId = RepositoryUtil.getRoot(cloud);
        }

        int nodenumber = store(cloud, odtStoreLocation, channelId);

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
    @SuppressWarnings("unchecked")
    public int store(Cloud cloud, String odtStoreLocation, String channelId) {

        OdtFileTranster.WORKINGFOLDER = getBaseStoreLocation()+ File.separator + "work";

        NodeManager manager = cloud.getNodeManager("article");

//		WorkflowService service = new WorkflowServiceMMBaseImpl();

        int firstNodeId = 0;

        File[] files = getAllOdtFiles(odtStoreLocation);
        for (File file : files) {
            OdtDocument doc = OdtFileTranster.process(file);

            Node node = manager.createNode();
            node.setValue("title", doc.getTitle());
            node.setValue("body", doc.getBody());

            node.commit();
            RepositoryUtil.addContentToChannel(node, channelId);
            // RepositoryUtil.addCreationChannel(node, channel);
            // RepositoryUtil.addDeletionRelation(node, channel);
//			addRelToWorkFlow(service, node);
            firstNodeId = node.getNumber();
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
