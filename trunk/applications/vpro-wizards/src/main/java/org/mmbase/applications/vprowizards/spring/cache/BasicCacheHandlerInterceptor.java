/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.cache;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.util.ClassInstanceFactory;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * <pre>
 *This is a cache handler intercepter that implements support for all types of cache flush hints.
 *It works as following:
 *- It creates a {@link Handling} instance for each type of cache flush hint.
 *- It uses a class instance factory of type {@link CacheNameResolver} to generate a resolver instance.
 *  The resolver is used to  transform the value of the 'flush name' parameter into a list of
 *  flush names for a specific type of cache flush hint.
 *- Any number of {@link Modifier}s can be registered to post process the resolved cache names. They are
 *  applied in the order they are registered.
 *- A {@link CacheWrapper} is used to handle the actual cache flushing
 *
 *</pre>
 * @author ebunders
 *
 */
public class BasicCacheHandlerInterceptor extends CacheHandlerInterceptor {

    private static Logger log = Logging.getLoggerInstance(BasicCacheHandlerInterceptor.class);
    private List<Modifier> modifiers = new ArrayList<Modifier>();
    
//    private Class<? extends CacheNameResolver> cachNameResolverClass;
    private ClassInstanceFactory<CacheNameResolver> cacheNameResolverFactory;
    private CacheWrapper cacheWrapper = null;

    BasicCacheHandlerInterceptor() {
        //add the namespaces to the cachename resolver

        handlings.add(new Handling(CacheFlushHint.TYPE_REQUEST) {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
                    throws Exception {

                log.debug("handling request type flush hint");
                if (shouldFlush(request)) {
                    CacheNameResolver resolver = cacheNameResolverFactory.newInstance();
                    resolver.setInput(ServletRequestUtils.getStringParameter(request, "flushname", ""));
                    flushForNames(resolver.getNames("request"));
                }
            }

        });

        handlings.add(new Handling(CacheFlushHint.TYPE_NODE) {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
                    throws Exception {

                log.debug("handling node type flush hint");
                if (shouldFlush(request)) {
                    CacheNameResolver resolver = cacheNameResolverFactory.newInstance();
                    resolver.setInput(ServletRequestUtils.getStringParameter(request, "flushname", ""));
                    flushForNames(resolver.getNames("node"));
                }
            }
        });

        handlings.add(new Handling(CacheFlushHint.TYPE_RELATION) {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
                    throws Exception {

                log.debug("handling relation type flush hint");
                if (shouldFlush(request)) {
                    CacheNameResolver resolver = cacheNameResolverFactory.newInstance();
                    resolver.setInput(ServletRequestUtils.getStringParameter(request, "flushname", ""));
                    flushForNames(resolver.getNames("relation"));
                }
            }
        });

    }

    
    
    public void setCacheWrapper(CacheWrapper cacheWrapper) {
        this.cacheWrapper = cacheWrapper;
    }


    public CacheWrapper getCacheWrapper() {
        return cacheWrapper;
    }
    
    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
    }
    
    public void setModifiers(List<Modifier> modifiers) {
        this.modifiers.addAll(modifiers);
    }

    private boolean shouldFlush(HttpServletRequest request) {
        return ! StringUtils.isEmpty(request.getParameter("flushname"));
    }

    public ClassInstanceFactory<CacheNameResolver> getCacheNameResolverFactory() {
        return cacheNameResolverFactory;
    }



    public void setCacheNameResolverFactory(ClassInstanceFactory<CacheNameResolver> classInstanceFactory) {
        this.cacheNameResolverFactory = classInstanceFactory;
    }



    /**
     * flush the given cache groups.
     * @param request
     * @param flushnames a comma separated list of cache groups.
     * @param request 
     */
    private void flushForNames(List<String> flushnames) {
        for(String name: flushnames) {
            cacheWrapper.flushForName(applyModifiers(name));
        }
    }
    
    private String applyModifiers(String input){
        for(Modifier modifier: modifiers){
            input = modifier.modify(input);
        }
        return input;
    }

    /**
     * apply all the modifiers to a list of strings.
     * All the cache names that are resolved will be put through all the registered modifiers
     * before they are returned.
     * 
     * @param items
     * @return
     */
    private List<String> modify(List<String> items) {
        List<String> result = new ArrayList<String>();
        for (String item: items) {
            for (Modifier modifier : modifiers) {
                item = modifier.modify(item);
            }
            result.add(item);
        }
        return result;
    }

}
