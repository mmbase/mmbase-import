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
 * @version $Revision: 1.3 $ $Date: 2000-08-06 14:54:34 $
 *
 * $Log: not supported by cvs2svn $
 */
public class XMLEntityResolver implements EntityResolver {
    String dtdpath, customdtdpath;
    boolean hasDTD; // tells whether or not a DTD is set - if not, no validition can take place

    /**
     * empty constructor
     */
    public XMLEntityResolver() {
        hasDTD = false;
        dtdpath = null;
        customdtdpath = null;
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
                String dtdLocation = MMBaseContext.getConfigPath()+File.separator+"dtd"+File.separator+dtdName;
                System.out.println("dtdLocation = "+dtdLocation);
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
     * Manually set a DTD that is to be used if the document doesn't refer to one itself.
     *
     * @param dtdpath Path to dtd file
     */
    public void setDTDPath(String dtdpath) {
        this.customdtdpath = dtdpath;
    }

    /**
     * @return The actually used path to the DTD
     */
    public String getDTDPath() {
        return this.dtdpath;
    }
}
