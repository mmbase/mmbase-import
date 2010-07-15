package org.mmbase.storage.search.implementation;

import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.mmbase.bridge.mock.*;
import org.mmbase.storage.search.Step;

/**
 *
 * @author Rob van Maris
 * @version $Id$
 */
public class BasicStepTest  {

    private final static String TEST_ALIAS = "abcd";

    private final static String BUILDER_NAME = "images";

    /** Test instance. */
    private BasicStep instance;

    @BeforeClass
    public static void setUpClass() throws Exception {
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addCoreModel();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("resources"));
    }

    /**
     * Sets up before each test.
     */
    @Before
    public void setUp() throws Exception {
        instance = new BasicStep(BUILDER_NAME);
    }


    /** Test of getTableName method, of class org.mmbase.storage.search.implementation.BasicStep. */
    @Test
    public void testGetTableName() {
        String tableName = instance.getTableName();
        assertTrue(tableName != null);
        assertTrue(tableName.equals(BUILDER_NAME));
    }

    /** Test of setAlias method, of class org.mmbase.storage.search.implementation.BasicStep. */
    @Test
    public void testSetAlias() {
        // Default is null.
        assertTrue(instance.getAlias() == null);

        BasicStep result = instance.setAlias(TEST_ALIAS);
        String alias = instance.getAlias();
        assertTrue(alias != null);
        assertTrue(alias.equals(TEST_ALIAS));
        assertTrue(result == instance);

        // Null value, should throw IllegalArgumentException.
        instance.setAlias(null);
        assertTrue(instance.getAlias() == null);

        // Blank spaces, should throw IllegalArgumentException.
        try {
            instance.setAlias("   ");
            fail("Null value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /** Test of getAlias method, of class org.mmbase.storage.search.implementation.BasicStep. */
    @Test
    public void testGetAlias() {
        // Same as:
        testSetAlias();
    }




    /** Test of addNode method, of class org.mmbase.storage.search.implementation.BasicStep. */
    @Test
    public void testAddNode() {
        SortedSet<Integer> nodes = instance.getNodes();
        assertNull(nodes);  	//  MMB-1682

        // Negative node, should not throw IllegalArgumentException
        instance.addNode(-10);


        int nodeNumber0 = 23456;
        instance.addNode(nodeNumber0);
        nodes = instance.getNodes();
        assertTrue(nodes.size() == 2);
        Iterator<Integer> iNodes = nodes.iterator();
        assertTrue(iNodes.hasNext());
        assertTrue(iNodes.next().equals(new Integer(-10)));
        assertTrue(iNodes.hasNext());
        assertTrue(iNodes.next().equals(new Integer(nodeNumber0)));
        assertTrue(!iNodes.hasNext());
        int nodeNumber1 = 2345;
        Step result = instance.addNode(nodeNumber1);
        nodes = instance.getNodes();
        assertTrue(nodes.size() == 3);
        iNodes = nodes.iterator();
        assertTrue(iNodes.hasNext());
        assertTrue(iNodes.next().equals(new Integer(-10)));
        assertTrue(iNodes.hasNext());
        assertTrue(iNodes.next().equals(new Integer(nodeNumber1)));
        assertTrue(iNodes.hasNext());
        assertTrue(iNodes.next().equals(new Integer(nodeNumber0)));
        assertTrue(!iNodes.hasNext());
        assertTrue(result == instance);
    }

    /** Test of getNodes method, of class org.mmbase.storage.search.implementation.BasicStep. */
    @Test
    public void testGetNodes() {
        // See:
        testAddNode();

        instance.setUnmodifiable();

        SortedSet<Integer> nodes = instance.getNodes();
        Integer item = nodes.first();

        // List returned must be unmodifiable.
        try {
            nodes.add(item);
            fail("Attempt to modify list, must throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {}
        try {
            nodes.clear();
            fail("Attempt to modify list, must throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {}
    }

    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicStep. */
    @Test
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicStep. */
    @Test
    public void testHashCode() {
        // TODO: implement test
    }

    /** Test of toString method, of class org.mmbase.storage.search.implementation.BasicStep. */
    @Test
    public void testToString() {
        // With default alias.
        assertTrue(instance.toString(),
        instance.toString().equals("Step(tablename:" + instance.getTableName()
        + ", alias:" + instance.getAlias() + ", nodes:"
        + instance.getNodes() + ")"));

        // With test alias.
        instance.setAlias(TEST_ALIAS);
        assertTrue(instance.toString(),
        instance.toString().equals("Step(tablename:" + instance.getTableName()
        + ", alias:" + instance.getAlias() + ", nodes:"
        + instance.getNodes() + ")"));

        // With nodes.
        instance.addNode(123)
            .addNode(3456);
        assertTrue(instance.toString(),
        instance.toString().equals("Step(tablename:" + instance.getTableName()
        + ", alias:" + instance.getAlias() + ", nodes:"
        + instance.getNodes() + ")"));
     }

    /** Test of getBuilder method, of class org.mmbase.storage.search.implementation.BasicStep. */
    //@Test
    public void testGetBuilder() {
        // Method was dropped
        //assertTrue(instance.getBuilder() == builder);
    }


}
