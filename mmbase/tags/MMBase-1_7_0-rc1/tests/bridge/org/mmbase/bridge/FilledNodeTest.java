/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * Test class <code>Node</code> from the bridge package. The tests are done on
 * a filled node.
 *
 * @author Jaco de Groot
 * @author Michiel Meeuwissen
 */
public class FilledNodeTest extends NodeTest {

    public FilledNodeTest(String name) {
        super(name);
    }

    public void setUp() {
        // Create a test node.
        Cloud cloud = getCloud();
        node = cloud.getNodeManager("aa").createNode();
        byte[] bytes = { 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100, 33 };
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
        node.delete();
    }

    public void testGetValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Object object = node.getValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                byte[] bytes = (byte[])object;
                assertTrue("Hello world!".equals(new String(bytes)));
            } else if (fieldTypes[i].equals("double")) {
                assertTrue(new Double(Double.MAX_VALUE).compareTo((Double)object) == 0);
            } else if (fieldTypes[i].equals("float")) {
                assertTrue(new Float(Float.MAX_VALUE).compareTo((Float)object) == 0);
            } else if (fieldTypes[i].equals("int")) {
                assertTrue(new Integer(Integer.MAX_VALUE).compareTo((Integer)object) == 0);
            } else if (fieldTypes[i].equals("long")) {
                assertTrue(new Long(Long.MAX_VALUE).compareTo((Long)object) == 0);
            } else if (fieldTypes[i].equals("string")) {
                assertTrue("Bridge testing!".equals((String)object));
            } else {
                fail();
            }
        }
    }

    public void testGetByteValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            byte[] bytes = node.getByteValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                byte[] check = { 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100, 33 };
                for (int j = 0; j < bytes.length; j++) {
                    assertTrue(bytes[j] == check[j]);
                }
            } else if (fieldTypes[i].equals("double")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("float")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("int")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("long")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("string")) {
                assertTrue("Bridge testing!".equals(new String(bytes)));
            } else {
                fail();
            }
        }
    }

    public void testGetDoubleValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            double d = node.getDoubleValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assertTrue(d == -1);
            } else if (fieldTypes[i].equals("double")) {
                assertTrue(d == Double.MAX_VALUE);
            } else if (fieldTypes[i].equals("float")) {
                assertTrue(d == Float.MAX_VALUE);
            } else if (fieldTypes[i].equals("int")) {
                assertTrue(d == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("long")) {
                assertTrue(d == Long.MAX_VALUE);
            } else if (fieldTypes[i].equals("string")) {
                assertTrue(d == -1);
            } else {
                fail();
            }
        }
    }

    public void testGetFloatValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            float f = node.getFloatValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assertTrue(f == -1);
            } else if (fieldTypes[i].equals("double")) {
                assertTrue("Infinity".equals(String.valueOf(f)));
            } else if (fieldTypes[i].equals("float")) {
                assertTrue(f == Float.MAX_VALUE);
            } else if (fieldTypes[i].equals("int")) {
                assertTrue(f == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("long")) {
                assertTrue(f == Long.MAX_VALUE);
            } else if (fieldTypes[i].equals("string")) {
                assertTrue(f == -1);
            } else {
                fail();
            }
        }
    }

    public void testGetIntValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            int integer = node.getIntValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assertTrue(integer == -1);
            } else if (fieldTypes[i].equals("double")) {
                assertTrue(integer == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("float")) {
                assertTrue(integer == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("int")) {
                assertTrue(integer == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("long")) {
                assertTrue(integer == new Long(Long.MAX_VALUE).intValue());
            } else if (fieldTypes[i].equals("string")) {
                assertTrue(integer == -1);
            } else {
                fail();
            }
        }
    }

    public void testGetLongValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            long l = node.getLongValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assertTrue(l == -1);
            } else if (fieldTypes[i].equals("double")) {
                assertTrue(l == Long.MAX_VALUE);
            } else if (fieldTypes[i].equals("float")) {
                assertTrue(l == Long.MAX_VALUE);
            } else if (fieldTypes[i].equals("int")) {
                assertTrue(l == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("long")) {
                assertTrue(l == Long.MAX_VALUE);
            } else if (fieldTypes[i].equals("string")) {
                assertTrue(l == -1);
            } else {
                fail();
            }
        }
    }

    public void testGetStringValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            String string = node.getStringValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assertTrue("Hello world!".equals(string));
            } else if (fieldTypes[i].equals("double")) {
                assertTrue(String.valueOf(Double.MAX_VALUE).equals(string));
            } else if (fieldTypes[i].equals("float")) {
                assertTrue(String.valueOf(Float.MAX_VALUE).equals(string));
            } else if (fieldTypes[i].equals("int")) {
                assertTrue(String.valueOf(Integer.MAX_VALUE).equals(string));
            } else if (fieldTypes[i].equals("long")) {
                assertTrue(String.valueOf(Long.MAX_VALUE).equals(string));
            } else if (fieldTypes[i].equals("string")) {
                assertTrue("Bridge testing!".equals(string));
            } else {
                fail();
            }
        }
    }

}
