/*
$Id: g2encoders.java,v 1.3 2000-03-21 15:39:18 wwwtech Exp $

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

$Log: not supported by cvs2svn $
Revision 1.2  2000/02/24 15:07:16  wwwtech
Davzev added debug() methods and calls to all methods.


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
 * @$Revision: 1.3 $ $Date: 2000-03-21 15:39:18 $
 */
public class g2encoders extends ServiceBuilder implements MMBaseObserver {

	private String classname = getClass().getName();
	private boolean debug = true;
	//  private void debug(String msg){System.out.println(classname+":"+msg);}

	public g2encoders() {
		if(debug) debug("setTableName("+tableName+")");
	}

	public void setTableName(String tableName) {
		if(debug) debug("setTableName("+tableName+")");

		super.setTableName(tableName);
		MMServers bul=(MMServers)mmb.getMMObject("mmservers");
		if (bul!=null) {
			bul.setCheckService(tableName);
		} else {
			if(debug) debug("setTableName("+tableName+"): ERROR: mmbase could not be found!");
		}
	}

	public boolean nodeRemoteChanged(String number,String builder,String ctype) {
		boolean result = false;
		if( debug ) debug("nodeRemoteChanged("+number+","+builder+","+ctype+")");

		super.nodeRemoteChanged(number,builder,ctype);
		result = nodeChanged(number,builder,ctype);

		if( debug ) debug("nodeRemoteChanged("+number+","+builder+","+ctype+"): return("+result+")");
		return result;
	}

	public boolean nodeLocalChanged(String number,String builder,String ctype) {
		boolean result = false;
		if( debug ) debug("nodeLocalChanged("+number+","+builder+","+ctype+")");

		super.nodeLocalChanged(number,builder,ctype);
		result = nodeChanged(number,builder,ctype);

		if( debug ) debug("nodeLocalChanged("+number+","+builder+","+ctype+"): returning("+result+")");
		return result;
	}

	public boolean nodeChanged(String number,String builder,String ctype) {
		boolean result = true;
		if( debug ) debug("nodeLocalChanged("+number+","+builder+","+ctype+"), do nothing, return("+result+")");
		return(result);
	}
}
