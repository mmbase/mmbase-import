/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache.xslt;

import org.mmbase.cache.Cache;
import org.mmbase.util.xml.URIResolver;

import javax.xml.transform.TransformerFactory;

import java.io.File;
import java.net.URL;

/**
 * A cache for XSL Transformer Factories.  There is one needed for
 * every directory, or more precisely, for every instance of
 * org.mmbase.util.xml.URIResolver.
 *
 * @author Michiel Meeuwissen
 * @version $Id: FactoryCache.java,v 1.9 2006-10-11 18:45:57 michiel Exp $
 */
public class FactoryCache extends Cache<URIResolver, TransformerFactory> {

    private static int cacheSize = 50;
    private static FactoryCache cache;
    private static File defaultDir = new File("");

    public static FactoryCache getCache() {
        return cache;
    }

    static {
        cache = new FactoryCache(cacheSize);
        putCache(cache);
    }

    public String getName() {
        return "XSLFactories";
    }
    public String getDescription() {
        return "XSL Transformer Factories";
    }

    /**
     * Creates the XSL Template Cache.
     */
    private FactoryCache(int size) {
        super(size);
    }

    /**
     * If it you are sure not to use the URIResolver, then you can as
     * well use always the same Factory. This function supplies one.
     */
    public TransformerFactory getDefaultFactory() {
        return getFactory(defaultDir);
    }

    /**
     * Make a factory for a certain URIResolver.
     */
    public TransformerFactory getFactory(URIResolver uri) {
        TransformerFactory tf =  get(uri);
        if (tf == null) {
            tf = TransformerFactory.newInstance();
            try {
                tf.setAttribute("http://saxon.sf.net/feature/version-warning", false);
            } catch (IllegalArgumentException iae) {
                // never mind
            }
            tf.setURIResolver(uri);
            // you must set the URIResolver in the tfactory, because it will not be called everytime, when you use Templates-caching.
            put(uri, tf);
        }
        return tf;
    }
    /**
     * Gets a Factory from the cache. This cache is 'intelligent', you
     * can also get from it when it is not in the cache, in which case
     * a new Factory will be created (and put in the cache).
     * @deprecated
     */

    public TransformerFactory getFactory(File cwd) {
        try {
            TransformerFactory tf = get(new URIResolver(new URL("file://" + cwd), true)); // quick access (true means: don't actually create an URIResolver)
            if (tf == null) {
                // try again, but now construct URIResolver first.
                return getFactory(new URIResolver(new URL("file://" + cwd)));
            } else {
                return tf;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public TransformerFactory getFactory(URL cwd) {
        TransformerFactory tf =  get(new URIResolver(cwd, true)); // quick access (true means: don't actually create an URIResolver)
        if (tf == null) {
            // try again, but now construct URIResolver first.
            return getFactory(new URIResolver(cwd));
        } else {
            return tf;
        }
    }



}
