/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: EncodeHandler.java,v 1.15 2001-02-15 13:28:07 vpro Exp $

$Log: not supported by cvs2svn $
Revision 1.14  2001/01/12 13:27:29  vpro
Davzev: added comment to doNewCdTrack() and fixed debug line in it.

Revision 1.13  2000/08/01 09:53:32  install
changed imports

Revision 1.12  2000/08/01 09:11:32  vpro
Rico: removed CDTrack references

Revision 1.11  2000/06/05 10:56:56  wwwtech
Rico: added support for new 3voor12

Revision 1.10  2000/03/30 13:11:36  wwwtech
Rico: added license

Revision 1.9  2000/03/29 11:04:45  wwwtech
Rob: Licenses changed

Revision 1.8  2000/03/27 16:10:37  wwwtech
Rico: added more refs in Audio/Video builders

Revision 1.6  2000/03/27 15:10:18  wwwtech
Rico: moved VPRO specific objects to nl.vpro

Revision 1.5  2000/03/24 14:34:04  wwwtech
Rico: total recompile

Revision 1.4  2000/03/21 15:36:57  wwwtech
- (marcel) Removed debug (globally declared in MMOBjectNode)

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

import org.mmbase.util.media.audio.*;
import org.mmbase.util.media.audio.audioparts.*;

/**
 * @author Rico Jansen
 * @version $Revision: 1.15 $ $Date: 2001-02-15 13:28:07 $
 */
public class EncodeHandler implements Runnable {

	private String 	classname = getClass().getName();
	private boolean debug = true;
	private void 	debug( String msg ) { System.out.println( classname +":"+ msg ); }

	public boolean waitingForFreeG2Node = false;

	Thread kicker = null;
	EncodeCop parent;
	public  MMObjectNode node;
	String task;

	/**
	 * Constructor initializes EncodeHandler task and related node.
	 */
	public EncodeHandler(EncodeCop parent,String task,MMObjectNode node) {
		if (debug) debug("EncodeHandler("+parent+","+task+","+node+"): Creating/Initializing EncodeHandler");
		this.parent=parent;
		this.task=task;
		this.node=node;
		init();	
	}

	public void init() {
		if (debug) debug("init()");
		this.start();	
	}

	/**
	 * Starts the admin Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			if (debug) debug("start: Creating and starting a new Thread.");
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

	/**
	 * Checks EncoderHandlers task variable to decide what to do.
	 * Currently two tasks exist: newcdtrack and g2encode.
	 * Task newcdtrack got nothing to do with encoding but with ripping a cdtrack.
	 * The second task is g2encode which is the actual encode task for encoding an audiopart.
	 */
	public void run () {
		if (debug) debug("run: Task is:'"+task+"', handling it now.");
		if (task.equals("newcdtrack")) {
			doCDRip();
		} else if (task.equals("g2encode")) {
			doG2Encode();
		} else  {
			debug("run: ERROR, unknown task :"+task);
		}
	}

