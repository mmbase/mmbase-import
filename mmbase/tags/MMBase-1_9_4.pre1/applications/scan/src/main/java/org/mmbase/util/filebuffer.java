/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.Date;

/**
 * Class filebuffer. Used by servdb.
 *
 * @javadoc
 * @rename FileBuffer
 * @version $Id$
 */
public class filebuffer {

    public Date lastmod;
    public byte data[] = null;
    public Object obj;
    public int filesize = 0;
    public String mimesuper;
    public String mimesub;
    public String mimetype;

    public filebuffer(Object o) {
        obj = o;
    }

    public filebuffer(byte[] data) {
        this.data = data;
    }

    public filebuffer(int len) {
        data = new byte[len];
        filesize = len;
    }
}

