/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import java.io.*;

/**
 * Retrieves the file properties from a File object and stores them as fields.
 * Eg: the files' modification time will be stored as a moddate long variabele.
 *
 * @author David V van Zeventer
 * @version: 13 nov 1998
 */
public class XFile {

    /**
     * The File itself
     */
    private File file=null;
    /**
     * The file path of the file
     */
    private String filepath=null;
    /**
     * The modification time of the file
     */
    private long modtime=0;

    /**
     * Creates a XFile object
     * @param f the File to store the values of.
     */
    public XFile(File f) {
        filepath = f.getPath();        // Get filepath.
        modtime = f.lastModified();    // Get modificationtime.
    }

    /**
     * Creates a XFile object
     * @param filepath the path of the file to store the values of.
     */
    public XFile(String filepath) {
        file = new File(filepath);        // Create fileobject.
        this.filepath = file.getPath();   // Get filepath.
        modtime = file.lastModified();    // Get modificationtime.
    }


    /**
     * Retrieves the file path.
     */
    public String getFilePath() {
        return filepath;
    }

    /**
     * Retrieves the modification time
     */
    public long getModTime() {
        return modtime;
    }
}
