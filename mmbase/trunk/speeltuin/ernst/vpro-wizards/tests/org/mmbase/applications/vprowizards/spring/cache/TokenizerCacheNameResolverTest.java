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
        tcnr.setNameSpace("twee");
        tcnr.setInput("foo,bar drie:hallo");
        List<String> l = tcnr.getNames();
        assertEquals(0, l.size());
    }


    public void test_values_without_namespace_should_become_global() {
        tcnr.setNameSpace("twee");
        tcnr.setInput("foo,bar drie:hallo");
        List<String> l = tcnr.getNames();
        assertEquals(2, l.size());
        assertEquals("foo", l.get(0));
        assertEquals("bar", l.get(1));
    }
    
    

    public void test_value_with_matching_namespace_should_be_returned() {
        tcnr.setNameSpace("drie");
        tcnr.setInput("drie:hallo vier:noop,noppes");
        List<String> l = tcnr.getNames();
        assertEquals(1, l.size());
        assertNotNull(l.contains("hallo"));
    }
    
    public void test_values_with_matching_namespace_should_be_returned() {
        tcnr.setNameSpace("vier");
        tcnr.setInput("drie:hallo vier:noop,noppes");
        List<String> l = tcnr.getNames();
        assertEquals(2, l.size());
        assertNotNull(l.contains("noop"));
        assertNotNull(l.contains("noppes"));
    }
    
    public void test_global_value_and_values_with_matching_namespace_should_be_returned() {
        tcnr.setNameSpace("drie");
        tcnr.setInput("foo drie:hallo vier:noop,noppes");
        List<String> l = tcnr.getNames();
        assertEquals(2, l.size());
        assertNotNull(l.contains("foo"));
        assertNotNull(l.contains("hallo"));
    }
    
    public void test_global_values_and_values_with_matching_namespace_should_be_returned() {
        tcnr.setNameSpace("drie");
        tcnr.setInput("foo,bar,again drie:hallo vier:noop,noppes");
        List<String> l = tcnr.getNames();
        assertEquals(4, l.size());
        assertNotNull(l.contains("foo"));
        assertNotNull(l.contains("bar"));
        assertNotNull(l.contains("again"));
        assertNotNull(l.contains("hallo"));
    }

    public void test_just_global_values() {
            tcnr.setNameSpace("drie");
            tcnr.setInput("foo,bar,again vier:noop,noppes");
            List<String> l = tcnr.getNames();
            assertEquals(3, l.size());
            assertNotNull(l.contains("foo"));
            assertNotNull(l.contains("bar"));
            assertNotNull(l.contains("again"));
    }
    
    
    public void test_simple_templates(){
        tcnr.setNameSpace("vier");
        tcnr.setInput("vier:noop[test],noppes");
        List<String> l = tcnr.getNames();
        assertEquals(2, l.size());
        assertNotNull(l.contains("noop[test]"));
        assertNotNull(l.contains("noppes"));
    }
    
    public void test_template_with_nodenr(){
        tcnr.setNameSpace("vier");
        tcnr.setInput("vier:noop[test:4576],noppes[jaja]");
        List<String> l = tcnr.getNames();
        assertEquals(2, l.size());
        assertNotNull(l.contains("noop[test:4576]"));
        assertNotNull(l.contains("noppes[jaja]"));
    }
}
