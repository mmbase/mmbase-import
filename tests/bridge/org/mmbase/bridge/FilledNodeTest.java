/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.w3c.dom.Document;
import org.mmbase.util.Casting;
import java.util.*;

/**
 * Test class <code>Node</code> from the bridge package. The tests are done on
 * a filled node.
 *
 * @author Jaco de Groot
 * @author Michiel Meeuwissen
 */
public class FilledNodeTest extends NodeTest {

    protected long TEST_TIME = (long) 20 * 356 * 24 * 60 * 60 * 1000;
    protected Date TEST_DATE = new Date(TEST_TIME);

    public FilledNodeTest(String name) {
        super(name);
    }

    protected Document getEmptyDocument() {
        try {
            javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            dfactory.setNamespaceAware(true);
            javax.xml.parsers.DocumentBuilder   documentBuilder = dfactory.newDocumentBuilder();
            org.mmbase.bridge.util.xml.Generator generator = new org.mmbase.bridge.util.xml.Generator(documentBuilder);
            return generator.getDocument();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    protected String getNodeManager() {
        return "aa";
    }

    public void setUp() {
        // Create a test node.
        Cloud cloud = getCloud();
        node = cloud.getNodeManager(getNodeManager()).createNode();
        Node typedefNode = cloud.getNodeManager("bb");
        assertTrue(typedefNode != null);
        byte[] bytes = { 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100, 33 };
        node.setValue("bytefield", bytes);
        node.setValue("doublefield", new Double(Double.MAX_VALUE));
        node.setValue("floatfield", new Float(Float.MAX_VALUE));
        node.setValue("intfield", new Integer(Integer.MAX_VALUE));
        node.setValue("longfield", new Long(Long.MAX_VALUE));
        node.setValue("stringfield", "Bridge testing!");
        node.setValue("xmlfield", getEmptyDocument());
        node.setValue("nodefield", typedefNode);
        org.mmbase.datatypes.DataType dt = node.getNodeManager().getField("datetimefield").getDataType();
        assertTrue("Not a datetime-datatype but " + dt.getClass(), dt.getClass().equals(org.mmbase.datatypes.DateTimeDataType.class)); // would give error in Node#setValue otherwise

        node.setValue("datetimefield", TEST_DATE);
        node.setValue("booleanfield", Boolean.TRUE);
        List list = new ArrayList();
        list.add(Boolean.TRUE);
        list.add(Boolean.TRUE);
        //node.setValue("listfield", list);
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
                assertTrue("getValue on byte field should give 'Hello World!' but gave '" + new String(bytes) + "'",
                    "Hello world!".equals(new String(bytes)));
            } else if (fieldTypes[i].equals("double")) {
                assertTrue("getValue on double field should give " +  Double.MAX_VALUE + " but gave " + object,
                    new Double(Double.MAX_VALUE).compareTo((Double)object) == 0);
            } else if (fieldTypes[i].equals("float")) {
                assertTrue("getValue on float field should give " +  Float.MAX_VALUE + " but gave " + object,
                    new Float(Float.MAX_VALUE).compareTo((Float)object) == 0);
            } else if (fieldTypes[i].equals("int")) {
                assertTrue("getValue on int field should give " +  Integer.MAX_VALUE + " but gave " + object,
                    new Integer(Integer.MAX_VALUE).compareTo((Integer)object) == 0);
            } else if (fieldTypes[i].equals("long")) {
                assertTrue("getValue on long field should give " +  Long.MAX_VALUE + " but gave " + object,
                    new Long(Long.MAX_VALUE).compareTo((Long)object) == 0);
            } else if (fieldTypes[i].equals("string")) {
                assertTrue("getValue on string field should give \"Bridge testing!\" but gave " + object,
                    "Bridge testing!".equals(object));
            } else if (fieldTypes[i].equals("xml")) {
                //   assertTrue(getEmptyDocument().isEqualNode((org.w3c.dom.Node)object)); java 1.5
                assertTrue("getValue on xml field should give ??? but gave ",
                    Casting.toString(getEmptyDocument()).equals(Casting.toString(object)));
            } else if (fieldTypes[i].equals("node")) {
                Node typedefNode = getCloud().getNodeManager("bb");
                assertTrue("getValue on node field should give " + typedefNode.getNumber() +" but gave " + ((Node) object).getNumber(),
                      ((Node) object).getNumber() == typedefNode.getNumber());
            } else if (fieldTypes[i].equals("datetime")) {
                assertTrue("getValue on datetime field should give " + TEST_DATE +" but gave " + object,
                    TEST_DATE.equals(object));
            } else if (fieldTypes[i].equals("boolean")) {
                assertTrue("getValue on boolean field should give TRUE but gave " + object,object.equals(Boolean.TRUE));
            } else if (fieldTypes[i].equals("list")) {
                assertTrue("getValue on list field should give [TRUE,TRUE] but gave " + object,
                    object instanceof List && ((List)object).size()==2 &&
                    ((List)object).get(0).equals(Boolean.TRUE) &&
                    ((List)object).get(1).equals(Boolean.TRUE));
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
                assertTrue("Infinity".equals(String.valueOf(f)));
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
                assertTrue(integer == Integer.MAX_VALUE);
            } else if (fieldTypes[i].equals("string")) {
                assertTrue(integer == -1);
            } else if (fieldTypes[i].equals("xml")) {
                assertTrue(integer == -1);
            } else if (fieldTypes[i].equals("node")) {
                assertTrue(integer == getCloud().getNodeManager("bb").getNumber());
            } else if (fieldTypes[i].equals("boolean")) {
                assertTrue(integer == 1);
            } else if (fieldTypes[i].equals("datetime")) {
                assertTrue(fieldTypes[i] + "field queried as double did not return " + TEST_TIME / 1000 + " but " + integer,
                        integer == (int) (TEST_TIME / 1000));
            } else if (fieldTypes[i].equals("list")) {
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
            } else if (fieldTypes[i].equals("xml")) {
                assertTrue(l == -1);
            } else if (fieldTypes[i].equals("node")) {
                assertTrue(l == getCloud().getNodeManager("bb").getNumber());
            } else if (fieldTypes[i].equals("boolean")) {
                assertTrue(l == 1);
            } else if (fieldTypes[i].equals("datetime")) {
                assertTrue(fieldTypes[i] + "field queried as double did not return " + TEST_TIME/1000 + " but " + l,
                    l == TEST_TIME/1000);
            } else if (fieldTypes[i].equals("list")) {
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
                assertTrue(fieldTypes[i] + "field queried as string did not return \"Hello world!\" but " + string, "Hello world!".equals(string));
            } else if (fieldTypes[i].equals("double")) {
                assertTrue(fieldTypes[i] + "field queried as string did not return " + Double.MAX_VALUE + " but " + string,
                    String.valueOf(Double.MAX_VALUE).equals(string));
            } else if (fieldTypes[i].equals("float")) {
                // SQLDB causes some problems when rounding floats, which it stores internally as Doubles.
                // so compare the resulting string to both Float.MAX_VALUE and Double(Float.MAX_VALUE )
                // to cover this. Note that this somehow only applies when comparing strings.
                assertTrue(fieldTypes[i] + "field queried as string did not return " + Float.MAX_VALUE + " but " + string,
                    String.valueOf(new Double(Float.MAX_VALUE)).equals(string) || String.valueOf(Float.MAX_VALUE).equals(string));
            } else if (fieldTypes[i].equals("int")) {
                assertTrue(fieldTypes[i] + "field queried as string did not return " + Integer.MAX_VALUE + " but " + string,
                    String.valueOf(Integer.MAX_VALUE).equals(string));
            } else if (fieldTypes[i].equals("long")) {
                assertTrue(fieldTypes[i] + "field queried as string did not return " + Long.MAX_VALUE + " but " + string,
                    String.valueOf(Long.MAX_VALUE).equals(string));
            } else if (fieldTypes[i].equals("string")) {
                assertTrue(fieldTypes[i] + "field queried as string did not return \"Bridge testing!\" but " + string,
                    "Bridge testing!".equals(string));
            } else if (fieldTypes[i].equals("xml")) {
                assertTrue(fieldTypes[i] + "field" +" field queried as string did not return empty document but " + string,
                    Casting.toString(getEmptyDocument()).equals(string));
            } else if (fieldTypes[i].equals("node")) {
                int number = getCloud().getNodeManager("bb").getNumber();
                assertTrue(fieldTypes[i] + "field queried as string did not return " + number + " but " + string,
                    String.valueOf(number).equals(string));
            } else if (fieldTypes[i].equals("boolean")) {
                assertTrue(fieldTypes[i] + "field queried as string did not return " + Boolean.TRUE + " but " + string,
                           String.valueOf(Boolean.TRUE).equals(string));
            } else if (fieldTypes[i].equals("datetime")) {
                assertTrue(fieldTypes[i] + "field of '" + getNodeManager() + "' queried as string did not return " + Casting.toString(TEST_DATE) + " but " + string,
                           Casting.toString(TEST_DATE).equals(string));
            } else if (fieldTypes[i].equals("list")) {
                assertTrue(fieldTypes[i] + "field queried as string did not return \"true,true\" but " + string,
                    "true,true".equals(string));
            } else {
                fail();
            }
        }
    }

