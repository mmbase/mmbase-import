/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge;

import org.mmbase.tests.*;
/**
 * Test cases to test the creation of relations and the retrieval 
 * @author Kees Jongenburger
 * @version $Id: RelationTest.java,v 1.2 2003-11-11 11:11:37 michiel Exp $
 */
public class RelationTest extends BridgeTest {
    
    public RelationTest(String name){
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
        aaFirstNode.createRelation(bbNode,relatedRelationManager).commit();
        
        //create a relation from bbNode to aaSecondNode
        bbNode.createRelation(aaSecondNode,relatedRelationManager).commit();
        
        //we now have 3 node
        //aaFirst -> related -> bb -> related -> aaSecond
        //check back if we can get the direction
        
        NodeList aaRelatedList = aaFirstNode.getRelatedNodes("bb","related" ,"source");
        assertTrue("related list has size " + aaRelatedList.size() + " but should be 1", aaRelatedList.size() == 1);
        NodeList bbRelatedListSource = bbNode.getRelatedNodes("aa","related","source");
        assertTrue("relation count should be 1 but is "+ bbRelatedListSource.size() +" mmbase relations dont' work",bbRelatedListSource.size() == 1);
        
        NodeList bbRelatedListDestination = bbNode.getRelatedNodes("aa","related","destination");
        assertTrue("relation count should be 1 buts is "+bbRelatedListDestination.size() +" mmbase relations dont' work",bbRelatedListDestination.size() ==1);
		
    }
}
