/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring.util;



import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;



public class URLParamMapTest extends TestCase{
    private URLParamMap testMap;
    private static final String url = "http://www.nogwat.com?een=zus&twee=zo";

    public void setUp(){
        testMap = new URLParamMap(url);
        
    }
    
    public void test_parse_url(){
        assertEquals(2, testMap.size());
        assertEquals(url, testMap.toString());
    }

    public void test_override_param_illegal(){
        testMap.addParam("een", "hallo", false);
        assertEquals(2, testMap.size());
        assertEquals(url, testMap.toString());
    }
    
    public void test_override_param_legal(){
        testMap.addParam("een", "vaarwel", true);
        assertEquals(2, testMap.size());
        assertEquals("http://www.nogwat.com?een=vaarwel&twee=zo", testMap.toString());
    }
    
    public void test_add_new_param(){
        testMap.addParam("drie", "hola", true);
        assertEquals(3, testMap.size());
        assertEquals(url + "&drie=hola", testMap.toString());
    }
    
    public void test_add_first_param(){
        testMap = new URLParamMap("www.blender.org");
        testMap.addParam("drie", "hola", true);
        assertEquals(1, testMap.size());
        assertEquals("www.blender.org?drie=hola", testMap.toString());
    }
    
    public void test_add_first_param_where_url_ends_with_questonmark(){
        testMap = new URLParamMap("www.blender.org?");
        testMap.addParam("drie", "hola", true);
        assertEquals(1, testMap.size());
        assertEquals("www.blender.org?drie=hola", testMap.toString());
    }
    
    public void test_output_is_input_when_no_params_added(){
        List<String>tests = new ArrayList<String>();
        tests.add("www.blender.org");
        tests.add("www.blender.org?");
        tests.add("www.blender.org?een=ja");
        tests.add("www.blender.org?een=ja&twee=nee");
        for(String t : tests){
            testMap = new URLParamMap(t);
            assertEquals("expected: "+t, t, testMap.toString());
        }
    }
    
    

        
}
