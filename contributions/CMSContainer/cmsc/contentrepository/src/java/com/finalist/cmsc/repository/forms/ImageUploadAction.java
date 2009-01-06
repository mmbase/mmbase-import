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

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      AssetUploadForm imageUploadForm = (AssetUploadForm) form;
      String parentchannel = imageUploadForm.getParentchannel();
      FormFile file = imageUploadForm.getFile();

      int nodeId = 0;
      String exist = "0";
      String exceed = "no";
      NodeManager manager = cloud.getNodeManager("images");
      List<Integer> nodes = null;

      if (file.getFileSize() != 0 && file.getFileName() != null) {
         if (isImage(file.getFileName())) {
            int fileSize = file.getFileSize();
            if (maxFileSizeBiggerThan(fileSize)) {
               if (isNewFile(file, manager)) {
                  nodes = BulkUploadUtil.store(cloud, manager, parentchannel, file);
                  request.setAttribute("uploadedNodes", nodes.size());
               } else {
                  exist = "1";
               }
            } else {
               exceed = "yes";
            }
         }
         // to archive the upload asset
         addRelationsForNodes(nodes, cloud);
      }
      return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?uploadAction=select&exist=" + exist
            + "&exceed=" + exceed + "&channelid=" + parentchannel + "&uploadedNodes=" + nodeId, true);
   }
}
