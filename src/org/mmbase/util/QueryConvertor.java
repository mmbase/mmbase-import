/*

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

*/
package org.mmbase.util;

import java.util.*;


public class QueryConvertor {
  
  public static String altaVista2SQL(String query) {
	if (query.indexOf("where")!=-1 || query.indexOf("WHERE")!=-1) return(query);
	StringBuffer buffer = new StringBuffer(64);
	// query = query.toLowerCase();
	DBQuery parsedQuery = new DBQuery(query);
	// System.out.println("Converting: " + query);

	if(!query.equals(""))
		parsedQuery.sqlConversion(buffer);

	// System.out.println("Converted to: "+buffer.toString());
	return buffer.toString();
  }

}

class ParseItem
{
	public void sqlConversion(StringBuffer result) {
	}

	public String toString() {
		StringBuffer result = new StringBuffer();
		this.sqlConversion(result);
		return result.toString();
	}
}
 
class DBQuery  extends ParseItem {
	public Vector items = new Vector();

	public DBQuery(String query) {
		StringTokenizer parser = new StringTokenizer(query, "+-",true);
		ParseItem item;
		String token;

		while (parser.hasMoreTokens()) {
			item = new DBConditionItem(parser.nextToken());
			items.addElement(item);

			// System.out.println("Item :" + item);
			if (parser.hasMoreTokens()) {
				item = new DBLogicalOperator(parser.nextToken());
				items.addElement(item);
			    // System.out.println("Logical :" + item);
			}
		}
	}
	
	public void sqlConversion(StringBuffer result) {
		Enumeration enum = items.elements();

		result.append("WHERE ");
		
		while (enum.hasMoreElements()) {
		    ((ParseItem)enum.nextElement()).sqlConversion(result);
		}
	}
}

class DBConditionItem extends ParseItem {
	public static final int NOTEQUAL=0, EQUAL = 1, GREATER = 2, SMALLER = 3, GREATEREQUAL=4,SMALLEREQUAL=5;
	String identifier;
	int operator;
	DBValue value;  

	public DBConditionItem(String item) {
		int conditionPos;
		char operatorChar;

		conditionPos = item.indexOf('=');
		identifier = item.substring(0,conditionPos);
		value = DBValue.abstractCreation(item.substring(conditionPos+2));
		// System.out.println("Id="+identifier);
		// System.out.println("val="+value);

		operatorChar = item.charAt(conditionPos + 1);
		// System.out.println("char="+operatorChar);
		switch (operatorChar) {
		case '=':
		case 'E':
			operator = EQUAL;
			break;
		case 'N':
			operator = NOTEQUAL;
			break;
		case 'G':
			operator = GREATER;
			break;
		case 'g':
			operator = GREATEREQUAL;
			break;
		case 'S':
			operator = SMALLER;
			break;
		case 's':
			operator = SMALLEREQUAL;
			break;
		}
	}
			
	public void sqlConversion(StringBuffer result) {
		
		if (value instanceof DBWildcardStringValue || value instanceof DBStringValue)
			result.append("lower(").append(identifier).append(")");
			//result.append("").append(identifier).append("");
		else 
			result.append(identifier);

		if (value instanceof DBWildcardStringValue) {
			result.append(" LIKE ");
		}
		else {
			switch (operator) {
			case EQUAL:
				result.append(" = ");
				break;
			case NOTEQUAL:
				result.append(" <> ");
				break;
			case GREATER:
				result.append(" > ");
				break;
			case GREATEREQUAL:
				result.append(" >= ");
				break;
			case SMALLER:
				result.append(" < ");
				break;
			case SMALLEREQUAL:
				result.append(" <= ");
				break;
			default:
				result.append(" = ");
			}
		}
		value.sqlConversion(result);
	}
}

class DBValue extends ParseItem {
	public static DBValue abstractCreation(String value) {
		value = value.toLowerCase();
		if (value.startsWith("'")) {
			if (value.indexOf('?') >= 0 || value.indexOf('*') >= 0)
				return new DBWildcardStringValue(Strip.Chars(value,"' ",Strip.BOTH));
			else
				return new DBStringValue(Strip.Chars(value,"' ",Strip.BOTH));
		}
		else
			return new DBNumberValue(value);   
	}
}

class DBNumberValue extends DBValue {
	String value;

	public DBNumberValue(String value) {
		// Protection against empty numbers
		if (value==null || value.length()==0) {
			value=""+Integer.MIN_VALUE;
		}
		this.value = value;
	}

	public void sqlConversion(StringBuffer result) {
	    result.append(value);
	}
}

class DBStringValue extends DBValue {
	String value;
	
	public DBStringValue(String value) {
		this.value = value;
		// System.out.println("New stringvalue:"+ value);
	}

	public void sqlConversion(StringBuffer result) {
		result.append('\'').append(value).append('\'');
	}

}

class DBWildcardStringValue extends DBValue {
	public static final char SINGLE = 0, MULTIPLE = 1;
	Vector conditions;

	public DBWildcardStringValue(String value) {
		StringTokenizer parser = new StringTokenizer(value,"*?",true);
		String token;

		conditions = new Vector();

		while (parser.hasMoreTokens()) {
			token = parser.nextToken();
			if (token.equals("*")) 
				conditions.addElement(new Integer(MULTIPLE)); 
			else { 
				if (token.equals("?")) 
					conditions.addElement(new Integer(SINGLE));
				else conditions.addElement(token);
			}
		}
	}

	public void sqlConversion(StringBuffer result) {
		result.append("'");
		Enumeration enum = conditions.elements();
		Object item;

		while (enum.hasMoreElements()) {
			item = enum.nextElement();
			if (item instanceof String)
				result.append(item);
			else if (item instanceof Integer) {
				switch (((Integer)item).intValue()) {
				case SINGLE:
					result.append('_');
					break;
				case MULTIPLE:
					result.append('%');
					break;
				}
			}
		}
		result.append("'");
	}
}

class DBLogicalOperator extends ParseItem {
	public static final char AND = '+', NOT = '-';

	char logOperator;

    public DBLogicalOperator(String operator) {
		if      (operator.equals("+")) logOperator = '+';
		else if (operator.equals("-")) logOperator = '-';
	}

	public DBLogicalOperator(char operator) {
		logOperator = operator;
	}

	public void sqlConversion(StringBuffer result) {
		switch (logOperator) {
		case AND:
			result.append(" AND ");
			break;
		case NOT:
			result.append(" AND NOT ");
		}
	}
}










