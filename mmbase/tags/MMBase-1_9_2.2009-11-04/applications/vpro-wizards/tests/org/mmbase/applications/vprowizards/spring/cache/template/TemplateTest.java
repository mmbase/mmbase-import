package org.mmbase.applications.vprowizards.spring.cache.template;

import junit.framework.TestCase;

public class TemplateTest extends TestCase {
    public void testTemplateWithoutNodenr(){
        Template t = new Template("hallo");
        assertFalse(t.hasNodeNumber());
        assertEquals("", t.getNodeNumber());
    }
    
    public void testTemplateWithNodenr(){
        Template t = new Template("hallo:9090");
        assertEquals("hallo:9090", t.getTemplate());
        assertTrue(t.hasNodeNumber());
        assertEquals("9090", t.getNodeNumber());
    }
    
    public void testRemoveTemplateWithNodenr(){
        Template t = new Template("hallo:9090");
        t.removeTemplate();
        assertEquals("9090", t.getTemplate());
    }
    public void testRemoveTemplateWithoutNodenr(){
        Template t = new Template("hallo");
        t.removeTemplate();
        assertEquals("", t.getTemplate());
    }
}
