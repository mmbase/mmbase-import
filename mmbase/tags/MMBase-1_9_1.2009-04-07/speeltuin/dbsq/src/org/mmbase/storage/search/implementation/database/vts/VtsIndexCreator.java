/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database.vts;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.mmbase.module.core.*;
import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * The Vts index creator creates Verity Text Search indices,
 * when used with an Informix database and a Verity Text Search datablade.
 * This class is provided as a utility to supplement the 
 * {@link VtsSqlHandler VtsSqlHandler}.
 * <p>
 * When run as an application, the index creator reads a list of vts-indices 
 * from a configuration file, and creates the indices that are not present 
 * already.
 * The configurationfile must be named <em>vtsindices.xml</em> and located
 * inside the <em>databases</em> configuration directory. 
 * It's dtd is located in the directory 
 * <code>org.mmbase.storage.search.implementation.database.vts.resources</code>
 * in the MMBase source tree and 
 * <a href="http://www.mmbase.org/dtd/vtsindices.dtd">here</a> online. 
 *
 * @author Rob van Maris
 * @version $Id: VtsIndexCreator.java,v 1.4 2003-12-23 10:59:06 robmaris Exp $
 * @since MMBase-1.7
 */
public class VtsIndexCreator {
    
    /** Database connection. */
    private Connection con = null;
    
    /** 
     * Creates a new instance of VtsIndexCreator. 
     *
     * @param path Path to the vts indices configuration file.
     */
    public VtsIndexCreator() {}
    
    /**
     * Application main method. 
     * <p>
     * Reads vtsindices configuration file, and creates the vts indices
     * that are not already created.
     *
     * @param args The command line arguments, should be path to
     *        MMBase configuration directory.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Command line arguments not as expected,"
                + "should be path to MMBase configuration directory.");
            System.exit(1);
        }
        try {
            // Start MMBase instance.
            // TODO: maybe this is a bit of overkill, 
            //       all we need is a database connection.
            MMBaseContext.init(args[0], false);
            
            // Execute tasks.
            new VtsIndexCreator().execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Kill MMBase instance.
            System.exit(0);
        }
    }
    
    /**
     * Executes the tasks: reads configuration file and creates indices
     * as needed.
     * Requires MMBase to be started.
     */
    public void execute() throws Exception {
        try {
            // Read config.
            File vtsConfigFile = new File(
                MMBaseContext.getConfigPath() + "/databases/vtsindices.xml");
            XmlVtsIndicesReader configReader = 
                new XmlVtsIndicesReader(
                    new InputSource(
                        new BufferedReader(
                            new FileReader(vtsConfigFile))));
            
            // Connect.
            con = MMBase.getMMBase().getConnection();
            
            boolean nolog = configReader.getNolog();
            Enumeration iSbspaces = configReader.getSbspaceElements();
            while (iSbspaces.hasMoreElements()) {
                Element sbspace = (Element) iSbspaces.nextElement();
                String sbspaceName = configReader.getSbspaceName(sbspace);
                Enumeration iVtsindices = configReader.getVtsindexElements(sbspace);
                while (iVtsindices.hasMoreElements()) {
                    Element vtsindex = (Element) iVtsindices.nextElement();
                    String vtsindexName = configReader.getVtsindexValue(vtsindex);
                    String table = configReader.getVtsindexTable(vtsindex);
                    String field = configReader.getVtsindexField(vtsindex);
                    if (!vtsIndexExists(vtsindexName)) {
                        createVtsIndex(nolog, sbspaceName, 
                            vtsindexName, table, field);
                    }
              }
            }
            
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }
    
    /**
     * Tests if a Vts index with already exists with a specified name.
     * 
     * @param vtsindexName The index name.
     * @return True if a Vts index already exists with this name,
     *         false otherwise.
     */
    private boolean vtsIndexExists(String vtsindexName) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(
                "SELECT * FROM sysindexes WHERE idxname = ?");
            ps.setString(1, vtsindexName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Index " + vtsindexName + " exists already.");
                return true;
            } else {
                System.out.println("Index " + vtsindexName + " does not exist already.");
                return false;
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }
    
    /**
     * Creates new Vts index.
     *
     * @param nolog Value for the <code>NOLOG</code> parameter.
     * @param sbspace The sbspace to use.
     * @param name The index name.
     * @param table The table.
     * @param field The field.
     */
    private void createVtsIndex(boolean nolog, String sbspace,
        String name, String table, String field) 
        throws SQLException {
            String operatorclass = getOperatorClass(table, field);
            String sqlCreateIndex = 
                "CREATE INDEX " + name 
                + " ON " + table + " (" + field + " " + operatorclass + ")" 
                + " USING vts (NOLOG=" + String.valueOf(nolog).toUpperCase()
                + ") IN " + sbspace;
            
            Statement st = null;
            try {
                System.out.println(sqlCreateIndex);
                st = con.createStatement();
                // TODO: uncomment to activate
//                st.executeUpdate(sqlCreateIndex);
                System.out.println("Index " + name + " created.");
            } finally {
                if (st != null) {
                    st.close();
                }
            }
    }
    
    /**
     * Determines the appropriate operator class for a field to be indexed,
     * based on metadata retrieved from the database.
     *
     * @param table The table.
     * @param field The field.
     * @return The operator class.
     */
    private String getOperatorClass(String table, String field) throws SQLException {
        DatabaseMetaData metadata = con.getMetaData();
        ResultSet columninfo = metadata.getColumns(null, "", table, field);
        columninfo.next();
        String typeName = columninfo.getString("TYPE_NAME").toLowerCase();
        if (typeName.equals("blob")) {
            return "Vts_blob_ops";
        } else if (typeName.equals("clob")) {
            return "Vts_clob_ops";
        } else if (typeName.equals("char")) {
            return "Vts_char_ops";
        } else if (typeName.equals("lvarchar")) {
            return "Vts_lvarchar_ops";
        } else if (typeName.equals("varchar")) {
            return ("Vts_varchar_ops");
        } else {
            throw new IllegalArgumentException(
                "The field " + field + " of table " + table
                + " is not of an appropriate type for a Vts index.");
        }
    }
}
