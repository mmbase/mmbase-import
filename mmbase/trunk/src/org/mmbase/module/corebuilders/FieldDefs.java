/*
$Id: FieldDefs.java,v 1.2 2000-03-17 14:53:42 wwwtech Exp $

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

$Log: not supported by cvs2svn $
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
 * @$Revision: 1.2 $ $Date: 2000-03-17 14:53:42 $
 */
public class FieldDefs  {
	public final static int DBSTATE_VIRTUAL = 0;
	public final static int DBSTATE_PERSISTENT = 2;

	public String GUIName; 
	public String GUIType; 
	public int	  GUISearch; 
	public int    GUIList; 
	public String DBName;
	public String DBType;
	public int	  GUIPos;
	public int	  DBState;
	public int    DBPos;

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
}
