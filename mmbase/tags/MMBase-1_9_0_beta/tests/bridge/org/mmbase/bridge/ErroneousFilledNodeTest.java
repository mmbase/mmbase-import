/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import org.mmbase.tests.*;
import java.util.*;
import org.mmbase.util.*;
import org.mmbase.datatypes.*;

/**
 * Like FilledNodeTest but the used builder is oddly configured.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.8
 */
public class ErroneousFilledNodeTest extends BridgeTest {

    protected static Object[] cases;
    static  {
        try {
            cases = new Object[] {
                /* {field    {valid values= {invalue, outvalue}, ... }   {invalid values}} */
                new Object[] {"integerstring", // integer in db. getValue returns an Integer
                              new Object[] {
                                 new Integer(1232), 
                                 new Entry<String, Integer>("1234", new Integer(1234))
                              },
                              new Object[] {
                                  "abac" 
                              }
                },
                new Object[] {"stringinteger", // string in db. getValue returns a String
                              new Object[] { 
                                  new Entry<Integer, String>(new Integer(1232), "1232"),
                                  "1234"},
                              new Object[] {
                                  "abac"
                              }
                },
                new Object[] {"floatdouble",  // float in db
                              new Object[] {new Float(1232), new Entry<String, Float>("1234", new Float(1234))},
                              new Object[] {
                                  "abac"
                              }
                },
                new Object[] {"doublefloat",  // double in db
                              new Object[] {new Double(1232), new Entry<String, Double>("1234", new Double(1234))},
                              new Object[] {
                                  "abac"
                              }
                },
            };
        } catch (Exception e) {
        }
    }


    public ErroneousFilledNodeTest(String name) {
        super(name);
    }

    protected String getNodeManager() {
        return "aaerrors";
    }

    public void testClasses() {
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager(getNodeManager());
        // MM: it's (very) odd, but DataTypes are not necessary of the database type (any more)
        // so actuallly I think these tests should be the other way around, but at least it should be defined, which of both must happen.
        assertTrue(nm.getField("stringinteger").getDataType() instanceof IntegerDataType);
        assertTrue(nm.getField("integerstring").getDataType() instanceof StringDataType); // ah, we can store strings in integers!
        assertTrue(nm.getField("floatdouble").getDataType() instanceof DoubleDataType);
        assertTrue(nm.getField("doublefloat").getDataType() instanceof FloatDataType);
        
    }

    public void testValues() {
     
        Cloud cloud = getCloud();
        NodeManager nm = cloud.getNodeManager(getNodeManager());
        List errors = new ArrayList();
        for (Object element : cases) {
            Object[] kase = (Object[]) element;
            String fieldName = (String) kase[0];
            Object[] validValues = (Object[]) kase[1];
            for (Object value : validValues) {
                Object inValue;
                Object outValue;
                if (value instanceof Entry) {
                    inValue = ((Entry) value).getKey();
                    outValue = ((Entry) value).getValue();
                } else {
                    inValue = value;
                    outValue = value;
                }
                Node newNode = nm.createNode();
                newNode.setValue(fieldName, inValue);
                newNode.commit();
                Object actualOutValue = newNode.getValue(fieldName);
                if (outValue == null ? actualOutValue == null : outValue.equals(actualOutValue)) {
                } else {
                    errors.add("Field " + fieldName + " " + 
                               (outValue != null ? " " + outValue.getClass() : "") + outValue + " != " + 
                               (actualOutValue != null ? " " + actualOutValue.getClass() : "") + actualOutValue);
                }
            }
            Object[] invalidValues = (Object[]) kase[2];
            for (Object invalidValue : invalidValues) {
                try {
                    Node newNode = nm.createNode();
                    newNode.setObjectValue(fieldName, invalidValue);
                    newNode.commit();
                    errors.add("Value " + invalidValue + " for field " + fieldName + " was expected to be invalid, but evaluated to " + newNode.getValue(fieldName) + " " + nm.getField(fieldName).getDataType());
                } catch (Exception e) {
                    // ok, threw exception
                }
            }
        }
        assertTrue("" + errors, errors.size() == 0);
    }

}
