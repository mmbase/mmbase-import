/*
	(C) Copyright 1999 VPRO Digitaal
*/

package org.mmbase.module.builders.vwms;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;
import java.io.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.core.*;

/**
 * @author Daniel Ockeloen
 * @author Rico Jansen
 */

public class Jerry extends Vwm implements MMBaseObserver {
private Hashtable dirtyQuestions=new Hashtable();

	int timercounter=0;

	public Jerry() {
		System.out.println("Yo Jerry and im alive");
	}

	public boolean performTask(MMObjectNode node) {
		boolean rtn=false;
		return(rtn);
	}

	public boolean probeCall() {
		if (Vwms!=null) {
			if (timercounter==1) {
				Vwms.mmb.addLocalObserver("posrel",this);
				Vwms.mmb.addRemoteObserver("posrel",this);
			}
			timercounter++;
			if (timercounter%60==0) {
				attachedContainers();
			}
			if (timercounter%10==0) {
				CleanDirtyQuestions();
			}
		}
		return(true);
	}

	private boolean attachedContainers() {
		// use the properties to hunt down all the nodes with AUTOFORUM to YES
		System.out.println("Jerry -> attachedContainers");
		Forums forums=(Forums)Vwms.mmb.getMMObject("forums");		
		Properties props=(Properties)Vwms.mmb.getMMObject("properties");		
		if (props!=null) {
			Enumeration e=props.search("WHERE key='AUTOFORUM' AND value='YES'");
			while (e.hasMoreElements()) {
				MMObjectNode propnode=(MMObjectNode)e.nextElement();
				MMObjectNode forumnode=(MMObjectNode)forums.getNode(propnode.getIntValue("parent"));
			
				// find all the forums that want jerry to control them
				if (forumnode!=null) {
					System.out.println("Jerry-> Forum node="+forumnode.getValue("number"));
					Enumeration tk;
					// check if this forum has a active opinions container attached
					tk=Vwms.mmb.getInsRel().getRelated(forumnode.getIntValue("number"),"actopi");
					if (tk.hasMoreElements()) {
						MMObjectNode actopinode=(MMObjectNode)tk.nextElement();
						System.out.println("Jerry-> Forum node="+forumnode.getValue("number")+" ActiveOpinions="+actopinode);

						checkActiveOpinions(actopinode);
					} else {
						attachActOpi(forumnode);
					}
				
					// check if this forum has a proposed opinions container attached
					tk=Vwms.mmb.getInsRel().getRelated(forumnode.getIntValue("number"),"proopi");
					if (tk.hasMoreElements()) {
						//
					} else {
						attachProOpi(forumnode);
					}

					// check if this forum has a rejected opinions container attached
					tk=Vwms.mmb.getInsRel().getRelated(forumnode.getIntValue("number"),"rejopi");
					if (tk.hasMoreElements()) {
						//
					} else {
						attachRejOpi(forumnode);
					}

					// check if this forum has a accepted opinions container attached
					tk=Vwms.mmb.getInsRel().getRelated(forumnode.getIntValue("number"),"accopi");
					if (tk.hasMoreElements()) {
						//
					} else {
						attachAccOpi(forumnode);
					}
				} else {
					System.out.println("Jerry -> Propertie found but no forum node "+propnode);
				}
			}
		}
		return(false);
	}

	private void attachActOpi(MMObjectNode forumnode) {
		MMObjectBuilder opi=Vwms.mmb.getMMObject("actopi");
		attachOpi(opi,forumnode);
	}
	private void attachProOpi(MMObjectNode forumnode) {
		MMObjectBuilder opi=Vwms.mmb.getMMObject("proopi");
		attachOpi(opi,forumnode);
	}
	private void attachAccOpi(MMObjectNode forumnode) {
		MMObjectBuilder opi=Vwms.mmb.getMMObject("accopi");
		attachOpi(opi,forumnode);
	}
	private void attachRejOpi(MMObjectNode forumnode) {
		MMObjectBuilder opi=Vwms.mmb.getMMObject("rejopi");
		attachOpi(opi,forumnode);
	}
		
	private void attachOpi(MMObjectBuilder opi,MMObjectNode forumnode) {
		InsRel insrel=(InsRel)Vwms.mmb.getMMObject("insrel");
		MMObjectNode rnode,anode;
		int anum;

		anode=opi.getNewNode("VWM Jerry");
		anode.setValue("title",forumnode.getStringValue("title"));
		anum=opi.insert("VWM Jerry",anode);
		if (anum>=0) {
			int rnum=insrel.insert("VWM Jerry",forumnode.getIntValue("number"),anum,14);
			if (rnum==-1) System.out.println("VWM Jerry : error -1 insrel (forum to opi ("+opi+"))");
		}
	}

