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
 * @author Rico Jansen
 */
public class FunctionRel extends InsRel {

	public FunctionRel() {
	}
	
	/**
	* setDefaults for a node
	*/
	int relnumber=-1;

	public void setDefaults(MMObjectNode node) {
		node.setValue("task",1);
		if (relnumber==-1) {
			RelDef bul=(RelDef)mmb.getMMObject("reldef");
			if (bul!=null) {
				relnumber=bul.getGuessedByName(tableName);
				if (relnumber==-1) System.out.println("FunctionRel-> Can not guess name");
			} else {
				System.out.println("FunctionRel-> Can not reach RelDef");
			}
		}
		node.setValue("rnumber",relnumber);
	}

	/**
	* get GUIIndicator
	*/
	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("task")) {
			int val=node.getIntValue("task");
			if (val==1) {
				return("Uitvoerende");
			} else if (val==2) {
				return("Maker");
			}
		}
		return(null);
	}

}
