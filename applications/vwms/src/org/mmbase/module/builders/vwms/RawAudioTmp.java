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

public class RawAudioTmp extends Vwm implements MMBaseObserver {
Hashtable properties;
boolean first=true;

	public RawAudioTmp() {
		System.out.println("RawAudioTmp ready for action");
	}


	public boolean probeCall() {
		System.out.println("RawAudioTmp probe");
		if (first) {
			first=false;
			try {
				Vwms.mmb.addLocalObserver("rawaudios",this);
				System.out.println("RawAudioTmp subscribed");
			} catch (Exception e) {
				System.out.println("RawAudioTmp -> can't load observer");
				e.printStackTrace();
			}
		}
		return(true);
	}

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		System.out.println("RawAudioTmp (remote) -> sees that : "+number+" has changed type="+ctype);
//		return(nodeChanged(number,builder,ctype));
		return(true);
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeChanged(String number,String builder, String ctype) {
		System.out.println("RawAudioTmp (local) -> sees that : "+number+" has changed type="+ctype);
		generateCopyFiles(number,builder,ctype);
		return(true);
	}


	private void generateCopyFiles(String number,String builder,String ctype) {
		
		if (builder.equals("rawaudios")) {
			RawAudios raw=(RawAudios)Vwms.mmb.getMMObject("rawaudios");		
			MMObjectNode fnode;
			String mmserver=Vwms.getMachineName(); 
			String path,service,filename,tpath,pre;
			int status,format;
			
			fnode=raw.getNode(number);
			status=fnode.getIntValue("status");
			format=fnode.getIntValue("format");
			if (status==3 && format!=3) {
				pre=raw.getFullName(fnode);
				service=getProperty(mmserver+":tmpservice");
				tpath=getProperty(mmserver+":dstpath");
				filename=pre.substring(tpath.length(),pre.length());
				insertFile(mmserver,filename,service);
			}
		}
	}

	private void insertFile(String mmserver,String filename,String service) {
		Netfiles bul=(Netfiles)Vwms.mmb.getMMObject("netfiles");		
		MMObjectNode mainnode;

		System.out.println("RawAudioTmp inserting -> "+filename);
		mainnode=bul.getNewNode("RawAudioTmp");
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
		properties.put("sjouw:dstpath","/data");
		properties.put("sjouw:path","/home/data");
		properties.put("sjouw:tmpservice","audio");
	}
}
