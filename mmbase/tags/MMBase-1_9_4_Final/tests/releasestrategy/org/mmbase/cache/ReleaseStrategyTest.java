/*
 * Created on 25-okt-2005
 *
 * This software is OSI Certified Open Source Software.
 * OSI Certified is a certification mark of the Open Source Initiative.
 *
 * The license (Mozilla version 1.0) can be read at the MMBase site.
 * See http://www.MMBase.org/license
 */
package org.mmbase.cache;

import java.util.HashMap;
import java.util.Map;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.core.event.NodeEvent;
import org.mmbase.core.event.Event;
import org.mmbase.core.event.NodeEventHelper;
import org.mmbase.core.event.RelationEvent;
import org.mmbase.module.core.*;
import org.mmbase.storage.search.*;

import org.mmbase.storage.search.implementation.BasicFieldValueConstraint;
import org.mmbase.storage.search.implementation.BasicLegacyConstraint;
import org.mmbase.tests.BridgeTest;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Test class for {@link org.mmbase.cache.BetterStrategy}.
 * TODO: the way the NodeEvent now works makes it core dependant. i can not do the tests
 *  without using the core. this does not mean it won't work without the core (rmmci) but i think it
 *  must be changed.
 * @author Ernst Bunders
 *
 */
public class ReleaseStrategyTest extends BridgeTest {

    static protected Cloud cloud = null;

    static protected RelationManager posrelManager;
    static protected NodeManager relDefManager;
    static protected NodeManager typeRelManager;
    static protected NodeManager insRelManager;
    static protected NodeManager newsManager;
    static protected NodeManager urlsManager;
    static protected NodeManager peopelManager;
    static protected NodeList    createdNodes;

    static protected ReleaseStrategy strategy;

    protected Query twooStepQuery;
    protected Query oneStepQuery;


    private static final Logger log = Logging.getLoggerInstance(ReleaseStrategyTest.class);

    protected final static String TEST_RELATION_ROLE = "test";
    protected final static String NEWS_TITLE = "title";
    protected final static String URLS_NAME = "name";

    protected static final int POSREL_POS = 0;

    static protected Node newsNode;

    static protected Node urlsNode;

    static protected Relation posrelNode;

    public ReleaseStrategyTest(String name){
        super(name);
    }


    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception {
        if (cloud == null) {
            startMMBase();
            cloud = getCloud();

            try {
                newsManager = cloud.getNodeManager("news");
                urlsManager = cloud.getNodeManager("urls");
                posrelManager = cloud.getRelationManager("posrel");
                relDefManager = cloud.getNodeManager("reldef");
                typeRelManager = cloud.getNodeManager("typerel");
                insRelManager  = cloud.getNodeManager("insrel");
                peopelManager = cloud.getNodeManager("people");
            } catch(NotFoundException e){
                throw new Exception("Test cases cannot be performed because " + e.getMessage() + " Please arrange this in your cloud before running this TestCase.");
            }
            strategy = new BetterStrategy();
            createdNodes = cloud.createNodeList();


            //create an alternative typerel: people > posrel > urls
            Node typerel = typeRelManager.createNode();
            typerel.setNodeValue("snumber", peopelManager);
            typerel.setNodeValue("dnumber", urlsManager);
            typerel.setNodeValue("rnumber", posrelManager);
            typerel.commit();
            createdNodes.add(typerel);

            //create some basic nodes

            newsNode = newsManager.createNode();
            newsNode.setStringValue("title", NEWS_TITLE);
            newsNode.commit();
            createdNodes.add(newsNode);

            urlsNode = urlsManager.createNode();
            urlsNode.setStringValue("name", URLS_NAME);
            urlsNode.commit();
            createdNodes.add(urlsNode);

            posrelNode = newsNode.createRelation(urlsNode, posrelManager);
            posrelNode.setIntValue("pos", POSREL_POS);
            posrelNode.commit();
            createdNodes.add(posrelNode);

        }
    }

