/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @deprecated-now does not add functionality
 * @author Arjan Houtman
 */
public class Question extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(Question.class.getName());
    /**
    * insert a new object, normally not used (only subtables are used)
    */
    /*
    public boolean create() {
        // create the main object table
        // informix
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            stmt.executeUpdate("create row type "+mmb.baseName+"_"+tableName+"_t (title varchar(255) not null"
                +", subtitle varchar(255)"
                +", intro char(2048)"
                +", body text"
                +") under "+mmb.baseName+"_object_t");
            log.debug("Created "+tableName);
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.debug("can't create type "+tableName);
            log.error(Logging.stackTrace(e));
        }
        try {
            MultiConnection con=mmb.getConnection();
            Statement stmt=con.createStatement();
            stmt.executeUpdate("create table "+mmb.baseName+"_"+tableName+" of type "+mmb.baseName+"_"+tableName+"_t ("
                +"primary key(number)) under "+mmb.baseName+"_object");
            stmt.close();
            con.close();
        } catch (SQLException e) {
            log.debug("can't create table "+tableName);
            log.error(Logging.stackTrace(e));
        }
        return(false);
    }
    */


    /**
    * insert a new object, normally not used (only subtables are used)
    */
    /*
    public int insert(String owner,MMObjectNode node) {
        String title=Escape.singlequote(node.getStringValue("title"));
        String subtitle=Escape.singlequote(node.getStringValue("subtitle"));
        String intro=Escape.singlequote(node.getStringValue("intro"));
        String body=Escape.singlequote(node.getStringValue("body"));

        if (subtitle==null) subtitle="";
        if (intro==null) intro="";
        if (body==null) body="";
        int number=-1;
        if ((number=node.getIntValue("number"))==-1) {
            number=getDBKey();
            if (number==-1) return(-1);
        }
        try {
            MultiConnection con=mmb.getConnection();
            PreparedStatement stmt=con.prepareStatement("insert into "+mmb.baseName+"_"+tableName+" values(?,?,?,?,?,?,?)");
                stmt.setInt(1,number);
                stmt.setInt(2,oType);
                stmt.setString(3,owner);
                stmt.setString(4,title);
                stmt.setString(5,subtitle);
                stmt.setString(6,intro);
                setDBText(7,stmt,body);
                stmt.executeUpdate();
                stmt.close();
                con.close();
        } catch (SQLException e) {
            log.error(Logging.stackTrace(e));
            log.debug("Error on : "+number+" "+owner+" fake");
            return(-1);
        }
        return(number);
    }
    */

    /*
    public String getGUIIndicator(MMObjectNode node) {
        String str=node.getStringValue("title");
        if (str.length()>15) {
            return(str.substring(0,12)+"...");
        } else {
            return(str);
        }
    }
    */

}

