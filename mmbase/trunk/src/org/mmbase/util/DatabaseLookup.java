/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import org.w3c.dom.traversal.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;

import java.io.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

public class DatabaseLookup {  
    private static Logger log = Logging.getLoggerInstance(DatabaseLookup.class.getName());
    private File databaseConfigPath;
    private Document document;

    public DatabaseLookup(File lookupConfig, File databaseConfigPath) {
	this.databaseConfigPath = databaseConfigPath;

        try {
            InputSource in = new InputSource(new FileInputStream(lookupConfig));
            document = XMLBasicReader.getDocumentBuilder().parse(in);
        } catch(org.xml.sax.SAXException se) {
            String message = "error loading configfile :'" + lookupConfig + "'" + Logging.stackTrace(se);
	    log.error(message);
            throw new RuntimeException(message);
        } catch(java.io.IOException ioe) {
            String message = "error loading configfile :'" + lookupConfig + "'" + Logging.stackTrace(ioe);
            throw new RuntimeException(message);
        }
    }

    public File getDatabaseConfig(Connection connection) {
	// process all the filters and when we have a match, return the result!
        String xpath = "/database-filters/filter";
        log.debug("gonna execute the query:" + xpath );
        NodeIterator found;
        try {
            found = org.apache.xpath.XPathAPI.selectNodeIterator(document, xpath);
        } 
	catch(javax.xml.transform.TransformerException te) {
            String message = "error executing xpath:'"+xpath+"'" + Logging.stackTrace(te);
	    log.error(message);
            throw new RuntimeException(message);
        }
        for(Node current = found.nextNode(); current != null; current = found.nextNode()) {
            NamedNodeMap nnm = current.getAttributes();	    
            Node databaseConfig = nnm.getNamedItem("database-config");
	    String databaseConfigName = databaseConfig.getNodeValue();
	    log.debug("gonna examine if we need to use the config:'" + databaseConfigName +"'");
	    if(match(databaseConfigName, current, connection)) {
		// return the config file which should be used!
		return new File(databaseConfigPath, databaseConfigName + ".xml");
            }
        }
	String error = "unresolved connection information:\n";
	error += databaseInformation(connection);
	error += "\nPlease add resolve information to lookup.xml, since this database is not known to the system.";
	throw new RuntimeException(error);
    }

    private String databaseInformation(Connection connection) {
	try {
	    DatabaseMetaData dmd = connection.getMetaData();
	    String databaseProductName = dmd.getDatabaseProductName();
	    String databaseProductVersion = dmd.getDatabaseProductVersion();
	    int driverMajorVersion = dmd.getDriverMajorVersion();
	    int driverMinorVersion = dmd.getDriverMinorVersion();
	    String driverName = dmd.getDriverName();
	    String driverVersion = dmd.getDriverVersion();
	    String result =  "database product name:'" + databaseProductName + "'";
	    result += "\ndatabase product version:'" + databaseProductVersion + "'";
	    result += "\ndriver class:'"+connection.getClass().getName()+"'";
	    result += "\ndriver major  version #" + driverMajorVersion;
	    result += "\ndriver minor  version #" + driverMinorVersion;
	    result += "\ndriver name '" + driverName + "'";
	    result += "\ndriver version '" + driverVersion + "'";
	    return result;
	}
	catch(java.sql.SQLException sqle) {
	    throw new RuntimeException(Logging.stackTrace(sqle));
	}
    }

    private boolean match(String database, Node filterNode, Connection connection) {
	// process all the conditions of this filternode...
	int i=0;
	Node current = filterNode.getFirstChild();
	while(current != null) {
	    // look if we have a condition (is an element)
	    if(current.getNodeType()== Node.ELEMENT_NODE) {
		Element condition = (Element) current;
		log.debug("looking at:" + condition);
		try {
		    // we have the following string value's:
		    //    driver-class driver-name driver-version database-product-name database-product-version
		    // and the following int values:
		    //  driver-minor-version driver-major-version
		    DatabaseMetaData dmd = connection.getMetaData();
		    if(condition.getTagName().equals("driver-class")) {
			if(!startMatch(condition, connection.getClass().getName())) return false;
		    }
		    else if(condition.getTagName().equals("driver-name")) {
			if(!match(condition, dmd.getDriverName())) return false;
		    }
		    else if(condition.getTagName().equals("driver-version")) {
			if(!match(condition, dmd.getDriverVersion())) return false;
		    }
		    else if(condition.getTagName().equals("database-product-name")) { 
			if(!match(condition, dmd.getDatabaseProductName())) return false;
		    }
		    else if(condition.getTagName().equals("database-product-version")) {
			if(!match(condition, dmd.getDatabaseProductVersion())) return false;
		    }
		    else if(condition.getTagName().equals("driver-major-version")) {
			if(!match(condition, dmd.getDriverMajorVersion())) return false;
		    }
		    else if(condition.getTagName().equals("driver-minor-version")) {
			if(!match(condition, dmd.getDriverMinorVersion())) return false;
		    }
		    else {
			throw new RuntimeException("tag with name:'"+condition.getTagName()+"' unknown.");
		    }
		    i++;
		}
		catch(java.sql.SQLException sqle) {
		    new RuntimeException(Logging.stackTrace(sqle));
		}
	    }
	    current = current.getNextSibling();
	}
	log.info("Selected database: '"+database+"' based on #" + i + " rules in it's filter.");
	if(i<=1) { log.warn("Please add additional filter,  add filter-rules and/or change the order in file: config/databases/lookup.xml, database lookup information:\n"+databaseInformation(connection));
	}
	return true;
    }

    private boolean match(Node node, String value) {
	// im stupid, this should work...
	return node.getFirstChild().getNodeType() == Node.TEXT_NODE
	    && node.getFirstChild().getNodeValue().equals(value)
	    && node.getFirstChild().getNextSibling() == null;
    }
    private boolean startMatch(Node node, String value) {
	// im stupid, this should work...
	return node.getFirstChild().getNodeType() == Node.TEXT_NODE
	    && value.startsWith(node.getFirstChild().getNodeValue())
	    && node.getFirstChild().getNextSibling() == null;
    }
    private boolean match(Node node, int value) {
	if(node.getFirstChild().getNodeType() != Node.TEXT_NODE) return false;
	Element element = (Element) node;

	String stringValue = element.getFirstChild().getNodeValue();
	if(element.getFirstChild().getNextSibling() != null) return false;

	// look what our value is
	int foundValue = Integer.parseInt(stringValue);
	String condition = element.getAttributes().getNamedItem("condition").getNodeValue();
	if(condition.equals("equals")) return foundValue == value;
	else if(condition.equals("from")) return foundValue <=  value;
	else if(condition.equals("until")) return foundValue >  value;
	else throw new RuntimeException("condition: '" + condition + "' unknown");
    }
}
