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
public class RoleRel extends InsRel {

	public RoleRel() {
	}
	
	/**
	* setDefaults for a node
	*/
	int relnumber=-1;

	public void setDefaults(MMObjectNode node) {
		if (relnumber==-1) {
			RelDef bul=(RelDef)mmb.getMMObject("reldef");
			if (bul!=null) {
				relnumber=bul.getGuessedByName(tableName);
				if (relnumber==-1) System.out.println("RoleRel-> Can not guess name");
			} else {
				System.out.println("RoleRel-> Can not reach RelDef");
			}
		}
		node.setValue("rnumber",relnumber);
	}


}
