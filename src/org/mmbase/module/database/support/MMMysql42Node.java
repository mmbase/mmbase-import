/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
*/
package org.mmbase.module.database.support;

import java.util.*;
import java.io.*;
import java.sql.*;

import org.mmbase.module.database.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.util.*;


/**
* MMMysql42Node implements the MMJdbc2NodeInterface for
* mysql this is the class used to abstact the query's
* needed for mmbase for each database.
*
* @author Daniel Ockeloen
* @version 12 Mar 1997
* @$Revision: 1.15 $ $Date: 2000-06-24 18:43:26 $
*/
public class MMMysql42Node extends MMSQL92Node implements MMJdbc2NodeInterface {


	public MMMysql42Node() {
		super();
		name="mysql";
	}


	public String parseFieldPart(String fieldname,String dbtype,String part) {
		String result="";
		boolean like=false;
		char operatorChar = part.charAt(0);
		//System.out.println("char="+operatorChar);
		String value=part.substring(1);
		int pos=value.indexOf("*");
		if (pos!=-1) {
			value=value.substring(pos+1,value.length()-1);
			like=true;
		}
		System.out.println("fieldname="+fieldname+" type="+dbtype);
		if (dbtype.equals("var") || dbtype.equals("varchar")) {
		//if (dbtype.equals("var") || dbtype.equals("VARCHAR")) {
			switch (operatorChar) {
			case '=':
			case 'E':
				// EQUAL
				if (like) {	
					result+="lower("+fieldname+") LIKE '%"+value+"%'";
				} else {
					result+="lower("+fieldname+") LIKE '%"+value+"%'";
				}
				break;
			}
		} else if (dbtype.equals("VARSTRING_EX")) {
			switch (operatorChar) {
			case '=':
			case 'E':
				// EQUAL
				result+="etx_contains("+fieldname+",Row('"+value+"','SEARCH_TYPE=PROX_SEARCH(5)'))";
				break;
			}

		} else if (dbtype.equals("LONG") || dbtype.equals("int")) {
		//} else if (dbtype.equals("LONG") || dbtype.equals("INTEGER")) {
			switch (operatorChar) {
			case '=':
			case 'E':
				// EQUAL
				result+=fieldname+"="+value;
				break;
			case 'N':
				// NOTEQUAL;
				result+=fieldname+"<>"+value;
				break;
			case 'G':
				// GREATER;
				result+=fieldname+">"+value;
				break;
			case 'g':
				// GREATEREQUAL;
				result+=fieldname+">="+value;
				break;
			case 'S':
				// SMALLER;
				result+=fieldname+"<"+value;
				break;
			case 's':
				// SMALLEREQUAL;
				result+=fieldname+"<="+value;
				break;
			}
		}
		return(result);
	}



	/**
	* set prepared statement field i with value of key from node
	*/
	private void setValuePreparedStatement( PreparedStatement stmt, MMObjectNode node, String key, int i)
		throws SQLException
	{
		String type = node.getDBType(key);
		if (type.equals("int") || type.equals("integer")) {
			stmt.setInt(i, node.getIntValue(key));
		} else if (type.equals("text") || type.equals("clob")) {
			String tmp=node.getStringValue(key);
			if (tmp!=null) {
				setDBText(i, stmt,tmp);
			} else {
				setDBText(i, stmt,"");
			}
		} else if (type.equals("byte")) {	
				setDBByte(i, stmt, node.getByteValue(key));
		} else { 
			String tmp=node.getStringValue(key);
			if (tmp!=null) {
				stmt.setString(i, tmp);
			} else {
				stmt.setString(i, "");
			}
		}
	}


}
