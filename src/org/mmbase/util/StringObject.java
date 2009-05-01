/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
/*
 * @(#)StringBuffer.java        1.28 96/02/14
 *
 * Copyright (c) 1994 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

package org.mmbase.util;

/**
 * This Class is a growable buffer for characters.
 * It is mainly used to create Strings. The compiler uses it to implement the "+" operator.
 * For example:
 * <pre>
 *        "a" + 4 + "c"
 * </pre>
 * is compiled to:
 * <pre>
 *        new StringBuffer().append("a").append(4).append("c").toString()
 * </pre>
 *
 * Note that the method toString() does not create a copy of the internal buffer. Instead
 * the buffer is marked as shared. Any further changes to the buffer will
 * cause a copy to be made. <p>
 *
 * this is based on StringBuffer code, we have a seperate class since sun doesn't
 * allow us to extend StringBuffer for some reason and we want methods like replace
 * over the whole buffer.
 *
 * @license Sun license
 * @see     String
 * @author Daniel Ockeloen 
 * @author Johannes Verelst (bugfix)
 * @author  Arthur van Hoff
 * @version $Id$
 */

public final class StringObject {
    /** The value is used for character storage. */
    private char value[];

    /** The count is the number of characters in the buffer. */
    private int count;

    /** A flag indicating whether the buffer is shared */
    private boolean shared;

    /**
     * Constructs an empty String buffer.
     */
    public StringObject() {
        this(16);
    }

    /**
     * Constructs an empty String buffer with the specified initial length.
     * @param length        the initial length
     */
    public StringObject(int length) {
        value = new char[length];
        shared = false;
    }

    /**
     * Constructs a String buffer with the specified initial value.
     * @param str        the initial value of the buffer
     */
    public StringObject(String str) {
        this(str.length() + 16);
        append(str);
    }

    /**
     * Returns the length (character count) of the buffer.
     */
    public int length() {
        return count;
    }

    /**
     * Returns the current capacity of the String buffer. The capacity
     * is the amount of storage available for newly inserted
     * characters; beyond which an allocation will occur.
     */
    public int capacity() {
        return value.length;
    }

    /**
     * Copies the buffer value if it is shared.
     */
    private final void copyWhenShared() {
        if (shared) {
            char newValue[] = new char[value.length];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
            shared = false;
        }
    }

    /**
     * Ensures that the capacity of the buffer is at least equal to the
     * specified minimum.
     * @param minimumCapacity        the minimum desired capacity
     */
    public synchronized void ensureCapacity(int minimumCapacity) {
        int maxCapacity = value.length;

        if (minimumCapacity > maxCapacity) {
            int newCapacity = (maxCapacity + 1) * 2;
            if (minimumCapacity > newCapacity) {
                newCapacity = minimumCapacity;
            }

            char newValue[] = new char[newCapacity];
            System.arraycopy(value, 0, newValue, 0, count);
            value = newValue;
            shared = false;
        }
    }

    /**
     * Sets the length of the String. If the length is reduced, characters
     * are lost. If the length is extended, the values of the new characters
     * are set to 0.
     * @param newLength        the new length of the buffer
     * @exception StringIndexOutOfBoundsException  If the length is invalid.
     */
    public synchronized void setLength(int newLength) {
        if (newLength < 0) {
            throw new StringIndexOutOfBoundsException(newLength);
        }
        ensureCapacity(newLength);

        if (count < newLength) {
            copyWhenShared();
            for (; count < newLength; count++) {
                value[count] = '\0';
            }
        }
        count = newLength;
    }

