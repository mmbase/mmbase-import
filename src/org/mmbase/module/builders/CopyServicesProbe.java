/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;
import java.io.*;

import org.mmbase.module.builders.vwms.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Rico Jansen
 */
public class CopyServicesProbe implements Runnable {

    private static Logger log = Logging.getLoggerInstance(CopyServicesProbe.class.getName()); 

	Thread kicker = null;
	CopyServices parent=null;
	String filename=null;
	MMObjectNode node=null;
	Hashtable properties;

	public CopyServicesProbe(CopyServices parent,MMObjectNode node) {
		this.parent=parent;
		this.node=node;
		init();
	}

	public void init() {
		this.start();	
	}

	/**
	 * Starts the admin Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			kicker = new Thread(this,"CopyServices");
			kicker.setDaemon(true);
			kicker.start();
		}
	}
	
	/**
	 * Stops the admin Thread.
	 */
	public void stop() {
		/* Stop thread */
		kicker.interrupt();
		kicker = null;
	}

	/**
	 * admin probe, try's to make a call to all the maintainance calls.
	 */
	public void run() {  
		String from=node.getStringValue("from");
		String to=node.getStringValue("to");
		String fromroot=node.getStringValue("fromroot");
		String toroot=node.getStringValue("toroot");
		String state=node.getStringValue("state");
		String info=node.getStringValue("info");
		StringTagger tagger=new StringTagger(info);
		MMObjectNode src=null,dst=null;
		String s,t;

		if (state.equals("copy")) {
			try {   
				node.setValue("state","busy");
				node.commit();
				s=(String)tagger.get("FROM");
				if (s!=null) src=parent.getNode(s);
				t=(String)tagger.get("TO");
				if (t!=null) dst=parent.getNode(t);

				filename=src.getStringValue("filename");
				String sshpath=getProperty("sshpath");
				String dstuser=getProperty(to+":user");
				String dsthost=getProperty(to+":host");
				String dstpath=getProperty(to+":path");
				String srcpath=getProperty(from+":path"); // hoe komen we hierachter ?
				SCPcopy scpcopy=new SCPcopy(sshpath,dstuser,dsthost,dstpath);

				scpcopy.copy(srcpath,filename);

				node.setValue("state","waiting"); // either copy result or so
				node.commit();
			} catch (Exception e) {
				node.setValue("state","error");
				node.commit();
				log.error("CopyServicesProbe " + e);
				log.error(Logging.stackTrace(e));
			}
		} else if (state.equals("remove")) {
			try {   
				node.setValue("state","busy");
				node.commit();
				s=(String)tagger.get("FROM");
				if (s!=null) src=parent.getNode(s);
				filename=src.getStringValue("filename");
				String srcpath=getProperty(from+":path"); // hoe komen we hierachter ?
				log.info("remove -> " + srcpath + " " + filename);
				removeFile(srcpath,filename);
				node.setValue("state","waiting"); // either copy result or so
				node.commit();
			} catch (Exception e) {
				node.setValue("state","error");
				node.commit();
				log.error(Logging.stackTrace(e));
			}
		}
	}

	public String getProperty(String key) {
		if (properties==null) initProperties();
		return((String)properties.get(key));
	}

	private void initProperties() {
		properties=new Hashtable();
		properties.put("sshpath","/usr/local/bin");
		properties.put("test1:user","wwwtech");
		properties.put("test1:host","twohigh.vpro.nl");
		properties.put("test1:path","/data");
		properties.put("sjouw:path","/home/data");
	}

	private void removeFile(String path,String name) {
		File f;

		f=new File(path,name);
		if (f.isDirectory()) {
			System.out.println("Removing dir "+f.getPath());
			if (!f.delete()) {
				log.error("Can't delete directory " + f.getPath());
			}
		} else {
			System.out.println("Removing file "+f.getPath());
			if (!f.delete()) {
				log.error("Can't delete file " + f.getPath());
			}
		}
	}
}
