/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.util.*;


/**
 * Classes which can be used as a cache implementation need to implement this interface.
 * An implementation of this interface has to be thread-safe to guarantee correctness.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public interface CacheImplementationInterface extends Map {

    /**
     * Sets the (maximal)  size  of the cache (if implementable).
     */
    void setMaxSize(int size);

    /**
     * Gets the (maximal)  size  of the cache (if implementable)
     */
    int  maxSize();

    /**
     * Returns the hit-count on a certain key (if implementable).
     */
    int getCount(Object key);

    /**
     * Configure the implementation with the given configuration values
     */
    void config(Map configuration);

    Object getLock();

}
