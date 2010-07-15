package org.mmbase.storage.search.implementation;

import org.junit.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.storage.search.*;

import static org.junit.Assert.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Id$
 */
public class BasicStepFieldTest  {

    private final static String TEST_ALIAS = "abcd";

    private final static String BUILDER_NAME1 = "mags";
    private final static String BUILDER_NAME2 = "news";
    private final static String FIELD_NAME1 = "title";

    /** Test instance. */
    private BasicStepField instance;

    /** Associated step. */
    private BasicStep step;


    /** Builder examples. */
    private NodeManager builder1 = null;
    private NodeManager builder2 = null;

    /** CoreField example. */
    private Field field = null;

    private Cloud cloud;

    @BeforeClass
    public static void setUpClass() throws Exception {
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addCoreModel();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("resources"));
    }


    /**
     * Sets up before each test.
     */
    @Before
    public void setUp() throws Exception {
        cloud = MockCloudContext.getInstance().getCloud("mmbase");
        builder1 = cloud.getNodeManager(BUILDER_NAME1);
        builder2 = cloud.getNodeManager(BUILDER_NAME2);
        field = builder1.getField(FIELD_NAME1);
        step = new BasicStep(builder1.getName());
        instance = new BasicStepField(step, field);
    }


    /** Test of constructor. **/
    @Test
    public void testConstructor() {
        Step step2 = new BasicStep(builder2.getName());
        // CoreField object does not belong to step, should throw IllegalArgumentException.
        try {
            new BasicStepField(step2, field);
            fail("CoreField object does not belong to step, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        // Null step, should throw IllegalArgumentException.
        try {
            new BasicStepField(null, field);
            fail("Null step, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        // Null field, should throw IllegalArgumentException.
        try {
            new BasicStepField(step2, null);
            fail("Null field, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /** Test of setAlias method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    @Test
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
    @Test
    public void testGetFieldName() {
        String fieldName = instance.getFieldName();
        assertTrue(fieldName != null);
        assertTrue(fieldName.equals(FIELD_NAME1));
    }

    /** Test of getAlias method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    //@Test
    public void testGetAlias() {
        // Same as:
        testSetAlias();
    }

    /** Test of getStep method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    @Test
    public void testGetStep() {
        assertTrue(instance.getStep() == step);
    }

    /** Test of getType method, of class org.mmbase.storage.search.implementation.BasicStepField.
     */
    @Test
    public void testGetType() {

        NodeManager images = cloud.getNodeManager("images");
        Field imagesNumber = images.getField("number");
        Field imagesOwner = images.getField("owner");
        Field imagesTitle = images.getField("title");
        Field imagesDescription = images.getField("description");
        Field imagesHandle = images.getField("handle");
        Field imagesItype = images.getField("itype");

        Step step = new BasicStep(images.getName());
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
    @Test
    public void testTestValue() {
        NodeManager images = cloud.getNodeManager("images");
        Field imagesNumber = images.getField("number");
        Field imagesHandle = images.getField("handle");
        Field imagesOwner = images.getField("owner");

        Step step = new BasicStep(images.getName());
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
    @Test
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
    //@Test
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    //@Test
    public void testHashCode() {
        // TODO: implement test
    }

    /** Test of toString method, of class org.mmbase.storage.search.implementation.BasicStepField. */
    @Test
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


}
