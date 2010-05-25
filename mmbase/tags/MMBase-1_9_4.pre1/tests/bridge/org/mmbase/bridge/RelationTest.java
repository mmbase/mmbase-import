/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge;

import org.mmbase.tests.*;
/**
 * Test cases to test the creation of relations and the retrieval of them
 * @author Kees Jongenburger
 * @version $Id$
 */
public class RelationTest extends BridgeTest {

    public RelationTest(String name) {
        super(name);
    }

    public void testCreateRelations() {
        //first create 3 nodes
        Cloud cloud = getCloud();

        Node aaFirstNode = cloud.getNodeManager("aa").createNode();
        aaFirstNode.setStringValue("stringfield", "aaFirstNode");
        aaFirstNode.commit();

        Node bbNode = cloud.getNodeManager("bb").createNode();
        bbNode.setStringValue("stringfield", "bbNode");
        bbNode.commit();

        Node aaSecondNode = cloud.getNodeManager("aa").createNode();
        aaSecondNode.setStringValue("stringfield", "aaSecondNode");
        aaSecondNode.commit();

        RelationManager relatedRelationManager = cloud.getRelationManager("related");

        //create a relation from aaFirstNode to bbNode
        Relation related1 = aaFirstNode.createRelation(bbNode, relatedRelationManager);
        related1.commit();

        //create a relation from bbNode to aaSecondNode
        Relation related2 = bbNode.createRelation(aaSecondNode, relatedRelationManager);
        related2.commit();

        //we now have 3 node
        //in the BridgeTest application the folowing is defined
        //<relation from="aa" to="bb" type="related" />
        //<relation from="bb" to="aa" type="related" />

        //and we have created the folowing structure
        //aaFirstNode -> related1 -> bbNode -> related2 -> aaSecondNode

        //check back if we can get the nodes
        {
            //ask for the relation in the "right" direction
            NodeList aaRelatedList = aaFirstNode.getRelatedNodes("bb", "related", "destination");
            assertTrue("related list has size " + aaRelatedList.size() + " but should be 1", aaRelatedList.size() == 1);
            if (aaRelatedList.size() > 0) {
                Node theOtherNode = aaRelatedList.getNode(0);
                assertTrue("expected the bb node but got " + theOtherNode, theOtherNode.getNumber() == bbNode.getNumber());
            }
        }

        {
            //folow the relation in  wrong direction
            NodeList aaRelatedList = aaFirstNode.getRelatedNodes("bb", "related", "source");
            assertTrue("related list has size " + aaRelatedList.size() + " but should be 0", aaRelatedList.size() == 0);
            RelationList aaRelationList = aaFirstNode.getRelations("related", cloud.getNodeManager("bb"), "source");
            assertTrue("relation list has size " + aaRelationList.size() + " but should be 0", aaRelationList.size() == 0);
        }

        {
            //try to get the first aa node from the bb node
            NodeList bbRelatedListSource = bbNode.getRelatedNodes("aa", "related", "source");
            assertTrue("relation count should be 1 but is " + bbRelatedListSource.size() + " mmbase relations dont' work", bbRelatedListSource.size() == 1);
            if (bbRelatedListSource.size() > 0) {
                Node sourceNode = bbRelatedListSource.getNode(0);
                assertTrue("expected the first aa node but got " + sourceNode, sourceNode.getNumber() == aaFirstNode.getNumber());
            }
            RelationList bbRelationListSource = bbNode.getRelations("related", cloud.getNodeManager("aa"), "source");
            assertTrue("relation list has size " + bbRelationListSource.size() + " but should be 1", bbRelationListSource.size() == 1);

        }

        {
            //try to get the second aa node from the bb node
            NodeList bbRelatedListDestination = bbNode.getRelatedNodes("aa", "related", "destination");
            assertTrue("relation count should be 1 but is " + bbRelatedListDestination.size() + " mmbase relations dont' work", bbRelatedListDestination.size() == 1);
            if (bbRelatedListDestination.size() > 0) {
                Node sourceNode = bbRelatedListDestination.getNode(0);
                assertTrue("expected the first aa node but got " + sourceNode, sourceNode.getNumber() == aaSecondNode.getNumber());
            }
            RelationList bbRelationListDestination = bbNode.getRelations("related", cloud.getNodeManager("aa"), "destination");
            assertTrue("relation count should be 1 but is " + bbRelationListDestination.size() + " mmbase relations dont' work", bbRelationListDestination.size() == 1);
        }

        {
            //try to get both the aa nodes using "both"
            NodeList bbRelatedListBoth = bbNode.getRelatedNodes("aa", "related", "both");
            assertTrue("relation count should be 2 but is " + bbRelatedListBoth.size() + " mmbase relatednodes(BOTH) does not work", bbRelatedListBoth.size() == 2);
            RelationList bbRelationListBoth = bbNode.getRelations("related", cloud.getNodeManager("aa"),  "both");
            assertTrue("relation count should be 2 but is " + bbRelationListBoth.size() + " mmbase relatednodes(BOTH) does not work", bbRelationListBoth.size() == 2);

        }

        {
            //try to get both the aa node from bb using "null"
            NodeList bbRelatedListNull = bbNode.getRelatedNodes("aa", "related", null);
            assertTrue("relation count should be 2 but is " + bbRelatedListNull.size() + " mmbase relatednodes() does not work", bbRelatedListNull.size() == 2);
            RelationList bbRelationListNull = bbNode.getRelations("related", cloud.getNodeManager("aa"),  null);
            assertTrue("relation count should be 2 but is " + bbRelationListNull.size() + " mmbase relatednodes() does not work", bbRelationListNull.size() == 2);

        }
        {
            try {
                aaFirstNode.delete();
                fail("Should raise a BridgeException");
            } catch (BridgeException e) {};

            try {
                aaSecondNode.delete();
                fail("Should raise a BridgeException");
            } catch (BridgeException e) {};            
            aaSecondNode.delete(true);
            aaFirstNode.delete(true);
            bbNode.delete();
        }
    }
}
