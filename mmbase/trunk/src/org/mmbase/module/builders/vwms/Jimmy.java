package org.mmbase.module.builders.vwms;

import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;
import nl.vpro.mmbase.module.builders.*;
import org.mmbase.module.builders.Properties;

/**
 * @author David V Van Zeventer
 * @version 30 nov 1998
 */

public class Jimmy extends Vwm {

	private boolean firstprobe=true;

	public final static int ON_AIR  = 2;	//Broadcaststate values for setting the status field of a LStreamsnode
	public final static int OFF_AIR = 1;	//

	public final static int EP_DISTANCE = 5*60;		//Distance between two episodes is 5 minutes
	public final static String MAILTO = "rico@vpro.nl"; 	//Send all warnings and errors to.
	public final static String botname = "Jimmy";

	public Jimmy() {
		System.out.println(botname+": Yo Im Jimmy");
	}

	public boolean performTask(MMObjectNode tnode) {	//Common methodtask called when time is reached.
		boolean rtn=false;
		String task;
		System.out.println(botname+" VWM JIMMY : PERFORMING A NEXT PROGRAM");

		claim(tnode);					//Set tasknode state to "onderweg".
		task=tnode.getStringValue("task");
		if (task.equals("startbroadcast")) {	
			try {
				performStartBroadcast(tnode);	//Change broadcaststate to ON_AIR if necessary.
				rtn=true;
			} catch (Exception e) {
				System.out.println(botname+": Jimmy startbroadcast exception"+e);
				e.printStackTrace();
				rtn=false;
			}
		} else if (task.equals("stopbroadcast")) {
			try {
				performStopBroadcast(tnode);	//Change broadcaststate to OFF_AIR is necessary.
				rtn=true;
			} catch (Exception e) {
				System.out.println(botname+": Jimmy stopbroadcast exception"+e);
				e.printStackTrace();
				rtn=false;
			}
		} else if (task.equals("generatelivebroadcasttasks")) {	
			generateLiveBroadcastTasks();	//Check for new episodes and generate needed start&stop Broadcast tasks.
			rtn=true;
		}

		if (rtn){			//taskmethod returnvalue 
			performed(tnode);	//Set tasknode state to "gedaan";
			Vwms.sendMail(name,MAILTO,task,"");	//Mail the taskname that is performed to
		} else{
			//rollback(tnode);			//Set tasknode state back to "verzoek".
			Vwms.sendMail(name,MAILTO,task+" failed","");	
		}
		return(rtn);
	}

	public boolean probeCall() {	//probe for vwmtasks in DB every timeinterval.
		if (Vwms!=null) {
			if (firstprobe) {
				firstprobe=false;
			} else {
				generateCheckTask();	//Create and put a generatelivebroadcasttask in DB if necessary. 
			}
		}
		return(true);
	}

	void generateCheckTask() {
		System.out.println(botname+": Now performing generateCheckTask()");
		Vwmtasks vwmtask=(Vwmtasks)Vwms.mmb.getMMObject("vwmtasks");		
		String machine="noise";		//The machine that executes the generatebroadcasttasks..
		MMObjectNode tnode;
		int wantedtime;
		//Check if generatelivebroadcasttask already in DB, if not then create and put one in DB.
		Enumeration e=vwmtask.searchWithWhere("task='generatelivebroadcasttasks' AND vwm='"+getName()+"' AND status=1 AND wantedcpu='"+machine+"'");
		if (!e.hasMoreElements()) {
			tnode=vwmtask.getNewNode("VWM Jimmy");
			tnode.setValue("wantedcpu",machine);
			tnode.setValue("task","generatelivebroadcasttasks");
			wantedtime=(int)(((DateSupport.currentTimeMillis())/1000)+6*(60*60));
			tnode.setValue("wantedtime",wantedtime); 
			tnode.setValue("expiretime",wantedtime+50*60);
			tnode.setValue("vwm",getName());
			tnode.setValue("status",1);
			int tnum=vwmtask.insert("VWM Jimmy",tnode);
		}
	}

