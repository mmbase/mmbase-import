package org.mmbase.applications.vprowizards.spring.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.vprowizards.spring.WizardController;
import org.mmbase.bridge.Relation;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.web.servlet.ModelAndView;

public class SortRelationActionTest extends AbstractRelationTest {
	private static final Logger log = Logging.getLoggerInstance(SortRelationActionTest.class);

	@SuppressWarnings("unchecked")
	public void test_sort_up() {
		Relation r1 = createRelation(destinationNode, 0);
		Relation r2 = createRelation(destinationNode2, 1);
		assertEquals(0, r1.getIntValue("pos"));
		assertEquals(1, r2.getIntValue("pos"));
		
		HttpServletRequest req = createRequest(
				"actions[sortRelation][1].sourceNodeNumber="+sourceNode.getNumber()+
				"&actions[sortRelation][1].destinationNodeNumber="+destinationNode2.getNumber()+
				"&actions[sortRelation][1].role=posrel"+
				"&actions[sortRelation][1].sortField=pos" +
				"&actions[sortRelation][1].direction="+SortRelationAction.DIRECTION_UP);
		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String,Object> model = (Map<String, Object>) mandv.getModel();
			assertNotNull("model is null!", model);
			
			//no errors?
			assertEquals(0, getGlobalErrors(model).size());
			assertEquals(0, getFieldErrors(model).size());
			
			log.debug("new pos r1: "+r1.getIntValue("pos"));
			log.debug("new pos r2: "+r2.getIntValue("pos"));
			log.debug(String.format("r1 node number is %s and r2 node number is %s", r1.getNumber(), r2.getNumber()));
			
			//check the new pos fields. they should be switched
			assertTrue("relation 1 should have a higher pos value that relation 2", r1.getIntValue("pos") > r2.getIntValue("pos"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	public void test_sort_down() {
//		fail("implement");
	}

	public void test_sort_up_with_duplicate_sortfield_values() {
//		fail("implement");
	}

	public void test_sort_down_with_duplicate_sortfield_values() {
//		fail("implement");
	}
}
