/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import javax.servlet.http.HttpServletResponse;

import org.mmbase.util.scanpage;

/**
 * File cache system interface.
 * System for caching texts (it stores and retrieves strings) by use of a key.
 * While in theory it is possible to cache ANY text, this is mainly used to store pages
 * based on their url.<br />
 * Caching is done in pools. Each pool has its own memory cache and files, and has
 * different ways to handle file caching.
 *
 * @application SCAN
 * @rename SCANCacheInterface
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id$
 */
public interface scancacheInterface {
    /**
     * Initializes the module.
     * Also defined in ModuleInterface, so why is it in here?
     */
    public void init();

    /**
     * Retrieve a file from the indicated pool's cache.
     * @param pool name of the cache pool
     * @param key key under which the content was cached
     * @return the cached content as a string, or <code>null</code> if no entry was found
     *     (i.e. cache was empty or poolname was invalid).
     */
    public String get(String pool,String key,scanpage sp);

    /**
     * Retrieve a file from the indicated pool's cache.
     * @param pool name of the cache pool
     * @param key key under which the content was cached
     * @param line options for retrieving, such as an expiration value
     * @return the cached content as a string, or <code>null</code> if no entry was found
     *     (i.e. cache was empty or poolname was invalid).
     */
    public String get(String pool,String key,String line,scanpage sp);

        /**
         *  getExpireDate.
         * @param poolName
         * @param key
         * @param expireStr
         * @return long
         */
        public long getExpireDate(String poolName, String key, String expireStr);

        /**
         *  getLastModDate.
         * @param poolName
         * @param key
         * @return long
         */
        public long getLastModDate(String poolName, String key);

    /**
     * Retrieve a file from the indicated pool's cache.
     * @param poolname name of the cache pool
     * @param key key under which the content was cached
     * @param line options for retrieving the cache
     * @return the cached content as a string, or <code>null</code> if no entry was found
     *     (i.e. cache was empty or poolname was invalid).
     */
//    public String getNew(String pool,String key,String line,scanpage sp);

    /**
     * Store a file in the indicated pool's cache.
     * Returns the old value if available.
     * @param pool name of the cache pool
     * @param key key under which the content was cached
     * @param value content to store
     * @return the old cached content as a string, or <code>null</code> if no entry was found
     */
    public String put(String pool,String key,String value);

    /**
     * Store a file in the indicated pool's cache.
     * Returns the old value if available.
     * @param pool name of the cache pool
     * @param res response object for retrieving headers (used by mmbase.org?)
     * @param key key under which the content was cached
     * @param value content to store
     * @param mimeType the content's mime type
     * @return the old cached content as a string, or <code>null</code> if no entry was found
     */
    public String newput(String pool,HttpServletResponse res,String key,String value, String mimeType);

    /**
     * Store a file in the indicated pool's cache.
     * Returns the old value if available.
     * @param pool name of the cache pool
     * @param key key under which the content was cached
     * @param value content to store
     * @param cachetype only needed for cachepool "PAGE".
     *        If 0, no file transfer is performed. Otherwise the {@link org.mmbase.module.builders.NetFileSrv} builder is
     *        invoked to start the VWM that handles the transfer.
     * @param mimeType the page's mime type, only needed for cachepool "PAGE"
     * @return the old cached content as a string, or <code>null</code> if no entry was found
     * @deprecated Temporary hack for solving asis problems (?). Use {@link #newput(String, HttpServletResponse, String, String, String)} instead.
     */
    public String newput2(String pool,String key,String value, int cachetype, String mimeType);

    /**
     * Removes an entry from the cache pool.
     * @param poolName name of cache pool
     * @param key key of the content to remove
     */
    public void remove(String poolName, String key);
}
