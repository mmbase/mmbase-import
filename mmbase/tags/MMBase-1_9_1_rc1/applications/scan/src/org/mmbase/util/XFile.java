/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.File;

/**
 * Retrieves the file properties from a File object and stores them as fields.
 * Eg: the files' modification time will be stored as a moddate long variabele.
 *
 * @author David V van Zeventer
 * @deprecated What's wrong with java.io.File itself? The only difference is that lastmodified gives last-modified not of file now, but of file when this object was created. If that is important, the name (nor javadoc) of this class is not very informative.
 * @application SCAN or Devices
 * @version $Id: XFile.java,v 1.8 2004-09-30 08:52:16 pierre Exp $
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
