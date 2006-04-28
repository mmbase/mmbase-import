/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/

package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.module.ParseException;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

/**
 * @author Daniel Ockeloen
 * @author Rico Jansen
 * @version $Id: Forums.java,v 1.7 2003-03-04 14:12:23 nico Exp $
 */
public class Forums extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(Forums.class.getName());

	/*
	public Forums(MMBase m) {
		this.mmb=m;
		this.tableName="forums";
		this.description="Forums grouping of contributions";
		this.dutchSName="Forum";
		init();
		m.mmobjs.put(tableName,this);
	}
	*/

	
	/**
	* insert a new object, normally not used (only subtables are used)
	*/
	/*
	public boolean create() {
		// create the main object table
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t (title varchar(255) not null"
				+", intro char(2048) not null) under "+mmb.baseName+"_object_t");
			log.debug("create(): Created "+tableName);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			log.error("create(): ERROR: can't create type "+tableName);
			e.printStackTrace();
		}
		try {
			MultiConnection con=mmb.getConnection();
			Statement stmt=con.createStatement();
			stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t ("
				+" primary key(number)) under "+mmb.baseName+"_object");
			stmt.close();
			con.close();
		} catch (SQLException e) {
			log.error("create(): can't create table "+tableName);
			e.printStackTrace();
		}
		return(false);
	}
	*/


	public void setDefaults(MMObjectNode node) {
		node.setValue("description","");
	}

	public String getGUIIndicator(MMObjectNode node) {
        String str=node.getStringValue("title");
        if (str.length()>15) {
            return(str.substring(0,12)+"...");
        } else {
            return(str);
        }
	}

	 public String replace(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("FIELD")) { 
				return(getObjectField(sp,tok));
			}
		}
		return("");
	 }

	/**
	 * Generate a list of values from a command to the processor
	 */
	 public Vector  getList(scanpage sp,StringTagger tagger, StringTokenizer tok) throws ParseException {
		Vector results=new Vector();
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();
			log.debug("getList("+sp.req.getRequestURI()+"): FORUMS->"+cmd);

			if (cmd.equals("ACTIVEOPINIONS")) {
				results=getAttachedOpinions("actopi",tagger);
			} else if (cmd.equals("PROPOSEDOPINIONS")) {
				results=getAttachedOpinions("proopi",tagger);
			} else if (cmd.equals("ACCEPTEDOPINIONS")) {
				results=getAttachedOpinions("accopi",tagger);
			} else if (cmd.equals("REJECTEDOPINIONS")) {
				results=getAttachedOpinions("rejopi",tagger);

			} else if (cmd.equals("ACTIVEARGUMENTS")) {
				results=getAttachedArguments("actarg",tagger);
			} else if (cmd.equals("PROPOPSEDARGUMENTS")) {
				results=getAttachedArguments("proarg",tagger);
			} else if (cmd.equals("ACCEPTEDARGUMENTS")) {
				results=getAttachedArguments("accarg",tagger);
			} else if (cmd.equals("REJECTEDARGUMENTS")) {
				results=getAttachedArguments("rejarg",tagger);

			} else if (cmd.equals("OPINION")) {
				results=getOpinion(tagger);
//			} else if (cmd.equals("ARGUMENT")) {
//				results=getArgument(tagger);
			}
			log.debug("getList("+sp.req.getRequestURI()+"): "+cmd+" done");
		}
		return(results);
	}

	public Vector getOpinion(StringTagger tagger) {
		Vector results=new Vector();
		String conopi=tagger.Value("CONOPI");
		MMObjectNode conopinode=getNode(conopi);

		// conopi.number,conopi.id,opi.title,opi.body,X,people.name,people.number
		// Poll=NO X= "" , question.number ITEMS=8
		// Poll=YES X= question.number, 1, yes% , 2 no%, 0 , maybe% ITEMS=13
		if (conopinode!=null) {
			boolean pollinfo;
			String pi=tagger.Value("POLL");
			if (pi!=null && (pi.equals("YES") || pi.equals("\"YES\""))) pollinfo=true;
			else pollinfo=false;
			
			results.addElement(""+conopinode.getIntValue("number"));
			results.addElement(""+conopinode.getIntValue("id"));
			MMObjectNode opinode=getNode(conopinode.getIntValue("id"));
			if (opinode!=null) {
				String tmp=opinode.getStringValue("title");
				if (tmp!=null) {
					results.addElement(tmp);
				} else {
					results.addElement("");
				}
				tmp=opinode.getStringValue("body");
				if (tmp!=null) {
					results.addElement(tmp);
				} else {
					results.addElement("");
				}
			} else {
				results.addElement("");
				results.addElement("");
			}

			// add the attached poll (question/posrel/answer)
			if (pollinfo) {
				results=getPollInfo2(conopinode,results);	
			} else {
				results=getPollInfo(conopinode,results);	
			}
			Enumeration y=mmb.getInsRel().getRelated(opinode.getIntValue("number"),"people"); // find the user this statement belongs to
			if (y.hasMoreElements()) {
				MMObjectNode peoplenode=(MMObjectNode)y.nextElement();
				results.addElement(peoplenode.getStringValue("firstname")+" "+peoplenode.getStringValue("lastname"));	
				results.addElement(""+peoplenode.getIntValue("number"));	
			} else {
				results.addElement("");
				results.addElement("");
			}
		}
		return(results);
	}

	public Vector getArgument(StringTagger tagger) {
		Vector results=new Vector();
		String conarg=tagger.Value("CONARG");
		MMObjectNode conargnode=getNode(conarg);

		// conarg.number,conarg.id,arg.title,arg.body,X,people.name,people.number
		// Poll=NO X= "" , question.number ITEMS=8
		// Poll=YES X= question.number, 1, yes% , 2 no%, 0 , maybe% ITEMS=13
		if (conargnode!=null) {
			boolean pollinfo;
			String pi=tagger.Value("POLL");
			if (pi!=null && (pi.equals("YES") || pi.equals("\"YES\""))) pollinfo=true;
			else pollinfo=false;
			
			results.addElement(""+conargnode.getIntValue("number"));
			results.addElement(""+conargnode.getIntValue("id"));
			MMObjectNode argnode=getNode(conargnode.getIntValue("id"));
			if (argnode!=null) {
				String tmp=argnode.getStringValue("title");
				if (tmp!=null) {
					results.addElement(tmp);
				} else {
					results.addElement("");
				}
				tmp=argnode.getStringValue("body");
				if (tmp!=null) {
					results.addElement(tmp);
				} else {
					results.addElement("");
				}
			} else {
				results.addElement("");
				results.addElement("");
			}

			// add the attached poll (question/posrel/answer)
			if (pollinfo) {
				results=getPollInfo2(conargnode,results);	
			} else {
				results=getPollInfo(conargnode,results);	
			}
			Enumeration y=mmb.getInsRel().getRelated(argnode.getIntValue("number"),"people"); // find the user this statement belongs to
			if (y.hasMoreElements()) {
				MMObjectNode peoplenode=(MMObjectNode)y.nextElement();
				results.addElement(peoplenode.getStringValue("firstname")+" "+peoplenode.getStringValue("lastname"));	
				results.addElement(""+peoplenode.getIntValue("number"));	
			} else {
				results.addElement("");
				results.addElement("");
			}
		}
		return(results);
	}


	public Vector getPollInfo(MMObjectNode anode, Vector results) {
		Enumeration r=mmb.getInsRel().getRelated(anode.getIntValue("number"),"questions"); // find the question attached 
		if (r.hasMoreElements()) {
			MMObjectNode questionnode=(MMObjectNode)r.nextElement();
			results.addElement("");
			results.addElement(""+questionnode.getIntValue("number"));
		} else {
			log.debug("Forums -> No poll for "+anode);
			results.addElement("");
			results.addElement("");
		}
		return(results);
	}

	public Vector getPollInfo2(MMObjectNode anode, Vector results) {
		MMObjectNode node,yesnode=null,nonode=null,maynode=null;
		String atitle;
		Vector tables=new Vector();
		tables.addElement(anode.getName());
		tables.addElement("questions");
		tables.addElement("posrel");
		tables.addElement("answers");
		Vector fields=new Vector();
		fields.addElement("questions.number");
		fields.addElement("posrel.pos");
		fields.addElement("answers.number");
		fields.addElement("answers.title");
		Vector ordervec=new Vector();
		Vector dirvec=new Vector();
		MultiRelations multirelations=(MultiRelations)mmb.getMMObject("multirelations");		
		Vector vec=multirelations.searchMultiLevelVector(anode.getIntValue("number"),fields,"YES",tables,"",ordervec,dirvec);

		if (vec.size()>0) {
			if (vec.size()!=3) {
				log.debug("getPollInfo2(): Forums -> More then 3 answers ? "+vec);
			}
			for (Enumeration e=vec.elements();e.hasMoreElements();) {
				node=(MMObjectNode)e.nextElement();
				atitle=node.getStringValue("answers.title");
				// Note add the one for 'Accepteer Ja/Nee');
				if (atitle.startsWith("One") || atitle.startsWith("Nee")) {
					nonode=node;
				} else if (atitle.startsWith("Mee") || atitle.startsWith("Ja")) {
					yesnode=node;
				} else if (atitle.startsWith("Nog")) {
					maynode=node;
				} else {
					log.debug("getPollInfo2(): Jerry -> Unknown title "+atitle+" ("+node+") ");
				}
			}
			calcPercentages(yesnode,nonode,maynode);
			results.addElement(""+yesnode.getIntValue("questions.number")); // Question number

			results.addElement("1"); // Yes ( );
			results.addElement(""+yesnode.getStringValue("posrel.percentage")); // Percent Yes

			results.addElement("2"); // No ( );
			results.addElement(""+nonode.getStringValue("posrel.percentage")); // Percent No

			results.addElement("0"); // Don't know ( );
			results.addElement(""+maynode.getStringValue("posrel.percentage")); // Percent Don't know
		} else {
			// Perhaps add q number here ?
			results.addElement(""); // Question number

			results.addElement("1"); // Yes ( );
			results.addElement("0"); // Percent Yes

			results.addElement("2"); // No ( );
			results.addElement("0"); // Percent No

			results.addElement("0"); // Don't know ( );
			results.addElement("0"); // Percent Don't know
		}
		return(results);
	}

	private void calcPercentages(MMObjectNode yesnode,MMObjectNode nonode,MMObjectNode maynode) {
		double sum;
		double ypercent,npercent,mpercent;
		double y,n,m;

		y=yesnode.getIntValue("posrel.pos");
		n=nonode.getIntValue("posrel.pos");
		m=maynode.getIntValue("posrel.pos");
		sum=y+n+m;
		if (sum==0.0) {
			ypercent=npercent=mpercent=0.0;
		} else {
			ypercent=(100.0*y)/sum;
			npercent=(100.0*n)/sum;
			mpercent=(100.0*m)/sum;
		}
		log.debug("calcPercentages(): Forums -> calcPercentage "+ypercent+" , "+npercent+" , "+mpercent+" totalvotes "+sum);
		yesnode.setValue("posrel.percentage",""+(int)(ypercent+0.5));
		nonode.setValue("posrel.percentage",""+(int)(npercent+0.5));
		maynode.setValue("posrel.percentage",""+(int)(mpercent+0.5));
	}

	String getObjectField(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String nodeNr=tok.nextToken();
			String fieldname=tok.nextToken();
			String result=null;
			MMObjectNode cnode=getNode(nodeNr);
			if (cnode!=null) {
				nodeNr=""+cnode.getIntValue("id");
			}
			MMObjectNode node=getNode(nodeNr);
			if (result!=null) {
				return(result);
			} else  {
				//String result=node.getValue(fieldName);
				result=node.getStringValue(fieldname);
				if (result!=null && !result.equals("null")) {
					return(result);
				} else {
					return("");
				}
			}
		}
		return("no command defined");
	}

	/*
	 * Forum opinion / argument migration 
	 *
	 */

	private void migrateOpinion(int forum,int conopi,boolean accept) {
		MMObjectNode forumnode=getNode(forum);
		MMObjectNode rnode,proopi,go_opi=null,connode,newcon,actnode;
		Enumeration tk,e1;
		int pronumber;
		int found=0;

		// Get proposed opininon container
		tk=mmb.getInsRel().getRelated(forum,"proopi");
		if (tk.hasMoreElements()) {
			proopi=(MMObjectNode)tk.nextElement();
			pronumber=proopi.getIntValue("number");
			// Select correct container to move to.
			if (accept) {
				e1=mmb.getInsRel().getRelated(forum,"accopi");
			} else {
				e1=mmb.getInsRel().getRelated(forum,"rejopi");
			}
			// Find container
			if (e1.hasMoreElements()) {
				go_opi=(MMObjectNode)e1.nextElement();
				connode=getNode(conopi);
				if (connode!=null) {
					// Get relations of conopi
					Enumeration rels=connode.getRelations("insrel");
					if (rels.hasMoreElements()) {
						// Find the one to the proposed opinion container
						rnode=null;
						while(rels.hasMoreElements() && found==0) {
							rnode=(MMObjectNode)rels.nextElement();
							if (rnode.getIntValue("snumber")==pronumber) found=1;
							if (rnode.getIntValue("dnumber")==pronumber) found=2;
						}
						// Move the relation to the go container (rejopi/accopi)
						if (found!=0) {
							if (found==1) {
								rnode.setValue("snumber",go_opi.getIntValue("number"));
								rnode.commit();
							} else if (found==2) {
								rnode.setValue("dnumber",go_opi.getIntValue("number"));
								rnode.commit();

							} else {
								log.debug("migrateOpinion(): BitRotter strikes again!!! "+conopi);
							}
							if (accept) {
								// Clone conopi and attach to actopi
								e1=mmb.getInsRel().getRelated(forum,"actopi");
								if (e1.hasMoreElements()) {
									actnode=(MMObjectNode)e1.nextElement();
									newcon=mmb.getMMObject("conopi").getNewNode("Forums");
									newcon.setValue("id",connode.getIntValue("id"));
									int r=newcon.insert("Forums");
									if (r>=0) {
										rnode=mmb.getInsRel().getNewNode("Forums");
										rnode.setValue("rnumber",14);
										rnode.setValue("snumber",r);
										rnode.setValue("dnumber",actnode.getIntValue("number"));
										r=rnode.insert("Forums");
										if (r<0) {
											log.debug("migrateOpinion(): can't create relations between "+newcon.getIntValue("number")+" AND "+actnode.getIntValue("number"));
										}
									} else {
										log.warn("migrateOpinion(): can't insert new conopi"+forum+","+conopi);
									}
								} else {
								
									log.warn("migrateOpinion(): forum has no relation to actopi "+forum);
								}
							} else {
								log.warn("migrateOpinion(): opinion not put in actopi (rejected opinion) "+go_opi);

							}
						} else {
							log.warn("migrateOpinion(): conopi has no relation to proopi "+conopi);
						}
					} else {
						log.warn("migrateOpinion(): conopi has no relations ? "+conopi);
					}
				}
			} else {
				log.warn("migrateOpinion(): No accopi/rejopi to migrate to "+forumnode);
			}

		} else {
			log.warn("migrateOpinion(): Can't promote conopi not attached to proopi : "+conopi);
		}
	}

	private void migrateArgument(int conopi,int conarg,boolean accept) {
		MMObjectNode connode=getNode(conopi);
		MMObjectNode rnode,proarg,go_arg=null,conargnode,newcon,actnode;
		Enumeration tk,e1;
		int pronumber;
		int found=0;

		// Get proposed opininon container
		tk=mmb.getInsRel().getRelated(conopi,"proarg");
		if (tk.hasMoreElements()) {
			proarg=(MMObjectNode)tk.nextElement();
			pronumber=proarg.getIntValue("number");
			// Select correct container to move to.
			if (accept) {
				e1=mmb.getInsRel().getRelated(conopi,"accarg");
			} else {
				e1=mmb.getInsRel().getRelated(conopi,"rejarg");
			}
			// Find container
			if (e1.hasMoreElements()) {
				go_arg=(MMObjectNode)e1.nextElement();
				conargnode=getNode(conarg);
				if (conargnode!=null) {
					// Get relations of conarg
					Enumeration rels=conargnode.getRelations("contextrel");
					if (rels.hasMoreElements()) {
						// Find the one to the proposed argument container
						rnode=null;
						while(rels.hasMoreElements() && found==0) {
							rnode=(MMObjectNode)rels.nextElement();
							if (rnode.getIntValue("snumber")==pronumber) found=1;
							if (rnode.getIntValue("dnumber")==pronumber) found=2;
						}
						// Move the relation to the go container (rejarg/accarg)
						if (found!=0) {
							if (found==1) {
								rnode.setValue("snumber",go_arg.getIntValue("number"));
								rnode.commit();
							} else if (found==2) {
								rnode.setValue("dnumber",go_arg.getIntValue("number"));
								rnode.commit();

							} else {
								log.debug("migrateArgument(): BitRotter strikes again!!! "+conopi);
							}
							if (accept) {
								// Clone conarg and attach to actarg
								e1=mmb.getInsRel().getRelated(conopi,"actarg");
								if (e1.hasMoreElements()) {
									actnode=(MMObjectNode)e1.nextElement();
									newcon=mmb.getMMObject("conarg").getNewNode("Forums");
									newcon.setValue("id",conargnode.getIntValue("id"));
									int r=newcon.insert("Forums");
									if (r>=0) {
										rnode=mmb.getMMObject("contextrel").getNewNode("Forums");
										rnode.setValue("snumber",r);
										rnode.setValue("dnumber",actnode.getIntValue("number"));
										rnode.setValue("contexttype",rnode.getIntValue("contexttype"));
										r=rnode.insert("Forums");
										if (r<0) {
											log.debug("migrateArgument(): can't create relations between "+newcon.getIntValue("number")+" AND "+actnode.getIntValue("number"));
										}
									} else {
										log.warn("migrateArgument(): can't insert new conarg"+conopi+","+conarg);
									}
								} else {
								
									log.warn("migrateArgument(): conopi has no relation to actarg "+conopi);
								}
							} else {
								log.warn("migrateArgument(): argument not put in actarg (rejected argument) "+go_arg);

							}
						} else {
							log.warn("migrateArgument(): conopi has no relation to proarg "+conopi);
						}
					} else {
						log.warn("migrateArgument(): conopi has no relations ? "+conopi);
					}
				}
			} else {
				log.warn("migrateArgument(): No accarg/rejarg to migrate to "+connode);
			}

		} else {
			log.warn("migrateArgument(): Can't promote conarg not attached to proarg : "+conarg);
		}
	}

	public Vector getAttachedOpinions(String connector,StringTagger tagger) {
		Vector results=new Vector();
		String forum=tagger.Value("FORUM");
		MMObjectNode forumnode=getNode(forum);

		if (forumnode!=null) {
			boolean pollinfo;
			String pi=tagger.Value("POLL");
			if (pi!=null && (pi.equals("YES") || pi.equals("\"YES\""))) pollinfo=true;
			else pollinfo=false;
			
			Enumeration tk=mmb.getInsRel().getRelated(forumnode.getIntValue("number"),connector); // find the active opinion container
			if (tk.hasMoreElements()) {
				MMObjectNode connectnode=(MMObjectNode)tk.nextElement();
				Enumeration r=mmb.getInsRel().getRelated(connectnode.getIntValue("number"),"conopi"); // find the conarguments in this container
				while (r.hasMoreElements()) {
					MMObjectNode conopinode=(MMObjectNode)r.nextElement();
					results.addElement(""+conopinode.getIntValue("number"));
					results.addElement(""+conopinode.getIntValue("id"));
					MMObjectNode opinode=getNode(conopinode.getIntValue("id"));
					if (opinode!=null) {
						String tmp=opinode.getStringValue("title");
						if (tmp!=null) {
							results.addElement(tmp);
						} else {
							results.addElement("");
						}
						tmp=opinode.getStringValue("body");
						if (tmp!=null) {
							results.addElement(tmp);
						} else {
							results.addElement("");
						}
					} else {
						results.addElement("");
						results.addElement("");
					}

					// add the attached poll (question/posrel/answer)
					if (pollinfo) {
						results=getPollInfo2(conopinode,results);	
					} else {
						results=getPollInfo(conopinode,results);	
					}
					Enumeration y=mmb.getInsRel().getRelated(opinode.getIntValue("number"),"people"); // find the user this statement belongs to
					if (y.hasMoreElements()) {
						MMObjectNode peoplenode=(MMObjectNode)y.nextElement();
						results.addElement(peoplenode.getStringValue("firstname")+" "+peoplenode.getStringValue("lastname"));	
						results.addElement(""+peoplenode.getIntValue("number"));	
					} else {
						results.addElement("");
						results.addElement("");
					}
				}
				
			}
		}
		return(results);
	}

	public Vector getAttachedArguments(String connector,StringTagger tagger) {
		MMObjectNode argnode=null;
		Vector results=new Vector();
		String conopi=tagger.Value("CONOPI");
		MMObjectNode conopinode=getNode(conopi);
		if (conopinode!=null) {
			boolean pollinfo;
			String pi=tagger.Value("POLL");
			if (pi!=null && (pi.equals("YES") || pi.equals("\"YES\""))) pollinfo=true;
			else pollinfo=false;

			Enumeration tk=mmb.getInsRel().getRelated(conopinode.getIntValue("number"),connector);
			if (tk.hasMoreElements()) {
				MMObjectNode connectnode=(MMObjectNode)tk.nextElement();
				Enumeration r=mmb.getInsRel().getRelated(connectnode.getIntValue("number"),"conarg");
				while (r.hasMoreElements()) {
					MMObjectNode conargnode=(MMObjectNode)r.nextElement();
					results.addElement(""+conargnode.getIntValue("number"));
					results.addElement(""+conargnode.getIntValue("id"));
					argnode=getNode(conargnode.getIntValue("id"));
					if (argnode!=null) {
						String tmp=argnode.getStringValue("title");
						if (tmp!=null) {
							results.addElement(tmp);
						} else {
							results.addElement("");
						}
						tmp=argnode.getStringValue("body");
						if (tmp!=null) {
							results.addElement(tmp);
						} else {
							results.addElement("");
						}
					} else {
						results.addElement("");
						results.addElement("");
					}
					if (pollinfo) {
						results=getPollInfo2(conargnode,results);	
					} else {
						results=getPollInfo(conargnode,results);
					}
					Enumeration y=mmb.getInsRel().getRelated(argnode.getIntValue("number"),"people"); // find the user this statement belongs to
					if (y.hasMoreElements()) {
						MMObjectNode peoplenode=(MMObjectNode)y.nextElement();
						results.addElement(peoplenode.getStringValue("firstname")+" "+peoplenode.getStringValue("lastname"));	
						results.addElement(""+peoplenode.getIntValue("number"));	
					} else {
						results.addElement("");
						results.addElement("");
					}
				}
			}
		}
		return(results);
	}
}
