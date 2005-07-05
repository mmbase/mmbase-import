/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.*;
import org.mmbase.util.Casting;
import org.w3c.dom.Document;

/**
 * Test class <code>Node</code> from the bridge package. The tests are done on
 * an empty node with 'notnull' fields.
 *
 * @author Michiel Meeuwissen
 */
public class EmptyNotNullNodeTest extends EmptyNodeTest {

    private static final String EMPTY_XML = "<p/>\n";

    public EmptyNotNullNodeTest(String name) {
        super(name);
    }

    public void testGetValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Object value = node.getValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field did return null, but the field is marked 'notnull'", value != null);
        }
    }

    public void testGetByteValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            byte[] bytes = node.getByteValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as byte did not return [], but " + bytes,
                bytes.length == 0);
        }
    }

    public void testGetDoubleValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            double value = node.getDoubleValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("node")) {
                int nodeValue =  getCloud().getNodeManager("bb").getNumber();
                // not-null nodes MUST have a valid value.
                assertTrue(fieldTypes[i] + " field queried as double did not return " + nodeValue + ", but " + value,
                        value == nodeValue);
            } else if (fieldTypes[i].equals("boolean")) {
                // not-null 'empty' booleans has value 0 (false)
                assertTrue("Empty " + fieldTypes[i] + " field queried as double did not return 0, but " + value,
                            value == 0);
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as double did not return -1, but " + value,
                            value == -1.0);
            }
        }
    }

    public void testGetFloatValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            float value = node.getFloatValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("node")) {
                int nodeValue =  getCloud().getNodeManager("bb").getNumber();
                // not-null nodes MUST have a valid value.
                assertTrue(fieldTypes[i] + " field queried as float did not return " + nodeValue + ", but " + value,
                        value == nodeValue);
            } else if (fieldTypes[i].equals("boolean")) {
                // not-null 'empty' booleans has value 0 (false)
                assertTrue("Empty " + fieldTypes[i] + " field queried as float did not return 0, but " + value,
                            value == 0.0);
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as float did not return -1, but " + value,
                        value == -1.0);
            }
        }
    }

    public void testGetIntValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            int value = node.getIntValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("node")) {
                int nodeValue =  getCloud().getNodeManager("bb").getNumber();
                // not-null nodes MUST have a valid value.
                assertTrue(fieldTypes[i] + " field queried as integer did not return " + nodeValue + ", but " + value,
                        value == nodeValue);
            } else if (fieldTypes[i].equals("boolean")) {
                // not-null 'empty' booleans has value 0 (false)
                assertTrue("Empty " + fieldTypes[i] + " field queried as integer did not return 0, but " + value,
                            value == 0);
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as integer did not return -1, but " + value,
                        value == -1);
            }
        }
    }

    public void testGetLongValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            long value = node.getLongValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("node")) {
                int nodeValue =  getCloud().getNodeManager("bb").getNumber();
                // not-null nodes MUST have a valid value.
                assertTrue(fieldTypes[i] + " field queried as long did not return " + nodeValue + ", but " + value,
                        value == nodeValue);
            } else if (fieldTypes[i].equals("boolean")) {
                // not-null 'empty' booleans has value 0 (false)
                assertTrue("Empty " + fieldTypes[i] + " field queried as long did not return 0, but " + value,
                            value == 0);
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as long did not return -1, but " + value,
                        value == -1);
            }
        }
    }

    public void testGetStringValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            String value = node.getStringValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("node")) {
                String nodeValue =  ""+getCloud().getNodeManager("bb").getNumber();
                // not-null nodes MUST have a valid value.
                assertTrue(fieldTypes[i] + " field queried as string did not return \"" + nodeValue + "\", but \"" + value +"\"",
                    nodeValue.equals(value));
            } else if (fieldTypes[i].equals("boolean")) {
                // not-null 'empty' booleans has value "false"
                assertTrue("Empty " + fieldTypes[i] + " field queried as string did not return \"false\", but \"" + value +"\"",
                    "false".equals(value));
            } else if (fieldTypes[i].equals("long") || fieldTypes[i].equals("int")) {
                // not-null 'empty' numerics has value "-1"
                assertTrue("Empty " + fieldTypes[i] + " field queried as string did not return \"-1\", but \"" + value +"\"",
                    "-1".equals(value));
            } else if (fieldTypes[i].equals("double") || fieldTypes[i].equals("float")) {
                // not-null 'empty' numerics has value "-1"
                assertTrue("Empty " + fieldTypes[i] + " field queried as string did not return \"-1.0\", but \"" + value +"\"",
                    "-1.0".equals(value));
            } else if (fieldTypes[i].equals("datetime")) {
                // not-null 'empty' dates return a date string
                String dateValue = Casting.toString(new Date(-1));
                assertTrue("Empty " + fieldTypes[i] + " field queried as string did not return \"" + dateValue + "\", but \"" + value +"\"",
                    dateValue.equals(value));
            } else if (fieldTypes[i].equals("xml")) {
                // not-null 'empty' xml values return <p/>
                assertTrue("Empty " + fieldTypes[i] + " field queried as string did not return \"" + EMPTY_XML + "\", but \"" + value +"\"",
                           EMPTY_XML.equals(value));
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as string did not return an empty string, but \"" + value +"\"",
                    "".equals(value));
            }
        }
    }

    public void testGetXMLValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Document value = node.getXMLValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as XML returns null",value !=null);
            if (fieldTypes[i].equals("node") || fieldTypes[i].equals("long") || fieldTypes[i].equals("int") ||
                fieldTypes[i].equals("double") || fieldTypes[i].equals("float") || fieldTypes[i].equals("boolean")
                || fieldTypes[i].equals("list") || fieldTypes.equals("datetime") ) {
                assertTrue("Empty " + fieldTypes[i] + " field queried as XML does not give an mmxf document but '" + value.getDoctype() + "'",
                    value.getDoctype().getName().equals("mmxf"));
            }
        }
    }

    public void testGetNodeValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Node value = node.getNodeValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("node")) {
                Node nodeValue =  getCloud().getNodeManager("bb");
                // not-null nodes MUST have a valid value.
                assertTrue(fieldTypes[i] + " field queried as node did not return \"bb\", but \"" + value +"\"",
                    nodeValue.equals(value));
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as Node did not return null, but " + value,
                            value == null);
            }
       }
    }

    public void testGetBooleanValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            boolean value = node.getBooleanValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("node")) {
                assertTrue("Empty " + fieldTypes[i] + " field queried as boolean did not return true, but " + value,
                            value);
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as boolean did not return false, but " + value,
                            !value);
            }
       }
    }

    public void testGetDateTimeValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Date value = node.getDateValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as datetime returned null", value!=null);
            assertTrue("Empty " + fieldTypes[i] + " field queried as datetime did not return "+new Date(-1)+", but " + value,
                        value.getTime()==-1);
       }
    }

    public void testGetListValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            List value = node.getListValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as list returned null", value!=null);
            if (fieldTypes[i].equals("list")) {
                assertTrue("Empty " + fieldTypes[i] + " field queried as list did not return [], but " + value,
                            value.size() == 0);
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as list did not return [<object>], but " + value,
                            value.size() == 1);
            }
       }
    }


    public void setUp() {
        // Create a empty test node.
        Cloud cloud = getCloud();
        node = cloud.getNodeManager("xx").createNode();
        // not-null node-field _must_ be filled because of referential integrity conflicts
        node.setValue("nodefield", cloud.getNodeManager("bb"));
        node.commit();
    }

}
