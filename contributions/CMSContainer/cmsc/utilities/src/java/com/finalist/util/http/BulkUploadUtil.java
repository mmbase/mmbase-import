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
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;

import net.sf.mmapps.commons.util.UploadUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;

public class BulkUploadUtil {

    private static final Log log = LogFactory.getLog(BulkUploadUtil.class);

    private static final int MAXSIZE = 16 * 1024 * 1024;

    private static final String CONFIGURATION_RESOURCE_NAME = "/com/finalist/util/http/util.properties";

    private static Set<String> supportedImages;

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
            supportedImages.add(image);
        }
    }

    public static int uploadAndStore(NodeManager manager, HttpServletRequest request) {
        List<UploadUtil.BinaryData> binaries = UploadUtil.uploadFiles(request, MAXSIZE);
        int count = 0;
        for (UploadUtil.BinaryData binary : binaries) {
            if (log.isDebugEnabled()) {
                log.debug("originalFileName: " + binary.getOriginalFileName());
                log.debug("contentType: " + binary.getContentType());
            }

            if ("application/zip".equalsIgnoreCase(binary.getContentType())) {
                log.debug("unzipping content");
                try {
                    count = createNodesInZip(manager, new ZipInputStream(binary.getInputStream()));
                } catch (IOException ex) {
                    log.error("Failed to read uploaded zipfile, skipping it", ex);
                    throw new RuntimeException(ex);
                }
            } else {
                createNode(manager, binary.getOriginalFileName(), binary.getInputStream(), binary.getLength());
                count = 1;
            }
        }
        return count;
    }

    private static Node createNode(NodeManager manager, String fileName, InputStream in, long length) {
        Node node = manager.createNode();
        node.setValue("title", fileName);
        node.setValue("filename", fileName);
        node.setInputStreamValue("handle", in, length);
        node.commit();
        return node;
    }

    private static int createNodesInZip(NodeManager manager, ZipInputStream zip) throws IOException {

        ZipEntry entry = null;
        int count = 0;

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
                count++;
                // create temp file for zip entry, create a node from it and
                // remove the temp file
                File tempFile = File.createTempFile("cmsc", null);
                FileOutputStream out = new FileOutputStream(tempFile);
                copyStream(zip, out);
                zip.closeEntry();
                out.close();
                FileInputStream in = new FileInputStream(tempFile);
                createNode(manager, entry.getName(), in, tempFile.length());
                in.close();
                tempFile.delete();
            }
        } finally {
            zip.close();
        }
        return count;
    }

    private static boolean isImage(String fileName) {
        if (supportedImages == null) {
            initSupportedImages();
        }
        return fileName != null && supportedImages.contains(getExtension(fileName));
    }

    private static String getExtension(String fileName) {
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
    }
}