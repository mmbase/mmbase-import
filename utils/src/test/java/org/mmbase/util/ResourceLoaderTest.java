/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;


import java.net.URL;
import java.util.*;
import java.io.*;
import org.springframework.mock.web.*;
import org.springframework.core.io.*;


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

    @Test
    public void builders2() throws java.io.IOException {
        List<URL> xmls1 = ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders").getResourceList("/properties.xml");
        List<URL> xmls2 = ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders").getResourceList("properties.xml");
        assertEquals(xmls1, xmls2);

    }
    //@Test // does work like this,
    public void weightConfiguration() throws java.io.IOException {
        URL u  = ResourceLoader.getConfigurationRoot().getResource("magic.xml");
        assertTrue(u.toString(), u.toString().endsWith("/mmbase-tests-1.jar!/org/mmbase/config/builders/core/object.xml")); // jar was in /tests
    }

    @Test
    public void getDocument() throws Exception {
        assertNull(ResourceLoader.getConfigurationRoot().getDocument("doesnotexist.xml"));
    }


    @Test
    public void spacesClassLoader() throws Exception {
        assertNotNull(ResourceLoader.getConfigurationRoot().getDocument("directory with spaces/file with spaces.xml", false, null));
        assertNotNull(ResourceLoader.getConfigurationRoot().getDocument("directory with spaces/file.xml", false, null));
        ResourceLoader child = ResourceLoader.getConfigurationRoot().getChildResourceLoader("directory with spaces");
        assertNotNull(child.getDocument("file.xml", false, null));
        Set<String> xmls = child.getResourcePaths(ResourceLoader.XML_PATTERN, true);
        assertEquals("" + child, 2, xmls.size());
    }


    @Test
    public void spacesFileLoader() throws Exception {
        ResourceLoader fileLoader = new ResourceLoader();
        fileLoader.roots.add(new ResourceLoader.FileURLStreamHandler(fileLoader, new File(System.getProperty("user.dir")), true));

        final String dir = "src/test/resources/org/mmbase/config/directory with spaces";
        assertNotNull(fileLoader.getDocument(dir + "/file with spaces.xml", false, null));
        assertNotNull(fileLoader.getDocument(dir + "/file.xml", false, null));

        ResourceLoader child = fileLoader.getChildResourceLoader(dir);
        Set<String> xmls = child.getResourcePaths(ResourceLoader.XML_PATTERN, true);
        assertEquals("" + child, 2, xmls.size());
        assertNotNull(child.getDocument("file.xml", false, null));
        assertNotNull(child.getDocument("file with spaces.xml", false, null));
        for (String x : xmls) {
            assertNotNull(child.getDocument(x, false, null));
        }
        List<URL> resources = child.getResourceList("file with spaces.xml");
        assertEquals(1, resources.size());
        for (URL u : resources) {
            assertEquals(new File(u.toURI()).exists(), u.openConnection().getDoInput()); // MMB-1894
        }


    }
    @Test
    public void servletContext() throws Exception {
        FileSystemResourceLoader files = new FileSystemResourceLoader();
        System.out.println("files " + files);
        MockServletContext sx = new MockServletContext(files);
        System.out.println("sx " + sx.getResourcePaths("/"));

        ResourceLoader.init(sx);
        ResourceLoader webRoot = ResourceLoader.getWebRoot();
        ResourceLoader child = webRoot.getChildResourceLoader("src/test/files");
        //assertEquals(child, webRoot);

        System.out.println("child contexts : " + child.getChildContexts(java.util.regex.Pattern.compile(".*"), false));
        System.out.println("" + child.getResourcePaths(java.util.regex.Pattern.compile(".*"), false));
        URL u = child.getResource("cx.png");
        System.out.println("Existing: " + child + " " + u.getClass() + " " + u);
        System.out.println("Not existing" + child + " " + child.getResource("doesnotexist"));

        System.out.println("" + sx.getResourcePaths("tmp"));
    }
}
