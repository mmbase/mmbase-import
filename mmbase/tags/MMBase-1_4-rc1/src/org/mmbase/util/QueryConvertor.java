/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import org.mmbase.module.database.support.*;
import org.mmbase.util.logging.*;

/**
 * Class for the converion of a expression string to a SQL where clause.
 * The expressions string is expected to be in 'altavista' format.
 * This means that logical operators are identified by '+' (AND), '-' (NOT),
 * and '|' (OR).
 * Comparative operators are the same as those used in SCAN (i.e. '=E', '=N', etc)
 * A wildcarded strings (with '*' or '?' characters) are automatically converted
 * to a LIKE expression.
 * <br>
 * The resulting converted expression is preceded with the SQL 'WHERE ' keyword.
 * <br>
 * Note that if the expression to convert starts with "WHERE", it is not converted at all,
 * but returned as is.
 *
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version 13 Apr 2001
 */
public class QueryConvertor {

    // logger
    //private static Logger log = Logging.getLoggerInstance(QueryConverter.class.getName());

    /**
     * Database used to convert invalid fieldnames (i.e.e keywords) to valid ones.
     */
    public static MMJdbc2NodeInterface database;

    /**
     * Creates the queryconverter
     * @item the query to convert
     * @item the db the database to use when converting fieldnames
     */
    public static String altaVista2SQL(String query,MMJdbc2NodeInterface db) {
        database=db;
        return altaVista2SQL(query);
    }

    /**
     * Creates the queryconverter
     * @item the query to convert
     */
    public static String altaVista2SQL(String query) {
        if (query.indexOf("where")!=-1 || query.indexOf("WHERE")!=-1) {
            return query;
        }

        StringBuffer buffer = new StringBuffer(64);
        // query = query.toLowerCase();
        DBQuery parsedQuery = new DBQuery(query);
        // log.debug("Converting: " + query);
        if(!query.equals(""))
            parsedQuery.sqlConversion(buffer);
        // log.debug("Converted to: "+buffer.toString());

        return buffer.toString();
    }
}

/**
 * Basic Class for parsing values and expressions.
 */
class ParseItem {

    /**
     * Appends the converted item to the stringbuffer.
     * @param result the stringbuffer to which to add the item
     */
    public void sqlConversion(StringBuffer result) {
    }

    /**
     * Returns the converted item as a <code>String</code>
     */
    public String toString() {
        StringBuffer result = new StringBuffer();
        this.sqlConversion(result);
        return result.toString();
    }
}

/**
 * Basic Class for parsing a set of conditional expressions.
 */
class DBQuery  extends ParseItem {
    // logger
    //private static Logger log = Logging.getLoggerInstance(DBQuery.class.getName());

    public Vector items = new Vector();

    /**
     * Creates the query
     * @item the query to convert
     */
    public DBQuery(String query) {
        StringTokenizer parser = new StringTokenizer(query, "+-|",true);
        ParseItem item;
        String token;

        while (parser.hasMoreTokens()) {
            item = new DBConditionItem(parser.nextToken());
            items.addElement(item);

            //log.debug("Item :" + item);
            if (parser.hasMoreTokens()) {
                item = new DBLogicalOperator(parser.nextToken());
                items.addElement(item);
                // log.debug("Logical :" + item);
            }
        }
    }

    /**
     * Appends the converted query to the stringbuffer.
     * @param result the stringbuffer to which to add the query
     */
    public void sqlConversion(StringBuffer result) {
        Enumeration enum = items.elements();

        result.append("WHERE ");

        while (enum.hasMoreElements()) {
            ((ParseItem)enum.nextElement()).sqlConversion(result);
        }
    }
}

/**
 * Class for conversion of boolean xpressions to their SQL equivalent.
 * This class converts the following conditional operators encountered in the
 * parameter passed to the constructor :<br>
 * '=='' or '=E' to '='<br>
 * '=N' to '<>'<br>
 * '=G' to '>'<br>
 * '=g' to '>='<br>
 * '=S' to '<'<br>
 * '=s' to '<='<br>
 * It also wraps string values with the SQL lower() function, and uses LIKE
 * when wildcards are used in a stringvalue.
 *
 */
class DBConditionItem extends ParseItem {
    public static final int NOTEQUAL=0, EQUAL = 1, GREATER = 2, SMALLER = 3, GREATEREQUAL=4,SMALLEREQUAL=5;
    // logger
    //private static Logger log = Logging.getLoggerInstance(DBConditionItem.class.getName());
    String identifier;
    int operator;
    DBValue value;