    public void testGetXMLValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
           if (fieldTypes[i].equals("xml") || fieldTypes[i].equals("string")) {
               Document document = node.getXMLValue(fieldTypes[i] + "field");
               assertTrue(fieldTypes[i] + " field queried as XML returns null", document !=null);
           }
        }
    }

   public void testGetNodeValue() {
        Node nodeValue = node.getNodeValue("nodefield");
        assertTrue(nodeValue != null);
        assertTrue(nodeValue.getNumber() == getCloud().getNodeManager("bb").getNumber());
        // getNodeValue on other types not defined (according to javadoc), so not tested here.
    }


    public void testGetBooleanValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
           boolean bool = node.getBooleanValue(fieldTypes[i] + "field");
           if (fieldTypes[i].equals("list") || fieldTypes[i].equals("xml") || fieldTypes[i].equals("string")
                || fieldTypes[i].equals("byte")) {
               assertTrue(fieldTypes[i] + " field queried as boolean returns TRUE", !bool);
           } else {
               assertTrue(fieldTypes[i] + " field queried as boolean returns FALSE", bool);
           }
        }
    }

    public void testGetDateTimeValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            Date value = null;
            if (fieldTypes[i].equals("node")) {
                value = node.getDateValue(fieldTypes[i] + "field");
                assertTrue(fieldTypes[i] + " field queried as datetime did not return "+new Date(-1)+", but " + value,
                            value.getTime()==-1);
            } else if (fieldTypes[i].equals("datetime")) {
                value = node.getDateValue(fieldTypes[i] + "field");
                assertTrue(fieldTypes[i] + " field queried as datetime did not return "+new Date(TEST_TIME)+", but " + value,
                            value.getTime()==TEST_TIME);
            } else if (fieldTypes[i].equals("double")) {
                value = node.getDateValue(fieldTypes[i] + "field");
                long time = new Double(Double.MAX_VALUE).longValue() * 1000;
                assertTrue(fieldTypes[i] + " field queried as datetime did not return "+new Date(time)+", but " + value,
                            value.getTime()==time);
            } else if (fieldTypes[i].equals("float")) {
                value = node.getDateValue(fieldTypes[i] + "field");
                long time = new Float(Float.MAX_VALUE).longValue() * 1000;
                assertTrue(fieldTypes[i] + " field queried as datetime did not return "+new Date(time)+", but " + value,
                            value.getTime()==time);
            } else if (fieldTypes[i].equals("int")) {
                value = node.getDateValue(fieldTypes[i] + "field");
                long time = new Integer(Integer.MAX_VALUE).longValue()*1000;
                assertTrue(fieldTypes[i] + " field queried as datetime did not return "+new Date(time)+", but " + value,
                            value.getTime()==time);
            } else if (fieldTypes[i].equals("long")) {
                value = node.getDateValue(fieldTypes[i] + "field");
                long time = Long.MAX_VALUE*1000; // oddd..
                assertTrue(fieldTypes[i] + " field queried as datetime did not return "+new Date(time) + ", but " + value,
                            value.getTime()==time);
            } else {
                try {
                    value = node.getDateValue(fieldTypes[i] + "field");
                    fail(fieldTypes[i] + " field 's value '" + node.getStringValue(fieldTypes[i] + "field") + "' cannot be queried as a date, should have thrown exception");
                } catch (Throwable e) {
                    return;
                }
            }
            assertTrue(fieldTypes[i] + " field queried as datetime returned null", value != null);
       }
    }

    public void testGetListValue() {
        for (int i = 0; i < fieldTypes.length; i++) {
            List value = node.getListValue(fieldTypes[i] + "field");
            assertTrue(fieldTypes[i] + " field queried as list returned null", value!=null);
            if (fieldTypes[i].equals("list")) {
                assertTrue(fieldTypes[i] + " field queried as list did not return [TRUE,TRUE], but " + value,
                    value.size()==2 && value.get(0).equals(Boolean.TRUE) && value.get(1).equals(Boolean.TRUE));
           } else {
                assertTrue(fieldTypes[i] + " field queried as list did not return [<node>], but " + value,
                            value.size() == 1);
            }
       }
    }

}
