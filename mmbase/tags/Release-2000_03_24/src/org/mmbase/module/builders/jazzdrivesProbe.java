/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @version 5 Jan 1999
 * @author Daniel Ockeloen
 * @author David V van Zeventer
 */
public class jazzdrivesProbe implements Runnable {

	Thread kicker = null;
	jazzdrives parent=null;
	int trackNr=-1;
	String filename=null;
	MMObjectNode node=null;

	public jazzdrivesProbe(jazzdrives parent,MMObjectNode node) {
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
			kicker = new Thread(this,"jazzdrives");
			kicker.start();
		}
	}
	
	/**
	 * Stops the admin Thread.
	 */
	public void stop() {
		/* Stop thread */
		kicker.setPriority(Thread.MIN_PRIORITY);  
		kicker.suspend();
		kicker.stop();
		kicker = null;
	}

	/**
	 * admin probe, try's to make a call to all the maintainance calls.
	 */
	public void run() {
		kicker.setPriority(Thread.MIN_PRIORITY+1);  
		String name=node.getStringValue("name");
		String cdtype=node.getStringValue("cdtype");
		String state=node.getStringValue("state");
		String info=node.getStringValue("info");
		StringTagger tagger=new StringTagger(info);

		if (state.equals("getdir")) {
			System.out.println("JazzDrivesProbe:run(): state='getdir' !!!");
			try {
				// set node busy while getting dir
				node.setValue("state","busy");
				node.commit();

				node.setValue("info",parent.getDir(node));
				// signal we are done
				node.setValue("state","waiting");
				node.commit();			
			}catch (GetDirFailedException gdfe){
				String Exc = "GetDirFailedException ->";
				System.out.println("JazzDrivesProbe:run():"+Exc+gdfe.errval+"  "+gdfe.explanation);
				node.setValue("state","error");
				node.setValue("info",Exc+gdfe.errval+"  "+gdfe.explanation+"\n\r");
				node.commit();
			}
		} 
        	else if (state.equals("copy")) {
			System.out.println("JazzDrivesProbe:run(): state='copy' !!!!");
                        try {
				try {   
					// set node busy while copying
                                	node.setValue("state","busy");
                                	node.commit();

                                	String srcfile=tagger.Value("srcfile");
                                	System.out.println("DEBUG4='"+srcfile+"'");
                                	String number=tagger.Value("id");	//Get AudioParts node id
                                	System.out.println("DEBUG5='"+number+"'");
                                	int inumber=Integer.parseInt(number);

                                	System.out.println("DECODE="+srcfile+" "+number);
                                	String dstfile="/data/audio/wav/"+number+".wav";
                                	MMObjectNode rnode=addRawAudio(inumber,2,3,441000,2);	//Add RawAudio obj to file
					parent.copy(srcfile,dstfile);
					rnode.setValue("status",3);
                                	rnode.commit();
                                	AudioParts bul=(AudioParts)parent.mmb.getMMObject("audioparts");
                                	if (bul!=null){ 
						bul.wavAvailable(""+inumber);	//Add more audiofiles using diff. settings.
                                	}
					node.setValue("state","waiting"); 
                                	node.commit();
				}catch (CopyFailedException cfe){
					String Exc = "CopyFailedException ->";
					System.out.println("JazzDrivesProbe:run():"+Exc+cfe.errval+"  "+cfe.explanation);
					node.setValue("state","error");
					node.setValue("info",Exc+cfe.errval+" "+cfe.explanation+"\n\r");
					node.commit();
				}
                        }catch (Exception e) {
                        }
                }
	}

	public MMObjectNode addRawAudio(int id, int status, int format, int speed, int channels) {
		MMObjectBuilder bul=parent.mmb.getMMObject("rawaudios");
		if (bul!=null) {
			MMObjectNode node=bul.getNewNode("system");		
			node.setValue("id",id);
			node.setValue("status",status);
			node.setValue("format",format);
			node.setValue("speed",speed);
			node.setValue("channels",channels);
			int number=bul.insert("system",node);
			node.setValue("number",number);
			return(node);
		} else {
			return(null);
		}
	}
}
