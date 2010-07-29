package org.mmbase.applications.vprowizards.spring;

import java.util.*;

import junit.framework.TestCase;

public class ResourceBundelTest extends TestCase {

    ResourceBundle bundle;

    
    public void testEnglish(){
        Locale locale = new Locale("en");
        bundle = ResourceBundle.getBundle("org.mmbase.applications.vprowizards.resources.messages", locale);
        assertEquals("Can not create node.", bundle.getString("error.create.node"));
    }
    
    public void testDutch(){
        Locale locale = new Locale("nl");
        bundle = ResourceBundle.getBundle("org.mmbase.applications.vprowizards.resources.messages", locale);
        assertEquals("De node kan niet worde gemaakt.", bundle.getString("error.create.node"));
    }
    
    
}
