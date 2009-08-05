/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.editwizard.util;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.*;
import org.mmbase.applications.editwizard.WizardException;
import org.mmbase.applications.editwizard.data.BinaryData;
import org.mmbase.applications.editwizard.data.FieldData;
import org.mmbase.applications.editwizard.session.WizardWorkSpace;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


public class UploadUtil {

    /** MMbase logging system */
    private static Logger log = Logging.getLoggerInstance(UploadUtil.class.getName());

    public static String uploadFiles(HttpServletRequest request, WizardWorkSpace workSpace, int maxsize) throws WizardException {
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
            List fileItems = fu.parseRequest(request);
            int fileCount = 0;
            for (Iterator i = fileItems.iterator(); i.hasNext(); ) {
                FileItem fi = (FileItem)i.next();
                if (!fi.isFormField()) {
                    String fullFileName = fi.getName();
                    if (fi.get().length > 0) { // no need uploading nothing
                      BinaryData binaryData = new BinaryData();
                      binaryData.setData(fi.get());
                      binaryData.setOriginalFilePath(fullFileName);
                      binaryData.setContentType(fi.getContentType());
                      if (log.isDebugEnabled()) {
                          log.debug("Setting binary " + binaryData.getLength() +" bytes in type "
                                  + binaryData.getContentType()+" with "
                                  + binaryData.getOriginalFilePath() + " name");
                       }
                      String did = fi.getFieldName();
                      workSpace.storeBinaryField(did, binaryData);
                      fileCount++;
                    }
                }
            }
            return "Uploaded files:" + fileCount;
        } catch (FileUploadBase.SizeLimitExceededException e) {
            String errorMessage = "Uploaded file exceeds maximum file size of "+maxsize+" bytes.";
            log.warn(errorMessage,e);
            throw new WizardException(errorMessage, e);
        } catch (FileUploadException e) {
            String errorMessage = "An error ocurred while uploading this file "+e.toString();
            log.warn(errorMessage,e);
            throw new WizardException(errorMessage, e);
        }
    }

}
