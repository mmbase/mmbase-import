/*
 
    Copyright (C) 1996 Marimba
 
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
 
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 
        @author Arthur van Hoff (Marimba)
        small method call changes Rob Vermeulen (VPRO)
 
        James people would like to thank Arthur van Hoff and Marimba
        for allowing us to use this under the GNU-Licence
 */

package org.mmbase.application;
import java.io.*;
import java.util.*;

/**
 * An implementation of the MD5 algorithm. This algorithm
 * computes a 64 bit (16 byte) checksum for an arbitrary
 * array of bytes or for an input stream. It is based on
 * the reference implementation  of the MD5 message digest
 * algorithm by R. Rivest of the MIT laboratory for Computer
 * Science.
 *
 * @author      Arthur van Hoff
 *                      small method call changes Rob Vermeulen (VPRO)
 *
 * @version     1.6, 06/11/96
 */
public class MD5 {
    private static final int S11 = 7;
    private static final int S12 = 12;
    private static final int S13 = 17;
    private static final int S14 = 22;
    private static final int S21 = 5;
    private static final int S22 = 9;
    private static final int S23 = 14;
    private static final int S24 = 20;
    private static final int S31 = 4;
    private static final int S32 = 11;
    private static final int S33 = 16;
    private static final int S34 = 23;
    private static final int S41 = 6;
    private static final int S42 = 10;
    private static final int S43 = 15;
    private static final int S44 = 21;
    