    /**
     * here go all the tests for checks that are done for every type of node event
     */
    public void testNodeEvent(){
        //a node event that has a type that is not part of the query path should not flush the query

        NodeEvent event = NodeEventHelper.createNodeEventInstance(newsNode, Event.TYPE_NEW, null);
        Query q1 = Queries.createQuery(cloud, null, "people,posrel,urls", "urls.name", null, null, null, null, false);

        assertFalse("a node event should not flush a query if it's type is not in the path",
                    strategy.evaluate(event, q1, null).shouldRelease());
    }





    /**
     * here go all the tests for checks that are done for every type of relation event
     */
    public void testRelationEvent(){

        //type: posrel, sourceType: news, destinationType: urls
        RelationEvent relEvent = NodeEventHelper.createRelationEventInstance(posrelNode, Event.TYPE_NEW, null);

        //if the query has one step, a relaion event should not flush the query
        Query q1 = Queries.createQuery(cloud, null, "urls", "urls.name", null, null, null, null, false);

        assertFalse("relation event should not flush query with one step",
                    strategy.evaluate(relEvent, q1, null).shouldRelease());

        //if either source, destination or role does not match, a relation event should not flush the query
        Query q2 = Queries.createQuery(cloud, null, "people,posrel,urls", "urls.name", null, null, null, null, false);
        Query q3 = Queries.createQuery(cloud, null, "news,sorted,urls", "news.title", null, null, null, null, false);
        Query q4 = Queries.createQuery(cloud, null, "news,posrel,attachments", "news.title", null, null, null, null, false);

        assertFalse("relation event of new relation between nodes in multi step query (where source does not match) should not be flushed",
                    strategy.evaluate(relEvent, q2, null).shouldRelease());
        assertFalse("relation event of new relation between nodes in multi step query (where role does not match) should not be flushed",
                    strategy.evaluate(relEvent, q3, null).shouldRelease());
        assertFalse("relation event of new relation between nodes in multi step query (where destinantion does not match) should not be flushed",
                    strategy.evaluate(relEvent, q4, null).shouldRelease());

        //but when they do it should be flushed, allso if source and destination are the other way around

        Query q4a = Queries.createQuery(cloud, null, "news,posrel,urls", "news.title", null, null, null, null, false);
        Query q4b = Queries.createQuery(cloud, null, "urls,posrel,news", "news.title", null, null, null, null, false);

        Query q4c = Queries.createQuery(cloud, null, "news,posrel,object", "news.title", null, null, null, null, false);
        Query q4d = Queries.createQuery(cloud, null, "object,posrel,news", "news.title", null, null, null, null, false);

        assertTrue("when path of query matches relation event, query should be flushed",
                   strategy.evaluate(relEvent, q4a, null).shouldRelease());
        assertTrue("when path of query matches relation event, query should be flushed, allso when source and relation are swapped",
                   strategy.evaluate(relEvent, q4b, null).shouldRelease());
        assertTrue("when path of query matches relation event, query should be flushed",
                   strategy.evaluate(relEvent, q4c, null).shouldRelease());
        assertTrue("when path of query matches relation event, query should be flushed, allso when source and relation are swapped",
                   strategy.evaluate(relEvent, q4d, null).shouldRelease());


        //but if the role is not specified the query should be flushed
        Query q5 = Queries.createQuery(cloud, null, "news,urls", "urls.name", null, null, null, null, false);
        assertTrue("relation event of new relation between nodes in multi step query (where role is not specified and source and destination match) should be flushed",
                   strategy.evaluate(relEvent, q5, null).shouldRelease());
        Query q51 = Queries.createQuery(cloud, null, "news,object", "object.number", null, null, null, null, false);
        assertTrue("relation event of new relation between nodes in multi step query (where role is not specified and source and destination match) should be flushed",
                   strategy.evaluate(relEvent, q51, null).shouldRelease());

        //unless source or destination do not match
        Query q6 = Queries.createQuery(cloud, null, "people,urls", "urls.name", null, null, null, null, false);
        Query q7 = Queries.createQuery(cloud, null, "news,attachments", "news.title", null, null, null, null, false);

        assertFalse("relation event of new relation between nodes in multi step query where is not specified (and source does not match) should not be flushed",
                    strategy.evaluate(relEvent, q6, null).shouldRelease());
        assertFalse("relation event of new relation between nodes in multi step query where is not specified (and destination does not match) should not be flushed",
                    strategy.evaluate(relEvent, q7, null).shouldRelease());

    }






