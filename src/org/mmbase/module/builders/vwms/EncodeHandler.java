/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders.vwms;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.*;

/**
 * @author Rico Jansen
 */
public class EncodeHandler implements Runnable {

	Thread kicker = null;
	EncodeCop parent;
	MMObjectNode node;
	String task;

	public EncodeHandler(EncodeCop parent,String task,MMObjectNode node) {
		this.parent=parent;
		this.node=node;
		this.task=task;
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
			kicker = new Thread(this,"EncodeHandler");
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

	public void run () {
		if (task.equals("g2encode")) {
			doG2Encode();
		} else if (task.equals("newcdtrack")) {
			doNewCdTrack();
		}
	}

	public void doNewCdTrack() {
		// called when a new cdtrack is made
		// its task is to create a wav file (rawaudio)
		// by recording it using a cdplayer allready 'claimed'
		// by the user.

		System.out.println("EncodeHandler newcdtrack started");

		// get the cdtrack id that will be used in the rawaudio node
		int id=node.getIntValue("number");	

		// get the owner of the cdtrack to find the cdplayer he has claimed !
		String owner=node.getStringValue("owner");	
		System.out.println("EncodeCop -> hunt cdplayer claimed by : "+owner);
		cdplayers bul=(cdplayers)parent.Vwms.mmb.getMMObject("cdplayers");	
		MMObjectNode playernode=bul.getClaimedBy(owner);

		// if we have found the player record it
		if (playernode!=null) {
			// create a new RawAudio to signal we have a wav
			MMObjectNode wavnode=addRawAudio(id,2,3,441000,2); 

			// setup the player & start the player  
			System.out.println("encodeHandler -> "+playernode);
			playernode.setValue("state","record");
			playernode.setValue("info","tracknr="+node.getIntValue("tracknr")+" id="+id);
			playernode.commit();
		

			boolean changed=false;
			MMObjectNode newnode=null;
			while (!changed) {	
				parent.Vwms.mmb.mmc.waitUntilNodeChanged(playernode);
				newnode=bul.getNode(playernode.getIntValue("number"));
				System.out.println("NEWNODE="+newnode);
				String state=newnode.getStringValue("state");
				if (state.equals("waiting")||state.equals("error")) changed=true;
			}

			// asume all went oke for now

			// put wav node in done state
			wavnode.setValue("status",3);
			wavnode.commit();
		
			// create the needed g2 node
			addRawAudio(id,1,6,96000,2);   

		} else {
			System.out.println("EncodeCop -> problem : can't find cdplayer claimed by : "+owner);
		}
	}


	public void doG2Encode() {
		System.out.println("EncodeHandler g2encoder started");

		g2encoders bul=(g2encoders)parent.Vwms.mmb.getMMObject("g2encoders");	
		MMObjectNode g2node=bul.getNode(2483396);
		if (!g2node.getStringValue("state").equals("waiting")) {
			g2node=bul.getNode(2477342);
		}
		if (g2node.getStringValue("state").equals("waiting")) {
			node.setValue("status",2);
			node.commit();

			int id=node.getIntValue("id");	

			// hack should move to the real encoder to create the dir ?

			File file = new File("/data/audio/ra/"+id);
			try {
				if (file.mkdir()) {
				}
			} catch (Exception f) {
			}
	
			String params="inputname=/data/audio/wav/"+id+".wav outputname=/data/audio/ra/"+id+"/surestream.rm sureStream=true encodeAudio=true forceOverwrite=true audioFormat=\"stereo music\"";
			g2node.setValue("info",params);
			g2node.setValue("state","encode");
			g2node.commit();	

			boolean changed=false;
			MMObjectNode newnode=null;
			while (!changed) {	
				parent.Vwms.mmb.mmc.waitUntilNodeChanged(g2node);

				newnode=bul.getNode(g2node.getIntValue("number"));

				System.out.println("NEWNODE="+newnode);
				String state=newnode.getStringValue("state");
				if (state.equals("waiting")||state.equals("error")) changed=true;
			}

			// asume all went oke for now
			node.setValue("url","F=/"+id+"/surestream.rm H1=station.vpro.nl");
			node.setValue("status",3);
			node.setValue("cpu","twohigh");
			node.commit();
		}
		System.out.println("EncodeHandler done ");
	}

	public MMObjectNode addRawAudio(int id, int status, int format, int speed, int channels) {
		RawAudios bul=(RawAudios)parent.Vwms.mmb.getMMObject("rawaudios");	
		MMObjectNode node=bul.getNewNode("system");		
		node.setValue("id",id);
		node.setValue("status",status);
		node.setValue("format",format);
		node.setValue("speed",speed);
		node.setValue("channels",channels);
		bul.insert("system",node);
		return(node);
	}
}
