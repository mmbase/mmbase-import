package org.mmbase.util;

/**
 * The SizeOf class tries to determin the size of memory structures.
 * This is tried by reflection and so on, but if an object is
 * 'SizeMeasurable' then it is asked to the object directly.
 *
 * So, if your object stores its bulk in private members, and you want
 * its size to be determined adequately, then you should let it
 * implement this interface.
 * 
 * If you did not implement a class yourself, but have a clue how to
 * guess the size anyhow, then you could also put this implementation
 * in SizeOf itself (as for example was done for String). The
 * disadvantage of this approach is of course that you don't have
 * access to private members.
 * 
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 */

public interface SizeMeasurable {
    /**
     * Determins the byte-size of this object
     */
    public int getByteSize();

    /** 
     * Determins the byte-size of this object using the given SizeOf instance.
     * A SizeOf instance stores a Set already counted objects. So this method is typically called by SizeOf itself (recursion).
     */
    public int getByteSize(SizeOf sizeof);
}
