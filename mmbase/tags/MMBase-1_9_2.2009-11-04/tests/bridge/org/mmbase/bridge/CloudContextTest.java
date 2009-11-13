/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.tests.*;
import junit.framework.*;


/**
 * Test class <code>CloudContext</code> from the bridge package.
 *
 * @author Jaco de Groot
 * @version $Id$
 */
public class CloudContextTest extends BridgeTest {

    public CloudContextTest(String name) {
        super(name);
    }

    public void testListClouds() {
        CloudContext cloudContext = getCloudContext();
        boolean defaultCloudFound = false;
        StringList stringList = cloudContext.getCloudNames();
        for (int i = 0; i < stringList.size(); i++) {
            Cloud cloud = cloudContext.getCloud(stringList.getString(i));
            if (cloud.getName().equals("mmbase")) {
                defaultCloudFound = true;
            }
        }
        assertTrue(defaultCloudFound);
    }
    public void testUri() {

        CloudContext cloudContext = getCloudContext();
        // System.out.println(cloudContext.getClass() + " " + cloudContext.getUri());

        Cloud cloud = cloudContext.getCloud("mmbase");
        // http://www.mmbase.org/jira/browse/MMB-1816
        assertEquals("" + cloud.getCloudContext().getClass() + " " + cloudContext.getClass(),
                     cloudContext.getUri(),
                     cloud.getCloudContext().getUri());

    }

}