    public void testNewNode(){
        log.debug("method: testMultiStepQueryNewNode()");
        Query q1 = Queries.createQuery(cloud, null, "news,posrel,urls", "news.title,urls.name",null, null, null, null, false);
        Query q2 = Queries.createQuery(cloud, null, "object,posrel,urls", "object.otype,urls.name",null, null, null, null, false);
        Query q3 = Queries.createQuery(cloud, null, "news,posrel,object", "news.title,object.owner",null, null, null, null, false);
        Query q4 = Queries.createQuery(cloud, null, "news,urls", "news.title,urls.name",null, null, null, null, false);
        Query q5 = Queries.createQuery(cloud, null, "object,object2", "object.otype,object2.otype",null, null, null, null, false);

        //a new node should not flush a multistep query frome the cache
        NodeEvent event = NodeEventHelper.createNodeEventInstance(newsNode, Event.TYPE_NEW, null);
        assertFalse("node event of new node on multi step query should not release the query", strategy.evaluate(event, q1, null).shouldRelease());
        assertFalse("node event of new node on multi step query should not release the query", strategy.evaluate(event, q2, null).shouldRelease());
        assertFalse("node event of new node on multi step query should not release the query", strategy.evaluate(event, q3, null).shouldRelease());
        assertFalse("node event of new node on multi step query should not release the query", strategy.evaluate(event, q4, null).shouldRelease());
        assertFalse("node event of new node on multi step query should not release the query", strategy.evaluate(event, q5, null).shouldRelease());

    }


    public void testNewRelation(){
        log.debug("method: testMultiStepQueryNewRelation()");
        Query q1 = Queries.createQuery(cloud, null, "news,posrel,urls",   "news.title,urls.name",       null, null, null, null, false);
        Query q2 = Queries.createQuery(cloud, null, "object,posrel,urls", "object.otype,urls.name",     null, null, null, null, false);
        Query q3 = Queries.createQuery(cloud, null, "news,posrel,object", "news.title,object.owner",    null, null, null, null, false);
        Query q4 = Queries.createQuery(cloud, null, "news,urls",          "news.title,urls.name",       null, null, null, null, false);
        Query q5 = Queries.createQuery(cloud, null, "object,object2",     "object.otype,object2.otype", null, null, null, null, false);

        NodeQuery nq1 = cloud.createNodeQuery();
        {
            Step step = nq1.addStep(newsManager);
            nq1.setNodeStep(step);
            RelationStep relationStep = nq1.addRelationStep(urlsManager, "posrel", "destination");
            nq1.setNodeStep(relationStep.getNext());
        }
        NodeQuery nq2 = cloud.createNodeQuery();
        {
            Step step = nq2.addStep(newsManager);
            nq2.setNodeStep(step);
            nq2.addRelationStep(urlsManager, "posrel", "destination");
        }

        //a new relation node should not flush cache the cache
        NodeEvent event = NodeEventHelper.createNodeEventInstance(posrelNode, Event.TYPE_NEW, null);
        assertFalse("node event of new relation node on multi step query should not release the query", strategy.evaluate(event, q1, null).shouldRelease());
        assertFalse("node event of new relation node on multi step query should not release the query", strategy.evaluate(event, q2, null).shouldRelease());
        assertFalse("node event of new relation node on multi step query should not release the query", strategy.evaluate(event, q3, null).shouldRelease());
        assertFalse("node event of new relation node on multi step query should not release the query", strategy.evaluate(event, q4, null).shouldRelease());
        assertFalse("node event of new relation node on multi step query should not release the query", strategy.evaluate(event, q5, null).shouldRelease());

        assertFalse("node event of new relation node on multi step nodequery should not release the query", strategy.evaluate(event, nq1, null).shouldRelease());
        assertFalse("node event of new relation node on multi step nodequery should not release the query", strategy.evaluate(event, nq2, null).shouldRelease());

        //but the subsequent relation event should
        RelationEvent relEvent = NodeEventHelper.createRelationEventInstance(posrelNode, Event.TYPE_NEW, null);
        assertTrue("relation event of new relation between nodes in multi step query should flush the cache", strategy.evaluate(relEvent, q1, null).shouldRelease());
        assertTrue("relation event of new relation between nodes in multi step query should flush the cache", strategy.evaluate(relEvent, q2, null).shouldRelease());
        assertTrue("relation event of new relation between nodes in multi step query should flush the cache", strategy.evaluate(relEvent, q3, null).shouldRelease());
        assertTrue("relation event of new relation between nodes in multi step query should flush the cache", strategy.evaluate(relEvent, q4, null).shouldRelease());
        assertTrue("relation event of new relation between nodes in multi step query should flush the cache", strategy.evaluate(relEvent, q5, null).shouldRelease());

        assertTrue("relation event of new relation between nodes in multi step nodequery should flush the cache", strategy.evaluate(relEvent, nq1, null).shouldRelease());
        assertTrue("relation event of new relation between nodes in multi step nodequery should flush the cache " + nq2.toSql() + "  " + relEvent,
                   strategy.evaluate(relEvent, nq2, null).shouldRelease());

    }