	/**
	 * Handles the audio file cdripping. The cdplayersnode is retrieved through the owner value, 
	 * which is the same as current audiopart owner value. 
	 * Ripping is signalled by changing cdplayers' state to 'record'.
	 * Also a wav rawaudio node will be created for the wav and will be set to a done state when
	 * recording is done and a g2 rawaudio will be created (creation will startup encode process).
	 * Right now it's assumed that recording succeeds so even when recording fails the rawaudio
	 * will get a done state.
	 */
	public void doCDRip() {
		// node is a reference to an audioparts node.
		int number = node.getIntValue("number");	
		if (debug) debug("doCDRip(): started for node("+number+") which is an audiopart");

		// Get the cdplayersnode through the owner value of the current audiopartnode.
		String owner=node.getStringValue("owner");	
		if (debug) debug("doCDRip(): Getting cdplayer that is claimed by owner: "+owner);
		cdplayers bul=(cdplayers)parent.Vwms.mmb.getMMObject("cdplayers");	
		MMObjectNode cdplayersnode=bul.getClaimedBy(owner);
		if (cdplayersnode!=null) {
			debug("doCDRip(): Found cdplayer "+cdplayersnode.getStringValue("name")+" that is claimed by "+owner);
			// create a new RawAudio to signal we have a wav
			MMObjectNode wavnode=addRawAudio(number,RawAudioDef.STATUS_ONDERWEG,RawAudioDef.FORMAT_WAV,RawAudioDef.WAV_MAXSPEED,2); 

			// Adding the audiopart objectnumber to the cdplayer.info field (which already contains tracknr.)
			debug("doCDRip: Adding id (audiopartobjnr) to the cdplayer.info");
			String info=cdplayersnode.getStringValue("info");
			StringTagger tagger=new StringTagger(info);
			String tracknr=tagger.Value("tracknr");
			if (tracknr!=null) {
				// Set the cdplayers node state to 'record',
				// The node change will eventually signal remote builder to start ripping
				debug("doCDRip(): Setting state to 'record' to rip tracknr "+tracknr+" for id "+number);
				cdplayersnode.setValue("state","record");
				cdplayersnode.setValue("info","tracknr="+tracknr+" id="+number);
			} else {
				debug("doCDRip(): ERROR: Can't get selected tracknr from cdplayers.info field value="+tracknr);
				cdplayersnode.setValue("state","error");
				cdplayersnode.setValue("info","ERROR: Can't get selected tracknr from cdplayers.info field value="+tracknr);
			}
			cdplayersnode.commit();

			// Wait for cdplayersnode to change again to a waiting or error state.
			boolean changed=false;
			MMObjectNode newnode=null;
			while (!changed) {	
				parent.Vwms.mmb.mmc.waitUntilNodeChanged(cdplayersnode);

				newnode=bul.getNode(cdplayersnode.getIntValue("number"));
				if (debug) debug("doCDRip(): waitUntilNodeChanged done, gettingNode: "+newnode);
				String state=newnode.getStringValue("state");
				if (state.equals("waiting")||state.equals("error")) changed=true;
			}

			if (debug) debug("doCDRip: cdplayersnode state has become '"+newnode.getStringValue("state")+"', continuing.");
			// asume all went oke for now

			// put wav node in done state
			if (debug) debug("doCDRip: Assuming all went ok and setting RawAudio wavnode state to: "+RawAudioDef.STATUS_GEDAAN);
			wavnode.setValue("status",RawAudioDef.STATUS_GEDAAN);
			wavnode.commit();
		
			// create the needed g2 RawAudio node
			if (debug) debug("doCDRip(): Creating new Rawaudio node of type G2 with state: "+RawAudioDef.STATUS_VERZOEK+" id:"+number);
			addRawAudio(number,RawAudioDef.STATUS_VERZOEK,RawAudioDef.FORMAT_G2,RawAudioDef.G2_MAXSPEED,2);   
			if (debug) debug("doCDRip(): Removing this EncodeHandler now.");
			parent.removeEncodeHandler(this);
		} else {
			debug("doCDRip(): ERROR: Can't find a cdplayer claimed by owner("+owner+")");
		}
	}


