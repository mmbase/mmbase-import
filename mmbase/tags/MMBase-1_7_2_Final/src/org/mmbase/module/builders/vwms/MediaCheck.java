/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.util.Enumeration;

import org.mmbase.module.builders.*;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.util.logging.*;

/**
 * @author Rico Jansen
 */

public class MediaCheck extends Vwm {

    private static Logger log = Logging.getLoggerInstance(MediaCheck.class.getName()); 

    boolean firstprobe=true;

	public MediaCheck() {
		log.info("VWM MediaCheck loaded");
	}

	public boolean performTask(MMObjectNode node) {
		boolean rtn=false;
		String task;

		claim(node);
		task=node.getStringValue("task");
		if (task.equals("mediacheck")) {
			try {
				doMediaCheck(node);
				rtn=true;
			} catch (Exception e) {
				log.error("exception " + e);
				log.error(Logging.stackTrace(e));
				rtn=false;
			}
		}

		if (rtn) {
			performed(node);
		} else {
			failed(node);
			Vwms.sendMail(name,"rico@vpro.nl",task+" failed","");
		}
		return(rtn);
	}

	public boolean probeCall() {
		if (Vwms!=null) {
			if (firstprobe) {
				firstprobe=false;
			} else {
				generateCheckTask();
			}
		}
		return(true);
	}

	void generateCheckTask() {
		Vwmtasks vwmtask=(Vwmtasks)Vwms.mmb.getMMObject("vwmtasks");		
		String machine="twohigh";
		MMObjectNode tnode;
		int wantedtime;
		Enumeration e=vwmtask.searchWithWhere("task='mediacheck' AND vwm='"+getName()+"' AND status=1 AND wantedcpu='"+machine+"'");
		if (!e.hasMoreElements()) {
			tnode=vwmtask.getNewNode("VWM MediaCheck");
			tnode.setValue("wantedcpu",machine);
			tnode.setValue("task","mediacheck");
			wantedtime=(int)(((System.currentTimeMillis())/1000)+36*(60*60));
			tnode.setValue("wantedtime",wantedtime); 
			tnode.setValue("expiretime",wantedtime+50*60);
			tnode.setValue("vwm",getName());
			tnode.setValue("status",1);
			vwmtask.preCommit(tnode);
			int tnum=vwmtask.insert("VWM MediaCheck",tnode);
		}
	}

	private boolean doMediaCheck(MMObjectNode node) {
		MediaInputs bul=(MediaInputs)Vwms.mmb.getMMObject("mediain");
		bul.maintainTable();
		return(true);
	}

}
