/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.module.builders.vwms;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;

import org.mmbase.util.media.audio.*;
import org.mmbase.util.media.audio.audioparts.*;
import org.mmbase.util.StringTagger;
import org.mmbase.util.logging.*;

/**
 * The EncodeCop vwm observes changes on a certain type of nodes and activates
 * services by starting EncodeHandlers for that particular node.
 * 
 * If a certain audio file has to be ripped, then the EncodeCop is programmed 
 * to check for newly AudioPart changes of ctype 'n' where the 
 * audiopart.source is equal to the CD audiosource. 
 * If this is true then it reacts by starting the EncodeHandler for ripping CD's.
 *
 * The same goes for the encoding of ripped audio. For this, the EncodeCop checks 
 * for RawAudio changes of type 'c' or 'n' where the rawaudio.state is equals 
 * to 1 and where the RawAudio represents encoded audio (eg. type=Real G2).  
 * 
 * The last type of node thats being checked is the g2encoder service. However,
 * the current code doesn't work at all and will be changed/removed soon. 
 * (See method comment for more info.)
 * 
 * To keep track of which EncodeHandlers are busy and are still waiting to begin, 
 * the EncodeCop uses 2 lists that can be altered using add/remove methods. 
 * 
 * Recovery :
 * When the mmbase running this EncodeCop is being reset recovery is needed.
 * Why?, cause there could be services that are still busy or services 
 * that just signalled that they're finished. In both cases, after mmbase reboot 
 * there are no more EncodeHandlers active for those services, thus the related 
 * rawaudios representing ripped/encoded audio won't be updated anymore. 
 * 
 * Thus, after mmbase reset recovery is written for the followin 2 cases:
 * 1) Service was busy (eg. ripping or encoding)
 *    In this case the servicebuilder state is still 'busy' and the info field 
 *    should contain the 'id' tag, where the value represents the audiopart 
 *    being encoded.
 *
 * 2) Service just finished (eg. file ripped or encoded)
 *    In this case the servicebuilder state is 'waiting' and the info field 
 *    should contain the 'result' tag and the 'id' tag.
 * 
 * When all can be found an EncodeHandler is started that will actually handle 
 * the type of recovery needed for that particular service.
 *
 *
 * The following node changed types exist in mmbase (stored in ctype variable: 
 * passed ctype:
 * d: node deleted
 * c: node changed
 * n: new node
 * f: node field changed
 * r: node relation changed
 * x: some xml notify?
 * g: ? anyone...anyone...
 * s: ? anyone...anyone...
 * 
 * 
 * @author Daniel Ockeloen, David van Zeventer
 * @version $Revision: 1.16 $ $Date: 2001-04-20 14:42:20 $
 */
public class EncodeCop extends Vwm implements MMBaseObserver {
    private static Logger log = Logging.getLoggerInstance(EncodeCop.class.getName());

	Vector EncoderHandlers		= new Vector();
	Vector waitingEncodeHandlers= new Vector();

	private boolean firstProbeCall = true;

	public EncodeCop() {
		log.info("EncodeCop started...");
	}

	/**
	 * Adds observers to audiopart, rawaudio and g2encoder changes.
	 * @return true, always.
	 */	
	public boolean probeCall() {
		if (firstProbeCall) {
			firstProbeCall = false;	
            log.info("Adding types: audioparts, rawaudios, g2encoders to observer list.");

			Vwms.mmb.addLocalObserver("audioparts",this);
			Vwms.mmb.addRemoteObserver("audioparts",this);

			Vwms.mmb.addLocalObserver("rawaudios",this);
			Vwms.mmb.addRemoteObserver("rawaudios",this);

			Vwms.mmb.addLocalObserver("g2encoders",this);
			Vwms.mmb.addRemoteObserver("g2encoders",this);
		
			// Recover for cdplayer en g2encoder services that were busy or that just finished during reset. 
			cdplayers cdpbul=(cdplayers)Vwms.mmb.getMMObject("cdplayers");
			recoverForBusyServices(cdpbul);
			recoverForFinishedServices(cdpbul);
			g2encoders g2bul=(g2encoders)Vwms.mmb.getMMObject("g2encoders");
			recoverForBusyServices(g2bul);
			recoverForFinishedServices(g2bul);
		}
		return true;
	}


