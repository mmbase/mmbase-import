/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

/**
 * @javadoc
 *
 * @author Ernst Bunders
 * @since MMBase-1.8
 * @version $Id: CacheConfigurationException.java,v 1.3 2006-06-06 21:58:47 michiel Exp $
 */
public class CacheConfigurationException extends Exception {


    /**
     * @param string
     */
    public CacheConfigurationException(String string) {
        super(string);
    }

}
