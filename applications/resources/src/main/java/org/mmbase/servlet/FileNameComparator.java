/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.servlet;

import java.io.*;
import java.util.*;
import org.mmbase.util.logging.*;


/**
 * Can be used as a value of the init parameters 'comparator' of FileServlet to make the files
 * ordered alphabeticly
 * @author Michiel Meeuwissen
 * @since  MMBase-1.9.2
 * @version $Id: $
 */
public class FileNameComparator implements Comparator<File> {

    public int compare(File f1, File f2) {
        return f1.getName().compareTo(f2.getName());
    }

    public boolean equals(Object o) {
        return o != null && o.getClass().equals(FileNameComparator.class);
    }

}


