/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security;

import java.util.*;
import java.text.*;
import org.mmbase.util.*;


/**
 * Thrown by the security classes to indicate a security violation/malfunction.
 *
 * @author Eduard Witteveen
 * @version $Id$
 */
public class SecurityException extends java.lang.SecurityException {
    private static final long serialVersionUID = -175749957183734250L;


    private String bundle;
    private String key;
    private Object[] args;

    private LocalizedString localizedMessage;

    //javadoc is inherited
    public SecurityException() {
        super();
    }

    //javadoc is inherited
    public SecurityException(String message) {
        super(message);
    }

    //javadoc is inherited
    public SecurityException(Throwable cause) {
        super(cause.getClass().getName() + ": " + cause.getMessage());
        initCause(cause);
    }

    //javadoc is inherited
    public SecurityException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    /**
     * @since MMBase-1.9.3
     */
    public SecurityException(String bundle, String key, Object... arguments) {
        super(MessageFormat.format(ResourceBundle.getBundle(bundle, Locale.getDefault()).getString(key), arguments));
        this.bundle = bundle;
        this.key = key;
        this.args = arguments;
    }

    /**
     * @since MMBase-1.9.3
     */
    public SecurityException(LocalizedString message) {
        super(message.get(null));
        localizedMessage = message;
    }



    /**
     * @since MMBase-1.9.3
     */
    public String getMessage(Locale locale) {
        if (localizedMessage != null) {
            return localizedMessage.get(locale);
        } else if (bundle != null) {
            return MessageFormat.format(ResourceBundle.getBundle(bundle, locale).getString(key), args);
        } else {
            return getMessage();
        }
    }

}
