package org.mmbase.module.builders.vwms;

import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import nl.vpro.mmbase.module.builders.*;
import org.mmbase.module.builders.Properties;


/**
 * @author Daniel Ockeloen
 * @author Rico Jansen
 */

public class Merlin extends Vwm {
boolean first=true;

	public Merlin() {
		System.out.println("Yo Im Merlin the wizard");
	}

	public boolean performTask(MMObjectNode node) {
		return(false);
	}

	public boolean probeCall() {
		if (Vwms!=null) {
			System.out.println("Merlin probe call");
			if (first) {
				first=false;
			} else {
				checkAutomaticPrograms();
			}
		}
		return(true);
	}

	public boolean checkAutomaticPrograms() {
		Vector prgs=getAutomaticPrograms();
		Enumeration e=prgs.elements();
		while (e.hasMoreElements()) {
			MMObjectNode node=(MMObjectNode)e.nextElement();

			MMObjectNode pnode=node.getProperty("AUTODAYS");
			String dtype=pnode.getStringValue("value");

			int ftime=node.getIntValue("firstepisode");		
			int ctime=(int)(DateSupport.currentTimeMillis()/1000);
			Date d=new Date(((long)ftime)*1000);
			Date d2=new Date(((long)ctime)*1000);
			int itime=node.getIntValue("intervaltime");		
			System.out.println("Merlin -> First epi time="+ftime+" d="+d.toString());
			System.out.println("Merlin -> Intervaltime="+itime);
			System.out.println("Merlin -> current time="+ctime+" d="+d2.toString());

			// first episode
			int day=d.getDay();
			int hours=d.getHours();
			int min=d.getMinutes();

			// now time
			int day2=d2.getDay();
			int hours2=d2.getHours();
			int min2=d2.getMinutes();
			int sec2=d2.getSeconds();

			System.out.println("Merlin -> dtype = '"+dtype+"'");
			if (dtype.equals("WEEKLY")) {
				// werk terug naar begin van de week
				System.out.println("WDAY="+day2);
				ctime-=(day2*86400);
				ctime-=(hours2*3600);
				ctime-=(min2*60);
				ctime-=(sec2);

				// verhoog met offset in de week bepaald door first epi
				ctime+=(day*86400);
				ctime+=(hours*3600);
				ctime+=(min*60);
				int createahead=2;
				System.out.println("Merlin -> d="+day+" h="+hours+" min="+min);
				while (createahead>0) {
					createEpisodeEvent(ctime,node);
					ctime+=(7*86400);
					createahead--;
				}
			} else if (dtype.equals("WORKDAYS")) {
					ctime-=(hours2*3600);
					ctime-=(min2*60);
					ctime-=(sec2);

					ctime+=(hours*3600);
					ctime+=(min*60);

					System.out.println("Merlin -> WORKDAYS "+day2);
					int createahead=7;
					while (createahead>0) {
						if (day2!=0 && day2!=6) {
							createEpisodeEvent(ctime,node);
						}
						ctime+=(86400);
						day2++;
						if (day2==7) day2=0;
						createahead--;
					}
			} else {
				System.out.println("Merlin -> Wrong dtype ? "+dtype);
			}
		}
		return(true);
	}
	
	public Vector getAutomaticPrograms() {
		System.out.println("Merlin -> Check Automatic Programs");
		// this checks for the propertie 'AUTOEPISODES'
		// on programs node to be able to create episodes
		// and adds mmevents nodes on these episodes
		// in the datafield of the propertie
		Vector results=new Vector();
		// use the properties to hunt down all the nodes with autorecord to yes
		Programs prgs=(Programs)Vwms.mmb.getMMObject("programs");		
		Properties props=(Properties)Vwms.mmb.getMMObject("properties");		
		if (props!=null) {
			Enumeration e=props.search("WHERE key='AUTOEPISODES' AND value='YES'");
			while (e.hasMoreElements()) {
				MMObjectNode propnode=(MMObjectNode)e.nextElement();
				MMObjectNode node2=(MMObjectNode)prgs.getNode(propnode.getIntValue("parent"));
				if (node2!=null) {
					results.addElement(node2);
				}
			}
		} else {
			System.out.println("ERROR vwm merlin has a null on Properties");
		}
		System.out.println("Merlin -> Automatic programs "+results);
		return(results);
	}

	private String t2d(int time) {
		return(DateSupport.getMonthDay(time)+" "+DateStrings.Dutch_months[DateSupport.getMonthInt(time)]+" "+DateSupport.getYear(time));
	}



