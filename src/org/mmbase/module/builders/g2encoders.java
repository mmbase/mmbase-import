/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/

package org.mmbase.module.builders;

import java.util.*;
import java.sql.*;
import java.io.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 */
public class g2encoders extends ServiceBuilder implements MMBaseObserver {

	public g2encoders() {
	}

	public void setTableName(String tableName) {
		super.setTableName(tableName);
		MMServers bul=(MMServers)mmb.getMMObject("mmservers");
		if (bul!=null) {
			bul.setCheckService(tableName);
		}
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
		return(true);
	}

     	
}
