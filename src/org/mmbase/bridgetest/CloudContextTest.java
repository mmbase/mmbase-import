/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridgetest;

import junit.framework.*;
import org.mmbase.bridge.*;

public class CloudContextTest extends TestCase {

    public CloudContextTest(String name) {
        super(name);
    }

    public void testListClouds() {
        CloudContext cloudContext = LocalContext.getCloudContext(); 
        boolean defaultCloudFound = false;
        StringList stringList = cloudContext.getCloudNames();
        for (int i = 0; i < stringList.size(); i++) {
            Cloud cloud = cloudContext.getCloud(stringList.getString(i));
            if (cloud.getName().equals("mmbase")) {
                defaultCloudFound = true;
            }
        }
        assert(defaultCloudFound);
    }

}