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
                              new Object[] {"abcde\nfghij"}},
                new Object[] {"field",  
                              new Object[] {"abcdefg", "abcde\nfghij"} , 
                              new Object[] {}},
                new Object[] {"zipcode",  
                              new Object[] {"7081EA", "7081  ea"}, 
                              new Object[] {"70823b", "", null}},
                new Object[] {"pattern",  
                              new Object[] {"ababa", "aBB"}, 
                              new Object[] {"c", "ababab", null}},
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
        // Create a test node.
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes"); 
        for (int i = 0; i < cases.length; i++) {
            Object[] kase = (Object[]) cases[i];
            Field field = nodeManager.getField((String)kase[0]);
            Object[] validValues = (Object[]) kase[1];
            Object[] invalidValues = (Object[]) kase[2];
            for (int j = 0; j < validValues.length; j++) {
                assertTrue("Field " + field + " value '" + validValues[j] + "' was expected to be valid", 
                           field.getDataType().validate(validValues[j]).size() == 0);
            }
            for (int j = 0; j < invalidValues.length; j++) {
                assertTrue("Field " + field + " value '" + validValues[j] + "' was expected to be invalid", 
                           field.getDataType().validate(validValues[j]).size() > 0);
            }
        }

    }


}
