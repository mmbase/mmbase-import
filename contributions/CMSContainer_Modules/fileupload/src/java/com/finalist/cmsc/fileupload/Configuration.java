package com.finalist.cmsc.fileupload;

import java.io.File;

import org.apache.struts.action.ActionMessage;

import com.finalist.cmsc.mmbase.PropertiesUtil;

import java.util.List;

/**
 * Provides access to the configuration properties. This can also trigger
 * {@link ConfigurationException} to be thrown if the configuration was not deemed valid.
 * 
 * @author Auke van Leeuwen
 */
public class Configuration {

    private static final String STORAGEPATH_PROP_NAME = "fileupload.storepath";

    private static final String URLPREFIX_PROP_NAME = "fileupload.urlprefix";

    private static final String ALLOWED_MIMETYPES_PROP_NAME = "fileupload.allowedmimetypes";

    /**
     * Returns the storage path that is stored as a property for this module. If the property - or
     * the folder the property points - does not exist it will thrown an
     * {@link ConfigurationException}. Since this property should denote a directory it will always
     * be appended with a {@value File#separatorChar} if it didn't end with one in the first place.
     * 
     * @return the value stored in the property for the url prefix.
     */
    public static String getStoragePath() {
        String storePath = PropertiesUtil.getProperty(STORAGEPATH_PROP_NAME);

        if (storePath == null) {
            ActionMessage message = new ActionMessage("fileupload.error.config.noproperty", STORAGEPATH_PROP_NAME);
            throw new ConfigurationException(message);
        }

        if (!storePath.endsWith(File.separator)) {
            storePath = (storePath + File.separator);
        }

        // quick check for existence
        File file = new File(storePath);

        if (!file.isDirectory()) {
            ActionMessage message = new ActionMessage("fileupload.error.config.notfound", storePath);
            throw new ConfigurationException(message);
        }

        return storePath;
    }

    /**
     * Returns the url prefix that is stored as a property for this module. If the property does not
     * exist, it will thrown an {@link ConfigurationException}.
     * 
     * @return the value stored in the property for the url prefix.
     */
    public static String getUrlPrefix() {
        String urlPrefix = PropertiesUtil.getProperty(URLPREFIX_PROP_NAME);

        if (urlPrefix == null) {
            ActionMessage message = new ActionMessage("fileupload.error.config.noproperty", URLPREFIX_PROP_NAME);
            throw new ConfigurationException(message);
        }

        return urlPrefix;
    }

    /**
     * Returns the url prefix that is stored as a property for this module. If the property does not
     * exist, it will thrown an {@link ConfigurationException}.
     * 
     * @return the value stored in the property for the url prefix.
     */
    public static List<String> getAllowedMimeTypes() {
		String allowedMimeTypes = PropertiesUtil.getProperty(ALLOWED_MIMETYPES_PROP_NAME);		

		if (allowedMimeTypes == null) {
			ActionMessage message = new ActionMessage("fileupload.error.config.noproperty", ALLOWED_MIMETYPES_PROP_NAME);
			throw new ConfigurationException(message);
		}
		
		String[] allowedMimeTypesArray = allowedMimeTypes.split(",");
		java.util.List<String> allowedMimeTypesList = java.util.Arrays.asList(allowedMimeTypesArray);
		return allowedMimeTypesList;
	}

    /**
     * Thrown when a configuration exception occurs, constructed with an ActionMessage for a
     * localized message.
     * 
     * @author Auke van Leeuwen
     */
    @SuppressWarnings("serial")
    public static class ConfigurationException extends RuntimeException {

        private ActionMessage actionMessage;

        /**
         * Construct a new ConfigurationException based on the given actionmessage and cause.
         * 
         * @param actionMessage
         *            the action message
         */
        public ConfigurationException(ActionMessage actionMessage) {
            this.actionMessage = actionMessage;
        }

        /**
         * Returns the action message.
         * 
         * @return the action message.
         */
        public ActionMessage getActionMessage() {
            return actionMessage;
        }
    }
}