/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import org.mmbase.module.core.MMBase;
import org.mmbase.util.xml.BuilderReader;
import org.mmbase.util.logging.*;

/**
 * Used to parse and retrieve data from a builder configuration file.
 * Code has been moved to {@link org.mmbase.util.xml.BuilderReader}.
 * This class functions as a place-holder for backward-compatibility, giving people
 * a chance to rewrite their code to use the new class.
 * It will be removed in a future MMBase release
 *
 * @deprecated-now use {@link org.mmbase.util.xml.BuilderReader}
 * @author Case Roole
 * @author Rico Jansen
 * @author Pierre van Rooden
 * @version $Id: XMLBuilderReader.java,v 1.31 2003-04-11 11:05:49 pierre Exp $
 */
public class XMLBuilderReader extends BuilderReader {

    // logger
    private static Logger log = Logging.getLoggerInstance(XMLBuilderReader.class.getName());

    /**
     * Creates an instance by reading a builder configuration (xml) file.
     * @since MMBase-1.6
     * @param filename path to the builder configuration file to parse
     * @param mmb The MMBase instance. Used to resolve inheritance of builders
     */
    public XMLBuilderReader(String filename, MMBase mmb) {
        super(filename, mmb);
        log.warn("Instantiating obsolete class XMLBuilderReader, use org.mmbase.xml.BuilderReader instead");
    }

    /**
     * Creates an instance by reading a builder configuration (xml) file.
     * A parser created with this constructor does not resolve inheritance, but maintains
     * the activity status as it is set in the file.
     * This call should be used if only the actual information in the xml file is needed.
     * @param filename path to the builder configuration file to parse
     */
    public XMLBuilderReader(String filename) {
        super(filename);
        log.warn("Instantiating obsolete class XMLBuilderReader, use org.mmbase.xml.BuilderReader instead");
    }


}

