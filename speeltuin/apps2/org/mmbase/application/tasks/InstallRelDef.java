/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application.tasks;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.*;
import org.mmbase.application.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

import java.util.*;
import java.io.*;
import java.lang.*;

import org.w3c.dom.*;

/**
 * Basic application task, needs to be extended for real tasks.
 * this providesthe basic tools and interface for the Manager
 */
public class InstallRelDef extends ApplicationTask {

    	private static Logger log = Logging.getLoggerInstance(InstallRelDef.class.getName());

	public InstallRelDef(int taskid,ApplicationTaskManager atm, MMBase mmb) {
		super(taskid,atm,mmb);
	}


	/**
	 * Main work loop
	 */
	public void doWork() {
		setState("ready","reldef install task, waiting for init vars");
		String ns=waitForNewState();
		while (!ns.equals("run")) {
			ns=waitForNewState();
		}

		// state is 'run' so read the needed vars
		String a=getField("APPNAME");
		String c=getField("COMPONENT");
		String e=getField("ELEMENT");
		String i=getField("ID");

        	Application application = mmb.getApplicationManager().getApplication(a);
		Component component = application.getComponent(c);
		CloudLayoutElement element = (CloudLayoutElement)component.getElement(e);
		try{
                	element.installRelDefById(i);
			setState("done","reldef install done");
		} catch (Exception excep ) {
			setField("ERRORMSG",excep.getMessage());
			setState("error","reldef install exception");
		}
	}

}
