/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.test.bridge;

import junit.framework.*;
import org.mmbase.bridge.*;

public class EmptyNodeTest extends TestCase {
    Node node;
    String[] fieldTypes = {"byte", "double", "float", "int", "long", "string"};

    public EmptyNodeTest(String name) {
        super(name);
    }

    public void setUp() {
        // Create a empty test node.
        Cloud cloud = LocalContext.getCloudContext().getCloud("mmbase");
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

    public void testGetValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetValue();
        testGetByteValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
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

    public void testGetByteValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetByteValue();
        testGetValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
    }

    public void testGetDoubleValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assert(node.getDoubleValue(fieldTypes[i] + "field") == -1);
        }
    }

    public void testGetDoubleValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetDoubleValue();
        testGetValue();
        testGetByteValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
    }

    public void testGetFloatValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assert(node.getFloatValue(fieldTypes[i] + "field") == -1);
        }
    }

    public void testGetFloatValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetFloatValue();
        testGetValue();
        testGetByteValue();
        testGetDoubleValue();
        testGetIntValue();
        testGetLongValue();
        testGetStringValue();
    }

    public void testGetIntValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assert(node.getIntValue(fieldTypes[i] + "field") == -1);
        }
    }

    public void testGetIntValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetIntValue();
        testGetValue();
        testGetByteValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetLongValue();
        testGetStringValue();
    }

    public void testGetLongValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assert(node.getLongValue(fieldTypes[i] + "field") == -1);
        }
    }

    public void testGetLongValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetLongValue();
        testGetValue();
        testGetByteValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetStringValue();
    }

    public void testGetStringValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assert("".equals(node.getStringValue(fieldTypes[i] + "field")));
        }
    }

    public void testGetStringValueCache() {
        // Test if the first call doesn't make MMBase cache an incorrect value.
        testGetStringValue();
        testGetValue();
        testGetByteValue();
        testGetDoubleValue();
        testGetFloatValue();
        testGetIntValue();
        testGetLongValue();
    }

    public void testSetSNumber() {
        try {
            node.setIntValue("snumber", 100);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {
        }
    }

    public void testSetDNumber() {
        try {
            node.setIntValue("dnumber", 100);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {
        }
    }

    public void testSetRNumber() {
        try {
            node.setIntValue("rnumber", 100);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {
        }
    }

    public void testSetOwner() {
        try {
            node.setStringValue("owner", "admin");
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {
        }
        try {
            node.setValue("owner", "admin");
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {
        }
    }

    public void testSetOType() {
        try {
            node.setIntValue("otype", 100);
            fail("Should raise a BridgeException");
        } catch (BridgeException e) {
        }
    }

}
