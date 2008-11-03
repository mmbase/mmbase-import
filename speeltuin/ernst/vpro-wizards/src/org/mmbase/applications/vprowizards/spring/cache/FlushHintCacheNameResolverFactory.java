/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * This factory instantiates a TokenizerCacheNameResolver instance, sets
 * all the configured modifiers on it, and sets the namespaces 'node', 'relation', and 'request',
 * matching the cache flush hint types.
 * @author ebunders
 *
 */
public class FlushHintCacheNameResolverFactory implements CacheNameResolverFactory {
    private List<Modifier> modifiers = new ArrayList<Modifier>();
    //:FIXME this should be abstracted through a setter (or a CacheFlushHint oriented subclass)
    private String[] namespaces =  {"request","node","relation"};

    public CacheNameResolver getCacheNameResolver(){
        TokenizerCacheNameResolver resolver = new TokenizerCacheNameResolver();
        resolver.addNameSpaces(namespaces);
        for(Modifier modifier: modifiers) {
            resolver.addModifier(modifier.copy());
        }
        return resolver;
    }

    /**
     * Configure what modifiers should be set on the created Tokenizer Cachname Resolvers?
     * @param modifierClasses a list of classes that implement the Modifier interface, and have a no-arg constructor.
     */
    public void setModifiers(List<Modifier> modifiers) {
        this.modifiers.addAll(modifiers);
    }

}
