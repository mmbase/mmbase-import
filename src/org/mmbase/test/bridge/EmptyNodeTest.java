/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.test.bridge;

import junit.framework.*;
import org.mmbase.bridge.*;

public class EmptyNodeTest extends NodeTest {

    public EmptyNodeTest(String name) {
        super(name);
    }

    public void setUp() {
        // Create a empty test node.
        Cloud cloud = getCloud();
        node = cloud.getNodeManager("aa").createNode();
        node.commit();
    }

    public void tearDown() {
        // Remove test node.
        node.remove();
    }

    public void testGetValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            if (fieldTypes[i].equals("byte")) {
                byte[] bytes = (byte[])node.getValue(fieldTypes[i] + "field");
                assert(bytes.length == 0);
            } else {
                assert(node.getValue(fieldTypes[i] + "field") == null);
            }
        }
    }

    public void testGetByteValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            byte[] bytes = node.getByteValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assert(bytes.length == 0);
            } else if (fieldTypes[i].equals("double")) {
                assert(bytes.length == 0);
            } else if (fieldTypes[i].equals("float")) {
                assert(bytes.length == 0);
            } else if (fieldTypes[i].equals("int")) {
                assert(bytes.length == 0);
            } else if (fieldTypes[i].equals("long")) {
                assert(bytes.length == 0);
            } else if (fieldTypes[i].equals("string")) {
                assert(bytes.length == 0);
            } else {
                fail();
            }
        }
    }
    public void testGetDoubleValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assert(node.getDoubleValue(fieldTypes[i] + "field") == -1);
        }
    }

    public void testGetFloatValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assert(node.getFloatValue(fieldTypes[i] + "field") == -1);
        }
    }

    public void testGetIntValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assert(node.getIntValue(fieldTypes[i] + "field") == -1);
        }
    }

    public void testGetLongValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assert(node.getLongValue(fieldTypes[i] + "field") == -1);
        }
    }

    public void testGetStringValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assert("".equals(node.getStringValue(fieldTypes[i] + "field")));
        }
    }

}
