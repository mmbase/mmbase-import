/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: FieldDefs.java,v 1.7 2000-05-07 20:20:07 wwwtech Exp $

$Log: not supported by cvs2svn $
Revision 1.6  2000/03/31 16:15:37  wwwtech
davzev: Added DBSTATE_UNKNOWN=-1 constant.

Revision 1.5  2000/03/30 13:11:41  wwwtech
Rico: added license

Revision 1.4  2000/03/29 10:46:34  wwwtech
Rob: Licenses changed

Revision 1.3  2000/03/20 14:23:27  wwwtech
davzev: Added constant DBSTATE_SYSTEM=3

Revision 1.2  2000/03/17 14:53:42  wwwtech
davzev: Added DBSTATE constants

*/
package org.mmbase.module.corebuilders;

import java.util.*;
import java.sql.*;

/**
 * One of the core objects, Defines one field of a object type / builder, has its
 * own builder called FieldDef (hitlisted)
 *
 * @author Daniel Ockeloen
 * @author Hans Speijer
 * @$Revision: 1.7 $ $Date: 2000-05-07 20:20:07 $
 */
public class FieldDefs  {
	public final static int DBSTATE_VIRTUAL = 0;
	public final static int DBSTATE_PERSISTENT = 2;
	public final static int DBSTATE_SYSTEM = 3;
	public final static int DBSTATE_UNKNOWN = -1;

	public String GUIName; 
	public String GUIType; 
	public int	  GUISearch; 
	public int    GUIList; 
	public String DBName;
	public String DBType;
	public int	  GUIPos;
	public int	  DBState=-1;
	public boolean	  DBNotNull=false;
	public int    DBPos;
	public int    DBSize=-1;

	public FieldDefs() {
	}

	public FieldDefs(String GUIName, String GUIType, int GUISearch, int GUIList, String DBName, String DBType) {
		this.GUIName=GUIName;
		this.GUIType=GUIType;
		this.GUISearch=GUISearch;
		this.GUIList=GUIList;
		this.DBName=DBName;
		this.DBType=DBType;
		this.GUIPos=2;
		this.DBState=2;
	}


	public FieldDefs(String GUIName, String GUIType, int GUISearch, int GUIList, String DBName, String DBType, int GUIPos, int DBState) {
		this.GUIName=GUIName;
		this.GUIType=GUIType;
		this.GUISearch=GUISearch;
		this.GUIList=GUIList;
		this.DBName=DBName;
		this.DBType=DBType;
		this.GUIPos=GUIPos;
		this.DBState=DBState;
	}



	public String getGUIName() {
		return (GUIName);
	}

	public String getGUIType() {
		return (GUIType);
	}

	public String getDBName() {
		return (DBName);
	}

	public String getDBType() {
		return (DBType);
	}

	public int getDBState() {
		return (DBState);
	}

	public int getGUISearch() {
		return (GUISearch);
	}

	public int getGUIList() {
		return (GUIList);
	}

	public String toString() {
		return("DEF Name="+GUIName+" Type="+GUIType+" Input="+GUIPos+" Search="+GUISearch+" List="+GUIList+" DBname="+DBName+" DBType="+DBType+" DBSTATE="+DBState+" DBNOTNULL="+DBNotNull+" DBPos="+DBPos+" DBSIZE="+DBSize);
	}
}
