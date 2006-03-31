/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.text.*;
import java.util.*;
import org.w3c.dom.Document;

import org.mmbase.util.Casting;
import org.mmbase.datatypes.*;

/**
 * Like FilledNodeTest but the used builder is oddly configured.
 *
 * @author Michiel Meeuwissen
 * @since MMBaes-1.8
 */
public class ErroneousFilledNodeTest extends FilledNodeTest {

    public ErroneousFilledNodeTest(String name) {
        super(name);
    }

    protected String getNodeManager() {
        return "aaerrors";
    }

    public void setUp() {
        // Create a test node.
        Cloud cloud = getCloud();
        cloud.setLocale(Locale.US);
        node = cloud.getNodeManager(getNodeManager()).createNode();
        Node typedefNode = cloud.getNodeManager("bb");
        assertTrue(typedefNode != null);
        byte[] bytes = { 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100, 33 };
        node.setValue("bytefield", bytes);
        node.setValue("doublefield", new Float(Float.MAX_VALUE));
        node.setValue("floatfield", new Float(Float.MAX_VALUE));
        node.setValue("intfield", new Integer(Integer.MAX_VALUE));
        node.setValue("longfield", new Long(Long.MAX_VALUE));
        node.setValue("stringfield", "Bridge testing!");
        node.setValue("xmlfield", getEmptyDocument());
        node.setValue("nodefield", typedefNode);
        org.mmbase.datatypes.DataType dt = node.getNodeManager().getField("datetimefield").getDataType();
        //assertTrue("Not a datetime-datatype but " + dt.getClass(), dt.getClass().equals(org.mmbase.datatypes.DateTimeDataType.class)); // would give error in Node#setValue otherwise

        node.setValue("datetimefield", TEST_DATE);
        node.setValue("booleanfield", Boolean.TRUE);
        List list = new ArrayList();
        list.add(Boolean.TRUE);
        list.add(Boolean.TRUE);
        //node.setValue("listfield", list);
        node.commit();
    }

    public void testGetDoubleValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            double d = node.getDoubleValue(fieldTypes[i] + "field");
            if (fieldTypes[i].equals("byte")) {
                assertTrue(d == -1);
            } else if (fieldTypes[i].equals("double")) {
                assertTrue(d == Float.MAX_VALUE);
            } else if (fieldTypes[i].equals("float")) {
                assertTrue(d == Float.MAX_VALUE);
            } else if (fieldTypes[i].equals("int")) {
                assertTrue(d == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("long")) {
                assertTrue(d == Long.MAX_VALUE);
            } else if (fieldTypes[i].equals("string")) {
                assertTrue(d == -1);
            } else if (fieldTypes[i].equals("xml")) {
                assertTrue(d == -1);
            } else if (fieldTypes[i].equals("node")) {
                assertTrue(d == getCloud().getNodeManager("bb").getNumber());
            } else if (fieldTypes[i].equals("boolean")) {
                assertTrue(d == 1);
            } else if (fieldTypes[i].equals("datetime")) {
                assertTrue(fieldTypes[i] + "field queried as double did not return " + (double)TEST_TIME/1000 + " but " + d,
                        d == (double)TEST_TIME/1000);
            } else if (fieldTypes[i].equals("list")) {
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
                assertTrue(f == Float.MAX_VALUE);
            } else if (fieldTypes[i].equals("float")) {
                assertTrue(f == Float.MAX_VALUE);
            } else if (fieldTypes[i].equals("int")) {
                assertTrue(f == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("long")) {
                assertTrue(f == Long.MAX_VALUE);
            } else if (fieldTypes[i].equals("string")) {
                assertTrue(f == -1);
            } else if (fieldTypes[i].equals("xml")) {
                assertTrue(f == -1);
            } else if (fieldTypes[i].equals("node")) {
                assertTrue(f == getCloud().getNodeManager("bb").getNumber());
            } else if (fieldTypes[i].equals("boolean")) {
                assertTrue(f == 1);
            } else if (fieldTypes[i].equals("datetime")) {
                assertTrue(fieldTypes[i] + "field queried as float did not return " + (float)TEST_TIME/1000 + " but " + f,
                        f == (float)TEST_TIME/1000);
            } else if (fieldTypes[i].equals("list")) {
                assertTrue(f == -1);
            } else {
                fail();
            }
        }
    }

}
