package org.mmbase.module.builders.vwms;

import java.util.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.builders.Properties;
import nl.vpro.mmbase.module.builders.*;

/**
 * @author Daniel Ockeloen
 * @author Rico Jansen
 */

public class AudioSmurf extends Vwm {
private boolean first=true;
private String machine="twohigh";

private Programs programs;
private Properties properties;
private DayMarkers daymarks;
private Vwmtasks vwmtasks;
private MultiRelations multirelations;
private PoolBuilder	pools;

	public AudioSmurf() {
		System.out.println("Audio Smurf ready for the job !!!");
	}


	public boolean probeCall() {
		if (Vwms!=null) {
			if (first) {
				first=false;
			} else {
				vwmtasks=(Vwmtasks)Vwms.mmb.getMMObject("vwmtasks");		
				programs=(Programs)Vwms.mmb.getMMObject("programs");		
				properties=(Properties)Vwms.mmb.getMMObject("properties");		
				daymarks=(DayMarkers)Vwms.mmb.getMMObject("daymarks");
				multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");		
				pools=(PoolBuilder)Vwms.mmb.getMMObject("pools");

				generateCheckTask();
				// checkRTUSPrograms();
			}
		}
		return(true);
	}

	public boolean performTask(MMObjectNode node) {
		boolean rtn=false;
		String task;

		claim(node);
		task=node.getStringValue("task");
		if (task.equals("checkRTUSPrograms")) {
			try {
				checkRTUSPrograms();
				rtn=true;
				int tim=(int)(DateSupport.currentTimeMillis()/1000);
				Vwms.sendMail(name,"rico@vpro.nl",task+" on "+node.getStringValue("wantedcpu")+":"+DateSupport.date2string(tim),"Node : "+node.getIntValue("number"));
			} catch (Exception e) {
				System.out.println("AudioSmurf -> exception"+e);
				e.printStackTrace();
				rtn=false;
			}
		} else {
			System.out.println("AudioSmurf -> Unknown task "+node);
		}

		if (rtn) {
			performed(node);
		} else {
			Vwms.sendMail(name,"rico@vpro.nl",task+" failed","");
			failed(node);
		}
		return(rtn);
	}

	void generateCheckTask() {
		MMObjectNode tnode;
		int wantedtime;

		Enumeration e=vwmtasks.searchWithWhere("task='checkRTUSPrograms' AND vwm='"+getName()+"' AND status=1 AND wantedcpu='"+machine+"'");
		if (!e.hasMoreElements()) {
			tnode=vwmtasks.getNewNode("AudioSmurf");
			tnode.setValue("wantedcpu",machine);
			tnode.setValue("task","checkRTUSPrograms");
			wantedtime=(int)(((DateSupport.currentTimeMillis())/1000)+6*(60*60));
			tnode.setValue("wantedtime",wantedtime); 
			tnode.setValue("expiretime",wantedtime+50*60);
			tnode.setValue("vwm",getName());
			tnode.setValue("status",1);
			vwmtasks.preCommit(tnode);
			int tnum=vwmtasks.insert("VWM Armin",tnode);
		}
	}

	/**
	 * Main code
	 *
	 */

	boolean checkRTUSPrograms() {

		Enumeration e=properties.search("WHERE key='RTUS'");
		System.out.println("AudioSmurf -> RTUS check");
		while (e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();
			if (node!=null) {
				MMObjectNode prgnode=(MMObjectNode)programs.getNode(node.getIntValue("parent"));
				if (prgnode!=null) {
					handleRTUSnode(node,prgnode);	
				} else {
					System.out.println("AudioSmurf -> parent of RTUS property not found");
				}
			}
		}
		return(true);
	}

	private boolean handleRTUSnode(MMObjectNode propnode,MMObjectNode prgnode) {
		// get the last 'valid' (with rawadio's all done) episode of this program
		Vector result=new Vector();
		MMObjectNode anode;
		Hashtable props;
		String cmd;

		System.out.println("AudioSmurf -> handling program with property "+propnode);


		// Perhaps should be made dependent on CMD in propertie
		int agenumber=daymarks.getDayCountAge(14); // 14 days old
		int pnumber=prgnode.getIntValue("number");
		Vector tables=new Vector();
		tables.addElement("programs");
		tables.addElement("episodes");
		tables.addElement("audioparts");
		Vector fields=new Vector();
		fields.addElement("programs.number");
		fields.addElement("episodes.number");
		Vector ordervec=new Vector();
		ordervec.addElement("episodes.number");
		Vector dirvec=new Vector();
		dirvec.addElement("DOWN");

		System.out.println("AudioSmurf -> searching episodes for "+prgnode.getStringValue("title"));
		Vector vec=multirelations.searchMultiLevelVector(pnumber,fields,"YES",tables,"audioparts.number=G"+agenumber,ordervec,dirvec);
		System.out.println("AudioSmurf -> found episodes "+vec.size());

		props=parseProperties(propnode.getStringValue("value"));
		cmd=Strip.DoubleQuote((String)props.get("CMD"),Strip.BOTH);
		System.out.println("AudioSmurf -> ("+prgnode.getIntValue("number")+") cmd "+cmd);
		if (cmd!=null && cmd.equals("LASTEPISODE")) {
			Enumeration e=vec.elements();
			if (e.hasMoreElements()) { // Only the newest one
				anode=(MMObjectNode)e.nextElement();
				System.out.println("AudioSmurf -> node "+anode);	
				handleRTUSepisode(propnode,prgnode,anode);
			}
		}
		return(true);
	}

