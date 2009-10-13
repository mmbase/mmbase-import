/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

/**
 * Class for holding a pool of random numbers.
 * Calling {@link #stir} takes care of generating a new 'random' number.
 * Calling {@link #value} returns the current random number.
 *
 * @author Rico Jansen
 * @author Pierre van Rooden (javadocs)
 * @version $Id$
 */
public class RandomPool {

    /**
     * Current random value.
     * Initial to a value from MD5 64-bit pool of randomness.
     * This values is maintained by {@link #stir}, and is
     * the value that is actually returned by {@link #value}
     * #see {@link #stir}
     */
    private static long ranPool    = 0x67452301efcdab89L;

    /**
     * Pool of magic numbers.
     * The following 31 constants are the first 62 "magic" numbers
     * from the MD5 algorithm,  RFC1321, concatenated in pairs to
     * form 64-bit Java long words.  Any random 64-bit values would do,
     * but these were selected from a public source for added user confidence.
     * The stir algorithm itself has nothing to do with MD5.
     */
     // No need to do it everytime EJJ
     private static long p[] = {
        0xd76aa478e8c7b756L, // 1,2
        0x242070dbc1bdceeeL, // 3,4
        0xf57c0faf4787c62aL, // 5,6
        0xa8304613fd469501L, // 7,8
        0x698098d88b44f7afL, // 8,10
        0xffff5bb1895cd7beL, // 11,12
        0x6b901122fd987193L, // 13,14
        0xa679438e49b40821L, // 15,16
        0xf61e2562c040b340L, // 17,18
        0x265e5a51e9b6c7aaL, // 19,20
        0xd62f105d02441453L, // 21,22
        0xd8a1e681e7d3fbc8L, // 23,24
        0x21e1cde6c33707d6L, // 25,26
        0xf4d50d87455a14edL, // 27,28
        0xa9e3e905fcefa3f8L, // 29,30
        0x676f02d98d2a4c8aL, // 31,32
        0xfffa39428771f681L, // 33,34
        0x6d9d6122fde5380cL, // 35,36
        0xa4beea444bdecfa9L, // 37,38
        0xf6bb4b60bebfbc70L, // 39,40
        0x289b7ec6eaa127faL, // 41,42
        0xd4ef308504881d05L, // 43,44
        0xd9d4d039e6db99e5L, // 45,46
        0x1fa27cf8c4ac5665L, // 47,48
        0xf4292244432aff97L, // 49,50
        0xab9423a7fc93a039L, // 51,52
        0x655b59c38f0ccc92L, // 53,54
        0xffeff47d85845dd1L, // 55,56
        0x6fa87e4ffe2ce6e0L, // 57,58
        0xa30143144e0811a1L, // 59,60
        0xf7537e82bd3af235L  // 61,62
    };

    /**
     * Create a pool.
     * Use the current time (long milliseconds) as a source of randomness
     */
    public RandomPool() {
        stir(System.currentTimeMillis());
    }

    /**
     * Create a pool.
     * @param init the source of randomness
     */
    public RandomPool(long init) {
        stir(init);
    }

    /**
     * Maintain a pool of randomness using a modified 64-bit
     * congruential generator with multipliers dynamically
     * selected from a set of pseudo-random values.
     * @param x the source of randomness
     */
    public synchronized void stir(long x) {
        int pIndex;

        pIndex = mod(ranPool, p.length);
        ranPool = (ranPool + x)*p[pIndex];
        pIndex = mod(ranPool, p.length);
        ranPool = ranPool ^ p[pIndex];
    }

    /**
     * Returns the current random value.
     */
    public long value() {
        return(ranPool);
    }

    /**
     * Stirs, then returns the (new) current random value.
     * Use the current time (long milliseconds) as a source of randomness
     * for stirring.
     */
    public long value_and_stir() {
        stir(System.currentTimeMillis());
        return(ranPool);
    }

    /**
     * Stirs, then returns the (new) current random value.
     * @param mix the source of randomness for stirring
     */
    public long value_and_stir(long mix) {
        stir(mix);
        return(ranPool);
    }

    /**
     * Returns the positive modulus of the arguments.
     * @param x the argument to modulate
     * @param y the argument to modulate with
     * @return the positive modulus of x modulus y.
     */
    private int mod (long x, long y) {
        if (x<0) x=-x;
        if (y<0) y=-y;
        return (int) (x % y);
    }
}

