/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.tools;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.io.*;
import java.sql.*;

import org.mmbase.util.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.*;
import org.mmbase.module.database.support.*;


/**
 * @author Daniel Ockeloen
 */
public class MMExamples extends ProcessorModule {

	MMBase mmb=null;

	public void init() {
		mmb=(MMBase)getModule("MMBASEROOT");		
	}


	/**
	 */
	public MMExamples() {
	}

	/**
	 * Generate a list of values from a command to the processor
	 */
	 public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
    	String line = Strip.DoubleQuote(value,Strip.BOTH);
		StringTokenizer tok = new StringTokenizer(line,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			//if (cmd.equals("OBJECTS")) return(doObjects(req,tagger));

		}
		return(null);
	}

	/**
	 * Execute the commands provided in the form values
	 */
	public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
		String cmdline,token;

		for (Enumeration h = cmds.keys();h.hasMoreElements();) {
			cmdline=(String)h.nextElement();	
			StringTokenizer tok = new StringTokenizer(cmdline,"-\n\r");
			token = tok.nextToken();
			if (token.equals("INSTALL")) {
				doInstall(cmds,vars);
			}
		}
		return(false);
	}

	/**
	*	Handle a $MOD command
	*/
	public String replace(scanpage sp, String cmds) {
		StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("FIELD")) { 
			}
		}
		return("No command defined");
	}

	public void maintainance() {
	}

	public void doInstall(Hashtable cmds, Hashtable vars) {

		if ((String)vars.get("NAME-Basics")!=null) installBasics();
		if ((String)vars.get("NAME-MyYahoo")!=null) installMyYahoo();
		if ((String)vars.get("NAME-MyYahoo2")!=null) installApplication("MyYahoo");
		if ((String)vars.get("NAME-MyNews")!=null) installMyNews();
		if ((String)vars.get("NAME-BasicAuth")!=null) installBasicAuth();
	}

	private void installApplication(String applicationname) {
		System.out.println("INSTALL APP ="+applicationname);
		String path=MMBaseContext.getConfigPath()+("/applications/");
		XMLApplicationReader app=new XMLApplicationReader(path+applicationname+".xml");
		if (app!=null) {
			//System.out.println(app.getApplicationName());
			//System.out.println(app.getApplicationVersion());
			if (areBuildersLoaded(app.getNeededBuilders())) {
				if (checkRelDefs(app.getNeededRelDefs())) {
					if (checkAllowedRelations(app.getAllowedRelations())) {
						if (installDataSources(app.getDataSources())) {
							if (installRelationSources(app.getRelationSources())) {
							} else {
								System.out.println("Application installer stopped : can't install relationsources");
							}
						} else {
							System.out.println("Application installer stopped : can't install datasources");
						}
					} else {
						System.out.println("Application installer stopped : can't install allowed relations");
					}
				} else {
					System.out.println("Application installer stopped : can't install reldefs");
				}
			} else {
				System.out.println("Application installer stopped : not all needed builders present");
			}
		}
	}

	boolean installDataSources(Vector ds) {
		for (Enumeration h = ds.elements();h.hasMoreElements();) {
			Hashtable bh=(Hashtable)h.nextElement();	
			String path=(String)bh.get("path");
			path=MMBaseContext.getConfigPath()+("/applications/")+path;
			XMLNodeReader nodereader=new XMLNodeReader(path,mmb);
			
			String exportsource=nodereader.getExportSource();
			int timestamp=nodereader.getTimeStamp();

			MMObjectBuilder syncbul=mmb.getMMObject("syncnodes");
			if (syncbul!=null) {
				for (Enumeration n = (nodereader.getNodes(mmb)).elements();n.hasMoreElements();) {
					MMObjectNode newnode=(MMObjectNode)n.nextElement();
					int exportnumber=newnode.getIntValue("number");
					String query="exportnumber=="+exportnumber+"+exportsource=='"+exportsource+"'";
					Enumeration b=syncbul.search(query);
					if (b.hasMoreElements()) {
						MMObjectNode syncnode=(MMObjectNode)b.nextElement();
						System.out.println("node allready installed : "+exportnumber);
					} else {
						newnode.setValue("number",-1);
						int localnumber=newnode.insert("import");
						if (localnumber!=-1) {
							MMObjectNode syncnode=syncbul.getNewNode("import");
							syncnode.setValue("exportsource",exportsource);
							syncnode.setValue("exportnumber",exportnumber);
							syncnode.setValue("timestamp",timestamp);
							syncnode.setValue("localnumber",localnumber);
							syncnode.insert("import");
						}
					}
				}
			} else {
				System.out.println("Application installer : can't reach syncnodes builder");
			}
		}
		return(true);
	}


	boolean installRelationSources(Vector ds) {
		for (Enumeration h = ds.elements();h.hasMoreElements();) {
			Hashtable bh=(Hashtable)h.nextElement();	
			String path=(String)bh.get("path");
			path=MMBaseContext.getConfigPath()+("/applications/")+path;
			XMLRelationNodeReader nodereader=new XMLRelationNodeReader(path,mmb);
			
			String exportsource=nodereader.getExportSource();
			int timestamp=nodereader.getTimeStamp();

			MMObjectBuilder syncbul=mmb.getMMObject("syncnodes");
			if (syncbul!=null) {
				for (Enumeration n = (nodereader.getNodes(mmb)).elements();n.hasMoreElements();) {
					MMObjectNode newnode=(MMObjectNode)n.nextElement();
					int exportnumber=newnode.getIntValue("number");
					Enumeration b=syncbul.search("exportnumber=="+exportnumber+"+exportsource=='"+exportsource+"'");
					if (b.hasMoreElements()) {
						MMObjectNode syncnode=(MMObjectNode)b.nextElement();
						System.out.println("node allready installed : "+exportnumber);
					} else {
						newnode.setValue("number",-1);
						
						// find snumber

						int snumber=newnode.getIntValue("snumber");
						b=syncbul.search("exportnumber=="+snumber+"+exportsource=='"+exportsource+"'");
						if (b.hasMoreElements()) {
							MMObjectNode n2=(MMObjectNode)b.nextElement();
							snumber=n2.getIntValue("localnumber");
						} else {
							snumber=-1;
						}


						// find dnumber
						int dnumber=newnode.getIntValue("dnumber");
						b=syncbul.search("exportnumber=="+dnumber+"+exportsource=='"+exportsource+"'");
						if (b.hasMoreElements()) {
							MMObjectNode n2=(MMObjectNode)b.nextElement();
							dnumber=n2.getIntValue("localnumber");
						} else {
							dnumber=-1;
						}
					
						newnode.setValue("snumber",snumber);
						newnode.setValue("dnumber",dnumber);
						System.out.println("REL="+newnode);	
						int localnumber=-1;	
						if (snumber!=-1 && dnumber!=-1) localnumber=newnode.insert("import");
						if (localnumber!=-1) {
							MMObjectNode syncnode=syncbul.getNewNode("import");
							syncnode.setValue("exportsource",exportsource);
							syncnode.setValue("exportnumber",exportnumber);
							syncnode.setValue("timestamp",timestamp);
							syncnode.setValue("localnumber",localnumber);
							syncnode.insert("import");
						}
					}
				}
			} else {
				System.out.println("Application installer : can't reach syncnodes builder");
			}
		}
		return(true);
	}

	boolean checkRelDefs(Vector reldefs) {
		for (Enumeration h = reldefs.elements();h.hasMoreElements();) {
			Hashtable bh=(Hashtable)h.nextElement();	
			String source=(String)bh.get("source");
			String target=(String)bh.get("target");
			String direction=(String)bh.get("direction");
			String guisourcename=(String)bh.get("guisourcename");
			String guitargetname=(String)bh.get("guitargetname");
			if (direction.equals("bidirectional")) {
				checkRelDef(source,target,2,guisourcename,guitargetname);
			} else {
				checkRelDef(source,target,1,guisourcename,guitargetname);
			}
		}
		return(true);
	}

	boolean checkAllowedRelations(Vector relations) {
		for (Enumeration h = relations.elements();h.hasMoreElements();) {
			Hashtable bh=(Hashtable)h.nextElement();	
			String from=(String)bh.get("from");
			String to=(String)bh.get("to");
			String type=(String)bh.get("type");
			checkTypeRel(from,to,type,-1);
		}
		return(true);
	}

	boolean areBuildersLoaded(Vector neededbuilders) {
		for (Enumeration h = neededbuilders.elements();h.hasMoreElements();) {
			Hashtable bh=(Hashtable)h.nextElement();	
			String name=(String)bh.get("name");
			MMObjectBuilder bul=mmb.getMMObject(name);
			if (bul==null) {
				System.out.println("Application installer error : builder '"+name+"' not loaded");
				return(false);
			}
		}		
		return(true);
	}

	private void installMyNews() {
		// check if the needed relation types are installed
		checkRelDef("related","related",2,"Gerelateerd","Gerelateerd");

		// allow relation between pools and pools
		checkTypeRel("pools","news","related",-1);
		checkTypeRel("news","people","related",-1);
		checkTypeRel("news","urls","related",-1);
		checkTypeRel("news","mmevents","related",-1);

		// add a few news items with some things attached
		String intro="As of 20 Dec, VPRO with help of several dutch Internet and Content company's";
		intro+=" announced that they will release a publish/content system as a opensource project in Q1 - 2000.";
		intro+=" The last 6 months work was underway to make it opensource ready, setup partners and this website/communitie.";
		int n1=checkNews("www.mmbase.org online at last","We took a while but www.mmbase.org is online as of 20 Dec 1999",intro,"");
		int u1=checkUrl("http://www.mmbase.org","mmbase homebase on the Internet, download, faq's etc etc ");

		checkRelation(n1,u1,"related");
		
	}

	private void installBasicAuth() {
		// check if the needed relation types are installed
		checkRelDef("authrel","authrel",2,"AuthRel","AuthRel");

		// allow relation between pools and pools
		checkTypeRel("people","typedef","authrel",-1);

		int p1=checkPeople("admin","admin","root@localhost","admin");
		
		TypeDef bul=(TypeDef)mmb.getMMObject("typedef");
		if (bul!=null) {
			int e1=bul.getIntValue("people");
			System.out.println("E="+e1+" P="+p1);
			checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("daymarks");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("fielddef");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("groups");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("insrel");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("jumpers");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("location");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("mmevents");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("mmservers");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("news");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("oalias");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("pools");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("reldef");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			e1=bul.getIntValue("typedef");
			checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("typerel");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("urls");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
			//e1=bul.getIntValue("users");
			//checkAuthRelation(p1,e1,"authrel",1,1,1);	
		} else {
			System.out.println("MMExamples -> can't get typedef builder");
		}
	}


	private void installBasics() {
		// check if the needed relation types are installed
		checkRelDef("related","related",2,"Gerelateerd","Gerelateerd");

		// these should be moved to basic install
		checkJumper("edit","/mmeditors/index.shtml");
		checkJumper("admin","/mmadmin/index.shtml");
	}

	private void installMyYahoo() {
		// check if the needed relation types are installed
		checkRelDef("related","related",2,"Gerelateerd","Gerelateerd");

		// allow relation between pools and pools
		checkTypeRel("pools","pools","related",-1);
		checkTypeRel("pools","urls","related",-1);

		int p1=checkPool("MyYahoo","Our own MyYahoo");
		int p2=checkPool("Music","All the best music links");
		int p3=checkPool("Movies","Some nice movie links");
		int p4=checkPool("Partners","Founding mmbase opensource partners");

		checkRelation(p1,p2,"related");
		checkRelation(p1,p3,"related");
		checkRelation(p1,p4,"related");

		checkJumper("yahoo","/mmadmin/examples/MyYahoo/index.shtml?"+p1);

		// attached a few urls to music pool
		int u1=checkUrl("http://www.3voor12.nl","The best music site on the net");
		int u2=checkUrl("http://www.music.com","Big music portal");
		checkRelation(p2,u1,"related");
		checkRelation(p2,u2,"related");

		// attached a few urls to movie pool
		int u3=checkUrl("http://www.cinemaservice.nl","Dutch movie servers, sends email when your movies are on");
		int u4=checkUrl("http://www.iffrotterdam.nl","Film Festival Rotterdam");
		checkRelation(p3,u3,"related");
		checkRelation(p3,u4,"related");

		// attached a few urls to movie pool
		int u5=checkUrl("http://www.vpro.nl","VPRO - A Dutch Public Broadcaster");
		int u6=checkUrl("http://www.xs4all.nl","XS4ALL - Oldest dutch public ISP");
		int u7=checkUrl("http://www.dds.nl","DDS - Dutch Digital City Amsterdam");
		int u8=checkUrl("http://www.sun.nl","SUN - Sun Microsystems");
		int u9=checkUrl("http://www.nos.nl","NOS - Org. For Dutch Public Brodcasters");
		checkRelation(p4,u5,"related");
		checkRelation(p4,u6,"related");
		checkRelation(p4,u7,"related");
		checkRelation(p4,u8,"related");
		checkRelation(p4,u9,"related");


	}


	private void checkRelDef(String sname, String dname, int dir,String sguiname, String dguiname) {
		MMObjectBuilder bul=mmb.getMMObject("reldef");
		if (bul!=null) {
			Vector res=bul.searchVector("WHERE sname='"+sname+"' AND dname='"+dname+"'");
			if (res!=null && res.size()>0) {
				System.out.println("RefDef ("+sname+","+dname+") allready installed");
			} else {
				MMObjectNode node=bul.getNewNode("system");
				node.setValue("sname",sname);
				node.setValue("dname",dname);
				node.setValue("dir",dir);
				node.setValue("sguiname",sguiname);
				node.setValue("dguiname",dguiname);
				int id=bul.insert("system",node);	
				if (id!=-1) {
					System.out.println("RefDef ("+sname+","+dname+") installed");
				} 
			}
		} else {
			System.out.println("MMExamples -> can't get reldef builder");
		}
	}


	private void checkTypeRel(String sname, String dname, String rname, int count) {
		MMObjectBuilder bul=mmb.getMMObject("typerel");
		if (bul!=null) {
			TypeDef typedef=(TypeDef)mmb.getMMObject("typedef");
			if (typedef==null) {
				System.out.println("MMExamples -> can't get typedef builder");
				return;
			} 
			RelDef reldef=(RelDef)mmb.getMMObject("reldef");
			if (reldef==null) {
				System.out.println("MMExamples -> can't get reldef builder");
				return;
			} 

			// figure out rnumber
			int rnumber=reldef.getGuessedNumber(rname);
			if (rnumber==-1) {
				System.out.println("MMExamples -> no reldef : "+rname+" defined");
				return;
			} 


			// figure out snumber
			int snumber=typedef.getIntValue(sname);
			if (snumber==-1) {
				System.out.println("MMExamples -> no object : "+sname+" defined");
				return;
			} 

			// figure out dnumber
			int dnumber=typedef.getIntValue(dname);
			if (dnumber==-1) {
				System.out.println("MMExamples -> no object : "+dname+" defined");
				return;
			} 


			
			Vector res=bul.searchVector("WHERE snumber="+snumber+" AND dnumber="+dnumber+" AND rnumber="+rnumber);
			if (res!=null && res.size()>0) {
				System.out.println("TypeRel ("+sname+","+dname+","+rname+") allready installed");
			} else {
				MMObjectNode node=bul.getNewNode("system");
				node.setValue("snumber",snumber);
				node.setValue("dnumber",dnumber);
				node.setValue("rnumber",rnumber);
				node.setValue("max",count);
				int id=bul.insert("system",node);	
				if (id!=-1) {
					System.out.println("TypeRel ("+sname+","+dname+","+rname+") installed");
				} 
			}
		} else {
			System.out.println("MMExamples -> can't get typerel builder");
		}
	}


	private int checkPool(String name, String description) {
		MMObjectBuilder bul=mmb.getMMObject("pools");
		if (bul!=null) {
			Vector res=bul.searchVector("WHERE name='"+name+"'");
			if (res!=null && res.size()>0) {
				System.out.println("Pool ("+name+") allready installed");
				MMObjectNode node=(MMObjectNode)res.elementAt(0);
				return(node.getIntValue("number"));
			} else {
				MMObjectNode node=bul.getNewNode("system");
				node.setValue("name",name);
				node.setValue("description",description);
				int id=bul.insert("system",node);	
				if (id!=-1) {
					System.out.println("Pool ("+name+") installed");
					return(id);
				} 
			}
		} else {
			System.out.println("MMExamples -> can't get pools builder");
		}
		return(-1);
	}


	private int checkPeople(String firstname,String lastname,String email,String account) {
		MMObjectBuilder bul=mmb.getMMObject("people");
		if (bul!=null) {
			Vector res=bul.searchVector("WHERE firstname='"+firstname+"' AND lastname='"+lastname+"'");
			if (res!=null && res.size()>0) {
				System.out.println("Person ("+firstname+" , "+lastname+") allready installed");
				MMObjectNode node=(MMObjectNode)res.elementAt(0);
				return(node.getIntValue("number"));
			} else {
				MMObjectNode node=bul.getNewNode("system");
				node.setValue("firstname",firstname);
				node.setValue("lastname",lastname);
				node.setValue("email",email);
				node.setValue("account",account);
				int id=bul.insert("system",node);	
				if (id!=-1) {
					System.out.println("People ("+firstname+" , "+lastname+") installed");
					return(id);
				} 
			}
		} else {
			System.out.println("MMExamples -> can't get people builder");
		}
		return(-1);
	}



	private int checkUrl(String url, String description) {
		MMObjectBuilder bul=mmb.getMMObject("urls");
		if (bul!=null) {
			Vector res=bul.searchVector("WHERE url='"+url+"'");
			if (res!=null && res.size()>0) {
				System.out.println("Url ("+url+") allready installed");
				MMObjectNode node=(MMObjectNode)res.elementAt(0);
				return(node.getIntValue("number"));
			} else {
				MMObjectNode node=bul.getNewNode("system");
				node.setValue("url",url);
				node.setValue("description",description);
				int id=bul.insert("system",node);	
				if (id!=-1) {
					System.out.println("Url ("+url+") installed");
					return(id);
				} 
			}
		} else {
			System.out.println("MMExamples -> can't get urls builder");
		}
		return(-1);
	}


	private void checkJumper(String name, String url) {
		MMObjectBuilder bul=mmb.getMMObject("jumpers");
		if (bul!=null) {
			Vector res=bul.searchVector("WHERE name='"+name+"'");
			if (res!=null && res.size()>0) {
				System.out.println("Jumper ("+name+") allready installed");
				MMObjectNode node=(MMObjectNode)res.elementAt(0);
			} else {
				MMObjectNode node=bul.getNewNode("system");
				node.setValue("name",name);
				node.setValue("id",-1);
				node.setValue("url",url);
				int id=bul.insert("system",node);	
				if (id!=-1) {
					System.out.println("Jumper ("+name+") installed");
				} 
			}
		} else {
			System.out.println("MMExamples -> can't get jumpers builder");
		}
	}


	private int checkNews(String title, String subtitle, String intro, String body) {
		MMObjectBuilder bul=mmb.getMMObject("news");
		if (bul!=null) {
			Vector res=bul.searchVector("WHERE title='"+title+"'");
			if (res!=null && res.size()>0) {
				System.out.println("News ("+title+") allready installed");
				MMObjectNode node=(MMObjectNode)res.elementAt(0);
				return(node.getIntValue("number"));
			} else {
				MMObjectNode node=bul.getNewNode("system");
				node.setValue("title",title);
				node.setValue("subtitle",subtitle);
				node.setValue("intro",intro);
				node.setValue("body",body);
				int id=bul.insert("system",node);	
				if (id!=-1) {
					System.out.println("News ("+title+") installed");
					return(id);
				} 
			}
		} else {
			System.out.println("MMExamples -> can't get news builder");
		}
		return(-1);
	}


	private void checkRelation(int snumber, int dnumber, String rname) {
		MMObjectBuilder bul=mmb.getMMObject("insrel");
		if (bul!=null) {
			RelDef reldef=(RelDef)mmb.getMMObject("reldef");
			if (reldef==null) {
				System.out.println("MMExamples -> can't get reldef builder");
			}
			// figure out rnumber
			int rnumber=reldef.getGuessedNumber(rname);
			if (rnumber==-1) {
				System.out.println("MMExamples -> no reldef : "+rname+" defined");
				return;
			} 
			Vector res=bul.searchVector("WHERE snumber="+snumber+" AND dnumber="+dnumber);
			if (res==null) {
				res=bul.searchVector("WHERE dnumber="+dnumber+" AND snumber="+snumber);
			}
			if (res!=null && res.size()>0) {
				System.out.println("Relation allready installed");
				MMObjectNode node=(MMObjectNode)res.elementAt(0);
			} else {
				MMObjectNode node=bul.getNewNode("system");
				node.setValue("snumber",snumber);
				node.setValue("dnumber",dnumber);
				node.setValue("rnumber",rnumber);
				int id=bul.insert("system",node);	
				if (id!=-1) {
					System.out.println("Relation installed");
				} 
			}
		} else {
			System.out.println("MMExamples -> can't get insrel builder");
		}
	}


	private void checkAuthRelation(int snumber, int dnumber, String rname,int creat, int look, int us) {
		MMObjectBuilder bul=mmb.getMMObject("authrel");
		if (bul!=null) {
			RelDef reldef=(RelDef)mmb.getMMObject("reldef");
			if (reldef==null) {
				System.out.println("MMExamples -> can't get reldef builder");
			}
			// figure out rnumber
			int rnumber=reldef.getGuessedNumber(rname);
			if (rnumber==-1) {
				System.out.println("MMExamples -> no reldef : "+rname+" defined");
				return;
			} 
			Vector res=bul.searchVector("WHERE snumber="+snumber+" AND dnumber="+dnumber);
			if (res==null) {
				res=bul.searchVector("WHERE dnumber="+dnumber+" AND snumber="+snumber);
			}
			if (res!=null && res.size()>0) {
				System.out.println("Auth Relation allready installed");
				MMObjectNode node=(MMObjectNode)res.elementAt(0);
			} else {
				MMObjectNode node=bul.getNewNode("system");
				node.setValue("snumber",snumber);
				node.setValue("dnumber",dnumber);
				node.setValue("rnumber",rnumber);
				node.setValue("creat",creat);
				node.setValue("look",look);
				node.setValue("us",us);
				int id=bul.insert("system",node);	
				if (id!=-1) {
					System.out.println("Auth Relation installed");
				} 
			}
		} else {
			System.out.println("MMExamples -> can't get authrel builder");
		}
	}
}
