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
 * @version $Revision: 1.6 $ $Date: 2000-10-19 23:17:19 $
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.5  2000/09/11 20:09:52  case
 * cjr: Changed call to MMBaseContext.getConfigPath() to System.getProperty()
 *      to be able to call this *without* having to start mmbase
 *
 * Revision 1.4  2000/08/10 20:44:15  case
 * cjr: removed some debug, checked path for '//', removed a stupid method
 *      that I added earlier.
 *
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
                        configpath=System.getProperty("mmbase.config");
                }



                String separator = "";
                if (!configpath.endsWith(File.separator)) {
                    separator = File.separator;
                }
                String dtdLocation = configpath+separator+"dtd"+File.separator+dtdName;
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
