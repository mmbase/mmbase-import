/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
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

import com.finalist.cmsc.services.versioning.Versioning;
import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.util.http.BulkUploadUtil;

public class ImageUploadAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      ImageUploadForm imageUploadForm = (ImageUploadForm) form;
      String parentchannel = imageUploadForm.getParentchannel();
      FormFile file = imageUploadForm.getFile();

      NodeManager manager = cloud.getNodeManager("images");

      if (file.getFileSize() != 0 && file.getFileName() != null) {
         String uploadFileType = file.getContentType();
         List<Integer> nodes = null;
         if (uploadFileType.equalsIgnoreCase("image/bmp") || uploadFileType.equalsIgnoreCase("image/jpeg")
               || uploadFileType.equalsIgnoreCase("image/gif") || uploadFileType.equalsIgnoreCase("image/png")) {
            nodes = BulkUploadUtil.store(cloud, manager, parentchannel, file);
            request.setAttribute("uploadedImages", nodes);
         }
         // to archive the upload asset
         if (nodes != null && nodes.size() > 0) {
            for (Integer node : nodes) {
               Versioning.addVersion(cloud.getNode(node));
            }
         }
      }
      return new ActionForward(mapping.findForward(SUCCESS).getPath(), true);
   }

}
