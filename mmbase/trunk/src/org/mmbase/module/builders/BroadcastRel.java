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
 * @author Daniel Ockeloen
 */
public class BroadcastRel extends InsRel {

	public BroadcastRel() {
	}

	int relnumber=-1;
	
	/**
	* setDefaults for a node
	*/
	public void setDefaults(MMObjectNode node) {
		System.out.println("BROADCASTREL NAME="+tableName);
		node.setValue("rerun",0);
		if (relnumber==-1) {
			RelDef bul=(RelDef)mmb.getMMObject("reldef");
			if (bul!=null) {
				relnumber=bul.getGuessedByName(tableName);
				if (relnumber==-1) System.out.println("BcastRel-> Can not guess name");
			} else {
				System.out.println("BroadCastRel-> Can not reach RelDef");
			}
		}
		node.setValue("rnumber",relnumber);
	}
}
