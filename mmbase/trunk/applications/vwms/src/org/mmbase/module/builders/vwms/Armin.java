package org.mmbase.module.builders.vwms;

import java.util.*;
import java.io.*;

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

public class Armin extends Vwm {

	public static Hashtable channel2machine;
	public static int interval=3600;
	public static int nrOfSlices=5;
	public static Hashtable basepaths;
	public static Hashtable outputpaths;
	private boolean firstprobe=true;

	static {
		String k,v;
		channel2machine=new Hashtable();
		channel2machine.put("2_1","station");
		channel2machine.put("2_3","station");
		channel2machine.put("2_4","noise");
		channel2machine.put("2_5","noise");
		channel2machine.put("3_6","station");
		channel2machine.put("2_6","station");
		basepaths=new Hashtable();
		basepaths.put("station_1","/pools/radio1/");
//		basepaths.put("station_x","/pools/3voor12/");
		basepaths.put("station_3","/pools/radio3/");
		basepaths.put("station_6","/pools/radio3/");
		basepaths.put("noise_4","/pools/radio4/");
		basepaths.put("noise_5","/pools/radio5/");
		outputpaths=new Hashtable();
		outputpaths.put("noise","/audio/wav/");
		outputpaths.put("station","/audio/wav/");
	}

	public Armin() {
		System.out.println("Yo Im Armin");
	}

	public boolean performTask(MMObjectNode node) {
		boolean rtn=false;
		String task;

		claim(node);
		task=node.getStringValue("task");
		if (task.equals("cutandlink")) {
			try {
				performCutAndLink(node);
				rtn=true;
				int tim=(int)(DateSupport.currentTimeMillis()/1000);
				Vwms.sendMail(name,"rico@vpro.nl",task+" on "+node.getStringValue("wantedcpu")+":"+DateSupport.date2string(tim),"Node : "+node.getIntValue("number"));
			} catch (Exception e) {
				System.out.println("Armin cutandlink exception"+e);
				e.printStackTrace();
				rtn=false;
			}
		} else if (task.equals("generaterecordtasks")) {
			try {
				generateRecordTasks();
				rtn=true;
			} catch (Exception e) {
				System.out.println("Armin generatedrecordtasks exception"+e);
				e.printStackTrace();
				rtn=false;
			}
		} else if (task.equals("zappool")) {
			zapThePools(node);
			rtn=true;
		} else if (task.equals("checkencode")) {
			rtn=checkEncode(node);
		}

		if (rtn) {
			performed(node);
		} else {
			if (!task.equals("checkencode")) {
				Vwms.sendMail(name,"rico@vpro.nl",task+" failed","");
				failed(node);
			} else {
				rollback(node);
			}
		}
		return(rtn);
	}

	public boolean probeCall() {
		if (Vwms!=null) {
			if (firstprobe) {
				firstprobe=false;
			} else {
				generateCheckTask();
//				generateZapTask();
			}
		}
		return(true);
	}

	void generateCheckTask() {
		Vwmtasks vwmtask=(Vwmtasks)Vwms.mmb.getMMObject("vwmtasks");		
		String machine="koelkast";
		MMObjectNode tnode;
		int wantedtime;
		Enumeration e=vwmtask.searchWithWhere("task='generaterecordtasks' AND vwm='"+getName()+"' AND status=1 AND wantedcpu='"+machine+"'");
		if (!e.hasMoreElements()) {
			tnode=vwmtask.getNewNode("VWM Armin");
			tnode.setValue("wantedcpu",machine);
			tnode.setValue("task","generaterecordtasks");
			wantedtime=(int)(((DateSupport.currentTimeMillis())/1000)+6*(60*60));
			tnode.setValue("wantedtime",wantedtime); 
			tnode.setValue("expiretime",wantedtime+50*60);
			tnode.setValue("vwm",getName());
			tnode.setValue("status",1);
			//tnode.commit();
			int tnum=vwmtask.insert("VWM Armin",tnode);
		}
	}

