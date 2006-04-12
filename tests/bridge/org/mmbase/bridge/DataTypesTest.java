/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.datatypes.*;
import java.util.*;
import org.mmbase.util.*;
import org.mmbase.tests.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class DataTypesTest extends BridgeTest {


    public DataTypesTest(String name) {
        super(name);
    }
    protected static Object[] cases = null;
    
    public void setUp() throws Exception {
        if (cases == null) {
            Cloud cloud = getCloud();
            Node node1 = cloud.getNodeManager("datatypes");
            Node node2 = cloud.getList(cloud.getNodeManager("object").createQuery()).getNode(0);
            cases = new Object[] {
                /* {field    {valid values}   {invalid values}} */
                new Object[] {"string",
                              new Object[] {"abcdefg", "ijklm\nopqrstx", null},
                              new Object[] {}},
                new Object[] {"line",
                              new Object[] {"abcdefg", null},
                              new Object[] {"ijklm\nopqrstx"}},
                new Object[] {"field",
                              new Object[] {"xyz", "zyz\nkloink", null} ,
                              new Object[] {}},
                new Object[] {"zipcode",
                              new Object[] {"7081EA", "7081  ea", null},
                              new Object[] {"70823b", "xx 7081 EA",  "xx\n7081 EA"}},
                new Object[] {"pattern",
                              new Object[] {"ababa", "aBB", null},
                              new Object[] {"c", "abaxbab"}},
                new Object[] {"languages", 
                              new Object[] {"nl", "en", null},
                              new Object[] {"c", "ababab", ""}},
                new Object[] {"integer",
                              new Object[] {new Integer(-100), "1234", "1234.4", null},
                              new Object[] {new Long(Long.MAX_VALUE), "1e30",  "asdfe" 
                              }},
                new Object[] {"range",
                              new Object[] {new Integer(5), null},
                              new Object[] {new Integer(0), new Integer(10)}},
                new Object[] {"datetime",
                              new Object[] {new Date(), "2005-01-01", DynamicDate.getInstance("now - 5 year"), null},
                              new Object[] {"xxx"}},
                new Object[] {"period",
                              new Object[] {new Date(), "2005-01-01", "2006-01-01", null},
                              new Object[] {"1973-03-05", "2050-01-01"}},
                new Object[] {"dynamic_period",
                              new Object[] {new Date(), "today + 100 year", null},
                              new Object[] {"now - 4 day", "today + 101 year"}},
                new Object[] {"mmbase_state_enumeration",
                              new Object[] {"ACTIVE", "inactive", "unknown", new Integer(1), "1", null},
                              new Object[] {"-2", new Long(71221111112L), "bla bla"}},
                new Object[] {"enumeration",
                              new Object[] {"2", "4", new Integer(6), null},
                              new Object[] {"-1", "xxx"}},
                new Object[] {"restricted_ordinals",
                              new Object[] {"2", "4", new Integer(6), null},
                              new Object[] {"1", "21", new Integer(10)}},
                new Object[] {"float",
                              new Object[] {"2", "4", new Integer(6), null, new Double(1.0), "1.0", "1e20", null},
                              new Object[] {new Double(Double.POSITIVE_INFINITY), "bla bla"
                              }},
                new Object[] {"boolean",
                              new Object[] {Boolean.TRUE, Boolean.FALSE, "true", "false", new Integer(1), new Integer(0), null},
                              new Object[] {"asjdlkf", "21", "yes", new Integer(10)}},
                new Object[] {"yesno",
                              new Object[] {Boolean.TRUE, Boolean.FALSE,"true", "false", new Integer(1), new Integer(0), null},
                              new Object[] {"asjdlkf", "21", new Integer(10)}},
                new Object[] {"integer_boolean",
                              new Object[] {Boolean.TRUE, Boolean.FALSE, "true", "false", new Integer(1), new Integer(0), null},
                              new Object[] {"asjdlkf", "21", new Integer(10)}},

                new Object[] {"string_boolean", 
                              new Object[] {Boolean.TRUE, Boolean.FALSE, "true", "false", new Integer(1), new Integer(0), null},
                              new Object[] {"asjdlkf", "21", new Integer(10)}},
                new Object[] {"boolean_string",
                              new Object[] {Boolean.TRUE, Boolean.FALSE, "true", "false", null},
                              new Object[] { "asjdlkf", "21", new Integer(10)}},
                new Object[] {"node",
                              new Object[] {node1, node2, null},
                              new Object[] {"asjdlkf", new Integer(-1), new Integer(-100), new Float(2.0)}
                }

            };
        }
    }

    protected Node getNewNode() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        return  nodeManager.createNode();
    }

    public void testCheckValid() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        StringBuffer err = new StringBuffer();
        for (int i = 0; i < cases.length; i++) {
            Object[] kase = (Object[]) cases[i];
            Field field = nodeManager.getField((String)kase[0]);
            Object[] validValues = (Object[]) kase[1];
            Object[] invalidValues = (Object[]) kase[2];
            for (int j = 0; j < validValues.length; j++) {
                Collection errors = field.getDataType().validate(validValues[j]);
                if(errors.size() != 0) {
                    err.append("Field " + field + " value '" + validValues[j] + "' was expected to be valid, but: " + LocalizedString.toStrings(errors, Locale.US) + "\n");
                }
            }
            for (int j = 0; j < invalidValues.length; j++) {
                if (field.getDataType().validate(invalidValues[j]).size() == 0) {
                    err.append("Field " + field + " value '" + invalidValues[j] + "' was expected to be invalid  according to datatype " + field.getDataType() + "\n");
                }


            }
        }
        assertTrue(err.toString(), err.length() == 0);

    }

    public void testEnumeration() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        Field field = nodeManager.getField("boolean_string");
        DataType dt = field.getDataType();
        LocalizedEntryListFactory fact = dt.getEnumerationFactory();
        assertTrue(dt instanceof StringDataType);
        assertTrue(fact.size() == 2);
        assertEquals("" + fact, "bla",  fact.castKey("bla"));
        assertEquals("true", fact.castKey("true"));
        assertEquals("21",   fact.castKey("21"));
    }
    public void testEnumeration2() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        Field field = nodeManager.getField("integer_boolean");
        DataType dt = field.getDataType();
        LocalizedEntryListFactory fact = dt.getEnumerationFactory();
        assertTrue(dt instanceof BooleanDataType);
        assertTrue("" + fact, fact.size() == 2);
        assertEquals("" + fact, "bla",  fact.castKey("bla"));
        assertEquals(Boolean.TRUE, fact.castKey("true"));
        assertEquals("21",   fact.castKey("21"));
    }
    public void testEnumeration3() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        Field field = nodeManager.getField("string_boolean");
        DataType dt = field.getDataType();
        LocalizedEntryListFactory fact = dt.getEnumerationFactory();
        assertTrue(dt instanceof BooleanDataType);
        assertTrue(fact.size() == 2);
        assertEquals("" + fact, "bla",  fact.castKey("bla"));
        assertEquals(Boolean.TRUE, fact.castKey("true"));
        assertEquals("21",   fact.castKey("21"));
    }



    protected Object getDefaultValue(Field field) {
       Object defaultValue = field.getDataType().getDefaultValue();
       if (defaultValue == null && false) { // && field.isNotNull(),, cannot be checked through bridge. The 'datatypes' builder only has nullable fields.
           defaultValue = Casting.toType(org.mmbase.core.util.Fields.typeToClass(field.getType()), "");
       }
       return defaultValue;
    }

    protected boolean sufficientlyEqual(Object value1, Object value2) {
        if (value1 == null) return value2 == null;
        if (value1 instanceof Date && value2 instanceof Date) {
            // for dynamic dates.
            return Math.abs(((Date) value1).getTime() - ((Date) value2).getTime()) < 10000L;
        } else {
            return value1.equals(value2);
        }
    }

    public void testDefaultValuesUncommited() {
        Node newNode = getNewNode();
        NodeManager nodeManager = newNode.getNodeManager();
        for (int i = 0; i < cases.length; i++) {
            Object[] kase = (Object[]) cases[i];
            Field field = nodeManager.getField((String)kase[0]);
            Object defaultValue = getDefaultValue(field);
            Object value = newNode.getValue(field.getName());
            assertTrue("default value of uncommitted node is not as expected for field " + field + " " + defaultValue + " != " + value,
                       sufficientlyEqual(value, defaultValue));

        }
        newNode.commit();

    }
    public void testDefaultValuesCommited() {
        Node newNode = getNewNode();
        NodeManager nodeManager = newNode.getNodeManager();
        newNode.commit();
        for (int i = 0; i < cases.length; i++) {
            Object[] kase = (Object[]) cases[i];
            Field field = nodeManager.getField((String)kase[0]);
            Object defaultValue = getDefaultValue(field);
            Object value = newNode.getValue(field.getName());
            assertTrue("default value of committed node is not as expected for field " + field + " " + defaultValue + " != " + value,
                       sufficientlyEqual(value, defaultValue));
        }
    }

    public void testEnumerations() {
        Node newNode = getNewNode();
        newNode.setValue("mmbase_state_enumeration", "ERROR");
        newNode.commit();
        int value = newNode.getIntValue("mmbase_state_enumeration");
        assertTrue("ERROR did evaluate to 3 but to " + value, value == 3);
        newNode = getNewNode();
        newNode.setStringValue("mmbase_state_enumeration", "ERROR");
        newNode.commit();
        value = newNode.getIntValue("mmbase_state_enumeration");
        assertTrue("ERROR did evaluate to 3 but to " + value, value == 3);

    }

    public void testBinary() {
        Node newNode = getNewNode();
        assertTrue("handle is not default null", newNode.getNodeManager().getField("handle").getDataType().getDefaultValue() == null);
        newNode.setValue("handle", new byte[] {1, 2, 3, 4});
        newNode.commit();
        try {
            Node newNode2 = getNewNode();
            newNode2.setValue("handle", new byte[] {1, 2, 3, 4});
            newNode2.commit();
            fail("There is unique on the 'checksum' of handle, so setting same value for second time should have thrown exception");
        } catch (Exception e) {
        }
    }

    public void testNotNull() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        FieldIterator iterator = nodeManager.getFields(NodeManager.ORDER_EDIT).fieldIterator();
        while(iterator.hasNext()) {
            Field field = iterator.nextField();
            DataType dt = (BasicDataType) field.getDataType().clone();
            dt.setRequired(true);
            dt.finish("bla");

            assertTrue(dt.isRequired());
            assertEquals(dt.getRequiredRestriction().getValue(), Boolean.TRUE);
            assertFalse(dt.getRequiredRestriction().valid(null, null, null));
            Collection errors = dt.validate(null);
            assertTrue("According to " + dt + " null should be invalid, but it was", errors.size() > 0);
        }
    }


}
