/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.database;

import java.sql.*;

/**
 * Interface to support specific database things for the JDBC module
 *
 * @javadoc
 * @duplicate make this a class instead of an interface, and let {@link DatabaseSupportInformix} extend
 *            from it instead of implementing the interface.
 * @author vpro
 * @version $Id$
 */
public interface DatabaseSupport {

    public void init();
    public void initConnection(Connection con);
}
