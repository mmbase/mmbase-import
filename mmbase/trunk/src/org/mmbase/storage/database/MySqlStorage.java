/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

import org.mmbase.storage.*;
import org.mmbase.util.logging.*;
import java.sql.*;
import java.io.*;

/**
 * MySQL is a typical Relational Database. This specific
 * implementation only adds hack to make it possible to store UTF-8 in
 * your MySQL database (depends on the 'encoding' option in
 * mmbaseroot.xml)
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: MySqlStorage.java,v 1.1 2003-05-02 14:25:46 michiel Exp $
 */
public class MySqlStorage extends RelationalDatabaseStorage {
    /**
     * Logging instance
     */
    private static Logger log = Logging.getLoggerInstance(MySqlStorage.class.getName());

    public MySqlStorage() {
        super();
    }


    // For mysql some tricks to make it UTF-8 compatible are implemented here.

    /**
     * Get text of a database blob
     * @javadoc
     */
    public String getDBText(ResultSet rs,int idx) {
        String str=null;
        try {
            InputStream inp = rs.getBinaryStream(idx);
            if ((inp==null) || rs.wasNull()) {
                return("");
            }
            int siz=inp.available(); // DIRTY
            if (siz<=0) return("");
            DataInputStream input=new DataInputStream(inp);
            byte[] rawchars = new byte[siz];
            input.readFully(rawchars);
            str = new String(rawchars, mmb.getEncoding());
            input.close(); // this also closes the underlying stream
        } catch (Exception e) {
            log.error("MMObjectBuilder -> MMMysql text  exception "+e);
            log.error(Logging.stackTrace(e));
            return "";
        }
        return str;
    }

    /**
     * Set text array in database
     * @javadoc
     */
    public void setDBText(int i, PreparedStatement stmt,String body) {
        byte[] rawchars=null;
        try {
            rawchars=body.getBytes(mmb.getEncoding());
        } catch (Exception e) {
            log.error("String contains odd chars");
            log.error(body);
            log.error(Logging.stackTrace(e));
        }
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(rawchars);
            stmt.setBinaryStream(i,stream,rawchars.length);
            stream.close();
        } catch (Exception e) {
            log.error("Can't set ascii stream");
            log.error(Logging.stackTrace(e));
        }
    }



}