	void generateZapTask() {
		Vwmtasks vwmtask=(Vwmtasks)Vwms.mmb.getMMObject("vwmtasks");		
		MediaOutputs mediao=(MediaOutputs)Vwms.mmb.getMMObject("mediaout");
		MMObjectNode tnode,node;
		int wantedtime;
		int curtime;
		String machine="";

try {
		Enumeration mo=mediao.search("WHERE outputtype="+MediaOutputs.AWA);
		while(mo.hasMoreElements()) {
			node=(MMObjectNode)mo.nextElement();
			System.out.println("Armin mediaoutput "+node);
			machine=node.getStringValue("machine");
		}
} catch (Exception e) {
	System.out.println("Armin got exception "+e);
	e.printStackTrace();
	System.out.println("Armin continueing...");
}


		for (Enumeration f=channel2machine.elements();f.hasMoreElements();) {
			machine=(String)f.nextElement();
			Enumeration e=vwmtask.searchWithWhere("task='zappool' AND vwm='"+getName()+"' AND wantedcpu='"+machine+"' AND status=1");
			if (!e.hasMoreElements()) {
				tnode=vwmtask.getNewNode("VWM Armin");
				tnode.setValue("wantedcpu",machine);
				tnode.setValue("task","zappool");
				curtime=(int)(DateSupport.currentTimeMillis()/1000);
				wantedtime=(int)(DateSupport.currentTimeMillis()/1000);
				wantedtime=((wantedtime/3600)+3)*3600-((int)(DateSupport.getMilliOffset()/1000));
				wantedtime=wantedtime-10*60;
				if (curtime>wantedtime) wantedtime+=60*60;
				tnode.setValue("wantedtime",wantedtime); 
				tnode.setValue("expiretime",wantedtime+50*60);
				tnode.setValue("vwm",getName());
				tnode.setValue("status",1);
				//tnode.commit();
				int tnum=vwmtask.insert("VWM Armin",tnode);
			}
		}
	}

	void generateRecordTasks() {
		int ctime=(int)((DateSupport.currentTimeMillis()/1000));

		Episodes epi=(Episodes)Vwms.mmb.getMMObject("episodes");		
		MultiRelations multirel=(MultiRelations)Vwms.mmb.getMMObject("multirelations");
		BroadcastRel brel=(BroadcastRel)Vwms.mmb.getMMObject("bcastrel");
		MMEvents evts=(MMEvents)Vwms.mmb.getMMObject("mmevents");

		MMObjectNode pnode,enode,eventnode,tasknode=null,bcastnode=null,tnode,mnode;
		Vector vec,fields,tables,ordervec,dirvec;
		int startevent,pnumber;
		boolean attached;
		Enumeration tk,gg;

		System.out.println("Armin -> Checking for record tasks");
		Vector progs=getAutoRecordPrograms();

		tables=new Vector();
		tables.addElement("programs");
		tables.addElement("episodes");
		tables.addElement("bcastrel");
		tables.addElement("mmevents");
		fields=new Vector();
		fields.addElement("episodes.number");
		fields.addElement("bcastrel.number");
		fields.addElement("mmevents.number");
		fields.addElement("mmevents.start");
		ordervec=new Vector();
		ordervec.addElement("mmevents.start");
		dirvec=new Vector();
		dirvec.addElement("UP");

		// oke loop at the programs that want to be recorded and  
		// find episodes in the future without any vwmtasks attached
		Enumeration t=progs.elements();
		while (t.hasMoreElements()) {
			// select next program
			pnode=(MMObjectNode)t.nextElement();
			pnumber=pnode.getIntValue("number");
			vec=multirel.searchMultiLevelVector(pnumber,fields,"YES",tables,"mmevents.start=G"+ctime+"+mmevents.start=S"+(ctime+7*24*3600),ordervec,dirvec);
			
			for (Enumeration xx=vec.elements();xx.hasMoreElements();) {
				mnode=(MMObjectNode)xx.nextElement();
				enode=epi.getNode(mnode.getIntValue("episodes.number"));
				Vwms.mmb.getInsRel().deleteRelationCache(enode.getIntValue("number"));
				tk=Vwms.mmb.getInsRel().getRelated(enode.getIntValue("number"),142123);
				System.out.println("Armin -> Episode : "+enode);
				attached=false;
				while (tk.hasMoreElements()) {
					tasknode=(MMObjectNode)tk.nextElement();
					if (tasknode.getStringValue("task").equals("cutandlink")) {
							attached=true;
							break;
					}
				}
				if (!attached) {
					System.out.println("Armin -> No task attached lets attach one");
					eventnode=evts.getNode(mnode.getIntValue("mmevents.number"));
					bcastnode=brel.getNode(mnode.getIntValue("bcastrel.number"));
					if (bcastnode.getIntValue("rerun")==0) {
						attachTask2Episode(pnode,enode,eventnode,bcastnode);
					}
				} else {
					System.out.println("Armin -> Already has a task attached :"+tasknode.getIntValue("number")+" : "+tasknode.getStringValue("task"));

				}
			}
		}
	}