	private void createEpisodeEvent(int ctime,MMObjectNode node) {
			Date d3=new Date(((long)ctime)*1000);
		
			// create the needed fields for the new record event
			String name="uitzend tijd : "+node.getStringValue("title")+" "+d3.getDate()+"/"+(d3.getMonth()+1);
			String newname="uitzend tijd : "+node.getStringValue("title")+" "+d3.getDate()+"/"+(d3.getMonth()+1)+"/"+(d3.getYear()+1900) ;
			boolean makenew=false;
			System.out.println("Merlin -> "+newname);

			// oke is there a event defined at this time allready
			// this matches the event name. A better version should
			// do mapping by tracing the known events to this program 
			// and do a near on its event time.
			MMEvents mmevents=(MMEvents)Vwms.mmb.getMMObject("mmevents");		
			if (mmevents!=null) {
				System.out.println("Merlin -> search on '"+name+"'");
				Enumeration f=mmevents.search("WHERE name='"+name+"'");	
				System.out.println("Merlin -> search on '"+newname+"'");
				Enumeration g=mmevents.search("WHERE name='"+newname+"'");	

				MMObjectNode node2;
				if (f.hasMoreElements()) {
					node2=(MMObjectNode)f.nextElement();	
					if (node2.getIntValue("number")>2000000) {
						System.out.println("Merlin-> : "+node2.toString());
						makenew=false;
					} else {
						if (g.hasMoreElements()) {
							node2=(MMObjectNode)g.nextElement();	
							System.out.println("Merlin-> : "+node2.toString());
							makenew=false;
						} else {
							makenew=true;
						}
					}
				} else {
					if (g.hasMoreElements()) {
						node2=(MMObjectNode)g.nextElement();	
						System.out.println("Merlin-> : "+node2.toString());
						makenew=false;
					} else {
						makenew=true;
					}
				}

				if (makenew) {
					System.out.println("Merlin-> : new event needed");
					try {
						MMObjectNode pnode=node.getProperty("EPISODELEN");
						int len=Integer.parseInt(pnode.getStringValue("value"));
						System.out.println("Merlin -> len="+len+"");

						pnode=node.getProperty("CUREPISODE");
						int curepi=Integer.parseInt(pnode.getStringValue("value"));
						System.out.println("Merlin -> curepi="+curepi+"");
		
						pnode=node.getProperty("AUTOCHANNEL");
						int channel=Integer.parseInt(pnode.getStringValue("value"));
						System.out.println("Merlin -> curchan="+channel+"");

						pnode=node.getProperty("AUTODAYS");
						String inttype=pnode.getStringValue("value");
						System.out.println("Merlin -> curday="+inttype+"");

						pnode=node.getProperty("CUREPISODE");
						pnode.setValue("value",""+(curepi+1));
						pnode.commit();
						// oke insert the new event
						MMObjectNode nnode=mmevents.getNewNode("system");
						nnode.setValue("name",newname);
						nnode.setValue("start",ctime);
						nnode.setValue("stop",ctime+len);
						nnode.setValue("playtime",len);
						int tid=nnode.insert("system");
		
						// insert the new episode	
						Episodes episodes=(Episodes)Vwms.mmb.getMMObject("episodes");		
						// Why the time in the title ?
						nnode=episodes.getNewNode("system");
						nnode.setValue("title",node.getStringValue("title"));
//						nnode.setValue("title",node.getStringValue("title")+" "+t2d(ctime));
						nnode.setValue("episodenr",curepi);
						System.out.println("MERLIN MERLIN MERLIN-> "+nnode);
						int eid=nnode.insert("system");

						// link episode to mmevent
						BroadcastRel bcastrel=(BroadcastRel)Vwms.mmb.getMMObject("bcastrel");		
						nnode=bcastrel.getNewNode("system");
						nnode.setValue("snumber",eid);
						nnode.setValue("dnumber",tid);
						nnode.setValue("channel",channel);
						nnode.setValue("rerun",0);
						int rid=nnode.insert("system");


						// link episode to program
						InsRel insrel=(InsRel)Vwms.mmb.getMMObject("insrel");		
						nnode=insrel.getNewNode("system");
						nnode.setValue("snumber",node.getIntValue("number"));
						nnode.setValue("dnumber",eid);
						nnode.setValue("rnumber",13);
						int pid=nnode.insert("system");

					} catch(Exception ex) {
						ex.printStackTrace();
					}
				}
			} else {
				System.out.println("Merlin -> can't access mmevents");
			}
	}
}
