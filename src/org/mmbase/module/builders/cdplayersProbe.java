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
 * @version 28 Nov 1998
 * @author Daniel Ockeloen
 */
public class cdplayersProbe implements Runnable {

	Thread kicker = null;
	cdplayers parent=null;
	int trackNr=-1;
	String filename=null;
	MMObjectNode node=null;

	public cdplayersProbe(cdplayers parent,MMObjectNode node) {
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
			kicker = new Thread(this,"cdplayer");
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
	public void run () {
		kicker.setPriority(Thread.MIN_PRIORITY+1);  
		String name=node.getStringValue("name");
		String cdtype=node.getStringValue("cdtype");
		String state=node.getStringValue("state");
		String info=node.getStringValue("info");
		StringTagger tagger=new StringTagger(info);

		if (state.equals("record")) {
			try {
				String tr=tagger.Value("track");
				int tracknr=Integer.parseInt(tr);
				String number=tagger.Value("id");
				int inumber=Integer.parseInt(number);

				System.out.println("CDplayer -> DECODE="+tracknr+" "+number);
				node.setValue("state","busy");
				node.commit();	
				String filename="/data/audio/wav/"+number+".wav";
				MMObjectNode rnode=addRawAudio(inumber,2,3,441000,2);   
				parent.recordTrack(tracknr,filename);
				rnode.setValue("status",3);
				rnode.commit();
				CDTracks bul=(CDTracks)parent.mmb.getMMObject("cdtracks");
				if (bul!=null) bul.wavAvailable(inumber);
				node.setValue("state","waiting");
				node.commit();	
			} catch (Exception e) {
			}
		} else if (state.equals("getdir")) {
			node.setValue("state","busy");
			node.commit();	
			node.setValue("info",parent.getCDInfo());
			node.setValue("state","waiting");
			node.commit();	
		} else {
			System.out.println("CDPlayers got a action : "+state+" is does not handle");
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
