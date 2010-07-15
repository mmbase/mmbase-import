package org.mmbase.storage.search.implementation;

import org.junit.*;
import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;

import static org.junit.Assert.*;

/**
 * JUnit tests.
 *
 * @author Rob van Maris
 * @version $Id$
 */
public class BasicCompositeConstraintTest  {

    private final static int TEST_OPERATOR = CompositeConstraint.LOGICAL_AND;

    /** Test instance. */
    private BasicCompositeConstraint instance = null;


    /**
     * Sets up before each test.
     */
    @Before
    public void setUp() throws Exception {
        instance = new BasicCompositeConstraint(TEST_OPERATOR);
    }


    /** Test of addChild method, of class org.mmbase.storage.search.implementation.BasicCompositeConstraint. */
    @Test
    public void testAddChild() {
        // Null child, should throw IllegalArgumentException.
        try {
            instance.addChild(null);
            fail("Null child, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        // Trying to add constraint as child to itself, should throw IllegalArgumentException.
        try {
            instance.addChild(instance);
            fail("Trying to add constraint as child to itself, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}

        List<Constraint> childs = instance.getChilds();
        assertTrue(childs.size() == 0);

        Constraint constraint1 = new BasicConstraint();
        instance.addChild(constraint1);
        childs = instance.getChilds();
        assertTrue(childs.size() == 1);
        assertTrue(childs.get(0).equals(constraint1));
        Constraint constraint2 = new BasicConstraint();
        BasicCompositeConstraint result = instance.addChild(constraint2);
        childs = instance.getChilds();
        assertTrue(childs.size() == 2);
        assertTrue(childs.get(0).equals(constraint1));
        assertTrue(childs.get(1).equals(constraint2));
        assertTrue(result == instance);
    }

    /** Test of BasicCompositeConstraint(int) */
    @Test
    public void testConstructor() {
        // Invalid logical operator value, should throw IllegalArgumentException.
        try {
            new BasicCompositeConstraint(CompositeConstraint.LOGICAL_OR + 10);
            fail("Invalid logical operator value, should throw IllegalArgumentException.");
        } catch (IllegalArgumentException e) {}
    }

    /** Test of getChilds method, of class org.mmbase.storage.search.implementation.BasicCompositeConstraint. */
    @Test
    public void testGetChilds() {
        // See:
        testAddChild();

        List<Constraint> childs = instance.getChilds();
        Constraint item = new BasicConstraint();

        // List returned must be unmodifiable.
        try {
            childs.add(item);
            fail("Attempt to modify list, must throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {}
        try {
            childs.clear();
            fail("Attempt to modify list, must throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {}
    }

    /** Test of getLogicalOperator method, of class org.mmbase.storage.search.implementation.BasicCompositeConstraint. */
    @Test
    public void testGetLogicalOperator() {
        assertTrue(instance.getLogicalOperator() == TEST_OPERATOR);
    }


    /** Test of equals method, of class org.mmbase.storage.search.implementation.BasicCompositeConstraint. */
    //@Test
    public void testEquals() {
        // TODO: implement test
    }

    /** Test of hashCode method, of class org.mmbase.storage.search.implementation.BasicCompositeConstraint. */
    //@Test
    public void testHashCode() {
        // TODO: implement test
    }

    /** Test of getBasicSupportLevel method, of class org.mmbase.storage.search.implementation.BasicCompositeConstraint. */
    @Test
    public void testGetBasicSupportLevel() {
        // No childs: optimal support.
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_OPTIMAL);
        instance.addChild(new TestConstraint(SearchQueryHandler.SUPPORT_OPTIMAL));
        // Lowest support among childs.
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_OPTIMAL);
        instance.addChild(new TestConstraint(SearchQueryHandler.SUPPORT_NORMAL));
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_NORMAL);
        instance.addChild(new TestConstraint(SearchQueryHandler.SUPPORT_OPTIMAL));
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_NORMAL);
        instance.addChild(new TestConstraint(SearchQueryHandler.SUPPORT_WEAK));
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_WEAK);
        instance.addChild(new TestConstraint(SearchQueryHandler.SUPPORT_OPTIMAL));
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_WEAK);
        instance.addChild(new TestConstraint(SearchQueryHandler.SUPPORT_NONE));
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_NONE);
        instance.addChild(new TestConstraint(SearchQueryHandler.SUPPORT_OPTIMAL));
        assertTrue(instance.getBasicSupportLevel() == SearchQueryHandler.SUPPORT_NONE);
    }



}