	Vector getAutoRecordPrograms() {
		Vector results=new Vector();
		// use the properties to hunt down all the nodes with autorecord to yes
		Programs prgs=(Programs)Vwms.mmb.getMMObject("programs");		
		Properties props=(Properties)Vwms.mmb.getMMObject("properties");		
		if (props!=null) {
			Enumeration e=props.search("WHERE key='AUTORECORD' AND value='YES'");
			System.out.println("Armin -> PROPCHECK");
			while (e.hasMoreElements()) {
				MMObjectNode propnode=(MMObjectNode)e.nextElement();
				MMObjectNode node2=(MMObjectNode)prgs.getNode(propnode.getIntValue("parent"));
				if (node2!=null) {
					results.addElement(node2);
				}
			}
		} else {
			System.out.println("ERROR vwm armin has a null on Properties");
		}
		return(results);
	}

	boolean attachTask2Episode(MMObjectNode pnode,MMObjectNode enode, MMObjectNode evnode,MMObjectNode bnode) {
/*
		System.out.println("Pnode="+pnode);
		System.out.println("Enode="+enode);
		System.out.println("Tnode="+evnode);
		System.out.println("Bnode="+bnode);
*/


		// oke lets figure out what 'hours we need for now (will change)
		String machine;
		MMObjectNode mnode,tasknode;
		Enumeration tk;
		boolean attached;
		Vector mnodes;
		MediaOutputs mediaout=(MediaOutputs)Vwms.mmb.getMMObject("mediaout");
		int channel,medium,start,stop,len;
		int pnr,enr,evnr,bnr;
		start=evnode.getIntValue("start");
		stop=evnode.getIntValue("stop");
		len=evnode.getIntValue("playtime");
		channel=bnode.getIntValue("channel");
		medium=pnode.getIntValue("medium");
		int wantedtime;

		/*
			BLAARGH Check
			Dubbel check of er stiekem toch wel een cutandlink aan hangt
		*/
		Vwms.mmb.getInsRel().deleteRelationCache(enode.getIntValue("number"));
		tk=Vwms.mmb.getInsRel().getRelated(enode.getIntValue("number"),142123);
		attached=false;
		while (tk.hasMoreElements()) {
			tasknode=(MMObjectNode)tk.nextElement();
			if (tasknode.getStringValue("task").equals("cutandlink")) {
					attached=true;
					break;
			}
		}
		if (attached) {
			System.out.println("Armin -> 1==2 error on attach2episode");
			return(false);
		}

try {
		mnodes=mediaout.getMediaOutputs(MediaOutputs.AWA,medium,channel);
		for (Enumeration mo=mnodes.elements();mo.hasMoreElements();) {
			mnode=(MMObjectNode)mo.nextElement();
			machine=mnode.getStringValue("machine");
			System.out.println("Armin : MediaOutput : "+mnode);
		}
} catch (Exception e) {
	System.out.println("Armin got exception "+e);
	e.printStackTrace();
	System.out.println("Armin continueing...");
}

			machine=(String)channel2machine.get(medium+"_"+channel);
			if (machine!=null) {
				MMObjectNode tnode;
				Vwmtasks vwmtask=(Vwmtasks)Vwms.mmb.getMMObject("vwmtasks");		
				ExtendedProperties p=new ExtendedProperties();
				tnode=vwmtask.getNewNode("VWM Armin");
				tnode.setValue("wantedcpu",machine);
				tnode.setValue("task","cutandlink");
	//			wantedtime=(((stop/3600)+1)*3600)+5*60;
				// This needs to change RICO timetrouble
				wantedtime=((stop/3600)+3)*3600-((int)(DateSupport.getMilliOffset()/1000))+5*60;
				System.out.println("Wanted Stop Time "+wantedtime+","+DateSupport.date2string(wantedtime)+" stop "+stop+","+DateSupport.date2string(stop));
				tnode.setValue("wantedtime",wantedtime); // 5 minutes after the stop hour
				tnode.setValue("expiretime",wantedtime+50*60);
				pnr=pnode.getIntValue("number");
				enr=enode.getIntValue("number");
				evnr=evnode.getIntValue("number");
				bnr=bnode.getIntValue("number");
				p.put("program",""+pnr);
				p.put("episode",""+enr);
				p.put("event",""+evnr);
				p.put("bcastrel",""+bnr);
				p.put("aflevering",evnode.getStringValue("name"));
				tnode.setValue("data",p.save());
				tnode.setValue("status",1);
				tnode.setValue("vwm",getName());
				tnode.setValue("id",pnr);
				//tnode.commit();
				System.out.println("VWM Armin: WANT A INSERT EPI="+enode.getIntValue("number"));
				int tnum=vwmtask.insert("VWM Armin",tnode);
				if (tnum!=-1) {
					int rnum=Vwms.mmb.getInsRel().insert("VWM Armin",enode.getIntValue("number"),tnum,14);
					if (rnum==-1) System.out.println("Armin : error -1 insrel (episodes to vwmtasks)");
				} else {
					System.out.println("Armin : error -1 vwmtask");
				}
			} else {
				System.out.println("Armin : couldn't find machine for channel "+medium+":"+channel);
			}
		

		return(false);
	}

