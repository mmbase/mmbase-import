/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.Date;

/**
 * Class cacheline
 *
 * @application cache [utility]
 * @javadoc
 * @rename CacheLine
 * @move org.mmbase.cache
 * @author  $Author: pierre $
 * @version $Id: cacheline.java,v 1.9 2003-03-10 11:50:14 pierre Exp $
 */

public class cacheline {

    /**
     * @javadoc
     * @scope private
     */
    public Date lastmod;
    /**
     * @javadoc
     * @scope private
     */
    public byte buffer[]=null;
    /**
     * @javadoc
     * @scope private
     */
    public int filesize;
    /**
     * @javadoc
     * @scope private
     */
    public String mimetype;

    /**
     * @javadoc
     */
    public cacheline(int len) {
        buffer = new byte[len];
        filesize=len;
    }

    /**
     * @javadoc
     */
    public cacheline() {
    }

    /**
     * @javadoc
     */
    public String toString() {
        String s = new String(buffer);
        return mimetype+","+lastmod+","+filesize+","+buffer.length+", "+s;
    }
}

