/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.text.*;
import java.util.*;
import java.math.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.mmbase.util.Casting;
import org.mmbase.util.xml.XMLWriter;
import org.mmbase.datatypes.*;

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
    protected BigDecimal TEST_DECIMAL = new BigDecimal("123123123.123456789");
    protected Long TEST_LONG = Long.MAX_VALUE - 10;
    protected Double TEST_DOUBLE = new Double(Double.MAX_VALUE);

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
        cloud.setLocale(Locale.US);
        node = cloud.getNodeManager(getNodeManager()).createNode();
        Node typedefNode = cloud.getNodeManager("bb");
        assertTrue(typedefNode != null);
        byte[] bytes = { 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100, 33 }; // 'Hello World!'
        node.setValue("binaryfield", bytes);
        node.setValue("doublefield", TEST_DOUBLE);
        node.setValue("floatfield", new Float(Float.MAX_VALUE));
        node.setValue("intfield", new Integer(Integer.MAX_VALUE));
        node.setValue("longfield", TEST_LONG);
        assertTrue(node.getLongValue("longfield") == TEST_LONG.longValue());
        node.setValue("stringfield", "Bridge testing!");
        node.setValue("xmlfield", getEmptyDocument());
        node.setValue("nodefield", typedefNode);
        org.mmbase.datatypes.DataType<?> dt = node.getNodeManager().getField("datetimefield").getDataType();
        //assertTrue("Not a datetime-datatype but " + dt.getClass(), dt.getClass().equals(org.mmbase.datatypes.DateTimeDataType.class)); // would give error in Node#setValue otherwise

        node.setValue("datetimefield", TEST_DATE);
        node.setValue("decimalfield", TEST_DECIMAL);
        node.setValue("booleanfield", Boolean.TRUE);
        List<Boolean> list = new ArrayList<Boolean>();
        list.add(Boolean.TRUE);
        list.add(Boolean.TRUE);
        //node.setValue("listfield", list);
        node.commit();
        assertTrue(node.getLongValue("longfield") == TEST_LONG.longValue());
    }

    public void tearDown() {
        node.delete();
    }

    public void testGetValue() {
        for (String element : fieldTypes) {
            Object object = node.getValue(element + "field");
            if (element.equals("binary")) {
                byte[] bytes = (byte[])object;
                assertTrue("getValue on byte field should give 'Hello World!' but gave '" + new String(bytes) + "'",
                           "Hello world!".equals(new String(bytes)));
            } else if (element.equals("double")) {
                assertTrue("getValue on double field should give " +  TEST_DOUBLE + " but gave " + object,
                    TEST_DOUBLE.compareTo((Double)object) == 0);
            } else if (element.equals("float")) {
                assertTrue("getValue on float field should give " +  Float.MAX_VALUE + " but gave " + object,
                    new Float(Float.MAX_VALUE).compareTo((Float)object) == 0);
            } else if (element.equals("int")) {
                assertTrue("getValue on int field should give " +  Integer.MAX_VALUE + " but gave " + object,
                    new Integer(Integer.MAX_VALUE).compareTo((Integer)object) == 0);
            } else if (element.equals("long")) {
                assertTrue("getValue on long field should give " +  TEST_LONG + " but gave " + object,
                           TEST_LONG.compareTo((Long)object) == 0);
            } else if (element.equals("string")) {
                assertTrue("getValue on string field should give \"Bridge testing!\" but gave " + object,
                    "Bridge testing!".equals(object));
            } else if (element.equals("xml")) {
                Element el = ((Document) object).getDocumentElement();
                Element empty = getEmptyDocument().getDocumentElement();
                assertTrue(XMLWriter.write(empty) + "!=" + XMLWriter.write(el),
                           empty.isEqualNode(el));
                assertTrue("getValue on xml field should give ??? but gave ",
                    Casting.toString(getEmptyDocument()).equals(Casting.toString(object)));
            } else if (element.equals("node")) {
                Node typedefNode = getCloud().getNodeManager("bb");
                assertTrue("getValue on node field should give " + typedefNode.getNumber() +" but gave " + ((Node) object).getNumber(),
                      ((Node) object).getNumber() == typedefNode.getNumber());
            } else if (element.equals("datetime")) {
                assertTrue("getValue on datetime field should give " + TEST_DATE +" but gave " + object,
                           TEST_DATE.equals(object));
            } else if (element.equals("decimal")) {
                assertTrue("getValue on decimal field should give " + TEST_DECIMAL +" but gave " + object,
                           TEST_DECIMAL.compareTo((BigDecimal) object) == 0);
            } else if (element.equals("boolean")) {
                assertTrue("getValue on boolean field should give TRUE but gave " + object,object.equals(Boolean.TRUE));
            } else if (element.equals("list")) {
                assertTrue("getValue on list field should give [TRUE,TRUE] but gave " + object,
                    object instanceof List && ((List)object).size()==2 &&
                    ((List)object).get(0).equals(Boolean.TRUE) &&
                    ((List)object).get(1).equals(Boolean.TRUE));
            } else {
                fail();
            }
        }
    }
    @Override
    public void testGetBinaryValue() {
        for (String element : fieldTypes) {
            byte[] bytes = node.getByteValue(element + "field");
            if (element.equals("binary")) {
                byte[] check = { 72, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100, 33 };
                for (int j = 0; j < bytes.length; j++) {
                    assertTrue(bytes[j] == check[j]);
                }
            } else if (element.equals("double")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("float")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("int")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("long")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("string")) {
                assertTrue("Bridge testing!".equals(new String(bytes)));
            } else if (element.equals("xml")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("node")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("boolean")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("datetime")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("decimal")) {
                assertTrue(bytes.length == 0);
            } else if (element.equals("list")) {
                assertTrue(bytes.length == 0);
            } else {
                fail();
            }
        }
    }

    public void testGetDoubleValue() {
        for (String element : fieldTypes) {
            double d = node.getDoubleValue(element + "field");
            if (element.equals("binary")) {
                assertTrue(d == -1);
            } else if (element.equals("double")) {
                assertTrue(d == TEST_DOUBLE);
            } else if (element.equals("float")) {
                assertTrue(d == Float.MAX_VALUE);
            } else if (element.equals("int")) {
                assertTrue(d == Integer.MAX_VALUE);
            } else if (element.equals("long")) {
                assertTrue(d == TEST_LONG);
            } else if (element.equals("string")) {
                assertTrue(d == -1);
            } else if (element.equals("xml")) {
                assertTrue(d == -1);
            } else if (element.equals("node")) {
                assertTrue(d == getCloud().getNodeManager("bb").getNumber());
            } else if (element.equals("boolean")) {
                assertTrue(d == 1);
            } else if (element.equals("datetime")) {
                assertTrue(element + "field queried as double did not return " + (double)TEST_TIME/1000 + " but " + d,
                        d == (double)TEST_TIME/1000);
            } else if (element.equals("decimal")) {
                assertTrue(d == TEST_DECIMAL.doubleValue());
            } else if (element.equals("list")) {
                assertTrue(d == -1);
            } else {
                fail();
            }
        }
    }

    public void testGetFloatValue() {
        for (String element : fieldTypes) {
            float f = node.getFloatValue(element + "field");
            if (element.equals("binary")) {
                assertTrue(f == -1);
            } else if (element.equals("double")) {
                assertTrue("Infinity".equals(String.valueOf(f)));
            } else if (element.equals("float")) {
                assertTrue(f == Float.MAX_VALUE);
            } else if (element.equals("int")) {
                assertTrue(f == Integer.MAX_VALUE);
            } else if (element.equals("long")) {
                assertTrue(f == TEST_LONG);
            } else if (element.equals("string")) {
                assertTrue(f == -1);
            } else if (element.equals("xml")) {
                assertTrue(f == -1);
            } else if (element.equals("node")) {
                assertTrue(f == getCloud().getNodeManager("bb").getNumber());
            } else if (element.equals("boolean")) {
                assertTrue(f == 1);
            } else if (element.equals("datetime")) {
                assertTrue(element + "field queried as float did not return " + (float)TEST_TIME/1000 + " but " + f,
                        f == (float)TEST_TIME/1000);
            } else if (element.equals("decimal")) {
                assertTrue(element + "field queried as float did not return " + TEST_DECIMAL + " but " + f,
                           f == TEST_DECIMAL.floatValue());
            } else if (element.equals("list")) {
                assertTrue(f == -1);
            } else {
                fail();
            }
        }
    }

    public void testGetIntValue() {
        for (String element : fieldTypes) {
            int integer = node.getIntValue(element + "field");
            if (element.equals("binary")) {
                assertTrue(integer == -1);
            } else if (element.equals("double")) {
                assertTrue(integer == Integer.MAX_VALUE);
            } else if (element.equals("float")) {
                assertTrue(integer == Integer.MAX_VALUE);
            } else if (element.equals("int")) {
                assertTrue(integer == Integer.MAX_VALUE);
            } else if (element.equals("long")) {
                assertTrue(integer == Integer.MAX_VALUE);
            } else if (element.equals("string")) {
                assertTrue(integer == -1);
            } else if (element.equals("xml")) {
                assertTrue(integer == -1);
            } else if (element.equals("node")) {
                assertTrue(integer == getCloud().getNodeManager("bb").getNumber());
            } else if (element.equals("boolean")) {
                assertTrue(integer == 1);
            } else if (element.equals("datetime")) {
                assertTrue(element + "field queried as double did not return " + TEST_TIME / 1000 + " but " + integer,
                        integer == (int) (TEST_TIME / 1000));
            } else if (element.equals("decimal")) {
                assertTrue("" + integer, integer == TEST_DECIMAL.intValue());
            } else if (element.equals("list")) {
                assertTrue(integer == -1);
            } else {
                fail();
            }
        }
    }

    public void testGetLongValue() {
        for (String element : fieldTypes) {
            long l = node.getLongValue(element + "field");
            if (element.equals("binary")) {
                assertTrue(l == -1);
            } else if (element.equals("double")) {
                assertTrue(l == Long.MAX_VALUE);
            } else if (element.equals("float")) {
                assertTrue(l == Long.MAX_VALUE);
            } else if (element.equals("int")) {
                assertTrue(l == Integer.MAX_VALUE);
            } else if (element.equals("long")) {
                assertTrue("Not found " + TEST_LONG + " but " + l, l == TEST_LONG);
            } else if (element.equals("string")) {
                assertTrue(l == -1);
            } else if (element.equals("xml")) {
                assertTrue(l == -1);
            } else if (element.equals("node")) {
                assertTrue(l == getCloud().getNodeManager("bb").getNumber());
            } else if (element.equals("boolean")) {
                assertTrue(l == 1);
            } else if (element.equals("datetime")) {
                assertTrue(element + "field queried as double did not return " + TEST_TIME/1000 + " but " + l,
                    l == TEST_TIME/1000);
            } else if (element.equals("list")) {
                assertTrue(l == -1);
            } else if (element.equals("decimal")) {
                assertTrue(l == TEST_DECIMAL.longValue());
            } else {
                fail();
            }
        }
    }

    public void testGetStringValue() {
        for (String element : fieldTypes) {
            String string = node.getStringValue(element + "field");
            if (element.equals("binary")) {
                assertTrue(element + "field queried as string did not return \"Hello world!\" but " + string, "Hello world!".equals(string));
            } else if (element.equals("double")) {
                assertTrue(element + "field queried as string did not return " + TEST_DOUBLE + " but " + string,
                    String.valueOf(TEST_DOUBLE).equals(string));
            } else if (element.equals("float")) {
                // SQLDB causes some problems when rounding floats, which it stores internally as Doubles.
                // so compare the resulting string to both Float.MAX_VALUE and Double(Float.MAX_VALUE )
                // to cover this. Note that this somehow only applies when comparing strings.
                assertTrue(element + "field queried as string did not return " + Float.MAX_VALUE + " but " + string,
                    String.valueOf(new Double(Float.MAX_VALUE)).equals(string) || String.valueOf(Float.MAX_VALUE).equals(string));
            } else if (element.equals("int")) {
                assertTrue(element + "field queried as string did not return " + Integer.MAX_VALUE + " but " + string,
                           String.valueOf(Integer.MAX_VALUE).equals(string));
            } else if (element.equals("long")) {
                assertTrue(element + "field queried as string did not return " + TEST_LONG + " but " + string,
                           String.valueOf(TEST_LONG).equals(string));
            } else if (element.equals("string")) {
                assertTrue(element + "field queried as string did not return \"Bridge testing!\" but " + string,
                           "Bridge testing!".equals(string));
            } else if (element.equals("xml")) {
                assertTrue(element + "field" +" field queried as string did not return empty document but " + string,
                    Casting.toString(getEmptyDocument()).equals(string));
            } else if (element.equals("node")) {
                int number = getCloud().getNodeManager("bb").getNumber();
                assertTrue(element + "field queried as string did not return " + number + " but " + string,
                    String.valueOf(number).equals(string));
            } else if (element.equals("boolean")) {
                assertTrue(element + "field queried as string did not return " + Boolean.TRUE + " but " + string,
                           String.valueOf(Boolean.TRUE).equals(string));
            } else if (element.equals("datetime")) {
                Field field = node.getNodeManager().getField(element + "field");
                String formatted;
                if (field.getDataType() instanceof DateTimeDataType) {
                    DateTimePattern pattern = ((DateTimeDataType)field.getDataType()).getPattern();
                    DateFormat dateFormat = pattern.getDateFormat(Locale.US);
                    formatted = dateFormat.format(TEST_DATE);
                } else {
                    formatted = Casting.toString(TEST_DATE);
                }
                assertTrue(element + "field of '" + getNodeManager() + "' queried as string did not return " + formatted + " but " + string,
                           formatted.equals(string));
            } else if (element.equals("list")) {
                assertTrue(element + "field queried as string did not return \"true,true\" but " + string,
                    "true,true".equals(string));
            } else if (element.equals("decimal")) {
                assertTrue(element + "field queried as string did not return " + TEST_DECIMAL + " but " + string,
                           String.valueOf(TEST_DECIMAL).equals(string));
            } else {
                fail();
            }
        }
    }

    public void testGetXMLValue() {
        for (String element : fieldTypes) {
           if (element.equals("xml") || element.equals("string")) {
               Document document = node.getXMLValue(element + "field");
               assertTrue(element + " field queried as XML returns null", document !=null);
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
        for (String element : fieldTypes) {
           boolean bool = node.getBooleanValue(element + "field");
           if (element.equals("list") || element.equals("xml") || element.equals("string")
                || element.equals("binary")) {
               assertTrue(element + " field queried as boolean returns TRUE", !bool);
           } else {
               assertTrue(element + " field queried as boolean returns FALSE", bool);
           }
        }
    }

    public void testGetDateTimeValue() {
        for (final String element : fieldTypes) {
            Date value = null;
            if (element.equals("node")) {
                value = node.getDateValue(element + "field");
                assertTrue(element + " field queried as datetime did not return "+new Date(-1)+", but " + value,
                            value.getTime()==-1);
            } else if (element.equals("datetime")) {
                value = node.getDateValue(element + "field");
                assertTrue(element + " field queried as datetime did not return "+new Date(TEST_TIME)+", but " + value,
                            value.getTime()==TEST_TIME);
            } else if (element.equals("double")) {
                value = node.getDateValue(element + "field");
                Date time = Casting.toDate(TEST_DOUBLE);
                assertTrue(element + " field queried as datetime did not return " + time + ", but " + value,
                           value.getTime() == time.getTime());
            } else if (element.equals("float")) {
                value = node.getDateValue(element + "field");
                Date time = Casting.toDate(Float.MAX_VALUE);
                assertTrue(element + " field queried as datetime did not return "+ time +", but " + value,
                           value.getTime() == time.getTime());
            } else if (element.equals("int")) {
                value = node.getDateValue(element + "field");
                long time = new Integer(Integer.MAX_VALUE).longValue()*1000;
                assertTrue(element + " field queried as datetime did not return "+new Date(time)+", but " + value,
                            value.getTime()==time);
            } else if (element.equals("long")) {
                value = node.getDateValue(element + "field");
                Date time = Casting.toDate(Long.MAX_VALUE);
                assertTrue(element + " field queried as datetime did not return "+ time + ", but " + value,
                           value.getTime() == time.getTime());
            } else if (element.equals("decimal")) {
                value = node.getDateValue(element + "field");
                long time = TEST_DECIMAL.longValue() * 1000;
                assertTrue(element + " field queried as datetime did not return "+new Date(time) + ", but " + value,
                            value.getTime()==time);
            } else {
                try {
                    value = node.getDateValue(element + "field");
                    fail(element + " field 's value '" + node.getStringValue(element + "field") + "' cannot be queried as a date, should have thrown exception");
                } catch (Throwable e) {
                    continue;
                }
            }
            assertTrue(element + " field queried as datetime returned null", value != null);
        }

    }

    public void testGetDecimalValue() {
        for (String element : fieldTypes) {
            BigDecimal l = node.getDecimalValue(element + "field");
            if (element.equals("binary")) {
                assertTrue(new BigDecimal(-1).compareTo(l) == 0);
            } else if (element.equals("double")) {
                assertTrue("" + l + " != " + TEST_DOUBLE, new BigDecimal(TEST_DOUBLE).compareTo(l) == 0);
            } else if (element.equals("float")) {
                assertTrue(new BigDecimal(Float.MAX_VALUE).compareTo(l) == 0);
            } else if (element.equals("int")) {
                assertTrue(new BigDecimal(Integer.MAX_VALUE).compareTo(l) == 0);
            } else if (element.equals("long")) {
                assertTrue("" + l + " != " + TEST_LONG, new BigDecimal(TEST_LONG).compareTo(l) == 0);
            } else if (element.equals("string")) {
                assertEquals(new BigDecimal(-1), l);
            } else if (element.equals("xml")) {
                assertEquals(new BigDecimal(-1), l);
            } else if (element.equals("node")) {
                assertEquals(new BigDecimal(getCloud().getNodeManager("bb").getNumber()), l);
            } else if (element.equals("boolean")) {
                assertEquals(BigDecimal.ONE, l);
            } else if (element.equals("datetime")) {
                assertEquals(element + "field queried as double did not return " + TEST_TIME/1000 + " but " + l,
                             0,
                             new BigDecimal(TEST_TIME / 1000).compareTo(l));
            } else if (element.equals("decimal")) {
                assertEquals(TEST_DECIMAL, l);
            } else if (element.equals("list")) {
                assertEquals(new BigDecimal(-1), 1);
            } else {
                fail();
            }
        }
    }


    public void testGetListValue() {
        for (String element : fieldTypes) {
            List value = node.getListValue(element + "field");
            assertTrue(element + " field queried as list returned null", value != null);
            if (element.equals("list")) {
                assertTrue(element + " field queried as list did not return [TRUE,TRUE], but " + value,
                    value.size()==2 && value.get(0).equals(Boolean.TRUE) && value.get(1).equals(Boolean.TRUE));
           } else {
                assertTrue(element + " field queried as list did not return [<node>], but " + value,
                           value.size() == 1);

            }
       }
    }

    public void testNodeFieldWithNewNode() {
        try {
            Cloud cloud = getCloud();
            Node node1 = cloud.getNodeManager(getNodeManager()).createNode();
            Node node2 = cloud.getNodeManager(getNodeManager()).createNode();
            node1.setNodeValue("nodefield", node2);

            node2.commit();
            node1.commit();

            assertEquals(node1.getIntValue("nodefield"), node2.getNumber());
        } catch (Exception e)  {
            //http://www.mmbase.org/jira/browse/MMB-1632

            // fail("Should have worked " + e); FAILS
        }
    }

    public void testNodeFieldWithNewNodeInverseOrder() {
        //http://www.mmbase.org/jira/browse/MMB-1632
        try {
            Cloud cloud = getCloud();
            Node node1 = cloud.getNodeManager(getNodeManager()).createNode();
            Node node2 = cloud.getNodeManager(getNodeManager()).createNode();
            node1.setNodeValue("nodefield", node2);

            node1.commit();
            node2.commit();

            assertEquals(node1.getIntValue("nodefield"), node2.getNumber());
        } catch (Exception e) {
            //http://www.mmbase.org/jira/browse/MMB-1632
            // fail("Should have worked " + e); FAILS
        }
    }

}