	/**
	 * generateLiveBroadcastTasks: Search for new episodes in DB and generate needed tasks for it using
	 *			       attachStartBroadcastTask2Episode, attachStartBroadcast2Episode.
	 */
	void generateLiveBroadcastTasks() {
		String methodname = "generateLiveBroadcastTasks";
		int ctime=(int)((DateSupport.currentTimeMillis()/1000));

		Episodes epi=(Episodes)Vwms.mmb.getMMObject("episodes");		
		MultiRelations multirel=(MultiRelations)Vwms.mmb.getMMObject("multirelations");
		BroadcastRel brel=(BroadcastRel)Vwms.mmb.getMMObject("bcastrel");
		MMEvents evts=(MMEvents)Vwms.mmb.getMMObject("mmevents");

		MMObjectNode pnode,enode,eventnode,tasknode=null,bcastnode=null,tnode,mnode, lsnode=null;
		Vector vec,fields,tables,ordervec,dirvec;
		int startevent,pnumber;
		boolean attached;
		Enumeration tk,gg;

		//Variables used concerning mediaoutputs.
		String ls_machine,ls_channel;	//Holds lstreamsnodes' machine and channel field.
		MMObjectNode mo_node=null;	//Holds a mediaoutputnode.
		Vector mo_vec=null;		//Holds a vector with mo_nodes.
		int type,medium,bcast_channel;
		MediaOutputs mediaoutputs = null;
		

		System.out.println(botname+": "+methodname+": Checking for livebroadcasttasks.");
		Vector progs=getLivePrograms();	//Search all programs which have an LIVEBROADCAST token attached.

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

		//Check if there are episodes in the future for these programs without any vwmtasks attached.
		Enumeration t=progs.elements();
		//For all programs with LIVEBROADCAST token attached do .
		while (t.hasMoreElements()) {
			// select next program
			pnode=(MMObjectNode)t.nextElement();
			pnumber=pnode.getIntValue("number");
			vec=multirel.searchMultiLevelVector(pnumber,fields,"NO",tables,"mmevents.start=G"+ctime+"+mmevents.start=S"+(ctime+7*24*3600),ordervec,dirvec);
			
			for (Enumeration xx=vec.elements();xx.hasMoreElements();) {
				mnode=(MMObjectNode)xx.nextElement();
				enode=epi.getNode(mnode.getIntValue("episodes.number"));
				
				Vwms.mmb.getInsRel().deleteRelationCache(enode.getIntValue("number"));
				tk=Vwms.mmb.getInsRel().getRelated(enode.getIntValue("number"),142123);
				attached=false;
				while (tk.hasMoreElements()) {
					tasknode=(MMObjectNode)tk.nextElement();
					if (tasknode.getStringValue("task").equals("startbroadcast")) {
							attached=true;
							break;
					}
				}
				if (!attached) {
					System.out.println(botname+": "+methodname+": No task attached lets attach one");
					eventnode=evts.getNode(mnode.getIntValue("mmevents.number"));
					bcastnode=brel.getNode(mnode.getIntValue("bcastrel.number"));
					if (bcastnode.getIntValue("rerun")==0) {

						type = mediaoutputs.LWA;	//Type is LiveWebAudio.
						//type = mediaoutputs.AWA;	//used for testing purposes.
						System.out.println(botname+": "+methodname+": Setting type to value for LWA: "+type);
						medium = pnode.getIntValue("medium");
						System.out.println(botname+": "+methodname+": Got medium from pnode, medium = "+medium);
						bcast_channel = bcastnode.getIntValue("channel");
						System.out.println(botname+": "+methodname+": Got channel from bcastnode, channel = "+ bcast_channel);

						mediaoutputs = (MediaOutputs)Vwms.mmb.getMMObject("mediaout");
						mo_vec = mediaoutputs.getMediaOutputs(type,medium,bcast_channel);
						System.out.println(botname+": "+methodname+": Got mediaoutputs vector using getMediaOutputs method mo_vec = "+mo_vec);

						Enumeration mo_enum = mo_vec.elements();
						while(mo_enum.hasMoreElements()){
							System.out.println("GenerateLiveBroadcastTasks: Getting next mo_node.");
							mo_node = (MMObjectNode)mo_enum.nextElement();	//Retrieve mo_node from Vector.
							LStreams lstreams=(LStreams)Vwms.mmb.getMMObject("lstreams");	//Get The LStreams object.
							if (lstreams!=null) {
								ls_machine = mo_node.getStringValue("machine");
								ls_channel = mo_node.getStringValue("channel");
								System.out.println(botname+": "+methodname+": mo_node found, machine field = "+ls_machine+"  channel field = "+ls_channel);

								Enumeration e=lstreams.search("WHERE channel='"+ls_channel+"'" +"AND machine='"+ls_machine+"'");
								while (e.hasMoreElements()) {
									lsnode =(MMObjectNode)e.nextElement();	//Retrieve lstreamsnode created by LStreams builder.
									System.out.println(botname+": "+methodname+": lstreamsnode nr to deliver acquired service determined: "+lsnode.getIntValue("number"));
								}
							} else {
								System.out.println(botname+": "+methodname+": ERROR vwm Jimmy has a null on LStreams");
							}	

							//Attach the tasks needed to this episode.
							attachStartBroadcastTask2Episode(pnode,enode,eventnode,bcastnode,lsnode,mo_node);
							attachStopBroadcastTask2Episode(pnode,enode,eventnode,bcastnode,lsnode,mo_node);
						}

						/* for testpurposes
						String lstreamsnodename="3voor12";
						LStreams lstreams=(LStreams)Vwms.mmb.getMMObject("lstreams");	//get The LStreams object.
						if (lstreams!=null) {
							//Retrieve lstreamsnode created by LStreams builder.
							Enumeration e=lstreams.search("WHERE channel='"+lstreamsnodename+"'");
							while (e.hasMoreElements()) {
								lsnode =(MMObjectNode)e.nextElement();
							}
							System.out.println(botname+": "+methodname+": Testing with testlstreamsnode: "+lsnode.getStringValue("channel"));
						} else {
							System.out.println(botname+": "+methodname+": ERROR vwm Jimmy has a null on LStreams");
						}	
						//Attach the tasks needed to this episode.
						attachStartBroadcastTask2Episode(pnode,enode,eventnode,bcastnode, lsnode, mo_node);
						attachStopBroadcastTask2Episode(pnode,enode,eventnode,bcastnode, lsnode, mo_node);
						*/
					}
				} else {
					System.out.println(botname+": "+methodname+": Already has a task attached :"+tasknode.getIntValue("number")+" : "+tasknode.getStringValue("task"));
				}
			}
		}//end while
		if (!t.hasMoreElements()) {
			System.out.println("Jimmy: generatelivebroadcasttasks: There are no more live programs left in vector");
		}
	}


