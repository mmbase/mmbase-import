/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Arjan Houtman
 */
public class Vwmtasks extends MMObjectBuilder implements Runnable {
 	public boolean replaceCache=true;

	Thread kicker;
	Vwms vwms;
	Hashtable vwm_cache = new Hashtable ();
	int lastchecked=0;

	/**
	 * Starts the admin Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			kicker = new Thread(this,"VwmTasks");
			kicker.start();
		}
	}
	
	/**
	 * Stops the admin Thread.
	 */
	public void stop() {
		/* Stop thread */
		kicker.setPriority(Thread.MIN_PRIORITY);  
		kicker.suspend();
		kicker.stop();
		kicker = null;
	}

	/**
	 * admin probe, try's to make a call to all the maintainance calls.
	 */
	public void run() {
		kicker.setPriority(Thread.MIN_PRIORITY+1);  
		if (debug) System.out.println("VWMtasks -> getVwmTasks thread started");
		while (kicker!=null) {
			try {Thread.sleep(37*1000);} catch (InterruptedException e){}
			getVwmTasks();
		}
	}


	public MMObjectNode preCommit(MMObjectNode node) {
		node.setValue("changedtime",(int)(DateSupport.currentTimeMillis()/1000)); 
		return(node);
	}

	public String getGUIIndicator (MMObjectNode node) {
		String str = node.getStringValue ("task");
		if (str.length () > 15) {
			return (str.substring (0,12) + "...");
		} else {
			return (str);
		}
	}

	public String getGUIIndicator (String field, MMObjectNode node) {
		if (field.equals ("status")) {
			int val = node.getIntValue ("status");
			if (val==1) { 
				return("verzoek");
			} else if (val==2) {
				return("onderweg");
			} else if (val==3) {
				return("gedaan");
			} else if (val==4) {
				return("timeout");
			} else if (val==5) {
				return("error emailed");
			} else {
				return ("unknown");
			}
		} else if (field.equals("changedtime")) {
			int str=node.getIntValue("changedtime");
			return(DateSupport.getTimeSec(str)+" op "+DateSupport.getMonthDay(str)+"/"+DateSupport.getMonth(str)+"/"+DateSupport.getYear(str));
		} else if (field.equals("wantedtime")) {
			int str=node.getIntValue("wantedtime");
			return(DateSupport.getTimeSec(str)+" op "+DateSupport.getMonthDay(str)+"/"+DateSupport.getMonth(str)+"/"+DateSupport.getYear(str));
		} else if (field.equals("expiretime")) {
			int str=node.getIntValue("expiretime");
			return(DateSupport.getTimeSec(str)+" op "+DateSupport.getMonthDay(str)+"/"+DateSupport.getMonth(str)+"/"+DateSupport.getYear(str));
		}
		return(null);
	}

	private void getVwmTasks() {
		String vwm,task;
		// get out alter ego Vwms Builder to pass the new tasks 
		if (vwms==null) vwms=(Vwms)mmb.getMMObject("vwms");
		int checktime=lastchecked;
		lastchecked=(int)(DateSupport.currentTimeMillis()/1000);
		//Enumeration e=search("WHERE changedtime>"+checktime+" AND wantedcpu='"+getMachineName()+"' AND status=1");
		Enumeration e=search("WHERE changedtime>"+checktime+" AND wantedcpu='"+getMachineName()+"' AND "+mmb.getDatabase().getAllowedField("status")+"=1");

		while(e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			if (debug) System.out.println("VWMtasks -> Starting tasks "+node);
			vwm=node.getStringValue("vwm");
			task=node.getStringValue("task");
			if (vwms!=null) vwms.putTask(vwm,node);
		}
	}

}
