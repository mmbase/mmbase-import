/*
 * 
 * This software is OSI Certified Open Source Software. OSI Certified is a certification mark of the Open Source
 * Initiative.
 * 
 * The license (Mozilla version 1.0) can be read at the MMBase site. See http://www.MMBase.org/license
 */
package com.finalist.cmsc.repository.forms;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.transformers.ByteToCharTransformer;
import org.mmbase.util.transformers.ChecksumFactory;

import com.finalist.cmsc.services.versioning.Versioning;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.util.http.BulkUploadUtil;

public class ImageUploadAction extends MMBaseAction {

   @Override
   public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception {

      ImageUploadForm imageUploadForm = (ImageUploadForm) form;
      String parentchannel = imageUploadForm.getParentchannel();
      FormFile file = imageUploadForm.getFile();
      int nodeId = 0;
      String exist = "0";
      NodeManager manager = cloud.getNodeManager("images");
      List<Integer> nodes = null;

      if (file.getFileSize() != 0 && file.getFileName() != null) {
         if (isImage(file.getFileName())) {

            ChecksumFactory checksumFactory = new ChecksumFactory();
            ByteToCharTransformer transformer = (ByteToCharTransformer) checksumFactory
                  .createTransformer(checksumFactory.createParameters());
            String checkSum = transformer.transform(file.getFileData());
            NodeQuery query = manager.createQuery();
            SearchUtil.addEqualConstraint(query, manager.getField("checksum"), checkSum);
            NodeList images = query.getList();

            boolean isNewFile = (images.size() == 0);
            if (isNewFile) {
               nodes = BulkUploadUtil.store(cloud, manager, parentchannel, file);
               request.setAttribute("uploadedNodes", nodes.size());
            } else {
               exist = "1";
            }
         }

         if (nodes != null && nodes.size() > 0) {
            for (Integer node : nodes) {
               Node assetNode = cloud.getNode(node);
               if (!Workflow.hasWorkflow(assetNode)) {
                  Workflow.create(assetNode, "");
               } else {
                  Workflow.addUserToWorkflow(assetNode);
               }
               Versioning.addVersion(cloud.getNode(node));
            }
            nodeId = nodes.get(0);
         }
      }
      return new ActionForward(mapping.findForward(SUCCESS).getPath() + "?uploadAction=select&exist=" + exist
            + "&channelid=" + parentchannel + "&uploadedNodes=" + nodeId, true);
   }

   private static Set<String> supportedImages;
   private static final String CONFIGURATION_RESOURCE_NAME = "/com/finalist/util/http/util.properties";
   private static final Log log = LogFactory.getLog(BulkUploadUtil.class);

   private static void initSupportedImages() {
      supportedImages = new HashSet<String>();
      Properties properties = new Properties();
      String images = ".bmp,.jpg,.jpeg,.gif,.png,.svg,.tiff,.tif";
      try {
         properties.load(BulkUploadUtil.class.getResourceAsStream(CONFIGURATION_RESOURCE_NAME));
         images = (String) properties.get("supportedImages");
      } catch (IOException ex) {
         log.warn("Could not load properties from " + CONFIGURATION_RESOURCE_NAME + ", using defaults", ex);
      }
      for (String image : images.split(",")) {
         supportedImages.add(image.trim());
      }
   }

   private static boolean isImage(String fileName) {
      if (supportedImages == null) {
         initSupportedImages();
      }
      return fileName != null && supportedImages.contains(getExtension(fileName).toLowerCase());
   }

   private static String getExtension(String fileName) {
      int index = fileName.lastIndexOf('.');
      if (index < 0) {
         return null;
      }
      return fileName.substring(index);
   }
}
