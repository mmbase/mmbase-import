package org.mmbase.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Query;
import org.mmbase.bridge.util.Queries;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.NodeEventHelper;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.tests.BridgeTest;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class ConstraintMatcherTest extends BridgeTest {
    static protected Cloud cloud = null;

    private static final Logger log = Logging.getLoggerInstance(ConstraintMatcherTest.class);

    static protected ConstraintsMatchingStrategy matchingStrategy;

    public ConstraintMatcherTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        if (cloud == null) {
            startMMBase();
            cloud = getCloud();
        }
        matchingStrategy = new ConstraintsMatchingStrategy();
    }

    protected static Map createMap(Object[][] objects) {
        Map map = new HashMap();
        for (int i = 0; i < objects.length; i++) {
            map.put(objects[i][0], objects[i][1]);
        }
        return map;
    }

    public void testBasicCompositeConstraintMatcher() {

    }

    public void testBasicFieldValueConstraintMatcherString() {

        // type string. Allso tests non-matching events (different step, different field), and non-matching datatypes
        Query q1 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string = 'disco'", null, null, null, false);

        NodeEvent event1a = new NodeEvent(null, "datatypes", 10, createMap(new String[][] { { "string", "disco" } }), createMap(new String[][] { { "string", "disco" } }), NodeEvent.EVENT_TYPE_CHANGED);
        NodeEvent event1b = new NodeEvent(null, "datatypes", 10, createMap(new String[][] { { "string", "disco" } }), createMap(new String[][] { { "string", "something" } }), NodeEvent.EVENT_TYPE_CHANGED);
        NodeEvent event1c = new NodeEvent(null, "datatypes", 10, new HashMap(), createMap(new String[][] { { "string", "disco" } }), NodeEvent.EVENT_TYPE_CHANGED);
        NodeEvent event1d = new NodeEvent(null, "datatypes", 10, createMap(new String[][] { { "string", "bobo" } }), createMap(new String[][] { { "string", "something" } }), NodeEvent.EVENT_TYPE_CHANGED);
        NodeEvent event1e = new NodeEvent(null, "datatypes", 10, createMap(new String[][] { { "string", "bobo" } }), createMap(new String[][] { { "string", "disco" } }), NodeEvent.EVENT_TYPE_CHANGED);
        NodeEvent event1f = new NodeEvent(null, "news", 10, createMap(new String[][] { { "string", "bobo" } }), createMap(new String[][] { { "string", "disco" } }), NodeEvent.EVENT_TYPE_CHANGED);
        NodeEvent event1g = new NodeEvent(null, "datatypes", 10, createMap(new Object[][] { { "integer", new Integer(3) } }), createMap(new String[][] { { "string", "disco" } }), NodeEvent.EVENT_TYPE_CHANGED);
        NodeEvent event1h = new NodeEvent(null, "datatypes", 10, createMap(new Object[][] { { "string", new Boolean(true) } }), createMap(new String[][] { { "string", "disco" } }), NodeEvent.EVENT_TYPE_CHANGED);
        NodeEvent event1i = new NodeEvent(null, "datatypes", 10, createMap(new Object[][] { { "string", new ArrayList() } }), createMap(new String[][] { { "string", "disco" } }), NodeEvent.EVENT_TYPE_CHANGED);

        // constraint matching tests
        // first tetst type 'changed'
        assertFalse("Changed node falls within constraints before, and within constraints after event. no flush", matchingStrategy.evaluate(event1a, q1, null).shouldRelease());
        assertTrue("Changed node falls within constraints before, but outside constraints after event. flush", matchingStrategy.evaluate(event1b, q1, null).shouldRelease());
        assertFalse("Changed node falls outside constraint before and outside constraint after event: no flush", matchingStrategy.evaluate(event1d, q1, null).shouldRelease());
        assertTrue("Changed node falls outside constraint before but within constraints after: flush", matchingStrategy.evaluate(event1e, q1, null).shouldRelease());

        // non-matching step and field
        assertTrue("Changed node's type dous not match the constraint's step: flush", matchingStrategy.evaluate(event1f, q1, null).shouldRelease());
        assertTrue("Changed node's changed field dous not match the constraint's field: flush", matchingStrategy.evaluate(event1g, q1, null).shouldRelease());
        // empty values map- exception
        assertTrue("A FieldComparisonException occurs: flush", matchingStrategy.evaluate(event1c, q1, null).shouldRelease());

        // wrong datatype
        assertTrue("Datatype Boolean dous not match field integer: flush", matchingStrategy.evaluate(event1h, q1, null).shouldRelease());
        // unsupported datatype
        assertTrue("Datatype 'List' is not supported: don't flush", matchingStrategy.evaluate(event1i, q1, null).shouldRelease());

        // test different operators
        NodeEvent event2a = new NodeEvent(null, "datatypes", 10, null, createMap(new String[][] { { "string", "a" } }), NodeEvent.EVENT_TYPE_NEW);
        NodeEvent event2b = new NodeEvent(null, "datatypes", 10, null, createMap(new String[][] { { "string", "c" } }), NodeEvent.EVENT_TYPE_NEW);
        
        Query q2 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string > 'b'", null, null, null, false);
        Query q3 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string < 'b'", null, null, null, false);
        Query q4 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string != 'c'", null, null, null, false);
        Query q5 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string >= 'b'", null, null, null, false);
        Query q6 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string <= 'b'", null, null, null, false);
        
//       >
        assertTrue("(>) New node falls within constraint: flush", matchingStrategy.evaluate(event2b, q2, null).shouldRelease());
        assertFalse("(>) New node falls outside constraint: don't flush", matchingStrategy.evaluate(event2a, q2, null).shouldRelease());
//      <
        assertTrue("(<) New node falls within constraint: flush", matchingStrategy.evaluate(event2a, q3, null).shouldRelease());
        assertFalse("(<) New node falls outside constraint: don't flush", matchingStrategy.evaluate(event2b, q3, null).shouldRelease());
//      !=
        assertTrue("(!=) New node falls within constraint: flush", matchingStrategy.evaluate(event2a, q4, null).shouldRelease());
        assertFalse("(!=) New node falls outside constraint: don't flush", matchingStrategy.evaluate(event2b, q4, null).shouldRelease());
//      >=
        assertTrue("(>=) New node falls within constraint: flush", matchingStrategy.evaluate(event2b, q5, null).shouldRelease());
        assertFalse("(>=) New node falls outside constraint: don't flush", matchingStrategy.evaluate(event2a, q5, null).shouldRelease());
//      <=
        assertTrue("(<=) New node falls within constraint: flush", matchingStrategy.evaluate(event2a, q6, null).shouldRelease());
        assertFalse("(<=) New node falls outside constraint: don't flush", matchingStrategy.evaluate(event2b, q6, null).shouldRelease());
    }
    
    
    

    public void testBasicFieldValueConstraintMatcherInt() {
        // type integer. allso tests event types delete and new
        Query query1 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "datatypes.integer < 3", null, null, null, false);

        NodeEvent event2a = new NodeEvent(null, "datatypes", 10, createMap(new Object[][] { { new String("integer"), new Integer(2) } }), createMap(new Object[][] { { new String("integer"), new Integer(1) } }), NodeEvent.EVENT_TYPE_CHANGED);
        NodeEvent event2b = new NodeEvent(null, "datatypes", 10, createMap(new Object[][] { { new String("integer"), new Integer(2) } }), createMap(new Object[][] { { new String("integer"), new Integer(10) } }), NodeEvent.EVENT_TYPE_CHANGED);
        NodeEvent event2c = new NodeEvent(null, "datatypes", 10, createMap(new Object[][] { { new String("integer"), new Integer(10) } }), createMap(new Object[][] { { new String("integer"), new Integer(100) } }), NodeEvent.EVENT_TYPE_CHANGED);
        NodeEvent event2d = new NodeEvent(null, "datatypes", 10, createMap(new Object[][] { { new String("integer"), new Integer(3) } }), createMap(new Object[][] { { new String("integer"), new Integer(2) } }), NodeEvent.EVENT_TYPE_CHANGED);

        // when the event type is 'new' the new values should be checked against the constraint
        NodeEvent event2e = new NodeEvent(null, "datatypes", 10, null, createMap(new Object[][] { { new String("integer"), new Integer(2) } }), NodeEvent.EVENT_TYPE_NEW);
        NodeEvent event2f = new NodeEvent(null, "datatypes", 10, null, createMap(new Object[][] { { new String("integer"), new Integer(4) } }), NodeEvent.EVENT_TYPE_NEW);

        // event type Delete
        NodeEvent event2g = new NodeEvent(null, "datatypes", 10, createMap(new Object[][] { { new String("integer"), new Integer(2) } }), null, NodeEvent.EVENT_TYPE_DELETE);
        NodeEvent event2h = new NodeEvent(null, "datatypes", 10, createMap(new Object[][] { { new String("integer"), new Integer(4) } }), null, NodeEvent.EVENT_TYPE_DELETE);

        // operator >
        Query query2 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "datatypes.integer > 3", null, null, null, false);
        // operator ==
        Query query3 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "datatypes.integer = 2", null, null, null, false);
        // operator >=
        Query query4 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "datatypes.integer >= 4", null, null, null, false);
        // operator <=
        Query query5 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "datatypes.integer <= 3", null, null, null, false);
        // operator <=
        Query query6 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "datatypes.integer != 4", null, null, null, false);

        assertFalse("Changed node falls within constraints before, and within constraints after event. no flush,", matchingStrategy.evaluate(event2a, query1, null).shouldRelease());
        assertTrue("Changed node falls within constraints before, but outside constraints after event. flush", matchingStrategy.evaluate(event2b, query1, null).shouldRelease());
        assertFalse("Changed node falls outside constraint before and outside constraint after event: no flush", matchingStrategy.evaluate(event2c, query1, null).shouldRelease());
        assertTrue("Changed node falls outside constraint before but within constraints after: flush", matchingStrategy.evaluate(event2d, query1, null).shouldRelease());

        // try event type New
        assertTrue("New node falls within constraint: flush", matchingStrategy.evaluate(event2e, query1, null).shouldRelease());
        assertFalse("New node falls outside constraint: don't flush", matchingStrategy.evaluate(event2f, query1, null).shouldRelease());
        // try event type Delete
        assertTrue("Old node falls within constraint: flush", matchingStrategy.evaluate(event2g, query1, null).shouldRelease());
        assertFalse("Old node falls outside constraint: don't flush", matchingStrategy.evaluate(event2h, query1, null).shouldRelease());

        // different operators:
        // >
        assertTrue("(>) New node falls within constraint: flush", matchingStrategy.evaluate(event2f, query2, null).shouldRelease());
        assertFalse("(>) New node falls outside constraint: don't flush", matchingStrategy.evaluate(event2e, query2, null).shouldRelease());
        // ==
        assertTrue("(==) New node falls within constraint: flush", matchingStrategy.evaluate(event2e, query3, null).shouldRelease());
        assertFalse("(==) New node falls outside constraint: don't flush", matchingStrategy.evaluate(event2f, query3, null).shouldRelease());
        // >=
        assertTrue("(>=) New node falls within constraint: flush", matchingStrategy.evaluate(event2f, query4, null).shouldRelease());
        assertFalse("(>=) New node falls outside constraint: don't flush", matchingStrategy.evaluate(event2e, query4, null).shouldRelease());
        // <=
        assertTrue("(<=) New node falls within constraint: flush", matchingStrategy.evaluate(event2e, query5, null).shouldRelease());
        assertFalse("(<=) New node falls outside constraint: don't flush", matchingStrategy.evaluate(event2f, query5, null).shouldRelease());
        // !=
        assertTrue("(!=) New node falls within constraint: flush", matchingStrategy.evaluate(event2e, query6, null).shouldRelease());
        assertFalse("(!=) New node falls outside constraint: don't flush", matchingStrategy.evaluate(event2f, query6, null).shouldRelease());
    }
    
    public void testCompositeMatcherAndString(){
    	MMBase mmbase = MMBase.getMMBase();
    	MMObjectBuilder newsBuilder = mmbase.getBuilder("news");
    	MMObjectNode node = newsBuilder.getNewNode("system");
    	node.setValue("title", "test");
    	node.setValue("subtitle", "testsub");
    	node.commit();
    	
    	node.setValue("title", "newtTitle");
    	NodeEvent event = NodeEventHelper.createNodeEventInstance(node, NodeEvent.EVENT_TYPE_CHANGED, null);
    	
    	Query query1 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.number = 10 AND mags.title='test'", null, null, null, false);
    	
    	NodeEvent event1 = new NodeEvent(null, "mags", 10, null, createMap(new Object[][] { { new String("title"), "test" } }), NodeEvent.EVENT_TYPE_NEW);
    	NodeEvent event2 = new NodeEvent(null, "mags", 11, null, createMap(new Object[][] { { new String("title"), "test" } }), NodeEvent.EVENT_TYPE_NEW);
    	NodeEvent event3 = new NodeEvent(null, "mags", 10, null, createMap(new Object[][] { { new String("title"), "testhalo" } }), NodeEvent.EVENT_TYPE_NEW);
    	NodeEvent event4 = new NodeEvent(null, "mags", 10, null, createMap(new Object[][] { { new String("subtitle"), "test" } }), NodeEvent.EVENT_TYPE_NEW);
    	
    	assertTrue("both constraints match: flush", matchingStrategy.evaluate(event1, query1, null).shouldRelease());
    	assertFalse("node number dous not match: don't flush", matchingStrategy.evaluate(event2, query1, null).shouldRelease());
    	assertFalse("magazine title dous not match: don't flush", matchingStrategy.evaluate(event3, query1, null).shouldRelease());
    	assertFalse("fields constraints are on don't match: don't flush", matchingStrategy.evaluate(event2, query1, null).shouldRelease());
    }

}
