/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

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
			return RelativeTime.convertIntToTime(time);

		} else if (field.equals("length")){
			int time = node.getIntValue("length");
			return RelativeTime.convertIntToTime(time);

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

//NOTE : getList, replace process, getEditRelativeTimeField en setEditRelativeTimeField can all be 
//removed, since we now use INFO.java for relativetime manipulation.

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

        if (command.hasMoreTokens()) {
            String token=command.nextToken();
			System.out.println("AnnotRel::replace: The nextToken = "+token);
			//RELTIME means RelativeTime.
			if (token.equals("GETFIELDRELTIME")) {
				// System.out.println("AnnotRel::replace: This method isn't implemented yet.");
        		// return("GETFIELDRELTIME not implemented yet, says the AnnotRel builder.");

                return (getEditRelativeTimeField(command.nextToken(), command.nextToken()));

            } else if (token.equals("SETFIELDRELTIME")) {
				// System.out.println("AnnotRel::replace: This method isn't implemented yet.");
        		// return("SETFIELDRELTIME not implemented yet, says the AnnotRel builder.");

                return (setEditRelativeTimeField(command));
            } 
        }
        return("No command defined, says the AnnotRel builder.");
    }		

	/**
	 *	This method retrieves the RelativeTimeField value that was filled in before by extracting it from
	 * 	the editnode, and returns it as a string to the user. 
	 * 	@param what The time field that's requested for.
	 * 	@param fieldname The fieldname of the annotrel node that's currently being edited.
	 * 	@returns The fieldname value as a String.
	 */
 	String getEditRelativeTimeField (String what, String fieldname) {
		String res = new String ();
		int timeValue = 0;

		System.out.println("AnnotRel::getEditRelativeTimeField: fieldname = "+fieldname);

		if (what.equals ("HOURS")) {
			res += RelativeTime.getHours(timeValue);
			System.out.println("AnnotRel::getEditRelativeTimeField::getHours() = "+res);
			// return ("HOURS not implemented yet.");
		} else if (what.equals ("MINUTES")) {
		 	res += RelativeTime.getMinutes(timeValue);
			System.out.println("AnnotRel::getEditRelativeTimeField::getHours() = "+res);
			// return ("MINUTES not implemented yet.");
		} else if (what.equals ("SECONDS")) {
			res += RelativeTime.getSeconds(timeValue);
			System.out.println("AnnotRel::getEditRelativeTimeField::getHours() = "+res);
			// return ("SECONDS not implemented yet.");
		} else if (what.equals ("MILLIS")) {
			res += RelativeTime.getMillis(timeValue);
			System.out.println("AnnotRel::getEditRelativeTimeField::getHours() = "+res);
			// return ("MILLIS not implemented yet.");
		} else {
			return ("No timeAttribute OR Invalid timeAttribute provided!"); 
		}

		// return ("getEditRelativeTimeField Not Implemented yet");
		return (res);
    }

	/**
	 *	This method sets the RelativeTime value by using the timefield values h,m,s and ms.  
	 * 	@param commands A StringTokenizer object containing the remainder of the $MOD String.
	 * 	@returns The calculated timeValue in milliseconds as a stringValue.
	 */
 	String setEditRelativeTimeField(StringTokenizer command) {

		String hours, minutes, seconds, millis, value;
		int time = 0;

        if (command.hasMoreTokens() && (command.countTokens() == 4) ) {

			hours   = command.nextToken();
			minutes = command.nextToken();
			seconds = command.nextToken();
			millis  = command.nextToken();
			value   = hours + ":" + minutes + ":" + seconds + "." + millis;

			time = RelativeTime.convertTimeToInt(value);
			System.out.println ("AnnotRel::setEditRelativeTimeField -> Storing time: " +time);

			return (""+time);

		} else {
			String error = "Annotrel::setEditRelativeTimeField: Error, Amount of timeValues is != 4 (h,m,s,ms)"; 
			System.out.println(error); 
			return error;
		}
	}

}