	public void performCutAndLink(MMObjectNode node) {
		String data,machine;
		Hashtable props;
		int programnr,episodenr,eventnr,bcastnr;
		MMObjectNode prnode,epnode,evnode,bnode,anode,tnode;
		AudioParts aupa=(AudioParts)Vwms.mmb.getMMObject("audioparts");		
		RawAudios bul=(RawAudios)Vwms.mmb.getMMObject("rawaudios");
		Vwmtasks vwmtask=(Vwmtasks)Vwms.mmb.getMMObject("vwmtasks");		
		Programs prgs;
		Episodes epis;
		MMEvents evts;
		BroadcastRel brel;
		int start,stop,len2,len,bhr,shr,numhour,tracknr=0,rnum,min;
		AudioObject ao;
		String pool,filename,out,naam,naam2;
		Date d1,d2;
		int wantedtime;

		System.out.println("PerformCutAndLink "+node);
		data=node.getStringValue("data");
		props=parseProperties(data);
		machine=node.getStringValue("claimedcpu");
		out=(String)outputpaths.get(machine);

		programnr=Integer.parseInt((String)props.get("program"));
		episodenr=Integer.parseInt((String)props.get("episode"));
		eventnr=Integer.parseInt((String)props.get("event"));
		bcastnr=Integer.parseInt((String)props.get("bcastrel"));
	
		prgs=(Programs)Vwms.mmb.getMMObject("programs");		
		epis=(Episodes)Vwms.mmb.getMMObject("episodes");		
		evts=(MMEvents)Vwms.mmb.getMMObject("mmevents");		
		brel=(BroadcastRel)Vwms.mmb.getMMObject("bcastrel");		

		prnode=prgs.getNode(programnr);
		epnode=epis.getNode(episodenr);
		evnode=evts.getNode(eventnr);
		bnode=brel.getNode(bcastnr);

		start=evnode.getIntValue("start");
		stop=evnode.getIntValue("stop");
		len=evnode.getIntValue("playtime");

		// Rico timetrouble (this is okay because we only use the diff)
		d1=new Date((long)start*1000);
		d2=new Date((long)stop*1000);
		bhr=d1.getHours();
		shr=d2.getHours();
		min=d2.getMinutes();
		System.out.println("Armin hour "+bhr+" - "+shr);
		if (bhr>shr) {
			shr+=24;
		}
		naam=evnode.getStringValue("name");
		naam=naam.replace('/','_');
		naam=naam.replace(' ','_');

		numhour=shr-bhr;
		if (min==0) numhour--; // remove 1 hour when we stop within te first minute
		System.out.println("Armin ("+naam+") number of hours "+numhour);
		for (int i=0;i<=numhour;i++) {
			pool=getPoolName(node,bnode,evnode,3600*i);
			if (pool!=null) {
				ao=AudioObject.get(pool);
				System.out.println("Armin "+ao);
				if (numhour>0) {
					naam2=prnode.getStringValue("title")+" "+t2d(start)+" uur "+(i+1);
				} else {
					naam2=prnode.getStringValue("title")+" "+t2d(start);
				}
				anode=aupa.getNewNode("VWM Armin");
				anode.setValue("title",naam2);
				anode.setValue("storage",2);
				anode.setValue("source",8);
				tracknr=aupa.insert("VWM Armin",anode);
				// try again if it fails
				if (tracknr==-1) {
					tracknr=aupa.insert("VWM Armin",anode);
				}
				System.out.println("Armin cutting "+naam2+" : "+tracknr);
				if (tracknr>0) {
					filename=out+tracknr+".wav";
//					filename=out+naam+"_"+i+".wav";
					System.out.println("Armin cutting to "+filename);
					// we know that cut will 'Do The Right Thing' when presented
					// with out of range input 
					// RICO timetrouble AudioObjects sense of time must be
					// the same as hours, this is 'DateSupport' time
					ao=ao.cut(filename,start,stop,len);
					aupa.addRawAudio(bul,tracknr,3,3,441000,2);   
					aupa.wavAvailable(""+tracknr);
					// Link track HERE (episodenr <-> tracknr)
					rnum=Vwms.mmb.getInsRel().insert("VWM Armin",episodenr,tracknr,14);
					if (rnum==-1) System.out.println("Armin : error -1 insrel (episodes to audioparts)");
					// Set length 
					len2=(int)(ao.getLength()+0.5);
					System.out.println("Armin -> Audiopart length "+len2);
					anode.setValue("playtime",len2);
					anode.commit();
					System.out.println("Armin -> Audiopart node "+node);
					writeRecord(programnr,machine,len2);
			
					// Generate checkencode task here
					tnode=vwmtask.getNewNode("VWM Armin");
					tnode.setValue("wantedcpu",machine);
					tnode.setValue("task","checkencode");
					// Rico timetrouble, verified, ok
					wantedtime=(int)(((DateSupport.currentTimeMillis())/1000)+60*19);
					tnode.setValue("wantedtime",wantedtime); 
					tnode.setValue("expiretime",wantedtime+20*60);
					tnode.setValue("vwm",getName());
					tnode.setValue("status",1);
					tnode.setValue("id",tracknr);
					int tnum=vwmtask.insert("VWM Armin",tnode);
				}
			}
		}
		// Log the cutandlink
//		writeRecord(programnr,machine,len);
	}

