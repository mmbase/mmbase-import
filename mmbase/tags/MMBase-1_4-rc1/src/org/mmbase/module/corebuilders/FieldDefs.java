/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
$Id: FieldDefs.java,v 1.17 2001-03-04 00:24:47 daniel Exp $

$Log: not supported by cvs2svn $
Revision 1.16  2000/12/28 09:27:36  daniel
added a few set methods

Revision 1.15  2000/12/16 20:42:42  daniel
added several set calls

Revision 1.14  2000/11/16 15:21:58  pierre
pierre: added public static final TYPE_UNKNOWN

Revision 1.13  2000/11/07 14:28:55  vpro
Rico: removed TYPE_TEXT

Revision 1.12  2000/07/15 09:47:26  daniel
Changed getDBType to int

Revision 1.11  2000/07/12 13:29:05  daniel
Daniel added isKey to FieldDefs

Revision 1.10  2000/06/28 14:44:58  daniel
Daniel.. added method to get all GUINames

Revision 1.9  2000/06/20 09:28:25  wwwtech
new fielddefs for xml config

Revision 1.8  2000/06/06 20:23:20  wwwtech
multi lang support

Revision 1.7  2000/05/07 20:20:07  wwwtech
daniel: upgrades for XML configs

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
 * @$Revision: 1.17 $ $Date: 2001-03-04 00:24:47 $
 */
public class FieldDefs  {
	public final static int DBSTATE_VIRTUAL = 0;
	public final static int DBSTATE_PERSISTENT = 2;
	public final static int DBSTATE_SYSTEM = 3;
	public final static int DBSTATE_UNKNOWN = -1;


	public final static int TYPE_STRING = 1;
	public final static int TYPE_INTEGER = 2;
	public final static int TYPE_TEXT = 3; // not used anymore
	public final static int TYPE_BYTE = 4;
	public final static int TYPE_FLOAT = 5;
	public final static int TYPE_DOUBLE = 6;
	public final static int TYPE_LONG = 7;
	public final static int TYPE_UNKNOWN = -1;

	private String GUIName; 
	public Hashtable GUINames = new Hashtable(); 
	public String GUIType; 
	public int    GUISearch; 
	public int    GUIList; 
	public String DBName;
	public int    DBType;
	public int	  GUIPos;
	public int	  DBState=-1;
	public boolean	  DBNotNull=false;
	public boolean	  isKey=false;
	public int    DBPos;
	public int    DBSize=-1;
	public int SearchAge=30;


	public FieldDefs() {
	}

	public FieldDefs(String GUIName, String GUIType, int GUISearch, int GUIList, String DBName, int DBType) {
		this.GUIName=GUIName;
		this.GUIType=GUIType;
		this.GUISearch=GUISearch;
		this.GUIList=GUIList;
		this.DBName=DBName;
		this.DBType=DBType;
		this.GUIPos=2;
		this.DBState=2;
	}


	public FieldDefs(String GUIName, String GUIType, int GUISearch, int GUIList, String DBName, int DBType, int GUIPos, int DBState) {
		this.GUIName=GUIName;
		this.GUIType=GUIType;
		this.GUISearch=GUISearch;
		this.GUIList=GUIList;
		this.DBName=DBName;
		this.DBType=DBType;
		this.GUIPos=GUIPos;
		this.DBState=DBState;
	}


	public String getGUIName(String country) {
		String tmp=(String)GUINames.get(country);
		if (tmp!=null) return(tmp);
		tmp=(String)GUINames.get("us");
		if (tmp!=null) return(tmp);
		return (GUIName);
	}


	public Hashtable getGUINames() {
		return (GUINames);
	}

	public String getGUIName() {
		String tmp=(String)GUINames.get("us");
		if (tmp!=null) return(tmp);
		return (GUIName);
	}

	public String getGUIType() {
		return (GUIType);
	}

	public String getDBName() {
		return (DBName);
	}

	public int getDBType() {
		return (DBType);
	}

	public int getDBSize() {
		return (DBSize);
	}

	public boolean getDBNotNull() {
		return (DBNotNull);
	}

	public int getDBState() {
		return (DBState);
	}

	public boolean isKey() {
		return (isKey);
	}

	public int getGUISearch() {
		return (GUISearch);
	}

	public int getGUIList() {
		return (GUIList);
	}

	public int getGUIPos() {
		return (GUIPos);
	}

	public void setGUIName(String country,String value) {
		GUINames.put(country,value);
	}

	public void setGUIType(String value) {
		GUIType=value;
	}

	public void setDBName(String value) {
		DBName=value;
	}

	public void setGUIList(int value) {
		GUIList=value;
	}

	public void setGUIPos(int value) {
		GUIPos=value;
	}

	public void setGUISearch(int value) {
		GUISearch=value;
	}

	public void setDBSize(int value) {
		DBSize=value;
	}

	public void setDBType(int value) {
		DBType=value;
	}

	public void setDBPos(int value) {
		DBPos=value;
	}

	public int getDBPos() {
		return(DBPos);
	}

	public void setDBState(int value) {
		DBState=value;
	}

	public void setDBKey(boolean value) {
		isKey=value;
	}

	public void setDBNotNull(boolean value) {
		DBNotNull=value;
	}
	public String toString() {
		return("DEF GUIName="+getGUIName()+" GUIType="+GUIType+" Input="+GUIPos+" Search="+GUISearch+" List="+GUIList+" DBname="+DBName+" DBType="+DBType+" DBSTATE="+DBState+" DBNOTNULL="+DBNotNull+" DBPos="+DBPos+" DBSIZE="+DBSize+" isKey="+isKey);
	}
}
