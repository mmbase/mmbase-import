package org.mmbase.util;

import java.net.URL;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import junit.framework.TestCase;

/**
 * Test the wokring of the ResourceLoader.
 * 
 * <ul>
 * <li>tests if the resource loader can run when mmbase is not started</li>
 * </ul>
 * 
 * @author Kees Jongenburger
 * @verion $Id: ResourceLoaderTest.java,v 1.1 2005-02-11 09:51:31 keesj Exp $
 */
public class ResourceLoaderTest extends TestCase {
    //MMBase logger
    private static Logger log = Logging.getLoggerInstance(ResourceLoaderTest.class);

    /**
     * perform lookup of non existing resource
     */
    public void testNonExistingResource() {
        URL url = ResourceLoader.getConfigurationRoot().getResource("nonExisting/test.xml");
        assertNull("non existing resource should return null", url);
    }

    /**
     * perform lookup of mmbaseroot.xml using getConfigurationroot
     */
    public void testGetMMBaseRootModule() {
        URL url = ResourceLoader.getConfigurationRoot().getResource("modules/mmbaseroot.xml");
        assertNotNull("did not find mmbaseroot.xml", url);
    }
}