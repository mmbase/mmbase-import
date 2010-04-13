/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import org.mmbase.bridge.*;
import org.mmbase.bridge.mock.*;
import java.util.*;
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;


/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id$
 */
public class LuceneTest {

    private static Lucene lucene;
    private static int articleNumber;

    public MockCloudContext getCloudContext() {
        return MockCloudContext.getInstance();
    }

    @BeforeClass
    public static void setup() throws Exception {
        MockCloudContext.getInstance().addCore();
        MockCloudContext.getInstance().addNodeManagers(MockBuilderReader.getBuilderLoader().getChildResourceLoader("mynews"));
        org.mmbase.module.Module.checkModules(true, "lucene");
        lucene = org.mmbase.module.Module.getModule(Lucene.class, true);
        assertTrue(lucene != null);
        lucene.init(false);
        lucene.fullIndexFunction.getFunctionValueWithArgs("mynews_magazine");
        lucene.fullIndexFunction.getFunctionValueWithArgs("mynews_news");
        System.out.println("Found cloud " + getCloud() + " and lucene " + lucene);
    }

    private static Cloud getCloud () {
        return MockCloudContext.getInstance().getCloud("mmbase", "class", null);
    }

    private Node getArticle() {
        return getCloud().getNode(articleNumber);
    }

    private void testSize(String index, String value, int expected) throws Exception {
        Cloud cloud = getCloud();
        int searchSize = lucene.getSearcher(index).searchSize(cloud, value);
        assertTrue ("Search size is not " + expected + " but " + searchSize, searchSize == expected);
    }
    private void waitForIndicesUpToDate() throws Exception {
        lucene.waitFunction.getFunctionValueWithArgs();
    }

    @Test
    public void testIndex() throws Exception {
        waitForIndicesUpToDate();

        testSize("mynews_news", null, 5);
        testSize("mynews_news", "xml", 3);

        testSize("mynews_magazine", null, 5);
        testSize("mynews_magazine", "xml", 3);
    }

    @Test
    public void testChange() throws Exception {
        Cloud cloud = getCloud();
        Node article = cloud.getNode("anewsarticle");
        article.setStringValue("title", "something else");
        article.setStringValue("subtitle", "something else");
        article.setStringValue("intro", "something else");
        article.setStringValue("body", "something else");
        article.commit();
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 2);
        testSize("mynews_news", "xml", 2);
    }

    public void testChangeStillInCondition() throws Exception {
        Cloud cloud = getCloud();
        Node article = cloud.getNode("anewsarticle");
        article.setStringValue("title", "Again with XML");
        article.commit();
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 3);
        testSize("mynews_news", "xml", 3);
    }

    public void testCreateInConditionNotInIndex() throws Exception {
        Cloud cloud = getCloud();
        Node article = cloud.getNodeManager("news").createNode();
        article.setStringValue("title", "Also with XML");
        article.commit();
        articleNumber = article.getNumber();
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 3);
        testSize("mynews_news", "xml", 4);
    }
    public static int rel1Number;
    public void testRelateIntoIndex() throws Exception {
        Cloud cloud = getCloud();
        Node mag = cloud.getNode("default.mags");
        RelationManager rm = cloud.getRelationManager("mags", "news", "posrel");
        Relation r = rm.createRelation(mag, getArticle());
        r.commit();
        rel1Number = r.getNumber();
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 4);
    }
    public void testChangeRemainInIndex() throws Exception {
        Node article = getArticle();
        article.setStringValue("title", "Still contains XML");
        article.commit();
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 4);
    }
    public void testRemoveFromCondition() throws Exception {
        Node article = getArticle();
        article.setStringValue("title", "XLM is not ok");
        article.commit();
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 3);
    }
    public void testPutBackIntoCondition() throws Exception {
        Node article = getArticle();
        article.setStringValue("title", "XML is ok");
        article.commit();
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 4);
    }

    public void testChangeStartNode() throws Exception {
        Cloud cloud = getCloud();
        Node mag = cloud.getNode("default.mags");
        mag.setStringValue("subtitle", "test test");
        mag.commit();
        // changing startnodes should have no influence of size of indices.
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 4);
        testSize("mynews_news", "xml", 4);
    }

    public static int rel2Number;
    public void testRelateIntoIndexTwice() throws Exception {
        Cloud cloud = getCloud();
        Node mag = cloud.getNode("nondefault.mags");
        RelationManager rm = cloud.getRelationManager("mags", "news", "posrel");
        Relation r = rm.createRelation(mag, getArticle());
        r.commit();
        rel2Number = r.getNumber();
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 4);
    }
    public void testRemoveFromConditionRelatedTwice() throws Exception {
        Node article = getArticle();
        article.setStringValue("title", "XLM is not ok");
        article.commit();
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 3);
    }
    public void testPutBackIntoConditionRelatedTwice() throws Exception {
        Node article = getArticle();
        article.setStringValue("title", "XML is ok");
        article.commit();
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 4);
    }

    public void testUnRelateFromStartNodeButStilInIndex() throws Exception {
        Cloud cloud = getCloud();
        Node r = cloud.getNode(rel1Number);
        r.delete();
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 4);
    }
    public void testUnRelateFromLastStartNode() throws Exception {
        Cloud cloud = getCloud();
        Node r = cloud.getNode(rel2Number);
        r.delete();
        waitForIndicesUpToDate();
        testSize("mynews_magazine", "xml", 3);
    }



}
