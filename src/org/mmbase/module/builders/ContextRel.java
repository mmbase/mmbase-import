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
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Rico Jansen
 */
public class ContextRel extends InsRel {

	public ContextRel() {
	}


	int relnumber=-1;
	
	/**
	* setDefaults for a node
	*/
	public void setDefaults(MMObjectNode node) {
		if (relnumber==-1) {
			RelDef bul=(RelDef)mmb.getMMObject("reldef");
			if (bul!=null) {
				relnumber=bul.getGuessedByName(tableName);
				if (relnumber==-1) System.out.println("ContextRel-> Can not guess name");
			} else {
				System.out.println("AuthRel-> Can not reach RelDef");
			}
		}
		node.setValue("rnumber",relnumber);
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("contexttype")) {
			int val=node.getIntValue("contexttype");
			if (val==1) {
				return("Voor");
			} else if (val==2) {
				return("Tegen");
			} else if (val==3) {
				return("Oorzaak");
			} else if (val==4) {
				return("Gevolg");
			} else if (val==5) {
				return("Neutraal");
			}
		}
		return(null);
	}

}