    public void testChangedNode(){
        log.debug("method: testMultiStepQueryChangedNode()");

        //if none of the fields that have changed are part of the select or the constraint part of the query it should not be flushed

        //MMObjectNode testNode = MMBase.getMMBase().getBuilder("object").getNode(newsNode.getNumber());
        //testNode.setValue("title", "another title");

        NodeEvent event = new NodeEvent(null, "news", 0, getMap("title", "oldTitle"), getMap("title", "newtitle"), Event.TYPE_CHANGE);
        NodeEvent event2 = new NodeEvent(null, "news", 0, getMap("owner", "oldOwner"), getMap("owner", "newowner"), Event.TYPE_CHANGE);
        {
            Query q1 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle", "news.number < 1000", null, null, null, false);
            Query q2 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle", "news.title = 'hallo'", null, null, null, false);
            Query q3 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.title", "news.number < 1000", null, null, null, false);

            assertFalse("changed field is not used by query: it should not be flushed",
                        strategy.evaluate(event, q1, null).shouldRelease());
            assertTrue("changed field is in constraints section of query: it should be flushed",
                       strategy.evaluate(event, q2, null).shouldRelease());
            assertTrue("changed field is in select section of query: it should be flushed",
                       strategy.evaluate(event, q3, null).shouldRelease());
        }
        {// same thing, but now with inheritance
            Query q1 = Queries.createQuery(cloud, null, "object,posrel,urls" ,"object.otype", "object.number < 1000", null, null, null, false);
            Query q2 = Queries.createQuery(cloud, null, "object,posrel,urls" ,"object.otype", "object.owner = 'hallo'", null, null, null, false);
            Query q3 = Queries.createQuery(cloud, null, "object,posrel,urls" ,"object.owner", "news.number < 1000", null, null, null, false);

            assertFalse("changed field is not used by query: it should not be flushed",
                        strategy.evaluate(event2, q1, null).shouldRelease());
            assertTrue("changed field is in constraints section of query: it should be flushed",
                       strategy.evaluate(event2, q2, null).shouldRelease());
            assertTrue("changed field is in select section of query: it should be flushed",
                       strategy.evaluate(event2, q3, null).shouldRelease());
        }
        //also test  composite constraints
        Query q4 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle", "news.number < 1000 AND urls.name = 'hi'", null, null, null, false);
        Query q5 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle", "news.title='something' AND urls.name = 'hi'", null, null, null, false);

        assertFalse("changed field is not used by (composite) constraint: it should not be flushed",
                    strategy.evaluate(event, q4, null).shouldRelease());
        assertTrue("changed field is used by (composite) constraint: it should be flushed",
                   strategy.evaluate(event, q5, null).shouldRelease());

        //also test legacy constraints
        Query q6 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle", null, null, null, null, false);
        q6.setConstraint(new BasicLegacyConstraint("news.number < 1000 AND urls.name = 'hi'"));

        assertFalse("changed field is not used by (legacy) constraint: it should not be flushed",
                    strategy.evaluate(event, q6, null).shouldRelease());

        q6.setConstraint(new BasicLegacyConstraint("news.title='something' AND urls.name = 'hi'"));

        assertTrue("changed field is used by (legacy) constraint: it should be flushed",
                   strategy.evaluate(event, q6, null).shouldRelease());

        //*************************
        //if the step(s) of the changed field(s) are (all) aggregate fields of type count, and this field is not used
        //in the constraint as well, the query shouldn't be flushed.

        Query q7;
        Step newsStep;
        q7 = cloud.createAggregatedQuery();
        newsStep = q7.addStep(newsManager);
        AggregatedField titleField = q7.addAggregatedField(newsStep, newsManager.getField("title"), 2);

        assertFalse("aggregate queries (type count) where the changed field(s) match the step(s) should not be flushed",
                    strategy.evaluate(event, q7, null).shouldRelease());

        //but whit this field in the constraint it should be flushed
        q7.setConstraint(new BasicFieldValueConstraint(titleField, "disco"));
        assertTrue("aggregate queries (type count) where the changed field(s) match the step(s) but there are constraints on the step(s) should be flushed",
                   strategy.evaluate(event, q7, null).shouldRelease());

        //but other types of aggregation should flush (they deal with the contents of the field
        int[] aggregations = new int[] {
            AggregatedField.AGGREGATION_TYPE_COUNT_DISTINCT,
            AggregatedField.AGGREGATION_TYPE_GROUP_BY,
            AggregatedField.AGGREGATION_TYPE_MAX,
            AggregatedField.AGGREGATION_TYPE_MIN};
        for(int i = 0; i < aggregations.length; i++){
            q7 = cloud.createAggregatedQuery();
            newsStep = q7.addStep(newsManager);
            q7.addAggregatedField(newsStep, newsManager.getField("title"),aggregations[i]);

            assertTrue ("aggregate queries (type " + AggregatedField.AGGREGATION_TYPE_DESCRIPTIONS[i] + ") where the changed field(s) match the step(s) should be flushed",
                        strategy.evaluate(event, q7, null).shouldRelease());
        }
    }


