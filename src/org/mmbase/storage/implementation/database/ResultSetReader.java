/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.implementation.database;
import java.sql.*;

/**
 * Instances of this can be fed to {@link DatabaseStorageManager#executeQuery}.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.1
 * @version $Id: ResultSetReader.java,v 1.1 2009-01-30 20:07:33 michiel Exp $
 */
public interface ResultSetReader {


    void read(ResultSet rs) throws SQLException;

}
