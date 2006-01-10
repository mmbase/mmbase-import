/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.tests.*;
import java.io.*;

/**
 */
public class SerializableTest extends BridgeTest {
    Cloud cloud;

    public SerializableTest(String name) {
        super(name);
    }

    public void setUp() {
        cloud = getCloud();
    }

    public void testSerialize() {
        if (!cloud.getClass().getName().equals("org.mmbase.bridge.implementation.BasicCloud")){
            return;
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(cloud);
            assertTrue(bos.size() > 0);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
