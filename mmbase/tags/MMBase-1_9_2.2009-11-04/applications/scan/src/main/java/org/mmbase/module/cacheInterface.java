/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import org.mmbase.util.LRUHashtable;

/**
 * The interface class for the cache module.
 *
 * @application cache [utility,interface]
 * @javadoc
 * @rename CacheInterface
 * @move org.mmbase.cache
 * @author  $Author: nklasens $
 * @version $Id$
 */
public interface cacheInterface {
    /**
     * @javadoc
     */
    public void init();
    /**
     * @deprecated-now direct access to lines seems undesirable and is implementation-dependent
     */
    public LRUHashtable<Object, Object> lines();
    /**
     * @javadoc
     */
    public boolean clear();
    /**
     * @javadoc
     */
    public cacheline get(Object key);
    /**
     * @javadoc
     */
    public cacheline put(Object key,Object value);
    /**
     * @javadoc
     */
    public cacheline remove(Object key);
}
