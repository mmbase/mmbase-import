/*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

 */
package com.finalist.util.http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

import com.finalist.cmsc.util.UploadUtil;
import com.finalist.cmsc.util.UploadUtil.BinaryData;

public class BulkUploadUtil {

   private static final Log log = LogFactory.getLog(BulkUploadUtil.class);

   private static final int MAXSIZE = 256 * 1024 * 1024;

   private static final String CONFIGURATION_RESOURCE_NAME = "/com/finalist/util/http/util.properties";

   private static final String ZIP_MIME_TYPES[] = new String[] { "application/x-zip-compressed", "application/zip", "application/x-zip" };

   private static Set<String> supportedImages;


   private static void initSupportedImages() {
      supportedImages = new HashSet<String>();
      Properties properties = new Properties();
      String images = ".bmp,.jpg,.jpeg,.gif,.png,.svg,.tiff,.tif";
      try {
         properties.load(BulkUploadUtil.class.getResourceAsStream(CONFIGURATION_RESOURCE_NAME));
         images = (String) properties.get("supportedImages");
      }
      catch (IOException ex) {
         log.warn("Could not load properties from " + CONFIGURATION_RESOURCE_NAME + ", using defaults", ex);
      }
      for (String image : images.split(",")) {
         supportedImages.add(image.trim());
      }
   }


   public static String convertToCommaSeparated(List<?> values) {
      StringBuffer buffer = new StringBuffer();
      for (Object obj : values) {
         if (obj != null) {
            buffer.append(obj.toString());
            buffer.append(",");
         }
      }
      return buffer.toString();
   }


   public static List<Integer> uploadAndStore(NodeManager manager, HttpServletRequest request) {
      List<UploadUtil.BinaryData> binaries = UploadUtil.uploadFiles(request, MAXSIZE);
      ArrayList<Integer> nodes = new ArrayList<Integer>();

      for (UploadUtil.BinaryData binary : binaries) {
         if (log.isDebugEnabled()) {
            log.debug("originalFileName: " + binary.getOriginalFileName());
            log.debug("contentType: " + binary.getContentType());
         }

         if (isZipFile(binary.getContentType(), binary.getOriginalFileName())) {
            log.debug("unzipping content");
            nodes.addAll(createNodesInZip(manager, new ZipInputStream(binary.getInputStream())));
         }
         else {
            Node node = createNode(manager, binary.getOriginalFileName(), binary.getInputStream(), binary.getLength());
            if(node != null) {
            	nodes.add(node.getNumber());
            }
         }
      }
      return nodes;
   }


   public static boolean isZipFile(String contentType, String fileName) {

      for (String element : ZIP_MIME_TYPES) {
         if (element.equalsIgnoreCase(contentType)) {
            return true;
         }
      }
      
      //Sometimes browsers don't return a nice mime-type (for example application/octet-stream)
      //So checking on extension might be a good idea too.
      if (getExtension(fileName).equalsIgnoreCase(".zip")) {
         return true;
      }
      
      return false;
   }


   private static Node createNode(NodeManager manager, String fileName, InputStream in, long length) {
	   if(length > manager.getField("handle").getMaxLength()) {
		   return null;
	   }
      Node node = manager.createNode();
      node.setValue("title", fileName);
      node.setValue("filename", fileName);
      node.setInputStreamValue("handle", in, length);
      node.commit();
      
      return node;
   }


   private static ArrayList<Integer> createNodesInZip(NodeManager manager, ZipInputStream zip) {

      ZipEntry entry = null;
      int count = 0;
      ArrayList<Integer> nodes = new ArrayList<Integer>();

      try {
         while ((entry = zip.getNextEntry()) != null) {
            if (entry.isDirectory()) {
               continue;
            }
            if ("images".equals(manager.getName()) && !isImage(entry.getName())) {
               if (log.isDebugEnabled()) {
                  log.debug("Skipping " + entry.getName() + " because it is not an image");
               }
               continue;
            }
            if (log.isDebugEnabled()) {
               log.debug("reading file (from ZIP): '" + entry.getName() + "'");
            }
            count++;
            // create temp file for zip entry, create a node from it and
            // remove the temp file
            File tempFile = File.createTempFile("cmsc", null);
            FileOutputStream out = new FileOutputStream(tempFile);
            copyStream(zip, out);
            zip.closeEntry();
            out.close();
            FileInputStream in = new FileInputStream(tempFile);
            Node node = createNode(manager, entry.getName(), in, tempFile.length());
            if(node != null) {
            	nodes.add(node.getNumber());
            }
            in.close();
            tempFile.delete();
         }

      }
      catch (IOException ex) {
         log.error("Failed to read uploaded zipfile, skipping it", ex);
      }
      finally {
         close(zip);
      }
      return nodes;
   }


   private static void close(InputStream stream) {
      try {
         stream.close();
      }
      catch (IOException ignored) {
         // ignored
      }
   }


   public static boolean isImage(String fileName) {
      if (StringUtils.isBlank(fileName)) {
         return false;
      }
      if (supportedImages == null) {
         initSupportedImages();
      }
      
      return supportedImages.contains(getExtension(fileName).toLowerCase());
   }


   public static String getExtension(String fileName) {
      if (StringUtils.isBlank(fileName)) {
         return null;
      }
      int index = fileName.lastIndexOf('.');
      if (index < 0) {
         return null;
      }
      return fileName.substring(index);
   }


   private static void copyStream(InputStream ins, OutputStream outs) throws IOException {
      int bufferSize = 1024;
      byte[] writeBuffer = new byte[bufferSize];

      BufferedOutputStream bos = new BufferedOutputStream(outs, bufferSize);
      int bufferRead;
      while ((bufferRead = ins.read(writeBuffer)) != -1)
         bos.write(writeBuffer, 0, bufferRead);
      bos.flush();
      bos.close();
      outs.flush();
      outs.close();
   }


   public static void main(String[] args) {

      System.out.println(isImage(getExtension("test.jpg")));
      System.out.println(isImage(getExtension(".jpg")));
      System.out.println(isImage(getExtension("test")));
      System.out.println(isImage(getExtension("test.")));
      System.out.println(isImage(getExtension("")));
      System.out.println(isImage(getExtension("test.jpeg")));
      System.out.println(isImage(getExtension("test.gif")));
      System.out.println(isImage(getExtension("test.txt")));
      System.out.println(isImage(getExtension("test.bummer")));
      System.out.println(isImage(""));
      System.out.println(isImage(" "));

      //Also test the isZipFile method
      //Also test the isZipFile method
      System.out.println(isZipFile("content","helloworld.zip")); //Should be true
      System.out.println(isZipFile("application/x-zip-compressed","helloworld.zipper")); //Should be true
      System.out.println(isZipFile("content","helloworld.zipper")); //Should be false
  }
}