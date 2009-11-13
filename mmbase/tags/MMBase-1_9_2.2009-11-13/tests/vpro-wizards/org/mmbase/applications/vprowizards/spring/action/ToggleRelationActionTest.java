/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring.action;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.vprowizards.spring.WizardController;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class ToggleRelationActionTest extends AbstractRelationActionTest {
	private static final Logger log = Logging.getLoggerInstance(ToggleRelationActionTest.class);
	
	/**
	 * A relation should be created.
	 */
	public void test_toggle_nonexisting_relation_on(){
		HttpServletRequest req = createRequestLocal("true");
		WizardController wc = createWizardController();
		try {
			assertEquals(0, findRelation(destinationNode).size());
			wc.handleRequest(req, null);
			assertEquals(1, findRelation(destinationNode).size());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail("exception: "+e.getMessage());
		}finally{
			Node node = getLastNodeOfType("posrel");
			if(node != null)node.delete(true);
		}
	}
	
	/**
	 * nothing should happen
	 */
	public void test_toggle_nonexisting_relation_off(){
		HttpServletRequest req = createRequestLocal("false");
		WizardController wc = createWizardController();
		try {
			assertEquals(0, findRelation(destinationNode).size());
			wc.handleRequest(req, null);
			assertEquals(0, findRelation(destinationNode).size());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail("exception: "+e.getMessage());
		}
	}
	
	/**
	 * nothing should happen
	 */
	public void test_toggle_existing_relation_on(){
		//first create a relation between source and destination
		createRelation(destinationNode, 1);
		assertEquals(1, findRelation(destinationNode).size());
		
		HttpServletRequest req = createRequestLocal("true");
		WizardController wc = createWizardController();
		try {
			wc.handleRequest(req, null);
			assertEquals(1, findRelation(destinationNode).size());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail("exception: "+e.getMessage());
		}finally{
			Node node = getLastNodeOfType("posrel");
			if(node != null)node.delete(true);
		}
	}
	
	/**
	 * the relation should be deleted
	 */
	public void test_toggle_existing_relation_off(){
		//first create a relation between source and destination
		createRelation(destinationNode, 1);
		assertEquals(1, findRelation(destinationNode).size());
		
		HttpServletRequest req = createRequestLocal("false");
		WizardController wc = createWizardController();
		try {
			wc.handleRequest(req, null);
			assertEquals(0, findRelation(destinationNode).size());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail("exception: "+e.getMessage());
		}
	}

	private HttpServletRequest createRequestLocal(String relate) {
	
		return createRequest("actions[toggleRelation][1].sourceNodeNumber=" + sourceNode.getNumber()
				+ "&actions[toggleRelation][1].destinationNodeNumber=" + destinationNode.getNumber()
				+ "&actions[toggleRelation][1].role=posrel"
				+ "&actions[toggleRelation][1].relate=" + relate);
	}
}