	private boolean checkActiveOpinions(MMObjectNode actopinode) {
		// find the active opinion containers in this 'pool'
		Enumeration tk=Vwms.mmb.getInsRel().getRelated(actopinode.getIntValue("number"),"conopi");
		while (tk.hasMoreElements()) {
			MMObjectNode conopinode=(MMObjectNode)tk.nextElement();
			System.out.println("Jerry-> ActiveOpinions="+actopinode.getIntValue("number")+" conopi="+conopinode);
			checkActiveOpinionPoll(conopinode);
			checkActiveArguments(conopinode);
		}
		return(true);
	}

	// Add actarg to conopi if not existing
	private boolean checkActiveArguments(MMObjectNode conopinode) {
		Vwms.mmb.getInsRel().deleteRelationCache(conopinode.getIntValue("number"));
		Enumeration tk=Vwms.mmb.getInsRel().getRelated(conopinode.getIntValue("number"),"actarg");
		if (!tk.hasMoreElements()) {
			MMObjectBuilder actarg=Vwms.mmb.getMMObject("actarg");		
			MMObjectNode anode=actarg.getNewNode("VWM Jerry");
			anode.setValue("title","Active Argumenten voor conopi="+conopinode.getIntValue("number"));
			int anum=actarg.insert("VWM Jerry",anode);
			if (anum!=-1) {

				InsRel insrel=(InsRel)Vwms.mmb.getMMObject("insrel");		
				int rnum=insrel.insert("VWM Jerry",conopinode.getIntValue("number"),anum,14);
				if (rnum==-1) System.out.println("VWM Jerry : error -1 insrel (quesion to answers)");
			}
		}
		return(false);
	}


	private boolean checkActiveOpinionPoll(MMObjectNode conopinode) {
		// find the poll (question) attached to this container
		Vwms.mmb.getInsRel().deleteRelationCache(conopinode.getIntValue("number"));
		Enumeration tk=Vwms.mmb.getInsRel().getRelated(conopinode.getIntValue("number"),"questions");
		if (tk.hasMoreElements()) {
			MMObjectNode questionnode=(MMObjectNode)tk.nextElement();
			System.out.println("Jerry->  conopi="+conopinode.getIntValue("number")+" POLL OKE");
		} else {
			System.out.println("Jerry->  conopi="+conopinode.getIntValue("number")+" POLL MISSING");
			createNewOpinionPoll(conopinode);
		}

		return(true);
	}

	private boolean createNewOpinionPoll(MMObjectNode conopinode) {
		PosRel posrel=(PosRel)Vwms.mmb.getMMObject("posrel");		
		// create the question
			MMObjectBuilder questions=Vwms.mmb.getMMObject("questions");		
			MMObjectNode qnode=questions.getNewNode("VWM Jerry");
			qnode.setValue("title","Mee Eens ?");
			int qnum=questions.insert("VWM Jerry",qnode);

		// link the 3 anwers (no/yes/noideayet)
			MMObjectBuilder answers=Vwms.mmb.getMMObject("answers");		

		// attach anwsers to the question
			if (qnum!=-1) {
				int rnum=posrel.insert("VWM Jerry",2088659,qnum,0);
				if (rnum==-1) System.out.println("VWM Jerry : error -1 insrel (quesion to answers)");
			}
			if (qnum!=-1) {
				int rnum=posrel.insert("VWM Jerry",2088657,qnum,0);
				if (rnum==-1) System.out.println("VWM Jerry : error -1 insrel (quesion to answers)");
			}
			if (qnum!=-1) {
				int rnum=posrel.insert("VWM Jerry",2088655,qnum,0);
				if (rnum==-1) System.out.println("VWM Jerry : error -1 insrel (quesion to answers)");
			}

		// attach question to the conopi
			if (qnum!=-1) {
				int rnum=Vwms.mmb.getInsRel().insert("VWM Jerry",conopinode.getIntValue("number"),qnum,14);
				if (rnum==-1) System.out.println("VWM Jerry : error -1 insrel (quesion to answers)");
			}
 
		return(true);
	}

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeChanged(String number,String builder, String ctype) {
		System.out.println("Jerry sees that : "+number+" has changed type="+ctype+" "+builder);
		if (builder.equals("posrel") && (ctype.equals("n")||ctype.equals("c")) ) {
			addDirtyQuestion(number);
		}
		return(true);
	}


