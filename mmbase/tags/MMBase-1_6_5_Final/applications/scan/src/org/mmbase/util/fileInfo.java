/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Object for storing file information.
 * Stores content and lastmodified date.
 * Does not store filename, so association should eb done manually.
 * Used by the {@link org.mmbase.module.scancache} module.
 *
 * @rename FileInfo
  * @author Daniel Ockeloen
 * @author Pierre van Rooden (javadocs)
 * @version 10 Apr 2001
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

