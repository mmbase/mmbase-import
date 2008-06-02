package nl.vpro.redactie.cache;

import javax.servlet.http.HttpServletRequest;

/**
 * The purpose of this interface is to resolve identifiers for oscache groups
 * and cache keys into real group or key names. usually the keys are prefixed with
 * a hostname or such. This may be application dependent and so this interface makes it pluggable.
 * @author ebunders
 *
 */
public interface OSCacheNameResolver {
    /**
     * create a full cache group name based on a given identifier.
     * @param name
     * @return
     */
    public String resolveCacheGroupName(String identifier, HttpServletRequest request);
    /**
     * create a full cache key name based on a given identifier.
     * @param identifier
     * @return
     */
    public String resolveCacheKeyName(String identifier, HttpServletRequest request);
}
