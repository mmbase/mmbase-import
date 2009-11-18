/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;


import java.net.URL;
import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;
/**
 * Test the working of the ResourceLoader.
 *
 * <ul>
 * <li>tests if the resource loader can run when mmbase is not started</li>
 * </ul>
 *
 * @author Kees Jongenburger
 * @verion $Id$
 */
public class ResourceLoaderTest {

    /**
     * perform lookup of non existing resource
     */
    @Test
    public void nonExistingResource() throws java.io.IOException {
        URL url = ResourceLoader.getConfigurationRoot().getResource("nonExisting/test.xml");
        assertTrue("non existing resource should not be openable for input", !url.openConnection().getDoInput());
    }

    /**
     * perform lookup of mmbaseroot.xml using getConfigurationroot
     */
    @Test
    public void getMMBaseRootModule() throws java.io.IOException {
        URL url = ResourceLoader.getConfigurationRoot().getResource("modules/mmbaseroot.xml");
        assertNotNull("did not find mmbaseroot.xml", url);
        assertTrue("non existing resource should openable for input", url.openConnection().getDoInput());
    }

    @Test
    public void getPropertiesBuilder() throws java.io.IOException {
        URL url = ResourceLoader.getConfigurationRoot().getResource("builders/properties.xml");
        assertNotNull("did not find properties.xml", url);
        assertTrue("non existing resource should openable for input", url.openConnection().getDoInput());
    }
    @Test
    public void getPropertiesBuilder2() throws java.io.IOException {
        URL url = ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders").getResource("properties.xml");
        assertNotNull("did not find properties.xml", url);
        assertTrue("non existing resource should openable for input", url.openConnection().getDoInput());
    }

    @Test
    public void getPropertiesBuilder3() throws java.io.IOException {
        URL url = ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders").getResource("/properties.xml");
        assertNotNull("did not find /properties.xml", url);
        assertTrue("non existing resource should openable for input", url.openConnection().getDoInput());
        System.out.println(url);
    }

    @Test
    public void builders() throws java.io.IOException {
        Set<String> xmls = ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders").getResourcePaths(ResourceLoader.XML_PATTERN, true);
        assertTrue("" + xmls + " did not contain  properties.xml", xmls.contains("properties.xml"));
        assertTrue("" + xmls + " did not contain  core/object.xml", xmls.contains("core/object.xml"));

    }

    @Test
    public void builders2() throws java.io.IOException {
        List<URL> xmls1 = ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders").getResourceList("/properties.xml");
        List<URL> xmls2 = ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders").getResourceList("properties.xml");
        assertEquals(xmls1, xmls2);

    }
    //@Test // does work like this,
    public void weightConfiguration() throws java.io.IOException {
        URL u  = ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders").getResource("core/object.xml");
        assertTrue(u.toString(), u.toString().endsWith("/mmbase-tests-1.jar!/org/mmbase/config/builders/core/object.xml")); // jar was in /tests
    }

    @Test
    public void getDocument() throws Exception {
        assertNull(ResourceLoader.getConfigurationRoot().getDocument("doesnotexist.xml"));
    }


    @Test
    public void spaces() throws Exception {
        assertNotNull(ResourceLoader.getConfigurationRoot().getDocument("directory with spaces/file with spaces.xml", false, null));
        assertNotNull(ResourceLoader.getConfigurationRoot().getDocument("directory with spaces/file.xml", false, null));
        ResourceLoader child = ResourceLoader.getConfigurationRoot().getChildResourceLoader("directory with spaces");
        assertNotNull(child.getDocument("file.xml", false, null));
        System.out.println(child.getResource("file.xml"));
        Set<String> xmls = child.getResourcePaths(ResourceLoader.XML_PATTERN, true);
        assertEquals("" + child, 2, xmls.size()); // MMB-1894?
        for (String x : xmls) {
            System.out.println(x);
        }


    }

}
