/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.builders.vwms;

import java.util.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.*;

/**
 * @author Daniel Ockeloen
 */

public class EncodeCop extends Vwm implements MMBaseObserver {

	Vector EncoderHandlers=new Vector();

	public EncodeCop() {
		System.out.println("Yo Im EncodeCop");
	}


	
	public boolean probeCall() {
		System.out.println("EncodeCop-> Adding observers");
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
		System.out.println("EncodeCop -> sees that encoder "+number+" has changed type="+ctype);
		return(true);
	}

	public boolean rawaudioChanged(String number,String ctype) {
		System.out.println("EncodeCop -> sees that rawaudio "+number+" has changed type="+ctype);
		RawAudios bul=(RawAudios)Vwms.mmb.getMMObject("rawaudios");		
		if (bul!=null) {
			MMObjectNode node=bul.getNode(number);
			int status=node.getIntValue("status");
			int format=node.getIntValue("format");
			if (status==1 && format==6) {
				EncodeHandler eh=new EncodeHandler(this,"g2encode",node);	
			}
		} else {
			System.out.println("Vwm henk can't use rawaudios");
		}
		return(true);
	}



	public boolean cdtracksChanged(String number,String ctype) {
		System.out.println("EncodeCop -> sees that cdtracks "+number+" has changed type="+ctype);
		if (ctype.equals("n")) {
			CDTracks bul=(CDTracks)Vwms.mmb.getMMObject("cdtracks");		
			if (bul!=null) {
				MMObjectNode node=bul.getNode(number);
				EncodeHandler eh=new EncodeHandler(this,"newcdtrack",node);	
			} else {
				System.out.println("Vwm encodecop can't use cdtracks");
			}
		}
		return(true);
	}


}
