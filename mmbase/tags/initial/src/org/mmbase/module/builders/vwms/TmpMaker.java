/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import java.io.*;

import javax.servlet.http.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;



/**
 * @author Rico Jansen
 */

public class TmpMaker extends Vwm {
Hashtable properties;

	public TmpMaker() {
		System.out.println("TmpMaker ready for action");
	}


	public boolean probeCall() {
		System.out.println("TmpMaker probe");
		return(true);
	}

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
//		return(nodeChanged(number,builder,ctype));
		return(true);
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeChanged(String number,String builder, String ctype) {
		System.out.println("TmpMaker -> sees that : "+number+" has changed type="+ctype);
		generateCopyFiles();
		return(true);
	}


	private void generateCopyFiles() {
		MMObjectNode fnode;
		String[] fil;
		String mmserver=Vwms.getMachineName(); 
		String path,service,filename,tpath,pre;
		
		service=getProperty(mmserver+":tmpservice");
		tpath=getProperty(mmserver+":tmppath");
		path=getProperty(mmserver+":path");
		pre=tpath.substring(path.length(),tpath.length());
		fil=getFiles(tpath);
		for (int i=0;i<fil.length;i++) {
			filename=pre+"/"+fil[i];
			System.out.println("TmpMaker inserting "+filename);
			insertFile(mmserver,filename,service);
		}
	}

	private String[] getFiles(String path) {
		String[] v;
		File f=new File(path);
		if (f.isDirectory()) {
			v=f.list();
		} else {
			System.out.println("TmpMaker specified path ("+path+") is not a directory");
			v=new String[0];
		}
		return(v);
	}

	private void insertFile(String mmserver,String filename,String service) {
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		MMObjectNode mainnode;

		mainnode=bul.getNewNode("TmpMaker");
		mainnode.setValue("filename",filename);
		mainnode.setValue("service",service);
		mainnode.setValue("subservice","tmp");
		mainnode.setValue("status",1);
		mainnode.setValue("mmserver",mmserver);
		bul.insert("ImageTmp",mainnode);
	}

	public String getProperty(String key) {
		if (properties==null) initProperties();
		return((String)properties.get(key));
	}

	private void initProperties() {
		properties=new Hashtable();
		properties.put("sjouw:tmppath","/home/data/import/images/pccard2");
		properties.put("sjouw:path","/home/data");
		properties.put("sjouw:tmpservice","images");
	}
}
