/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

/**
 * Test class <code>Node</code> from the bridge package. The tests are done on
 * an empty node.
 *
 * @author Jaco de Groot
 * @author Michiel Meeuwissen
 */
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
        node.delete();
    }

    public void testGetValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            if (fieldTypes[i].equals("byte")) {
                byte[] bytes = (byte[]) node.getValue(fieldTypes[i] + "field");                
                assertTrue("Empty " + fieldTypes[i] + " field did return null (should be empty byte[])", bytes != null);
                assertTrue(bytes.length == 0);
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field did not return null", node.getValue(fieldTypes[i] + "field") == null);
            }
        }
    }

    public void testGetByteValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            byte[] bytes = node.getByteValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("double")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("float")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("int")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("long")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("string")) {
                assertTrue(bytes.length == 0);
            } else {
                fail();
            }
        }
    }
    public void testGetDoubleValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assertTrue(node.getDoubleValue(fieldTypes[i] + "field") == -1);
        }
    }

    public void testGetFloatValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assertTrue(node.getFloatValue(fieldTypes[i] + "field") == -1);
        }
    }

    public void testGetIntValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assertTrue(node.getIntValue(fieldTypes[i] + "field") == -1);
        }
    }

    public void testGetLongValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assertTrue(node.getLongValue(fieldTypes[i] + "field") == -1);
        }
    }

    public void testGetStringValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            assertTrue("".equals(node.getStringValue(fieldTypes[i] + "field")));
        }
    }

}
