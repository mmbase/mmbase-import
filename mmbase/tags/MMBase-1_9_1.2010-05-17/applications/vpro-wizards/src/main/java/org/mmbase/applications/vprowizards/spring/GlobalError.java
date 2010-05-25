/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.builder.ToStringBuilder;

public class GlobalError {

    /**
     * This is the type of error that is created when something went wrong, and the transaction can not be committed in
     * the end. A global error is a kind of error that will return an error page, in stead of the referrer page.
     * 
     * @author Ernst Bunders
     */
    private static ResourceBundle bundle = null;
    private String messageKey = "";
    private String[] properties = null;
    public static final String MODEL_MAPPING_KEY="globalErrors";

    /**
     * @param messageKey
     *            the key of the error message in the resourceBundle with messages
     */
    public GlobalError(String messageKey, Locale locale) {
        this.messageKey = messageKey;
        initBundle(locale);
    }

    /**
     * Use this constructor if the message is a template that contains certain placeholders to be replaced.
     * 
     * @param messageKey
     * @param properties
     */
    public GlobalError(String messageKey, String[] properties, Locale locale) {
        this.messageKey = messageKey;
        this.properties = properties;
        initBundle(locale);
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String[] getProperties() {
        return properties;
    }

    /**
     * @return the message as defined in the messages resource bundle.
     * @throws RuntimeException
     *             when the key was not found in the bundle
     */
    public String getMessage() {
        String message = bundle.getString(messageKey);
        if (message == null || "".equals(message)) {
            throw new RuntimeException("no message declared in bundle for key '" + messageKey + "'");
        }
        int count = 0;
        if (properties != null) {
            while (message.contains("${" + count + "}") && properties.length > count) {
                message = message.replace("${" + count + "}", properties[count]);
                count++;
            }
        }
        return message;

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    private void initBundle(Locale locale) {
        if (locale == null) {
            throw new IllegalStateException("Locale should not be null");
        }
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("org.mmbase.applications.vprowizards.resources.messages", locale);
        }
    }
}
