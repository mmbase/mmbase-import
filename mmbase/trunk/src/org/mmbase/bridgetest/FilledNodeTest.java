/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridgetest;

import junit.framework.*;
import org.mmbase.bridge.*;

public class FilledNodeTest extends TestCase {
    Node node;
    String[] fieldTypes = {"byte", "double", "float", "int", "long", "string"};

    public FilledNodeTest(String name) {
        super(name);
    }

    public void setUp() {
        // Create a test node.
        Cloud cloud = LocalContext.getCloudContext().getCloud("mmbase");
        node = cloud.getNodeManager("aa").createNode();
        byte[] bytes = {72,101,108,108,111,32,119,111,114,108,100,33};
        node.setValue("bytefield", bytes);
        node.setValue("doublefield", new Double(Double.MAX_VALUE));
        node.setValue("floatfield", new Float(Float.MAX_VALUE));
        node.setValue("intfield", new Integer(Integer.MAX_VALUE));
        node.setValue("longfield", new Long(Long.MAX_VALUE));
        node.setValue("stringfield", "Bridge testing!");
        node.commit();
    }

    public void tearDown() {
        // Remove test node.
        node.remove();
    }

    public void testGetValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Object object = node.getValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                byte[] bytes = (byte[])object;
                assert("Hello world!".equals(new String(bytes)));
            } else if (fieldTypes[i].equals("double")) {
                assert(new Double(Double.MAX_VALUE).compareTo((Double)object)
                       == 0);
            } else if (fieldTypes[i].equals("float")) {
                assert(new Float(Float.MAX_VALUE).compareTo((Float)object)
                       == 0);
            } else if (fieldTypes[i].equals("int")) {
                assert(new Integer(Integer.MAX_VALUE).compareTo((Integer)object)
                       == 0);
            } else if (fieldTypes[i].equals("long")) {
                assert(new Long(Long.MAX_VALUE).compareTo((Long)object)
                       == 0);
            } else if (fieldTypes[i].equals("string")) {
                assert("Bridge testing!".equals((String)object));
            } else {
                fail();
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
                byte[] check = {72,101,108,108,111,32,119,111,114,108,100,33};
                for (int j = 0; j < bytes.length; j++) {
                    assert(bytes[j] == check[j]);
                }
            } else if (fieldTypes[i].equals("double")) {
                assert(bytes.length == 0);
            } else if (fieldTypes[i].equals("float")) {
                assert(bytes.length == 0);
            } else if (fieldTypes[i].equals("int")) {
                assert(bytes.length == 0);
            } else if (fieldTypes[i].equals("long")) {
                assert(bytes.length == 0);
            } else if (fieldTypes[i].equals("string")) {
                assert("Bridge testing!".equals(new String(bytes)));
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
            double d = node.getDoubleValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assert(d == -1);
            } else if (fieldTypes[i].equals("double")) {
                assert(d == Double.MAX_VALUE);
            } else if (fieldTypes[i].equals("float")) {
                assert(d == Float.MAX_VALUE);
            } else if (fieldTypes[i].equals("int")) {
                assert(d == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("long")) {
                assert(d == Long.MAX_VALUE);
            } else if (fieldTypes[i].equals("string")) {
                assert(d == -1);
            } else {
                fail();
            }
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
            float f = node.getFloatValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assert(f == -1);
            } else if (fieldTypes[i].equals("double")) {
                assert("Infinity".equals(String.valueOf(f)));
            } else if (fieldTypes[i].equals("float")) {
                assert(f == Float.MAX_VALUE);
            } else if (fieldTypes[i].equals("int")) {
                assert(f == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("long")) {
                assert(f == Long.MAX_VALUE);
            } else if (fieldTypes[i].equals("string")) {
                assert(f == -1);
            } else {
                fail();
            }
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
            int integer = node.getIntValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assert(integer == -1);
            } else if (fieldTypes[i].equals("double")) {
                assert(integer == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("float")) {
                assert(integer == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("int")) {
                assert(integer == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("long")) {
                assert(integer == new Long(Long.MAX_VALUE).intValue());
            } else if (fieldTypes[i].equals("string")) {
                assert(integer == -1);
            } else {
                fail();
            }
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
            long l = node.getLongValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assert(l == -1);
            } else if (fieldTypes[i].equals("double")) {
                assert(l == Long.MAX_VALUE);
            } else if (fieldTypes[i].equals("float")) {
                assert(l == Long.MAX_VALUE);
            } else if (fieldTypes[i].equals("int")) {
                assert(l == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("long")) {
                assert(l == Long.MAX_VALUE);
            } else if (fieldTypes[i].equals("string")) {
                assert(l == -1);
            } else {
                fail();
            }
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
            String string = node.getStringValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assert("Hello world!".equals(string));
            } else if (fieldTypes[i].equals("double")) {
                assert(String.valueOf(Double.MAX_VALUE).equals(string));
            } else if (fieldTypes[i].equals("float")) {
                assert(String.valueOf(Float.MAX_VALUE).equals(string));
            } else if (fieldTypes[i].equals("int")) {
                assert(String.valueOf(Integer.MAX_VALUE).equals(string));
            } else if (fieldTypes[i].equals("long")) {
                assert(String.valueOf(Long.MAX_VALUE).equals(string));
            } else if (fieldTypes[i].equals("string")) {
                assert("Bridge testing!".equals(string));
            } else {
                fail();
            }
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
