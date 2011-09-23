/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring.action;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.vprowizards.spring.WizardController;
import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.NotFoundException;
import org.springframework.web.servlet.ModelAndView;

public class DeleteNodeActionTest extends AbstractActionTest{

	public void test_delete_existing_node(){
	
		Cloud c = getCloud();
		org.mmbase.bridge.Node n = c.getNodeManager("news").createNode();
		n.setStringValue("title", "test");
		n.commit();
		int nodenr = n.getNumber();
		HttpServletRequest req = createRequest("actions[deleteNode][1].nodenr="+nodenr);
		WizardController wc = createWizardController();
		try {
			ModelAndView mandv =  wc.handleRequest(req, null);
			checkNoErrors(mandv);
			try{
				c.getNode(nodenr);
				fail("node is found, but should be deleted");
			}catch(NotFoundException e){}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void test_delete_nonexisting_node_should_couse_global_error(){
		HttpServletRequest req = createRequest("actions[deleteNode][1].nodenr=879878979");
		try {
			checkRequestCreatesGlobalError(req, "error.node.notfound");
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