	/**
	 * Handles the Real G2 Encoding of an audiopart.
	 */
	public void doG2Encode() {
		if (debug) debug("doG2Encode(): Started for node("+node.getIntValue("number")+") which is a rawaudio");

		g2encoders bul=(g2encoders)parent.Vwms.mmb.getMMObject("g2encoders");	
		MMObjectNode g2node	= null;

		// wait while no node found .. try x times y secs before giving up...
		g2node = getFreeG2Node(bul);
		if(g2node!=null)
			debug("doG2Enode(): GOOD!: found testnode("+g2node+")");
		else
			debug("doG2Enode(): WARNING: No node found in getFreeG2Node("+bul+")!");

		if (g2node!=null) {
			if (g2node.getStringValue("state").equals("waiting")) {
				if (debug) debug("doG2Encode: Found free g2node, setting RawAudio node state from "+node.getIntValue("status")+" to "+RawAudioDef.STATUS_ONDERWEG);
				node.setValue("status",RawAudioDef.STATUS_ONDERWEG);
				node.commit();
	
				int id=node.getIntValue("id");	
	
				// hack should move to the real encoder to create the dir ?
				// Will be moved to remote builder in next version, davzev
				File file = new File("/data/audio/ra/"+id);
				try {
					if (file.mkdir())
						debug("doG2Encode: Created directory : "+id+" in /data/audio/ra/ to put encoded file in.");
					else 
						debug("doG2Encode: WARNING mkdir() for "+id+" in /data/audio/ra returned false");
				} catch (Exception f) {
					debug("doG2Encode: ERROR Creating directory "+id+" in /data/audio/ra/");
					f.printStackTrace();
				}

				// Encoded files are stored in subdir, name is the id.	
				String params="subdir="+id+" inputname=/data/audio/wav/"+id+".wav outputname=/data/audio/ra/"+id+"/surestream.rm sureStream=true encodeAudio=true forceOverwrite=true audioFormat=\"stereo music\"";
				if (debug) debug("doG2Encode: Changing g2node setting state to 'encode' info to '"+params+"'");
				g2node.setValue("info",params);
				g2node.setValue("state","encode");
				g2node.commit();	
	
				// Wait for g2node to change again to a waiting state.
				boolean changed=false;
				MMObjectNode newnode=null;
				while (!changed) {	
					parent.Vwms.mmb.mmc.waitUntilNodeChanged(g2node);
	
					newnode=bul.getNode(g2node.getIntValue("number"));
					if (debug) debug("doG2Encode: waitUntilNodeChanged done, gettingNode: "+newnode);
					String state=newnode.getStringValue("state");
					if (state.equals("waiting")||state.equals("error")) changed=true;
				}
	
				if (debug) debug("doG2Encode: g2node state has become '"+newnode.getStringValue("state")+"', continuing.");
				if (debug) debug("doG2Encode: Changing RawAudio node url= 'F=/"+id+"/surestream.rm H1=station.vpro.nl' , status= "+RawAudioDef.STATUS_GEDAAN);
				// asume all went oke for now
				node.setValue("url","F=/"+id+"/surestream.rm H1=station.vpro.nl");
				node.setValue("status",RawAudioDef.STATUS_GEDAAN);
				node.setValue("cpu","twohigh");
				node.commit();
				parent.removeEncodeHandler( this );
			}
			debug("doG2Encode(): EncodeHandler done ");
		} else 
			debug("doG2Encode(): ERROR: No encoders found!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}

	/**
	 * Creates a RawAudio node for an audiopart.
	 * @param id the idnumber = audiopartobjectnumber
	 * @param status current state
	 * @param format file format
	 * @param speed bitrate
	 * @param channels amount of audio channels
	 * @return RawAudio node
	 */
	public MMObjectNode addRawAudio(int id,int status,int format,int speed,int channels) {
		RawAudios bul=(RawAudios)parent.Vwms.mmb.getMMObject("rawaudios");	
		MMObjectNode node=bul.getNewNode("system");		
		node.setValue("id",id);
		node.setValue("status",status);
		node.setValue("format",format);
		node.setValue("speed",speed);
		node.setValue("channels",channels);
		bul.insert("system",node);
		return node;
	}

	/**
	 * Get a free g2encoder
	 * This method should wait till a free node is found.
	 * After a notify or when the wait times out, the g2encoders table is queried for a 'waiting' node.
	 * @param builder g2encoders builder.
	 * @return a free g2encoders node.
	 */
	private synchronized MMObjectNode getFreeG2Node(g2encoders builder) {
		if (debug) debug("getFreeG2Node: Searching for the first g2encodersnode having a 'waiting' state."); 
		MMObjectNode result = null;
		int i = 1;
		try {
			while(result == null) {
				Enumeration e = builder.search("WHERE state='waiting'");
				if(e.hasMoreElements()) {
					result = (MMObjectNode)e.nextElement();
				} else {
					debug("getFreeG2Node(): no free node found.. waiting 60 secs.. goodbye!");
					parent.addWaitingEncodeHandler(this);
					wait( (60*1000) ); 
					parent.removeWaitingEncodeHandler(this);
					i++;
					debug("getFreeG2Node(): checking for "+i+" time for free enoder..");
				}	
			}
		} catch(InterruptedException ie) {
			debug("getFreeG2Node(): ERROR: " + ie);		
			ie.printStackTrace();
		}
		if (debug) debug("getFreeG2Node: Found free node "+result);
		return result;
	}

	public synchronized void notifyG2Free() {
		notify();
	}
}
