/*
$Id: AnnotRel.java,v 1.4 2000-02-24 14:15:51 wwwtech Exp $

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

$Log: not supported by cvs2svn $
*/
package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.StringTagger;
import org.mmbase.util.scanpage;
import org.mmbase.util.RelativeTime;

/**
 * @author David van Zeventer
 * @version 8 Dec 1999 
 * @$Revision $Date
 */
public class AnnotRel extends InsRel {

    // Defining possible annotation types
    public final static int HOURS   = 0;
    public final static int MINUTES = 1;
    public final static int SECONDS = 2;
    public final static int MILLIS  = 3;
/*
    public final static int LINES   = 4;
    public final static int WORDS   = 5;
    public final static int CHARS   = 6;
    public final static int PIXELS  = 7;
    public final static int ROWS    = 8;
    public final static int COLS    = 9;
*/

	int relnumber=-1;

	/**
	 * setDefaults for a node
	 */
	public void setDefaults(MMObjectNode node) {
		// Set the default value for pos and length to 0 (0:0:0.0)
		node.setValue("pos",0);
		node.setValue("end",0);
		node.setValue("length",0);
		// All time values are default stored in milliseconds.
		node.setValue("type",MILLIS);

		if (relnumber==-1) {
			RelDef bul=(RelDef)mmb.getMMObject("reldef");
			if (bul!=null) {
				relnumber=bul.getGuessedByName(tableName);
				if (relnumber==-1) System.out.println("AnnotRel-> Can not guess name");
			} else {
				System.out.println("AnnotRel-> Can not reach RelDef");
			}
		}
		node.setValue("rnumber",relnumber);
	}

	/**
     * get GUIIndicator
     */
    public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("pos")){
			int time = node.getIntValue("pos");
			return (RelativeTime.convertIntToTime(time));
		} else if (field.equals("end")){
			int time = node.getIntValue("end");
			return (RelativeTime.convertIntToTime(time));
		} else if (field.equals("length")) {
			int time = node.getIntValue("length");
			return(RelativeTime.convertIntToTime(time));
		} else if (field.equals("type")) {
            int val=node.getIntValue("type");
            if (val==HOURS) {
                return("Hours");
            } else if (val==MINUTES) {
                return("Minuten");
            } else if (val==SECONDS) {
                return("Seconden");
            } else if (val==MILLIS) {
                return("Milliseconden");
            }

            /*
              else if (val==LINES) {
                return("Regels");
            } else if (val==WORDS) {
                return("Woorden");
            } else if (val==CHARS) {
                return("Karakters");
            } else if (val==PIXELS) {
                return("Pixels");
            } else if (val==ROWS) {
                return("Rijen");
            } else if (val==COLS) {
                return("Kolommen");
            }
            */
        }
        return(null);
    }

	/**
    * getList all for frontend code
    */
    public Vector getList(scanpage sp, StringTagger tagger, StringTokenizer tok) {
        System.out.println("AnnotRel::getList This method isn't implemented yet.");
        return(null);
    }

	/**
 	 * Execute the commands provided in the form values
	 */
	public boolean process(scanpage sp, Hashtable cmds, Hashtable vars) {
		System.out.println("AnnotRel::process: This method isn't implemented yet.");
		return false;
	}

	/**
    * replace all for frontend code
    */
	public String replace(scanpage sp, StringTokenizer command) {
		System.out.println("AnnotRel::replace: This method isn't implemented yet.");
        return("");
    }		


	/**
	* called then a local field is changed
	*/

	public boolean setValue(MMObjectNode node,String field) {
		if (field.equals("end")) {
			int pos=node.getIntValue("pos");
			int end=node.getIntValue("end");
			if (end!=-1) node.setValue("length",(end-pos));
		} else if (field.equals("pos")) {
			int pos=node.getIntValue("pos");
			int end=node.getIntValue("end");
			if (end!=-1) node.setValue("length",(end-pos));
		} else if (field.equals("length")) {
			// extra check needed to make sure we don't create a loop !
			int pos=node.getIntValue("pos");
			int end=node.getIntValue("end");
			int len=node.getIntValue("length");
			if ((end-pos)!=len) node.setValue("end",pos+len);
		}
		return(true);
	}
}
