package com.finalist.cmsc.repository.forms;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NodeManager;

import com.finalist.util.http.BulkUploadUtil;

public class AssetUploadAction extends AbstractUploadAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      AssetUploadForm assetUploadForm = (AssetUploadForm) form;
      String parentchannel = assetUploadForm.getParentchannel();
      String insertAsset = assetUploadForm.getInsertAsset();
      FormFile file = assetUploadForm.getFile();

      String exceed = "yes";
      String exist = "1";
      String emptyFile = "no";
      String url = "";
      int nodeId = 0;
      int fileSize = file.getFileSize();

      if( fileSize == 0 || StringUtils.isEmpty(file.getFileName())){
         emptyFile = "yes";
      }
      
      if (maxFileSizeBiggerThan(fileSize)&&emptyFile.equalsIgnoreCase("no")) {
         exceed = "no";
         String assetType = "";
         if (isImage(file.getFileName())) {
            assetType = "images";
         } else {
            assetType = "attachments";
         }

         NodeManager manager = cloud.getNodeManager(assetType);

         if (isNewFile(file, manager)) {
            exist = "0";
            List<Integer> nodes = null;
            nodes = BulkUploadUtil.store(cloud, manager, parentchannel, file);
            request.setAttribute("uploadedAssets", nodes);
            // to archive the upload asset
            if (nodes != null) {
               addRelationsForNodes(nodes, cloud);
               nodeId = nodes.get(0);
            }
         }
      }
      if(emptyFile.equalsIgnoreCase("no")){
         if (StringUtils.isNotEmpty(insertAsset)) {
            if (insertAsset.equalsIgnoreCase("insertAsset")) {
               url = mapping.findForward("insertAsset").getPath() + "&uploadAction=select&exist=" + exist + "&exceed="
                     + exceed + "&parentchannel=" + parentchannel + "&uploadedNodes=" + nodeId;
            }
         } else {
            url = mapping.findForward(SUCCESS).getPath() + "?type=asset&direction=down&exist=" + exist + "&exceed="
                  + exceed + "&parentchannel=" + parentchannel;
         }
      }else{
         if (StringUtils.isNotEmpty(insertAsset)) {
            if (insertAsset.equalsIgnoreCase("insertAsset")) {
               url = mapping.findForward("insertAsset").getPath() + "&uploadAction=select" +
                  "&parentchannel=" + parentchannel + "&uploadedNodes=" + nodeId+"&emptyFile="+ emptyFile;
            }
         } else {
            url = mapping.findForward(SUCCESS).getPath() + "?type=asset&direction=down" +
               "&parentchannel=" + parentchannel+"&emptyFile="+ emptyFile;
         }
      }
      
      
      return new ActionForward(url, true);
   }
}
