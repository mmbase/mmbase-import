/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml.applicationdata;

import javax.sql.*;
import org.mmbase.module.core.MMBase;
import org.mmbase.storage.implementation.database.Attributes;

/**
 * This is used to export a full backup, by writing all nodes to XML.
 *
 * @since MMBase-1.9.5
 * @author Michiel Meeuwissen
 * @version $Id: FullBackupDataWriter.java 34900 2009-05-01 16:29:42Z michiel $
 */


public class SqlExecutor extends org.mmbase.util.SqlExecutor implements Runnable {

    @Override
    public String getPrefix() {
        if (prefix == null) {
            return MMBase.getMMBase().getBaseName();
        } else {
            return prefix;
        }
    }
    @Override
    public DataSource getDataSource() {
        if (dataSource == null) {
            return (DataSource) MMBase.getMMBase().getStorageManagerFactory().getAttribute(Attributes.DATA_SOURCE);
        } else {
            return dataSource;
        }
    }

}


