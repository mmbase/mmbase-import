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
 * @version $Revision: 1.9 $ $Date: 2001-07-09 12:30:06 $
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
                // 11 sept 2000, changed MMBaseContext to getProperty call to be able to use from cmdline
                //String configpath = MMBaseContext.getConfigPath();
                // fixed fo mmdemo String configpath = System.getProperty("mmbase.config");
                String configpath = null;
                String dtmp=System.getProperty("mmbase.mode");
                if (dtmp!=null && dtmp.equals("demo")) {
                        String curdir=System.getProperty("user.dir");
                        if (curdir.endsWith("orion")) {
                                curdir=curdir.substring(0,curdir.length()-6);
                        }
                        configpath=curdir+"/config";
                } else {
                        configpath = MMBaseContext.getConfigPath();
                }



                String separator = "";
                if (!configpath.endsWith(File.separator)) {
                    separator = File.separator;
                }
                String dtdLocation = configpath+separator+"dtd"+File.separator+dtdName;
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
