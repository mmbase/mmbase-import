/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.datatypes.*;
import org.mmbase.datatypes.processors.*;
import java.util.*;
import java.math.BigDecimal;
import org.mmbase.util.*;
import org.mmbase.tests.*;
import junit.framework.*;

/**
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class DataTypesTest extends BridgeTest {

    public DataTypesTest(String name) {
        super(name);
    }
    protected static Map<Class, Object[][]> casesCache = new HashMap<Class, Object[][]>();

    private Object[][] cases;



    public void setUp() throws Exception {
        cases = casesCache.get(this.getClass());
        if (cases == null) {
            Cloud cloud = getCloud();

	    // it's driving me nuts. On james the following line simply refuses to compile, without any understandable cause:

            //cases = org.mmbase.datatypes.ParameterizedDataTypesTest.getCases(cloud, getBinary());

	    // doing it with reflection then, which does work, and I have lost enough time now because of this
	    // damn stupid issue.
	    Class damn = Class.forName("org.mmbase.datatypes.ParameterizedDataTypesTest");
            cases = (Object[][]) damn.getMethod("getCases", Cloud.class, byte[].class).invoke(null, cloud, getBinary());

            casesCache.put(this.getClass(), cases);
        }
    }


    // There is a unique constraint on checksum.
    // This method is overriden in -Transaction extension, to avoid the unique constraint exception
    protected  byte[] getBinary() {
        return new byte[] {1, 2, 3, 4};
    }


    protected Node getNewNode() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        return  nodeManager.createNode();
    }

    protected void commit(Node node) {
        node.commit();
        if (node.getCloud() instanceof Transaction) {
            ((Transaction) node.getCloud()).commit();
        }
    }

    public void testCheckValid() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        StringBuffer err = new StringBuffer();
        for (Object[] kase : cases) {
            Field field = nodeManager.getField((String)kase[0]);
            Object[] validValues = (Object[]) kase[1];
            Object[] invalidValues = (Object[]) kase[2];
            for (Object element2 : validValues) {
                field.getDataType().validate(element2); // should not give exception
                Collection<LocalizedString> errors = field.getDataType().validate(element2, null, field);
                if(errors.size() != 0) {
                    err.append("V Field " + field.getName() + " value '" + (element2 == null ? "" : element2.getClass().getName() + " ") +  Casting.toString(element2) + "' was expected to be valid, but: " + LocalizedString.toStrings(errors, Locale.US) + "\n");
                }
            }
            for (Object element2 : invalidValues) {
                try {
                    field.getDataType().validate(element2); // should not give exception
                } catch (Exception e) {
                    fail("Validation of value " + element2 + " for " + field + " gave exception " + e.getClass() + " " + e.getMessage());
                }
                if (field.getDataType().validate(element2, null, field).size() == 0) {
                    err.append("I Field " + field.getName() + " value '" + (element2 == null ? "" : element2.getClass().getName() + " ") +  Casting.toString(element2) + "' was expected to be invalid  according to datatype " + field.getDataType() + "\n");
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
        LocalizedEntryListFactory<Object> fact = dt.getEnumerationFactory();
        assertTrue(dt instanceof StringDataType);
        assertEquals(fact.size(), 2);
        assertEquals("" + fact, "bla",  fact.castKey("bla"));
        assertEquals("true", fact.castKey("true"));
        assertEquals("21",   fact.castKey("21"));
    }
    public void testEnumeration2() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        Field field = nodeManager.getField("integer_boolean");
        DataType dt = field.getDataType();
        LocalizedEntryListFactory<Object> fact = dt.getEnumerationFactory();
        assertTrue(dt instanceof BooleanDataType);
        assertEquals(fact.size(), 2);
        assertEquals("" + fact, "bla",  fact.castKey("bla"));
        assertEquals(Boolean.TRUE, fact.castKey("true"));
        assertEquals("21",   fact.castKey("21"));
    }
    public void testEnumeration3() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        Field field = nodeManager.getField("string_boolean");
        DataType dt = field.getDataType();
        LocalizedEntryListFactory<Object> fact = dt.getEnumerationFactory();
        assertFalse(fact.isEmpty());
        assertTrue(dt instanceof BooleanDataType);
        assertEquals(fact.size(), 2);
        assertEquals("" + fact, "bla",  fact.castKey("bla"));
        assertEquals(Boolean.TRUE, fact.castKey("true"));
        assertEquals("21",   fact.castKey("21"));
        assertEquals(Boolean.TRUE, dt.cast("true", null, field));
        assertEquals(Boolean.FALSE, dt.cast("21", null, field));
        assertEquals(Boolean.FALSE, dt.cast("bla", null, field));
    }

    public void testEnumeration4() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        Field field = nodeManager.getField("typedef");
        DataType dt = field.getDataType();
        assertNotNull(dt.getEnumerationValues(null, field.getNodeManager().getCloud(), null, field));
        LocalizedEntryListFactory<Object> fact = dt.getEnumerationFactory();
        assertFalse(fact.isEmpty());
        assertTrue(dt instanceof NodeDataType);
        int numberOfTypes = cloud.getList(null, "typedef", null, null, null, null, null, false).size();
        assertEquals(fact.size(cloud), numberOfTypes);
        assertEquals(fact.get(null, cloud).size(), numberOfTypes);
        assertEquals(fact.get(Locale.US, cloud).size(), numberOfTypes);
        assertEquals(fact.get(new Locale("dk", "CN"), cloud).size(), numberOfTypes);
        assertEquals(nodeManager,  fact.castKey("" + nodeManager.getNumber(), cloud));
        assertEquals("bla",  fact.castKey("bla", cloud));
        assertEquals(null,  fact.castKey(null, cloud));
        assertEquals("",  fact.castKey("", cloud));
        // I think DataType.cast must really return righ ttype, so if necessary even return null?
        assertEquals(nodeManager,  dt.cast(nodeManager, null, field));
        assertEquals(null, dt.cast("bla", null, field));
        assertEquals(null, dt.cast(null, null, field));
        assertEquals(null, dt.cast("", null, field));
    }



    protected Object getDefaultValue(Field field) {
        Object defaultValue = field.getDataType().getDefaultValue(null, null, field);
        if (defaultValue == null && field.isRequired()) {
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
        for (Object[] kase : cases) {
            Field field = nodeManager.getField((String)kase[0]);
            Object defaultValue = getDefaultValue(field);
            Object value = newNode.getValue(field.getName());
            assertTrue("default value of uncommitted node is not as expected for field " + field + " " + defaultValue + " != " + value + " locale: " + newNode.getCloud().getLocale(),
                       sufficientlyEqual(value, defaultValue));

        }
        commit(newNode);

    }
    public void testDefaultValuesCommited() {
        Node newNode = getNewNode();
        NodeManager nodeManager = newNode.getNodeManager();
        commit(newNode);
        for (Object[] kase : cases) {
            Field field = nodeManager.getField((String)kase[0]);
            Object defaultValue = getDefaultValue(field);
            Object value = newNode.getValue(field.getName());
            assertTrue("default value of committed node is not as expected for field " + field + " " + defaultValue + " != " + value,
                       sufficientlyEqual(value, defaultValue));
        }
    }


    public void testValidValuesCommit() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        for (Object[] kase : cases) {
            Field field = nodeManager.getField((String)kase[0]);
            Object[] validValues = (Object[]) kase[1];
            for (Object validValue : validValues) {
                Node newNode = nodeManager.createNode();
                try {
                    newNode.setValue(field.getName(), validValue);
                    newNode.setValue(field.getName(), null);
                    newNode.setValue(field.getName(), validValue);
                    newNode.commit(); // should not give exception
                    if(field.getName().equals("handle") && validValue != null) {
                        assertFalse("Checksum is null", newNode.isNull("checksum"));
                    }
                    if (field.getDataType().isRequired() ||
                        (
                         validValue != null &&
                         (! (validValue.equals("") && (
                                                       field.getDataType() instanceof NumberDataType
                                                       || field.getDataType() instanceof NodeDataType)
                             )) && // "" for numbers and nodes may be interpreted as null
                         ! (field.getDataType() instanceof NodeDataType && validValue.equals(new Integer(-1))) // -1 casts to null for node-fields.
                         )
                         ) {
                        assertFalse("field " + field.getName() + " was null, after we set '" + validValue + "' in it", newNode.isNull(field.getName()));
                    } else {

                        assertTrue("field " + field.getName() + " was not null, after we set '" + validValue + "' in it", newNode.isNull(field.getName()));
                    }
                    if (! field.getDataType().isRequired()) {
                        // so, it must be possible to set field back to null.
                        newNode.setValue(field.getName(), null);
                        assertNull(newNode.getValue(field.getName()));
                        assertTrue(newNode.isNull(field.getName()));
                    }
                    newNode.commit();

                    if (! field.getDataType().isRequired()) {
                        assertNull(newNode.getValue(field.getName()));
                        assertTrue(newNode.isNull(field.getName()));
                        if(field.getName().equals("handle")) {
                            assertTrue("valid value: '" + validValue + "' checksum of " + newNode.getNumber() + " is " + newNode.getValue("checksum") + " but expected null", newNode.isNull("checksum"));
                        }
                    }
                } catch (Throwable t) {
                    AssertionFailedError fail = new AssertionFailedError("During field " + field + " of " + newNode.getNumber() + " value: '" + validValue + "' " + t.getMessage());
                    fail.initCause(t);
                    throw fail;
                }
            }
        }
    }

    // Often is _is_ possible to set invalid values, which will be cast to valid values then.
    // This is perhaps a backwards compatibility issue.
    // Not really happy with it though.
    /*
    public void testInValidValuesCommit() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        StringBuffer errors = new StringBuffer();
        for (int i = 0; i < cases.length; i++) {
            Object[] kase = (Object[]) cases[i];
            Field field = nodeManager.getField((String)kase[0]);
            Object[] invalidValues = (Object[]) kase[2];
            for (int j = 0; j < invalidValues.length; j++) {
                Node newNode = getNewNode();
                try {
                    newNode.setValue(field.getName(), invalidValues[j]);
                    commit(newNode);
                    errors.append("Invalid value " + invalidValues[j] + " should have given exception for " + field + " but evaluated to " + newNode.getValue(field.getName()) + "\n");
                } catch (Throwable e) {
                }
            }
        }
        assertTrue(errors.toString(), errors.length() == 0);
    }
    */


    public void testEnumerations() {
        Node newNode = getNewNode();
        newNode.setValue("mmbase_state_enumeration", "ERROR");
        commit(newNode);
        int value = newNode.getIntValue("mmbase_state_enumeration");
        assertTrue("ERROR did evaluate to 3 but to " + value, value == 3);
        newNode = getNewNode();
        newNode.setStringValue("mmbase_state_enumeration", "ERROR");
        commit(newNode);
        value = newNode.getIntValue("mmbase_state_enumeration");
        assertTrue("ERROR did evaluate to 3 but to " + value, value == 3);

    }


    public void testBinary() {
        Node newNode = getNewNode();
        assertTrue("handle is not default null", newNode.getNodeManager().getField("handle").getDataType().getDefaultValue() == null);
        newNode.setValue("handle", getBinary());
        commit(newNode);
        try {
            Node newNode2 = getNewNode();
            newNode2.setValue("handle", getBinary());
            commit(newNode2);
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
            DataType dt = (DataType) field.getDataType().clone();
            dt.setRequired(true);
            dt.finish("bla");

            assertTrue(dt.isRequired());
            assertEquals(dt.getRequiredRestriction().getValue(), Boolean.TRUE);
            assertFalse(dt.getRequiredRestriction().valid(null, null, null));
            Collection<LocalizedString> errors = dt.validate(null);
            assertTrue("According to " + dt + " null should be invalid, but it was", errors.size() > 0);
        }
    }

    public void testIntegerDateTime() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        Field field = nodeManager.getField("integer_datetime");
        DataType dt = field.getDataType();
        assertTrue(dt instanceof DateTimeDataType);
    }


    public void testLocaleFloat() {
        Cloud cloud = getCloud();
        cloud.setLocale(new Locale("nl", "NL"));

        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        Field field = nodeManager.getField("float");
        DataType dt = field.getDataType();

        System.out.print(cloud.getLocale());
        Collection<LocalizedString> errors = field.getDataType().validate("1,2");
        assertTrue("" + errors, errors.size() == 0);

        Node newNode = nodeManager.createNode();
        newNode.setValue("float", "1,3");

        assertEquals(1.3, newNode.getFloatValue("float"), 0.001);

        newNode.setValue("float", "1.4");
        assertEquals(1.4, newNode.getFloatValue("float"), 0.001);

        newNode.setValue("integer", "1e2");
        assertEquals(100, newNode.getIntValue("integer"));

        cloud.setLocale(Locale.US);

        assertTrue(field.getDataType().validate("1,5").size() > 0);


    }
    public void testDuration() {
        Node node = getNewNode();
        node.setStringValue("duration", "30:20:10");
        node.commit();

        assertEquals(10 + 20 * 60 + 30 * 60 * 60, node.getLongValue("duration"));

    }
    // http://www.mmbase.org/jira/browse/MMB-1504
    public void testRequiredLegacy() {
        Node node = getNewNode();
        assertTrue(node.getNodeManager().getField("required_legacy").isRequired());
    }

    //http://www.mmbase.org/jira/browse/MMB-1418
    public void testProcessorConfiguration() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("datatypes");
        DataType dt = nm.getField("lastmodifier").getDataType();
        CommitProcessor cp = dt.getCommitProcessor();
        assertFalse(cp instanceof ChainedCommitProcessor);
        assertTrue(cp instanceof LastModifier);

    }

    public void testClone() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("datatypes");
        DataType dt = nm.getField("lastmodifier").getDataType();
        //assertEquals(dt.getGUIName(), "lastmodifier");
    }

    public void testRequiredButNoDefaultValue() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager("invalid_defaults");
        Node n = nm.createNode();
        assertNull(n.getNodeManager().getField("required_line").getDataType().getDefaultValue());

        //Following few tests are failing, I think this may be considered a bug
        // http://www.mmbase.org/jira/browse/MMB-1901

        assertNull(n.getValueWithoutProcess("required_line")); // FAILS Storage layer makes it "" too
        assertNull(n.getValue("required_line"));
        try {
            n.commit();
            fail("Default value of " + n + ".required_line is not valid, so this node could not have been committed");
        } catch (Exception e) {
            // ok
        }
    }



}
