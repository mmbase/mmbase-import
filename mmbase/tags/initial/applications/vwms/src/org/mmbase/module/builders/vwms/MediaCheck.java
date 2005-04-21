/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.builders.Properties;

/**
 * @author Rico Jansen
 */

public class MediaCheck extends Vwm {
boolean firstprobe=true;

	public MediaCheck() {
		System.out.println("VWM MediaCheck loaded");
	}

	public boolean performTask(MMObjectNode node) {
		boolean rtn=false;
		String task;

		claim(node);
		task=node.getStringValue("task");
		if (task.equals("mediacheck")) {
			try {
				MediaCheck(node);
				rtn=true;
			} catch (Exception e) {
				System.out.println("MediaCheck exception"+e);
				e.printStackTrace();
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
			wantedtime=(int)(((DateSupport.currentTimeMillis())/1000)+36*(60*60));
			tnode.setValue("wantedtime",wantedtime); 
			tnode.setValue("expiretime",wantedtime+50*60);
			tnode.setValue("vwm",getName());
			tnode.setValue("status",1);
			vwmtask.preCommit(tnode);
			int tnum=vwmtask.insert("VWM MediaCheck",tnode);
		}
	}

	private boolean MediaCheck(MMObjectNode node) {
		MediaInputs bul=(MediaInputs)Vwms.mmb.getMMObject("mediain");
		bul.maintainTable();
		return(true);
	}

}
