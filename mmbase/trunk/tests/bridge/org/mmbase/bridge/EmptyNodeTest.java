/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.w3c.dom.Document;
import java.util.*;

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
            Object value = node.getValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field did not return null, but " + value + " a " + (value == null ? "" : value.getClass().getName()), value == null);
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
            } else if (fieldTypes[i].equals("xml")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("node")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("boolean")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("datetime")) {
                assertTrue(bytes.length == 0);
            } else if (fieldTypes[i].equals("list")) {
                assertTrue(bytes.length == 0);
            } else {
                fail("Unknown fieldtype encountered " + fieldTypes[i]);
            }
        }
    }

    public void testGetDoubleValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            double value = node.getDoubleValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as double did not return -1, but " + value,
                        value == -1);
        }
    }

    public void testGetFloatValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            float value = node.getFloatValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as float did not return -1, but " + value,
                        value == -1);
        }
    }

    public void testGetIntValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            int value = node.getIntValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as integer did not return -1, but " + value,
                        value == -1);
        }
    }

    public void testGetLongValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            long value = node.getLongValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as long did not return -1, but " + value,
                        value == -1);
        }
    }

    public void testGetStringValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Object value = node.getStringValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as string did not return an empty string, but " + value, "".equals(value));
        }
    }

    public void testGetXMLValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Document value = node.getXMLValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as XML not null (as javadoc sais it should) but '" + value + "'",
                value == null);
        }
    }

    public void testGetNodeValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Node value = node.getNodeValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as Node did not return null, but " + value,
                        value == null);
       }
    }

    public void testGetBooleanValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            boolean value = node.getBooleanValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as boolean did not return false, but " + value,
                        !value);
       }
    }

    public void testGetDateTimeValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Date value = node.getDateValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as datetime returned null", value != null);
            assertTrue("Empty " + fieldTypes[i] + " field queried as datetime did not return "+new Date(-1)+", but " + value,
                        value.getTime()==-1);
       }
    }

    public void testGetListValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            List value = node.getListValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as list returned null", value != null);
            assertTrue("Empty " + fieldTypes[i] + " field queried as list did not return [], but " + value, value.size() == 0);
       }
    }

}
