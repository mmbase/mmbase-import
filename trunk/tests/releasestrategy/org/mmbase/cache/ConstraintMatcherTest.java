package org.mmbase.cache;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.core.CoreField;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.Event;
import org.mmbase.core.event.NodeEventHelper;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectBuilder;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.storage.search.*;
import org.mmbase.tests.BridgeTest;

public class ConstraintMatcherTest extends BridgeTest {
    static protected Cloud cloud = null;

    static private int nodeNumber = -1;

    static protected ConstraintsMatchingStrategy matchingStrategy;

    public ConstraintMatcherTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        if (cloud == null) {
            startMMBase();
            cloud = getCloud();
            Node newNode = cloud.getNodeManager("datatypes").createNode();
            newNode.commit();
            nodeNumber = newNode.getNumber();
            
        }
        matchingStrategy = new ConstraintsMatchingStrategy();
    }

    protected static Map<String, Object> createMap(Object[][] objects) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Object[] element : objects) {
            map.put((String) element[0], element[1]);
        }
        return map;
    }



    public void testBasicFieldValueConstraintMatcherString() {

        // type string. Allso tests non-matching events (different step, different field), and non-matching datatypes
        Query q1 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string = 'disco'", null, null, null, false);

        NodeEvent event1a = new NodeEvent(null, "datatypes", nodeNumber, createMap(new String[][] { { "string", "disco" } }), createMap(new String[][] { { "string", "disco" } }), Event.TYPE_CHANGE);
        NodeEvent event1b = new NodeEvent(null, "datatypes", nodeNumber, createMap(new String[][] { { "string", "disco" } }), createMap(new String[][] { { "string", "something" } }), Event.TYPE_CHANGE);
        NodeEvent event1c = new NodeEvent(null, "datatypes", nodeNumber, new HashMap<String, Object>(), createMap(new String[][] { { "string", "disco" } }), Event.TYPE_CHANGE);
        NodeEvent event1d = new NodeEvent(null, "datatypes", nodeNumber, createMap(new String[][] { { "string", "bobo" } }), createMap(new String[][] { { "string", "something" } }), Event.TYPE_CHANGE);
        NodeEvent event1e = new NodeEvent(null, "datatypes", nodeNumber, createMap(new String[][] { { "string", "bobo" } }), createMap(new String[][] { { "string", "disco" } }), Event.TYPE_CHANGE);
        NodeEvent event1f = new NodeEvent(null, "news",      nodeNumber, createMap(new String[][] { { "string", "bobo" } }), createMap(new String[][] { { "string", "disco" } }), Event.TYPE_CHANGE);
        NodeEvent event1g = new NodeEvent(null, "datatypes", nodeNumber, createMap(new Object[][] { { "integer", new Integer(3) } }), createMap(new String[][] { { "string", "disco" } }), Event.TYPE_CHANGE);
        NodeEvent event1h = new NodeEvent(null, "datatypes", nodeNumber, createMap(new Object[][] { { "string", new Boolean(true) } }), createMap(new String[][] { { "string", "disco" } }), Event.TYPE_CHANGE);
        NodeEvent event1i = new NodeEvent(null, "datatypes", nodeNumber, createMap(new Object[][] { { "string", new ArrayList<Object>() } }), createMap(new String[][] { { "string", "disco" } }), Event.TYPE_CHANGE);

        // constraint matching tests
        // first tetst type 'changed'
        assertTrue("Changed node falls within constraints before, and within constraints after event. flush", matchingStrategy.evaluate(event1a, q1, null).shouldRelease());
        assertTrue("Changed node falls within constraints before, but outside constraints after event. flush", matchingStrategy.evaluate(event1b, q1, null).shouldRelease());
        assertFalse("Changed node falls outside constraint before and outside constraint after event: no flush", matchingStrategy.evaluate(event1d, q1, null).shouldRelease());
        assertTrue("Changed node falls outside constraint before but within constraints after: flush", matchingStrategy.evaluate(event1e, q1, null).shouldRelease());

        // non-matching step and field
        assertTrue("Changed node's type does not match the constraint's step: flush", matchingStrategy.evaluate(event1f, q1, null).shouldRelease());
        assertTrue("Changed node's changed field does not match the constraint's field: flush", matchingStrategy.evaluate(event1g, q1, null).shouldRelease());
        // empty values map- exception
        assertTrue("A FieldComparisonException occurs: flush", matchingStrategy.evaluate(event1c, q1, null).shouldRelease());

        // wrong datatype
        assertTrue("Datatype Boolean does not match field integer: flush", matchingStrategy.evaluate(event1h, q1, null).shouldRelease());
        // unsupported datatype
        assertTrue("Datatype 'List' is not supported: don't flush", matchingStrategy.evaluate(event1i, q1, null).shouldRelease());

        // test different operators
        NodeEvent event2a = new NodeEvent(null, "datatypes", nodeNumber, null, createMap(new String[][] { { "string", "a" } }), Event.TYPE_NEW);
        NodeEvent event2b = new NodeEvent(null, "datatypes", nodeNumber, null, createMap(new String[][] { { "string", "c" } }), Event.TYPE_NEW);

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

        // test the like comparison
        NodeEvent event2c = new NodeEvent(null, "datatypes", nodeNumber, null, createMap(new String[][] { { "string", "abcd" } }), Event.TYPE_NEW);
        Query q7 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string like 'a' ", null, null, null, false);
        Query q8 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string like 'a?'", null, null, null, false);
        Query q9 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string like 'a%'", null, null, null, false);
        Query q10= Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string like'c'", null, null, null, false);
        Query q11= Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string like '?c?'", null, null, null, false);
        Query q12= Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string like '%c?'", null, null, null, false);
        Query q13= Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string like lower('ADBC')", null, null, null, false);
        Query q14= Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string like lower('%C?')", null, null, null, false);

        assertFalse("(like 1) no match: don't flush",  matchingStrategy.evaluate(event2c, q7, null).shouldRelease());
        assertFalse("(like 2) no match: don't flush",  matchingStrategy.evaluate(event2c, q8, null).shouldRelease());
        assertTrue("(like 3) matches: flush",             matchingStrategy.evaluate(event2c, q9, null).shouldRelease());
        assertFalse("(like 4) no match: don't flush",  matchingStrategy.evaluate(event2c, q10, null).shouldRelease());
        assertFalse("(like 5) no match: don't flush",  matchingStrategy.evaluate(event2c, q11, null).shouldRelease());
        assertTrue("(like 6) matches: flush",             matchingStrategy.evaluate(event2c, q12, null).shouldRelease());
        assertTrue("(like 7) matches: flush",             matchingStrategy.evaluate(event2c, q13, null).shouldRelease());
        assertTrue("(like 8) matches: flush",             matchingStrategy.evaluate(event2c, q14, null).shouldRelease());

        Query q16= Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "string!='xyz'", null, null, null, false);
        assertTrue("(not) matches: flush",             matchingStrategy.evaluate(event2c, q16, null).shouldRelease());

        Query q15= Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "NOT([string]='xyz')", null, null, null, false);
        assertTrue("(not) matches: flush",             matchingStrategy.evaluate(event1a, q15, null).shouldRelease());
        assertTrue("(not) matches: flush",             matchingStrategy.evaluate(event2c, q15, null).shouldRelease());

    }


    public void testBasicFieldValueConstraintMatcherBoolean(){

        MMBase mmbase = MMBase.getMMBase();
        MMObjectBuilder builder = mmbase.getBuilder("datatypes");
        MMObjectNode node = builder.getNewNode("system");
        node.setValue("boolean", new Boolean(true));
        node.setValue("checksum", "jewjkekwekk");
        builder.insert("system", node);

        NodeEvent event1 = NodeEventHelper.createNodeEventInstance(node, Event.TYPE_NEW, null);


        BasicSearchQuery query3 = new BasicSearchQuery(false);
        BasicStep s1 = query3.addStep(builder);
        CoreField bField = builder.getField("boolean");
        BasicStepField sf1 = query3.addField(s1, bField);
        BasicFieldValueConstraint  c1 = new BasicFieldValueConstraint(sf1, new Boolean(true));
        c1.setOperator(FieldCompareConstraint.EQUAL);
        query3.setConstraint(c1);

        BasicSearchQuery query4 = (BasicSearchQuery) query3.clone();
        BasicFieldValueConstraint c2 = (BasicFieldValueConstraint) query4.getConstraint();
        c2.setValue(new Boolean(false));


        assertTrue("boolean value does match:  flush",              matchingStrategy.evaluate(event1, query3, null).shouldRelease());
        assertFalse("boolean value dus not match: don't flush",     matchingStrategy.evaluate(event1, query4, null).shouldRelease());
    }



    public void testBasicFieldValueConstraintMatcherNode(){

        MMBase mmbase = MMBase.getMMBase();
        MMObjectBuilder builder = mmbase.getBuilder("datatypes");

        MMObjectNode node = builder.getNewNode("system");
        builder.insert("system", node);


        MMObjectNode othernode = builder.getNewNode("system");
        builder.insert("system", othernode);

        MMObjectNode node1 = builder.getNewNode("system");
        node1.setValue("node",node);
        builder.insert("system", node1);

        NodeEvent event1 = NodeEventHelper.createNodeEventInstance(node1, Event.TYPE_NEW, null);


        BasicSearchQuery query3 = new BasicSearchQuery(false);
        BasicStep s1 = query3.addStep(builder);
        CoreField bField = builder.getField("node");
        BasicStepField sf1 = query3.addField(s1, bField);
        BasicFieldValueConstraint  c1 = new BasicFieldValueConstraint(sf1, new Integer(node.getNumber()));
        c1.setOperator(FieldCompareConstraint.EQUAL);
        query3.setConstraint(c1);

        BasicSearchQuery query4 = (BasicSearchQuery) query3.clone();
        BasicFieldValueConstraint c2 = (BasicFieldValueConstraint) query4.getConstraint();
        c2.setValue(new Integer(othernode.getNumber()));

        assertTrue("node value does match:  flush",              matchingStrategy.evaluate(event1, query3, null).shouldRelease());
        assertFalse("node value dus not match: don't flush",     matchingStrategy.evaluate(event1, query4, null).shouldRelease());
    }


    public void testBasicFieldValueConstraintMatcherDate(){
        MMBase mmbase = MMBase.getMMBase();
        MMObjectBuilder builder = mmbase.getBuilder("datatypes");
        MMObjectNode node = builder.getNewNode("system");
        Date date = new Date();
        node.setValue("datetime", date);
        builder.insert("system", node);

        NodeEvent event1 = NodeEventHelper.createNodeEventInstance(node, Event.TYPE_NEW, null);


        BasicSearchQuery query3 = new BasicSearchQuery(false);
        BasicStep s1 = query3.addStep(builder);
        CoreField bField = builder.getField("datetime");
        BasicStepField sf1 = query3.addField(s1, bField);
        BasicFieldValueConstraint  c1 = new BasicFieldValueConstraint(sf1, date);
        c1.setOperator(FieldCompareConstraint.EQUAL);
        query3.setConstraint(c1);

        BasicSearchQuery query4 = (BasicSearchQuery) query3.clone();
        BasicFieldValueConstraint c2 = (BasicFieldValueConstraint) query4.getConstraint();
        Calendar cal =Calendar.getInstance();
        cal.set(Calendar.YEAR, -1);
        c2.setValue(cal.getTime());


        assertTrue("date value does match:  flush",              matchingStrategy.evaluate(event1, query3, null).shouldRelease());
        assertFalse("date value dus not match: don't flush",     matchingStrategy.evaluate(event1, query4, null).shouldRelease());
    }
    public void testBasicFieldValueConstraintMatcherInt() {
        // type integer. allso tests event types delete and new
        Query query1 = Queries.createQuery(cloud, null, "datatypes", "datatypes.number", "datatypes.integer < 3", null, null, null, false);

        NodeEvent event2a = new NodeEvent(null, "datatypes", nodeNumber, createMap(new Object[][] { { new String("integer"), new Integer(2) } }), createMap(new Object[][] { { new String("integer"), new Integer(1) } }), Event.TYPE_CHANGE);
        NodeEvent event2b = new NodeEvent(null, "datatypes", nodeNumber, createMap(new Object[][] { { new String("integer"), new Integer(2) } }), createMap(new Object[][] { { new String("integer"), new Integer(10) } }), Event.TYPE_CHANGE);
        NodeEvent event2c = new NodeEvent(null, "datatypes", nodeNumber, createMap(new Object[][] { { new String("integer"), new Integer(10) } }), createMap(new Object[][] { { new String("integer"), new Integer(100) } }), Event.TYPE_CHANGE);
        NodeEvent event2d = new NodeEvent(null, "datatypes", nodeNumber, createMap(new Object[][] { { new String("integer"), new Integer(3) } }), createMap(new Object[][] { { new String("integer"), new Integer(2) } }), Event.TYPE_CHANGE);

        // when the event type is 'new' the new values should be checked against the constraint
        NodeEvent event2e = new NodeEvent(null, "datatypes", nodeNumber, null, createMap(new Object[][] { { new String("integer"), new Integer(2) } }), Event.TYPE_NEW);
        NodeEvent event2f = new NodeEvent(null, "datatypes", nodeNumber, null, createMap(new Object[][] { { new String("integer"), new Integer(4) } }), Event.TYPE_NEW);

        // event type Delete
        NodeEvent event2g = new NodeEvent(null, "datatypes", nodeNumber, createMap(new Object[][] { { new String("integer"), new Integer(2) } }), null, Event.TYPE_DELETE);
        NodeEvent event2h = new NodeEvent(null, "datatypes", nodeNumber, createMap(new Object[][] { { new String("integer"), new Integer(4) } }), null, Event.TYPE_DELETE);

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

        assertTrue("Changed node falls within constraints before, and within constraints after event. flush,", matchingStrategy.evaluate(event2a, query1, null).shouldRelease());
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

    public void testCompositeMatcherAnd(){
        MMBase mmbase = MMBase.getMMBase();
        MMObjectBuilder magsBuilder = mmbase.getBuilder("mags");
        MMObjectNode node = magsBuilder.getNewNode("system");
        node.setValue("title", "test");
        node.setValue("subtitle", "testsub");
        node.setValue("intro", "intro");
        magsBuilder.insert("system", node);

        	//test the new node
        NodeEvent event = NodeEventHelper.createNodeEventInstance(node, Event.TYPE_NEW, null);
        Query query1 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.number = "+node.getNumber()+" AND mags.title='test'", null, null, null, false);
        Query query2 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.number = 11 AND mags.title='test'", null, null, null, false);
        Query query3 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.number = "+node.getNumber()+" AND mags.title='hallo'", null, null, null, false);
        Query query4 = Queries.createQuery(cloud, null, "mags,news", "mags.number", "mags.number = 11 AND news.title='test'", null, null, null, false);

        assertTrue("both constraints match: flush", matchingStrategy.evaluate(event, query1, null).shouldRelease());
        assertFalse("node number does not match: don't flush", matchingStrategy.evaluate(event, query2, null).shouldRelease());
        assertFalse("magazine title does not match: don't flush", matchingStrategy.evaluate(event, query3, null).shouldRelease());
        assertTrue("event does not (fully) match query: flush", matchingStrategy.evaluate(event, query4, null).shouldRelease());

        //test the changed node
        node.setValue("subtitle", "newTitle");
        NodeEvent event1 = NodeEventHelper.createNodeEventInstance(node, Event.TYPE_CHANGE, null);

        Query query5 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.title = 'test' AND mags.subtitle='testsub'", null, null, null, false);
        Query query6 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.title = 'test' AND mags.subtitle='newTitle'", null, null, null, false);
        Query query7 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.title = 'test' AND mags.intro='intro'", null, null, null, false);

        assertTrue("changed node: value used to match, but no more: flush", matchingStrategy.evaluate(event1, query5, null).shouldRelease());
        assertTrue("changed node: value matches now, but did not before: flush", matchingStrategy.evaluate(event1, query6, null).shouldRelease());
        assertTrue("changed node: value matched before and matches now: flush", matchingStrategy.evaluate(event1, query7, null).shouldRelease());

        node.commit();

        //test deleted nodes
        NodeEvent event2 = NodeEventHelper.createNodeEventInstance(node, Event.TYPE_DELETE, null);
        Query query8 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.title = 'hallo' AND mags.intro='intro'", null, null, null, false);
        Query query9 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.title = 'test' AND mags.intro='intro'", null, null, null, false);
        Query query10 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.title = 'test' AND mags.intro='something'", null, null, null, false);
        Query query11 = Queries.createQuery(cloud, null, "mags,news", "mags.number", "news.title = 'test' AND mags.intro='something'", null, null, null, false);

        assertFalse("deleted node: one value matches: don't flush", matchingStrategy.evaluate(event2, query8, null).shouldRelease());
        assertTrue("deleted node: both  values matches: flush", matchingStrategy.evaluate(event2, query9, null).shouldRelease());
        assertFalse("deleted node: both  values don't match: don't flush", matchingStrategy.evaluate(event2, query10, null).shouldRelease());
        assertTrue("deleted node: event does not (fully) match query: flush", matchingStrategy.evaluate(event2, query11, null).shouldRelease());
    }

    public void testCompositeMatcherOr(){

        MMBase mmbase = MMBase.getMMBase();
        MMObjectBuilder magsBuilder = mmbase.getBuilder("mags");
        MMObjectNode node = magsBuilder.getNewNode("system");
        node.setValue("title", "test");
        node.setValue("subtitle", "testsub");
        node.setValue("intro", "intro");
        magsBuilder.insert("system", node);

        NodeEvent event1 = NodeEventHelper.createNodeEventInstance(node, Event.TYPE_NEW, null);
        Query query1 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.subtitle='notso' OR mags.title='test'", null, null, null, false);
        Query query2 = Queries.createQuery(cloud, null, "mags, news", "mags.number", "news.title='notso'  OR mags.title='test'", null, null, null, false);
        Query query3 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.subtitle='testsub'  OR mags.title='test'", null, null, null, false);
        Query query4 = Queries.createQuery(cloud, null, "mags", "mags.number", "mags.subtitle='notso'  OR mags.title='notso'", null, null, null, false);
        Query query5 = Queries.createQuery(cloud, null, "mags,news", "mags.number", "news.subtitle='notso'  OR news.title='test'", null, null, null, false);

        assertTrue("new node: one value matches,  and both can be cheked: flush", matchingStrategy.evaluate(event1, query1, null).shouldRelease());
        assertTrue("new node: one value matches,  and the other can not be cheked: flush", matchingStrategy.evaluate(event1, query2, null).shouldRelease());
        assertTrue("new node: both  values matches: flush", matchingStrategy.evaluate(event1, query3, null).shouldRelease());
        assertFalse("new node: no values matches: don't flush", matchingStrategy.evaluate(event1, query4, null).shouldRelease());
        assertTrue("new node: no values can be checked: flush", matchingStrategy.evaluate(event1, query5, null).shouldRelease());
    }


    public void testBasicFieldValueInConstraintMatcher() {
        Query query = Queries.createQuery(cloud, null, "datatypes", "datatypes.number,datatypes.integer", null, null, null, null, false);
        Set<Integer> values = new HashSet<Integer>();
        values.add(new Integer(0));
        values.add(new Integer(1));
        query.setConstraint(Queries.createConstraint(query, "datatypes.integer", Queries.getOperator("IN"), values, null, false));


        {
            // will match
            NodeEvent event = new NodeEvent(null, "datatypes", nodeNumber, 
                                            createMap(new Object[][] { { new String("integer"), new Integer(2) } }), 
                                            createMap(new Object[][] { { new String("integer"), new Integer(1) } }), Event.TYPE_CHANGE);
            assertTrue(matchingStrategy.evaluate(event, query, null).shouldRelease());
        }

        {
            // never matches
            NodeEvent event = new NodeEvent(null, "datatypes", nodeNumber, 
                                            createMap(new Object[][] { { new String("integer"), new Integer(2) } }), 
                                            createMap(new Object[][] { { new String("integer"), new Integer(3) } }), Event.TYPE_CHANGE);
            assertFalse(matchingStrategy.evaluate(event, query, null).shouldRelease());
        }
        {
            // did match
            NodeEvent event = new NodeEvent(null, "datatypes", nodeNumber, 
                                            createMap(new Object[][] { { new String("integer"), new Integer(1) } }), 
                                            createMap(new Object[][] { { new String("integer"), new Integer(2) } }), Event.TYPE_CHANGE);
            assertTrue(matchingStrategy.evaluate(event, query, null).shouldRelease());
        }
        {
            // still matches
            NodeEvent event = new NodeEvent(null, "datatypes", nodeNumber, 
                                            createMap(new Object[][] { { new String("integer"), new Integer(1) } }), 
                                            createMap(new Object[][] { { new String("integer"), new Integer(0) } }), Event.TYPE_CHANGE);
            assertTrue(matchingStrategy.evaluate(event, query, null).shouldRelease());
        }
    }


    public void testSpeed() {
        Query query = Queries.createQuery(cloud, null, "datatypes", "datatypes.number,datatypes.integer", null, null, null, null, false);
        Set<Integer> values = new HashSet<Integer>();
        values.add(new Integer(0));
        values.add(new Integer(1));
        query.setConstraint(Queries.createConstraint(query, "datatypes.integer", Queries.getOperator("IN"), values, null, false));

        NodeEvent event1 = new NodeEvent(null, "datatypes", nodeNumber, 
                                         createMap(new Object[][] { { new String("integer"), new Integer(2) } }), 
                                         createMap(new Object[][] { { new String("integer"), new Integer(1) } }), Event.TYPE_CHANGE);
        NodeEvent event2 = new NodeEvent(null, "datatypes", nodeNumber, 
                                         createMap(new Object[][] { { new String("integer"), new Integer(2) } }), 
                                         createMap(new Object[][] { { new String("integer"), new Integer(3) } }), Event.TYPE_CHANGE);
        NodeEvent event3 = new NodeEvent(null, "datatypes", nodeNumber, 
                                            createMap(new Object[][] { { new String("integer"), new Integer(1) } }), 
                                            createMap(new Object[][] { { new String("integer"), new Integer(2) } }), Event.TYPE_CHANGE);
        
        ChainedReleaseStrategy chain = new ChainedReleaseStrategy(); // in reality always a chain is used;
        chain.addReleaseStrategy(matchingStrategy);
        System.out.println("Simple performance (constraint matching).");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 50000; i++) {
            chain.evaluate(event1, query, null).shouldRelease();
            chain.evaluate(event2, query, null).shouldRelease();
            chain.evaluate(event3, query, null).shouldRelease();
            //chain.evaluate(event3, query, null).shouldRelease();
        }
        System.out.println("Simple performance test result (contraint matching): " + (System.currentTimeMillis() - startTime) + " ms");


    }


}
