package org.mmbase.applications.vprowizards.spring.cache.template;

import junit.framework.TestCase;

public class TemplateTest extends TestCase {
    public void testTemplateWithoutNodenr(){
        Template t = new Template("hallo");
        assertFalse(t.hasNodenr());
        assertEquals("", t.getNodeNumber());
    }
    
    public void testTemplateWithNodenr(){
        Template t = new Template("hallo:9090");
        assertEquals("hallo:9090", t.getTemplate());
        assertTrue(t.hasNodenr());
        assertEquals("9090", t.getNodeNumber());
    }
}
