/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: EncodeCop.java,v 1.14 2001-02-28 13:56:49 vpro Exp $

$Log: not supported by cvs2svn $
Revision 1.13  2001/02/15 13:29:30  vpro
Davzev: Renamed methods comments etc...

Revision 1.12  2000/10/09 12:03:43  vpro
Rico: added marcel comments

Revision 1.11  2000/08/01 09:53:31  install
changed imports

Revision 1.10  2000/08/01 09:11:32  vpro
Rico: removed CDTrack references

Revision 1.9  2000/08/01 08:53:32  install
Rob: removed method that was not needed antmore

Revision 1.8  2000/06/05 10:56:55  wwwtech
Rico: added support for new 3voor12

Revision 1.7  2000/03/30 13:11:36  wwwtech
Rico: added license

Revision 1.6  2000/03/29 11:04:45  wwwtech
Rob: Licenses changed

Revision 1.5  2000/03/27 16:01:01  wwwtech
Rico: moved lots of VPRO stuff to nl.vpro

Revision 1.3  2000/03/24 14:34:04  wwwtech
Rico: total recompile

Revision 1.2  2000/03/21 15:36:57  wwwtech
- (marcel) Removed debug (globally declared in MMOBjectNode)

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;

import org.mmbase.util.media.audio.*;
import org.mmbase.util.media.audio.audioparts.*;

/**
 * The EncodeCop vwm class observes changes of MMObjectnodes that are related to the
 * audio ripping encoding process. The nodes that are observed are AudioParts, 
 * RawAudios and g2encoders nodes. Depending on type of change that's performed on this
 * node, a related EncodeHandler will be created that will handle the actual audio job.
 * 
 * The following node changed types exist in mmbase (stored in ctype variable: 
 * passed ctype:
 * d: node deleted
 * c: node changed
 * n: new node
 * f: node field changed
 * r: node relation changed
 * x: some xml notify?
 * 
 * @author Daniel Ockeloen
 * @version $Revision: 1.14 $ $Date: 2001-02-28 13:56:49 $
 */

public class EncodeCop extends Vwm implements MMBaseObserver {

	private	String classname 	= getClass().getName();
	private boolean	debug		= true;

	// private void debug( String msg ) { System.out.println( classname +":"+ msg ); }

	Vector EncoderHandlers		= new Vector();
	Vector waitingEncodeHandlers= new Vector();

	private boolean firstProbeCall = true;

	public EncodeCop() {
		debug("EncodeCop(): EncodeCop started...");
	}