    /**
     * Returns the character at the specified index. An index ranges
     * from 0..length()-1.
     * @param index        the index of the desired character
     * @exception StringIndexOutOfBoundsException If the index is invalid.
     */
    public synchronized char charAt(int index) {
        if ((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return value[index];
    }

    /**
     * Copies the characters of the specified substring (determined by
     * srcBegin and srcEnd) into the character array, starting at the
     * array's dstBegin location. Both srcBegin and srcEnd must be legal
     * indexes into the buffer.
     * @param srcBegin        begin copy at this offset in the String
     * @param srcEnd        stop copying at this offset in the String
     * @param dst                the array to copy the data into
     * @param dstBegin        offset into dst
     * @exception StringIndexOutOfBoundsException If there is an invalid index into the buffer.
     */
    public synchronized void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        if ((srcBegin < 0) || (srcBegin >= count)) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }
        if ((srcEnd < 0) || (srcEnd > count)) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }
        if (srcBegin < srcEnd) {
            System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
        }
    }

    /**
     * Changes the character at the specified index to be ch.
     * @param index        the index of the character
     * @param ch                the new character
     * @exception        StringIndexOutOfBoundsException If the index is invalid.
     */
    public synchronized void setCharAt(int index, char ch) {
        if ((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        copyWhenShared();
        value[index] = ch;
    }

    /**
     * Appends an object to the end of this buffer.
     * @param obj        the object to be appended
     * @return         the StringBuffer itself, NOT a new one.
     */
    public synchronized StringObject append(Object obj) {
        return append(String.valueOf(obj));
    }

    /**
     * Appends a String to the end of this buffer.
     * @param str        the String to be appended
     * @return         the StringBuffer itself, NOT a new one.
     */
    public synchronized StringObject append(String str) {
        if (str == null) {
            str = String.valueOf(str);
        }

        int len = str.length();
        ensureCapacity(count + len);
        copyWhenShared();
        str.getChars(0, len, value, count);
        count += len;
        return this;
    }

    /**
     * Appends an array of characters to the end of this buffer.
     * @param str        the characters to be appended
     * @return         the StringBuffer itself, NOT a new one.
     */
    public synchronized StringObject append(char str[]) {
        int len = str.length;
        ensureCapacity(count + len);
        copyWhenShared();
        System.arraycopy(str, 0, value, count, len);
        count += len;
        return this;
    }

    /**
     * Appends a part of an array of characters to the end of this buffer.
     * @param str        the characters to be appended
     * @param offset        where to start
     * @param len        the number of characters to add
     * @return         the StringBuffer itself, NOT a new one.
     */
    public synchronized StringObject append(char str[], int offset, int len) {
        ensureCapacity(count + len);
        copyWhenShared();
        System.arraycopy(str, offset, value, count, len);
        count += len;
        return this;
    }

    /**
     * Appends a boolean to the end of this buffer.
     * @param b        the boolean to be appended
     * @return         the StringBuffer itself, NOT a new one.
     */
    public StringObject append(boolean b) {
        return append(String.valueOf(b));
    }

    /**
     * Appends a character to the end of this buffer.
     * @param c        the character to be appended
     * @return         the StringBuffer itself, NOT a new one.
     */
    public synchronized StringObject append(char c) {
        ensureCapacity(count + 1);
        copyWhenShared();
        value[count++] = c;
        return this;
    }

    /**
     * Appends an integer to the end of this buffer.
     * @param i        the integer to be appended
     * @return         the StringBuffer itself, NOT a new one.
     */
    public StringObject append(int i) {
        return append(String.valueOf(i));
    }

    /**
     * Appends a long to the end of this buffer.
     * @param l        the long to be appended
     * @return         the StringBuffer itself, NOT a new one.
     */
    public StringObject append(long l) {
        return append(String.valueOf(l));
    }

    /**
     * Appends a float to the end of this buffer.
     * @param f        the float to be appended
     * @return         the StringBuffer itself, NOT a new one.
     */
    public StringObject append(float f) {
        return append(String.valueOf(f));
    }

    /**
     * Appends a double to the end of this buffer.
     * @param d        the double to be appended
     * @return         the StringBuffer itself, NOT a new one.
     */
    public StringObject append(double d) {
        return append(String.valueOf(d));
    }

    /**
     * Inserts an object into the String buffer.
     * @param offset        the offset at which to insert
     * @param obj                the object to insert
     * @return                 the StringBuffer itself, NOT a new one.
     * @exception        StringIndexOutOfBoundsException If the offset is invalid.
     */
    public synchronized StringObject insert(int offset, Object obj) {
        return insert(offset, String.valueOf(obj));
    }

    /**
     * Inserts a String into the String buffer.
     * @param offset        the offset at which to insert
     * @param str                the String to insert
     * @return                 the StringBuffer itself, NOT a new one.
     * @exception        StringIndexOutOfBoundsException If the offset is invalid.
     */
    public synchronized StringObject insert(int offset, String str) {
        if ((offset < 0) || (offset > count)) {
            throw new StringIndexOutOfBoundsException();
        }
        int len = str.length();
        ensureCapacity(count + len);
        copyWhenShared();
        System.arraycopy(value, offset, value, offset + len, count - offset);
        str.getChars(0, len, value, offset);
        count += len;
        return this;
    }

    /**
     * Inserts an array of characters into the String buffer.
     * @param offset        the offset at which to insert
     * @param str                the characters to insert
     * @return                 the StringBuffer itself, NOT a new one.
     * @exception        StringIndexOutOfBoundsException If the offset is invalid.
     */
    public synchronized StringObject insert(int offset, char str[]) {
        if ((offset < 0) || (offset > count)) {
            throw new StringIndexOutOfBoundsException();
        }
        int len = str.length;
        ensureCapacity(count + len);
        copyWhenShared();
        System.arraycopy(value, offset, value, offset + len, count - offset);
        System.arraycopy(str, 0, value, offset, len);
        count += len;
        return this;
    }

    /**
     * Inserts a boolean into the String buffer.
     * @param offset        the offset at which to insert
     * @param b                the boolean to insert
     * @return                 the StringBuffer itself, NOT a new one.
     * @exception        StringIndexOutOfBoundsException If the offset is invalid.
     */
    public StringObject insert(int offset, boolean b) {
        return insert(offset, String.valueOf(b));
    }

    /**
     * Inserts a character into the String buffer.
     * @param offset        the offset at which to insert
     * @param c                the character to insert
     * @return                 the StringBuffer itself, NOT a new one.
     * @exception        StringIndexOutOfBoundsException If the offset invalid.
     */
    public synchronized StringObject insert(int offset, char c) {
        ensureCapacity(count + 1);
        copyWhenShared();
        System.arraycopy(value, offset, value, offset + 1, count - offset);
        value[offset] = c;
        count += 1;
        return this;
    }

    /**
     * Inserts an integer into the String buffer.
     * @param offset        the offset at which to insert
     * @param i                the integer to insert
     * @return                 the StringBuffer itself, NOT a new one.
     * @exception        StringIndexOutOfBoundsException If the offset is invalid.
     */
    public StringObject insert(int offset, int i) {
        return insert(offset, String.valueOf(i));
    }

    /**
     * Inserts a long into the String buffer.
     * @param offset        the offset at which to insert
     * @param l                the long to insert
     * @return                 the StringBuffer itself, NOT a new one.
     * @exception        StringIndexOutOfBoundsException If the offset is invalid.
     */
    public StringObject insert(int offset, long l) {
        return insert(offset, String.valueOf(l));
    }

    /**
     * Inserts a float into the String buffer.
     * @param offset        the offset at which to insert
     * @param f                the float to insert
     * @return                 the StringBuffer itself, NOT a new one.
     * @exception        StringIndexOutOfBoundsException If the offset is invalid.
     */
    public StringObject insert(int offset, float f) {
        return insert(offset, String.valueOf(f));
    }

    /**
     * Inserts a double into the String buffer.
     * @param offset        the offset at which to insert
     * @param d                the double to insert
     * @return                 the StringBuffer itself, NOT a new one.
     * @exception        StringIndexOutOfBoundsException If the offset is invalid.
     */
    public StringObject insert(int offset, double d) {
        return insert(offset, String.valueOf(d));
    }

    /**
     * Reverse the order of the characters in the String buffer.
     */
    public synchronized StringObject reverse() {
        copyWhenShared();
        int n = count - 1;
        for (int j = (n-1) >> 1; j >= 0; --j) {
            char temp = value[j];
            value[j] = value[n - j];
            value[n - j] = temp;
        }
        return this;
    }


    /**
     * Converts to a String representing the data in the buffer.
     */
    public String toString() {
        return new String(value,0,count);
    }

    // The following two methods are needed by String to efficiently
    // convert a StringBuffer into a String.  They are not public.
    // They shouldn't be called by anyone but String.
    // XXX doesn't make sense... if this is package scope how can String call it?

    final void setShared() {
        shared = true;
    }

    final char[] getValue() {
        return value;
    }

    /**
     * delete part of the buffer
     */
    public synchronized StringObject delete(int offset,int len) {

        if ((offset < 0) || (offset > count)) {
            throw new StringIndexOutOfBoundsException();
        }
        copyWhenShared();
        System.arraycopy(value, offset+len, value, offset, count-(offset+len));
        count -= len;
        return this;
        }


    /**
     * replace
     */
    public synchronized StringObject replace(int offset,int len,String str) {
        delete(offset,len);
        insert(offset,str);
        return this;
    }


    /**
     * replace
     */
    public synchronized StringObject replaceFirst(String oldstr,String newstr) {
        int pos=indexOf(oldstr,0);
        if (pos!=-1) {
            delete(pos,oldstr.length());
            insert(pos,newstr);
        }
        return this;
    }


    /**
     * replace
     */
    public synchronized StringObject replace(String oldstr,String newstr) {
        int strlen = oldstr.length();
        int pos=indexOf(oldstr,0,strlen);
        while (pos!=-1) {
            delete(pos,strlen);
            insert(pos,newstr);
            pos=indexOf(oldstr,pos+newstr.length(),strlen);
        }
        return this;
    }


    /**
     * Does a replace/insert.
     * Like make bold:bla into &lt:b&gt:bla&lt:/b&gt:
     */
    public synchronized StringObject replace(String oldstart,String oldend,String newstart, String newend) {
        int pos2;
        int pos=indexOf(oldstart,0);

        while (pos!=-1) {
            delete(pos,oldstart.length());
            insert(pos,newstart);
            pos2=indexOf(oldend,pos+newstart.length());
            if (pos2!=-1) {
                delete(pos2,oldend.length());
                insert(pos2,newend);
            }
            pos=indexOf(oldstart,pos2+newend.length());
        }
        return this;
    }

        /**
        * inserts links
        */
    public synchronized StringObject insertLinks(String oldstart,String oldend,String newstart,String newend, String startend) {
        int pos2;
        int pos=indexOf(oldstart,0);
        String link="";
        while (pos!=-1) {
            delete(pos,oldstart.length());
            insert(pos,newstart);
            pos2=indexOf(oldend,pos+newstart.length());
            if (pos2!=-1) {
                link=new String(value,pos+newstart.length(),pos2-(pos+newstart.length()));
                delete(pos2,oldend.length());
                insert(pos2,newend+link+startend);
            }
            pos=indexOf(oldstart,pos2+newend.length());
        }
        return this;
    }

    public int indexOf(String str) {
        return indexOf(str,0);
    }

    public int indexOf(String str, int fromIndex) {
       return indexOf(str, fromIndex, str.length());
    }

    private int indexOf(String str, int fromIndex, int strlen) {
        char v1[] = value;
        char v2[] = str.toCharArray();
        int max = (count - strlen);
      test:
        for (int i = ((fromIndex < 0) ? 0 : fromIndex); i <= max ; i++) {
            int n = strlen;
            int j = i;
            int k = 0;
            while (n-- != 0) {
                if (v1[j++] != v2[k++]) {
                    continue test;
                }
            }
            return i;
        }
        return -1;
    }
    

    /**
     */
    public byte[] getBytes() {
        return toString().getBytes();
    }
}
