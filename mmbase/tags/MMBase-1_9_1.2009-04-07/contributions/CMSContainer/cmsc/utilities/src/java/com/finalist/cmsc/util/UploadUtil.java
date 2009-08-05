/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package com.finalist.cmsc.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


public class UploadUtil {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(UploadUtil.class.getName());
    
    /**
     * Process files which are uploaded through http
     * 
     * @param request - http request
     * @param maxsize - maximum allowed size of a file
     * @return List of UploadUtil.BinaryData objects
     */
    public static List<BinaryData> uploadFiles(HttpServletRequest request, int maxsize) {
        // Initialization
        DiskFileUpload fu = new DiskFileUpload();
        // maximum size before a FileUploadException will be thrown
        fu.setSizeMax(maxsize);

        // maximum size that will be stored in memory --- what should this be?
        // fu.setSizeThreshold(maxsize);

        // the location for saving data that is larger than getSizeThreshold()
        // where to store?
        // fu.setRepositoryPath("/tmp");

        // Upload
        try {
            List<BinaryData> binaries = new ArrayList<BinaryData>();
            List<FileItem> fileItems = fu.parseRequest(request);

            for (FileItem fi : fileItems) {
                if (!fi.isFormField()) {
                    String fullFileName = fi.getName();
                    if (fi.get().length > 0) { // no need uploading nothing
                        BinaryData binaryData = new BinaryData();
                        binaryData.setData(fi.get());
                        binaryData.setOriginalFilePath(fullFileName);
                        binaryData.setContentType(fi.getContentType());
                        if (log.isDebugEnabled()) {
                            log.debug("Setting binary " + binaryData.getLength()
                                    + " bytes in type " + binaryData.getContentType() + " with "
                                    + binaryData.getOriginalFilePath() + " name");
                        }
                        binaries.add(binaryData);
                    }
                }
            }
            return binaries;
        }
        catch (FileUploadBase.SizeLimitExceededException e) {
            String errorMessage = "Uploaded file exceeds maximum file size of " + maxsize + " bytes.";
            log.warn(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
        catch (FileUploadException e) {
            String errorMessage = "An error ocurred while uploading this file " + e.toString();
            log.warn(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }
    
    
    public static class BinaryData {
        
        private String originalFilePath = null;
        private int length = 0;
        private byte[] data = null;
        private String contentType = null;
        
        /**
         * set the binary data into this class to cache.
         * @param data
         */
        public void setData(byte[] data) {
            if (data == null) {
                this.length=0;
            }
            else {
                this.data = data;
                this.length = this.data.length;
            }
        }
        
        /**
         * get the binary data cached by this class
         * @return binary data
         */
        public byte[] getData() {
            if (this.length==0) {
                return new byte[0];
            }
            return data;
        }
        
        /**
         * get original file name, it is the path in the client in which upload the file.
         * @return Returns the originalFilePath.
         */
        public String getOriginalFilePath() {
            return originalFilePath;
        }

        /**
         * set original file name, it is the path in the client in which upload the file.
         * @param originalFilePath The originalFilePath to set.
         */
        public void setOriginalFilePath(String originalFilePath) {
            this.originalFilePath = originalFilePath;
        }
        
        /**
         * get original file name. the return value only contains the file name, 
         * the directory path is not include in return value.
         * @return original file name
         */
        public String getOriginalFileName() {
            if (originalFilePath==null) {
                return null;
            }
            // the path passed is in the client system's format,
            // so test all known path separator chars ('/', '\' and "::" )
            // and pick the one which would create the smallest filename
            // Using Math is rather ugly but at least it is shorter and performs better
            // than Stringtokenizer, regexp, or sorting collections
            int last = Math.max(Math.max(
                    originalFilePath.lastIndexOf(':'), // old mac path (::)
                    originalFilePath.lastIndexOf('/')),  // unix path
                    originalFilePath.lastIndexOf('\\')); // windows path
            if (last > -1) {
                return originalFilePath.substring(last+1);
            }
            return originalFilePath;
        }

        public int getLength() {
            return length;
        }
        
        public String getContentType() {
            return contentType;
        }
        
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
        
        public InputStream getInputStream() {
            return new ByteArrayInputStream(getData());
        }
    }

}
