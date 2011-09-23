/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;


/**
 * See http://java.sun.com/docs/books/tutorial/jmx/mbeans/standard.html
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */
public interface CacheManagerMBean {

    String clear(String regex);
    String disable(String regex);
    String enable(String regex);
    String readConfiguration();
}
