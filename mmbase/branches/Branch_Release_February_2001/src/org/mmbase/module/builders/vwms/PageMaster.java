/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import java.sql.*;
import javax.servlet.http.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;
import org.mmbase.module.gui.html.*;

/**
 * @author Daniel Ockeloen
 */

public class PageMaster extends Vwm implements MMBaseObserver,VwmServiceInterface {

	//Hashtable properties;
	boolean first=true;
	private boolean debug=false;
	Object syncobj=new Object();
	Queue files2copy=new Queue(128);
	FileCopier filecopier=new FileCopier(files2copy);
	Vector mirrornodes;

	public PageMaster() {
		debug("ready for action");
	}


	public boolean probeCall() {
		if (first) {
			first=false;
		} else {
			try {
				Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
				//Enumeration e=bul.search("WHERE service='pages' AND subservice='main' AND status=1 ORDER BY number DESC");
				Enumeration e=bul.search("service=='pages'+subservice=='main'+status=1");
				int i=0;
				while (e.hasMoreElements() && i<10) {
					MMObjectNode node=(MMObjectNode)e.nextElement();
					fileChange(""+node.getIntValue("number"),"c");
					i++;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}	
			try {
				Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
				Enumeration e=bul.search("service=='pages'+subservice=='mirror'+status=1");
				//Enumeration e=bul.search("WHERE service='pages' AND subservice='mirror' AND status=1 ORDER BY number DESC");
				int i=0;
				while (e.hasMoreElements() && i<50) {
					MMObjectNode node=(MMObjectNode)e.nextElement();
					fileChange(""+node.getIntValue("number"),"c");
					i++;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}	
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
		// debug("sees that : "+number+" has changed type="+ctype);
		return(true);
	}

	public boolean fileChange(String service,String subservice,String filename) {
		debug("frontend change -> "+filename);
		System.out.println("s="+service+" sub="+subservice+"file="+filename);
		// jump to correct subhandles based on the subservice
		if (subservice.equals("main")) {
			handleMainCheck(service,subservice,filename);
		}	
		return(true);
	}

	public boolean fileChange(String number, String ctype) {
		// debug("fileChange="+number+" "+ctype);
		// first get the change node so we can see what is the matter with it.
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		MMObjectNode filenode=bul.getNode(number);
		if (filenode!=null) {
			// obtain all the basic info on the file.
			String service=filenode.getStringValue("service");
			String subservice=filenode.getStringValue("subservice");
			int status=filenode.getIntValue("status");

			// jump to correct subhandles based on the subservice
			if (subservice.equals("main")) {
				handleMain(filenode,status,ctype);
			} else if (subservice.equals("mirror")) {
				handleMirror(filenode,status,ctype);
			}	
		}
		return(true);
	}

	public boolean handleMirror(MMObjectNode filenode,int status,String ctype) {
		switch(status) {
			case 1:  // Verzoek
				filenode.setValue("status",2);
				filenode.commit();
				// do stuff
				String filename=filenode.getStringValue("filename");
				String dstserver=filenode.getStringValue("mmserver");

				// recover teh correct source/dest properties for this mirror
				String sshpath=getProperty("demoserver","sshpath");
				String srcpath=getProperty("demoserver","path"); // hoe komen we hierachter ?
				String dstuser=getProperty(dstserver,"user");
				String dsthost=getProperty(dstserver,"host");
				String dstpath=getProperty(dstserver,"path");

				System.out.println("sshpath="+sshpath);
				System.out.println("srcpath="+srcpath);
				System.out.println("dstuser="+dstuser);
				System.out.println("dsthost="+dsthost);
				System.out.println("dstpath="+dstpath);
/*
				SCPcopy scpcopy=new SCPcopy(sshpath,dstuser,dsthost,dstpath);

				synchronized(syncobj) {
					scpcopy.copy(srcpath,filename);
				}
*/
				files2copy.append(new aFile2Copy(dstuser,dsthost,dstpath,srcpath,filename,sshpath));

				filenode.setValue("status",3);
				filenode.commit();
				break;
			case 2:  // Onderweg
				break;
			case 3:  // Gedaan
				break;
		}
		return(true);
	}


	public boolean handleMain(MMObjectNode filenode,int status,String ctype) {
		switch(status) {
			case 1:  // Verzoek
				filenode.setValue("status",2);
				filenode.commit();
				// do stuff
				doMainRequest(filenode);
				filenode.setValue("status",3);
				filenode.commit();
				break;
			case 2:  // Onderweg
				break;
			case 3:  // Gedaan
				break;
			case 4:  // Dirty
				filenode.setValue("status",5);
				filenode.commit();
				break;
			case 5:  // Recalc
				String filename=filenode.getStringValue("filename");
				calcPage(filename);
				filenode.setValue("status",1);
				filenode.commit();
				break;
		}
		return(true);
	}

	public boolean doMainRequest(MMObjectNode filenode) {
		// so this file has changed probably, check if the file is ready on
		// disk and set the mirrors to dirty/request.
		String filename = filenode.getStringValue("filename");
		
		// find and change all the mirror node so they get resend
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		Enumeration e=bul.search("WHERE filename='"+filename+"' AND service='pages' AND subservice='mirror'");
		while (e.hasMoreElements()) {
			MMObjectNode mirrornode=(MMObjectNode)e.nextElement();
			mirrornode.setValue("status",1);
			mirrornode.commit();
		}
		return(true);
	}

	public void handleMainCheck(String service,String subservice,String filename) {
		System.out.println("Reached handleMainCheck");
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		Enumeration e=bul.search("WHERE filename='"+filename+"' AND service='"+service+"' AND subservice='"+subservice+"'");
		if (e.hasMoreElements()) {
			MMObjectNode mainnode=(MMObjectNode)e.nextElement();
			mainnode.setValue("status",1);
			mainnode.commit();
		} else {
			MMObjectNode mainnode=bul.getNewNode("system");
			mainnode.setValue("filename",filename);
			mainnode.setValue("mmserver",Vwms.mmb.getMachineName());
			mainnode.setValue("service",service);
			mainnode.setValue("subservice",subservice);
			mainnode.setValue("status",1);
			mainnode.setValue("filesize",-1);
			bul.insert("system",mainnode);	

			Enumeration f=getMirrorNodes(service).elements();
			while (f.hasMoreElements()) {
				MMObjectNode n2=(MMObjectNode)f.nextElement();
				// hack hack moet ook mirror nodes aanmaken !
				mainnode=bul.getNewNode("system");
				mainnode.setValue("filename",filename);
				mainnode.setValue("mmserver",n2.getStringValue("name"));
				mainnode.setValue("service",service);
				mainnode.setValue("subservice","mirror");
				mainnode.setValue("status",3);
				mainnode.setValue("filesize",-1);
				bul.insert("system",mainnode);	
			}
		}
	}

	public String getProperty(String machine,String key) {
		MMServers mmservers=(MMServers)Vwms.mmb.getMMObject("mmservers");		
		return(mmservers.getMMServerProperty(machine,key));
	}


  	public void calcPage(String url) { 
		scanparser m=(scanparser)Vwms.mmb.getBaseModule("SCANPARSER");
		url=url.substring(0,url.length()-5);
		url=url.replace(':','?');
		debug("getPage="+url);
		if (m!=null) {
			scanpage sp=new scanpage();
			m.calcPage(url,sp,0);
		}
	}

	public Vector getMirrorNodes(String service) {
		if (mirrornodes!=null) return(mirrornodes);
		NetFileSrv bul=(NetFileSrv)Vwms.mmb.getMMObject("netfilesrv");		
		if (bul!=null) {
			Enumeration e=bul.search("service=='pages'+subservice=='mirror'");
			if (e.hasMoreElements()) {
				MMObjectNode n1=(MMObjectNode)e.nextElement();
				mirrornodes=n1.getRelatedNodes("mmservers");		
				if (mirrornodes!=null) return(mirrornodes);
			}
		}
		mirrornodes=new Vector();
		return(mirrornodes);
	}
}