	// RICO timetrouble shuld be okay as this doesn't use hours
	private String t2d(int time) {
		return(DateSupport.getMonthDay(time)+" "+DateStrings.Dutch_months[DateSupport.getMonthInt(time)]+" "+DateSupport.getYear(time));
	}

	private void zapThePools(MMObjectNode tnode) {
		String machinestr,am;
		int curtime=(int)(System.currentTimeMillis()/1000)+interval;
		double	currentSliceOffset = ((double)curtime/interval);
		int 	curSliceNr 	= (int)(currentSliceOffset % nrOfSlices);
		machinestr=tnode.getStringValue("claimedcpu")+"_";
		for (Enumeration k=basepaths.keys();k.hasMoreElements();) {
			am=(String)k.nextElement();
			if (am.startsWith(machinestr)) {
				// Zap the pool
				zapAPool(tnode,(String)basepaths.get(am)+curSliceNr);
			}
		}
	}

	private void zapAPool(MMObjectNode tnode,String path) {
		String f=path+"/0.wav";
		File fil;
//		Vwms.sendMail(name,"rico@vpro.nl","About to zap "+f,"");
		System.out.println("Armin zapping "+f);
		fil=new File(f);
		fil.delete();
		try { Thread.sleep(1000); } catch (Exception e) {}
	}