    public void testChangedRelation(){
        //if a none of the fields that have changed are part of the select or the constraint part of the query it should not be flushed

        //stupid hack to create a changed field for the event. for some reason settin the field on an Node does not alter the
        //changedField collection on MMObjectNode
        MMObjectNode pos = MMBase.getMMBase().getBuilder( posrelNode.getNodeManager().getName() ).getNode(posrelNode.getNumber());
        pos.setValue("pos", new Integer(100));
        //now create a relation event
        RelationEvent relEvent = NodeEventHelper.createRelationEventInstance(posrelNode, Event.TYPE_CHANGE, null);

        Query q1 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle", "news.number < 10 ", null, null, null, false);
        Query q2 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle", "posrel.pos < 10 ", null, null, null, false);
        Query q3 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle,posrel.pos", "news.number < 10 ", null, null, null, false);

        assertFalse("changed relation field is not used by query: it should not be flushed",
                    strategy.evaluate(relEvent, q1, null).shouldRelease());
        assertTrue("changed relation field is in constraints section of query: it should be flushed",
                   strategy.evaluate(relEvent, q2, null).shouldRelease());
        assertTrue("changed relation field is in select section of query: it should be flushed",
                   strategy.evaluate(relEvent, q3, null).shouldRelease());

        //also test  composite constraints
        Query q4 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle", "news.number < 1000 AND urls.name = 'hi'", null, null, null, false);
        Query q5 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle", "posrel.pos < 1 AND urls.name = 'hi'", null, null, null, false);

        assertFalse("changed relation field is not used by (composite) constraint: it should not be flushed",
                    strategy.evaluate(relEvent, q4, null).shouldRelease());
        assertTrue("changed relation field is used by (composite) constraint: it should be flushed",
                   strategy.evaluate(relEvent, q5, null).shouldRelease());

        //also test legacy constraints
        Query q6 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle", null, null, null, null, false);
        q6.setConstraint(new BasicLegacyConstraint("news.number < 1000 AND urls.name = 'hi'"));

        assertFalse("changed relation field is not used by (legacy) constraint: it should not be flushed",
                    strategy.evaluate(relEvent, q6, null).shouldRelease());

        q6.setConstraint(new BasicLegacyConstraint("news.title='something' AND posrel.pos < 1"));

        assertTrue("changed relation field is used by (legacy) constraint: it should be flushed",
                   strategy.evaluate(relEvent, q6, null).shouldRelease());

    }

