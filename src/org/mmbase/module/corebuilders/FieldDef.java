/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
/*
	$Id: FieldDef.java,v 1.2 2000-02-25 12:52:15 wwwtech Exp $

	$Log: not supported by cvs2svn $
*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;
import org.mmbase.module.core.*;
import org.mmbase.module.database.*;

/**
 * FieldDef, one of the meta stucture nodes it is used to define the
 * fields (using its nodes) of object types at this point has its
 * own nodes (FieldDefs) instead of MMObjectNodes but that will change
 *
 *
 * @author Daniel Ockeloen
 * @version $Id: FieldDef.java,v 1.2 2000-02-25 12:52:15 wwwtech Exp $
 */
public class FieldDef extends MMObjectBuilder {

	public FieldDef(MMBase m) {
		this.mmb=m;
		this.tableName="fielddef";
		this.description="Field defs";
		init();
		m.mmobjs.put(tableName,this);
	}

	public String getGUIIndicator(String field,MMObjectNode node) {
		if (field.equals("dbtable")) {
			int val=node.getIntValue("dbtable");
			return(""+val+"="+mmb.getTypeDef().getValue(val));
		}
		return(null);
	}


	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		super.nodeRemoteChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		super.nodeLocalChanged(number,builder,ctype);
		return(nodeChanged(number,builder,ctype));
	}

	public boolean nodeChanged(String number,String builder,String ctype) {
		MMObjectNode node=getNode(number);
		if (node!=null) {
			// figure out the table type to reload the correct FieldDefs
			int otype=node.getIntValue("dbtable");
			TypeDef tbul=(TypeDef)mmb.getMMObject("typedef");
			String bulname=tbul.getValue(otype);
			//System.out.println("Change node ! fielddef DBTABLLE="+otype+" bul="+bulname);
			if (bulname!=null) {
				MMObjectBuilder bul=(MMObjectBuilder)mmb.getMMObject(bulname);
				if (bul!=null) {
					bul.initFields(false);
				} 
			}
		}
		return(true);
	}

}
