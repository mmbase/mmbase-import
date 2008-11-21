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

public class AssetUploadAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      AssetUploadForm assetUploadForm = (AssetUploadForm) form;
      String assetType = assetUploadForm.getAssetType();
      String parentchannel = assetUploadForm.getParentchannel();
      FormFile file = assetUploadForm.getFile();

      NodeManager manager = cloud.getNodeManager(assetType);
      
      if (file.getFileSize() != 0 && file.getFileName() != null) {
         String uploadFileType = file.getContentType();
         List<Integer> nodes = null;
         if (assetType.equalsIgnoreCase("images")) {
            if (uploadFileType.equalsIgnoreCase("image/bmp") || uploadFileType.equalsIgnoreCase("image/jpeg") || uploadFileType.equalsIgnoreCase("image/gif")){
               nodes = BulkUploadUtil.store(cloud, manager, parentchannel, file);
               request.setAttribute("uploadedAssets", nodes);
            }
         }
         else if(assetType.equalsIgnoreCase("attachments")){
            nodes = BulkUploadUtil.store(cloud, manager, parentchannel, file);
            request.setAttribute("uploadedAssets", nodes);
         }
         // to archive the upload asset
         if (nodes != null && nodes.size() > 0) {
            for (Integer node : nodes) {
               Versioning.addVersion(cloud.getNode(node));
            }
         }
      }
      
      return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?type=asset&direction=down&parentchannel="
            + parentchannel, true);
   }

}
