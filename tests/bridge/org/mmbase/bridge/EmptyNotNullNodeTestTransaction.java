/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import java.util.*;
import org.w3c.dom.Document;

/**
 * Test class <code>Node</code> from the bridge package. The tests are done on
 * an empty node inside a transaction.
 *
 * @author Michiel Meeuwissen
 */
public class EmptyNotNullNodeTestTransaction extends EmptyNotNullNodeTest {

    public EmptyNotNullNodeTestTransaction(String name) {
        super(name);
    }

    protected Cloud getCloud() {
        return super.getCloud().getTransaction("test_transaction");
    }

    public void testGetValue() {
        // normally, a node with not-null fields would have it's field be set to non-null values
        // when the node is committed. However this does NOT work on a node in a transaction
        for (int i = 0; i < fieldTypes.length; i++) {
            Object value = node.getValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("node")) {
                assertTrue(fieldTypes[i] + " field returned null", value != null);
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field did not return null, but \"" + value +"\"",
                    value == null);
            }
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
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as string did not return an empty string, but \"" + value +"\"",
                    "".equals(value));
            }
        }
    }

    public void testGetXMLValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Document value = node.getXMLValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("node")) {
                assertTrue("Empty " + fieldTypes[i] + " field queried as XML returns null",value !=null);
                assertTrue("Empty " + fieldTypes[i] + " field queried as XML does not give an mmxf document but '" + value.getDoctype() + "'",
                    value.getDoctype().getName().equals("mmxf"));
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as XML does not return null but " +value ,
                    value == null);
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
            if (fieldTypes[i].equals("node")) {
                assertTrue("Empty " + fieldTypes[i] + " field queried as list did not return [<node>], but " + value,
                            value.size() == 1);
            } else {
                assertTrue("Empty " + fieldTypes[i] + " field queried as list did not return [], but " + value,
                            value.size() == 0);
            }
       }
    }


    public void tearDown() {
        // simply roll back transaction
        Transaction trans = (Transaction) getCloud();
        trans.cancel();
    }

}