    /**
     * Creates the boolean expression
     * @item the expression to convert
     */
    public DBConditionItem(String item) {
        int conditionPos;
        char operatorChar;

        conditionPos = item.indexOf('=');
        identifier = item.substring(0,conditionPos);
        boolean hasPrefix = false;
        String prefix = "";
        int prefixPos = identifier.indexOf(".");
        if (prefixPos!=-1) {
            hasPrefix = true;
            prefix = identifier.substring(0,prefixPos);
            identifier = identifier.substring((prefixPos+1),identifier.length());
            //log.debug("prefix="+prefix);
            //log.debug("identifier="+identifier);
        }
        if (QueryConvertor.database!=null) {
            identifier=QueryConvertor.database.getAllowedField(identifier);
        }
        if (hasPrefix) {
            identifier = prefix +"."+ identifier;
        }

        value = DBValue.abstractCreation(item.substring(conditionPos+2));
        //log.debug("Id="+identifier);
        //log.debug("val="+value);

        operatorChar = item.charAt(conditionPos + 1);
        // log.debug("char="+operatorChar);
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

    /**
     * Appends the converted expression to the stringbuffer.
     * @param result the stringbuffer to which to add the expression
     */
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

/**
 * Basic Class for storing values.
 */
class DBValue extends ParseItem {
    /**
     * Determines whether a value is a string, a string with wildcards, or
     * a number, and returns the appropriate class.
     * @param value the value to parse
     * @return the appropriate subclass of <code>DBValue</code>
     */
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

/**
 * Class for storing numeric values.
 */
class DBNumberValue extends DBValue {
    String value;

    /**
     * Creates the numeric value
     * @value the value to convert
     */
    public DBNumberValue(String value) {
        // Protection against empty numbers
        if (value==null || value.length()==0) {
            value=""+Integer.MIN_VALUE;
        }
        this.value = value;
    }

    /**
     * Appends the converted value to the stringbuffer.
     * @param result the stringbuffer to which to add the expression
     */
    public void sqlConversion(StringBuffer result) {
        result.append(value);
    }
}

/**
 * Class for storing and converting string values.
 * Wraps the result with quotes.
 */
class DBStringValue extends DBValue {
    // logger
    //private static Logger log = Logging.getLoggerInstance(DBStringValue.class.getName());
    String value;

    /**
     * Creates the string value
     * @value the value to convert
     */
    public DBStringValue(String value) {
        this.value = value;
        // log.debug("New stringvalue:"+ value);
    }

    /**
     * Appends the converted value to the stringbuffer.
     * @param result the stringbuffer to which to add the expression
     */
    public void sqlConversion(StringBuffer result) {
        result.append('\'').append(value).append('\'');
    }

}

/**
 * Class for storing and converting string values with wildcards.
 * Wraps the result with quotes and replaces any wildcards with
 * SQL-wildcards.
 */
class DBWildcardStringValue extends DBValue {
    public static final char SINGLE = 0, MULTIPLE = 1;
    Vector conditions;

    /**
     * Creates the wildcarded string value
     * @value the value to convert
     */
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

    /**
     * Appends the converted value to the stringbuffer.
     * @param result the stringbuffer to which to add the expression
     */
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

/**
 * Class for conversion of operators to their SQL equivalent.
 * This class converts:<br>
 * '+' to 'AND'<br>
 * '-' to 'AND NOT'<br>
 * '|' to 'OR'<br>
 */
class DBLogicalOperator extends ParseItem {
    public static final char AND = '+';
    public static final char NOT = '-';
    public static final char OR ='|';

    char logOperator;

    /**
     * Creates the operator
     * @operator the original operator to convert
     */
    public DBLogicalOperator(String operator) {
        if      (operator.equals("+")) logOperator = AND;
        else if (operator.equals("-")) logOperator = NOT;
        else if (operator.equals("|")) logOperator = OR;
    }

    public DBLogicalOperator(char operator) {
        logOperator = operator;
    }

    /**
     * Appends the converted operator to the stringbuffer.
     * @param result the stringbuffer to which to add the operator
     */
    public void sqlConversion(StringBuffer result) {
        switch (logOperator) {
            case AND:
                result.append(" AND ");
                break;
            case NOT:
                result.append(" AND NOT ");
                break;
            case OR:
                result.append(" OR ");
                break;
        }
    }
}










