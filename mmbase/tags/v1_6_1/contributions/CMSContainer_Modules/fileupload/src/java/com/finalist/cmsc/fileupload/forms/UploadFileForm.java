package com.finalist.cmsc.fileupload.forms;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;

import com.finalist.cmsc.fileupload.Configuration;

/**
 * Struts form-bean for the file upload.
 * 
 * @author Auke van Leeuwen
 */
@SuppressWarnings("serial")
public class UploadFileForm extends ActionForm {

    private static final Log log = LogFactory.getLog(UploadFileForm.class);

    private String title;

    private String description;

    private FormFile file;

    /** {@inheritDoc} */
    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        String title = getTitle();
        if (StringUtils.isBlank(title)) {
            errors.add("title", new ActionMessage("fileupload.upload.error.title.empty"));
        }
        else
            if (title != null && title.length() > 255) {
                errors.add("title", new ActionMessage("fileupload.upload.error.title.toolong"));
            }

        String description = getDescription();
        if (description != null && description.length() > 2048) {
            errors.add("description", new ActionMessage("fileupload.upload.error.description.toolong"));
        }

        FormFile file = getFile();
        if ((file == null) || (file.getFileSize() == 0)) {
            errors.add("file", new ActionMessage("fileupload.upload.error.file.empty"));
        }
        else
            if (!isValidFileType(file)) {
                errors.add("file", new ActionMessage("fileupload.upload.error.file.invalidtype"));
            }

        return errors;
    }

    private boolean isValidFileType(FormFile file) {
        List<String> allowedFileTypes = Configuration.getAllowedMimeTypes();
        List<String> allowedExtensions = Arrays.asList(".flv");
        String contentType = file.getContentType();
        log.debug("file contentType: " + contentType);

        // contenttype checking
        boolean isAllowed = allowedFileTypes.contains(contentType);

        // unfortunately I can't figure out how to correclty configure the flv
        // mimetype, so I'll do an extension comparison here.
        String fileName = file.getFileName();
        for (String extension : allowedExtensions) {
            isAllowed = isAllowed || fileName.endsWith(extension);
        }

        return isAllowed;
    }

    /**
     * Returns the title.
     * 
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title to the specified value.
     * 
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description to the specified value.
     * 
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the file.
     * 
     * @return the file
     */
    public FormFile getFile() {
        return file;
    }

    /**
     * Sets the file to the specified value.
     * 
     * @param file
     *            the file to set
     */
    public void setFile(FormFile file) {
        this.file = file;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        FormFile file = getFile();
        String fileFilename = (file == null ? null : file.getFileName());

        return String.format("%s: [title: '%s', filename: '%s']", this.getClass(), getTitle(), fileFilename);
    }
}
