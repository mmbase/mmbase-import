/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Log: not supported by cvs2svn $
Revision 1.7  2001/12/14 09:33:17  pierre
pierre: Cleaning project:
added @javadoc and @rename tags, and License statement where needed

Revision 1.6  2000/06/20 15:21:03  wwwtech
Davzev: added cvs comments.

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
 * @author  $Author: pierre $
 * @version $Revision: 1.8 $
 */
public interface cacheInterface {
    /**
     * @javadoc
     */
    public void init();
    /**
     * @deprecated-now direct access to lines seems undesirable and is implementation-dependent
     */
    public LRUHashtable lines();
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
