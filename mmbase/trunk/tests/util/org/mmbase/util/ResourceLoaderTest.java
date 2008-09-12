/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.net.URL;

import junit.framework.TestCase;

/**
 * Test the working of the ResourceLoader.
 *
 * <ul>
 * <li>tests if the resource loader can run when mmbase is not started</li>
 * </ul>
 *
 * @author Kees Jongenburger
 * @verion $Id: ResourceLoaderTest.java,v 1.5 2008-09-12 13:10:43 michiel Exp $
 */
public class ResourceLoaderTest extends TestCase {

    /**
     * perform lookup of non existing resource
     */
    public void testNonExistingResource() throws java.io.IOException {
        URL url = ResourceLoader.getConfigurationRoot().getResource("nonExisting/test.xml");
        assertTrue("non existing resource should not be openable for input", !url.openConnection().getDoInput());
    }

    /**
     * perform lookup of mmbaseroot.xml using getConfigurationroot
     */
    public void testGetMMBaseRootModule() throws java.io.IOException {
        URL url = ResourceLoader.getConfigurationRoot().getResource("modules/mmbaseroot.xml");
        assertNotNull("did not find mmbaseroot.xml", url);
        assertTrue("non existing resource should openable for input", url.openConnection().getDoInput());
    }

    public void testGetPropertiesBuilder() throws java.io.IOException {
        URL url = ResourceLoader.getConfigurationRoot().getResource("builders/properties.xml");
        assertNotNull("did not find properties.xml", url);
        assertTrue("non existing resource should openable for input", url.openConnection().getDoInput());
    }
    public void testGetPropertiesBuilder2() throws java.io.IOException {
        URL url = ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders").getResource("properties.xml");
        assertNotNull("did not find properties.xml", url);
        assertTrue("non existing resource should openable for input", url.openConnection().getDoInput());
    }
}
