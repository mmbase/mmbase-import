/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders.vwms;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.*;
import org.mmbase.util.media.audio.*;
import org.mmbase.util.media.audio.audioparts.*;
import org.mmbase.util.logging.*;

/**
 * When an audiopart has to be ripped or when ripped audio has to be encoded,
 * an EncodeHandler is created to control this process.
 * Each EncodeHandler has a task describing what it must do. 
 * The node field contains a reference to nodes (audiopart or rawaudio) used when 
 * controlling the services.
 * After initializing the service, the EncodeHandler starts the service by putting it in 
 * a certain state, and then waits for it to finish.
 *
 * When the service finishes the EncodeHandler checks the result value stored in info field 
 * which is equal to the proces exitvalue.
 * If this is equal to 0, the RawAudio representing the data created by service 
 * (eg. wav or realaudio) is set in a state ready for usage and the service will be reset.
 * Otherwise the EncodeHandler quits and removes itself from list of running
 * EncodeHandlers and leaves the service in current state.
 * 
 * Also there are EncodeHandlers used to recover for when the mmbase is reset when service
 * is still busy or just finished.
 *
 * @author Rico Jansen?, Daniel?, David van Zeventer
 * @version $Revision: 1.18 $ $Date: 2001-04-20 14:39:35 $
 */
public class EncodeHandler implements Runnable {
	private static Logger log = Logging.getLoggerInstance(EncodeCop.class.getName());
	
	private String 	classname = getClass().getName();
	private boolean debug = true;
	private void 	debug( String msg ) { System.out.println( classname +":"+ msg ); }

	public boolean waitingForFreeG2Node = false;

	Thread kicker = null;
	EncodeCop parent;
	public  MMObjectNode node;
	String task;

	// Hardcoded hostname where surestreams are stored, will move to prop/cfg file soon.
	public final static String RAWAUDIO_URL_HOST1 = "station.vpro.nl";

	/**
	 * Constructor initializes EncodeHandler task and related node.
	 * @param parent EncodeCop reference.
	 * @param task name of the task.
	 * @param node is a reference to either a audioparts or rawaudio node (type is encoded audio eg. g2).
	 */
	public EncodeHandler(EncodeCop parent,String task,MMObjectNode node) {
		log.info("("+parent+","+task+","+node+"): Creating/Initializing EncodeHandler");
		this.parent=parent;
		this.task=task;
		this.node=node; // node is a reference to either a audioparts or a rawaudio node.
		init();	
	}

	public void init() {
		log.debug("Calling this.start()");
		this.start();	
	}

