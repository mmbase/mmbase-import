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
import org.mmbase.util.*;

/**
 */
public class AuthRel extends InsRel {

	
	int relnumber=-1;
	
	/**
	* setDefaults for a node
	*/
	public void setDefaults(MMObjectNode node) {
		node.setValue("creat",1);
		node.setValue("us",1);
		node.setValue("look",1);
		if (relnumber==-1) {
			RelDef bul=(RelDef)mmb.getMMObject("reldef");
			if (bul!=null) {
				relnumber=bul.getGuessedByName(tableName);
				if (relnumber==-1) System.out.println("AuthRel-> Can not guess name");
			} else {
				System.out.println("AuthRel-> Can not reach RelDef");
			}
		}
		node.setValue("rnumber",relnumber);
	}


	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("creat")) {
			int val=node.getIntValue("creat");
			switch(val) {
				case -1: return("Nee");
				case 1: return("Ja");
				default: return("Unknown");
			}
		} else if (field.equals("look")) {
			int val=node.getIntValue("look");
			switch(val) {
				case -1: return("Nee");
				case 1: return("Ja");
				default: return("Unknown");
			}
		} else if (field.equals("us")) {
			int val=node.getIntValue("us");
			switch(val) {
				case -1: return("Nee");
				case 1: return("Ja");
				default: return("Unknown");
			}
		}
		return(null);
	}


}