	/**
	 * Called when node was changed by remote mmbase, inturn this method calls nodeChanged method.
	 * @param number object number of node who's state has been changed remotely.
	 * @param builder a String with the buildername of the node that was changed remotely.
	 * @param ctype a String with the node change type.
	 * @return result value of nodeChanged call.
	 */
	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		log.debug("("+number+","+builder+","+ctype+"): Calling nodeChanged to evaluate change.");
		return(nodeChanged(number,builder,ctype));
	}

	/**
	 * Called when node was changed on local mmbase, inturn this method calls nodeChanged method.
	 * @param number object number of node who's state has been changed.
	 * @param builder a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 * @return result value of nodeChanged call.
	 */
	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		log.debug("("+number+","+builder+","+ctype+"): Calling nodeChanged to evaluate change.");
		return(nodeChanged(number,builder,ctype));
	}

	/**
	 * Checks node changetype and buildertype to decide which type of Changed method should be called.
	 * @param number object number of node who's state has been changed.
	 * @param builder a String with the buildername of the node that was changed.
	 * @param ctype a String with the node change type.
	 * @return true, always.
	 */
	public boolean nodeChanged(String number,String builder, String ctype) {
		log.info("("+number+","+builder+","+ctype+"): Checking ctype and buildertype.");
		if (ctype.equals("c") || ctype.equals("n")) {
			if (builder.equals("audioparts")) audiopartChanged(number,ctype);	
			if (builder.equals("rawaudios")) rawaudioChanged(number,ctype);	
			if (builder.equals("g2encoders")) g2encoderChanged(number,ctype);	
		}
		return true;
	}

	/**
	 * Checks the audiopart ctype and source value and when ok, it creates an 
	 * EncodeHandler with task 'newcdtrack' to rip for this new audiopart.
	 * @param number - a String containing the object nr of this audioparts node.
	 * @param ctype - a String with the node changed type.
	 * @return true, always.
	 * @param number
	 */
	public boolean audiopartChanged(String number,String ctype) {
		log.info("("+number+","+ctype+"): Getting node and checking ctype and audiosource.");
		if (ctype.equals("n")) {
			AudioParts bul=(AudioParts)Vwms.mmb.getMMObject("audioparts");
			if (bul!=null) {
				MMObjectNode apNode=bul.getNode(number);
				if(apNode.getIntValue("source") == AudioParts.AUDIOSOURCE_CD) {
					log.info("New audiopart source:CD, adding new EncodeHandler with task 'newcdtrack'");
					EncoderHandlers.addElement(new EncodeHandler(this,"newcdtrack",apNode));
				}
			} else
				log.error("Can't get the AudioParts builder.");
		}
		return true;
	}

	/**
	 * Checks the rawaudio ctype and state & format value and when ok, it creates an 
	 * an EncodeHandler with task 'g2encode' to encode for this rawaudio. 
	 * @param number - a String containing the object nr of this rawaudios node.
	 * @param ctype - a String with the node changed type.
	 * @return true, always.
	 */
	public boolean rawaudioChanged(String number,String ctype) {
		log.info("("+number+","+ctype+"): Getting raNode and checking state & format.");
		RawAudios bul=(RawAudios)Vwms.mmb.getMMObject("rawaudios");		
		if (bul!=null) {
			MMObjectNode raNode=bul.getNode(number);
			int status=raNode.getIntValue("status");
			int format=raNode.getIntValue("format");

			if (status==RawAudioDef.STATUS_VERZOEK && format==RawAudioDef.FORMAT_G2) {
                log.info("raNode state:"+RawAudioDef.STATUS_VERZOEK+" format:"+RawAudioDef.FORMAT_G2+" (G2), adding new EncodeHandler with task 'g2encode'."); 
				EncoderHandlers.addElement(new EncodeHandler(this,"g2encode",raNode));
			}
		} else
			log.error("Can't get the RawAudios builder!");
		return true;
	}

	/**
	 * Checks if the incoming g2encoders node has an EncoderHandler running for it 
	 * and releases the EncodeHandler, no matter if it's still busy or not! this is wrong
	 * and will be fixed soon.
	 * Above all to find the EncodeHandler it compares the incoming g2encoder number with
	 * the EncodeHandlers.node.number value which is never of type g2encoder!
     *
	 * That's why this method currently will print 'crashed/rebooted' always.
	 *
	 * @param number - a String containing the object nr of this g2encoders node.
	 * @param ctype - a String with the node changed type.
	 * @return true, always.
	 */
	public boolean g2encoderChanged(String number,String ctype) {
		log.info("("+number+","+ctype+"): About to get EncodeHandler for "+number);

		// check whether we have a encodeHandler running.. not, than machine crashed 
		try {
			int num = Integer.parseInt(number);
			if (getEncodeHandler(num) == null) {
				log.error("("+number+","+ctype+"): ERROR: No handler found, machine crashed/rebooted !?!");
			} else {
				log.info("("+number+","+ctype+"): EncodeHandler found, everything ok, signalling free()!");
				signalEncoderFree(num);
			}
		} catch (NumberFormatException nfe) {
			log.error("("+number+","+ctype+"): ERROR: while converting String "+number+" to int");
			nfe.printStackTrace();
		}	
		return true;
	}

	/**
	 * Tries to get a reference to the EncodeHandler that handles current g2encoder.
	 * It finds the encodehandler by comparing the g2encoders number with the 
	 * EncodeHandlers.node.number value, which can only be a rawaudio or audiopartnr.
	 * Method will be changed soon.
	 * @param number the g2encoder objnr to which an EncodeHandler is assigned.
	 * @return EncodeHandler reference.
	 */
	private EncodeHandler getEncodeHandler(int number) {
		log.info("Getting EncodeHandler reference for g2encoder "+number+" from EncoderHandlers vector"); 
		Enumeration 	e 		= EncoderHandlers.elements();
		EncodeHandler	result	= null;
		EncodeHandler 	eh 		= null;
		while(e.hasMoreElements() && (result==null)) {
			eh = (EncodeHandler) e.nextElement();
			log.info("vector element is an EncodeHandler, task="+eh.task+" node="+eh.node); 
			log.info("Comparing incoming g2encoders obj:"+number+" with EncHand.rawaudiog2:"+eh.node.getIntValue("number")); 
			if(eh.node != null)
				if(eh.node.getIntValue("+number+") == number)
					result = eh;
		}
		return result;
	}

	/**
	 * Adds EncodeHandler reference to the waitingEncoderHandlers vector.
	 * This results in a signal to the encodecop that we are waiting for a free encoder..
	 * @param h EncodeHandlers reference.
	 */
	public void addWaitingEncodeHandler(EncodeHandler h) {
		waitingEncodeHandlers.add(h);
	}

	/**
	 * Removes EncodeHandler reference from the waitingEncoderHandlers vector.
	 * This will remove ourselfs from waitinglist, we are not waiting, but activly searching for encoder
	 * @param h EncodeHandlers reference.
	 */
	public void removeWaitingEncodeHandler(EncodeHandler h) {
		waitingEncodeHandlers.remove(h);	
	}

	/**
	 * Removes EncodeHandler reference from the EncoderHandlers vector.
	 * @param h EncodeHandlers reference.
	 * @return a boolean result of the Vector remove. 
	 */
	public boolean removeEncodeHandler(EncodeHandler eh) {
		return EncoderHandlers.remove(eh);
	}

	/**
	 * The encodecop saw a free encoder, signal first waiting handler that a free encoder has arrived
	 * @param number the g2encoders objectnumber used to select the EncodeHandler through which signal must occur.
	 */
	public void signalEncoderFree(int number) {
		log.info(": Getting the waiting EncodeHandler for g2encoder "+number+" from waitingEncodeHandlers vector");
		Enumeration e = waitingEncodeHandlers.elements();
		EncodeHandler eh = null;
		if(e.hasMoreElements()) {
			eh = (EncodeHandler)e.nextElement();
			log.info("Vector element is an EncodeHandler, task="+eh.task+" node="+eh.node); 
			if(eh.node.getIntValue("number") != number)	// just to be sure..
				eh.notifyG2Free();
			else
				log.error("SEVERE: this node is waiting for free encoder, but got signal that it has finished!!!");
		}		
	}
	
	/**
	 * Searches all services that are 'busy'. For every service found we check for the idtag in info field.
	 * The idtag contains the reference (objnr) to the audiopart for which service is busy.
	 * If the id can be found and has valid value, we search the related RawAudio and start a recovery 
	 * EncodeHandler.
	 * @param servicebul currently either a reference to the cdplayers or g2encoders builder.
	 */
	private void recoverForBusyServices(ServiceBuilder servicebul) {
        RawAudios rabul = (RawAudios) Vwms.mmb.getMMObject("rawaudios");
        int number=0; // service number
		int apnumber=0;
        Enumeration raenum = null;
		MMObjectNode servicenode = null;
		StringTagger infoTagger = null;
		// Initializing service vars.
		String task=null, busyWith=null;
		int raformat= 0;
		int rastate = RawAudioDef.STATUS_ONDERWEG; // busy 
		String idname = "id";

		// Defining service and audio vars.	
		if ((servicebul.getTableName()).equals("cdplayers")) {
			raformat = RawAudioDef.FORMAT_WAV;
			task = "recoverbusycdrip";
			busyWith = "recording";
		} else if ((servicebul.getTableName()).equals("g2encoders")) {
			raformat = RawAudioDef.FORMAT_G2;
			task = "recoverbusyg2encoder";
			busyWith = "encoding";
		}
		// Search for busy services and start recovery. 
		Enumeration e = servicebul.search("WHERE state='busy'");
		while (e.hasMoreElements()) {
			servicenode = (MMObjectNode)e.nextElement();
			number = servicenode.getIntValue("number");
			infoTagger = new StringTagger(servicenode.getStringValue("info"));
			if (infoTagger.containsKey(idname)) {
				try {
					apnumber = Integer.parseInt((String) infoTagger.Value(idname));
					log.info("Service: "+servicenode.getStringValue("name")+" is "+busyWith+" audiopart "+apnumber);
					log.info("Adding new EncodeHandler, task '"+task+"' for it.");
					raenum = rabul.search("WHERE id="+apnumber+" AND format="+raformat+" AND status="+rastate);
					if (raenum.hasMoreElements())
						EncoderHandlers.addElement(new EncodeHandler(this,task+":"+number,(MMObjectNode)raenum.nextElement()));
				} catch (NumberFormatException nfe) {
					log.error(idname+":"+infoTagger.Value(idname)+" is no int");
					nfe.printStackTrace();
				}
			} else
				log.error("Can't find "+idname+" tag in infofield for service:"+servicenode);
		}
	}

	/**
	 * Searches all services that are 'waiting'. For every service found we check for the 
	 * result tag and id tag in info field.
	 * The idtag contains the reference (objnr) to the audiopart for which service just finished.
	 * If the result and id can be found and has valid value, we search the related RawAudio 
	 * and start a recovery EncodeHandler.
	 * @param servicebul currently either a reference to the cdplayers or g2encoders builder.
	 */
	private void recoverForFinishedServices(ServiceBuilder servicebul) {
        RawAudios rabul = (RawAudios) Vwms.mmb.getMMObject("rawaudios");
        int number=0; // service number
		int apnumber=0;
        Enumeration raenum = null;

		MMObjectNode servicenode = null;
		StringTagger infoTagger = null;
		// Initializing service vars.
		String task=null, finishedWith=null, result=null;
		int raformat=0;
		int rastate = RawAudioDef.STATUS_ONDERWEG; // busy 
		String idname = "id";

		// Defining service and audio vars.	
		if ((servicebul.getTableName()).equals("cdplayers")) {
			raformat = RawAudioDef.FORMAT_WAV;
			task = "recoverfinishedcdrip";
			finishedWith = "recorded";
			result = "result";	
		} else if ((servicebul.getTableName()).equals("g2encoders")) {
			raformat = RawAudioDef.FORMAT_G2;
			task = "recoverfinishedg2encoder";
			finishedWith = "encoded";
			result = "result";	// info should also contain id but since subdir == id we now use subdir.
		}
		// Search for waiting services that just finished and start recovery. 
		Enumeration e = servicebul.search("WHERE state='waiting'");
		while (e.hasMoreElements()) {
			servicenode = (MMObjectNode)e.nextElement();
			number = servicenode.getIntValue("number");
			infoTagger = new StringTagger(servicenode.getStringValue("info"));
			if (infoTagger.containsKey(result) && infoTagger.containsKey(idname)) {
				try {
					apnumber = Integer.parseInt((String) infoTagger.Value(idname));
					log.info("Service: "+servicenode.getStringValue("name")+" just "+finishedWith+" audiopart "+apnumber);
					log.info("Adding new EncodeHandler, task '"+task+"' for it.");
					raenum = rabul.search("WHERE id="+apnumber+" AND format="+raformat+" AND status="+rastate);
					if (raenum.hasMoreElements())
						EncoderHandlers.addElement(new EncodeHandler(this,task+":"+number,(MMObjectNode)raenum.nextElement()));
				} catch (NumberFormatException nfe) {
					log.error(idname+":"+infoTagger.Value(idname)+" is no int");
					nfe.printStackTrace();
				}
			} else if ((!infoTagger.containsKey(result)) && (!infoTagger.containsKey(idname)))
				log.info("Service "+servicenode.getStringValue("name")+" finished correctly.");
			else
				log.error("Info field misses either result or id key in service: "+servicenode);
		}
	}

	/**
	 * Gets the first mmserver connected to this Vwm.
	 * @return mmservername or null if it can't be found.
	 */
	public String getRelatedMMServerName() {
		log.info("Get the mmserver that runs this code.");
		Enumeration e=Vwms.mmb.getInsRel().getRelated(wvmnode.getIntValue("number"),"mmservers");
		if (e.hasMoreElements()) {
			MMObjectNode mmserverNode = (MMObjectNode) e.nextElement();
			String name = mmserverNode.getStringValue("name");
			log.info("Found mmserver: "+name);
			return name;
		} else {
			log.error("No mmserver found!");
			return null;
		}
	}
}
