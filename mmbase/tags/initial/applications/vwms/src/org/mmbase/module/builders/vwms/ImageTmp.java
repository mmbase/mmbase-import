/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import java.sql.*;

import javax.servlet.http.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;

/**
 * @author Rico Jansen
 */

public class ImageTmp extends Vwm implements MMBaseObserver,VwmServiceInterface {

	public ImageTmp() {
		System.out.println("ImageTmp ready for action");
	}


	public boolean probeCall() {
		System.out.println("ImageTmp probe");
		return(true);
	}

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		System.out.println("ImageTmp -> remote node changed "+number);
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		System.out.println("ImageTmp -> local node changed "+number);
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeChanged(String number,String builder, String ctype) {
		System.out.println("ImageTmp -> sees that : "+number+" has changed type="+ctype);
		return(true);
	}

	public boolean fileChange(String service,String subservice,String filename) {
		System.out.println("ImageTmp frontend change -> "+filename);
		// jump to correct subhandles based on the subservice
		if (subservice.equals("tmp")) {
			handleTmpCheck(service,subservice,filename);
		}
		if (subservice.equals("upload")) {
			handleUploadCheck(service,subservice,filename);
		}
		return(true);
	}

	public boolean fileChange(String number, String ctype) {
		// System.out.println("ImageTmp -> fileChange="+number+" "+ctype);
		// first get the change node so we can see what is the matter with it.
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		MMObjectNode filenode=bul.getNode(number);
		if (filenode!=null) {
			// obtain all the basic info on the file.
			String service=filenode.getStringValue("service");
			String subservice=filenode.getStringValue("subservice");
			int status=filenode.getIntValue("status");

			// jump to correct subhandles based on the subservice
			if (subservice.equals("upload")) {
				handleUpload(filenode,status,ctype);
			} else if (subservice.equals("tmp")) {
				handleTmp(filenode,status,ctype);
			}	
		}
		return(true);
	}

	protected boolean handleTmp(MMObjectNode filenode,int status,String ctype) {
		System.out.println("ImageTmp -> handleTmp "+filenode+" : "+status+" "+ctype);
		if (ctype.equals("d")) return(false);
		switch(status) {
			case 1:  // Verzoek
				System.out.println("ImageTmp-> Tmp verzoek");
//				filenode.setValue("status",2);  // Use only when they are *real* operations
//				filenode.commit();
				// do stuff
				// We would copy it from the media (jazz/flashcard etc.)
				// But that has already been done for us.
				// end stuff
				System.out.println("ImageTmp-> doing Tmp stuff");
				filenode.setValue("status",3);
				filenode.commit();
				break;
			case 2:  // Onderweg
				System.out.println("ImageTmp-> Tmp Onderweg");
				break;
			case 3:  // Gedaan
				System.out.println("ImageTmp-> Tmp Done");
				generateUploadNode(filenode);
				break;
		}
		return(true);
	}


	// Problem how do we determine which server gets this ?
	protected boolean handleUpload(MMObjectNode filenode,int status,String ctype) {
		if (ctype.equals("d")) return(false);
		switch(status) {
			case 1:  // Verzoek
				System.out.println("ImageTmp-> main verzoek");
				doUploadRequest(filenode);
				break;
			case 2:  // Onderweg
				System.out.println("ImageTmp-> main Onderweg");
				break;
			case 3:  // Gedaan
				System.out.println("ImageTmp-> main Done");
				// Eleminate TMP files
				cleanupTmp(filenode);
				break;
		}
		return(true);
	}

	protected boolean generateUploadNode(MMObjectNode filenode) {
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		MMObjectNode mainnode;
		String mmserver="test1"; // Destination mmserver PROPERTY

		mainnode=bul.getNewNode("ImageTmp");
		mainnode.setValue("filename",filenode.getStringValue("filename"));
		mainnode.setValue("service",filenode.getStringValue("service"));
		mainnode.setValue("subservice","upload");
		mainnode.setValue("status",1);
		mainnode.setValue("mmserver",mmserver);
		System.out.println("ImageTmp -> inserting "+mainnode);
		bul.insert("ImageTmp",mainnode);
		return(true);
	}

	protected boolean doUploadRequest(MMObjectNode filenode) {
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		CopyServices cp=(CopyServices)Vwms.mmb.getMMObject("cpservices");
		MMObjectNode tnode;
		

		Enumeration e=bul.search("WHERE filename='"+filenode.getStringValue("filename")+"' AND service='"+filenode.getStringValue("service")+"' AND subservice='tmp'");
		if (e.hasMoreElements()) {
			filenode.setValue("status",2);
			filenode.commit();
			while (e.hasMoreElements()) {
				tnode=(MMObjectNode)e.nextElement();
				System.out.println("ImageTmp -> tmp "+tnode);
				// DO COPY
				System.out.println("ImageTmp -> copying ... ");
				cp.copy(tnode,filenode);
				System.out.println("ImageTmp -> end copying ... ");
			}

			filenode.setValue("status",3);
			filenode.commit();
		}
		return(true);
	}

	protected boolean cleanupTmp(MMObjectNode filenode) {
		String filename=filenode.getStringValue("filename");
		String service=filenode.getStringValue("service");
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		MMObjectNode tnode;

		// Find temporary nodes by this file
		Enumeration e=bul.search("WHERE filename='"+filename+"' AND service='"+service+"' AND subservice='tmp'");
		while (e.hasMoreElements()) {
			tnode=(MMObjectNode)e.nextElement();
			System.out.println("ImageTmp -> for us "+tnode);
			// Remove file ?
			CopyServices cp=(CopyServices)Vwms.mmb.getMMObject("cpservices");
//			cp.remove(tnode);
	 		// Remove node (tmp and upload)
//			bul.removeNode(tnode);
//			bul.removeNode(filenode);
		}
		return(true);
	}



	protected void handleTmpCheck(String service,String subservice,String filename) {
	}



	protected void handleUploadCheck(String service,String subservice,String filename) {
	}

}
