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
 * Take the systemId and converts it into a local file, using the MMBase config path
 *
 * @author Gerard van Enk
 * @version $Revision: 1.11 $ $Date: 2001-10-18 11:05:08 $
 */
public class XMLEntityResolver implements EntityResolver {

    private static Logger log = Logging.getLoggerInstance(XMLEntityResolver.class.getName());

    private String dtdpath;
    private boolean hasDTD; // tells whether or not a DTD is set - if not, no validition can take place

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
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

        //does systemId contain a mmbase-dtd
        if ((systemId == null) || (systemId.indexOf("http://www.mmbase.org/") == -1)) {
            // it's a systemId we can't do anything with,
            // so let the parser decide what to do
            return null;
        } else if (!canResolve()) {
            // cannot determine the dtd - create an empty dtd stream instead
            return new InputSource(new StringReader(""));
        } else {
            hasDTD = true;
            int i = systemId.indexOf("/dtd/");
            String dtdName = systemId.substring(i+5);
            String configpath = MMBaseContext.getConfigPath();
            if (configpath==null) return null;
            String dtdLocation = configpath+File.separator+"dtd"+File.separator+dtdName;
            log.debug("dtdLocation = "+dtdLocation);
            InputStreamReader dtdInputStreamReader =
                new InputStreamReader(new FileInputStream(dtdLocation));
            InputSource dtdInputSource = new InputSource();
            dtdInputSource.setCharacterStream(dtdInputStreamReader);
            dtdpath = dtdLocation;
            return dtdInputSource;
        }
    }

    /**
     * Returns whether the resolver has enough environmental information to resolve the DTD.
     * @return whether the resolver can resolve DTDs
     */
    public boolean canResolve() {
        return MMBaseContext.isInitialized();
    }

    /**
     * @return whether the resolver has determiend a DTD
     */
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
