/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.bridge.remote.generator;

import java.io.*;
import org.xml.sax.*;

/**
 * Dummy Resolver for the MMCI DTD
 * (xxx: doesn't do anything yet!)
 *
 * @author Pierre van Rooden
 */
public class DummyEntityResolver implements EntityResolver {

    private String dtdpath;
    private boolean hasDTD; // tells whether or not a DTD is set - if not, no validition can take place

    /**
     * empty constructor
     */
    public DummyEntityResolver() {
        hasDTD = false;
        dtdpath = null;
    }

    /**
     * takes the systemId and returns an dummy (empty) dtd
     */
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return new InputSource(new StringReader(""));
    }

    /**
     * @return whether the resolver has determined a DTD
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
