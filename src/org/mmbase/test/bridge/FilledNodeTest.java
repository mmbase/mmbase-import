/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.test.bridge;

import java.util.Iterator;
import junit.framework.*;
import org.mmbase.bridge.*;

public class FilledNodeTest extends NodeTest {

    public FilledNodeTest(String name) {
        super(name);
    }

    protected Cloud getCloud() {
        return  LocalContext.getCloudContext().getCloud("mmbase");
    }

    public void setUp() {
        // Create a test node.
        Cloud cloud = getCloud();
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
        node.delete();
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

}
