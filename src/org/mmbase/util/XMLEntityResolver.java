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
 * @author Gerard van Enk
 * @version $Revision: 1.2 $ $Date: 2000-07-04 10:20:27 $
 */
public class XMLEntityResolver implements EntityResolver {

    /**
     * empty constructor
     */
    public XMLEntityResolver(){}

	/**
	 * takes the systemId and returns the local location of the dtd
	 */
    public InputSource resolveEntity(String publicId, String systemId) 
      throws SAXException, IOException { 
        if (systemId != null) {
	    //does systemId contain a mmbase-dtd 
	    if (systemId.indexOf("http://www.mmbase.org/") != -1) {
	        int i = systemId.indexOf("/dtd/");
		String dtdName = systemId.substring(i);
                String dtdLocation = MMBaseContext.getConfigPath()+dtdName;
	        System.out.println("dtdLocation = "+dtdLocation);
		InputStreamReader dtdInputStreamReader = 
                  new InputStreamReader(new FileInputStream(dtdLocation));
	        InputSource dtdInputSource = new InputSource();
		dtdInputSource.setCharacterStream(dtdInputStreamReader);
                return dtdInputSource;
	    }
        } 
        // it's a systemId we can't do anything with, 
	// so let the parser decide what to do
        return null; 
    } 
}
