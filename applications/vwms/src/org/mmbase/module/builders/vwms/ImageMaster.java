/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

/*
$Id: ImageMaster.java,v 1.12 2000-06-05 15:36:39 wwwtech Exp $
$Log: not supported by cvs2svn $
Revision 1.11  2000/06/05 10:56:56  wwwtech
Rico: added support for new 3voor12

*/

package org.mmbase.module.builders.vwms;

import java.util.*;
import java.sql.*;
import java.io.*;

import javax.servlet.http.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;


/**
 * @author Daniel Ockeloen
 */

public class ImageMaster extends Vwm implements MMBaseObserver,VwmServiceInterface {

	Hashtable properties;
	boolean first=true;
	private boolean debug=true;
	Object syncobj=new Object();
	private int maxSweep=16;
	Vector files=new Vector();
	ImagePusher pusher;

	public ImageMaster() {
		debug("ready for action");
	}


	public boolean probeCall() {
		if (first) {
			first=false;
			if (pusher==null) {
				pusher=new ImagePusher(this);
				System.out.println("ImageMaster -> Starting Image pusher");
			}
		} else {
			try {
				Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
				Enumeration e=bul.search("WHERE service='images' AND subservice='main' AND status=1 ORDER BY number DESC");
				int i=0;
				while (e.hasMoreElements() && i<maxSweep) {
					MMObjectNode node=(MMObjectNode)e.nextElement();
					fileChange(""+node.getIntValue("number"),"c");
					i++;
				}
				try { Thread.sleep(1500); } catch(InterruptedException x) {}
				Enumeration f=bul.search("WHERE service='images' AND subservice='mirror' AND status=1 ORDER BY number DESC");
				i=0;
				while (f.hasMoreElements() && i<maxSweep) {
					MMObjectNode node=(MMObjectNode)f.nextElement();
					fileChange(""+node.getIntValue("number"),"c");
					i++;
				}
			} catch(Exception e) {
				debug("probeCall exception "+e);
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
		debug("sees that : "+number+" has changed type="+ctype);
		return(true);
	}

	public boolean fileChange(String service,String subservice,String filename) {
		filename=URLEscape.unescapeurl(filename);
		debug("fileChange -> "+filename);
		// jump to correct subhandles based on the subservice
		if (subservice.equals("main")) {
			handleMainCheck(service,subservice,filename);
		}	
		return(true);
	}

	public boolean fileChange(String number, String ctype) {
		debug("fileChange="+number+" "+ctype);
		// first get the change node so we can see what is the matter with it.
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		MMObjectNode filenode=bul.getNode(number);
		if (filenode!=null) {
			// obtain all the basic info on the file.
			String service=filenode.getStringValue("service");
			String subservice=filenode.getStringValue("subservice");
			int status=filenode.getIntValue("status");

			debug("fileChange "+number+" "+subservice+" "+status);

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
		if (filenode==null) {
			debug("ERROR: handleMirror filenode null!");
			return true;
		}
		
		switch(status) {
			case 1:  // Verzoek
				filenode.setValue("status",2);
				filenode.commit();
				// do stuff
				String filename=filenode.getStringValue("filename");
				if ((filename==null) || filename.equals("")) {
					debug("ERROR handleMirror: filename null");
					return true;
				}
				if (debug) debug("handleMirror"+filename);
				String dstserver=filenode.getStringValue("mmserver");
				
				// save the image to disk
				ImageCaches bul=(ImageCaches)Vwms.mmb.getMMObject("icaches");
				if (bul==null) {
					debug("ERROR: ImageCaches builder is null");
					return true;
				}
			
				String mimetype = "image/jpeg"; // When not overwritten, it will stay on 'jpeg'.

				// get the clear ckey
				String ckey=filename.substring(8);
				int pos=ckey.indexOf(".");
				if (pos!=-1) {
					ckey=ckey.substring(0,pos);
					// We now have a clean ckey ( aka 234242+f(gif) )
					// Get mimetype from ckey params string.
					StringTokenizer st = new StringTokenizer(ckey,"+\n\r");
					Vector ckeyVec = new Vector();
					while (st.hasMoreTokens()) {
						ckeyVec.addElement(st.nextElement());
					}
					Images imagesBuilder = (Images)Vwms.mmb.getMMObject("images");
					if (imagesBuilder==null) {
						debug("ERROR handleMirror images builder not found");
						return true;
					}
					mimetype = imagesBuilder.getImageMimeType(ckeyVec);
					// debug("handleMirror: ckey "+ckey+" has mimetype: "+mimetype);
					ckey=path2ckey(ckey, imagesBuilder);
				}

				debug("verzoek ckey "+ckey);
				byte[] filebuf=bul.getCkeyNode(ckey);
				debug("verzoek size "+filebuf.length);
				String srcpath=getProperty("test1:path"); // hoe komen we hierachter ?
				// Pass mimetype.
				saveImageAsisFile(srcpath,filename,filebuf,mimetype);

				// recover the correct source/dest properties for this mirror
				String sshpath=getProperty("sshpath");
				String dstuser=getProperty(dstserver+":user");
				String dsthost=getProperty(dstserver+":host");
				String dstpath=getProperty(dstserver+":path");

/*
				SCPcopy scpcopy=new SCPcopy(sshpath,dstuser,dsthost,dstpath);

				synchronized(syncobj) {
					scpcopy.copy(srcpath,filename);
				}
*/
				files.addElement(new aFile2Copy(dstuser,dsthost,dstpath,srcpath,filename));

				// remove the tmp image file

				filenode.setValue("status",3);
				filenode.commit();
				break;
			case 2:  // Onderweg
				break;
			case 3:  // Gedaan
				break;
			default:
				debug("ljjljkljkjol error");
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
			default:
				debug("aslfaslfasfasljk error");
				break;
		}
		return(true);
	}

	public boolean doMainRequest(MMObjectNode filenode) {
		debug("doMainRequest for "+filenode.getIntValue("number")+" "+filenode.getStringValue("filename"));
		// so this file has changed probably, check if the file is ready on
		// disk and set the mirrors to dirty/request.
		String filename = filenode.getStringValue("filename");
		String service = filenode.getStringValue("service");
		
		// find and change all the mirror node so they get resend
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		Enumeration e=bul.search("WHERE filename='"+filename+"' AND service='"+service+"' AND subservice='mirror'");
		if (!e.hasMoreElements()) {
			debug("doMainRequest: No mirror nodes found for : "+filenode.toString()+" !!");
		}
		while (e.hasMoreElements()) {
			MMObjectNode mirrornode=(MMObjectNode)e.nextElement();
			if (debug) debug("doMainRequest sending change for "+mirrornode.getIntValue("number"));
			mirrornode.setValue("status",1);
			mirrornode.commit();
		}
		return(true);
	}

	public void handleMainCheck(String service,String subservice,String filename) {
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		Enumeration e=bul.search("WHERE filename='"+filename+"' AND service='"+service+"' AND subservice='"+subservice+"'");
		if (e.hasMoreElements()) {
			if (debug) debug("handleMainCheck: existing file");
			MMObjectNode mainnode=(MMObjectNode)e.nextElement();
			int currentstatus=mainnode.getIntValue("status");
			if (currentstatus>2) { // check only the ones that are done
				mainnode.setValue("status",1);
				mainnode.commit();
			}
		} else {
			if (debug) debug("handleMainCheck: new file");
			MMObjectNode mainnode=bul.getNewNode("system");
			mainnode.setValue("filename",filename);
			mainnode.setValue("mmserver","test1");
			mainnode.setValue("service",service);
			mainnode.setValue("subservice",subservice);
			mainnode.setValue("status",3);
			mainnode.setValue("filesize",-1);
			bul.insert("system",mainnode);	

			// hack hack moet ook mirror nodes aanmaken !
			mainnode=bul.getNewNode("system");
			mainnode.setValue("filename",filename);
			mainnode.setValue("mmserver","omroep");
			mainnode.setValue("service",service);
			mainnode.setValue("subservice","mirror");
			mainnode.setValue("status",1);
			mainnode.setValue("filesize",-1);
			bul.insert("system",mainnode);	
		}
	}

	public String getProperty(String key) {
		if (properties==null) initProperties();
		return((String)properties.get(key));
	}

	private void initProperties() {
		properties=new Hashtable();
		properties.put("sshpath","/usr/local/bin");
		properties.put("omroep:user","vpro");
		properties.put("omroep:host","vpro.omroep.nl");
		properties.put("omroep:path","/bigdisk/htdocs/");
		properties.put("test1:path","/usr/local/log/james/scancache/PAGE");
	}


	private boolean saveImageAsisFile(String path,String filename,byte[] value, String mimetype) {
		String header="Status: 200 OK";
		// header+="\r\nContent-type: image/jpeg";
		header+="\r\nContent-type: "+mimetype;
		header+="\r\nContent-length: "+value.length;
	    header+="\r\n\r\n";

		File sfile = new File(path+filename);
		try {
			DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
			scan.writeBytes(header);
			scan.write(value);
			scan.flush();
			scan.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return(true);
	}


	private String path2ckey(String path, Images imageBuilder) {
		StringTokenizer tok = new StringTokenizer(path,"+\n\r");
		String ckey=tok.nextToken();
		ckey = ""+imageBuilder.convertAlias(ckey);
		while (tok.hasMoreTokens()) {
			String key=tok.nextToken();
			ckey+=key;
		}
		return(ckey);
	}
}
