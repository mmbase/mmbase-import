/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.*;
import java.util.Arrays;



/**
 * An input stream only producing zeros. Not costing any memory though.
 * @since MMBase-1.9.2
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class NullInputStream extends MockInputStream {

    public NullInputStream(int l) {
        super(l);
    }
    public NullInputStream() {
        this(Integer.MAX_VALUE);
    }


    @Override
    protected int oneByte() {
        return 0;
    }

    @Override
    protected void fillArray(byte[] data, int offset, int l) {
        Arrays.fill(data, offset, l, (byte) 0);
    }
}
