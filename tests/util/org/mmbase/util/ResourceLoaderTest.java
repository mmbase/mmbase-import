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
 * @verion $Id: ResourceLoaderTest.java,v 1.3 2005-05-29 11:20:41 nico Exp $
 */
public class ResourceLoaderTest extends TestCase {

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