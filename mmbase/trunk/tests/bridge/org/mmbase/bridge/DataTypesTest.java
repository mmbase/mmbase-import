/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.*;
import org.mmbase.util.*;
import org.mmbase.tests.*;

/**
 *
 * @author Michiel Meeuwissen
 */
public class DataTypesTest extends BridgeTest {


    public DataTypesTest(String name) {
        super(name);
    }
    protected static Object[] cases;
    static  {
        try {
            cases = new Object[] {
                /* {field    {valid values}   {invalid values}} */
                new Object[] {"string", 
                              new Object[] {"abcdefg"}, 
                              new Object[] {"ijklm\nopqrstx"}},
                new Object[] {"field",  
                              new Object[] {"xyz", "zyz\nkloink"} , 
                              new Object[] {}},
                new Object[] {"zipcode",  
                              new Object[] {"7081EA", "7081  ea"}, 
                              new Object[] {"70823b", "xx 7081 EA",  "xx\n7081 EA", null}},
                new Object[] {"pattern",  
                              new Object[] {"ababa", "aBB"}, 
                              new Object[] {"c", "ababab", null, ""}},
                new Object[] {"integer",  
                              new Object[] {new Integer(-100)}, 
                              new Object[] {new Long(Long.MAX_VALUE)}},
                new Object[] {"range",  
                              new Object[] {new Integer(5)}, 
                              new Object[] {new Integer(0), new Integer(10)}},
                new Object[] {"datetime",  
                              new Object[] {new Date(), "2005-01-01", DynamicDate.getInstance("now - 5 year")},
                              new Object[] {"xxx"}},
                new Object[] {"period",  
                              new Object[] {new Date(), "2005-01-01", "2006-01-01"},
                              new Object[] {"1973-03-05", "2050-01-01"}},
                new Object[] {"dynamicperiod",  
                              new Object[] {new Date(), "today + 100 year"},
                              new Object[] {"now - 4 day", "today + 101 year"}}                
            };
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
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
                    err.append("Field " + field + " value '" + invalidValues[j] + "' was expected to be invalid\n");
                } 

                          
            }
        }
        assertTrue(err.toString(), err.length() == 0);

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
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes"); 
        Node newNode = nodeManager.createNode();
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
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes"); 
        Node newNode = nodeManager.createNode();
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


}
