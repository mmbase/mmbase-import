package org.mmbase.applications.vprowizards.spring.cache;

import org.mmbase.applications.vprowizards.spring.util.ClassInstanceFactory;

public class OSCacheHandlerInterceptorIntegrationTest extends OSCacheHandlerInterceptorSupport {
    
    @Override
    protected void setUp() {
        cacheNameResolverFactory = new ClassInstanceFactory<CacheNameResolver>();
        cacheNameResolverFactory.setClass(TokenizerCacheNameResolver.class);
        cacheWrapper = new DummyCacheWrapper();
        
        osCacheHandlerInterceptor = new BasicCacheHandlerInterceptor();
        osCacheHandlerInterceptor.setCacheNameResolverFactory(cacheNameResolverFactory);
        osCacheHandlerInterceptor.setCacheWrapper(cacheWrapper);
    }

    public void test_no_flush_param_does_nothing(){
        setUpRequest(createList(createCacheFlushHintTypeNode()), "");
        handleRequest();
        
        assertAmountOfCacheFlushes(0);
    }
    
    public void test_no_cacheflush_hint_dous_nothing(){
        setUpRequest(null, "node:user");
        handleRequest();
        assertAmountOfCacheFlushes(0);
    }
    
    public void test_single_hint_with_matching_template_couses_flush(){
        setUpRequest(createList(createCacheFlushHintTypeNode()), "node:user");
        handleRequest();
        assertAmountOfCacheFlushes(1);
        assertCacheHasBeenFlushed("user");

        setUp();
        setUpRequest(createList(createCacheFlushHintTypeRelation()), "relation:user");
        handleRequest();
        assertAmountOfCacheFlushes(1);
        assertCacheHasBeenFlushed("user");
        
        setUp();
        setUpRequest(createList(createCacheFlushHintTypeRequest()), "request:user");
        handleRequest();
        assertAmountOfCacheFlushes(1);
        assertCacheHasBeenFlushed("user");
    }
    
    public void test_single_hint_with_template_for_different_hint_type_dous_nothing(){
        setUpRequest(createList(createCacheFlushHintTypeNode()), "relation:user");
        handleRequest();
        assertAmountOfCacheFlushes(0);
        
        setUp();
        setUpRequest(createList(createCacheFlushHintTypeRelation()), "node:user");
        handleRequest();
        assertAmountOfCacheFlushes(0);
        
        setUp();
        setUpRequest(createList(createCacheFlushHintTypeRequest()), "node:user");
        handleRequest();
        assertAmountOfCacheFlushes(0);
    }
    
    public void test_single_hint_with_multiple_matching_templates_for_different_hint_type_dous_nothing(){
        setUpRequest(createList(createCacheFlushHintTypeNode()), "relation:user,disco");
        handleRequest();
        assertAmountOfCacheFlushes(0);
        
        setUp();
        setUpRequest(createList(createCacheFlushHintTypeRelation()), "request:user,disco");
        handleRequest();
        assertAmountOfCacheFlushes(0);
        
        setUp();
        setUpRequest(createList(createCacheFlushHintTypeRequest()), "node:user,disco");
        handleRequest();
        assertAmountOfCacheFlushes(0);
    }

    public void test_single_hint_with_multiple_matching_templates_couses_flush(){
        setUpRequest(
                createList(createCacheFlushHintTypeNode()), 
                "node:user,disco");
        handleRequest();
        assertAmountOfCacheFlushes(2);
        assertCacheHasBeenFlushed("user");
        assertCacheHasBeenFlushed("disco");
        
        setUp();
        setUpRequest(
                createList(createCacheFlushHintTypeRelation()), 
                "relation:user,disco");
        handleRequest();
        assertAmountOfCacheFlushes(2);
        assertCacheHasBeenFlushed("user");
        assertCacheHasBeenFlushed("disco");
        
        setUp();
        setUpRequest(
                createList(createCacheFlushHintTypeRequest()), 
                "request:user,disco");
        handleRequest();
        assertAmountOfCacheFlushes(2);
        assertCacheHasBeenFlushed("user");
        assertCacheHasBeenFlushed("disco");
    }
    
