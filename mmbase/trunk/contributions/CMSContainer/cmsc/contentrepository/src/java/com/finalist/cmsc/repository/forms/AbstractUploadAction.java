package com.finalist.cmsc.repository.forms;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import org.mmbase.bridge.NotFoundException;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.util.transformers.ByteToCharTransformer;
import org.mmbase.util.transformers.ChecksumFactory;

import com.finalist.cmsc.mmbase.PropertiesUtil;
import com.finalist.cmsc.services.versioning.Versioning;
import com.finalist.cmsc.services.versioning.VersioningException;
import com.finalist.cmsc.services.workflow.Workflow;
import com.finalist.cmsc.struts.MMBaseAction;
import com.finalist.util.http.BulkUploadUtil;

public abstract class AbstractUploadAction extends MMBaseAction {

   public static final String UPLOADED_FILE_MAX_SIZE = "uploaded.file.max.size";
   public static final String CONFIGURATION_RESOURCE_NAME = "/com/finalist/util/http/util.properties";
   protected static Set<String> supportedImages;

   protected static final Log log = LogFactory.getLog(AbstractUploadAction.class);

   @Override
   public abstract ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
         HttpServletResponse response, Cloud cloud) throws Exception;

   protected static void initSupportedImages() {
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

   protected static boolean isImage(String fileName) {
      if (StringUtils.isBlank(fileName)) {
         return false;
      }
      if (supportedImages == null) {
         initSupportedImages();
      }
      return supportedImages.contains(getFilenameExtension(fileName).toLowerCase());
   }

   protected static String getFilenameExtension(String fileName) {
      if (StringUtils.isBlank(fileName)) {
         return null;
      }
      int index = fileName.lastIndexOf('.');
      if (index < 0) {
         return null;
      }
      return fileName.substring(index);
   }

   public boolean isNewFile(FormFile file, NodeManager manager) {
      ChecksumFactory checksumFactory = new ChecksumFactory();
      ByteToCharTransformer transformer = (ByteToCharTransformer) checksumFactory.createTransformer(checksumFactory
            .createParameters());
      String checkSum = null;
      try {
         checkSum = transformer.transform(file.getFileData());
      } catch (FileNotFoundException e) {
         log.warn("Uploading file is not found!");
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      NodeQuery query = manager.createQuery();
      SearchUtil.addEqualConstraint(query, manager.getField("checksum"), checkSum);
      NodeList assets = query.getList();
      return (assets.size() == 0);
   }

   public void addRelationsForNodes(List<Integer> nodes, Cloud cloud) throws NotFoundException, VersioningException {
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
      }
   }

   public boolean maxFileSizeBiggerThan(int fileSize) {
      int maxFileSize = 16 * 1024 * 1024; // Default value of 16MB
      try {
         maxFileSize = Integer.parseInt(PropertiesUtil.getProperty(UPLOADED_FILE_MAX_SIZE)) * 1024 * 1024;
      } catch (NumberFormatException e) {
         log.warn("System property '" + UPLOADED_FILE_MAX_SIZE + "' is not set. Please add it (units = MB).");
      }
      return (fileSize < maxFileSize);
   }
}
