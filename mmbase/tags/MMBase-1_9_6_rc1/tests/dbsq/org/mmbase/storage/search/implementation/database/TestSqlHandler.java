/*
 * TestSqlHandler.java
 *
 * Created on October 21, 2002, 2:55 PM
 */

package org.mmbase.storage.search.implementation.database;

import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.database.SqlHandler;

/**
 * Test implementation of the <@link SqlHandler SqlHandler> interface.
 *
 * @author  Rob van Maris
 */
public class TestSqlHandler implements SqlHandler {
    public final static String TEST1 = "test1";
    public final static String TEST2 = "test2";
    public final static String TEST3 = "test3";
    public final static String TEST4 = "test4";
    
    /** Fixed supportLevel. */
    int supportLevel = 0;
    
    /** Creates a new instance of TestSqlHandler */
    public TestSqlHandler(int supportLevel) {
        this.supportLevel = supportLevel;
    }
    
    /** 
     * Implements this method by appending sb with TEST1. 
     */
    public void appendConstraintToSql(StringBuilder sb, Constraint constraint, 
    SearchQuery query, boolean inverse, boolean inComposite) {
        sb.append(TEST1);
    }
    
    /** 
     * Implements this method by appending sb with TEST2.
     */
    public void appendQueryBodyToSql(
    StringBuilder sb, SearchQuery query, SqlHandler firstInChain) {
        sb.append(TEST2);
    }
    
    /**
     * Implements this method by returning with TEST3 + value.
     */
    public String getAllowedValue(String value) {
        return TEST3 + value;
    }
    
    /**
     * Implements this method by returning the supportlevel set 
     * in the constructor.
     */
    public int getSupportLevel(Constraint constraint, SearchQuery query) {
        return supportLevel;
    }
    
    /**
     * Implements this method by returning the supportlevel set 
     * in the constructor.
     */
    public int getSupportLevel(int feature, SearchQuery query) {
        return supportLevel;
    }
    
    /**
     * Implements this method by returning TEST4.
     */
    public String toSql(SearchQuery query, SqlHandler firstInChain) {
        return TEST4;
    }
    
}
