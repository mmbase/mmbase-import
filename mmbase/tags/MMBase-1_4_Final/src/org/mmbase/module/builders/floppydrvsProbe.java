/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * admin module, keeps track of all the worker pools
 * and adds/kills workers if needed (depending on
 * there load and info from the config module).
 *
 * @version 22 Jan 1999
 * @author Daniel Ockeloen
 * @author David V van Zeventer
 */
public class floppydrvsProbe implements Runnable {

    static Logger log = Logging.getLoggerInstance(floppydrvsProbe.class.getName()); 

	Thread kicker = null;
	floppydrvs parent=null;
	int trackNr=-1;
	String filename=null;
	MMObjectNode node=null;

	public floppydrvsProbe(floppydrvs parent,MMObjectNode node) {
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
			kicker = new Thread(this,"floppydrives");
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
            if (log.isDebugEnabled()) {
                log.debug("run(): state='getdir' !!!");
            }
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
                if (log.isDebugEnabled()) {
                    log.debug("run():"+Exc+gdfe.errval+"  "+gdfe.explanation);
                }
				node.setValue("state","error");
				node.setValue("info",Exc+gdfe.errval+"  "+gdfe.explanation+"\n\r");
				node.commit();
			}
		} 
        	else if (state.equals("copy")) {
                log.debug("run(): state='copy' !!!!");
                        try {
				try {   
					// set node busy while copying
                                	node.setValue("state","busy");
                                	node.commit();

                                	String srcfile=tagger.Value("srcfile");
                                	log.debug("DEBUG4='"+srcfile+"'");
                                	String number=tagger.Value("id");  //Get AudioParts node id
                                	log.debug("DEBUG5='"+number+"'");
                                	int inumber=Integer.parseInt(number);
                                	log.debug("DECODE="+srcfile+" "+number);
                                	String dstfile="/data/audio/wav/"+number+".wav";
                                	MMObjectNode rnode=addRawAudio(inumber,2,3,441000,2);  //Add RawAudio obj to file
					parent.copy(srcfile,dstfile);
					rnode.setValue("status",3); 
					rnode.commit();
                                	AudioParts bul=(AudioParts)parent.mmb.getMMObject("audioparts");
					/* removed temp to allow compile, Daniel (marcel this is not ported ?)
                                	if (bul!=null){ 
						bul.wavAvailable(""+inumber);  //Add more audiofiles using diff. settings.
                                	}
					*/
					node.setValue("state","waiting"); 
					node.commit();
				}catch (CopyFailedException cfe){
					String Exc = "CopyFailedException ->";
					log.error("run():"+Exc+cfe.errval+"  "+cfe.explanation);
					node.setValue("state","error");
					node.setValue("info",Exc+cfe.errval+" "+cfe.explanation+"\n\r");
					node.commit();
				}
                        }catch (Exception e) {
                        }
                }
	}

	public MMObjectNode addRawAudio(int id,int status,int format,int speed,int channels) {
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