	private boolean handleRTUSepisode(MMObjectNode propnode,MMObjectNode prognode,MMObjectNode epinode) {
		boolean rtn=true;
		String poolname;
		MMObjectNode pool,apool,oldepi,oldrel;
		int rnum,poolnumber=-1,programnumber,epinumber;
		Hashtable props;
		boolean linked;
		Vector v;

		props=parseProperties(propnode.getStringValue("value"));
		poolname=(String)props.get("POOLNAME");
		if (poolname!=null) {
			poolname=Strip.DoubleQuote(poolname,Strip.BOTH);
			Enumeration e=pools.search("WHERE name='"+poolname+"'");
			if (e.hasMoreElements()) {
				pool=(MMObjectNode)e.nextElement();
				System.out.println("AudioSmurf -> Pool by name "+pool);
				poolnumber=pool.getIntValue("number");
			} else {
				System.out.println("AudioSmurf -> Pool "+poolname+" not found");
				poolnumber=-1;
			}
		}
		if (poolnumber==-1) {
			poolname=(String)props.get("POOLNUMBER");
			try {
				poolnumber=Integer.parseInt(poolname);
			} catch (NumberFormatException e) {
				poolnumber=-1;
			}
			if (poolnumber>=0) {
				pool=pools.getNode(poolnumber);
				if (pool!=null) {
					poolname=pool.getStringValue("name");
					System.out.println("AudioSmurf -> Pool by number "+poolname);
				} else {
					System.out.println("AudioSmurf -> Pool "+poolnumber+" not found");
					poolnumber=-1;
				}
			} else {
				System.out.println("AudioSmurf -> Pool "+poolname+" not found");
			}
		}
		programnumber=prognode.getIntValue("number");

		if (poolnumber>=0) {
			// link episode to pool specified in property.
			// watch out for duplicates.
			// remove episodes of same program already in there ?
			// or expire them
			// use prepool with confirmation ?
	
			// Check if episode already linked
			linked=false;
			apool=null;
			epinumber=epinode.getIntValue("episodes.number");
			System.out.println("AudioSmurf -> Episode nr "+epinumber);
			Vwms.mmb.getInsRel().deleteRelationCache(epinumber);
			Enumeration tk=Vwms.mmb.getInsRel().getRelated(epinumber,10); // relation to pools
			while(tk.hasMoreElements()) {
				apool=(MMObjectNode)tk.nextElement();
				System.out.println("Pool found "+apool);
				if (apool.getIntValue("number")==poolnumber) {
					linked=true;
					break;
				}
			}
			if (!linked) {
				// Find old episodes
				v=getOldEpisodes(poolnumber,programnumber);
				for (Enumeration x=v.elements();x.hasMoreElements();) {
					oldepi=(MMObjectNode)x.nextElement();
					System.out.println("AudioSmurf -> Old epi "+oldepi.getIntValue("episodes.number")+" rel "+oldepi.getIntValue("insrel2.number"));
					// Zap link
					oldrel=Vwms.mmb.getInsRel().getNode(oldepi.getIntValue("insrel2.number"));
					System.out.println("AudioSmurf -> Old relation "+oldrel);
					Vwms.mmb.getInsRel().removeNode(oldrel);
				}
				// link episode to pool
				rnum=Vwms.mmb.getInsRel().insert("AudioSmurf",poolnumber,epinumber,14);
			} else {
				System.out.println("AudioSmurf -> episode already linked "+apool);
			}
		} else {
			System.out.println("AudioSmurf -> pool not found");
		}
		return(rtn);
	}

	private Vector getOldEpisodes(int poolnumber,int programnumber) {
		Vector tables=new Vector();
		tables.addElement("programs");
		tables.addElement("insrel1");
		tables.addElement("episodes");
		tables.addElement("insrel2");
		tables.addElement("pools");
		Vector fields=new Vector();
		fields.addElement("episodes.number");
		fields.addElement("insrel2.number");
		Vector ordervec=new Vector();
		ordervec.addElement("episodes.number");
		Vector dirvec=new Vector();
		dirvec.addElement("UP");

		Vector vec=multirelations.searchMultiLevelVector(programnumber,fields,"YES",tables,"pools.number=E"+poolnumber,ordervec,dirvec);
		return vec;
	}
}