	/**
	 * Adds observers to audiopart, rawaudio and g2encoder changes.
	 * @return true, always.
	 */	
	public boolean probeCall() {
		if (firstProbeCall) {
			firstProbeCall = false;	
			if (debug) debug("probeCall(): Adding types: audioparts, rawaudios, g2encoders to observer list.");

			Vwms.mmb.addLocalObserver("audioparts",this);
			Vwms.mmb.addRemoteObserver("audioparts",this);

			Vwms.mmb.addLocalObserver("rawaudios",this);
			Vwms.mmb.addRemoteObserver("rawaudios",this);

			Vwms.mmb.addLocalObserver("g2encoders",this);
			Vwms.mmb.addRemoteObserver("g2encoders",this);
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
		if (debug) debug("nodeRemoteChanged("+number+","+builder+","+ctype+"): Calling nodeChanged to evaluate change.");
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
		if (debug) debug("nodeLocalChanged("+number+","+builder+","+ctype+"): Calling nodeChanged to evaluate change.");
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
		if( debug ) debug("nodeChanged("+number+","+builder+","+ctype+"): Checking ctype and buildertype.");
		if (ctype.equals("c") || ctype.equals("n")) {
			if (builder.equals("audioparts")) audiopartChanged(number,ctype);	
			if (builder.equals("rawaudios")) rawaudioChanged(number,ctype);	
			if (builder.equals("g2encoders")) g2encoderChanged(number,ctype);	
		}
		return true;
	}

	/**
	 * Creates an EncodeHandler with task 'newcdtrack' for this newly created audiopart.
	 * node, depending on the audiosource value of the audiopartnode.
	 * @param number - a String containing the object nr of this audioparts node.
	 * @param ctype - a String with the node changed type.
	 * @return true, always.
	 * @param number
	 */
	public boolean audiopartChanged(String number,String ctype) {
		if (debug) debug("audiopartChanged("+number+","+ctype+"): Getting node and checking ctype and audiosource.");
		if (ctype.equals("n")) {
			AudioParts bul=(AudioParts)Vwms.mmb.getMMObject("audioparts");
			if (bul!=null) {
				MMObjectNode apNode=bul.getNode(number);
				if(apNode.getIntValue("source") == AudioParts.AUDIOSOURCE_CD) {
					if (debug) debug("audiopartChanged("+number+","+ctype+"): apNode : is 'new' and source is CD, adding a new EncodeHandler with task 'newcdtrack'.");
					EncoderHandlers.addElement(new EncodeHandler(this,"newcdtrack",apNode));
				}
			} else
				debug("audiopartChanged(): ERROR, Can't get the AudioParts builder.");
		}
		return true;
	}

	/**
	 * Creates an EncodeHandler with task 'g2encode' for this changed rawaudio node, 
	 * depending on the node state and format value.
	 * @param number - a String containing the object nr of this rawaudios node.
	 * @param ctype - a String with the node changed type.
	 * @return true, always.
	 */
	public boolean rawaudioChanged(String number,String ctype) {
		if (debug) debug("rawaudioChanged("+number+","+ctype+"): Getting raNode and checking state & format.");
		RawAudios bul=(RawAudios)Vwms.mmb.getMMObject("rawaudios");		
		if (bul!=null) {
			MMObjectNode raNode=bul.getNode(number);
			int status=raNode.getIntValue("status");
			int format=raNode.getIntValue("format");

			if (status==RawAudioDef.STATUS_VERZOEK && format==RawAudioDef.FORMAT_G2) {
				if (debug) debug("rawaudioChanged("+number+","+ctype+"): raNode state:"+RawAudioDef.STATUS_VERZOEK+" format:"+RawAudioDef.FORMAT_G2+" (G2), adding new EncodeHandler with task 'g2encode'.");
				EncoderHandlers.addElement(new EncodeHandler(this,"g2encode",raNode));
			}
		} else
			debug("rawaudioChanged(): ERROR, Can't get the RawAudios builder!");
		return true;
	}

	/**
	 * Checks if the incoming g2encoders node has an EncoderHandler running for it 
	 * and calls a method to signal that it's encoder is free.
	 * @param number - a String containing the object nr of this g2encoders node.
	 * @param ctype - a String with the node changed type.
	 * @return true, always.
	 */
	public boolean g2encoderChanged(String number,String ctype) {
		if (debug) debug("g2encoderChanged("+number+","+ctype+"): About to get EncodeHandler for "+number);

		// check whether we have a encodeHandler running.. not, than machine crashed 
		try {
			int num = Integer.parseInt(number);
			if (getEncodeHandler(num) == null) {
				debug("g2encoderChanged("+number+","+ctype+"): ERROR: No handler found, machine crashed/rebooted !?!");
			} else {
				debug("g2encoderChanged("+number+","+ctype+"): EncodeHandler found, everything ok, signalling free()!");
				signalEncoderFree(num);
			}
		} catch (NumberFormatException nfe) {
			debug("g2encoderChanged("+number+","+ctype+"): ERROR: while converting String "+number+" to int");
			nfe.printStackTrace();
		}	
		return true;
	}

	/**
	 * Gets reference to the EncodeHandler that was assigned to current g2encoders node.
	 * @param number the audioparts number to which an EncodeHandler is assigned.
	 * @return EncodeHandler reference.
	 */
	private EncodeHandler getEncodeHandler(int number) {
		if (debug) debug("getEncodeHandler: Getting EncodeHandler reference for g2encoder "+number+" from EncoderHandlers vector"); 
		Enumeration 	e 		= EncoderHandlers.elements();
		EncodeHandler	result	= null;
		EncodeHandler 	eh 		= null;
		while(e.hasMoreElements() && (result==null)) {
			eh = (EncodeHandler) e.nextElement();
			if (debug) debug("getEncodeHandler: vector element is an EncodeHandler, task="+eh.task+" node="+eh.node); 
			if (debug) debug("getEncodeHandler: Comparing incoming g2encoders obj:"+number+" with EncHand.rawaudiog2:"+eh.node.getIntValue("number")); 
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
		if (debug) debug("signalEncoderFree: Getting the waiting EncodeHandler for g2encoder "+number+" from waitingEncodeHandlers vector");
		Enumeration e = waitingEncodeHandlers.elements();
		EncodeHandler eh = null;
		if(e.hasMoreElements()) {
			eh = (EncodeHandler)e.nextElement();
			if (debug) debug("signalEncoderFree: vector element is an EncodeHandler, task="+eh.task+" node="+eh.node); 
			if(eh.node.getIntValue("number") != number)	// just to be sure..
				eh.notifyG2Free();
			else
				debug("signalEncoderFree("+number+"): ERROR: SEVERE: this node is waiting for free encoder, but got signal that it has finished!!!");
		}		
	}
}
