/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache.xslt;

import org.mmbase.cache.Cache;

import javax.xml.transform.*;
import java.util.*;
import org.w3c.dom.Document;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Caches the results of XSL transformations. 
 *
 * @todo Cache entries must be invalidated if XSL template changes (now getSystemId is used as cache
 * entry). See TemplatesCache (which uses a FileWatcher).
 *
 * @author  Michiel Meeuwissen
 * @version $Id: ResultCache.java,v 1.1 2002-03-30 16:30:22 michiel Exp $
 * @since   MMBase-1.6
 */
public class ResultCache extends Cache {

    private static Logger log = Logging.getLoggerInstance(ResultCache.class.getName());

    private static final int maxResultSize = 1500;
    private static int cacheSize = 50;
    private static ResultCache cache;

    /**
     * Returns the XSLT Result cache.
     */
    public static ResultCache getCache() {
        return cache;
    }

    static {
        cache = new ResultCache(cacheSize);
        putCache(cache);
    }

    public String getName() {
        return "XSLTResults";
    }
    public String getDescription() {
        return "XSL Transformation Results";
    }

    /**
     * Creates the XSL Result Cache.
     */
    private ResultCache(int size) {
        super(size);
    }

    /**
     * You can only put Source/Templates values in the cache, so this throws an Exception.
     *
     * @throws RuntimeException
     **/
    
    public Object put(Object key, Object value) {
        throw new RuntimeException("wrong types in cache");
    }

    /** 
     * Generates the key which is to be used in the Cache Map.
     *
     * @todo Generate this key faster and smaller
     */
    private String getKey(Source xsl, Map params, Properties props, Document src) {        
        java.io.StringWriter result = new java.io.StringWriter();
        XMLSerializer xml = new XMLSerializer(result, null);
        try {
            xml.serialize(src);
            return xsl.getSystemId() + "/" + (params != null ? params.toString() : "")  + "/" + (props != null ? props.toString() : "")+ "/" + result.toString();
        } catch (java.io.IOException e) {
            return "KEYCOULDNOTBEGENERATED";
        }
    }
      
    /**
     * This is an intelligent get, which also does the put if it cannot find the requested result.
     *
     * @param temp The Templates from which the transformer must be created (if necessary)
     * @param xsl  The XSL Source. This only used to produce the key, because with the Templates it
     *             is difficult
     * @param params  Parameters for the XSL Transformation
     * @param src     The Document which must be transformed.
     * @return The transformation result. It does not return null.
     */
    public String get(Templates temp, Source xsl, Map params, Properties props, Document src) {
        String key = getKey(xsl, params, props, src);
        log.debug("Getting cacehd result of XSL transformation: " + key);
        String result = (String) get(key);
        if (result == null) {
            try {
                // do the transformation, and cache the result:
                Transformer transformer = temp.newTransformer();
                // add the params:
                if (params != null) {
                    Iterator i = params.entrySet().iterator();
                    while (i.hasNext()) {
                        Map.Entry entry = (Map.Entry) i.next();
                        transformer.setParameter((String) entry.getKey(), entry.getValue());
                    }
                }
                if (props != null) {
                    transformer.setOutputProperties(props);
                }
                
                java.io.StringWriter res = new java.io.StringWriter();
                transformer.transform(new javax.xml.transform.dom.DOMSource(src),
                                      new javax.xml.transform.stream.StreamResult(res));
                result = res.toString();
            } catch (TransformerException e) {
                result = e.toString();
            }
            // if result is not too big, then it can be cached:
            if (result.length() < maxResultSize) {
                log.service("Put xslt Result in cache with key " + key.substring(0,10) + "...");
                super.put(key, result);
            } else {
                log.debug("xslt Result of key " + key + " is too big to put in cache");
            }
        }
            
        return result;
        
    }

}
