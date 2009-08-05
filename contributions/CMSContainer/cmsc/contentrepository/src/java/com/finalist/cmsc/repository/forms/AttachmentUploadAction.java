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

import com.finalist.cmsc.repository.RepositoryUtil;
import com.finalist.util.http.BulkUploadUtil;

public class AttachmentUploadAction extends AbstractUploadAction {
   
   private static final String ALL = "all";
   private static final String SITEASSETS = "siteassets";
   private static final String SESSION_CREATION = "creation";
   
   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      AssetUploadForm attachmentUploadForm = (AssetUploadForm) form;
      String parentchannel = attachmentUploadForm.getParentchannel();
      FormFile file = attachmentUploadForm.getFile();
      String strict = attachmentUploadForm.getStrict();

      String exist = "1";
      String exceed = "yes";
      int nodeId = 0;

      if (parentchannel.equalsIgnoreCase(SITEASSETS)) {
         parentchannel = RepositoryUtil.getRoot(cloud);
      } else if (parentchannel.equalsIgnoreCase(ALL) || StringUtils.isEmpty(parentchannel)) {
         parentchannel = (String) request.getSession().getAttribute(SESSION_CREATION);
      }
      int fileSize = file.getFileSize();
      if (maxFileSizeBiggerThan(fileSize)) {
         NodeManager manager = cloud.getNodeManager("attachments");
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
      return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?uploadAction=select&strict=" + strict + "&exist=" + exist
            + "&exceed=" + exceed + "&channelid=" + parentchannel + "&uploadedNodes=" + nodeId, true);
   }
}
