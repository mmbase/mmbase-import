/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.vprowizards.spring.CloudFactory;
import org.mmbase.applications.vprowizards.spring.FieldError;
import org.mmbase.applications.vprowizards.spring.GlobalError;
import org.mmbase.applications.vprowizards.spring.WizardController;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.Transaction;
import org.mmbase.bridge.util.Queries;
import org.mmbase.tests.BridgeTest;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

public abstract class AbstractActionTest extends BridgeTest  {
	
	private static final String DUMMY_LOCAL_URL = "www.disco.com/hallo";
	private static final Logger log = Logging.getLoggerInstance(AbstractActionTest.class);
	protected boolean setReferrer = true;
	private MyCloudFactory myCloudFactory = new MyCloudFactory();

	public AbstractActionTest(String name) {
		super(name);
	}

	public AbstractActionTest() {
		super();
	}

	protected MockHttpServletRequest createRequest(Map<String, String> params) {
		MockHttpServletRequest mreqest = new MockHttpServletRequest();
		
		if (params != null) {
			for (String key : params.keySet()) {
				mreqest.addParameter(key, params.get(key));
			}
		}
		if (setReferrer) {
			log.debug("setting referrer header");
			mreqest.addHeader("referer", DUMMY_LOCAL_URL);
		}
		return mreqest;
	}
	
	protected MockHttpServletRequest createRequest(){
		return createRequest("");
	}
	
	/**
	 * ceate a mock request with a string of params formatted like:
	 * param1=some&param2=that.
	 * @param params
	 * @return
	 */
	protected MockHttpServletRequest createRequest(String params){
		Map<String, String> paramMap = new HashMap<String, String>();
		StringTokenizer st = new StringTokenizer(params, "&"), st1;
		while(st.hasMoreTokens()){
			st1 = new StringTokenizer(st.nextToken(), "=");
			paramMap.put(st1.nextToken(), st1.nextToken());
		}
		return createRequest(paramMap);
	}

	protected WizardController createWizardController() {
		BeanFactory f = new XmlBeanFactory(new ClassPathResource(
				"org/mmbase/applications/vprowizards/spring/resources/vpro-wizards-servlet.test.xml"));
		WizardController wizardController = (WizardController) f.getBean("wizardController");
		wizardController.setCloudFactory(myCloudFactory);
		return wizardController;
	}
	
	protected List<GlobalError> getGlobalErrors(Map<String, Object> model) {
		List<GlobalError> result = new ArrayList<GlobalError>();
		log.debug(">>> should find global errors  with key "+GlobalError.MODEL_MAPPING_KEY+" in map: "+model);
		if(model.get(GlobalError.MODEL_MAPPING_KEY) != null){
			for(Object o: (List<?>)model.get(GlobalError.MODEL_MAPPING_KEY)){
				result.add((GlobalError) o);
			}
		}
		return result;
	}
	
	protected List<FieldError> getFieldErrors(Map<String, Object> model) {
		List<FieldError> result = new ArrayList<FieldError>();
		if(model.get(FieldError.MODEL_MAPPING_KEY) != null){
			for(Object o: (List<?>)model.get(FieldError.MODEL_MAPPING_KEY)){
				result.add((FieldError) o);
			}
		}
		return result;
	}
	
	/**
	 * fetch the node with the highest number of given type.
	 * 
	 * @param nodeManagerName
	 * @return the node or null if anything goes wrong
	 */
	protected Node getLastNodeOfType(String nodeManagerName) {
		Cloud cloud = getCloud();
		if(!cloud.hasNodeManager(nodeManagerName)){
			log.error(String.format("Nodemanager of type '%s' is not available", nodeManagerName));
			return null;
		}
		NodeManager nodeManager = cloud.getNodeManager(nodeManagerName);
		NodeQuery query = nodeManager.createQuery();
		Queries.addSortOrders(query, "number", "down");
		query.setMaxNumber(1);
		List<Node> nodeList = nodeManager.getList(query);
		if(nodeList.size() == 1){
			return nodeList.get(0);
		}else{
			log.error(String.format("No nodes found of type '%s'", nodeManagerName));
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param request
	 * @param globalErrorKey
	 * @return true when there is exactly one global error with matching key, and zero field errors, and the the request is redirected to the error page
	 * @throws Exception any exception that might occur.
	 */
	protected void checkRequestCreatesGlobalError(HttpServletRequest request, String globalErrorKey) throws Exception{
		WizardController wc = createWizardController();
		
			ModelAndView mandv = wc.handleRequest(request, null);
			Map<String,Object> model = (Map<String, Object>) mandv.getModel();
			
			assertEquals(0, getFieldErrors(model).size());
			assertEquals(1, getGlobalErrors(model).size());
			assertEquals(globalErrorKey, getGlobalErrors(model).get(0).getMessageKey());
			
			assertTrue("ModelAndView instance not of expected type", ModelAndView.class.isAssignableFrom(mandv.getClass()));
			//the error page is set in the xml bean configuration. check that first if below assertion fails.
			assertEquals( "error-html", mandv.getViewName());
	}
	
	protected void checkNoErrors(ModelAndView mandv){
		Map<String,Object> model = (Map<String, Object>) mandv.getModel();
		assertEquals(0, getFieldErrors(model).size());
		assertEquals(0, getFieldErrors(model).size());
	}
	
	
	public final class MyCloudFactory implements CloudFactory{
		public Transaction createTransaction(HttpServletRequest request) {
			Transaction transaction = getCloud().createTransaction("test-transaction");
			log.debug("transaction: "+transaction.getUser().toString());
            return transaction;
		}
    }
	
	public static class StaticCloudFactory implements CloudFactory{
		private Transaction t;
		
		public StaticCloudFactory(Transaction t) {
			this.t = t;
		}
		public Transaction createTransaction(HttpServletRequest request) {
			log.debug(">> static cloud factory producing transaction: "+t);
			return t;
		}
	}
	
	
		
	
}
