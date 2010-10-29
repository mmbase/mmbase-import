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
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

public class CreateNodeActionTest extends AbstractActionTest {
	private static final Logger log = Logging.getLoggerInstance(CreateNodeActionTest.class);
	
	public CreateNodeActionTest() {
		super();
	}
	
	


	@SuppressWarnings("unchecked")
	public void test_no_nodemanager_should_couse_global_error(){
		HttpServletRequest req = createRequest("actions[createNode][1].id=testId");
		try {
			checkRequestCreatesGlobalError(req, "error.property.required");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	public void test_no_referrer_header_should_couse_global_error(){
		setReferrer = false;
		HttpServletRequest req = createRequest("actions[createNode][1].nodeType=mags");
		try {
			checkRequestCreatesGlobalError(req, "error.no.referrer.header");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}finally{
			setReferrer = true;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void test_illegal_nodemanager_should_couse_global_error(){
		HttpServletRequest req = createRequest("actions[createNode][1].nodeType=nonexistant");
		assertEquals(1, req.getParameterMap().size());
		
		try {
			checkRequestCreatesGlobalError(req, "error.illegal.nodemanager");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	public void test_global_error_should_couse_redirect_to_errorpage(){
		//TODO:  this is not really a create node action, it should not be here
		HttpServletRequest req = createRequest("actions[createNode][1].id=fail");
		//create an error by means of an invalid createNodeAction
		
		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			assertTrue("ModelAndView instance not of expected type", ModelAndView.class.isAssignableFrom(mandv.getClass()));
			assertEquals( "error-html", mandv.getViewName());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	public void test_legal_nodemanager_should_return_to_referrer(){
		//TODO:  this is not really a create node action, it should not be here
		MockHttpServletRequest req = createRequest(
				"actions[createNode][1].nodeType=mags" +
				"&actions[createNode][1].fields[title]=sometitle" +
				"&actions[createNode][1].fields[subtitle]=somesubtitle");
		String dummyURL = "www.disco.com/hallo";
		req.setLocalAddr(dummyURL);
		
		WizardController wc = createWizardController();
		
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			View view = mandv.getView();
			assertNotNull(view);
			assertTrue(RedirectView.class.isAssignableFrom(view.getClass()));
			RedirectView rdv = (RedirectView) view;
			assertEquals( dummyURL, rdv.getUrl());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	

	public void test_legal_nodemanager_should_create_node(){
		MockHttpServletRequest req = createRequest(
				"actions[createNode][1].nodeType=mags" +
				"&actions[createNode][1].fields[title]=sometitle" +
				"&actions[createNode][1].fields[subtitle]=somesubtitle");
		assertEquals(3, req.getParameterMap().size());
		
		WizardController wc = createWizardController();
		assertNotNull("transaction is null!", wc.getCloudFactory().createTransaction(req));
		
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String,Object> model = (Map<String, Object>) mandv.getModel();
			
			View view = mandv.getView();
			assertTrue(RedirectView.class.isAssignableFrom(view.getClass()));
			
			checkNoErrors(mandv);

			//fetch the node we just created
			Node node = getLastNodeOfType("mags");
			log.debug("node we just creted: "+node);
			
			assertNotNull("Node of type 'mags' has not been created", node);
			assertEquals("sometitle", node.getStringValue("title"));
			assertEquals("somesubtitle", node.getStringValue("subtitle"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	


	public void test_illegal_fields_should_couse_global_error(){
		HttpServletRequest req = createRequest(
				"actions[createNode][1].nodeType=mags" +
				"&actions[createNode][1].fields[notthere]=howdown" +
				"&actions[createNode][1].fields[missing]=bananas");
		
		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String,Object> model = (Map<String, Object>) mandv.getModel();
			
			//errors
			assertEquals(2, getGlobalErrors(model).size());
			assertEquals(0, getFieldErrors(model).size());
			assertEquals("error.field.unknown", getGlobalErrors(model).get(0).getMessageKey());
			assertEquals("error.field.unknown", getGlobalErrors(model).get(1).getMessageKey());

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void test_node_with_id_is_added_to_idmap(){
		HttpServletRequest req = createRequest(
				"actions[createNode][1].nodeType=mags" +
				"&actions[createNode][1].id=someid");
		
		WizardController wc = createWizardController();
		try {
			ModelAndView mandv = wc.handleRequest(req, null);
			Map<String,Object> model = (Map<String, Object>) mandv.getModel();
			Map<String,Node> idmap = (Map<String, Node>) model.get("idmap");
			
			assertEquals(0, getFieldErrors(model).size());
			assertEquals(0, getGlobalErrors(model).size());
			
			assertNotNull(idmap.get("someid"));

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
}