    public void test_multiple_matching_hints(){
        setUpRequest(
                createList(createCacheFlushHintTypeNode(), 
                        createCacheFlushHintTypeRelation(), 
                        createCacheFlushHintTypeRequest()), 
                "node:user relation:disco,something request:global");
        handleRequest();
        assertAmountOfCacheFlushes(4);
        assertCacheHasBeenFlushed("user");
        assertCacheHasBeenFlushed("disco");
        assertCacheHasBeenFlushed("something");
        assertCacheHasBeenFlushed("global");
    }
    
    public void test_mix_of_matching_and_nonmatching_hints(){
        setUpRequest(
                createList(createCacheFlushHintTypeNode(), 
                        createCacheFlushHintTypeRequest()), 
                "node:user relation:disco,something request:global");
        handleRequest();
        assertAmountOfCacheFlushes(2);
        assertCacheHasBeenFlushed("user");
        assertCacheHasBeenFlushed("global");
    }    
    
    public void test_unsupported_namespace_dous_nothing(){
        setUpRequest(
                createList(createCacheFlushHintTypeNode()), 
                "unsupportednamespace:user");
        handleRequest();
        assertAmountOfCacheFlushes(0);
    }
    
    public void test_with_prefix_suffix_modifier(){
        setUpRequest(
                createList(createCacheFlushHintTypeNode(), 
                        createCacheFlushHintTypeRelation(), 
                        createCacheFlushHintTypeRequest()), 
                "node:user relation:disco,something request:global");
        addPrefixSuffixModifier("p_", "_s");
        handleRequest();
        assertAmountOfCacheFlushes(4);
        assertCacheHasBeenFlushed("p_user_s");
        assertCacheHasBeenFlushed("p_disco_s");
        assertCacheHasBeenFlushed("p_something_s");
        assertCacheHasBeenFlushed("p_global_s");
    }
    
    
    public void test_with_template_cleaner_modifier_and_simple_template(){
        setUpRequest(createList(createCacheFlushHintTypeNode()), "node:user_[users:90]");
        addTemplateRemover();
        handleRequest();
        assertAmountOfCacheFlushes(1);
        assertCacheHasBeenFlushed("user_90");
    }
    
    public void test_with_template_cleaner_modifier_and_query_template(){
        setUpRequest(createList(createCacheFlushHintTypeNode()), "node:user_[users.disco.nogwat:90]");
        addTemplateRemover();
        handleRequest();
        assertAmountOfCacheFlushes(1);
        assertCacheHasBeenFlushed("user_90");
    }
    
    public void test_with_template_cleaner_modifier_and_multi_template(){
        setUpRequest(createList(createCacheFlushHintTypeNode()), 
                "node:user_[users.disco.nogwat:90]and[something:80]");
        addTemplateRemover();
        handleRequest();
        assertAmountOfCacheFlushes(1);
        assertCacheHasBeenFlushed("user_90and80");
    }
    
    public void test_with_template_cleaner_modifier_and_simple_template_without_number(){
        setUpRequest(createList(createCacheFlushHintTypeNode()), "node:user_[users]");
        addTemplateRemover();
        handleRequest();
        assertAmountOfCacheFlushes(1);
        assertCacheHasBeenFlushed("user_");
    }
    
    public void test_with_both_prefixsuffix_and_template_cleaner(){
        setUpRequest(createList(createCacheFlushHintTypeNode()), "node:user_[users:100]");
        addTemplateRemover();
        addPrefixSuffixModifier("p_", "_s");
        handleRequest();
        assertAmountOfCacheFlushes(1);
        assertCacheHasBeenFlushed("p_user_100_s");
    }
}
