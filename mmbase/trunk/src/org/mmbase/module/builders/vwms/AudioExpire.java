package org.mmbase.module.builders.vwms;

import java.util.*;
import java.io.*;
import java.sql.*;
import java.util.Date;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.builders.Properties;
import nl.vpro.mmbase.module.builders.*;


/**
 * @author Rico Jansen
 */

public class AudioExpire extends Vwm {
boolean firstprobe=true;
String machine="station";
Programs programs;
Properties properties;
DayMarkers daymarkers;
MultiRelations multirelations;
AudioParts audioparts;
RawAudios rawaudios;
boolean loaded=false;

	public AudioExpire() {
		System.out.println("VWM AudioExpire loaded");
	}

	public boolean performTask(MMObjectNode node) {
		boolean rtn=false;
		String task;

		if (!loaded) {
			loadBuilders();
			loaded=true;
		}
		claim(node);
		task=node.getStringValue("task");
		if (task.equals("audioexpire")) {
			try {
				AudioExpire(node);
				rtn=true;
			} catch (Exception e) {
				System.out.println("AudioExpire exception"+e);
				e.printStackTrace();
				rtn=false;
			}
		}

		if (rtn) {
			performed(node);
			Vwms.sendMail(name,"rico@vpro.nl",task+" succes","");
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
				if (!loaded) {
					loadBuilders();
					loaded=true;
				}
				generateCheckTask();
			}
		}
		return(true);
	}

	private void loadBuilders() {

		programs=(Programs)Vwms.mmb.getMMObject("programs");		
		properties=(Properties)Vwms.mmb.getMMObject("properties");		
		daymarkers=(DayMarkers)Vwms.mmb.getMMObject("daymarks");		
		multirelations=(MultiRelations)Vwms.mmb.getMMObject("multirelations");		
		audioparts=(AudioParts)Vwms.mmb.getMMObject("audioparts");		
		rawaudios=(RawAudios)Vwms.mmb.getMMObject("rawaudios");		
		if (programs==null || properties==null || daymarkers==null || audioparts==null || rawaudios==null) {
			System.out.println("VWM AudioExpire can't get at builders");
		}
	}

	void generateCheckTask() {
		Vwmtasks vwmtask=(Vwmtasks)Vwms.mmb.getMMObject("vwmtasks");		
		MMObjectNode tnode;
		int wantedtime;
		Enumeration e=vwmtask.searchWithWhere("task='audioexpire' AND vwm='"+getName()+"' AND status=1 AND wantedcpu='"+machine+"'");
		if (!e.hasMoreElements()) {
			tnode=vwmtask.getNewNode("VWM AudioExpire");
			tnode.setValue("wantedcpu",machine);
			tnode.setValue("task","audioexpire");
			wantedtime=(int)(((DateSupport.currentTimeMillis())/1000)+12*(60*60));
			tnode.setValue("wantedtime",wantedtime); 
			tnode.setValue("expiretime",wantedtime+50*60);
			tnode.setValue("vwm",getName());
			tnode.setValue("status",1);
			int tnum=vwmtask.insert("VWM AudioExpire",tnode);
		}
	}

	/**
	 * Main audio expiration function
	 * Expiration on time (using daycount)
	 * Variants are :
	 * - plain time, full expire
	 * - no expire
	 * - plain time, occasional keep by software
	 * - plain time, occasional keep by human
	 * - expiring different qualities
	 */
	private boolean AudioExpire(MMObjectNode node) {
		Vector progs;
		String prop;
		Hashtable props;
		MMObjectNode propnode;
		MMObjectNode prognode;

		for (Enumeration e=getAudioExpirePrograms();e.hasMoreElements();) {
			propnode=(MMObjectNode)e.nextElement();
			prop=propnode.getStringValue("value");
			if (prop!=null) props=parseProperties(prop);
			else props=new Hashtable();
			prognode=(MMObjectNode)programs.getNode(propnode.getIntValue("parent"));
			if (prognode!=null) {
				ExpireProgram(prognode,props);
			}
		}
		return(true);
	}

	Enumeration getAudioExpirePrograms() {
		Enumeration results;
		// use the properties to hunt down all the nodes with autorecord to yes
		if (properties!=null) {
			results=properties.search("WHERE key='AUDIOEXPIRE'");
			if (results==null) {
				results=(new Vector()).elements();
			}
		} else {
			System.out.println("ERROR vwm AudioExpire has a null on Properties");
			results=(new Vector()).elements();
		}
		return(results);
	}

	private final static String KEEP_EVERY="EVERY";
	private final static String KEEP_NO="NO";
	private final static String KEEP_YES="YES";

	void ExpireProgram(MMObjectNode prog,Hashtable props) {
		String stime;
		String skeep;
		String severy;
		int days,every;

		System.out.println("AudioExpire -> Expiring for : "+prog.getStringValue("title"));
		stime=(String)props.get("TIME");
		skeep=(String)props.get("KEEP");
		severy=(String)props.get("EVERY");
		try {
			days=Integer.parseInt(Strip.Whitespace(stime,Strip.BOTH));
		} catch (NumberFormatException ee) {
			days=56;
			System.out.println("AudioExpire -> can't parse time "+stime);
		}
		if (severy!=null) {
			try {
				every=Integer.parseInt(Strip.Whitespace(severy,Strip.BOTH));
			} catch (NumberFormatException ee) {
				System.out.println("AudioExpire -> can't parse every "+severy);
				every=1;
			}
		} else {
			every=1;
		}

		if (skeep.equals(KEEP_EVERY)) {
			System.out.println("AudioExpire -> KEEP_EVERY");
			setEvery(prog,every);
			expireAlways(prog,days);
		} else if (skeep.equals(KEEP_YES)) {
			System.out.println("AudioExpire -> KEEP_YES");
			// no expire at all
		} else if (skeep.equals(KEEP_NO)) {
			System.out.println("AudioExpire -> KEEP_NO");
			expireAlways(prog,days);
		} else {
			System.out.println("AudioExpire -> DEF");
			expireAlways(prog,days);
		}
	}

	private void setEvery(MMObjectNode prog,int every) {
		// every is in episodes
		Vector epi;
		MMObjectNode node,propnode;
		int enumber;
		int epinumber;
		Enumeration results;
		
		epi=getCurrentEpisodes(prog);
		for (Enumeration e=epi.elements();e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();
			enumber=node.getIntValue("episodes.number");
			epinumber=node.getIntValue("episodes.episodenr");
			if (epinumber<0) epinumber=0;
//			System.out.println("AudioExpire -> Every "+every+" ("+prog.getStringValue("title")+") "+node.getIntValue("episodes.episodenr"));
			if (((epinumber-1)%every)==0 ) {
				results=properties.search("WHERE key='KEEPAUDIO' AND parent="+enumber);
				if (results==null || !results.hasMoreElements()) {
					// Attach property
					System.out.println("AudioExpire -> Every "+every+" ("+prog.getStringValue("title")+") "+node.getIntValue("episodes.episodenr"));
					System.out.println("AudioExpire -> attaching keep property");
					propnode=properties.getNewNode("VWM AudioExpire");
					propnode.setValue("parent",enumber);
					propnode.setValue("key","KEEPAUDIO");
					propnode.setValue("ptype","string");
					propnode.setValue("value","YES");
					int tnum=properties.insert("VWM AudioExpire",propnode);
				} else {
					System.out.println("AudioExpire -> Every "+every+" ("+prog.getStringValue("title")+") "+node.getIntValue("episodes.episodenr"));
					System.out.println("AudioExpire -> keep this one");
				}
			}
		}
	}

	private void expireAlways(MMObjectNode prog,int days) {
		Vector parts;
		int pnumber;
		int enumber;
		MMObjectNode node;
		Enumeration results;

		pnumber=prog.getIntValue("number");
		parts=findOldAudioParts(pnumber,days);
		for (Enumeration e=parts.elements();e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();
			System.out.println("AudioExpire -> Checking node ("+pnumber+")"+node.getIntValue("audioparts.number")+","+node.getIntValue("episodes.number"));
			enumber=node.getIntValue("episodes.number");
			results=properties.search("WHERE key='KEEPAUDIO' AND parent="+enumber);
			if (results==null || !results.hasMoreElements()) {
				// It's ours so zap the audiopart
				zapAudioPart(node);
			}
		}
	}

	private void zapAudioPart(MMObjectNode node) {
		int enumber,anumber,rnumber;
		MMObjectNode znode;

		enumber=node.getIntValue("episodes.number");
		anumber=node.getIntValue("audioparts.number");

		znode=audioparts.getNode(anumber);
		System.out.println("AudioExpire -> Zzzzap ("+enumber+","+anumber+") "+znode.getStringValue("title"));
		if (znode!=null) {
			writeRecord(enumber,machine,znode.getIntValue("playtime"));
			// remove relations and audiopart
			audioparts.removeRelations(znode);
			audioparts.removeNode(znode);
			// remove rawaudios files and directorys
			rawaudios.removeAudio(anumber);
		} else {
			System.out.println("AudioExpire -> Can't find audiopart "+anumber);
		}
		
	}

	private Vector findOldAudioParts(int pnumber,int days) {
		int onumber;
		Vector tables,fields,ordervec,dirvec;
		Vector vec;

		onumber=daymarkers.getDayCountAge(days);

		tables=new Vector();
		tables.addElement("programs");
		tables.addElement("episodes");
		tables.addElement("audioparts");
		fields=new Vector();
		fields.addElement("episodes.number");
		fields.addElement("audioparts.number");
		ordervec=new Vector();
		ordervec.addElement("episodes.number");
		dirvec=new Vector();
		dirvec.addElement("UP");

		vec=multirelations.searchMultiLevelVector(pnumber,fields,"NO",tables,"audioparts.number=S"+onumber,ordervec,dirvec);
		return(vec);
	}

	private Vector getCurrentEpisodes(MMObjectNode prog) {
		Vector tables,fields,ordervec,dirvec;
		Vector vec;
		int pnumber=prog.getIntValue("number");
		int lw,nw;

		lw=(int)(DateSupport.currentTimeMillis()/1000)-(28*24*3600); // 4 weeks backward
		nw=lw+(14*24*3600); // 2 weeks forward

		tables=new Vector();
		tables.addElement("programs");
		tables.addElement("episodes");
		tables.addElement("mmevents");
		fields=new Vector();
		fields.addElement("episodes.number");
		fields.addElement("episodes.episodenr");
		ordervec=new Vector();
		ordervec.addElement("episodes.number");
		dirvec=new Vector();
		dirvec.addElement("UP");

		// Now do all old ones as well
		vec=multirelations.searchMultiLevelVector(pnumber,fields,"NO",tables,"mmevents.start=S"+nw,ordervec,dirvec);
//		vec=multirelations.searchMultiLevelVector(pnumber,fields,"NO",tables,"mmevents.start=G"+lw+"+mmevents.start=S"+nw,ordervec,dirvec);
		return(vec);
	}

	private synchronized void writeRecord(int programnr,String machine,int len) {
		long dl=DateSupport.currentTimeMillis();
		Date dld=new Date(dl);
		RandomAccessFile raf;
		String name;
		String data;

		try {
			name="/mm/wwwtech/audioexpire/"+machine+"."+DateSupport.weekInYear(dld);
			data=DateSupport.weekInYear(dld)+"\t"+DateSupport.date2string((int)(dl/1000))+"\t"+machine+"\t"+programnr+"\t"+len+"\n";
			System.out.println("Armin -> Writing "+data);
			raf=new RandomAccessFile(name,"rw");
			if (raf!=null) raf.seek(raf.length());
			raf.writeBytes(data);
			raf.close();
		} catch(Exception e) {
			System.out.println("Armin -> can't write cutandlink log");
			e.printStackTrace();
		}
	}
}
