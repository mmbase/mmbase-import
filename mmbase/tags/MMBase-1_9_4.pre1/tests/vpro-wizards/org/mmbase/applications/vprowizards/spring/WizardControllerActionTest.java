/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.mmbase.applications.vprowizards.spring.action.Action;
import org.mmbase.applications.vprowizards.spring.action.TestAction;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * This test will create a wizard controller and will test the following things:
 * <ul>
 * <li>data binding</li>
 * <li>error setting</li>
 * </ul>
 * 
 * @author Ernst Bunders
 * 
 */
public class WizardControllerActionTest extends TestCase {
	public void testController() {
		// create the wizard controller.
		// BeanFactory f = new XmlBeanFactory(new
		// ClassPathResource("org/mmbase/applications/vprowizards/spring/resources/vpro-wizards-servlet.test.xml"));

		WizardController testController = createController();

		// create mock request and response objects.
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		// create an instance of the test action class
		request.setParameter("actions[test][een].name", "hallo");
		request.setParameter("actions[test][een].fields[een]", "een");
		request.setParameter("actions[test][een].fields[twee]", "twee");

		try {
			testController.handleRequest(request, response);
			assertNotNull(request.getAttribute("test"));
			TestAction action = (TestAction) request.getAttribute("test");
			assertEquals(action.getName(), "hallo");

			assertNotNull(action.getFields());
			assertEquals(2, action.getFields().size());
			assertEquals("een", action.getFields().get("een"));
			assertEquals("twee", action.getFields().get("twee"));

		} catch (Exception e) {
			fail();
			e.printStackTrace();

		}

	}

	/**
	 * Test if the configuration reading is ok (for the controller).
	 */
	public void testSpringConfiguration() {
		WizardController wizardController = createApplicationContext();
		assertEquals(wizardController.getViewResolver().getClass(), ReferrerResolver.class);
		CommandFactory commandFactory = wizardController.getCommandFactory();
		assertEquals(commandFactory.getClass(), BasicCommandFactory.class);

		BasicCommandFactory basicCommandFactory = (BasicCommandFactory) commandFactory;
		assertEquals(7, basicCommandFactory.getActionClasses().size());
		assertEquals(TestAction.class, basicCommandFactory.getActionClasses().get(0));
	}

	/**
	 * Test if the creation of an error is handeled well.
	 */
	public void testError() {
		WizardController testController = createController();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		// now try to make an error
		request.setParameter("actions[test][een].error", "true");
		try {
			testController.handleRequest(request, response);
			ResultContainer resultContainer = (ResultContainer) request.getAttribute("result");
			assertNotNull(resultContainer.getGlobalErrors());
			assertEquals(1, resultContainer.getGlobalErrors().size());
			GlobalError globalError = resultContainer.getGlobalErrors().get(0);
			assertEquals("testAction", globalError.getMessageKey());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private WizardController createController() {
		WizardController testController = new WizardController();
		BasicCommandFactory cf = new BasicCommandFactory();
		List<Class<? extends Action>> actionClasses = new ArrayList<Class<? extends Action>>();
		actionClasses.add(TestAction.class);
		cf.setActionClasses(actionClasses);
		ReferrerResolver referrerResolver = new ReferrerResolver();
		referrerResolver.setErrorPage("error.jsp");

		testController.setCloudFactory(new DummyCloudFactory());
		testController.setCommandFactory(cf);
		testController.setViewResolver(referrerResolver);
		return testController;
	}

	private WizardController createApplicationContext() {
		BeanFactory f = new XmlBeanFactory(new ClassPathResource(
				"org/mmbase/applications/vprowizards/spring/resources/vpro-wizards-servlet.test.xml"));
		return (WizardController) f.getBean("wizardController");
	}

}