	/** 
	 * getLivePrograms: 	Find all programs that have LIVEBROADCAST token attached.
	 */
	Vector getLivePrograms() {
		String methodname = "getLivePrograms";
		Vector results=new Vector();
		// use the properties to hunt down all the nodes with livebroadcast to yes
		Programs prgs=(Programs)Vwms.mmb.getMMObject("programs");		
		Properties props=(Properties)Vwms.mmb.getMMObject("properties");		
		if (props!=null) {
			Enumeration e=props.search("WHERE key='LIVEBROADCAST' AND value='YES'");
			System.out.println(botname+": "+methodname+": PROPCHECK");
			while (e.hasMoreElements()) {
				MMObjectNode propnode=(MMObjectNode)e.nextElement();	//get next propertynode
				MMObjectNode node2=(MMObjectNode)prgs.getNode(propnode.getIntValue("parent"));
				if (node2!=null) {	//add to the resultslist with programs
					results.addElement(node2);
				}
				System.out.println(botname+": "+methodname+": PROPNODE="+propnode);
			}
		} else {
			System.out.println(botname+": "+methodname+": ERROR vwm Jimmy has a null on Properties");
		}
		return(results);
	}

	/**
	 * attachStartBroadcastTask2Episode: This method actually creates & puts a StartBroadcasttask in the DB for
	 *				     given episode.  
	 */
	boolean attachStartBroadcastTask2Episode(MMObjectNode pnode,MMObjectNode enode,MMObjectNode evnode,MMObjectNode bnode, MMObjectNode lsnode, MMObjectNode mo_node) {

		String methodname = "attachStartBroadcastTask2Episode";
		boolean retval = false;
		//System.out.println(botname+": "+methodname+": pnode="+pnode);
		//System.out.println(botname+": "+methodname+": enode="+enode);
		//System.out.println(botname+": "+methodname+": tnode="+evnode);
		//System.out.println(botname+": "+methodname+": bnode="+bnode);
		//System.out.println(botname+": "+methodname+": lsnode="+lsnode);
		//System.out.println(botname+": "+methodname+": mo_node="+mo_node);

		// oke lets figure out what 'hours we need for now (will change)
		//String machine = "test1";		//Testmachine is test1 instead of noise.
		String machine = lsnode.getStringValue("machine");
		int channel,medium,start,stop,len;
		int pnr,enr,evnr,bnr, lsnr, mo_nr;
		start=evnode.getIntValue("start");	//event starttime
		stop=evnode.getIntValue("stop");	//event stoptime
		len=evnode.getIntValue("playtime");	
		channel=bnode.getIntValue("channel");	//channel used for broadcasting
		medium=pnode.getIntValue("medium");

		if (machine!=null) {
			MMObjectNode tnode;	//Select a new tasknode. 
			Vwmtasks vwmtask=(Vwmtasks)Vwms.mmb.getMMObject("vwmtasks");
			ExtendedProperties p=new ExtendedProperties();

			//Create a new VWMtasknode named "startbroadcast" that will be used
			//by Jimmy and fill various fields.
			tnode=vwmtask.getNewNode("VWM Jimmy");		
			tnode.setValue("wantedcpu",machine);
			tnode.setValue("task","startbroadcast");	
			tnode.setValue("wantedtime",start);
			tnode.setValue("expiretime",start+5*60);	//expiretime 5 minutes.
			
			//Get various objectnrs that are related with the newly created task
			//and put them in tasknode's datafield.
			pnr=pnode.getIntValue("number");
			enr=enode.getIntValue("number");
			evnr=evnode.getIntValue("number");
			bnr=bnode.getIntValue("number");
			lsnr=lsnode.getIntValue("number");	//get lstreamsnode nr.
			mo_nr = mo_node.getIntValue("number");	//get mediaoutput node nr.

			p.put("program",""+pnr);
			p.put("episode",""+enr);
			p.put("event",""+evnr);
			p.put("bcastrel",""+bnr);
			p.put("aflevering",evnode.getStringValue("name"));
			p.put("lstreamsnode",""+lsnr); 
			p.put("mediaoutput",""+mo_nr);

			tnode.setValue("data",p.save());
			tnode.setValue("status",1);
			tnode.setValue("vwm",getName());
			tnode.setValue("id",pnr);
			//Insert newly created tasknode in DB.
			System.out.println(botname+": "+methodname+": VWM Jimmy: WANT A INSERT EPI="+enode.getIntValue("number"));
			int tnum=vwmtask.insert("VWM Jimmy",tnode);
			if (tnum!=-1) {
				//Create a relation between episodenode ANd vwmtasknode startbroadcast.
				int rnum=Vwms.mmb.getInsRel().insert("VWM Jimmy",enode.getIntValue("number"),tnum,14);
				if (rnum==-1) System.out.println("Jimmy : attachStartBroadcastTask2Episode: error -1 insrel (episodes to vwmtasks)");
				retval = true;
			} else {
				System.out.println(botname+": "+methodname+": error -1 vwmtask");
				retval = false;
			}
		} else {
			System.out.println(botname+": "+methodname+": couldn't find machine for channel "+medium+":"+channel);
			retval = false;
		}
		return retval;
	}


