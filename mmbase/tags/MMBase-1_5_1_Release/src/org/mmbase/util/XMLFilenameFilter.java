/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.util;

import java.lang.*;
import java.io.*;

/**
 * @author cjr@dds.nl
 * @version $Id: XMLFilenameFilter.java,v 1.1 2000-08-20 10:38:27 case Exp $
 *
 * Implement a FilenameFilter for xml files
 * 
 * $Log: not supported by cvs2svn $
 *
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
