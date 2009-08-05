/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.*;
import org.mmbase.util.Casting;
import org.mmbase.bridge.Field;
import org.mmbase.datatypes.DataType;
import org.w3c.dom.Document;

/**
 * Test class <code>Node</code> from the bridge package. The tests are done on
 * an empty node with 'notnull' fields.
 *
 * @author Michiel Meeuwissen
 * @version $Id: EmptyNotNullNodeTest.java,v 1.15 2008-12-01 22:41:58 michiel Exp $
 */
public class EmptyNotNullNodeTest extends EmptyNodeTest {

    private static final String EMPTY_XML = "<p/>";

    public EmptyNotNullNodeTest(String name) {
        super(name);
    }

    public void testGetValue() {
        for (String element : fieldTypes) {
            Object value = node.getValue(element + "field");
            //assertTrue("Field " + element + "field was expected to be marked 'notnull'", org.mmbase.module.core.MMBase.getMMBase().getBuilder(node.getNodeManager().getName()).getField(element + "field").isNotNull()); // don't use core, rmmci test-cases will fail

            assertTrue("Empty " + element + "field did return null, but the field is marked 'notnull'", value != null);
        }
    }

    public void testGetByteValue() {
        for (String element : fieldTypes) {
            byte[] bytes = node.getByteValue(element + "field");
            assertTrue("Empty " + element + " field queried as byte did not return [], but " + bytes,
                bytes.length == 0);
        }
    }

    public void testGetDoubleValue() {
        for (String element : fieldTypes) {
            double value = node.getDoubleValue(element + "field");
            if (element.equals("node")) {
                int nodeValue =  getCloud().getNodeManager("bb").getNumber();
                // not-null nodes MUST have a valid value.
                assertTrue(element + " field queried as double did not return " + nodeValue + ", but " + value,
                           value == nodeValue);
            } else if (element.equals("boolean")) {
                // not-null 'empty' booleans has value 0 (false)
                assertTrue("Empty " + element + " field queried as double did not return 0, but " + value,
                           value == 0);
            } else if (element.equals("datatype")) {
                Date expected = new Date();
                long diff = Math.abs(expected.getTime() - (long)value);
                assertTrue("Empty " + element + " field queried as datetime did not return " + expected + ", but " + value + "(differs " + diff + ") value:" + node.getStringValue(element + "field"),  diff < 60000L); // allow for a minute differnce (duration of test or so..)
            } else {
                assertTrue("Empty " + element + " field queried as double did not return -1, but " + value,
                           value == -1.0);
            }
        }
    }

    public void testGetFloatValue() {
        for (String element : fieldTypes) {
            float value = node.getFloatValue(element + "field");
            if (element.equals("node")) {
                int nodeValue =  getCloud().getNodeManager("bb").getNumber();
                // not-null nodes MUST have a valid value.
                assertTrue(element + " field queried as float did not return " + nodeValue + ", but " + value,
                        value == nodeValue);
            } else if (element.equals("boolean")) {
                // not-null 'empty' booleans has value 0 (false)
                assertTrue("Empty " + element + " field queried as float did not return 0, but " + value,
                            value == 0.0);
            } else {
                assertTrue("Empty " + element + " field queried as float did not return -1, but " + value,
                        value == -1.0);
            }
        }
    }

    public void testGetIntValue() {
        for (String element : fieldTypes) {
            int value = node.getIntValue(element + "field");
            if (element.equals("node")) {
                int nodeValue =  getCloud().getNodeManager("bb").getNumber();
                // not-null nodes MUST have a valid value.
                assertTrue(element + " field queried as integer did not return " + nodeValue + ", but " + value,
                        value == nodeValue);
            } else if (element.equals("boolean")) {
                // not-null 'empty' booleans has value 0 (false)
                assertTrue("Empty " + element + " field queried as integer did not return 0, but " + value,
                            value == 0);
            } else {
                assertTrue("Empty " + element + " field queried as integer did not return -1, but " + value,
                        value == -1);
            }
        }
    }

    public void testGetLongValue() {
        for (String element : fieldTypes) {
            long value = node.getLongValue(element + "field");
            if (element.equals("node")) {
                int nodeValue =  getCloud().getNodeManager("bb").getNumber();
                // not-null nodes MUST have a valid value.
                assertTrue(element + " field queried as long did not return " + nodeValue + ", but " + value,
                        value == nodeValue);
            } else if (element.equals("boolean")) {
                // not-null 'empty' booleans has value 0 (false)
                assertTrue("Empty " + element + " field queried as long did not return 0, but " + value,
                            value == 0);
            } else {
                assertTrue("Empty " + element + " field queried as long did not return -1, but " + value,
                        value == -1);
            }
        }
    }

