/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: EncodeCop.java,v 1.7 2000-03-30 13:11:36 wwwtech Exp $

$Log: not supported by cvs2svn $
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

import nl.vpro.mmbase.util.media.audio.*;
import nl.vpro.mmbase.util.media.audio.cdtracks.*;
import nl.vpro.mmbase.util.media.audio.audioparts.*;

/**
 * @author Daniel Ockeloen
 * @version $Revision: 1.7 $ $Date: 2000-03-30 13:11:36 $
 */

public class EncodeCop extends Vwm implements MMBaseObserver {

	private	String classname 	= getClass().getName();
	private boolean	debug		= true;
	// private void debug( String msg ) { System.out.println( classname +":"+ msg ); }

	Vector EncoderHandlers		= new Vector();
	Vector waitingEncodeHandlers= new Vector();

	public EncodeCop() {
		debug("EncodeCop(): EncodeCop started...");
	}
	
	public boolean probeCall() {
		debug("probeCall(): Adding observers");
		Vwms.mmb.addLocalObserver("rawaudios",this);
		Vwms.mmb.addRemoteObserver("rawaudios",this);
		Vwms.mmb.addLocalObserver("g2encoders",this);
		Vwms.mmb.addRemoteObserver("g2encoders",this);
		Vwms.mmb.addLocalObserver("cdtracks",this);
		Vwms.mmb.addRemoteObserver("cdtracks",this);
		return(true);
	}

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		if( debug ) debug("nodeRemoteChanged("+number+","+builder+","+ctype+")");	
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		if( debug ) debug("nodeLocalChanged("+number+","+builder+","+ctype+")");
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeChanged(String number,String builder, String ctype) {
		if( debug ) debug("nodeChanged("+number+","+builder+","+ctype+")");
		if (ctype.equals("c") || ctype.equals("n")) {
			if (builder.equals("g2encoders")) encoderChanged(number,ctype);	
			if (builder.equals("rawaudios")) rawaudioChanged(number,ctype);	
			if (builder.equals("cdtracks")) cdtracksChanged(number,ctype);	
		}
		return(true);
	}

	public boolean encoderChanged(String number,String ctype) {
		if( debug ) debug("encoderChanged("+number+","+ctype+")");

		// check whether we have a encodeHandler running.. not, than machine crashed

		try {
			int num = Integer.parseInt( number );
			if( getEncodeHandler( num ) == null ) {
				debug("encoderChanged("+number+","+ctype+"): ERROR: No handler found, machine crashed/rebooted !?!");
			} else {
				debug("encoderChanged("+number+","+ctype+"): handler found, everything ok, signaling free()!");
				signalEncoderFree( num );
			}
		} catch (NumberFormatException e ) {
			debug("encoderChanged("+number+","+ctype+"): ERROR: while converting to int:"+e);
		}	
		return(true);
	}

	public boolean rawaudioChanged(String number,String ctype) {
		if( debug ) debug("rawaudioChanged("+number+","+ctype+")");
		// debug("rawaudioChanged(): sees that rawaudio "+number+" has changed type="+ctype);
		RawAudios bul=(RawAudios)Vwms.mmb.getMMObject("rawaudios");		
		if (bul!=null) {
			MMObjectNode node=bul.getNode(number);
			int status=node.getIntValue("status");
			int format=node.getIntValue("format");

			if (status==RawAudioDef.STATUS_VERZOEK && format==RawAudioDef.FORMAT_G2) {
				if( debug ) debug("rawaudioChanged("+number+","+ctype+"): adding new handler..");
				EncoderHandlers.addElement( new EncodeHandler(this,"g2encode",node) );
			}
		} else {
			debug("rawaudioChanged(): no reference could be made to rawaudios");
		}
		return(true);
	}


	public boolean cdtracksChanged(String number,String ctype) {
		if( debug ) debug("cdtracksChanged("+number+","+ctype+")");
		if (ctype.equals("n")) {
			CDTracks bul=(CDTracks)Vwms.mmb.getMMObject("cdtracks");
			if (bul!=null) {
				MMObjectNode node=bul.getNode(number);
				EncoderHandlers.addElement( new EncodeHandler(this,"newcdtrack",node) );
			} else {
				debug("cdtracksChanged(): no reference could be make to cdtracks");
			}
		}
		return(true);
	}

	private EncodeHandler getEncodeHandler( int number ) {
		Enumeration 	e 		= EncoderHandlers.elements();
		EncodeHandler	result	= null;
		EncodeHandler 	eh 		= null;
		while( e.hasMoreElements() && (result==null) ) {
			eh = (EncodeHandler) e.nextElement();
			if( eh.node != null )
				if( eh.node.getIntValue("+number+") == number )
					result = eh;
		}
		return result;
	}

	// signal to encodecop that we are waiting for a free encoder..
	public void addWaitingEncodeHandler( EncodeHandler h ) {
		waitingEncodeHandlers.add( h );
	}

	// remove ourselfs from waitinglist, we are not waiting, but activly searching for encoder
	public void removeWaitingEncodeHandler( EncodeHandler h ) {
		waitingEncodeHandlers.remove( h );	
	}

	public boolean removeEncodeHandler( EncodeHandler eh ) {
		return EncoderHandlers.remove( eh );
	}

	// encodecop saw a free encoder, signal first waiting handler that a free encoder has arrived
	public void signalEncoderFree( int num ) {
		Enumeration e = waitingEncodeHandlers.elements();
		EncodeHandler h = null;
		if( e.hasMoreElements() ) {
			h = (EncodeHandler)e.nextElement();
			if( h.node.getIntValue("number") != num )	// just to be sure..
				h.notifyG2Free();
			else
				debug("signalEncoderFree("+num+"): ERROR: SEVERE: this node is waiting for free encoder, but got signal that it has finished!!!");
		}		
	}
}