	/**
	 * attachStopBroadcastTask2Episode: This method actually creates & puts a StopBroadcasttask in the DB for
					    given episode.  
	 */
	boolean attachStopBroadcastTask2Episode(MMObjectNode pnode,MMObjectNode enode,MMObjectNode evnode,MMObjectNode bnode, MMObjectNode lsnode, MMObjectNode mo_node) {

		String methodname = "attachStopBroadcastTask2Episode";
		boolean retval = false;
		//System.out.println(botname+": "+methodname+": pnode="+pnode);
		//System.out.println(botname+": "+methodname+": enode="+enode);
		//System.out.println(botname+": "+methodname+": tnode="+evnode);
		//System.out.println(botname+": "+methodname+": bnode="+bnode);
		//System.out.println(botname+": "+methodname+": lsnode="+lsnode);
		//System.out.println(botname+": "+methodname+": mo_node="+mo_node);

		// oke lets figure out what 'hours we need for now (will change)
		//String machine = "test1";	//Used for test purposes.
		String machine = lsnode.getStringValue("machine");;
		int channel,medium,start,stop,len;
		int pnr,enr,evnr,bnr,lsnr, mo_nr;
		start=evnode.getIntValue("start");
		stop=evnode.getIntValue("stop");
		len=evnode.getIntValue("playtime");
		channel=bnode.getIntValue("channel");
		medium=pnode.getIntValue("medium");
		int wantedtime;

		if (machine!=null) {
			MMObjectNode tnode;				//Select a new tasknode. 
			Vwmtasks vwmtask=(Vwmtasks)Vwms.mmb.getMMObject("vwmtasks");
			ExtendedProperties p=new ExtendedProperties();

			//Create a new VWMtasknode named "stopbroadcast" that will be used by Jimmy and fill various fields.
			tnode=vwmtask.getNewNode("VWM Jimmy");		
			tnode.setValue("wantedcpu",machine);
			tnode.setValue("task","stopbroadcast");
			tnode.setValue("wantedtime",stop);
			tnode.setValue("expiretime",stop+5*60);	//expiretime 5 minutes.
		
			//Get various objectnr that are related with the newly created task.
			//and put them in tasknode's datafield.
			pnr=pnode.getIntValue("number");
			enr=enode.getIntValue("number");
			evnr=evnode.getIntValue("number");
			bnr=bnode.getIntValue("number");
			lsnr=lsnode.getIntValue("number");	//get lstreamsnode nr.
			mo_nr=mo_node.getIntValue("number");

			p.put("program",""+pnr);
			p.put("episode",""+enr);
			p.put("event",""+evnr);
			p.put("bcastrel",""+bnr);
			p.put("aflevering",evnode.getStringValue("name"));
			p.put("lstreamsnode",""+lsnr);
			p.put("mediaoutputnode",""+mo_nr);

			tnode.setValue("data",p.save());
			tnode.setValue("status",1);
			tnode.setValue("vwm",getName());
			tnode.setValue("id",pnr);
			//Insert newly created tasknode in DB.
			System.out.println(botname+": "+methodname+": VWM Jimmy: WANT A INSERT EPI="+enode.getIntValue("number"));
			int tnum=vwmtask.insert("VWM Jimmy",tnode);
			if (tnum!=-1) {
				//Create a relation between episodenode ANd vwmtasknode startbroadcast.
				int rnum=Vwms.mmb.getInsRel().insert("VWM Jimmy",enode.getIntValue("number"),tnum,14);
				if (rnum==-1) System.out.println(botname+": "+methodname+": error -1 insrel (episodes to vwmtasks)");
				retval = true;
			} else {
				System.out.println(botname+": "+methodname+": error -1 vwmtask");
				retval = false;
			}
		} else {
			System.out.println(botname+": "+methodname+": couldn't find machine for channel "+medium+":"+channel);
			retval = false;
		}
		return retval;
	}


