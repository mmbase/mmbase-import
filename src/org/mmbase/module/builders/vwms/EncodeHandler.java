/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: EncodeHandler.java,v 1.17 2001-04-10 13:01:37 michiel Exp $

$Log: not supported by cvs2svn $
Revision 1.16  2001/02/28 10:57:26  vpro
Davzev: Removed mkdir() from doG2Encode(), since remotebuilder g2encoders performs it now. Also G2 rawaudio node cpu field gets right value depending on which mmserver the code runs, instead of hardcoded.

Revision 1.15  2001/02/15 13:28:07  vpro
Davzev: Renamed methods comments etc... and most importantly tracknr is already in cdplayers.info field so additional cdrip info like audiopartobj nr is added.

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

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Rico Jansen
 * @version $Revision: 1.17 $ $Date: 2001-04-10 13:01:37 $
 */
public class EncodeHandler implements Runnable {
  
    private static Logger log = Logging.getLoggerInstance(EncodeHandler.class.getName()); 

	public boolean waitingForFreeG2Node = false;

	Thread kicker = null;
	EncodeCop parent;
	public  MMObjectNode node;
	String task;

	/**
	 * Constructor initializes EncodeHandler task and related node.
	 */
	public EncodeHandler(EncodeCop parent,String task,MMObjectNode node) {
		if (log.isDebugEnabled()) {
            log.debug("EncodeHandler(" + parent + "," + task + "," + node + "): Creating/Initializing  EncodeHandler");
        }
		this.parent=parent;
		this.task=task;
		this.node=node;
		init();	
	}

	public void init() {
		log.info("init of EncodeHandler");
		this.start();	
	}

