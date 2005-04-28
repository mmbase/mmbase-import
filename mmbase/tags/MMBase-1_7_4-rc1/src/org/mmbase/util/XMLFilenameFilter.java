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
 * @author cjr@dds.nl
 * @version $Id: XMLFilenameFilter.java,v 1.3 2003-03-10 11:51:14 pierre Exp $
 */
public class XMLFilenameFilter implements FilenameFilter {
    public boolean accept(File directory, String name) {
        if (name.endsWith(".xml")) {
            return true;
        } else {
            return false;
        }
    }
}
