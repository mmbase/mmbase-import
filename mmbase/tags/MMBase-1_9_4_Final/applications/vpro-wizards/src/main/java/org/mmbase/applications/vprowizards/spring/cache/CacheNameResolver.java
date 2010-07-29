/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.cache;

import java.util.List;

/**
 * This interface expresses the concept that the name(s) of cache keys that need to 
 * be flushed can be coded and need to be processed before the cache(s) can be flushed.
 * You can think about a http request parameter containing any number of cache keys that need
 * to be split up in a specific way.
 * 
 * At least on of the getNames() menthods need to be implemented. If one is not implemented it should
 * throw a {@link UnsupportedOperationException}
 * @author ebunders
 *
 */
public interface CacheNameResolver {
    /**
     * Obtain the cache names for the given namespace or those without a namespace (global)
     * 
     * @param qualifier this can be some implementation-specific qualifier or null if the implementation 
     * does not need it.
     * @return a list of specific cache keys
     */
    public List<String> getNames(String qualifier);
    
    /**
     * Obtain the cache names for the given namespace or those without a namespace (global)
     * @return a list of specific cache keys
     */
    public List<String> getNames();

    

    /**
     * set the input string to be tokenized, using the registered tokens.
     * @param input the formatted flushname string
     */
    public void setInput(String input);
}
