/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: EncodeHandler.java,v 1.13 2000-08-01 09:53:32 install Exp $

$Log: not supported by cvs2svn $
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
 * @version $Revision: 1.13 $ $Date: 2000-08-01 09:53:32 $
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
			String stracknr = (String) node.getValue("info");

			debug("doNewCdtrack(): found playernode("+node+"), tracknr("+stracknr+")");
			playernode.setValue("state","record");
			playernode.setValue("info","tracknr="+stracknr+" id="+id);
			playernode.commit();

			boolean changed=false;
			MMObjectNode newnode=null;
			while (!changed) {	
				parent.Vwms.mmb.mmc.waitUntilNodeChanged(playernode);
				newnode=bul.getNode(playernode.getIntValue("number"));
				debug("doNewCdtrack(): newnode("+newnode+")");
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
			debug("doNewCdtrack(): ERROR: Can't find cdplayer claimed by  owner("+owner+")");
		}
	}


	public void doG2Encode() {
		debug("doG2Encode(): started");

		g2encoders bul=(g2encoders)parent.Vwms.mmb.getMMObject("g2encoders");	
		MMObjectNode g2node	= null;

		// the very old way (this is *not* good :)
		// ---------------------------------------
		// MMObjectNode g2node=bul.getNode(2483396);
		// MMObjectNode g2node=bul.getNode(2496582);

		// -------------------------------------------

		// wait while no node found .. try x times y secs before giving up...

		g2node = getFreeG2Node(bul);
		if( debug ) {
			if( g2node != null )
				debug("doG2Enode(): GOOD!: found testnode("+g2node+")");
			else
				debug("doG2Enode(): WARNING: No node found in getFreeG2Node("+bul+")!");
		}

		// -------------------------------------------
/*
		int 	number	= -1;
		String snumber = bul.getNumberFromName("noise1");

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
*/

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


	/**
	*	Get a free g2encoder
	*
	* 	This method should wait till a free node is found.
	* 
	*/
	private synchronized MMObjectNode getFreeG2Node( g2encoders builder ) {
		MMObjectNode result = null;
		int i = 1;
		try {
			while( result == null ) {
				Enumeration e = builder.search( "WHERE state='waiting'" );
				if( e.hasMoreElements() ) {
					result = (MMObjectNode)e.nextElement();
				} else {
					debug("getFreeG2Node(): no free node found.. waiting 60 secs.. goodbye!");
					parent.addWaitingEncodeHandler( this );
					wait( (60*1000) ); 
					parent.removeWaitingEncodeHandler( this );
					i++;
					debug("getFreeG2Node(): checking for "+i+" time for free enoder..");
				}	
			}
		} catch( InterruptedException e ) {
			debug("getFreeG2Node(): ERROR: " + e);		
		}
		return result;
	}

	public synchronized void notifyG2Free() {
		notify();
	}
}
