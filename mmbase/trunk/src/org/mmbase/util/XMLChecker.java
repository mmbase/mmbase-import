/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;

import javax.xml.parsers.DocumentBuilder;

import org.mmbase.util.logging.*;
import org.w3c.dom.Document;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Check XML documents against their DTD's and be as pretty as possible about errors
 *
 * @author Cees Roele
 */
public class XMLChecker {

    // logger
    private static Logger log = Logging.getLoggerInstance(XMLChecker.class.getName());

    /**
     * Document to check
     */
    Document document;

    /**
     * Stream that recieves the errors and warnings.
     */
    PrintStream out;

    /**
     * Color for error messages
     */
    public static String error_color = "#FF8000";
    /**
     * Color for fatal error messages
     */
    public static String fatalerror_color = "#DD0000";
    /**
     * Color for warning messages
     */
    public static String warning_color = "#007700";

    /**
     * Handler for processing errors and warnings during parsing of a xml document.
     * Outputs the messages to a specified out stream.
     */
    public class XMLCheckerHandler extends DefaultHandler {
        /**
         * Stream that recieves the errors and warnings.
         */
        PrintStream out; // XXX: not necessarily needed, can use the parent stream

        /**
         * Number of warnings
         */
        int warningCount;
        /**
         * Number of errors
         */
        int errorCount;
        /**
         * Number of fatal errors
         */
        int fatalErrorCount;

        /**
         * Create a handler.
         * @param out Stream that recieves the errors and warnings.
         */
        public XMLCheckerHandler(PrintStream out) {
            this.out = out;
            warningCount = 0;
            errorCount = 0;
            fatalErrorCount = 0;
        }

        /**
         * Handling of an error in a xml document during parsing.
         * Writes the error to the out stream.
         * @param e the exception (error) that was thrown during parsing.
         */
        public void error(SAXParseException e) {
            errorCount++;
            out.println("<font color=\"" + error_color + "\">error:</font> " + e.getMessage() + "<br />\n");
        }

        /**
         * Handling of a fatal error in a xml document during parsing.
         * Writes the error to the out stream.
         * @param e the exception (fatal error) that was thrown during parsing.
         */
        public void fatalError(SAXParseException e) {
            fatalErrorCount++;
            out.println("<font color=\"" + fatalerror_color + "\">fatal error:</font> " + e.getMessage() + "<br />\n");
        }

        /**
         * Handling of a warning in a xml document during parsing.
         * Writes the warning to the out stream.
         * @param e the exception (warning) that was thrown during parsing.
         */
        public void warning(SAXParseException e) {
            warningCount++;
            out.println("<font color=\"" + warning_color + "\">warning:</font> " + e.getMessage() + "<br />\n");
        }

        /**
         * Final action after parsing of a document.
         * Writes a report (number of errors, fatal errors, and warnings) to the out stream.
         */
        public void report() {
            out.println("warning(s): " + warningCount + ", error(s): " + errorCount + ", fatal error(s): " + fatalErrorCount + "<p>\n");
        }
    }

    /**
     * Create a checker
     * @param out Stream that recieves the results of the check (errors and warnings and report).
     */
    public XMLChecker(PrintStream out) {
        this.out = out;
    }

    /**
     * Validate a specified xml file, and create a report.
     * Results of the parsing attempt (errors and warnings) are sent to the out stream.
     * @param filename path to the xml file to parse and check
     */
    public void validateAndReport(String filename) {
        try {
            XMLCheckerHandler errorhandler = new XMLCheckerHandler(out);
            DocumentBuilder db = XMLBasicReader.getDocumentBuilder(true, (ErrorHandler)errorhandler);
            db.parse(new File(filename));

            errorhandler.report();
        } catch (Exception e) {
            log.error(e);
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * Validate a specified xml file.
     * Does not curerntly do anything.
     * @param filename path to the xml file to parse and check
     * @return always <code>true</code>
     */
    public boolean validate(String filename) {
        return true;
    }
}
