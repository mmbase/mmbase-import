/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;
import static org.junit.Assert.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.*;
import java.util.*;
import java.math.BigDecimal;

import org.mmbase.util.*;


/**
 * Testing valid/invalid for lots of different configurations. The fields of tests/datatypes.xml are used.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.2
 * @version $Id: DataTypesTest.java 39003 2009-10-06 14:42:39Z michiel $
 */
@RunWith(Parameterized.class)
public class ParameterizedDataTypesTest  {

    static final MockCloudContext CLOUD_CONTEXT = new MockCloudContext();
    static {
        // Use on odd locale, this may make go wrong some things
        LocalizedString.setDefault(new Locale("da"));
        DataTypes.initialize();
        try {
            CLOUD_CONTEXT.addCore();
            CLOUD_CONTEXT.addNodeManagers(ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders/tests"));

            System.out.println("Using for test " + CLOUD_CONTEXT);
            MockCloudContext.getInstance().addCore(); // avoids the frightening warning otherwised caused by the line REMARK1
        } catch (java.io.IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {


    }

    protected static Cloud getCloud() {
        return CLOUD_CONTEXT.getCloud("mmbase");
    }

    /**
     * All test-cases from this class are defined here.
     * Note that this method is also called from the 'integration tests" from tests/bridge, where the same tests are performed on an actual running mmbase (so no mocking).
     */

    public static Object[][] getCases(Cloud cloud, byte[] binary) throws Exception {

        Node node1 = cloud.getNodeManager("datatypes");
        NodeManager object = cloud.getNodeManager("object");
        Node node2 = object.createNode();
        node2.commit();
        NodeManager aa = cloud.getNodeManager("aa");
        Node node3 = aa.createNode();
        commit(node3);
        Object[][] cases = new Object[][] {
            /*            {field,
                          {valid values},
                          {invalid values}} */

            new Object[] {"string",
                          new Object[] {"abcdefg", "ijklm\nopqrstx", null},
                          new Object[] {}},
            new Object[] {"line",
                          new Object[] {"abcdefg", new Integer(40), new Float(3.141592), null},
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
            new Object[] {"email",
                          new Object[] {"a@bla.nl", null},
                          new Object[] {"abaxbab"}},
            new Object[] {"stringrange",
                          new Object[] {"a", "ab", "xyzab", "zzzzz", "zzzzaaaaa", null},
                          new Object[] {"", "zzzzza", "\na"}},
            new Object[] {"stringlength",
                          new Object[] {"a", "0123456789",  "123456789\n", "\n123456789", null},
                          new Object[] {"",  "bbbbbbbbbbb", "123456789\n\n"}},
            new Object[] {"required_stringlength",
                          new Object[] {"aaa", "0123456789",  "123456789\n", "\n123456789"},
                          new Object[] {null, "",  "bbbbbbbbbbb", "123456789\n\n"}},
            new Object[] {"required_string",
                          new Object[] {"aaa", "0123456789",  "123456789\n", "\n123456789", ""},
                          new Object[] {null}}
            ,
            new Object[] {"required_legacy",
                          new Object[] {"aaa", "0123456789",  "123456789\n", "\n123456789", ""},
                          new Object[] {null}}
            ,
            new Object[] {"languages",
                          new Object[] {"nl", "en", null},
                          new Object[] {"c", "ababab", ""}}
            ,
            new Object[] {"integer",
                          new Object[] {new Integer(-100), "-1", new Integer(100), "-100", new Float(10.0), "1234", "1234.4", "1e7",  null, ""},
                          new Object[] {new Long(Long.MAX_VALUE), "1e30",  "asdfe"}
            },
            new Object[] {"duration",
                          new Object[] { new Integer(100), "100", new Float(10.0), "1234", "1234.4", "1e7",  null, "", "10:10:10", new Long(Long.MAX_VALUE)},
                          new Object[] { "1e50",  "asdfe", "-100", new Integer(-100) }
            }
            ,
            new Object[] {"duration_required",
                          new Object[] { new Integer(100), "100", new Float(10.0), "1234", "1234.4", "1e7", "10:10:10", new Long(Long.MAX_VALUE)},
                          new Object[] { "1e50",  "asdfe", "-100", new Integer(-100), null, "" }
            }
            ,
            new Object[] {"range",
                          new Object[] {new Integer(5), "1", "6.0", new Float(2.0), null},
                          new Object[] {"-1", "11", "xyz", new Integer(0), new Integer(10)}},
            new Object[] {"datetime",
                          new Object[] {new Date(), "2005-01-01", DynamicDate.getInstance("now - 5 year"), null},
                          new Object[] {"xxx"}},
            new Object[] {"period",
                          new Object[] {new Date(), "2005-01-01", "2006-01-01", null},
                          new Object[] {"1973-03-05", "2050-01-01"}},
            new Object[] {"dynamic_period",
                          new Object[] {new Date(), "today + 100 year", null},
                          new Object[] {"now - 4 day", "today + 101 year"}},
            new Object[] {"integer_datetime",
                          new Object[] {new Date(), "2005-01-01", DynamicDate.getInstance("now - 5 year"), new Integer(Integer.MAX_VALUE), null},
                          new Object[] {"xxx", "2100-01-01", DynamicDate.getInstance("now + 100 year"), new Long(Long.MAX_VALUE)}},
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
                          new Object[] {"2", "4", new Integer(6), null, new Double(1.0), "1.0", "1e20", null, ""},
                          new Object[] {new Double(Double.POSITIVE_INFINITY), "bla bla"
                          }},
            new Object[] {"handle",
                          new Object[] {null, binary // UNDETERMINED so valid, or at least unknnown
                          },
                          new Object[] {new byte[] {1, 2} //oo short
                          }
            },
            new Object[] {"image",
                          new Object[] {null, binary // UNDTERMINED so valid (or at least unknown)

                },
                          new Object[] {new byte[] {1, 2}// TODO think of invalid binaries.
                          }
            },
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
            new Object[] {"integer_string",
                          new Object[] {"1", "100", new Integer(10), new Integer(-1), "-1" , null},
                          new Object[] { "asjdlkf"}},
            new Object[] {"node",
                          new Object[] {node1 , node2,
                                        "" + node1.getNumber(), new Integer(node1.getNumber()), new Integer(node2.getNumber()),  new Integer(-1), null, ""
                },
                          new Object[] {"asjdlkf", new Integer(-2), new Integer(-100)
                          }
            }
            ,
            new Object[] {"typedef",
                          new Object[] {node1, new Integer(node1.getNumber()),  null, ""},
                          new Object[] {"asjdlkf", node3, new Integer(node3.getNumber()), new Integer(-2), new Integer(-100)}}
            ,
            new Object[] {"nonode_typedef",
                          new Object[] {"object", "typedef", "datatypes"},
                          new Object[] {"", "asjdlkf", node1}}
            ,
            new Object[] {"decimal",
                          new Object[] {"22222222222222222222222222222222222.111111111111111111111111111111" , //35.30
                                        "1", new Integer(100),
                                        new BigDecimal("22222222222222222222222222222222222.1234")},
                          new Object[] {"333333333333333333333333333333333333", "asjdlkf"}}
            ,
            new Object[] {"currency",
                          new Object[] {"222222222222222.11111", //15.5
                                        "1", new Integer(100),
                                        new BigDecimal("1.123456789"), "12345.1111111111"},
            new Object[] {"3333333333333333", "asjdlkf"}}
            /*
              XML not very well supported yet
              new Object[] {"xml",
              new Object[] {"<p />",  null},
              new Object[] {"asjdlkf", new Integer(-1), new Integer(-100), new Float(2.0)}
              }
            */

        };
        return cases;
    }


    @Parameterized.Parameters
    public static Collection<Object[]> data() throws Exception {
        Cloud cloud = getCloud();

        List<Object[]> result = new ArrayList<Object[]>();
        for (Object[] kase : getCases(cloud, new byte[] {1, 2, 3, 4})) {
            String fieldName = (String) kase[0];
            Object[] validValues = (Object[]) kase[1];
            Object[] invalidValues = (Object[]) kase[2];
            for (Object validValue : validValues) {
                result.add(new Object[] {fieldName, validValue, true});
            }
            for (Object invalidValue : invalidValues) {
                result.add(new Object[] {fieldName, invalidValue, false});
            }
        }
        //result = result.subList(0, 10);
        //System.out.println("Found "+ result);
        return result;
    }


    private final String fieldName;
    private final Object value;
    private final boolean valid;
    public ParameterizedDataTypesTest(String field, Object value, boolean valid) {
        this.fieldName = field;
        this.value = value;
        this.valid = valid;
    }




    protected static void commit(Node node) {
        node.commit();
        if (node.getCloud() instanceof Transaction) {
            ((Transaction) node.getCloud()).commit();
        }
    }


    @Test
    public void testCheckValid() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        Field field = nodeManager.getField(fieldName);
        field.getDataType().validate(value); // REMARK1 should not give errors
        Collection<LocalizedString> errors = field.getDataType().validate(value, null, field);

        if (valid) {
            assertEquals(toString() + " is valid, but: " + errors, 0, errors.size());
        } else {
            assertTrue(toString() + " is not valid, but no error", errors.size() > 0);
        }
    }
    /**
     * MMB-1901
     */
    @Test
    public void defaultValue() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("datatypes");
        Field field = nodeManager.getField(fieldName);
        //System.out.println("Testing " + LocalizedString.getDefault() + " " + cloud.getLocale());
        assertEquals("For " + fieldName + " " + field.getDataType(), field.getDataType().getDefaultValue(), nodeManager.createNode().getValue(fieldName));
    }


    @Override
    public String toString() {
        return fieldName + ":" + value +
            (value instanceof byte[] ? (" (" + ((byte[]) value).length + " bytes)") : "") +
            " (" + (valid ? "valid" : "invalid") + ")";
    }




}
