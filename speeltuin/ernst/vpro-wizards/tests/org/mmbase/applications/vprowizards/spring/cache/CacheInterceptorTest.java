package org.mmbase.applications.vprowizards.spring.cache;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

public class CacheInterceptorTest extends TestCase {
    
    private ApplicationContext context;

    @Override
    protected void setUp() throws Exception {
        context = new ClassPathXmlApplicationContext("org/mmbase/applications/vprowizards/spring/cache/applicationContext.xml");
    }
    
    public void test_application_context(){
        assertNotNull(context.getBean("handlerInterceptor"));
        assertNotNull(context.getBean("cachewrapper"));
        assertNotNull(context.getBean("flushHintCacheNameResolverFactory"));
        BasicCacheHandlerInterceptor handlerInterceptor = (BasicCacheHandlerInterceptor) context.getBean("handlerInterceptor");
        assertTrue(DummyCacheWrapper.class.isAssignableFrom(handlerInterceptor.getCacheWrapper().getClass()));
        FlushHintCacheNameResolverFactory x = (FlushHintCacheNameResolverFactory) context.getBean("flushHintCacheNameResolverFactory");
        assertEquals(x.getClass(), handlerInterceptor.getCacheNameResolverFactory().getClass());
    }

}
