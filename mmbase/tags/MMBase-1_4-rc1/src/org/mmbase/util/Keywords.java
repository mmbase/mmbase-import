/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.Vector;
import java.util.StringTokenizer;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.logging.*;


/**
 * Class Keywords is a util class to extract keywords from a string
 * See {#link #getIgnoreVector} for config options
 */

public class Keywords {
    private static final int MINKEYWORDLENGTH = 2;
    private static Vector ignoreVector;
    // logger
    private static Logger log = Logging.getLoggerInstance(Keywords.class.getName());


    /**
     * Retrieves the list of words from keywordstoignore.txt in the
     * MMBase config dir and stores them in the Vector ignoreVector. Each word should be
     * placed on a new line. Lines starting with # are comments and words shorter than
     * MINKEYWORDLENGTH will not be added to the ignoreVector
     * @return none, ifnoreVector si fileld with with content of keywordstoignore.txt or an
     * empty ignoreVector and a error msg written to the logs when an IOException is thrown.
     */

    private static void getIgnoreVector() {
        ignoreVector = new Vector();

        String fileName = MMBaseContext.getConfigPath()
                          + "/keywordstoignore.txt";
        char sep = System.getProperty("file.separator").charAt(0);
        fileName = fileName.replace('/', sep);
        fileName = fileName.replace('\\', sep);
        try {
            BufferedReader f = new BufferedReader( new FileReader(fileName) );
            String line;
            do {
                line = f.readLine();
                if (line == null) break;
                line = line.trim();
                if ((line.length()>=MINKEYWORDLENGTH) && (line.charAt(0)!='#'))
                    ignoreVector.addElement( line );
            } while ( true );
            f.close();
        } catch (IOException e) {
            log.error("org.mmbase.util.Keywords could not retrieve "+fileName+": "+e.getMessage());
            log.error(Logging.stackTrace(e));
        }
    }

    /**
     * createKeywords creates keywords from the passed string s
     * It ignores words shorter than MINKEYWORDLENGTH and words
     * given by ignoreVector.
     * @param String s: the string to convert to keywords
     * @return a vector containing the keywords created from string s
     */
    public static Vector createKeywords( String s ) {
        Vector results = new Vector();
        if (ignoreVector == null) getIgnoreVector();
        StringTokenizer tok = new StringTokenizer( s, " \t\n\r,.;~`!#&()+={}[]:;\"'<>?/\\|" );
        String token;

        while (tok.hasMoreTokens()) {
            token = tok.nextToken().toLowerCase();
            if ((token.length()>=MINKEYWORDLENGTH) && !ignoreVector.contains(token)
                && !results.contains(token))
                results.addElement( token );
        }// while
        return results;
    }// createKeywords
}