	/**
	 * Starts the admin Thread.
	 */
	public void start() {
		/* Start up the main thread */
		if (kicker == null) {
			log.debug("Creating and starting a new Thread.");
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
	 * Currently there are two type of tasks: service tasks and 'recover for service' tasks.
	 * The servicetasks are tasks for CDRipping 'newcdtrack' and Encoding 'g2encode'.
	 * The recovertasks perform recovery for when the mmbaseserver is being reset when service is busy 
	 * or when service just finished. 
	 */
	public void run () {
		log.info("Task is:'"+task+"', handling it now.");
		if (task.equals("newcdtrack")) {
			doCDRip();
		} else if (task.equals("g2encode")) {
			doG2Encode();
        } else {
            try {
				if (task.startsWith("recoverbusyg2encoder")) {
					// Recover taskname syntax = 'recoverbusyg2encoder:g2encnumber'
					 doRecoverBusyG2Encoder(Integer.parseInt(task.substring(task.indexOf(":")+1)));
				} else if (task.startsWith("recoverbusycdrip")) {
					// Recover taskname syntax = 'recoverbusycdrip:cdplayersnumber'
					doRecoverBusyCDRip(Integer.parseInt(task.substring(task.indexOf(":")+1)));
				} else if (task.startsWith("recoverfinishedg2encoder")) {
					// Recover taskname syntax = 'recoverfinishedg2encoder:g2encnumber'
					doRecoverFinishedG2Encoder(Integer.parseInt(task.substring(task.indexOf(":")+1)));
				} else if (task.startsWith("recoverfinishedcdrip")) {
					// Recover taskname syntax = 'recoverfinishedcdrip:cdplayersnumber'
					doRecoverFinishedCDRip(Integer.parseInt(task.substring(task.indexOf(":")+1)));
				} else {
					debug("run: ERROR, unknown task :"+task);
				}
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }
		}
	}

	/**
	 * Handles the audio file cdripping. First we search the ripping cdplayer by checking which owner claimed it.
	 * This is the same as the owner of the audiopart for which this EncodeHandler is created.
	 * Ripping is signalled by changing cdplayers' state to 'record'.
	 * The ripped audio is represented by a rawaudio of type wav, which gets a 'done' state when ripping is done.
	 * When ripping is done and all went ok, we finish the ripped rawaudio and reset the cdplayer 
	 * and create a rawaudio that represents the encoded audio, its creating will trigger the encode process.
	 * If something goes wrong during all this, we stop and remove the EncodeHandler from list. 
	 */
	public void doCDRip() {
		// node is a reference to the audiopart node needed to be ripped.
		int id = node.getIntValue("number");	
		if (debug) debug("doCDRip: started for audiopart: "+id+", node is a reference to the audiopart to be ripped.");

		// Get the cdplayer node through the owner value of the current audiopart.
		String owner=node.getStringValue("owner");	
		if (debug) debug("doCDRip: Getting cdplayer that is claimed by owner: "+owner);
		cdplayers cdpbul=(cdplayers)parent.Vwms.mmb.getMMObject("cdplayers");	
		MMObjectNode cdpnode=cdpbul.getClaimedBy(owner);
		if (cdpnode!=null) {
			debug("doCDRip: Found cdplayer "+cdpnode.getStringValue("name"));
			// create a new rawaudio for the wav that's will be ripped
			int STEREO = 2; // mmm... no constant for stereo
			MMObjectNode wavnode=addRawAudio(id,RawAudioDef.STATUS_ONDERWEG,RawAudioDef.FORMAT_WAV,RawAudioDef.WAV_MAXSPEED,STEREO); 

			// Adding the audiopart objectnumber to the cdplayer.info field (which already contains tracknr.)
			debug("doCDRip: Adding id (audiopartobjnr) to the cdplayer.info");
			String info=cdpnode.getStringValue("info");
			StringTagger tagger=new StringTagger(info);
			String tracknr=tagger.Value("tracknr");
			if (tracknr!=null) {
				// Set cdplayer state to 'record', the node change will eventually signal remotebuilder to start ripping
				debug("doCDRip: Setting state to 'record' to rip tracknr "+tracknr+" for id "+id);
				cdpnode.setValue("state","record");
				cdpnode.setValue("info","tracknr="+tracknr+" id="+id);
			} else {
				debug("doCDRip: ERROR: Can't get selected tracknr from cdplayers.info field value="+tracknr);
				cdpnode.setValue("state","error");
				cdpnode.setValue("info","ERROR: Can't get selected tracknr from cdplayers.info field value="+tracknr);
			}
			cdpnode.commit();

			// wait for cdplayer to finish
			boolean serviceFinished = waitForServiceToFinish(cdpbul,cdpnode.getIntValue("number"));
			if (serviceFinished) {
				if (debug) debug("doCDRip: cdplayer is 'waiting' again, checking exitvalue");
				// check the cdplayer exitvalue before recovering.
				cdpnode = cdpbul.getNode(cdpnode.getIntValue("number"));	
				if (checkExitValue(cdpnode)) {
					debug("doCDRip: exit ok, resetting cdplayer, and finishing rawaudio state.");
					finishRawAudio(); // Recover by finshing the rawaudio node state.
					resetService(cdpnode); // Reset the current service.

					// create the needed g2 RawAudio node
					int rastate = RawAudioDef.STATUS_VERZOEK;
					if (debug) debug("doCDRipRecover: Creating new rawaudio of type G2 with state: "+rastate+" id:"+id);
					addRawAudio(id,rastate,RawAudioDef.FORMAT_G2,RawAudioDef.G2_MAXSPEED,STEREO);
					parent.removeEncodeHandler(this); // Finally remove this encodehandler.
				} else {
					debug("doCDRip: ERROR: can't recover for "+id+", removing encodehandler");
					parent.removeEncodeHandler(this); // cant recover so remove handler.
				}
			} else {
				debug("doCDRip: ERROR: cdplayer did not finish correctly, node:"+cdpnode+", removing encodehandler");
				parent.removeEncodeHandler(this); // cant recover so remove handler.
			}
		} else {
			debug("doCDRip: ERROR: Can't find a cdplayer claimed by owner: "+owner+", removing encodehandler");
			parent.removeEncodeHandler(this); // cant recover so remove handler.
		}
	}

	/**
	 * Handles the Real G2 Encoding of an audiopart.
	 * First we wait for a free g2encoder, and when we found one initialize the g2encoder
	 * and start it by setting the state to 'encode'.
	 * The rawaudio representing the encoded audio is set to a state indicating that its being encoded.
	 * We wait until encoding finishes and if everything went ok, we finish the rawaudio 
	 * and reset the encoder and finally remote the encodehandler from list.
	 * If something goes wrong during all this, we stop and remove the EncodeHandler from list. 
	 */
	public void doG2Encode() {
		int id=node.getIntValue("id");	
		if (debug) debug("doG2Encode(): Started for audiopart:"+id+", node is reference to a rawaudio for the encoded audio");

		// Waits until a free g2encoder is available.
		g2encoders g2bul =(g2encoders)parent.Vwms.mmb.getMMObject("g2encoders");	
		MMObjectNode g2encnode = getFreeG2Node(g2bul);
		if (debug) debug("doG2Encode: Got free g2encoder, setting rawaudio state from "+node.getIntValue("status")+" to "+RawAudioDef.STATUS_ONDERWEG);
		node.setValue("status",RawAudioDef.STATUS_ONDERWEG);
		node.commit();

		// Encoded files will be stored in subdir, where name is the id nr. (mkdir is done remotely).	
		// Id is used during a busy situation recovery
		String params="id="+id+" subdir="+id+" inputname=/data/audio/wav/"+id+".wav outputname=/data/audio/ra/"+id+"/surestream.rm sureStream=true encodeAudio=true forceOverwrite=true audioFormat=\"stereo music\"";
		if (debug) debug("doG2Encode: Setting g2encoder state to 'encode' and filling info with '"+params+"'");
		g2encnode.setValue("info",params);
		g2encnode.setValue("state","encode");
		g2encnode.commit();	

		// wait for encoder to finish
		boolean serviceFinished = waitForServiceToFinish(g2bul,g2encnode.getIntValue("number"));
		if (serviceFinished) {
			if (debug) debug("doG2Encoder: g2encoder is 'waiting' again, checking exitvalue");
			// check the encoder exitvalue before recovering.
			g2encnode = g2bul.getNode(g2encnode.getIntValue("number"));	
			if (checkExitValue(g2encnode)) {
				debug("doG2Encode: exit ok, resetting g2encoder, and finishing rawaudio state.");
				finishRawAudio(); // Recover by finshing the rawaudio node state.
				resetService(g2encnode); // Reset the current service.
				parent.removeEncodeHandler(this); // Finally remove this encodehandler.
			} else {
				debug("doG2EncodeRecover: ERROR: can't recover for "+id+", removing encodehandler");
				parent.removeEncodeHandler(this); // cant recover so remove handler.
			}
		} else {
			debug("doG2Encode: ERROR: g2encoder did not finish correctly, node:"+g2encnode+", removing encodehandler");
			parent.removeEncodeHandler(this); // cant recover so remove handler.
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
	 * Will change soon
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

	/**
	 * calls notify. 
	 */
	public synchronized void notifyG2Free() {
		notify();
	}

	/**
	 * When mmbase has been reset during encoding we wait again for the encoder to finish.
	 * After the encoder finishes, we check the process exitvalue and when all is ok,
	 * we finish the RawAudio representing the encoded audio (this will make audiopart available to usage).
	 * After that we reset the service and remove the EncodeHandler from list.
	 * If something went wrong during all this, the RawAudio and g2encoder service will be left in 
	 * current state and we remove the EncodeHandler from list.
	 * @param g2encnumber object number of the g2encoder we're recovering for.
	 */
	public void doRecoverBusyG2Encoder(int g2encnumber) {
		if (debug) debug("doRecoverBusyG2Encoder: Recovery started for rawaudio.id:"+node.getIntValue("id"));

		// wait for encoder to finish
		g2encoders g2bul= (g2encoders)parent.Vwms.mmb.getMMObject("g2encoders");
		MMObjectNode g2encnode = g2bul.getNode(g2encnumber);
		boolean serviceFinished = waitForServiceToFinish(g2bul,g2encnumber);
		if (serviceFinished) {
			if (debug) debug("doRecoverBusyG2Encoder: g2encoder is 'waiting' again, checking exitvalue");
			// check the encoder exitvalue before recovering.
			g2encnode = g2bul.getNode(g2encnumber);	
			if (checkExitValue(g2encnode)) {
				debug("doRecoverBusyG2Encoder: exit ok, resetting g2encoder, and finishing rawaudio state.");
				finishRawAudio(); // Recover by finshing the rawaudio node state.
				resetService(g2encnode); // Reset the current service.
				parent.removeEncodeHandler(this); // Finally remove this encodehandler.
			} else {
				debug("doRecoverBusyG2Encoder: ERROR: can't recover for "+node.getIntValue("id")+", removing encodehandler");
				parent.removeEncodeHandler(this); // cant recover so remove handler.
			}
		} else {
			debug("doRecoverBusyG2Encoder: ERROR: g2encoder did not finish correctly, node:"+g2encnode+", removing encodehandler");
			parent.removeEncodeHandler(this); // cant recover so remove handler.
		}
	}

	/**
	 * When mmbase has been reset during cd ripping we wait again for the cdplayer to finish.
	 * After the cdplayer finishes, we check the process exitvalue and when all is ok,
	 * we finish the RawAudio representing the ripped audio.
	 * After that we reset the service and we create a RawAudio representing the to be encoded audio.
	 * (Creation will eventually start encoder via EncodeCop that inturn start the EncodeHandler.)
	 * When the RawAudio is created we remove the EncodeHandler from list.
	 * If something went wrong during all this, the RawAudio and cdplayer service will be left in 
	 * current state and we remove the EncodeHandler from list.
	 * @param cdplayernumber object number of the cdplayer we're recovering for.
	 */
	public void doRecoverBusyCDRip(int cdplayernumber) {
		if (debug) debug("doRecoverBusyCDRip: Recovery started for rawaudio.id:"+node.getIntValue("id"));

		// wait for cdplayer to finish
		cdplayers cdpbul= (cdplayers)parent.Vwms.mmb.getMMObject("cdplayers");
		MMObjectNode cdpnode = cdpbul.getNode(cdplayernumber);
		boolean serviceFinished = waitForServiceToFinish(cdpbul,cdplayernumber);
		if (serviceFinished) {
			// check the cdplayer exitvalue before recovering.
			cdpnode = cdpbul.getNode(cdplayernumber);
			if (checkExitValue(cdpnode)) {
				debug("doRecoverBusyCDRip: exit ok, finishing rawaudio state, add new ra g2 and resetting cdplayer.");
				finishRawAudio(); // Recover by finishing the rawaudio node state.
				resetService(cdpnode); // Reset the current service.

				// create the needed g2 RawAudio node
				int state = RawAudioDef.STATUS_VERZOEK;
				int id = node.getIntValue("id");
				if (debug) debug("doRecoverBusyCDRip: Creating new rawaudio of type G2 with state: "+state+" id:"+id);
				addRawAudio(id,state,RawAudioDef.FORMAT_G2,RawAudioDef.G2_MAXSPEED,2);
				if (debug) debug("doRecoverBusyCDRip: Removing this EncodeHandler now.");
				parent.removeEncodeHandler(this); // Finally remove this encodehandler.
			} else {
				debug("doRecoverBusyCDRip: ERROR: can't recover for "+node.getIntValue("id")+", removing encodehandler");
				parent.removeEncodeHandler(this); // cant recover so remove handler.
			}
		} else {
			debug("doRecoverBusyCDRip: ERROR: g2encoder did not finish correctly, node:"+cdpnode+", removing encodehandler");
			parent.removeEncodeHandler(this); // cant recover so remove handler.
		}
	}

	/**
	 * This is for the case when mmbase was reset just when an encoder finishes.
	 * After bootup the EncodeCop detects that there's an encoder that just finished,
	 * so recovery is needed.
	 * First we check the process exitvalue and when all is ok, we finish the RawAudio representing 
	 * the encoded audio (this will make audiopart available to usage).
	 * After that we reset the service and remove the EncodeHandler from list.
	 * If something went wrong during all this, the RawAudio and g2encoder service will be left in 
	 * current state and we remove the EncodeHandler from list.
	 * @param g2encnumber object number of the g2encoder we're recovering for.
	 */
	public void doRecoverFinishedG2Encoder(int g2encnumber) {
		if (debug) debug("doRecoverFinishedG2Encoder: Recovery started for rawaudio.id:"+node.getIntValue("id"));

		g2encoders g2bul= (g2encoders)parent.Vwms.mmb.getMMObject("g2encoders");
		MMObjectNode g2encnode = g2bul.getNode(g2encnumber);
		// check the encoder exitvalue before recovering.
		if (checkExitValue(g2encnode)) {
			debug("doRecoverFinishedG2Encoder: exit ok, finishing rawaudio state and resetting g2encoder.");
			finishRawAudio(); // Recover by finishing the rawaudio node state.
			resetService(g2encnode); // Reset the current service.
			parent.removeEncodeHandler(this); // Finally remove this encodehandler.
		} else {
			debug("doRecoverFinishedG2Encoder: ERROR: can't recover for "+node.getIntValue("id")+", removing encodehandler");
			parent.removeEncodeHandler(this); // cant recover so remove handler.
		}
	}

	/**
	 * This is for the case when mmbase was reset just when an cdplayer finishes.
	 * After bootup the EncodeCop detects that there's an cdplayer that just finished,
	 * so recovery is needed.
	 * First we check the process exitvalue and when all is ok, we finish the RawAudio representing 
	 * the ripped audio.
	 * After that we reset the service and we create a RawAudio representing the to be encoded audio.
	 * (Creation will eventually start encoder via EncodeCop that inturn start the EncodeHandler.)
	 * When the RawAudio is created we remove the EncodeHandler from list.
	 * If something went wrong during all this, the RawAudio and cdplayer service will be left in 
	 * current state and we remove the EncodeHandler from list.
	 * @param cdplayernumber object number of the cdplayer we're recovering for.
	 */
	public void doRecoverFinishedCDRip(int cdplayernumber) {
		if (debug) debug("doRecoverFinishedCDRip: Recovery started for rawaudio.id:"+node.getIntValue("id"));

		cdplayers cdpbul= (cdplayers)parent.Vwms.mmb.getMMObject("cdplayers");
		MMObjectNode cdpnode = cdpbul.getNode(cdplayernumber);
		// check the cdplayer exitvalue before recovering.
		if (checkExitValue(cdpnode)) {
			debug("doRecoverFinishedCDRip: exit ok, finishing rawaudio state, add new ra g2 and resetting cdplayer.");
			finishRawAudio(); // Recover by finishing the rawaudio node state.
			resetService(cdpnode); // Reset the current service.

			// create the needed g2 RawAudio node
			int state = RawAudioDef.STATUS_VERZOEK;
			int id = node.getIntValue("id");
			if (debug) debug("doRecoverFinishedCDRip: Creating new rawaudio of type G2 with state: "+state+" id:"+id);
			addRawAudio(id,state,RawAudioDef.FORMAT_G2,RawAudioDef.G2_MAXSPEED,2);
			if (debug) debug("doRecoverFinishedCDRip: Removing this EncodeHandler now.");
			parent.removeEncodeHandler(this); // Finally remove this encodehandler.
		} else {
			debug("doRecoverFinishedCDRip: ERROR: can't recover for "+node.getIntValue("id")+", removing encodehandler");
			parent.removeEncodeHandler(this); // cant recover so remove handler.
		}
	}

	/**
	 * Waits for the specific service to finish, expected current state:busy, expected finished state:waiting.
	 * @param servicenumber objectnr of the service that we're waiting for.
	 * @return service node or null if it can't be retrieved.
	 */
	private boolean waitForServiceToFinish(MMObjectBuilder servicebul,int servicenumber) {
		// search service that's busy on current audiopart.
		String state=null;
		MMObjectNode servicenode = servicebul.getNode(servicenumber);
		if (servicenode!=null) {
			// wait for service to finish.
			boolean changed=false;
			MMObjectNode newservicenode=null;
			while (!changed) {
				parent.Vwms.mmb.mmc.waitUntilNodeChanged(servicenode);

				newservicenode=servicebul.getNode(servicenode.getIntValue("number"));
				if (debug) debug("waitForServiceToFinish: waitUntilNodeChanged done, gettingNode: "+newservicenode);
				state = newservicenode.getStringValue("state");
				if (state.equals("waiting")) {
					changed=true;
				} else if (state.equals("error")) {
					debug("waitForServiceToFinish: ERROR: Service state is error!");
					return false;
				}
			}
			if (debug) debug("waitForServiceToFinish: Service has state:"+state);
			return true;
		} else {
			debug("waitForServiceToFinish: ERROR: Can't get servicenode:"+servicenumber+", getNode returns null");
			return false;
		}
	}

	/**
	 * Checks the exitvalue of the task that was executed by the service. 
	 * @param servicenode reference to service.
	 * @return true if the exitvalue=0, false otherwise.
	 */	
	private boolean checkExitValue(MMObjectNode servicenode) {
		if (debug) debug("checkExitValue: Checking for service: "+servicenode.getStringValue("name"));
		// check exitvalue and recover rawaudio node.
		StringTagger infoTagger = new StringTagger(servicenode.getStringValue("info"));
		if (infoTagger.containsKey("result")) {
			int exit=-1;
			try {
				exit = Integer.parseInt((String) infoTagger.Value("result")); // result contains exitvalue.
				if (exit==0) {
					return true;
				} else {
					debug("checkExitValue: ERROR: exit !=0 (exit="+exit+")");
					return false;
				}
			} catch (NumberFormatException nfe) {
				debug("checkExitValue: ERROR: exit is no int (exit="+exit+")");
				nfe.printStackTrace();
				return false;
			}
		} else {
			debug("checkExitValue: ERROR: no result tag in info field");
			return false;
		}
	}

	/**
	 * Resets the service, meaning that info field is cleared.
	 * @param servicenode reference to service.
	 */
	private void resetService(MMObjectNode servicenode) {
		servicenode.setValue("info","");
		servicenode.commit();
	}

	/**
	 * Sets the rawaudionode (field of this EncodeHandler) in a done state, thus the audiopart will 
	 * become for usage. Depending on the type of rawaudio additional fields will be set. 
	 * Currently only when rawaudio format is G2 we set the url.
	 */
	private void finishRawAudio()  {
		// When rawaudio represents encoded audio (G2) then set the url field 
		// using our obscure F=path H1=host1 (H2=host2) format, (for the record...I didn't create it,davzev).
		if (node.getIntValue("format")==RawAudioDef.FORMAT_G2) {
			int id = node.getIntValue("id");
			node.setValue("url","F=/"+id+"/surestream.rm H1="+RAWAUDIO_URL_HOST1);
		}
		node.setValue("status",RawAudioDef.STATUS_GEDAAN);
		node.setValue("cpu",parent.getRelatedMMServerName());
		node.commit();
	}
}
