/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import org.mmbase.util.logging.*;
import org.mmbase.util.xml.DatabaseReader;
import org.xml.sax.InputSource;


/**
 * Used to parse and retrieve data from a database configuration file.
 * Code has been moved to {@link org.mmbase.util.xml.DatabaseReader}.
 * This class functions as a place-holder for backward-compatibility, giving people
 * a chance to rewrite their code to use the new class.
 * It will be removed in a future MMBase release
 *
 * @deprecated-now use {@link org.mmbase.util.xml.DatabaseReader}
 * @author Case Roole
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @version $Id: XMLDatabaseReader.java,v 1.22 2003-08-29 12:12:30 keesj Exp $
 */
public class XMLDatabaseReader extends DatabaseReader  {
    // logger
    private static Logger log = Logging.getLoggerInstance(XMLDatabaseReader.class.getName());

    /**
     * Constructor
     * @param path the filename
     */
    public XMLDatabaseReader(String path) {
        super(path);
        log.warn("Instantiating obsolete class XMLDatabaseReader, use org.mmbase.xml.DatabaseReader instead");
    }

    /**
     * Constructor.
     *
     * @param source Inputsource to the xml document.
     * @since MMBase-1.7
     */
    public XMLDatabaseReader(InputSource source) {
        super(source);
        log.warn("Instantiating obsolete class XMLDatabaseReader, use org.mmbase.xml.DatabaseReader instead");
    }


}
