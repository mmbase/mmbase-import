/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.vprowizards.spring.WizardController;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Relation;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.web.servlet.ModelAndView;

public class SortRelationActionTest extends AbstractRelationActionTest {
	private static final Logger log = Logging.getLoggerInstance(SortRelationActionTest.class);

	@SuppressWarnings("unchecked")
	private void test_sort(String direction, int r1Posvalue, int r2PosValue) {
		Relation r1 = createRelation(destinationNode, r1Posvalue);
		Relation r2 = createRelation(destinationNode2, r2PosValue);
		int r1Number = r1.getNumber();
		int r2Number = r2.getNumber();
		assertEquals(r1Posvalue, r1.getIntValue("pos"));
		assertEquals(r2PosValue, r2.getIntValue("pos"));

		HttpServletRequest req = createRequest("actions[sortRelation][1].sourceNodeNumber=" + sourceNode.getNumber()
				+ "&actions[sortRelation][1].destinationNodeNumber=" + destinationNode2.getNumber()
				+ "&actions[sortRelation][1].role=posrel" + "&actions[sortRelation][1].sortField=pos"
				+ "&actions[sortRelation][1].direction=" + direction);
		// "&actions[sortRelation][1].direction="+SortRelationAction.DIRECTION_UP);
		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String, Object> model = (Map<String, Object>) mandv.getModel();
			assertNotNull("model is null!", model);

			// no errors?
			assertEquals(0, getGlobalErrors(model).size());
			assertEquals(0, getFieldErrors(model).size());

			Cloud c = getCloud();

			int newr1posvalue = c.getNode(r1Number).getIntValue("pos");
			int newr2posvalue = c.getNode(r2Number).getIntValue("pos");

			log.debug("new pos r1: " + newr1posvalue);
			log.debug("new pos r2: " + newr2posvalue);
			log.debug(String.format("r1 node number is %s and r2 node number is %s", c.getNode(r1Number).getNumber(), c
					.getNode(r2Number).getNumber()));


				if (r1Posvalue != r2PosValue) {
					if (direction.equals(SortRelationAction.DIRECTION_UP)) {
						assertTrue("relation 1 should have a higher pos value that relation 2",
								newr1posvalue > newr2posvalue);
					}
					if (direction.equals(SortRelationAction.DIRECTION_DOWN)) {
						assertTrue("relation 1 should have a higher pos value that relation 2",
								newr1posvalue < newr2posvalue);
					}
				}else{
					//we're testing the resorting
					assertTrue("relation 1 should not have the same value as relation 2", newr1posvalue != newr2posvalue);
				}
			// delete the relations again.
			c.getNode(r1Number).delete(true);
			c.getNode(r2Number).delete(true);

		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void test_sort() {
		test_sort(SortRelationAction.DIRECTION_UP, 0, 1);
	}

	public void test_sort_down() {
		test_sort(SortRelationAction.DIRECTION_DOWN, 0, 1);
	}

	public void test_sort_up_with_duplicate_sortfield_values() {
		//when the list of relations to be sorted contain double values, 
		//they are renumbered first.
		//how this is done exactly i don't care, all i care about is that 
		//they don't have the same value anymore
		 test_sort(SortRelationAction.DIRECTION_UP, 1, 1);
	}


}