	private void addDirtyQuestion(String number) {
		String qnumber;

		qnumber=findQuestionByUserPosrel(number);
		if (qnumber!=null && qnumber.length()>0) {
			if (!dirtyQuestions.containsKey(qnumber)) {
				dirtyQuestions.put(qnumber,qnumber);
			}
		}
	}

	private String findQuestionByUserPosrel(String number) {
		String rtn=null;
		Vector tables,fields,ordervec,dirvec,vec;
		MMObjectNode node,pnode;
		MultiRelations multirel=(MultiRelations)Vwms.mmb.getMMObject("multirelations");
		// Fetch the questions which are connected to users and questions
		// Maybe add conopi/conarg to the list (that would require two queries though
		tables=new Vector();
		tables.addElement("questions");
		tables.addElement("posrel");
		tables.addElement("users");
		fields=new Vector();
		fields.addElement("questions.number");
		ordervec=new Vector();
		dirvec=new Vector();
		vec=multirel.searchMultiLevelVector(Integer.parseInt(number),fields,"NO",tables,"",ordervec,dirvec);

		for (Enumeration e=vec.elements();e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();
			rtn=""+node.getIntValue("questions.number");
		}
		return(rtn);
	}

	private void CleanDirtyQuestions() {
		if (dirtyQuestions.size()>0) {
			Hashtable cd;
			String qkey;
			MMObjectBuilder questions=Vwms.mmb.getMMObject("questions");		
			MultiRelations multirel=(MultiRelations)Vwms.mmb.getMMObject("multirelations");
			MMObjectNode qnode;

			System.out.println("Jerry -> And the dirty questions are : "+dirtyQuestions);
			synchronized(dirtyQuestions) {
				cd=dirtyQuestions;
				dirtyQuestions=new Hashtable();
			}
			for (Enumeration e=cd.keys();e.hasMoreElements();) {
				qkey=(String)e.nextElement();
				qnode=questions.getNode(qkey);
				if (qnode!=null) calculatePoll(qnode);
			}
		}
	}
	
	private void calculatePoll(MMObjectNode qnode) {
		int a_yes=0,a_no=0,a_unknown=0,pval;
		Vector tables,fields,ordervec,dirvec,vec;
		String atitle;
		MMObjectNode node,pnode;
			MultiRelations multirel=(MultiRelations)Vwms.mmb.getMMObject("multirelations");

		System.out.println("Jerry -> Calculating poll for "+qnode);
		// Fetch the posrels which are connected to users
		tables=new Vector();
		tables.addElement("questions");
		tables.addElement("posrel");
		tables.addElement("users");
		fields=new Vector();
		fields.addElement("posrel.pos");
		ordervec=new Vector();
		dirvec=new Vector();
		vec=multirel.searchMultiLevelVector(qnode.getIntValue("number"),fields,"NO",tables,"",ordervec,dirvec);

		System.out.println("Jerry -> Following posrels -> "+vec);
		for (Enumeration e=vec.elements();e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();
			pval=node.getIntValue("posrel.pos");
			switch(pval) {
				case 0:
					a_unknown++;
					break;
				case 1:
					a_yes++;
					break;
				case 2:
					a_no++;
					break;
			}
		}
		System.out.println("Question result : "+qnode);
		System.out.println("Question result : Tegen "+a_no+" Voor "+a_yes+" Weet niet "+a_unknown);

		// Fetch the posrels which are connected to answers
		tables=new Vector();
		tables.addElement("questions");
		tables.addElement("posrel");
		tables.addElement("answers");
		fields=new Vector();
		fields.addElement("posrel.number");
		fields.addElement("answers.title");
		ordervec=new Vector();
		dirvec=new Vector();
		vec=multirel.searchMultiLevelVector(qnode.getIntValue("number"),fields,"NO",tables,"",ordervec,dirvec);
		for (Enumeration e=vec.elements();e.hasMoreElements();) {
			node=(MMObjectNode)e.nextElement();
			pnode=multirel.getNode(node.getIntValue("posrel.number"));
			atitle=node.getStringValue("answers.title");
			// Note add the one for 'Accepteer Ja/Nee');
			if (atitle.startsWith("One") || atitle.startsWith("Nee")) {
				pnode.setValue("pos",a_no);
			} else if (atitle.startsWith("Mee") || atitle.startsWith("Ja")) {
				pnode.setValue("pos",a_yes);
			} else if (atitle.startsWith("Nog")) {
				pnode.setValue("pos",a_unknown);
			} else {
				System.out.println("Jerry -> Unknown title "+atitle+" ("+qnode+") ");
			}
			pnode.commit();
		}
	}


}
