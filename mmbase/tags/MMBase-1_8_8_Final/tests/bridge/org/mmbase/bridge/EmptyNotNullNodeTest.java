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
 * @version $Id: EmptyNotNullNodeTest.java,v 1.12 2005-11-05 08:55:20 michiel Exp $
 */
public class EmptyNotNullNodeTest extends EmptyNodeTest {

    private static final String EMPTY_XML = "<p/>";

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
            } else if (fieldTypes[i].equals("datatype")) {
                Date expected = new Date();
                long diff = Math.abs(expected.getTime() - (long)value);
                assertTrue("Empty " + fieldTypes[i] + " field queried as datetime did not return " + expected + ", but " + value + "(differs " + diff + ") value:" + node.getStringValue(fieldTypes[i] + "field"),  diff < 60000L); // allow for a minute differnce (duration of test or so..)
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
                Field field = node.getNodeManager().getField(fieldTypes[i] + "field");
                String dateValue = (String) field.getDataType().getProcessor(DataType.PROCESS_GET, Field.TYPE_STRING).process(node, field, Casting.toString(new Date(-1)));
                assertTrue("Empty " + fieldTypes[i] + " field queried as string did not return \"" + dateValue + "\", but \"" + value +"\" " , dateValue.equals(value));
            } else if (fieldTypes[i].equals("xml")) {
                // not-null 'empty' xml values return <p/>
                assertTrue("Empty " + fieldTypes[i] + " field queried as string did not return \"" + EMPTY_XML + "\", but \"" + value +"\" " +
                        "\n field: \"" + getBytesString(value.getBytes())+"\" teststring: \"" + getBytesString(EMPTY_XML.getBytes()) + "\"",
                           EMPTY_XML.equals(value));
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as string did not return an empty string, but \"" + value +"\"",
                    "".equals(value));
            }
        }
    }
    
    private String getBytesString(byte[] bytes){
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++){
            sb.append(bytes[i]);
            sb.append(",");
        }
      return sb.toString();
    }

    public void testGetXMLValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Document value = node.getXMLValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as XML returns null", value != null);
        }
    }

    public void testGetNodeValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Node value = node.getNodeValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("node")) {
                Node nodeValue =  getCloud().getNodeManager("bb");
                // not-null nodes MUST have a valid value.
                assertTrue(fieldTypes[i] + " field queried as node did not return \"bb\", but \"" + value +"\"", nodeValue.equals(value));
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as Node did not return null, but " + value, value == null);
            }
       }
    }

    public void testGetBooleanValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            boolean value = node.getBooleanValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("node")) {
                assertTrue("Empty " + fieldTypes[i] + " field queried as boolean did not return true, but " + value, value);
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as boolean did not return false, but " + value, !value);
            }
       }
    }

    public void testGetDateTimeValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Date value = node.getDateValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as datetime returned null", value != null);            
            Date expected = new Date(-1);
            assertTrue("Empty " + fieldTypes[i] + " field queried as datetime did not return " + expected + ", but " + value + " value:" + node.getStringValue(fieldTypes[i] + "field"),  value.equals(expected)); 
       }
    }

    public void testGetListValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            List value = node.getListValue(fieldTypes[i] + "field");
            assertTrue("Empty " + fieldTypes[i] + " field queried as list returned null", value != null);
            if (fieldTypes[i].equals("list")) {
                assertTrue("Empty " + fieldTypes[i] + " field queried as list did not return [], but " + value, value.size() == 0);
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as list did not return [<object>], but " + value, value.size() == 1);
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
