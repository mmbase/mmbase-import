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


/**
 * A cache for XSL Transformer Factories (there is one needed for every directory). 
 *
 * @author Michiel Meeuwissen
 * @version $Id: FactoryCache.java,v 1.2 2002-03-30 16:32:43 michiel Exp $
 */
public class FactoryCache extends Cache {

    private static int cacheSize = 50;
    private static FactoryCache cache;
    private static File defaultDir = new File("/");

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
     * Gets a Factory from the cache. This cache is 'intelligent', you
     * can also get from it when it is not in the cache, in which case
     * a new Factory will be created (and put in the cache).
     */

    public TransformerFactory getFactory(File cwd) {
        TransformerFactory tf =  (TransformerFactory) get(cwd);
        if (tf == null) {
            URIResolver uriResolver = new URIResolver(cwd);
            tf = TransformerFactory.newInstance();
            tf.setURIResolver(uriResolver);
            // you must set the URIResolver in the tfactory, because it will not be called everytime, when you use Templates-caching.
            put(cwd, tf);
        }
        return tf;
    }



}
