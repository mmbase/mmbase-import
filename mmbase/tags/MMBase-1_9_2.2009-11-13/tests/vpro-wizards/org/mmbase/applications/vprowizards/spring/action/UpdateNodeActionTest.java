/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.vprowizards.spring.action;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.mmbase.applications.vprowizards.spring.WizardController;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class UpdateNodeActionTest extends AbstractActionTest {
	private static final Logger log = Logging.getLoggerInstance(UpdateNodeActionTest.class);
	
	/**
	 * check we get the right error when no nodenr is set
	 */
	public void test_no_nodenr_creates_global_error(){
		HttpServletRequest req = createRequest("actions[updateNode][1].id=testId");
		try {
			checkRequestCreatesGlobalError(req, "error.property.required");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}

	/**
	 * check we get the right error when a non existing node number is set
	 */
	public void test_nonexistant_node_creates_global_error(){
		HttpServletRequest req = createRequest("actions[updateNode][1].nodenr=testId");
		try {
			checkRequestCreatesGlobalError(req, "error.node.notfound");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test if we get the right error when we update an existing node, but set
	 * a field that is not present in the node.
	 */
	public void test_nonexistant_field_creates_global_error(){
		int nodenr = getLastNodeOfType("news").getNumber();
		HttpServletRequest req = createRequest(
				"actions[updateNode][1].nodenr=" + nodenr + 
				"&actions[updateNode][1].fields[thisisnotafield]=somevalue");
		try {
			checkRequestCreatesGlobalError(req, "error.field.unknown");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	
	
	
	
	public void test_update_existing_node_with_valid_field_ok(){
		Node n = getLastNodeOfType("news");
		final String newTitle = "incredibleTitle";
		final int number = n.getNumber();
		
		HttpServletRequest req = createRequest(
				"actions[updateNode][1].nodenr=" + n.getNumber() + 
				"&actions[updateNode][1].fields[title]="+newTitle);
		WizardController wc = createWizardController();
		
		try{
			checkNoErrors(wc.handleRequest(req, null));
			assertEquals(newTitle, getCloud().getNode(number).getStringValue("title"));
		}catch(Exception e){
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	public void test_update_date_field(){
		Node n = getCloud().getNodeManager("mmevents").createNode();
		n.commit();
		int number =n.getNumber();
		
		HttpServletRequest req = createRequest(
				"actions[updateNode][1].nodenr=" + number + 
				"&actions[updateNode][1].dateFields[start].date=01-01-2007"+
				"&actions[updateNode][1].dateFields[start].time=12:00:00");
		WizardController wc = createWizardController();
		
		try{
			checkNoErrors(wc.handleRequest(req, null));
			n = getCloud().getNode(number);
			Date d = n.getDateValue("start");
			log.debug("date as read from node: "+d);
			
			GregorianCalendar c = new GregorianCalendar(new Locale("nl", "NL"));
			c.setTime(d);
			log.debug("date as read from calendar object: "+c);
			
			assertEquals(2007, c.get(Calendar.YEAR));
			assertEquals(1, c.get(Calendar.DAY_OF_MONTH));
			
			//the calendar seems to start counting the months at 0 in stead of 1. Don't know how to switch that (should be determined by locale)
			assertEquals(0, c.get(Calendar.MONTH));
			
			//the calendar seems to start counting the hours at 0 in stead of 1. Don't know how to switch that (should be determined by locale)
			assertEquals(12, c.get(Calendar.HOUR_OF_DAY));
			assertEquals(0, c.get(Calendar.MINUTE));
			assertEquals(0, c.get(Calendar.SECOND));
			
		}catch(Exception e){
			log.error(e.getMessage(), e);
			fail(e.getMessage());
		}
	}
	
	
	public void setUp(){
		Node node = getCloud().getNodeManager("news").createNode();
		node.setStringValue("title", "title");
		node.commit();
	}
}
