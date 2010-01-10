/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.mmbase.cache.CachePolicy;

/**
 * A Cacheable object contains information on cache policies, which determines whether the object should be cached or not.
 * The code that handles the caching should verify this for a cacheable object.
 *
 * @author Pierre van Rooden
 * @version $Id$
 * @since MMBase-1.8
 * @see org.mmbase.cache.CachePolicy
 */
public interface Cacheable {

    /**
     * Returns the CachePolicy of the object.
     * @return the {@link org.mmbase.cache.CachePolicy} object.
     */
    CachePolicy getCachePolicy();

    /**
     * Sets the CachePolicy of the query.
     * @param policy the {@link org.mmbase.cache.CachePolicy} object.
     */
    void setCachePolicy(CachePolicy policy);

}
