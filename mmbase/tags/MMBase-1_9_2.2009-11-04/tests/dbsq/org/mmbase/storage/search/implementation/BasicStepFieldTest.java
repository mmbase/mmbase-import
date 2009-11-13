package org.mmbase.storage.search.implementation;

import junit.framework.*;

import org.mmbase.bridge.Field;
import org.mmbase.core.CoreField;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Revision: 1.10 $
 */
public class BasicStepFieldTest extends TestCase {

    private final static String TEST_ALIAS = "abcd";

    private final static String BUILDER_NAME1 = "images";
    private final static String BUILDER_NAME2 = "news";
    private final static String FIELD_NAME1 = "title";

    /** Test instance. */
    private BasicStepField instance;

    /** Associated step. */
    private BasicStep step;

    /** MMBase instance. */
    private MMBase mmbase = null;

    /** Builder examples. */
    private MMObjectBuilder builder1 = null;
    private MMObjectBuilder builder2 = null;

    /** CoreField example. */
    private CoreField CoreField = null;

    public BasicStepFieldTest(java.lang.String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    /**
     * Sets up before each test.
     */
    public void setUp() throws Exception {
        MMBaseContext.init();
        mmbase = MMBase.getMMBase();
        builder1 = mmbase.getBuilder(BUILDER_NAME1);
        builder2 = mmbase.getBuilder(BUILDER_NAME2);
        CoreField = builder1.getField(FIELD_NAME1);
        step = new BasicStep(builder1);
        instance = new BasicStepField(step, CoreField);
    }

    /**
     * Tears down after each test.
     */
    public void tearDown() throws Exception {}

    /** Test of constructor. **/
    public void testConstructor() {
        Step step2 = new BasicStep(builder2);
        // CoreField object does not belong to step, should throw IllegalArgumentException.
        try {
            new BasicStepField(step2, CoreField);
            fail("CoreField object does not belong to step, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        // Null step, should throw IllegalArgumentException.
        try {
            new BasicStepField(null, CoreField);
            fail("Null step, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        // Null field, should throw IllegalArgumentException.
        try {
            new BasicStepField(step2, null);
            fail("Null field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /** Test of setAlias method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    public void testSetAlias() {
        // Default is null.
        assertTrue(instance.getAlias() == null);

        BasicStepField result = instance.setAlias(TEST_ALIAS);
        String alias = instance.getAlias();
        assertTrue(alias != null);
        assertTrue(alias.equals(TEST_ALIAS));
        assertTrue(result == instance);

        // Null value, should not throw IllegalArgumentException.
        instance.setAlias(null);
        assertTrue(instance.getAlias() == null);

        // Blank spaces, should throw IllegalArgumentException.
        try {
            instance.setAlias("   ");
            fail("Null value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /** Test of getFieldName method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    public void testGetFieldName() {
        String fieldName = instance.getFieldName();
        assertTrue(fieldName != null);
        assertTrue(fieldName.equals(FIELD_NAME1));
    }

    /** Test of getAlias method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    public void testGetAlias() {
        // Same as:
        testSetAlias();
    }

    /** Test of getStep method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    public void testGetStep() {
        assertTrue(instance.getStep() == step);
    }

    /** Test of getType method, of class org.mmbase.storage.search.implementation.BasicStepField.
     */
    public void testGetType() {

        MMObjectBuilder images = mmbase.getBuilder("images");
        CoreField imagesNumber = images.getField("number");
        CoreField imagesOwner = images.getField("owner");
        CoreField imagesTitle = images.getField("title");
        CoreField imagesDescription = images.getField("description");
        CoreField imagesHandle = images.getField("handle");
        CoreField imagesItype = images.getField("itype");

        Step step = new BasicStep(images);
        instance = new BasicStepField(step, imagesNumber);
        assertTrue(instance.getType() == Field.TYPE_NODE);

        instance = new BasicStepField(step, imagesOwner);
        assertTrue(instance.getType() == Field.TYPE_STRING);

        instance = new BasicStepField(step, imagesTitle);
        assertTrue(instance.getType() == Field.TYPE_STRING);

        instance = new BasicStepField(step, imagesDescription);
        assertTrue(instance.getType() == Field.TYPE_STRING);

        instance = new BasicStepField(step, imagesHandle);
        assertTrue(instance.getType() == Field.TYPE_BINARY);

        instance = new BasicStepField(step, imagesItype);
        assertTrue(instance.getType() == Field.TYPE_STRING);
    }

    /** Test of testValue method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    public void testTestValue() {
        MMObjectBuilder images = mmbase.getBuilder("images");
        CoreField imagesNumber = images.getField("number");
        CoreField imagesHandle = images.getField("handle");
        CoreField imagesOwner = images.getField("owner");

        Step step = new BasicStep(images);
        // NODE type field.
        instance = new BasicStepField(step, imagesNumber);
        BasicStepField.testValue(new Integer(123), instance);
        BasicStepField.testValue(new Long(123), instance);
        BasicStepField.testValue(new Float(123), instance);
        BasicStepField.testValue(new Double(123), instance);
        try {
            // String value for NODE field, should throw IllegalArgumentException.
            BasicStepField.testValue("123", instance);
            fail("String value for NODE field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            BasicStepField.testValue(new byte[] {(byte) 123}, instance);
            fail("byte[] value for NODE field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        // BYTE type field.
        instance = new BasicStepField(step, imagesHandle);
        BasicStepField.testValue(new byte[] {(byte) 123}, instance);
        try {
            // String value for BYTE field, should throw IllegalArgumentException.
            BasicStepField.testValue(new Integer(123), instance);
            fail("Integer value for BYTE field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            BasicStepField.testValue(new Long(123), instance);
            fail("Long value for BYTE field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        try {
            BasicStepField.testValue(new Double(123), instance);
            fail("Double value for BYTE field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            BasicStepField.testValue("123", instance);
            fail("String value for BYTE field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}


        // STRING type field.
        instance = new BasicStepField(step, imagesOwner);
        try {
            // Byte value for STRING field, should throw IllegalArgumentException.
            BasicStepField.testValue(new byte[] {(byte) 123}, instance);
            fail("Byte value for STRING field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            // Integer value for STRING field, should throw IllegalArgumentException.
            BasicStepField.testValue(new Integer(123), instance);
            fail("Integer value for STRING field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            // Long value for STRING field, should throw IllegalArgumentException.
            BasicStepField.testValue(new Long(123), instance);
            fail("Long value for STRING field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            // Float value for STRING field, should throw IllegalArgumentException.
            BasicStepField.testValue(new Float(123), instance);
            fail("Float value for STRING field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        try {
            // Double value for STRING field, should throw IllegalArgumentException.
            BasicStepField.testValue(new Double(123), instance);
            fail("Double value for STRING field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
        BasicStepField.testValue("123", instance);
    }

    /** Test of equalFieldValues method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    public void testEqualFieldValues() {
        assertTrue(BasicStepField.equalFieldValues(null, null));
        assertTrue(!BasicStepField.equalFieldValues("abc def", null));
        assertTrue(!BasicStepField.equalFieldValues(null, "abc def"));
        assertTrue(BasicStepField.equalFieldValues("abc def", "abc def"));
        assertTrue(!BasicStepField.equalFieldValues(new Integer(123), "abc def"));
        assertTrue(!BasicStepField.equalFieldValues("abc def", new Integer(123)));
        assertTrue(BasicStepField.equalFieldValues(new Integer(123), new Integer(123)));
        assertTrue(BasicStepField.equalFieldValues(new Double(123), new Integer(123)));
        assertTrue(BasicStepField.equalFieldValues(new Integer(123), new Double(123)));
        assertTrue(BasicStepField.equalFieldValues(new Double(123), new Double(123)));
        assertTrue(!BasicStepField.equalFieldValues(null, new Double(123)));
        assertTrue(!BasicStepField.equalFieldValues(new Double(123), null));
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    public void testHashCode() {
        // TODO: implement test
    }

    /** Test of toString method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    public void testToString() {
        // Null alias.
        assertTrue(instance.toString(),
                   instance.toString().equals(step.getTableName() + "." + instance.getFieldName()));
        // Set step alias.
        step.setAlias("abdef");
        assertTrue(instance.toString(),
                   instance.toString().equals(step.getAlias() + "." + instance.getFieldName()));

        // Set field alias.
        instance.setAlias("fedbac");
        assertTrue(instance.toString(),
                   instance.toString().equals(step.getAlias() + "." + instance.getFieldName() + " as " + instance.getAlias()));
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(BasicStepFieldTest.class);

        return suite;
    }

}
