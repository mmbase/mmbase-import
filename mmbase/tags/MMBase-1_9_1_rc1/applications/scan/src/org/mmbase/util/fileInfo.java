/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 * Object for storing file information.
 * Stores content and lastmodified date.
 * Does not store filename, so association should eb done manually.
 * Used by the {@link org.mmbase.module.scancache} module.
 *
 * @rename FileInfo
 * @application SCAN
 * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version $Id: fileInfo.java,v 1.9 2004-09-30 08:52:17 pierre Exp $
 */
public class fileInfo {
    /**
     * Content of the file
     */
    public String value;

    /**
     * Last Modified Date of the file
     */
    public int time;
}