    public void testGetStringValue() {
        for (String element : fieldTypes) {
            String value = node.getStringValue(element + "field");
            if (element.equals("node")) {
                String nodeValue =  ""+getCloud().getNodeManager("bb").getNumber();
                // not-null nodes MUST have a valid value.
                assertTrue(element + " field queried as string did not return \"" + nodeValue + "\", but \"" + value +"\"",
                    nodeValue.equals(value));
            } else if (element.equals("boolean")) {
                // not-null 'empty' booleans has value "false"
                assertTrue("Empty " + element + " field queried as string did not return \"false\", but \"" + value +"\"",
                    "false".equals(value));
            } else if (element.equals("long") || element.equals("int") || element.equals("decimal")) {
                // not-null 'empty' numerics has value "-1"
                assertTrue("Empty " + element + " field queried as string did not return \"-1\", but \"" + value +"\"",
                    "-1".equals(value));
            } else if (element.equals("double") || element.equals("float")) {
                // not-null 'empty' numerics has value "-1"
                assertTrue("Empty " + element + " field queried as string did not return \"-1.0\", but \"" + value +"\"",
                    "-1.0".equals(value));
            } else if (element.equals("datetime")) {
                // not-null 'empty' dates return a date string
                Field field = node.getNodeManager().getField(element + "field");
                String dateValue = (String) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_STRING).process(node, field, Casting.toString(new Date(-1)));
                assertTrue("Empty " + element + " field queried as string did not return \"" + dateValue + "\", but \"" + value +"\" " , dateValue.equals(value));
            } else if (element.equals("xml")) {
                // not-null 'empty' xml values return <p/>
                assertTrue("Empty " + element + " field queried as string did not return \"" + EMPTY_XML + "\", but \"" + value +"\" " +
                        "\n field: \"" + getBytesString(value.getBytes())+"\" teststring: \"" + getBytesString(EMPTY_XML.getBytes()) + "\"",
                           EMPTY_XML.equals(value));
            } else {
                assertTrue("Empty " + element + " field queried as string did not return an empty string, but \"" + value +"\"",
                    "".equals(value));
            }
        }
    }

    private String getBytesString(byte[] bytes){
        StringBuffer sb = new StringBuffer();
        for (byte element : bytes) {
            sb.append(element);
            sb.append(",");
        }
      return sb.toString();
    }

    public void testGetXMLValue() {
        for (String element : fieldTypes) {
            Document value = node.getXMLValue(element + "field");
            assertTrue("Empty " + element + " field queried as XML returns null", value != null);
        }
    }

    public void testGetNodeValue() {
        for (String element : fieldTypes) {
            Node value = node.getNodeValue(element + "field");
            if (element.equals("node")) {
                Node nodeValue =  getCloud().getNodeManager("bb");
                // not-null nodes MUST have a valid value.
                assertTrue(element + " field queried as node did not return \"bb\", but \"" + value +"\"", nodeValue.equals(value));
            } else {
                assertTrue("Empty " + element + " field queried as Node did not return null, but " + value, value == null);
            }
       }
    }

    public void testGetBooleanValue() {
        for (String element : fieldTypes) {
            boolean value = node.getBooleanValue(element + "field");
            if (element.equals("node")) {
                assertTrue("Empty " + element + " field queried as boolean did not return true, but " + value, value);
            } else {
                assertTrue("Empty " + element + " field queried as boolean did not return false, but " + value, !value);
            }
       }
    }

    public void testGetDateTimeValue() {
        for (String element : fieldTypes) {
            Date value = node.getDateValue(element + "field");
            assertTrue("Empty " + element + " field queried as datetime returned null", value != null);
            Date expected = new Date(-1);
            assertTrue("Empty " + element + " field queried as datetime did not return " + expected + ", but " + value + " value:" + node.getStringValue(element + "field"),  value.equals(expected));
       }
    }

    public void testGetListValue() {
        for (String element : fieldTypes) {
            List value = node.getListValue(element + "field");
            assertTrue("Empty " + element + " field queried as list returned null", value != null);
            if (element.equals("list")) {
                assertTrue("Empty " + element + " field queried as list did not return [], but " + value, value.size() == 0);
            } else {
                assertTrue("Empty " + element + " field queried as list did not return [<object>], but " + value, value.size() == 1);
            }
       }
    }


    public void setUp() {
        // Create a empty test node.
        Cloud cloud = getCloud();
        node = cloud.getNodeManager("xx").createNode();
        assertTrue(cloud.hasNode(cloud.getNodeManager("bb").getNumber()));
        // not-null node-field _must_ be filled because of referential integrity conflicts
        node.setValue("nodefield", cloud.getNodeManager("bb"));
        node.commit();
    }

}
