/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.File;
import java.io.FilenameFilter;


/**
 * Implement a FilenameFilter for xml files
 *
 * @application Config
 * @deprecated too specific, instead make use of an inner class in Config module
 * @author cjr@dds.nl
 * @version $Id: XMLFilenameFilter.java,v 1.4 2004-10-01 08:41:11 pierre Exp $
 */
public class XMLFilenameFilter implements FilenameFilter {

    public boolean accept(File directory, String name) {
        return name.endsWith(".xml");
    }
}