    public void testGetConstraintsforField(){
        Query q1 = Queries.createQuery(cloud, null, "news,posrel,urls", "news.number", "news.title = 'hallo'", null, null, null, false);
        Query q2 = Queries.createQuery(cloud, null, "news,posrel,urls", "news.number", "news.subtitle = 'hallo'", null, null, null, false);
        Query q3 = Queries.createQuery(cloud, null, "news,posrel,urls", "news.number", "news.title = 'hallo' AND news.subtitle='hi'", null, null, null, false);
        Query q4 = Queries.createQuery(cloud, null, "news,posrel,urls", "news.number", null, null, null, null, false);

        MMBase mmb = MMBase.getMMBase();
        MMObjectBuilder news = mmb.getBuilder("news");
        assertTrue("title field of news builder is in constraints",
                   ReleaseStrategy.getConstraintsForField("title", news, null, q1).size() == 1);
        assertTrue("title field of news builder is not in constraints",
                   ReleaseStrategy.getConstraintsForField("title", news, null, q2).size() == 0);
        assertTrue("title field of news builder is one of the constraints.",
                   ReleaseStrategy.getConstraintsForField("title", news, null, q3).size() == 1);
        assertTrue("there are no constraints.",
                   ReleaseStrategy.getConstraintsForField("title", news, null, q4).size() == 0);
    }


    protected Node createRelDefNode(String role, int dir) {
        // create a new relation-definition
        Node reldef = relDefManager.createNode();
        reldef.setValue("sname", role);
        reldef.setValue("dname", "d" + role );
        reldef.setValue("sguiname", role);
        reldef.setValue("dguiname", "d" + role);
        reldef.setIntValue("dir", dir);
        reldef.setNodeValue("builder", insRelManager);
        reldef.commit();
        createdNodes.add(reldef);
        return reldef;
    }

    public void testSpeed() {
        NodeEvent event = new NodeEvent(null, "news", 0, getMap("title", "oldTitle"), getMap("title", "newtitle"), Event.TYPE_CHANGE);

        Query q1 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle", "news.number < 1000", null, null, null, false);
        Query q2 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.subtitle", "news.title = 'hallo'", null, null, null, false);
        Query q3 = Queries.createQuery(cloud, null, "news,posrel,urls" ,"news.title", "news.number < 1000", null, null, null, false);


        ChainedReleaseStrategy chain = new ChainedReleaseStrategy(); // in reality always a chain is used;
        chain.addReleaseStrategy(strategy);
        System.out.println("Simple performance.");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 50000; i++) {
            chain.evaluate(event, q1, null).shouldRelease();
            chain.evaluate(event, q2, null).shouldRelease();
            chain.evaluate(event, q3, null).shouldRelease();
        }
        System.out.println("Simple performance test result: " + (System.currentTimeMillis() - startTime) + " ms");


    }

    private Map<String,Object> getMap(String key, Object value){
        Map<String,Object> m = new HashMap<String,Object>();
        m.put(key, value);
        return m;
    }

}