	/**
	 * performBroadcast: This method calls the performStart or StopBroadcast task.
	 **/
	public synchronized void performBroadcast(MMObjectNode tnode){
		String methodname = "performBroadcast";

		if (tnode.getStringValue("task").equals("startbroadcast")){
			System.out.println(botname+": "+methodname+": startBroadcast has entered "+methodname+" monitor");
			startBroadcast(tnode);
			System.out.println(botname+": "+methodname+": startBroadcast has left "+methodname+" monitor");
		}else{
			if  (tnode.getStringValue("task").equals("stopbroadcast")){
				System.out.println(botname+": "+methodname+": stopBroadcast has entered "+methodname+" monitor");
				stopBroadcast(tnode);
				System.out.println(botname+": "+methodname+": stopBroadcast has left "+methodname+" monitor");
			}else{
				System.out.println(botname+": "+methodname+": Can't find task "+tnode.getStringValue("task"));
			}
		}
	}
	

	/**
 	 * performStartBroadcast:
 	 */
	public void performStartBroadcast(MMObjectNode tnode) {
		performBroadcast(tnode);
	}
	/**
 	 * startBroadcast: Sets the status field of an LStreamsnode (whose name is given in "data" 
	 *  	           field) to ON_AIR if necessary.
 	 */
	private void startBroadcast(MMObjectNode tnode) {
		String methodname = "startBroadcast";
		MMObjectNode lstreamsnode=null;
		int direction=1;
		Integer semvalue_iobj = null;
		Boolean small_ep_distance_bobj=null;
		String mailsubject1 = "Error in updateEnconSemaphore method (returnvalue=null) used by task: "+tnode.getStringValue("task");
		String mailsubject2 = "Error occured in getSmallEpDistance method (returnvalue=null) used by task: "+tnode.getStringValue("task");

		System.out.println(botname+": "+methodname+": Now executing task: startbroadcast.");

	 	String datafield = tnode.getStringValue("data");//Get tasknodes' datafield.
		Hashtable props = parseProperties(datafield);	//Parse properties.
		int lstreamsnr = Integer.parseInt( (String)props.get("lstreamsnode"));	//get lstreamsnodenr.

		LStreams lstreams=(LStreams)Vwms.mmb.getMMObject("lstreams");	//get The LStreams object.
		lstreamsnode = lstreams.getNode(lstreamsnr);	//get lstreamsnode.

		//Updating semaphore variable.
		semvalue_iobj = lstreams.updateEnconSemaphore(direction,lstreamsnode); //update the semaphore in this LStreamsnode. 
		if (semvalue_iobj==null){	//Error has occured in updateEnconSemaphore.
			System.out.println(botname+": "+methodname+": Error has occured in updateEnconSemaphore method.");
			Vwms.sendMail(name,MAILTO,mailsubject1,"");	//Mail the taskname performed.
		} else {
			System.out.println(botname+": "+methodname+": enc_on semaphore updated --> enc_on is now: "+ semvalue_iobj.intValue()+".");
			if (semvalue_iobj.intValue()==0) {
				System.out.println(botname+": "+methodname+": Error previous semvalue was 0, new semvalue = 0.");
			}else {
				if (semvalue_iobj.intValue()==1){
					small_ep_distance_bobj=lstreams.getSmallEpDistance(lstreamsnode);
					if (small_ep_distance_bobj==null){
						System.out.println(botname+": "+methodname+": Error has occured in getSmallEpDistance method.");
						Vwms.sendMail(name,MAILTO,mailsubject2,"");	//Mail the taskname performed.
					} else {
						if(!small_ep_distance_bobj.booleanValue()) {	//If distance > given distance.
							System.out.println(botname+": "+methodname+": Setting the broadcaststate of lstreamsnode to ON_AIR.");
							lstreamsnode.setValue("state", ON_AIR);		//Change broadcaststate to ON_AIR which will start the LiveEncoder.
							lstreamsnode.commit();			
						} else{		//If distance <= given distance.
							System.out.println(botname+": "+methodname+": Distance between current episode and previous is <= given distance, so i don't have to change the broadcast state to ON_AIR.");
						}
					}	
				} else {	//Current episode has started or will start soon, so dont start the encoder again.
					System.out.println(botname+": "+methodname+": Current episode has started during previous episode OR will start soon, immediately following so the broadcaststate will stay on ON_AIR.");
				}
			}
		}
	}

	
	/**
	 * performStopBroadcast: 
	 **/
	public void performStopBroadcast(MMObjectNode tnode) {
		performBroadcast(tnode);
	}
	/**
	 * stopBroadcast: Task will change the broadcaststate of an LStreamnode, to OFF_AIR. 
	 *			 PROVIDED that there's no other episode being broadcasted immediately following this
	 *			 episode. Otherwise it won't change this broadcaststate.
	 **/
	private void stopBroadcast(MMObjectNode tnode) {
		String methodname = "stopBroadcast";
		MMObjectNode lstreamsnode=null;
		int direction=0;
		Integer semvalue_iobj=null;
		Boolean small_ep_distance_bobj=null;	//boolean which indicates if the distance between two
							//episodes is smaller than given distance or not.
							//true=> smaller than given distance.
		String mailsubject = "Error occured in updateEnconSemaphore method (returnvalue=null) used by task: "+tnode.getStringValue("task");

		System.out.println(botname+": "+methodname+": Now executing task StopBroadcast.");

		String datafield = tnode.getStringValue("data");	//Get tasknodes' datafield.
		Hashtable props = parseProperties(datafield);		//Parse properties.
		int lstreamsnr = Integer.parseInt( (String)props.get("lstreamsnode"));	//get lstreamsnodenr
	
		LStreams lstreams=(LStreams)Vwms.mmb.getMMObject("lstreams");	//get The LStreams object.
		//Retrieve lstreamsnode created by LStreams builder.
		lstreamsnode = lstreams.getNode(lstreamsnr);	
	
		//Update the semaphore variabele.			
 		semvalue_iobj = lstreams.updateEnconSemaphore(direction,lstreamsnode);
		if (semvalue_iobj==null) {
			System.out.println(botname+": "+methodname+": updateEnconSemaphore method done ->Error has occured returned semvalueobject =null!!");
			Vwms.sendMail(name,MAILTO,mailsubject,"");	//Mail the taskname performed.
		} else {			
			System.out.println(botname+": "+methodname+": updateEnconSemaphore method done --> enc_on is now: "+ semvalue_iobj.intValue()+".");  
			if (semvalue_iobj.intValue()==0){

				//Determine distance between current and next episode.
				small_ep_distance_bobj = determineSmallEpDistanceValue(tnode,lstreams,lstreamsnode);
		
				if (!small_ep_distance_bobj.booleanValue()) {		//-> Distance > given distance.
					System.out.println(botname+": "+methodname+": Setting the broadcaststate of lstreamsnode to OFF_AIR.");
					lstreamsnode.setValue("state", OFF_AIR);	//Change broadcaststate to OFF_AIR which
					lstreamsnode.commit();				//will stop the LiveEncoder.
				} else{		//-> Distance <= given distance.
					System.out.println(botname+": "+methodname+": Distance between current episode and next is <= given distance, so i don't have to change the broadcast state to OFF_AIR.");
				}
			} else{		//Don't do anything stopenc = 0 ->Encoder MUST NOT BE stopped.
				System.out.println(botname+": "+methodname+": There's an episode immediately following so i'm not changing broadcaststate to OFF_AIR.");
			}
		}
	}

