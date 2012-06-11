package org.mmbase.applications.vprowizards.spring.cache;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.mmbase.applications.vprowizards.spring.cache.modifiers.PrefixSuffixModifier;
import org.mmbase.applications.vprowizards.spring.cache.modifiers.TemplateCleanerModifier;
import org.mmbase.applications.vprowizards.spring.util.ClassInstanceFactory;

public class OSCacheHandlerInterceptorSupport extends TestCase {
    
    BasicCacheHandlerInterceptor osCacheHandlerInterceptor;
    ClassInstanceFactory<CacheNameResolver> cacheNameResolverFactory;
    DummyCacheWrapper cacheWrapper;
    
    HttpServletRequest request;
    HttpServletResponse response;

    

    protected void handleRequest() {
        try {
            osCacheHandlerInterceptor.postHandle(request, response, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    protected void addPrefixSuffixModifier(String prefix, String suffix) {
        Modifier prefixSuffix = new PrefixSuffixModifier(prefix, suffix);
        osCacheHandlerInterceptor.addModifier(prefixSuffix);
    }

    protected void addTemplateRemover() {
        Modifier templateRemover = new TemplateCleanerModifier();
        osCacheHandlerInterceptor.addModifier(templateRemover);
    }

    protected void setUpRequest(List<CacheFlushHint> hints, String template) {
        request = createMock(HttpServletRequest.class);
        response = createMock(HttpServletResponse.class);
        expect(request.getAttribute(CacheHandlerInterceptor.PARAM_NAME)).andReturn(hints);
        expect(request.getParameter("flushname")).andReturn(template).atLeastOnce();
        replay(request);
        replay(response);
        
    }

    protected CacheFlushHint createCacheFlushHintTypeRelation() {
        return new CacheFlushHint(CacheFlushHint.TYPE_RELATION);
    }

    protected CacheFlushHint createCacheFlushHintTypeRequest() {
        return new CacheFlushHint(CacheFlushHint.TYPE_REQUEST);
    }

    protected CacheFlushHint createCacheFlushHintTypeNode() {
        return new CacheFlushHint(CacheFlushHint.TYPE_NODE) ;
    }

    protected List<CacheFlushHint> createList(CacheFlushHint... hints) {
        List<CacheFlushHint> hintsList = new ArrayList<CacheFlushHint>();
        for(CacheFlushHint  hint: hints){
            hintsList.add(hint);
        }
        return hintsList;
    }

    protected void assertAmountOfCacheFlushes(int amount) {
        assertEquals(amount, cacheWrapper.getNames().size());
    }

    protected void assertCacheHasBeenFlushed(String name) {
        assertTrue(String.format("cache was not flused for expected name %s", name), cacheWrapper.getNames().contains(name));
    }

}
