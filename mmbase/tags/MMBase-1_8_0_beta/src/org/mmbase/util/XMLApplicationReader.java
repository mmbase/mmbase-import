/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.util;

import org.xml.sax.InputSource;
import org.mmbase.util.xml.ApplicationReader;

/**
 * @javadoc
 * @deprecated-now use org.mmbase.util.xml.ApplicationReader
 * @author Daniel Ockeloen
 * @author Pierre van Rooden
 * @version $Id: XMLApplicationReader.java,v 1.25 2005-09-12 14:07:39 pierre Exp $
 */
public class XMLApplicationReader extends ApplicationReader {

    public XMLApplicationReader(String filename) {
        super(XMLBasicReader.getInputSource(filename));
    }

    public XMLApplicationReader(InputSource is) {
        super(is);
    }

    /**
     * Get the name of this application
     */
    public String getApplicationName() {
        return getName();
    }

    /**
     * Get the version of this application
     */
    public int getApplicationVersion() {
        return getVersion();
    }

    /**
     * Get the auto-deploy value of this application
     */
    public boolean getApplicationAutoDeploy() {
        return hasAutoDeploy();
    }

    /**
     * Get the maintainer of this application
     */
    public String getApplicationMaintainer() {
        return getMaintainer();
    }


}
