package org.mmbase.applications.vprowizards.spring.cache;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class OSCachehandlerSpringConfigTest extends OSCacheHandlerInterceptorSupport {
    private ApplicationContext context;

    
    @Override
    protected void setUp(){
        context = new ClassPathXmlApplicationContext("org/mmbase/applications/vprowizards/spring/cache/applicationContext.xml");
        osCacheHandlerInterceptor = (BasicCacheHandlerInterceptor) context.getBean("handlerInterceptor");
        cacheWrapper = (DummyCacheWrapper) osCacheHandlerInterceptor.getCacheWrapper();
    }
    
    public void test_application_context(){
        assertNotNull(context.getBean("handlerInterceptor"));
        assertNotNull(context.getBean("dummyCacheWrapper"));
        assertNotNull(osCacheHandlerInterceptor.getCacheNameResolverFactory());
        
        assertTrue(DummyCacheWrapper.class.isInstance(osCacheHandlerInterceptor.getCacheWrapper()));
        assertEquals(TokenizerCacheNameResolver.class, osCacheHandlerInterceptor.getCacheNameResolverFactory().getClazz());
    }
    public void test_with_both_prefixsuffix_and_template_cleaner(){
        setUpRequest(createList(createCacheFlushHintTypeNode()), "node:user_[users:100]");
        handleRequest();
        assertAmountOfCacheFlushes(1);
        assertCacheHasBeenFlushed("pre_user_100_suf");
    }

}
