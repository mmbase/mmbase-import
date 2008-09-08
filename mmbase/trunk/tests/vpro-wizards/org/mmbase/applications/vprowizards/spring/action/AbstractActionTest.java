package org.mmbase.applications.vprowizards.spring.action;

import java.net.URL;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.vprowizards.spring.CloudFactory;
import org.mmbase.applications.vprowizards.spring.FieldError;
import org.mmbase.applications.vprowizards.spring.GlobalError;
import org.mmbase.applications.vprowizards.spring.WizardController;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.NodeQuery;
import org.mmbase.bridge.Query;
import org.mmbase.bridge.Transaction;
import org.mmbase.bridge.util.Queries;
import org.mmbase.tests.BridgeTest;
import org.mmbase.tests.MMBaseTest;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;

public abstract class AbstractActionTest extends BridgeTest {
	
	private static final String DUMMY_LOCAL_URL = "www.disco.com/hallo";
	private static final Logger log = Logging.getLoggerInstance(AbstractActionTest.class);
	protected boolean setReferrer = true;

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
			mreqest.addHeader("Referrer", DUMMY_LOCAL_URL);
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
		wizardController.setCloudFactory(getCloudFactory());
		return wizardController;
	}
	
	protected List<GlobalError> getGlobalErrors(Map<String, Object> model) {
		List<GlobalError> result = new ArrayList<GlobalError>();
		if(model.get("globalerrors") != null){
			for(Object o: (List<?>)model.get("globalerrors")){
				result.add((GlobalError) o);
			}
		}
		return result;
	}
	
	protected List<FieldError> getFieldErrors(Map<String, Object> model) {
		List<FieldError> result = new ArrayList<FieldError>();
		if(model.get("fielderrors") != null){
			for(Object o: (List<?>)model.get("fielderrors")){
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
	
	protected CloudFactory getCloudFactory(){
		assertNotNull("can not create cloud factory, for cloud is null", getCloud());
		MyCloudFactory myCloudFactory = new MyCloudFactory(getCloud());
		return myCloudFactory;
	}
	
	static class MyCloudFactory implements CloudFactory{

		private Cloud cloud;
		
		public MyCloudFactory(Cloud c){
			if(c == null){
				throw new NullPointerException("oops, cloud argument is null");
			}
			cloud = c;
		}
		
		public Transaction getTransaction(HttpServletRequest request) {
			Transaction transaction = cloud.getTransaction("test transaction");
			if(transaction == null){
				throw new IllegalStateException("hey, transaction is null!");
			}
			return transaction;
		}
		
	}
	
}
