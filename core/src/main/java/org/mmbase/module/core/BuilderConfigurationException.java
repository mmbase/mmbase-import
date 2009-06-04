/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * Builder configuration exception.
 * This exception is thrown when there is a (unrecoverable) foault in teh configuration
 * of the builder file, i.e. a required builder file does not exist, a core builder is
 * inactive, circularity is detected between two builders, etc.
 *
 * @since MMBase-1.6
 * @author Pierre van Rooden
 * @version $Id$
 */
public class BuilderConfigurationException extends RuntimeException {

    //javadoc is inherited
    public BuilderConfigurationException() {
        super();
    }

    //javadoc is inherited
    public BuilderConfigurationException(String message) {
        super(message);
    }

    //javadoc is inherited
    public BuilderConfigurationException(Throwable cause) {
        super(cause);
    }

    //javadoc is inherited
    public BuilderConfigurationException(String message, Throwable cause) {
        super(message,cause);
    }

}

