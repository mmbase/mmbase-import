/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.mmbase.applications.vprowizards.spring.GlobalError;
import org.mmbase.applications.vprowizards.spring.WizardController;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.web.servlet.ModelAndView;

public class CreateRelationActionTest extends AbstractRelationActionTest {


	private static final Logger log = Logging.getLoggerInstance(CreateRelationActionTest.class);

	/**
	 * deletes all relation nodes of type posrel between sourcenode and destinationnode
	 * 
	 * @return the number of nodes deleted
	 */
	protected int deleteRelations() {
		int counter = 0;
		Cloud cloud = getCloud();
		NodeList nl = cloud.getList(null, "mags,posrel,news", "posrel.number", null, null, null, null, true);
		for (NodeIterator ni = nl.nodeIterator(); ni.hasNext();) {
			Node relation = ni.nextNode().getNodeValue("posrel");
			relation.delete();
			counter++;
		}
		return counter;
	}

	public void test_setup() {
		assertEquals("dummy", "dummy");
		assertNotNull(sourceNode);
		assertNotNull(destinationNode);
		assertEquals("mags", sourceNode.getNodeManager().getName());
		assertEquals("news", destinationNode.getNodeManager().getName());
		assertEquals("testmags", sourceNode.getStringValue("title"));
		assertEquals("testnews", destinationNode.getStringValue("title"));
		assertTrue(sourceNode.getNumber() > 0);
		assertTrue(destinationNode.getNumber() > 0);
	}