	/**
	 * Starts the admin Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			if (log.isDebugEnabled()) {
                log.debug("start: Creating and starting a new Thread.");
            }
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
		if (log.isDebugEnabled()) {
            log.debug("run: Task is:'" + task + "', handling it now.");
        }
		if (task.equals("newcdtrack")) {
			doCDRip();
		} else if (task.equals("g2encode")) {
			doG2Encode();
		} else  {
			log.error("run: unknown task :"+task);
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
		if (log.isDebugEnabled()) {
            log.debug("doCDRip(): started for node(" + number + ") which is an audiopart");
        }

		// Get the cdplayersnode through the owner value of the current audiopartnode.
		String owner=node.getStringValue("owner");	
		if (log.isDebugEnabled()) {
            log.debug("doCDRip(): Getting cdplayer that is claimed by owner: " + owner);
        }
		cdplayers bul=(cdplayers)parent.Vwms.mmb.getMMObject("cdplayers");	
		MMObjectNode cdplayersnode=bul.getClaimedBy(owner);
		if (cdplayersnode!=null) {
            if (log.isServiceEnabled()) {
                log.service("doCDRip(): Found cdplayer " + cdplayersnode.getStringValue("name") + " that is claimed by " + owner);
            }
			// create a new RawAudio to signal we have a wav
			MMObjectNode wavnode=addRawAudio(number,RawAudioDef.STATUS_ONDERWEG,RawAudioDef.FORMAT_WAV,RawAudioDef.WAV_MAXSPEED,2); 

			// Adding the audiopart objectnumber to the cdplayer.info field (which already contains tracknr.)
			if (log.isServiceEnabled()) {
                log.service("doCDRip: Adding id (audiopartobjnr) to the cdplayer.info");
            }
			String info=cdplayersnode.getStringValue("info");
			StringTagger tagger=new StringTagger(info);
			String tracknr=tagger.Value("tracknr");
			if (tracknr!=null) {
				// Set the cdplayers node state to 'record',
				// The node change will eventually signal remote builder to start ripping
                log.service("doCDRip(): Setting state to 'record' to rip tracknr " + tracknr + " for id " + number);
				cdplayersnode.setValue("state","record");
				cdplayersnode.setValue("info","tracknr=" + tracknr + " id=" + number);
			} else {
				log.error("doCDRip(): Can't get selected tracknr from cdplayers.info field value=" + tracknr);
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
				if (log.isDebugEnabled()) {
                    log.debug("doCDRip(): waitUntilNodeChanged done, gettingNode: " + newnode);
                }
				String state=newnode.getStringValue("state");
				if (state.equals("waiting")||state.equals("error")) changed=true;
			}

			if (log.isDebugEnabled()) {
                log.debug("doCDRip: cdplayersnode state has become '" + newnode.getStringValue("state") + "', continuing.");
            }
			// asume all went oke for now

			// put wav node in done state
			if (log.isDebugEnabled()) {
                log.debug("doCDRip: Assuming all went ok and setting RawAudio wavnode state to: " + RawAudioDef.STATUS_GEDAAN);
            }
			wavnode.setValue("status",RawAudioDef.STATUS_GEDAAN);
			wavnode.commit();
		
			// create the needed g2 RawAudio node
			if (log.isDebugEnabled()) {
                log.debug("doCDRip(): Creating new Rawaudio node of type G2 with state: " + RawAudioDef.STATUS_VERZOEK + " id:"+number);
            }
			addRawAudio(number,RawAudioDef.STATUS_VERZOEK,RawAudioDef.FORMAT_G2,RawAudioDef.G2_MAXSPEED,2);   
			if (log.isDebugEnabled()) {
               log.debug("doCDRip(): Removing this EncodeHandler now.");
            }
			parent.removeEncodeHandler(this);
		} else {
			log.error("doCDRip(): Can't find a cdplayer claimed by owner(" + owner + ")");
		}
	}


	/**
	 * Handles the Real G2 Encoding of an audiopart.
	 */
	public void doG2Encode() {
		if (log.isDebugEnabled()) {
            log.debug("doG2Encode(): Started for node(" + node.getIntValue("number") + ") which is a rawaudio");
        }

		g2encoders bul=(g2encoders)parent.Vwms.mmb.getMMObject("g2encoders");	
		MMObjectNode g2node	= null;

		// wait while no node found .. try x times y secs before giving up...
		g2node = getFreeG2Node(bul);
		if(g2node!=null) {
			log.info("doG2Enode(): found testnode(" + g2node + ")");
		} else {
			log.warn("doG2Enode(): No node found in getFreeG2Node("+bul+")!");
        }
		if (g2node!=null) {
			if (g2node.getStringValue("state").equals("waiting")) {
				if (log.isDebugEnabled()) {
                   log.debug("doG2Encode: Found free g2node, setting RawAudio node state from " + node.getIntValue("status") + " to " + RawAudioDef.STATUS_ONDERWEG);
                }
				node.setValue("status",RawAudioDef.STATUS_ONDERWEG);
				node.commit();
	
				int id=node.getIntValue("id");	
	
				// Encoded files will be stored in subdir, where name is the id nr. (mkdir is done remotely).	
				String params="subdir="+id+" inputname=/data/audio/wav/"+id+".wav outputname=/data/audio/ra/"+id+"/surestream.rm sureStream=true encodeAudio=true forceOverwrite=true audioFormat=\"stereo music\"";
				if (log.isDebugEnabled()) {
                   log.debug("doG2Encode: Changing g2node setting state to 'encode' info to '" + params + "'");
                }
				g2node.setValue("info",params);
				g2node.setValue("state","encode");
				g2node.commit();	
	
				// Wait for g2node to change again to a waiting state.
				boolean changed=false;
				MMObjectNode newnode=null;
				while (!changed) {	
					parent.Vwms.mmb.mmc.waitUntilNodeChanged(g2node);
	
					newnode=bul.getNode(g2node.getIntValue("number"));
					if (log.isDebugEnabled()) {
                        log.debug("doG2Encode: waitUntilNodeChanged done, gettingNode: " + newnode);
                    }
					String state=newnode.getStringValue("state");
					if (state.equals("waiting")||state.equals("error")) changed=true;
				}
	
				if (log.isDebugEnabled()) {
                    log.debug("doG2Encode: g2node state has become '" + newnode.getStringValue("state") + "', continuing.");
				    log.debug("doG2Encode: Changing RawAudio node url= 'F=/" + id + "/surestream.rm H1=station.vpro.nl' , status= " + RawAudioDef.STATUS_GEDAAN);
                }
				// asume all went oke for now
				node.setValue("url","F=/"+id+"/surestream.rm H1=station.vpro.nl");
				node.setValue("status",RawAudioDef.STATUS_GEDAAN);

				// Searching mmserver running this code.
				log.info("doG2Encode: Searching for mmserver that runs this code.");
				Enumeration e=parent.Vwms.mmb.getInsRel().getRelated(parent.wvmnode.getIntValue("number"),"mmservers");
				if (e.hasMoreElements()) {
					MMObjectNode mmserverNode = (MMObjectNode) e.nextElement();
					log.info("doG2Encode: Found mmserverNode: " + mmserverNode);
					node.setValue("cpu",mmserverNode.getStringValue("name"));
				}
				node.commit();
				parent.removeEncodeHandler( this );
			}
			log.info("doG2Encode(): EncodeHandler done ");
		} else {
			log.error("doG2Encode(): No encoders found!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
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
		if (log.isDebugEnabled()) {
            log.debug("getFreeG2Node: Searching for the first g2encodersnode having a 'waiting' state."); 
        }
		MMObjectNode result = null;
		int i = 1;
		try {
			while(result == null) {
				Enumeration e = builder.search("WHERE state='waiting'");
				if(e.hasMoreElements()) {
					result = (MMObjectNode)e.nextElement();
				} else {
					log.debug("getFreeG2Node(): no free node found.. waiting 60 secs.. goodbye!");
					parent.addWaitingEncodeHandler(this);
					wait( (60*1000) ); 
					parent.removeWaitingEncodeHandler(this);
					i++;
					log.debug("getFreeG2Node(): checking for " + i + " time for free enoder..");
				}	
			}
		} catch(InterruptedException ie) {
			log.error("getFreeG2Node(): ERROR: " + ie);		
			log.error(Logging.stackTrace(ie));
		}
		if (log.isDebugEnabled()) {
            log.debug("getFreeG2Node: Found free node " + result);
        }
		return result;
	}

	public synchronized void notifyG2Free() {
		notify();
	}
}
