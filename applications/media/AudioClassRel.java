/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
/*
	$Id: AudioClassRel.java,v 1.1 2002-05-29 10:54:29 rob Exp $

	$Log: not supported by cvs2svn $
	Revision 1.2  2002/04/26 11:49:59  wwwtech
	marcel: changed builders to use logging from org.mmbase.util.logging
	
	Revision 1.1.1.1  2000/10/31 13:38:19  wwwtech
	Initial vpro
	
*/
package speeltuin.media;

import java.util.*;

import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Rico Jansen
 * @version $Id: AudioClassRel.java,v 1.1 2002-05-29 10:54:29 rob Exp $
 */
public class AudioClassRel extends InsRel {

    private static Logger log
    = Logging.getLoggerInstance(AudioClassRel.class.getName());

	int relnumber=-1;

	/**
	* setDefaults for a node
	*/
	public void setDefaults(MMObjectNode node) {
		if (relnumber==-1) {
			RelDef bul=(RelDef)mmb.getMMObject("reldef");
			if (bul!=null) {
				relnumber=bul.getGuessedByName(tableName);
				if (relnumber==-1) log.debug("setDefaults(): Can not guess name");
			} else {
				log.debug("setDefaults(): Can not reach RelDef");
			}
		}
		node.setValue("rnumber",relnumber);
	}

	/**
	* get GUIIndicator
	*/
	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("class")) {
			return(getAudioClassificationString(node.getIntValue("class")));
			
		}
		return(null);
	}

	public Object getValue(MMObjectNode node, String field) {
		if (field.equals("showclass")) {
			return getAudioClassificationString( node.getIntValue("class") );
		} else {
			return super.getValue( node, field );
		}
	}

	private String getAudioClassificationString(int classification) {
		String rtn="";

		switch(classification) {
			case 0:
				rtn="";					// Default
				break;
			case 1:
				rtn="Track";			// Recording of a studio track
				break;
			case 2:
				rtn="Studio Session";	// Recording of a live session in a Studio
				break;
			case 3:
				rtn="Live Recording";	// Recording of a live performance
				break;
			case 4:
				rtn="DJ Set";			// Recording of a DJ-set
				break;
			case 5:
				rtn="Remix";			// Remixed by
				break;
			case 6:
				rtn="Interview";		// Interview of
				break;
			case 7:
				rtn="Report";			// Report of
				break;
			case 8:
				rtn="Jingle";			// Jingle
				break;
			case 9:
				rtn="Program";			// Broadcast of a program
				break;
			default:
				rtn="Unknown";			// Unknown
				break;
		}
		return(rtn);
	}
}
