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
import org.mmbase.module.tools.MMAppTool.*;


/**
 * @author Daniel Ockeloen
 */
public class MMAdmin extends ProcessorModule {

	MMBase mmb=null;
	MMAdminProbe probe=null;
	String lastmsg="";
	private boolean restartwanted=false;

	public void init() {
		mmb=(MMBase)getModule("MMBASEROOT");		
		probe = new MMAdminProbe(this);
	}


	/**
	 */
	public MMAdmin() {
	}

	/**
	 * Generate a list of values from a command to the processor
	 */
	 public Vector getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
    	String line = Strip.DoubleQuote(value,Strip.BOTH);
		StringTokenizer tok = new StringTokenizer(line,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("APPLICATIONS")) return(getApplicationsList());
			if (cmd.equals("BUILDERS")) return(getBuildersList());
			if (cmd.equals("MODULES")) return(getModulesList());
			if (cmd.equals("DATABASES")) return(getDatabasesList());
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
			if (token.equals("SERVERRESTART")) {
				String user=(String)cmds.get(cmdline);
				doRestart(user);
			} else if (token.equals("LOAD")) {
				Versions ver=(Versions)mmb.getMMObject("versions");
				String appname=(String)cmds.get(cmdline);
				String path=MMBaseContext.getConfigPath()+("/applications/");
				XMLApplicationReader app=new XMLApplicationReader(path+appname+".xml");
				if (app!=null) {
					String name=app.getApplicationName();
					String maintainer=app.getApplicationMaintainer();
					int version=app.getApplicationVersion();
					int installedversion=ver.getInstalledVersion(name,"application");
					if (installedversion==-1 || version>installedversion) {
						if (installedversion==-1) {
							System.out.println("Installing application : "+name);
						} else {	
							System.out.println("installing application : "+name+" new version from "+installedversion+" to "+version);
						}
						if (installApplication(name)) {
							lastmsg="Application loaded oke<BR><BR>\n";
							lastmsg+="The application has the following install notice for you : <BR><BR>\n";
							lastmsg+=app.getInstallNotice();
							if (installedversion==-1) {
								ver.setInstalledVersion(name,"application",maintainer,version);
							} else {
								ver.updateInstalledVersion(name,"application",maintainer,version);
							}
						} else {
							System.out.println("Problem installing application : "+name);
						}
					} else { 
							lastmsg="Application was allready loaded (or a higher version)<BR><BR>\n";
							lastmsg+="To remind you here is the install notice for you again : <BR><BR>\n";
							lastmsg+=app.getInstallNotice();
					}
				} else {
					lastmsg="Install error can't find xml file";
				}
			} else if (token.equals("SAVE")) {
				String appname=(String)cmds.get(cmdline);
				String savepath=(String)vars.get("PATH");
				String goal=(String)vars.get("GOAL");
				System.out.println("APP="+appname+" P="+savepath+" G="+goal);
				writeApplication(appname,savepath,goal);
			} else if (token.equals("APPTOOL")) {
				String appname=(String)cmds.get(cmdline);
				startAppTool(appname);
			} else if (token.equals("BUILDERSAVE")) {
				String buildername=(String)cmds.get(cmdline);
				String savepath=(String)vars.get("PATH");
				MMObjectBuilder bul=mmb.getMMObject(buildername);
				if (bul!=null) {
					XMLBuilderWriter.writeXMLFile(savepath,bul);
					lastmsg="Writing finished, no problems.<BR><BR>\n";
					lastmsg+="A clean copy of "+buildername+".xml can be found at : "+savepath+"<BR><BR>\n";
				}
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
			if (cmd.equals("VERSION")) { 
				return(""+getVersion(tok.nextToken()));
			} else if (cmd.equals("DESCRIPTION")) { 
				return(getDescription(tok.nextToken()));
			} else if (cmd.equals("LASTMSG")) { 
				return(lastmsg);
			} else if (cmd.equals("BUILDERVERSION")) { 
				return(""+getBuilderVersion(tok.nextToken()));
			} else if (cmd.equals("BUILDERCLASSFILE")) { 
				return(""+getBuilderClass(tok.nextToken()));
			} else if (cmd.equals("BUILDERDESCRIPTION")) { 
				return(""+getBuilderDescription(tok.nextToken()));
			}
		}
		return("No command defined");
	}

	int getVersion(String appname) {
		String path=MMBaseContext.getConfigPath()+("/applications/");
		XMLApplicationReader app=new XMLApplicationReader(path+appname+".xml");
		if (app!=null) {
			return(app.getApplicationVersion());
		}
		return(-1);
	}


	int getBuilderVersion(String appname) {
		String path=MMBaseContext.getConfigPath()+("/builders/");
		XMLBuilderReader app=new XMLBuilderReader(path+appname+".xml");
		if (app!=null) {
			return(app.getBuilderVersion());
		}
		return(-1);
	}

	String getBuilderClass(String bulname) {
		String path=MMBaseContext.getConfigPath()+("/builders/");
		XMLBuilderReader bul=new XMLBuilderReader(path+bulname+".xml");
		if (bul!=null) {
			return(bul.getClassFile());
		}
		return("");
	}



	String getDescription(String appname) {
		String path=MMBaseContext.getConfigPath()+("/applications/");
		XMLApplicationReader app=new XMLApplicationReader(path+appname+".xml");
		if (app!=null) {
			return(app.getDescription());
		}
		return("");
	}


	String getBuilderDescription(String appname) {
		String path=MMBaseContext.getConfigPath()+("/builders/");
		XMLBuilderReader app=new XMLBuilderReader(path+appname+".xml");
		if (app!=null) {
			Hashtable desc=app.getDescriptions();
			String us=(String)desc.get("us");
			if (us!=null) {
				return(us);
			}
		}
		return("");
	}

	public void maintainance() {
	}

	public void doRestart(String user) {
		lastmsg="Server Reset requested by '"+user+"' Restart in 3 seconds<BR><BR>\n";
		System.out.println("Server Reset requested by '"+user+"' Restart in 3 seconds");
		restartwanted=true;
		probe = new MMAdminProbe(this,3*1000);
	}

	private boolean startAppTool(String appname) {
		String path=MMBaseContext.getConfigPath()+("/applications/");
		System.out.println("Starting apptool with : "+path+"/"+appname+".xml");
		MMAppTool app=new MMAppTool(path+"/"+appname+".xml");
		lastmsg="Started a instance of the MMAppTool with path : <BR><BR>\n";
		lastmsg+=path+"/"+appname+".xml<BR><BR>\n";
		return(true);
	}

	private boolean installApplication(String applicationname) {
		String path=MMBaseContext.getConfigPath()+("/applications/");
		XMLApplicationReader app=new XMLApplicationReader(path+applicationname+".xml");
		if (app!=null) {
			if (areBuildersLoaded(app.getNeededBuilders())) {
				if (checkRelDefs(app.getNeededRelDefs())) {
					if (checkAllowedRelations(app.getAllowedRelations())) {
						if (installDataSources(app.getDataSources(),applicationname)) {
							if (installRelationSources(app.getRelationSources())) {
							} else {
								System.out.println("Application installer stopped : can't install relationsources");
								return(false);
							}
						} else {
							System.out.println("Application installer stopped : can't install datasources");
							return(false);
						}
					} else {
						System.out.println("Application installer stopped : can't install allowed relations");
						return(false);
					}
				} else {
					System.out.println("Application installer stopped : can't install reldefs");
					return(false);
				}
			} else {
				System.out.println("Application installer stopped : not all needed builders present");
				return(false);
			}
		} else {
			System.out.println("Can't install application : "+path+applicationname+".xml");
		}
		return(true);
	}

	boolean installDataSources(Vector ds,String appname) {
		for (Enumeration h = ds.elements();h.hasMoreElements();) {
			Hashtable bh=(Hashtable)h.nextElement();	
			String path=(String)bh.get("path");
			String prepath=MMBaseContext.getConfigPath()+("/applications/");
			XMLNodeReader nodereader=new XMLNodeReader(prepath+path,prepath+appname+"/",mmb);
			
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
			System.out.println("MMAdmin -> can't get reldef builder");
		}
	}


	private void checkTypeRel(String sname, String dname, String rname, int count) {
		MMObjectBuilder bul=mmb.getMMObject("typerel");
		if (bul!=null) {
			TypeDef typedef=(TypeDef)mmb.getMMObject("typedef");
			if (typedef==null) {
				System.out.println("MMAdmin -> can't get typedef builder");
				return;
			} 
			RelDef reldef=(RelDef)mmb.getMMObject("reldef");
			if (reldef==null) {
				System.out.println("MMAdmin -> can't get reldef builder");
				return;
			} 

			// figure out rnumber
			int rnumber=reldef.getGuessedNumber(rname);
			if (rnumber==-1) {
				System.out.println("MMAdmin -> no reldef : "+rname+" defined");
				return;
			} 


			// figure out snumber
			int snumber=typedef.getIntValue(sname);
			if (snumber==-1) {
				System.out.println("MMAdmin -> no object : "+sname+" defined");
				return;
			} 

			// figure out dnumber
			int dnumber=typedef.getIntValue(dname);
			if (dnumber==-1) {
				System.out.println("MMAdmin -> no object : "+dname+" defined");
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
			System.out.println("MMAdmin -> can't get typerel builder");
		}
	}

	private void checkRelation(int snumber, int dnumber, String rname) {
		MMObjectBuilder bul=mmb.getMMObject("insrel");
		if (bul!=null) {
			RelDef reldef=(RelDef)mmb.getMMObject("reldef");
			if (reldef==null) {
				System.out.println("MMAdmin -> can't get reldef builder");
			}
			// figure out rnumber
			int rnumber=reldef.getGuessedNumber(rname);
			if (rnumber==-1) {
				System.out.println("MMAdmin -> no reldef : "+rname+" defined");
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
			System.out.println("MMAdmin -> can't get insrel builder");
		}
	}


	public void probeCall() {
		if (restartwanted) {
			System.exit(0);
		}
		Versions ver=(Versions)mmb.getMMObject("versions");
		if (ver==null) {
			System.out.println("Versions builder not installed, Can't auto deploy apps");
			return;
		}
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
						String name=app.getApplicationName();
						String maintainer=app.getApplicationMaintainer();
						int version=app.getApplicationVersion();
						int installedversion=ver.getInstalledVersion(name,"application");
						if (installedversion==-1 || version>installedversion) {
							if (installedversion==-1) {
								System.out.println("Auto deploy application : "+aname+" started");
							} else {	
								System.out.println("Auto deploy application : "+aname+" new version from "+installedversion+" to "+version);
							}
							if (installApplication(aname.substring(0,aname.length()-4))) {
								if (installedversion==-1) {
									ver.setInstalledVersion(name,"application",maintainer,version);
								} else {
									ver.updateInstalledVersion(name,"application",maintainer,version);
								}
								System.out.println("Auto deploy application : "+aname+" done");
							} else {
								System.out.println("Problem installing application : "+name);
							}
						}

					}
				}
			}
		}
	}

	private boolean	writeApplication(String appname,String targetpath,String goal) {
		String path=MMBaseContext.getConfigPath()+("/applications/");
		XMLApplicationReader app=new XMLApplicationReader(path+appname+".xml");
		Vector savestats=XMLApplicationWriter.writeXMLFile(app,targetpath,goal,mmb);
		lastmsg="Application saved oke<BR><BR>\n";
		lastmsg+="Some statistics on the save : <BR><BR>\n";
		for (Enumeration h = savestats.elements();h.hasMoreElements();) {
			String result=(String)h.nextElement();	
			lastmsg+=result+"<BR><BR>\n";
		}
		return(true);
	}

	Vector getApplicationsList() {
		Versions ver=(Versions)mmb.getMMObject("versions");
		if (ver==null) {
			System.out.println("Versions builder not installed, Can't get to apps");
			return(null);
		}
		Vector results=new Vector();
		
		String path=MMBaseContext.getConfigPath()+("/applications/");
		// new code checks all the *.xml files in builder dir
        	File bdir = new File(path);
		if (bdir.isDirectory()) {
			String files[] = bdir.list();		
			for (int i=0;i<files.length;i++) {
				String aname=files[i];
				if (aname.endsWith(".xml")) {
					XMLApplicationReader app=new XMLApplicationReader(path+aname);
					String name=app.getApplicationName();
					results.addElement(name);
					results.addElement(""+app.getApplicationVersion());
					int installedversion=ver.getInstalledVersion(name,"application");
					if (installedversion==-1) {
						results.addElement("no");
					} else {
						results.addElement("yes (ver : "+installedversion+")");
					}
					results.addElement(app.getApplicationMaintainer());
					boolean autodeploy=app.getApplicationAutoDeploy();
					if (autodeploy) {
						results.addElement("yes");
					} else {
						results.addElement("no");
					}
				}
			}
		}
		return(results);
	}


	Vector getBuildersList() {
		Versions ver=(Versions)mmb.getMMObject("versions");
		if (ver==null) {
			System.out.println("Versions builder not installed, Can't get to builders");
			return(null);
		}
		Vector results=new Vector();
		
		String path=MMBaseContext.getConfigPath()+("/builders/");
		// new code checks all the *.xml files in builder dir
        	File bdir = new File(path);
		if (bdir.isDirectory()) {
			String files[] = bdir.list();		
			for (int i=0;i<files.length;i++) {
				String aname=files[i];
				if (aname.endsWith(".xml")) {
					String name=aname;
					String sname=name.substring(0,name.length()-4);
					XMLBuilderReader app=new XMLBuilderReader(path+aname);
					results.addElement(sname);
					results.addElement(""+app.getBuilderVersion());
					int installedversion=ver.getInstalledVersion(sname,"builder");
					if (installedversion==-1) {
						results.addElement("no");
					} else {
						results.addElement("yes");
					}
					results.addElement(app.getBuilderMaintainer());
				}
			}
		}
		return(results);
	}


	Vector getModulesList() {
		Versions ver=(Versions)mmb.getMMObject("versions");
		if (ver==null) {
			System.out.println("Versions builder not installed, Can't get to builders");
			return(null);
		}
		Vector results=new Vector();
		
		String path=MMBaseContext.getConfigPath()+("/modules/");
		// new code checks all the *.xml files in builder dir
        	File bdir = new File(path);
		if (bdir.isDirectory()) {
			String files[] = bdir.list();		
			for (int i=0;i<files.length;i++) {
				String aname=files[i];
				if (aname.endsWith(".xml")) {
					String name=aname;
					String sname=name.substring(0,name.length()-4);
					XMLBuilderReader app=new XMLBuilderReader(path+aname);
					results.addElement(sname);

					results.addElement("0");
					results.addElement("yes");
					results.addElement("mmbase.org");
					/*
					results.addElement(""+app.getBuilderVersion());
					int installedversion=ver.getInstalledVersion(sname,"builder");
					if (installedversion==-1) {
						results.addElement("no");
					} else {
						results.addElement("yes");
					}
					results.addElement(app.getBuilderMaintainer());
					*/
				}
			}
		}
		return(results);
	}


	Vector getDatabasesList() {
		Versions ver=(Versions)mmb.getMMObject("versions");
		if (ver==null) {
			System.out.println("Versions builder not installed, Can't get to builders");
			return(null);
		}
		Vector results=new Vector();
		
		String path=MMBaseContext.getConfigPath()+("/databases/");
		// new code checks all the *.xml files in builder dir
        	File bdir = new File(path);
		if (bdir.isDirectory()) {
			String files[] = bdir.list();		
			for (int i=0;i<files.length;i++) {
				String aname=files[i];
				if (aname.endsWith(".xml")) {
					String name=aname;
					String sname=name.substring(0,name.length()-4);
					XMLBuilderReader app=new XMLBuilderReader(path+aname);
					results.addElement(sname);

					results.addElement("0");
					results.addElement("yes");
					results.addElement("mmbase.org");
					/*
					results.addElement(""+app.getBuilderVersion());
					int installedversion=ver.getInstalledVersion(sname,"builder");
					if (installedversion==-1) {
						results.addElement("no");
					} else {
						results.addElement("yes");
					}
					results.addElement(app.getBuilderMaintainer());
					*/
				}
			}
		}
		return(results);
	}
}
