/*
$Id: EncodeCop.java,v 1.2 2000-03-21 15:36:57 wwwtech Exp $

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

$Log: not supported by cvs2svn $
*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;

/**
 * @author Daniel Ockeloen
 * @version $Revision: 1.2 $ $Date: 2000-03-21 15:36:57 $
 */

public class EncodeCop extends Vwm implements MMBaseObserver {

	private	String classname 	= getClass().getName();
	private boolean	debug		= false;
	// private void debug( String msg ) { System.out.println( classname +":"+ msg ); }

	Vector EncoderHandlers=new Vector();

	public EncodeCop() {
		debug("EncodeCop(): Yo Im EncodeCop");
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
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeChanged(String number,String builder, String ctype) {
		if (ctype.equals("c") || ctype.equals("n")) {
			if (builder.equals("g2encoders")) encoderChanged(number,ctype);	
			if (builder.equals("rawaudios")) rawaudioChanged(number,ctype);	
			if (builder.equals("cdtracks")) cdtracksChanged(number,ctype);	
		}
		return(true);
	}

	public boolean encoderChanged(String number,String ctype) {
		// recovery  
		debug("encoderChanged("+number+","+ctype+"): sees that encoder "+number+" has changed type="+ctype);
		return(true);
	}

	public boolean rawaudioChanged(String number,String ctype) {
		debug("rawaudioChanged(): sees that rawaudio "+number+" has changed type="+ctype);
		RawAudios bul=(RawAudios)Vwms.mmb.getMMObject("rawaudios");		
		if (bul!=null) {
			MMObjectNode node=bul.getNode(number);
			int status=node.getIntValue("status");
			int format=node.getIntValue("format");
			if (status==1 && format==6) {
				EncodeHandler eh=new EncodeHandler(this,"g2encode",node);	
			}
		} else {
			debug("rawaudioChanged(): Vwm henk can't use rawaudios");
		}
		return(true);
	}



	public boolean cdtracksChanged(String number,String ctype) {
		debug("cdtracksChanged(): sees that cdtracks "+number+" has changed type="+ctype);
		if (ctype.equals("n")) {
			CDTracks bul=(CDTracks)Vwms.mmb.getMMObject("cdtracks");		
			if (bul!=null) {
				MMObjectNode node=bul.getNode(number);
				EncodeHandler eh=new EncodeHandler(this,"newcdtrack",node);	
			} else {
				debug("cdtracksChanged(): encodecop can't use cdtracks");
			}
		}
		return(true);
	}
}
