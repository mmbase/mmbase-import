/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.util;

import java.io.*;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;

import org.mmbase.module.core.MMBaseContext;


/**
 * Take the systemId and converts it into a local file
 *
 *
 * @author Gerard van Enk
 * @version $Revision: 1.4 $ $Date: 2000-08-10 20:44:15 $
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2000/08/06 14:54:34  case
 * cjr: removed UNIX dependency from a path; added boolean for whether DTD is set
 *
 */
public class XMLEntityResolver implements EntityResolver {

    private String classname = getClass().getName();

    String dtdpath;
    boolean hasDTD; // tells whether or not a DTD is set - if not, no validition can take place

    private static boolean debug = false;

    /**
     * empty constructor
     */
    public XMLEntityResolver() {
        hasDTD = false;
        dtdpath = null;
    }

    /**
     * takes the systemId and returns the local location of the dtd
     */
    public InputSource resolveEntity(String publicId, String systemId)
    throws SAXException, IOException {

        if (systemId != null) {
            hasDTD = true;
            //does systemId contain a mmbase-dtd
            if (systemId.indexOf("http://www.mmbase.org/") != -1) {
                int i = systemId.indexOf("/dtd/");
                String dtdName = systemId.substring(i+5);
                String configpath = MMBaseContext.getConfigPath();
                String separator = "";
                if (!configpath.endsWith(File.separator)) {
                    separator = File.separator;
                }
                String dtdLocation = MMBaseContext.getConfigPath()+separator+"dtd"+File.separator+dtdName;
                if (debug) {
                    debug("dtdLocation = "+dtdLocation);
                }
                InputStreamReader dtdInputStreamReader =
                    new InputStreamReader(new FileInputStream(dtdLocation));
                InputSource dtdInputSource = new InputSource();
                dtdInputSource.setCharacterStream(dtdInputStreamReader);
                dtdpath = dtdLocation;
                return dtdInputSource;
            }
        }
        // it's a systemId we can't do anything with,
        // so let the parser decide what to do
        return null;
    }
    public boolean hasDTD() {
        return hasDTD;
    }

    /**
     * @return The actually used path to the DTD
     */
    public String getDTDPath() {
        return this.dtdpath;
    }

    private void debug( String msg ) {
        System.out.println( classname +":"+msg );
    }
}
