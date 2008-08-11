package org.mmbase.applications.vprowizards.spring.cache;

import java.util.List;

public interface CacheNameResolver {
    /**
     * Obtain the cache names for the given namespace or those without a namespace (global)
     * 
     * @param namespace
     * @return
     */
    public List<String> getNames();

    

    /**
     * set the input string to be tokenized, using the registered tokens.
     * @param input the formatted flushname string
     */
    public void setInput(String input);
}
