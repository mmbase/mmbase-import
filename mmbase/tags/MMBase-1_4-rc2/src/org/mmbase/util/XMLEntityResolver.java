/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.util;

import java.io.*;

import org.xml.sax.*;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.logging.*;

/**
 * Take the systemId and converts it into a local file
 *
 *
 * @author Gerard van Enk
 * @version $Revision: 1.10 $ $Date: 2001-07-16 10:08:16 $
 */
public class XMLEntityResolver implements EntityResolver {

    private static Logger log = Logging.getLoggerInstance(XMLEntityResolver.class.getName());

    String dtdpath;
    boolean hasDTD; // tells whether or not a DTD is set - if not, no validition can take place

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
                String dtdLocation = configpath + File.separator + "dtd"
		                     + File.separator + dtdName;
                log.debug("dtdLocation = "+dtdLocation);
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
}
