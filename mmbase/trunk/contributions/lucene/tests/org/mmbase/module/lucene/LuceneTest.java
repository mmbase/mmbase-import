/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.lucene;

import org.mmbase.tests.BridgeTest;
import org.mmbase.bridge.*;
import java.util.*;
import junit.framework.TestCase;

/**
 *
 * @author Michiel Meeuwissen
 * @verion $Id: LuceneTest.java,v 1.1 2007-12-27 09:44:52 michiel Exp $
 */
public class LuceneTest extends BridgeTest {

    private static Lucene lucene;
    private static Node article;
    private static Cloud cloud;

    public void setUp() throws Exception {
        System.out.println("Starting mmbase");
        startMMBase(true);
        lucene = org.mmbase.module.Module.getModule(Lucene.class);
        Thread.sleep(15000);
        System.out.println("" + lucene.listFunction.getFunctionValue());
        System.out.println("NOW DOING FULL INDICES");
        lucene.fullIndexFunction.getFunctionValue("mynews_magazine");
        lucene.fullIndexFunction.getFunctionValue("mynews_news");
        Thread.sleep(5000);
        //assertIndicesUpToDate();

    }

    private void testSize(String index, String value, int expected) {
        int searchSize = lucene.getSearcher(index).searchSize(cloud, value);
        assertTrue ("Search size is not " + expected + " but " + searchSize, searchSize == expected);
    }
    private void assertIndicesUpToDate() throws Exception {
        // perhaps something faster then this could be come up with.
        lucene.waitFunction.getFunctionValue();
        //Thread.sleep(14000);
    }
    /**
     */
    public void testIndex() throws Exception {
        cloud = getCloud();
        System.out.println("Found cloud " + cloud + " and lucene " + lucene);

        testSize("mynews_magazine", null, 5);
        testSize("mynews_magazine", "xml", 3);
        testSize("mynews_news", null, 5);
        testSize("mynews_news", "xml", 3);
    }

    public void testChange() throws Exception {
        article = cloud.getNode("anewsarticle");
        article.setStringValue("title", "something else");
        article.setStringValue("subtitle", "something else");
        article.setStringValue("intro", "something else");
        article.setStringValue("body", "something else");
        article.commit();
        assertIndicesUpToDate();
        Thread.sleep(10000);
        testSize("mynews_magazine", "xml", 2);
        testSize("mynews_news", "xml", 2);
    }

    public void testChangeStillInCondition() throws Exception {
        article = cloud.getNode("anewsarticle");
        article.setStringValue("title", "Again with XML");
        article.commit();
        assertIndicesUpToDate();
        testSize("mynews_magazine", "xml", 3);
        testSize("mynews_news", "xml", 3);
    }

    public void testCreateInConditionNotInIndex() throws Exception {
        article = cloud.getNodeManager("news").createNode();
        article.setStringValue("title", "Also with XML");
        article.commit();
        assertIndicesUpToDate();
        testSize("mynews_magazine", "xml", 3);
        testSize("mynews_news", "xml", 4);
    }
    public void testRelateIntoIndex() throws Exception {
        Node mag = cloud.getNode("default.mags");
        RelationManager rm = cloud.getRelationManager("mags", "news", "posrel");
        Relation r = rm.createRelation(mag, article);
        r.commit();
        assertIndicesUpToDate();
        testSize("mynews_magazine", "xml", 4);
    }
    public void testChangeRemainInIndex() throws Exception {
        article.setStringValue("title", "Still contains XML");
        article.commit();
        assertIndicesUpToDate();
        testSize("mynews_magazine", "xml", 4);
    }
    public void testRemoveFromCondition() throws Exception {
        article.setStringValue("title", "XLM is not ok");
        article.commit();
        assertIndicesUpToDate();
        testSize("mynews_magazine", "xml", 3);
    }
    public void testPutBackIntoCondition() throws Exception {
        article.setStringValue("title", "XML is ok");
        article.commit();
        assertIndicesUpToDate();
        testSize("mynews_magazine", "xml", 4);
    }

    public void testChangeStartNode() throws Exception {
        Node mag = cloud.getNode("default.mags");
        mag.setStringValue("subtitle", "test test");
        mag.commit();
        // changing startnodes should have no influence of size of indices.
        assertIndicesUpToDate();
        testSize("mynews_magazine", "xml", 4);
        testSize("mynews_news", "xml", 4);
    }



}
