/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.cache;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * A CachePolicy object determines for a given object whether it should be cached or not, and how.
 * Code that makes use of a cache should use a CachePolicy object, when available, to determine if the
 * object should be cached or not.
 *
 * @since MMBase 1.8
 * @author Pierre van Rooden
 * @version $Id$
 */
abstract public class CachePolicy implements Serializable {

    // map with all known policies
    static private Map<Object,CachePolicy> policies = new HashMap<Object,CachePolicy>();

    /**
     * Standard cache policy that advises to never cache a passed object.
     * Accessible with the key "never".
     */
    static public CachePolicy NEVER = new CachePolicy("never") {
        public boolean checkPolicy(Object o) {
            return false;
        }

        public String getDescription() {
            return "CACHE NEVER";
        }
    };

    /**
     * Standard cache policy that advises to always cache a passed object.
     * Accessible with the key "always".
     */
    static public CachePolicy ALWAYS = new CachePolicy("always") {
        public boolean checkPolicy(Object o) {
            return true;
        }

        public String getDescription() {
            return "CACHE ALWAYS";
        }
    };

    /**
     * Obtains a cache policy given a policy key.
     * @param policyKey the key of the cache policy
     * @return the policy key
     * @throws IllegalArgumentException if the policy does not exist
     */
    static public CachePolicy getPolicy(Object policyKey) {
        CachePolicy policy = policies.get(policyKey);
        if (policy == null) {
            throw new IllegalArgumentException("There is no cache policy known with key '"+policyKey+"'");
        }
        return policy;
    }


    static public void putPolicy(Object policyKey, CachePolicy policy) {
        policies.put(policyKey, policy);
    }

    /**
     * Instantiates a new cache policy, and registers it with the given policy key.
     */
    protected CachePolicy(Object policyKey) {
        CachePolicy.putPolicy(policyKey, this);
    }

    /**
     * Instantiates a new cache policy without registering it
     */
    protected CachePolicy() {
    }

    /**
     * Checks whether the policy advises to cache the passed object.
     * @param o the object to check the cache for
     * @return <code>true</code> if the policy advises to cache this object, <code>false</code> otherwise.
     */
    abstract public boolean checkPolicy(Object o);

    /**
     * Returns a description of the policy.
     */
    public String getDescription() {
        return getClass().getName();
    }

}