	/**
	 * determineSmallEpDistanceValue: This method determines the smallepdistance value by finding the following
	 *				  episode closest to the current episode. 
	 *				  If the starttime of this episode is <= EP_DISTANCE then the value = true;
	 *				  else value = false.
	 */
	public Boolean determineSmallEpDistanceValue(MMObjectNode tnode,LStreams lstreams,MMObjectNode lstreamsnode) {
		String methodname = "determineSmallEpDistanceValue";
		Boolean small_ep_distance_bobj = null;	
		int startnext,stopcurrent;	//start|stoptime of the current|nextepisode.
		int closest=Integer.MAX_VALUE;	
		int curdistance;
		MMObjectNode tmptnode=null;
		int putsed_retvalue;

		String mailsubject1 = "Warning occured due to method putSmallEpDistance used by task: "+tnode.getStringValue("task");
		String mailsubject2 = "Warning in determineSmallEpDistance method used by task: "+tnode.getStringValue("task");
		String sedwarning1  = "This means that 1: server has been reset before stopbroadcasttask."
				     +"OR 2:stopbroadcasttask occured before startbroadcasttask. "
				     +"The small_ep_distance variable entered with value = false.";
		String sedwarning2  = "A startbroadcast task is found (status= 1 OR 2) with a starttime "
				     +"that lies more than the EP_DISTANCE back in time.";

		System.out.println(botname+": "+methodname+": Now determining the SmallEpisodeDistance value.");
		Vwmtasks vwmtask=(Vwmtasks)Vwms.mmb.getMMObject("vwmtasks");	
		Enumeration e=vwmtask.search("WHERE task='startbroadcast' AND wantedcpu ='" +tnode.getStringValue("wantedcpu")+"'" +" AND ((status=1) OR (status=2))");
		if (e!=null) {
			while (e.hasMoreElements()) {
				tmptnode =(MMObjectNode)e.nextElement();
				startnext = tmptnode.getIntValue("wantedtime");	//Get starttime new tasknode.

				if (startnext<closest) {	//See if starttime < closest time found till now.
					closest = startnext;
				}
			}

			//Retrieve stoptime of the currentepisode.
			stopcurrent = tnode.getIntValue("wantedtime");
				
			//Determine value of the "small_ep_distance" boolean by using the static class
			//variable "EP_DISTANCE" to find out if lstreamsnode status must stay the same.
			curdistance = closest - stopcurrent;
			if (curdistance>= 0) {	//Next episode doesn't start during current episode.
				if( curdistance <= EP_DISTANCE ) {
					small_ep_distance_bobj = new Boolean(true);
					putsed_retvalue = lstreams.putSmallEpDistance(small_ep_distance_bobj,lstreamsnode);
					
					if (putsed_retvalue == 1) {
						Vwms.sendMail(name,MAILTO,mailsubject1,sedwarning1);	//Mail taskname performed.
					}
				} else{
					small_ep_distance_bobj = new Boolean(false);
					putsed_retvalue = lstreams.putSmallEpDistance(small_ep_distance_bobj,lstreamsnode);
					
					if (putsed_retvalue == 1) {
						Vwms.sendMail(name,MAILTO,mailsubject1,sedwarning1);
					}
				}
			} else {
				if (Math.abs(curdistance) <= EP_DISTANCE){	//This means that task found has just started.
					small_ep_distance_bobj = new Boolean(true);
					putsed_retvalue = lstreams.putSmallEpDistance(small_ep_distance_bobj,lstreamsnode);

					if (putsed_retvalue == 1) {
						Vwms.sendMail(name,MAILTO,mailsubject1,sedwarning1);
					}
				}else{	//This is probably an old task that still has the taskstatus 1 OR 2
					System.out.println(botname+": "+methodname+": Warning: A startbroadcast task is found still having taskstatus 1 OR 2 !");
					small_ep_distance_bobj = new Boolean(false);
					Vwms.sendMail(name,MAILTO,mailsubject2,"sedwarning2");
				}
			}
		} else {	//There aren't any startbroadcasttasks following this stoptask.
			System.out.println(botname+": "+methodname+": There aren't any startbroadcasttasks following.");
			small_ep_distance_bobj = new Boolean(false);
		}
		return small_ep_distance_bobj;
	}
}
