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
import org.mmbase.util.FileWatcher;
import java.io.File;
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
 * @version $Id: TemplateCache.java,v 1.7 2002-06-14 19:30:03 michiel Exp $
 * @since   MMBase-1.6
 */
public class TemplateCache extends Cache {

    private static Logger log = Logging.getLoggerInstance(TemplateCache.class.getName());

    private static int cacheSize = 50;
    private static TemplateCache cache;

    /**
     * The Source-s which are based on a file, are added to this FileWatcher, which wil invalidate
     * the corresponding cache entry when the file changes.
     */
    private static FileWatcher templateWatcher = new FileWatcher (true) {
            protected void onChange(File file) {
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
        templateWatcher.setDelay(10 * 1000); // check every 10 secs if one of the stream source templates was changed
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

    private String getKey(Source src) {
        return src.getSystemId();
    }
    
    private String getKey(Source src, URIResolver uri) {     
        return (uri != null ? "" + uri.hashCode() : "") + src.getSystemId();
    }

    private int remove(File file) {
        int removed = 0;
        String key = "file:///" + file.getPath();
        Iterator i =  getOrderedEntries().iterator();
        while (i.hasNext()) {
            String mapKey = (String) ((Map.Entry) i.next()).getKey();
            if (mapKey.indexOf(key) > 0) {
                if(remove(mapKey) != null) {
                    removed++;
                }
            }
        }
        return 0;        
    }

    public Templates getTemplates(Source src) {
        return getTemplates(src, null);
    }
    public Templates getTemplates(Source src, URIResolver uri) {
        String key = getKey(src, uri);
        if (key == null) return null;
        return (Templates) get(key);
    }

    /**
     * You can only put Source/Templates values in the cache, so this throws an Exception.
     *
     * @throws RuntimeException
     **/
    
    public Object put(Object key, Object value) {
        throw new RuntimeException("wrong types in cache");
    }
    public Object put(Source src, Templates value) {
        return put(src, value, null);
    }
    public Object put(Source src, Templates value, URIResolver uri) {
        if (! isActive()) {
            if (log.isDebugEnabled()) {
                log.debug("XSLT Cache is not active");
            }
            return null;
        }
        String key = getKey(src, uri);
        if (key == null) return null;
        Object res = super.put(key, value);        
        log.service("Put xslt in cache with key " + key.substring(0, 20) + "...");
        if (key.startsWith("file:////")) { // this Source is a File, watch it, because it it changes, the cache entry must be invalidated.
            try {
                java.io.File  f  = new java.io.File(new java.net.URL(key).getFile());
                log.debug("setting watch on  " + f.getAbsolutePath());
                templateWatcher.add(f);                
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return res;
    }

}
