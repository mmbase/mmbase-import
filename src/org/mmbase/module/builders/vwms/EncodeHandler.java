/*
$Id: EncodeHandler.java,v 1.5 2000-03-24 14:34:04 wwwtech Exp $

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

$Log: not supported by cvs2svn $
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

import nl.vpro.mmbase.util.media.audio.*;
import nl.vpro.mmbase.util.media.audio.cdtracks.*;
import nl.vpro.mmbase.util.media.audio.audioparts.*;

/**
 * @author Rico Jansen
 * @version $Revision: 1.5 $ $Date: 2000-03-24 14:34:04 $
 */
public class EncodeHandler implements Runnable {

	private String 	classname = getClass().getName();
	private boolean debug = false;
	private void 	debug( String msg ) { System.out.println( classname +":"+ msg ); }

	Thread kicker = null;
	EncodeCop parent;
	public  MMObjectNode node;
	String task;

	public EncodeHandler(EncodeCop parent,String task,MMObjectNode node) {
		if( debug ) debug( "EncodeHandler("+parent+","+task+","+node.getIntValue("number")+")");
		this.parent=parent;
		this.node=node;
		this.task=task;
		init();	
	}

	public void init() {
		if( debug ) debug("init()");
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
		int id=node.getIntValue("number");	
		if( debug ) debug("doNewCdtrack(): started for node("+id+")");

		// called when a new cdtrack is made
		// its task is to create a wav file (rawaudio)
		// by recording it using a cdplayer allready 'claimed'
		// by the user.

		// get the owner of the cdtrack to find the cdplayer he has claimed !
		String owner=node.getStringValue("owner");	
		debug("doNewCdtrack(): control cdplayer claimed by owner("+owner+")");

		cdplayers bul=(cdplayers)parent.Vwms.mmb.getMMObject("cdplayers");	
		MMObjectNode playernode=bul.getClaimedBy(owner);

		// if we have found the player record it
		if (playernode!=null) {
			// create a new RawAudio to signal we have a wav
			MMObjectNode wavnode=addRawAudio(id,RawAudioDef.STATUS_ONDERWEG,RawAudioDef.FORMAT_WAV,RawAudioDef.WAV_MAXSPEED,2); 

			// setup the player & start the player  
			debug("encodeHandler -> "+playernode);
			playernode.setValue("state","record");
			playernode.setValue("info","tracknr="+node.getIntValue("tracknr")+" id="+id);
			playernode.commit();
		

			boolean changed=false;
			MMObjectNode newnode=null;
			while (!changed) {	
				parent.Vwms.mmb.mmc.waitUntilNodeChanged(playernode);
				newnode=bul.getNode(playernode.getIntValue("number"));
				debug("NEWNODE="+newnode);
				String state=newnode.getStringValue("state");
				if (state.equals("waiting")||state.equals("error")) changed=true;
			}

			// asume all went oke for now

			// put wav node in done state
			wavnode.setValue("status",RawAudioDef.STATUS_GEDAAN);
			wavnode.commit();
		
			// create the needed g2 node
			addRawAudio(id,RawAudioDef.STATUS_VERZOEK,RawAudioDef.FORMAT_G2,RawAudioDef.G2_MAXSPEED,2);   
			parent.removeEncodeHandler( this );

		} else {
			debug("EncodeCop -> problem : can't find cdplayer claimed by : "+owner);
		}
	}


	public void doG2Encode() {
		debug("EncodeHandler g2encoder started");

		g2encoders bul=(g2encoders)parent.Vwms.mmb.getMMObject("g2encoders");	

		// MMObjectNode g2node=bul.getNode(2483396);
		// MMObjectNode g2node=bul.getNode(2496582);

		int 	number	= -1;
		String snumber = bul.getNumberFromName("noise1");
		MMObjectNode g2node	= null;

		if( snumber != null && !snumber.equals(""))
		{	
			try
			{
				number = Integer.parseInt( snumber );	
				if( number > 0 )
				{
					g2node=bul.getNode(number);
					if( g2node != null )
					{
						if (!g2node.getStringValue("state").equals("waiting")) {
							snumber = bul.getNumberFromName("beep1");
							if( snumber != null && !snumber.equals(""))
							{
								try
								{	
									number=Integer.parseInt( snumber );
									if( number > 0 )
									{
										g2node=bul.getNode(number);
										if( g2node == null )
										{
											debug("doG2Encode(): ERROR: snumber("+snumber+"), number("+number+") no node found for beep1!");
										}

									} else debug("doG2Encode(): ERROR: snumber("+snumber+"), number("+number+") not a good nodenumber for beeep1!");
									
								} catch( NumberFormatException e ){
									debug("doG2Encode(): ERROR: number("+snumber+") not a real number for beep1!");
								}
							} else debug("doG2Encode(): ERROR: no number("+snumber+") found for beep1!");
						}
					} else debug("doG2Encode(): ERROR: no node found for this number("+number+") for noise1)!");	
				} else debug("doG2Encode(): ERROR: snumber("+snumber+"), number("+number+") not a good nodenumber for noise1!");	
			} catch( NumberFormatException e ) {
				debug("doG2Encode(): ERROR: number("+snumber+") not a real number!");	
			}
		} else debug("doG2Encode(): ERROR: no number("+snumber+") found for noise1!");	


		if( g2node != null )
		{
			if (g2node.getStringValue("state").equals("waiting")) {
				node.setValue("status",RawAudioDef.STATUS_ONDERWEG);
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
	
					debug("NEWNODE="+newnode);
					String state=newnode.getStringValue("state");
					if (state.equals("waiting")||state.equals("error")) changed=true;
				}
	
				// asume all went oke for now
				node.setValue("url","F=/"+id+"/surestream.rm H1=station.vpro.nl");
				node.setValue("status",RawAudioDef.STATUS_GEDAAN);
				node.setValue("cpu","twohigh");
				node.commit();
		
				parent.removeEncodeHandler( this );
			}
			debug("doG2Encode(): EncodeHandler done ");
		}
		else debug("doG2Encode(): ERROR: No encoders found!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
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
