/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.module.database.support;

import java.util.*;
import java.net.*;
import java.sql.*;

import org.mmbase.module.core.*;


/**
* MMJdbc2NodeInterface interface needs to be implemented to support a new database
* it is used to abstact the query's needed for mmbase for each database.
*/
public interface MMJdbc2NodeInterface {
	public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldtype,String fieldname, ResultSet rs,int i);
	public MMObjectNode decodeDBnodeField(MMObjectNode node,String fieldtype,String fieldname, ResultSet rs,int i,String prefix);
	public String getMMNodeSearch2SQL(String where,MMObjectBuilder bul);
	public String getShortedText(String tableName,String fieldname,int number);
	public byte[] getShortedByte(String tableName,String fieldname,int number);
	public byte[] getDBByte(ResultSet rs,int idx);
	public String getDBText(ResultSet rs,int idx);
	public int insert(MMObjectBuilder bul,String owner, MMObjectNode node);
	public boolean create(String tableName);
	public boolean commit(MMObjectBuilder bul,MMObjectNode node);
	public void removeNode(MMObjectBuilder bul,MMObjectNode node);
	public int getDBKey();
	public void init(MMBase mmb);
	public void setDBByte(int i, PreparedStatement stmt,byte[] bytes);
	public boolean created(String tableName);
}