	public void test_empty_role_creates_global_error() {
		HttpServletRequest req = createRequest("actions[createRelation][1].sourceNode=" + sourceNode.getNumber()
				+ "&actions[createRelation][1].destinationNode=" + destinationNode.getNumber());

		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String, Object> model = (Map<String, Object>) mandv.getModel();

			assertEquals(1, getGlobalErrors(model).size());
			assertEquals(0, getFieldErrors(model).size());
			GlobalError globalError = getGlobalErrors(model).get(0);
			assertEquals("error.property.required", globalError.getMessageKey());
			assertNotNull(globalError.getProperties());
			assertEquals("role", globalError.getProperties()[0]);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void test_nonexistant_role_creates_global_error() {
		HttpServletRequest req = createRequest("actions[createRelation][1].sourceNodeNumber=" + sourceNode.getNumber()
				+ "&actions[createRelation][1].destinationNodeNumber=" + destinationNode.getNumber()
				+ "&actions[createRelation][1].role=hasdahasda");

		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String, Object> model = (Map<String, Object>) mandv.getModel();

			assertEquals(1, getGlobalErrors(model).size());
			assertEquals(0, getFieldErrors(model).size());
			GlobalError globalError = getGlobalErrors(model).get(0);
			assertEquals("error.illegal.relationmanager", globalError.getMessageKey());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void test_illegal_typerel_credates_global_error() {
		// this test uses a valid reldef, but invalid typerel
		HttpServletRequest req = createRequest("actions[createRelation][1].sourceNodeNumber=" + sourceNode.getNumber()
				+ "&actions[createRelation][1].destinationNodeNumber=" + destinationNode.getNumber()
				+ "&actions[createRelation][1].role=sorted");

		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String, Object> model = (Map<String, Object>) mandv.getModel();

			assertEquals(1, getGlobalErrors(model).size());
			assertEquals(0, getFieldErrors(model).size());
			GlobalError globalError = getGlobalErrors(model).get(0);
			assertEquals("error.create.relation.typerel", globalError.getMessageKey());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void test_illegal_source_creates_global_error() {
		HttpServletRequest req = createRequest("actions[createRelation][1].sourceNodeNumber=10000000"
				+ "&actions[createRelation][1].destinationNodeNumber=" + destinationNode.getNumber()
				+ "&actions[createRelation][1].role=posrel");

		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String, Object> model = (Map<String, Object>) mandv.getModel();

			log.info("***" + getGlobalErrors(model));

			assertEquals(1, getGlobalErrors(model).size());
			assertEquals(0, getFieldErrors(model).size());
			GlobalError globalError = getGlobalErrors(model).get(0);
			assertEquals("error.node.notfound", globalError.getMessageKey());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void test_illegal_destination_creates_global_error() {
		HttpServletRequest req = createRequest("actions[createRelation][1].sourceNodeNumber=" + sourceNode.getNumber()
				+ "&actions[createRelation][1].destinationNodeNumber=10000000"
				+ "&actions[createRelation][1].role=posrel");

		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String, Object> model = (Map<String, Object>) mandv.getModel();

			assertEquals(1, getGlobalErrors(model).size());
			assertEquals(0, getFieldErrors(model).size());
			GlobalError globalError = getGlobalErrors(model).get(0);
			assertEquals("error.node.notfound", globalError.getMessageKey());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void test_no_source_or_sourceref_creates_global_error() {
		HttpServletRequest req = createRequest("actions[createRelation][1].destinationNodeNumber="
				+ destinationNode.getNumber() + "&actions[createRelation][1].role=posrel");

		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String, Object> model = (Map<String, Object>) mandv.getModel();

			assertEquals(1, getGlobalErrors(model).size());
			assertEquals(0, getFieldErrors(model).size());
			GlobalError globalError = getGlobalErrors(model).get(0);
			assertEquals("error.create.relation.nosource", globalError.getMessageKey());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void test_no_destination_or_destinationref_creates_global_error() {
		HttpServletRequest req = createRequest("actions[createRelation][1].sourceNodeNumber=" + sourceNode.getNumber()
				+ "&actions[createRelation][1].role=posrel");

		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String, Object> model = (Map<String, Object>) mandv.getModel();

			assertEquals(1, getGlobalErrors(model).size());
			assertEquals(0, getFieldErrors(model).size());
			GlobalError globalError = getGlobalErrors(model).get(0);
			assertEquals("error.create.relation.nodestination", globalError.getMessageKey());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void test_sourceref() {
		HttpServletRequest req = createRequest(
				"actions[createNode][1].nodeType=mags"
				+ "&actions[createNode][1].fields[title]=magsforsourceref" 
				+ "&actions[createNode][1].id=newmags"
				+ "&actions[createRelation][1].sourceNodeRef=newmags"
				+ "&actions[createRelation][1].destinationNodeNumber=" + destinationNode.getNumber()
				+ "&actions[createRelation][1].role=posrel");

		WizardController wc = createWizardController();
		try {
			// make sure there are no relations
			deleteRelations();

			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String, Object> model = (Map<String, Object>) mandv.getModel();

			assertEquals(0, getGlobalErrors(model).size());
			assertEquals(0, getFieldErrors(model).size());

			// new node created?
			Node magsnode = getLastNodeOfType("mags");
			assertEquals("magsforsourceref", magsnode.getStringValue("title"));

			// and in idmap?
			Map<String, Node> idMap = (Map<String, Node>) model.get("idmap");
			assertNotNull(idMap.get("newmags"));

			// posrel created?
			assertEquals(1, deleteRelations());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public void test_destinationref() {
		HttpServletRequest req = createRequest(
				"actions[createNode][1].nodeType=news"
				+ "&actions[createNode][1].fields[title]=sometitle" 
				+ "&actions[createNode][1].id=newid"
				+ "&actions[createRelation][1].sourceNodeNumber=" + sourceNode.getNumber()
				+ "&actions[createRelation][1].destinationNodeRef=newid" 
				+ "&actions[createRelation][1].role=posrel");

		WizardController wc = createWizardController();
		try {
			// make sure there are no relations
			deleteRelations();

			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String, Object> model = (Map<String, Object>) mandv.getModel();

			assertEquals(0, getGlobalErrors(model).size());
			assertEquals(0, getFieldErrors(model).size());

			// news node created?
			Node newsNode = getLastNodeOfType("news");
			assertEquals("sometitle", newsNode.getStringValue("title"));

			// and in idmap?
			Map<String, Node> idMap = (Map<String, Node>) model.get("idmap");
			assertNotNull(idMap.get("newid"));

			// posrel created?
			assertEquals(1, deleteRelations());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void test_sortorder_up() {
		try {
			// first create a relation between the 'setup' nodes
			createRelation(sourceNode, destinationNode, null);
			Node posrelNode = getLastNodeOfType("posrel");
			assertNotNull(posrelNode.getValue("pos"));
			
			// than create a new newsnode, and relate it to the mags node with position 'begin'
			Node n1 = createNewsNode("node1");
			assertEquals("node1", n1.getStringValue("title"));
			createRelation(sourceNode, n1, CreateRelationAction.SORT_POSITION_BEGIN);
			Node posrelNode1 = getLastNodeOfType("posrel");

			// test the position (should be lower).
			assertNotNull(posrelNode1.getValue("pos"));
			assertTrue("new relation pos value should be lower", posrelNode1.getIntValue("pos") < posrelNode.getIntValue("pos"));

			// than create a new newsnode, and relate it to the mags node with position 'end'
			Node n2 = createNewsNode("node2");
			assertEquals("node2", n2.getStringValue("title"));
			createRelation(sourceNode, n2, CreateRelationAction.SORT_POSITION_END);
			Node posrelNode2 = getLastNodeOfType("posrel");

			// test the position (should be higher).
			assertNotNull(posrelNode2.getValue("pos"));
			assertTrue("new relation pos value should be higher", posrelNode2.getIntValue("pos") > posrelNode.getIntValue("pos"));
			assertTrue("new relation pos value should be higher", posrelNode2.getIntValue("pos") > posrelNode1.getIntValue("pos"));
			log.debug("relation position 1: "+posrelNode.getStringValue("pos"));
			log.debug("relation position 2: "+posrelNode1.getStringValue("pos")+" must be lower");
			log.debug("relation position 3: "+posrelNode2.getStringValue("pos")+" must be higher");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	public void test_sortorder_down() {
	}
	
	private void createRelation(Node sNode, Node dNode, String position){
		HttpServletRequest req = createRequest(
				"actions[createRelation][1].sourceNodeNumber="+sNode.getNumber()+
				"&actions[createRelation][1].destinationNodeNumber="+dNode.getNumber()+
				"&actions[createRelation][1].role=posrel"+
				"&actions[createRelation][1].sortField=pos" +
				(StringUtils.isBlank(position) ? "" : "&actions[createRelation][1].sortPosition=" + position));
		WizardController wc = createWizardController();
		try {
			wc.handleRequest(req, null);
		} catch (Exception e) {
			e.printStackTrace();
			fail("oops");
		}
	}
	
	private Node createNewsNode(String title){
		HttpServletRequest req = createRequest(
				"actions[createNode][1].nodeType=news"+
				"&actions[createNode][1].fields[title]="+title);
		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String,Object> model = mandv.getModel();
			if(getGlobalErrors(model).size() > 0){
				log.error(">>> errors: "+getGlobalErrors(model));
			}
			assertEquals(0, getGlobalErrors(model).size());
			return getLastNodeOfType("news");
		} catch (Exception e) {
			e.printStackTrace();
			fail("oops");
		}
		return null;
	}

}
