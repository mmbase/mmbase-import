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
        for (String element : fieldTypes) {
            Object value = node.getValue(element + "field");
            assertTrue("Empty " + element + " field did not return null, but " + value + " a " + (value == null ? "" : value.getClass().getName()), value == null);
        }
    }

    public void testGetBinaryValue() {
        for (String element : fieldTypes) {
            byte[] bytes = node.getByteValue(element + "field");
            if (element.equals("binary")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("double")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("float")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("int")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("long")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("string")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("xml")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("node")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("boolean")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("datetime")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("list")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("decimal")) {
                assertTrue(bytes.length == 0);
            } else {
                fail("Unknown fieldtype encountered " + element);
            }
        }
    }

    public void testGetDoubleValue() {
        for (String element : fieldTypes) {
            double value = node.getDoubleValue(element + "field");
            assertTrue("Empty " + element + " field queried as double did not return -1, but " + value,
                        value == -1);
        }
    }

    public void testGetFloatValue() {
        for (String element : fieldTypes) {
            float value = node.getFloatValue(element + "field");
            assertTrue("Empty " + element + " field queried as float did not return -1, but " + value,
                        value == -1);
        }
    }

    public void testGetIntValue() {
        for (String element : fieldTypes) {
            int value = node.getIntValue(element + "field");
            assertTrue("Empty " + element + " field queried as integer did not return -1, but " + value,
                        value == -1);
        }
    }

    public void testGetLongValue() {
        for (String element : fieldTypes) {
            long value = node.getLongValue(element + "field");
            assertTrue("Empty " + element + " field queried as long did not return -1, but " + value,
                        value == -1);
        }
    }

    public void testGetStringValue() {
        for (String element : fieldTypes) {
            Object value = node.getStringValue(element + "field");
            assertTrue("Empty " + element + " field queried as string did not return an empty string, but " + value, "".equals(value));
        }
    }

    public void testGetXMLValue() {
        for (String element : fieldTypes) {
            Document value = node.getXMLValue(element + "field");
            assertTrue("Empty " + element + " field queried as XML not null (as javadoc sais it should) but '" + value + "'",
                value == null);
        }
    }

    public void testGetNodeValue() {
        for (String element : fieldTypes) {
            Node value = node.getNodeValue(element + "field");
            assertTrue("Empty " + element + " field queried as Node did not return null, but " + value,
                        value == null);
       }
    }

    public void testGetBooleanValue() {
        for (String element : fieldTypes) {
            boolean value = node.getBooleanValue(element + "field");
            assertTrue("Empty " + element + " field queried as boolean did not return false, but " + value,
                        !value);
       }
    }

    public void testGetDateTimeValue() {
        for (String element : fieldTypes) {
            Date value = node.getDateValue(element + "field");
            assertTrue("Empty " + element + " field queried as datetime returned null", value != null);
            assertTrue("Empty " + element + " field queried as datetime did not return "+new Date(-1)+", but " + value,
                        value.getTime()==-1);
       }
    }

    public void testGetDecimalValue() {
        for (String element : fieldTypes) {
            java.math.BigDecimal value = node.getDecimalValue(element + "field");
            assertTrue("Empty " + element + " field queried as datetime returned null", value != null);
       }
    }

    public void testGetListValue() {
        for (String element : fieldTypes) {
            List value = node.getListValue(element + "field");
            assertTrue("Empty " + element + " field queried as list returned null", value != null);
            assertTrue("Empty " + element + " field queried as list did not return [], but " + value, value.size() == 0);
       }
    }

    public void testSetField() {
        node.setValue("stringfield", "");
        node.commit();
        testGetStringValue();
    }

}
