/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;



/**
 * http://www.macchiato.com/columns/Durable6.html
 *
 * Hash Collections (HashSet, HashMap, Hashtable, etc) are typically implemented with an array of buckets.
 * Each bucket is itself an array or linked list of <key, value> pairs. To decide which keys go into
 * which buckets, the array index for the bucket is produced by taking the hash value, modulo the size
 * of the collection. That is,
 * <code>bucketIndex = abs(hashValue % tableSize);</code>
 * Within each bucket, the search for the object is sequential, and uses equals. In the ideal case, the
 * table size is a prime number, the hash values would be evenly distributed, and each bucket would
 * contain just one value. Lookup in that case is incredibly fast: just one call to hashCode and one call
 * to equals. If the hash values (modulo the table size) are constant, you have the worst case; all the
 * objects will be in one bucket, and accessing the objects will require a sequential search through every
 * single item in the hash table, calling equals for every single item. Lookup in that case will be incredibly
 * slooow!
 *
 * Requirements: synchronization with Equality
 * <code>If x.equals(y), then x.hashCode() == y.hashCode()</code>
 * That is, if two objects are equal, then their hash values are equal. Note that the reverse is not true;
 * two objects may have the same hash value but not be equal!
 *
 * Basic design goals:
 * Even Distribution
 *   An ideal implementation would return integer values that are
 *   evenly distributed over the range from zero to Integer.MAX_VALUE.
 *   That is, if I picked a million random objects, their hash values
 *   would be pretty randomly distributed over this range. In the best
 *   case, any unequal objects would have different hash values.
 * Fast
 *   An ideal implementation would compute the hash value very quickly.
 *   After all, this method is going to be called every time an object
 *   is put into a hash-based collection, and every time you query whether
 *   an object is in a hash-based collection.
 * Simple
 *   Since you should implement this for every object that could go into
 *   hash-based collections, you want your code to be simple to write and easy
 *   to maintain.
 *
 * @since MMBase-1.8
 */
final public class HashCodeUtil {

   private static final int PRIME = 1000003;

   public static final int hashCode(int source, boolean x) {
      return PRIME * source + (x ? 1 : 0);
   }

   public static final int hashCode(int source, int x) {
      return PRIME * source + x;
   }

   public static final int hashCode(int source, long x) {
      return PRIME * source + (int) (PRIME * (x >>> 32) + (x & 0xFFFFFFFF));
   }

   public static final int hashCode(int source, float x) {
      return hashCode(source, x == 0.0F ? 0 : Float.floatToIntBits(x));
   }

   public static final int hashCode(int source, double x) {
      return hashCode(source, x == 0.0 ? 0L : Double.doubleToLongBits(x));
   }

   public static final int hashCode(int source, Object x) {
      return hashCode(source, x == null ? 0 : x.hashCode());
   }

   public static final int hashCode(int source, boolean[] x) {
      for (boolean element : x) {
         source = hashCode(source, element);
      }
      return source;
   }

   public static final int hashCode(int source, int[] x) {
      for (int element : x) {
         source = hashCode(source, element);
      }
      return source;
   }

   public static final int hashCode(int source, long[] x) {
      for (long element : x) {
         source = hashCode(source, element);
      }
      return source;
   }

   public static final int hashCode(int source, float[] x) {
      for (float element : x) {
         source = hashCode(source, element);
      }
      return source;
   }

   public static final int hashCode(int source, double[] x) {
      for (double element : x) {
         source = hashCode(source, element);
      }
      return source;
   }

   public static final int hashCode(int source, Object[] x) {
      for (Object element : x) {
         source = hashCode(source, element);
      }
      return source;
   }


   public static final int hashCodeGentle(int source, Object[] x) {
      source = PRIME * source + x.length;
      for (Object element : x) {
        source = PRIME * source + element.hashCode();
      }
      return source;
   }

   public static final int hashCodeGentle2(int source, Object[] x) {
      int last = x.length - 1;
      int i = 0, j = last;
      source = PRIME * source + last;
      for (; i < j; i = 17 * (i + 1) >> 4, j = last - i) {
         source = PRIME * source + x[i].hashCode();
         source = PRIME * source + x[j].hashCode();
      }
      if (i == j) {
         source = PRIME * source + x[i].hashCode();
      }
      return source;
   }
}