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
	MMExamplesProbe probe=null;

	public void init() {
		mmb=(MMBase)getModule("MMBASEROOT");		
		probe = new MMExamplesProbe(this);
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

		if ((String)vars.get("NAME-MyYahoo")!=null) installApplication("MyYahoo");
		if ((String)vars.get("NAME-Basics")!=null) installApplication("Basics");
		if ((String)vars.get("NAME-BasicAuth")!=null) installApplication("BasicAuth");
	}

	private void installApplication(String applicationname) {
		String path=MMBaseContext.getConfigPath()+("/applications/");
		XMLApplicationReader app=new XMLApplicationReader(path+applicationname+".xml");
		if (app!=null) {
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
						//System.out.println("node allready installed : "+exportnumber);
					} else {
						newnode.setValue("number",-1);
						int localnumber=doKeyMergeNode(newnode);
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

	private int doKeyMergeNode(MMObjectNode newnode) {
		MMObjectBuilder bul=newnode.parent;
		if (bul!=null) {
			String checkQ="";
			Vector vec=bul.getFields();
			for (Enumeration h = vec.elements();h.hasMoreElements();) {
				FieldDefs def=(FieldDefs)h.nextElement();	
				if (def.isKey) {
					int type=def.getDBType();
					String name=def.getDBName();
					if (type==FieldDefs.TYPE_STRING) {
						String value=newnode.getStringValue(name);
						if (checkQ.equals("")) {
							checkQ+=name+"=='"+value+"'";
						} else {
							checkQ+="+"+name+"=='"+value+"'";
						}
					}
				}
			}
			if (!checkQ.equals("")) {
				Enumeration r=bul.search(checkQ);
				if (r.hasMoreElements()) {
					MMObjectNode oldnode=(MMObjectNode)r.nextElement();	
					return(oldnode.getIntValue("number"));
				}

			} else {
				int localnumber=newnode.insert("import");
				return(localnumber);
			} 
		} else {
			System.out.println("Application installer can't find builder for : "+newnode);
		}	
		return(-1);
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
						//System.out.println("node allready installed : "+exportnumber);
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


	private void checkRelDef(String sname, String dname, int dir,String sguiname, String dguiname) {
		MMObjectBuilder bul=mmb.getMMObject("reldef");
		if (bul!=null) {
			Vector res=bul.searchVector("sname=='"+sname+"'+dname=='"+dname+"'");
			if (res!=null && res.size()>0) {
				//System.out.println("RefDef ("+sname+","+dname+") allready installed");
			} else {
				MMObjectNode node=bul.getNewNode("system");
				node.setValue("sname",sname);
				node.setValue("dname",dname);
				node.setValue("dir",dir);
				node.setValue("sguiname",sguiname);
				node.setValue("dguiname",dguiname);
				int id=bul.insert("system",node);	
				if (id!=-1) {
					// System.out.println("RefDef ("+sname+","+dname+") installed");
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


			
			Vector res=bul.searchVector("snumber=="+snumber+"+dnumber=="+dnumber+"+rnumber=="+rnumber);
			if (res!=null && res.size()>0) {
				//System.out.println("TypeRel ("+sname+","+dname+","+rname+") allready installed");
			} else {
				MMObjectNode node=bul.getNewNode("system");
				node.setValue("snumber",snumber);
				node.setValue("dnumber",dnumber);
				node.setValue("rnumber",rnumber);
				node.setValue("max",count);
				int id=bul.insert("system",node);	
				if (id!=-1) {
					//System.out.println("TypeRel ("+sname+","+dname+","+rname+") installed");
				} 
			}
		} else {
			System.out.println("MMExamples -> can't get typerel builder");
		}
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
			Vector res=bul.searchVector("snumber=="+snumber+"+dnumber=="+dnumber);
			if (res==null) {
				res=bul.searchVector("dnumber=="+dnumber+"+snumber=="+snumber);
			}
			if (res!=null && res.size()>0) {
				//System.out.println("Relation allready installed");
				MMObjectNode node=(MMObjectNode)res.elementAt(0);
			} else {
				MMObjectNode node=bul.getNewNode("system");
				node.setValue("snumber",snumber);
				node.setValue("dnumber",dnumber);
				node.setValue("rnumber",rnumber);
				int id=bul.insert("system",node);	
				if (id!=-1) {
					//System.out.println("Relation installed");
				} 
			}
		} else {
			System.out.println("MMExamples -> can't get insrel builder");
		}
	}


	public void probeCall() {
		String path=MMBaseContext.getConfigPath()+("/applications/");
		// new code checks all the *.xml files in builder dir
        	File bdir = new File(path);
		if (bdir.isDirectory()) {
			String files[] = bdir.list();		
			for (int i=0;i<files.length;i++) {
				String aname=files[i];
				if (aname.endsWith(".xml")) {
					XMLApplicationReader app=new XMLApplicationReader(path+aname);
					if (app!=null && app.getApplicationAutoDeploy()) {
						System.out.println("Auto deploy application : "+aname);
						installApplication(aname.substring(0,aname.length()-4));

					}
				}
			}
		}
	}
}
