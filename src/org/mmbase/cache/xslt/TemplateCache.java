/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache.xslt;

import org.mmbase.cache.Cache;
import javax.xml.transform.Templates;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.ResourceWatcher;

import java.util.Map;
import java.util.Iterator;
import javax.xml.transform.URIResolver;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * A cache for XSL transformation templates. A template can be based
 * on a file, or on a string. In the first case the cache key is based
 * on the file name, and the cache entry is invalidated if the file
 * changes (so, if you uses 'imports' in the XSL template, you have to
 * touch the file which imports, if the imported files changes). If
 * the template is based on a string, then the string itself serves as
 * a key.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: TemplateCache.java,v 1.16 2006-09-04 12:53:51 michiel Exp $
 * @since   MMBase-1.6
 */
public class TemplateCache extends Cache<Object, Templates> {

    private static final Logger log = Logging.getLoggerInstance(TemplateCache.class);

    private static int cacheSize = 50;
    private static TemplateCache cache;

    /**
     * The Source-s which are based on a file, are added to this FileWatcher, which wil invalidate
     * the corresponding cache entry when the file changes.
     */
    private static ResourceWatcher templateWatcher = new ResourceWatcher(ResourceLoader.getWebRoot()) {
            public  void onChange(String file) {
                // invalidate cache.
                if (log.isDebugEnabled()) log.debug("Removing " + file.toString() + " from cache");
                synchronized(cache) {
                    int removed = cache.remove(file);
                    if (removed == 0) {
                        log.error("Could not remove " + file.toString() + " Template(s) from cache!");
                    } else {
                        if (log.isDebugEnabled()) log.debug("Removed " + removed + " entries from cache");
                    }
                }
                this.remove(file); // should call remove of FileWatcher, not of TemplateCache again.
            }
        };

    /**
     * Returns the Template cache.
     */
    public static TemplateCache getCache() {
        return cache;
    }

    static {
        cache = new TemplateCache(cacheSize);
        putCache(cache);
        templateWatcher.setDelay(10 * 1000); // check every 10 secs if one of the stream source templates was change
        templateWatcher.start();

    }

    public String getName() {
        return "XSLTemplates";
    }
    public String getDescription() {
        return "XSL Templates";
    }

    /**
     * Creates the XSL Template Cache.
     */
    private TemplateCache(int size) {
        super(size);
    }

    /**
     * Object to use as a key in the Caches.
     * Contains the systemid of the XSLT object (if there is one)
     * and the URIResolver.
     */
    private class Key {
        private String  src;
        private URIResolver uri;
        Key(Source src, URIResolver uri) {
            this.src = src.getSystemId();
            this.uri = uri;
        }
        public boolean equals(Object o) {
            if (o instanceof Key) {
                Key k = (Key) o;
                return  (src == null ? k.src == null : src.equals(k.src)) &&
                        (uri == null ? k.uri == null : uri.equals(k.uri));
            }
            return false;
        }
        public int hashCode() {
            return 32 * (src == null ? 0 : src.hashCode()) + (uri == null ? 0 : uri.hashCode());
        }
        /**
         * Returns File object or null
         */
        String getURL() {
            if (src == null) return null;
            try {
                return src;
            } catch (Exception e) {
                return null;
            }
        }
        public String toString() {
            return "" + src + "/" + uri;
        }

    }

    /**
     * Remove all entries associated wit a certain url (used by FileWatcher).
     *
     * @param  The file under concern
     * @return The number of cache entries removed
     */

    private int remove(String file) {
        int removed = 0;
        Iterator i =  entrySet().iterator();
        if (log.isDebugEnabled()) log.debug("trying to remove keys containing " + file);
        while (i.hasNext()) {
            Key mapKey = (Key) ((Map.Entry) i.next()).getKey();
            if (mapKey.getURL().equals(file)) {
                if(remove(mapKey) != null) {
                    removed++;
                } else {
                    log.warn("Could not remove " + mapKey);
                }
            }
        }
        return removed;
    }


    public Templates getTemplates(Source src) {
        return getTemplates(src, null);
    }
    public Templates getTemplates(Source src, URIResolver uri) {
        Key key = new Key(src, uri);
        if (log.isDebugEnabled()) log.debug("Getting from cache " + key);
        return (Templates) get(key);
    }

    /**
     * When removing an entry (because of LRU e.g), then also the FileWatcher must be removed.
     */

    public synchronized Templates remove(Object key) {
        if (log.isDebugEnabled()) log.debug("Removing " + key);
        Templates result = super.remove(key);
        String url = ((Key) key).getURL();
        remove(url);
        templateWatcher.remove(url);
        return result;
    }

    /**
     * You can only put Source/Templates values in the cache, so this throws an Exception.
     *
     * @throws RuntimeException
     **/

    public Templates put(Object key, Templates value) {
        throw new RuntimeException("wrong types in cache");
    }
    public Templates put(Source src, Templates value) {
        return put(src, value, null);
    }
    public Templates put(Source src, Templates value, URIResolver uri) {
        if (! isActive()) {
            if (log.isDebugEnabled()) {
                log.debug("XSLT Cache is not active");
            }
            return null;
        }
        Key key = new Key(src, uri);
        Templates res = super.put(key, value);
        log.service("Put xslt in cache with key " + key);
        templateWatcher.add(key.getURL());
        if (log.isDebugEnabled()) {
            log.debug("have set watch on  " + key.getURL());
            log.trace("currently watching: " + templateWatcher);
        }
        return res;
    }


    /**
     * Invocation of the class from the commandline for testing
     */
    public static void main(String[] argv) {
        log.setLevel(org.mmbase.util.logging.Level.DEBUG);
        try {
            java.io.File xslFile = java.io.File.createTempFile("templatecachetest", ".xsl");
            log.info("using file " + xslFile);
            java.io.FileWriter fw = new java.io.FileWriter(xslFile);
            fw.write("<xsl:stylesheet  version = \"1.1\" xmlns:xsl =\"http://www.w3.org/1999/XSL/Transform\"></xsl:stylesheet>");
            fw.close();
            for (int i =0; i<10; i++) {
                TemplateCache cache = TemplateCache.getCache();
                Source xsl = new StreamSource(xslFile);
                org.mmbase.util.xml.URIResolver uri = new org.mmbase.util.xml.URIResolver(xslFile.getParentFile());
                Templates cachedXslt = cache.getTemplates(xsl, uri);
                log.info("template cache size " + cache.size() + " entries: " + cache.entrySet());
                if (cachedXslt == null) {
                    cachedXslt = FactoryCache.getCache().getFactory(uri).newTemplates(xsl);
                    cache.put(xsl, cachedXslt, uri);
                } else {
                    if (log.isDebugEnabled()) log.debug("Used xslt from cache with " + xsl.getSystemId());
                }
            }
            xslFile.delete();
        } catch (Exception e) {
            System.err.println("hmm?" + e);
        }


    }

}
