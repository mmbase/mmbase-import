package org.mmbase.applications.vprowizards.spring.action;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.Relation;

public class AbstractRelationTest extends AbstractActionTest {
	protected Node sourceNode = null;
	protected Node destinationNode = null;
	protected Node destinationNode2 = null;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		// create the source and relation node for this test.
		// using the mynews application we will create a posrel between
		// mags and news. Let's create a mags and a news object.
		Cloud cloud = getCloud();
		sourceNode = cloud.getNodeManager("mags").createNode();
		sourceNode.setStringValue("title", "testmags");
		sourceNode.commit();

		destinationNode = cloud.getNodeManager("news").createNode();
		destinationNode.setStringValue("title", "testnews");
		destinationNode.commit();
		
		destinationNode2 = cloud.getNodeManager("news").createNode();
		destinationNode2.setStringValue("title", "testnews2");
		destinationNode2.commit();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		sourceNode.delete(true);
		destinationNode.delete(true);
		destinationNode2.delete(true);
	}
	
	protected Relation createRelation(Node newsNode, int position){
		Cloud c = sourceNode.getCloud();
		Relation r = sourceNode.createRelation(newsNode, c.getRelationManager("posrel"));
		r.setIntValue("pos", position);
		r.commit();
		return r;
	}
}
