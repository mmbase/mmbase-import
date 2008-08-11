package org.mmbase.applications.vprowizards.spring.cache;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.web.ServletCacheAdministrator;

/**
 *This is a cache handler interceptor that implements support for all types of cacheflush hints.
 *
 *
 * @author ebunders
 *
 */
public class BasicCacheHandlerInterceptor extends CacheHandlerInterceptor {

    private static Logger log = Logging.getLoggerInstance(BasicCacheHandlerInterceptor.class);

    
    private CacheNameResolverFactory cacheNameResolverFactory = null;
    private CacheWrapper cacheWrapper = null;

    BasicCacheHandlerInterceptor() {
        //add the namespaces to the cachename resolver

        handlings.add(new Handling(CacheFlushHint.TYPE_REQUEST) {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
                    throws Exception {

                log.debug("handling request type flush hint");
                if (shouldFlush(request)) {
                    TokenizerCacheNameResolver resolver = (TokenizerCacheNameResolver) cacheNameResolverFactory.getCacheNameResolver();
                    resolver.setInput(RequestUtils.getStringParameter(request, "flushname", ""));
                    resolver.setNameSpace("request");
                    flushForName(resolver.getNames());
                }
            }

        });

        handlings.add(new Handling(CacheFlushHint.TYPE_NODE) {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
                    throws Exception {

                log.debug("handling node type flush hint");
                if (shouldFlush(request)) {
                    TokenizerCacheNameResolver resolver = (TokenizerCacheNameResolver) cacheNameResolverFactory.getCacheNameResolver();
                    resolver.setInput(RequestUtils.getStringParameter(request, "flushname", ""));
                    resolver.setNameSpace("node");
                    flushForName(resolver.getNames());
                }
            }
        });

        handlings.add(new Handling(CacheFlushHint.TYPE_RELATION) {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
                    throws Exception {

                log.debug("handling relation type flush hint");
                if (shouldFlush(request)) {
                    TokenizerCacheNameResolver resolver = (TokenizerCacheNameResolver) cacheNameResolverFactory.getCacheNameResolver();
                    resolver.setInput(RequestUtils.getStringParameter(request, "flushname", ""));
                    resolver.setNameSpace("relation");
                    flushForName(resolver.getNames());
                }
            }
        });

    }

    /**
     * util: concatenate all strings in the list, separated by comma's
     * @param namesForNamespace
     * @return
     */
    private String listToString(List<String> namesForNamespace) {
        String result = "";
        for (Iterator<String> iter = namesForNamespace.iterator(); iter.hasNext();) {
            result = iter.next() + (iter.hasNext() ? "'" : "");
            
        }
        return result;
    }
    
    

    /**
     * flush the given cache groups.
     * @param request
     * @param flushnames a comma separated list of cache groups.
     */
    private void flushForName(List<String> flushnames) {
        for(String name: flushnames) {
            cacheWrapper.flushForName(name);
        }
       
    }

    private boolean shouldFlush(HttpServletRequest request) {
        return ! StringUtils.isEmpty(request.getParameter("flushname"));
    }

   

    public void setCacheNameResolverFactory(CacheNameResolverFactory cacheNameResolverFactory) {
        this.cacheNameResolverFactory = cacheNameResolverFactory;
    }

    public void setCacheWrapper(CacheWrapper cacheWrapper) {
        this.cacheWrapper = cacheWrapper;
    }


}
