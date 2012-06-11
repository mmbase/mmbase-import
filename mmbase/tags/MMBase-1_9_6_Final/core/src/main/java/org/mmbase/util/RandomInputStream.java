/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.io.*;
import java.util.Random;


/**
 * An input stream only producing random bytes. Not costing any memory though.
 * @since MMBase-1.9.2
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class RandomInputStream extends MockInputStream {

    private final Random generator = new Random();

    /**
     * This input stream produces a given number of random number.
     * @param l How long this input stream will be
     */
    public RandomInputStream(int l) {
        super(l);
    }

    /**
     * Produces an immense amount of randomness
     */
    public RandomInputStream() {
        this(Integer.MAX_VALUE);
    }

    @Override
    protected int oneByte() {
        int result = generator.nextInt() % 256;
        if (result < 0) result = -result;
        return result;
    }
    @Override
    protected void fillArray(byte[] data, int offset, int l) {
        byte[] temp = new byte[l];
        generator.nextBytes(temp);
        System.arraycopy(temp, 0, data, offset, l);
    }

}
