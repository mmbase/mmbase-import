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
 * @version $Id: RelationTest.java,v 1.4 2003-11-28 12:04:30 keesj Exp $
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
        }

        {
            //try to get the first aa node from the bb node
            NodeList bbRelatedListSource = bbNode.getRelatedNodes("aa", "related", "source");
            assertTrue("relation count should be 1 but is " + bbRelatedListSource.size() + " mmbase relations dont' work", bbRelatedListSource.size() == 1);
            if (bbRelatedListSource.size() > 0) {
                Node sourceNode = bbRelatedListSource.getNode(0);
                assertTrue("expected the first aa node but got " + sourceNode, sourceNode.getNumber() == aaFirstNode.getNumber());
            }
        }

        {
            //try to get the second aa node from the bb node
            NodeList bbRelatedListSource = bbNode.getRelatedNodes("aa", "related", "destination");
            assertTrue("relation count should be 1 but is " + bbRelatedListSource.size() + " mmbase relations dont' work", bbRelatedListSource.size() == 1);
            if (bbRelatedListSource.size() > 0) {
                Node sourceNode = bbRelatedListSource.getNode(0);
                assertTrue("expected the first aa node but got " + sourceNode, sourceNode.getNumber() == aaSecondNode.getNumber());
            }
        }

        {
            //try to get both the aa nodes using "both"
            NodeList bbRelatedListSource = bbNode.getRelatedNodes("aa", "related", "both");
            assertTrue("relation count should be 2 but is " + bbRelatedListSource.size() + " mmbase relatednodes(BOTH) does not work", bbRelatedListSource.size() == 2);
        }
        
        {
            //try to get both the aa node from bb using "null"
            NodeList bbRelatedListSource = bbNode.getRelatedNodes("aa", "related", null);
            assertTrue("relation count should be 2 but is " + bbRelatedListSource.size() + " mmbase relatednodes() does not work", bbRelatedListSource.size() == 2);
        }
    }
}
