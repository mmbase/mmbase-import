/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.database;

import org.mmbase.util.logging.*;
import java.sql.*;
import java.io.*;

/**
 * MySQL is a typical Relational Database. This specific
 * implementation only adds hacks to make it possible to store UTF-8 in
 * your MySQL database (depends on the 'encoding' option in
 * mmbaseroot.xml)
 *
 * @deprecated This code is scheduled for removal once MMBase has been fully converted to the new
 *             StorageManager implementation.
 * @author Michiel Meeuwissen
 * @since MMBase-1.7
 * @version $Id: MySqlStorage.java,v 1.6 2004-01-27 12:04:46 pierre Exp $
 */
public class MySqlStorage extends RelationalDatabaseStorage {
    private static Logger log = Logging.getLoggerInstance(MySqlStorage.class.getName());

    public MySqlStorage() {
        super();
    }

    // For mysql some tricks to make it UTF-8 compatible are implemented here.


    // javadoc inherited
    public String getDBText(ResultSet rs, int idx) {
        InputStream inp;
        try {
            inp = rs.getBinaryStream(idx);
            if ((inp == null) || rs.wasNull()) {
                return "";
            }
        } catch(SQLException e) {
            log.error("MySqlStorage exception "+ e.toString());
            log.error(Logging.stackTrace(e));
            return "";
        }

        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            while (true) {
                int c = inp.read();
                if (c == -1) break;
                bytes.write(c);
            }
            inp.close();
            return  new String(bytes.toByteArray(), mmb.getEncoding());
        } catch (IOException e) {
            log.error("MySqlStorage exception "+ e.toString());
            log.error(Logging.stackTrace(e));
            return "";
        }
    }


    // javadoc inherited
    public void setDBText(int i, PreparedStatement stmt, String body) {
        byte[] rawchars = null;
        try {
            rawchars = body.getBytes(mmb.getEncoding());
        } catch (Exception e) {
            log.error("String contains odd chars");
            log.error(body);
            log.error(Logging.stackTrace(e));
        }
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(rawchars);
            stmt.setBinaryStream(i, stream, rawchars.length);
            stream.close();
        } catch (Exception e) {
            log.error("Can't set ascii stream");
            log.error(Logging.stackTrace(e));
        }
    }

    public byte[] getDBByte(ResultSet rs, int idx) {
        return getDBByteBinaryStream(rs,idx);
    }
}