	protected String getPoolName(MMObjectNode node,MMObjectNode bnode,MMObjectNode evnode,int offset) {
		String rtn="";
		String machine;
		String basepath;

		// RICO timetrouble, looks good as recorder does no compensation
		// and this kills the compensation we do (getMilliOffset does) .
		int curtime=1+offset+evnode.getIntValue("start")+(int)(DateSupport.getMilliOffset()/1000);
		double	currentSliceOffset = ((double)curtime/interval);
		int 	curSliceNr 	= (int)(currentSliceOffset % nrOfSlices);
		machine=node.getStringValue("claimedcpu");
		basepath=(String)basepaths.get(machine+"_"+bnode.getIntValue("channel"));
		rtn=basepath+curSliceNr;

		return(rtn);
	}

	protected boolean checkEncode(MMObjectNode node) {
		int tnr;
		RawAudios bul=(RawAudios)Vwms.mmb.getMMObject("rawaudios");
		MMObjectNode raw;
		boolean done=false;
		int wantedtime;
		String machine;
		int tim=(int)(DateSupport.currentTimeMillis()/1000);

		System.out.println("Armin checkEncode ("+DateSupport.date2string(tim)+")"+node);
		tnr=node.getIntValue("id");
		machine=node.getStringValue("claimedcpu");
		Enumeration e=bul.search("WHERE id="+tnr); // get noncached version
		if (e.hasMoreElements()) {
			done=true;
			while (e.hasMoreElements()) {
				raw=(MMObjectNode)e.nextElement();
				if (raw.getIntValue("status")!=3) done=false;
			}
			if (done) {
				// We are done zap wav.
				String path=(String)outputpaths.get(machine);
				String f=path+tnr+".wav";
				File fil;
				Vwms.sendMail(name,"rico@vpro.nl","About to zap "+f,"Time : "+DateSupport.date2string(tim)+" : "+f);
				System.out.println("Armin zapping "+f+" "+DateSupport.date2string(tim));
				fil=new File(f);
				fil.delete();
			} else {
				// Not done, reset our task
				wantedtime=(int)(((DateSupport.currentTimeMillis())/1000)+60*14);
				node.setValue("wantedtime",wantedtime); 
				node.setValue("expiretime",wantedtime+15*60);
				node.setValue("status",1);
				node.setValue("claimedcpu","");
				node.commit();
			}
		} else {
			System.out.println("Armin : No rawaudios found for id="+tnr);
			done=true; // zap our task
		}
		return(done);
	}

	private synchronized void writeRecord(int programnr,String machine,int len) {
		long dl=DateSupport.currentTimeMillis();
		Date dld=new Date(dl); // RICO timetrouble This is not the correct time;
		RandomAccessFile raf;
		String name;
		String data;

		try {
			name="/mm/wwwtech/cutandlink/"+machine+"."+DateSupport.weekInYear(dld);
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
