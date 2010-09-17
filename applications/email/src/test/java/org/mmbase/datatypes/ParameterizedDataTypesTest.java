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
            CLOUD_CONTEXT.addNodeManagers(ResourceLoader.getConfigurationRoot().getChildResourceLoader("builders/email"));

            System.out.println("Using for test " + CLOUD_CONTEXT);
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

        Object[][] cases = new Object[][] {
            /*            {field,
                          {valid values},
                          {invalid values}} */

            new Object[] {"from",
                          new Object[] {"michiel@foo.bar", "\"pietje puk\" <pietje.puk@foo.bar>"},
                          new Object[] {null}}
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



    @Test
    public void testCheckValid() {
        Cloud cloud = getCloud();
        NodeManager nodeManager = cloud.getNodeManager("email");
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
        NodeManager nodeManager = cloud.getNodeManager("email");
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
