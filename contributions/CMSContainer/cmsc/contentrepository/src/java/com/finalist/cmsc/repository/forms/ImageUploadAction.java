package com.finalist.cmsc.repository.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;

import com.finalist.util.http.BulkUploadUtil;

public class ImageUploadAction extends AbstractUploadAction {

   private static final String ALL = "all";
   private static final String CREATION = "creation";

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      AssetUploadForm imageUploadForm = (AssetUploadForm) form;
      String parentchannel = imageUploadForm.getParentchannel();
      FormFile file = imageUploadForm.getFile();

      String exist = "1";
      String exceed = "yes";
      int nodeId = 0;

      if (parentchannel.equalsIgnoreCase(ALL)) {
         parentchannel = (String) request.getSession().getAttribute(CREATION);
      }
      if (isImage(file.getFileName())) {
         int fileSize = file.getFileSize();
         if (maxFileSizeBiggerThan(fileSize)) {
            NodeManager manager = cloud.getNodeManager("images");
            exceed = "no";
            if (isNewFile(file, manager)) {
               exist = "0";
               List<Integer> nodes = null;
               nodes = BulkUploadUtil.store(cloud, manager, parentchannel, file);
               // to archive the upload asset
               if (nodes != null) {
                  addRelationsForNodes(nodes, cloud);
                  nodeId = nodes.get(0);
               }
            } else {
               exist = "1";
            }
         } else {
            exist = "0";
         }
      }
      return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?uploadAction=select&exist=" + exist
            + "&exceed=" + exceed + "&channelid=" + parentchannel + "&uploadedNodes=" + nodeId, true);
   }
}
