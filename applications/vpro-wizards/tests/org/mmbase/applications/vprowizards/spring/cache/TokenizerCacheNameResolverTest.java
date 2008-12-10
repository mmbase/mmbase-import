package org.mmbase.applications.vprowizards.spring.cache;

import java.util.List;

import junit.framework.TestCase;

public class TokenizerCacheNameResolverTest extends TestCase {

    private TokenizerCacheNameResolver tcnr;

    @Override
    protected void setUp() throws Exception {
        tcnr = new TokenizerCacheNameResolver();
    }

public void assert_asking_values_for_namespace_that_has_no_values_should_return_empty_list(){
        tcnr.setInput("foo,bar drie:hallo");
        List<String> l = tcnr.getNames("twee");
        assertEquals(0, l.size());
    }


    public void test_values_without_namespace_should_become_global() {
        tcnr.setInput("foo,bar drie:hallo");
        List<String> l = tcnr.getNames("twee");
        assertEquals(2, l.size());
        assertEquals("foo", l.get(0));
        assertEquals("bar", l.get(1));
    }
    
    

    public void test_value_with_matching_namespace_should_be_returned() {
        tcnr.setInput("drie:hallo vier:noop,noppes");
        List<String> l = tcnr.getNames("drie");
        assertEquals(1, l.size());
        assertTrue(l.contains("hallo"));
    }
    
    public void test_values_with_matching_namespace_should_be_returned() {
        tcnr.setInput("drie:hallo vier:noop,noppes");
        List<String> l = tcnr.getNames("vier");
        assertEquals(2, l.size());
        assertTrue(l.contains("noop"));
        assertTrue(l.contains("noppes"));
        l = tcnr.getNames("drie");
        assertEquals(1, l.size());
        
    }
    
    public void test_global_value_and_values_with_matching_namespace_should_be_returned() {
        tcnr.setInput("foo drie:hallo vier:noop,noppes");
        List<String> l = tcnr.getNames("drie");
        assertEquals(2, l.size());
        assertTrue(l.contains("foo"));
        assertTrue(l.contains("hallo"));
    }
    
    public void test_global_values_and_values_with_matching_namespace_should_be_returned() {
        tcnr.setInput("foo,bar,again drie:hallo vier:noop,noppes");
        List<String> l = tcnr.getNames("drie");
        assertEquals(4, l.size());
        assertTrue(l.contains("foo"));
        assertTrue(l.contains("bar"));
        assertTrue(l.contains("again"));
        assertTrue(l.contains("hallo"));
    }

    public void test_just_global_values() {
            tcnr.setInput("foo,bar,again vier:noop,noppes");
            List<String> l = tcnr.getNames("drie");
            assertEquals(3, l.size());
            assertTrue(l.contains("foo"));
            assertTrue(l.contains("bar"));
            assertTrue(l.contains("again"));
    }
    
    
    public void test_simple_templates(){
        tcnr.setInput("vier:noop[test],noppes");
        List<String> l = tcnr.getNames("vier");
        assertEquals(2, l.size());
        assertTrue(l.contains("noop[test]"));
        assertTrue(l.contains("noppes"));
    }
    
    public void test_template_with_simple_template(){
        tcnr.setInput("vier:noop[test:4576],noppes[jaja]");
        List<String> l = tcnr.getNames("vier");
        assertEquals(2, l.size());
        assertTrue(l.contains("noop[test:4576]"));
        assertTrue(l.contains("noppes[jaja]"));
    }
    
    public void test_templates_with_query_template(){
        tcnr.setInput("vier:noop[test.disco.nogwat:4576]");
        List<String> l = tcnr.getNames("vier");
        assertEquals(1, l.size());
        assertTrue(l.contains("noop[test.disco.nogwat:4576]"));
    }
    
    public void test_templates_with_multi_template(){
        tcnr.setInput("vier:noop[test.disco.nogwat:4576]en[een:89],twee");
        List<String> l = tcnr.getNames("vier");
        assertEquals(2, l.size());
        assertTrue(l.contains("noop[test.disco.nogwat:4576]en[een:89]"));
        assertTrue(l.contains("twee"));
    }
    
    public void test_tokenizer_resets_with_new_input(){
        tcnr.setInput("een:a,b twee:c");
        tcnr.getNames("een");
        tcnr.setInput("een:a,b,x,d twee:c");
        List<String> l = tcnr.getNames("een");
        assertEquals(4, l.size());
    }
}