    /**
     * Add 64 bytes to the digest. The digest (the checksum) consists
     * of 4 integers.
     */
    private static void add64bytes(byte buf[], int i, int digest[]) {
        int a = digest[0];
        int b = digest[1];
        int c = digest[2];
        int d = digest[3];
        
        // Decode the 64 bytes into 16 integers.
        int x0  = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        int x1  = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        int x2  = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        int x3  = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        
        int x4  = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        int x5  = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        int x6  = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        int x7  = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        
        int x8  = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        int x9  = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        int x10 = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)
        |((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        int x11 = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)
        |((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        
        int x12 = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        int x13 = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        int x14 = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        int x15 = (buf[i++] & 0xFF) | ((buf[i++] & 0xFF) << 8)|
        ((buf[i++] & 0xFF) << 16) | ((buf[i++] & 0xFF) << 24);
        
        // Round 1
        a += ((b & c) | ((~b) & d)) + x0 + 0xd76aa478;
        a = ((a << S11) | (a >>>(32 - S11))) + b;
        d += ((a & b) | ((~a) & c)) + x1 + 0xe8c7b756;
        d = ((d << S12) | (d >>>(32 - S12))) + a;
        c += ((d & a) | ((~d) & b)) + x2 + 0x242070db;
        c = ((c << S13) | (c >>>(32 - S13))) + d;
        b += ((c & d) | ((~c) & a)) + x3 + 0xc1bdceee;
        b = ((b << S14) | (b >>>(32 - S14))) + c;
        
        a += ((b & c) | ((~b) & d)) + x4 + 0xf57c0faf;
        a = ((a << S11) | (a >>>(32 - S11))) + b;
        d += ((a & b) | ((~a) & c)) + x5 + 0x4787c62a;
        d = ((d << S12) | (d >>>(32 - S12))) + a;
        c += ((d & a) | ((~d) & b)) + x6 + 0xa8304613;
        c = ((c << S13) | (c >>>(32 - S13))) + d;
        b += ((c & d) | ((~c) & a)) + x7 + 0xfd469501;
        b = ((b << S14) | (b >>>(32 - S14))) + c;
        
        a += ((b & c) | ((~b) & d)) + x8 + 0x698098d8;
        a = ((a << S11) | (a >>>(32 - S11))) + b;
        d += ((a & b) | ((~a) & c)) + x9 + 0x8b44f7af;
        d = ((d << S12) | (d >>>(32 - S12))) + a;
        c += ((d & a) | ((~d) & b)) + x10 + 0xffff5bb1;
        c = ((c << S13) | (c >>>(32-S13))) + d;
        b += ((c & d) | ((~c) & a)) + x11 + 0x895cd7be;
        b = ((b << S14) | (b >>>(32-S14))) + c;
        
        a += ((b & c) | ((~b) & d)) + x12 + 0x6b901122;
        a = ((a << S11) | (a >>>(32-S11))) + b;
        d += ((a & b) | ((~a) & c)) + x13 + 0xfd987193;
        d = ((d << S12) | (d >>>(32-S12))) + a;
        c += ((d & a) | ((~d) & b)) + x14 + 0xa679438e;
        c = ((c << S13) | (c >>>(32-S13))) + d;
        b += ((c & d) | ((~c) & a)) + x15 + 0x49b40821;
        b = ((b << S14) | (b >>>(32-S14))) + c;
        
        // Round 2
        a += ((b & d) | (c & (~d))) + x1 + 0xf61e2562;
        a = ((a << S21) | (a >>>(32 - S21))) + b;
        d += ((a & c) | (b & (~c))) + x6 + 0xc040b340;
        d = ((d << S22) | (d >>>(32 - S22))) + a;
        c += ((d & b) | (a & (~b))) + x11 + 0x265e5a51;
        c = ((c << S23) | (c >>>(32-S23))) + d;
        b += ((c & a) | (d & (~a))) + x0 + 0xe9b6c7aa;
        b = ((b << S24) | (b >>>(32 - S24))) + c;
        
        a += ((b & d) | (c & (~d))) + x5 + 0xd62f105d;
        a = ((a << S21) | (a >>>(32 - S21))) + b;
        d += ((a & c) | (b & (~c))) + x10 + 0x02441453;
        d = ((d << S22) | (d >>>(32-S22))) + a;
        c += ((d & b) | (a & (~b))) + x15 + 0xd8a1e681;
        c = ((c << S23) | (c >>>(32-S23))) + d;
        b += ((c & a) | (d & (~a))) + x4 + 0xe7d3fbc8;
        b = ((b << S24) | (b >>>(32 - S24))) + c;
        
        a += ((b & d) | (c & (~d))) + x9 + 0x21e1cde6;
        a = ((a << S21) | (a >>>(32 - S21))) + b;
        d += ((a & c) | (b & (~c))) + x14 + 0xc33707d6;
        d = ((d << S22) | (d >>>(32-S22))) + a;
        c += ((d & b) | (a & (~b))) + x3 + 0xf4d50d87;
        c = ((c << S23) | (c >>>(32 - S23))) + d;
        b += ((c & a) | (d & (~a))) + x8 + 0x455a14ed;
        b = ((b << S24) | (b >>>(32 - S24))) + c;
        
        a += ((b & d) | (c & (~d))) + x13 + 0xa9e3e905;
        a = ((a << S21) | (a >>>(32-S21))) + b;
        d += ((a & c) | (b & (~c))) + x2 + 0xfcefa3f8;
        d = ((d << S22) | (d >>>(32 - S22))) + a;
        c += ((d & b) | (a & (~b))) + x7 + 0x676f02d9;
        c = ((c << S23) | (c >>>(32 - S23))) + d;
        b += ((c & a) | (d & (~a))) + x12 + 0x8d2a4c8a;
        b = ((b << S24) | (b >>>(32-S24))) + c;
        
        // Round 3
        a += ((b ^ c) ^ d) + x5 + 0xfffa3942;
        a = ((a << S31) | (a >>>(32 - S31))) + b;
        d += ((a ^ b) ^ c) + x8 + 0x8771f681;
        d = ((d << S32) | (d >>>(32 - S32))) + a;
        c += ((d ^ a) ^ b) + x11 + 0x6d9d6122;
        c = ((c << S33) | (c >>>(32-S33))) + d;
        b += ((c ^ d) ^ a) + x14 + 0xfde5380c;
        b = ((b << S34) | (b >>>(32-S34))) + c;
        
        a += ((b ^ c) ^ d) + x1 + 0xa4beea44;
        a = ((a << S31) | (a >>>(32 - S31))) + b;
        d += ((a ^ b) ^ c) + x4 + 0x4bdecfa9;
        d = ((d << S32) | (d >>>(32 - S32))) + a;
        c += ((d ^ a) ^ b) + x7 + 0xf6bb4b60;
        c = ((c << S33) | (c >>>(32 - S33))) + d;
        b += ((c ^ d) ^ a) + x10 + 0xbebfbc70;
        b = ((b << S34) | (b >>>(32-S34))) + c;
        
        a += ((b ^ c) ^ d) + x13 + 0x289b7ec6;
        a = ((a << S31) | (a >>>(32-S31))) + b;
        d += ((a ^ b) ^ c) + x0 + 0xeaa127fa;
        d = ((d << S32) | (d >>>(32 - S32))) + a;
        c += ((d ^ a) ^ b) + x3 + 0xd4ef3085;
        c = ((c << S33) | (c >>>(32 - S33))) + d;
        b += ((c ^ d) ^ a) + x6 + 0x04881d05;
        b = ((b << S34) | (b >>>(32 - S34))) + c;
        
        a += ((b ^ c) ^ d) + x9 + 0xd9d4d039;
        a = ((a << S31) | (a >>>(32 - S31))) + b;
        d += ((a ^ b) ^ c) + x12 + 0xe6db99e5;
        d = ((d << S32) | (d >>>(32-S32))) + a;
        c += ((d ^ a) ^ b) + x15 + 0x1fa27cf8;
        c = ((c << S33) | (c >>>(32-S33))) + d;
        b += ((c ^ d) ^ a) + x2 + 0xc4ac5665;
        b = ((b << S34) | (b >>>(32 - S34))) + c;
        
        // Round 4
        a += (c ^ (b | (~d))) + x0 + 0xf4292244;
        a = ((a << S41) | (a >>>(32 - S41))) + b;
        d += (b ^ (a | (~c))) + x7 + 0x432aff97;
        d = ((d << S42) | (d >>>(32 - S42))) + a;
        c += (a ^ (d | (~b))) + x14 + 0xab9423a7;
        c = ((c << S43) | (c >>>(32-S43))) + d;
        b += (d ^ (c | (~a))) + x5 + 0xfc93a039;
        b = ((b << S44) | (b >>>(32 - S44))) + c;
        
        a += (c ^ (b | (~d))) + x12 + 0x655b59c3;
        a = ((a << S41) | (a >>>(32-S41))) + b;
        d += (b ^ (a | (~c))) + x3 + 0x8f0ccc92;
        d = ((d << S42) | (d >>>(32 - S42))) + a;
        c += (a ^ (d | (~b))) + x10 + 0xffeff47d;
        c = ((c << S43) | (c >>>(32-S43))) + d;
        b += (d ^ (c | (~a))) + x1 + 0x85845dd1;
        b = ((b << S44) | (b >>>(32 - S44))) + c;
        
        a += (c ^ (b | (~d))) + x8 + 0x6fa87e4f;
        a = ((a << S41) | (a >>>(32 - S41))) + b;
        d += (b ^ (a | (~c))) + x15 + 0xfe2ce6e0;
        d = ((d << S42) | (d >>>(32-S42))) + a;
        c += (a ^ (d | (~b))) + x6 + 0xa3014314;
        c = ((c << S43) | (c >>>(32 - S43))) + d;
        b += (d ^ (c | (~a))) + x13 + 0x4e0811a1;
        b = ((b << S44) | (b >>>(32-S44))) + c;
        
        a += (c ^ (b | (~d))) + x4 + 0xf7537e82;
        a = ((a << S41) | (a >>>(32 - S41))) + b;
        d += (b ^ (a | (~c))) + x11 + 0xbd3af235;
        d = ((d << S42) | (d >>>(32-S42))) + a;
        c += (a ^ (d | (~b))) + x2 + 0x2ad7d2bb;
        c = ((c << S43) | (c >>>(32 - S43))) + d;
        b += (d ^ (c | (~a))) + x9 + 0xeb86d391;
        b = ((b << S44) | (b >>>(32 - S44))) + c;
        
        // Add to the digest
        digest[0] += a;
        digest[1] += b;
        digest[2] += c;
        digest[3] += d;
    }
    
    /**
     * Finish computation of the digest. Must pass the remaining bytes (always
     * fewer than 64). This method return the digest as a byte array.
     */
    private static long[] finish(byte data[], int off, int len, long total, int digest[]) {
        byte buf[] = new byte[128];
        System.arraycopy(data, off, buf, 0, len);
        buf[len++] = (byte)0x80;
        len = (len < 56) ? 56: 120;
        total *= 8L;
        
        // append the length in bits
        buf[len++] = (byte)((total >>> 0) & 0xFF);
        buf[len++] = (byte)((total >>> 8) & 0xFF);
        buf[len++] = (byte)((total >>> 16) & 0xFF);
        buf[len++] = (byte)((total >>> 24) & 0xFF);
        buf[len++] = (byte)((total >>> 32) & 0xFF);
        buf[len++] = (byte)((total >>> 40) & 0xFF);
        buf[len++] = (byte)((total >>> 48) & 0xFF);
        buf[len++] = (byte)((total >>> 56) & 0xFF);
        
        // finish computation of the digest
        add64bytes(buf, 0, digest);
        if (len > 64) {
            add64bytes(buf, 64, digest);
        }
        
        // Convert the digest to longs
        long d[] = new long[2];
        d[0] = (digest[1] << 32L) | (digest[0] & 0xFFFFFFFFL);
        d[1] = (digest[3] << 32L) | (digest[2] & 0xFFFFFFFFL);
        return d;
    }
    
    /**
     * Compute the MD5 checksum for a buffer of bytes.
     */
    public static long[] checksum(byte data[], int offset, int len) {
        int digest[] = {0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476};
        
        while (true) {
            if (offset + 64 > len) {
                return finish(data, offset, len-offset, len, digest);
            }
            add64bytes(data, offset, digest);
            offset += 64;
        }
    }
    
    /**
     * Compute the MD5 checksum for an input stream. The stream is
     * read 64 bytes at a time until the EOF is reached.
     */
    public static long[] checksum(InputStream in) throws IOException {
        int digest[] = {0x67452301, 0xefcdab89, 0x98badcfe, 0x10325476};
        byte buf[] = new byte[64];
        long total = 0;
        
        while (true) {
            for (int n = 0, m ; n < 64 ; n += m) {
                if ((m = in.read(buf, n, 64-n)) <= 0) {
                    return finish(buf, 0, n, total + n, digest);
                }
            }
            total += 64;
            add64bytes(buf, 0, digest);
        }
    }
    
    /**
     * Create a checksum key.
     */
    public static String digestString(long digest[]) {
        StringBuffer buf=new StringBuffer();
        for (int i = 0 ; i < 16 ; i++) {
            if ((i > 0) && ((i % 4) == 0)) {
                buf.append(' ');
            }
            int b = (int)(digest[i/8] >>> ((i%8)*8));
            buf.append(Character.forDigit((b & 0xF0)>>4, 16));
            buf.append(Character.forDigit(b & 0x0F, 16));
        }
        return buf.toString();
    }
    
    /**
     * Compute MD5 checksums for data files.
     */
    public static String calculate(FileInputStream in) {
        String md5value=null;
        try {
            md5value = (digestString(checksum(in)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5value;
    }
}
