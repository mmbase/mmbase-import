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
public class LocalInstallTask extends ApplicationTask {

    	private static Logger log = Logging.getLoggerInstance(LocalInstallTask.class.getName());

	public LocalInstallTask(int taskid,ApplicationTaskManager atm, MMBase mmb) {
		super(taskid,atm,mmb);
	}


	/**
	 * Main work loop
	 */
	public void doWork() {
		setState("ready","install task, waiting for init vars");
		String ns=waitForNewState();
		while (!ns.equals("start")) {
			ns=waitForNewState();
		}

		// state is 'start' so read the needed vars
		String source=getField("SOURCE");
		String name=getField("NAME");
		String type=getField("TYPE");
		if (source!=null && name!=null && type!=null) {
			if (type.equals("FULL")) {
				doFullInstall(source,name);
			} else if (type.equals("PARTLY")) {
				doPartlyInstall(source,name);
			}
		} else {
			setState("abort","missing init vars");
		}
			
		setState("finished","install task done");
	}

	private boolean doPartlyInstall(String source,String name) {
		setState("running","performing a partly install");
        	Application application = mmb.getApplicationManager().getApplication(name);
		// check all the components we need to install
		// install the wanted display parts 
		Vector wanted=getListValues("display");
		if (wanted!=null) {
			// so we have 1 or more display parts, get the component
			// then so we can install them
			Component component = application.getComponent("display");
			Enumeration enum=wanted.elements();
			while (enum.hasMoreElements()) {
				String elementname=(String)enum.nextElement();
				DisplayElement element = (DisplayElement)component.getElement(elementname);
				if (element!=null) {
					try {
                				element.install();
						taskLog("installed display element : "+elementname);
					} catch(Exception e) {
						return(false);
					}
				} else {
				
					taskLog("can't install display element : "+elementname+"its missing");
				}
			}
		}
		return(true);
	}


	private boolean doFullInstall(String source,String name) {
		setState("running","performing a full install");
		log.info("Doing a install of application : "+name);
        	Application application = mmb.getApplicationManager().getApplication(name);
		try {
                	application.install();
		} catch(Exception e) {
			return(false);
		}
		return(true);
	}

}
