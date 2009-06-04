/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.cache;

import org.mmbase.module.core.MMBaseContext;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.web.ServletCacheAdministrator;

/**
 * this wraps an oscahe instance.
 * @author ebunders
 *
 */
public class OSCacheWrapper implements CacheWrapper {

    private static Cache cache = null;

    /**
     * This method flushes an oscache cache group with given name.
     * @see org.mmbase.applications.vprowizards.spring.cache.CacheWrapper#flushForName(java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    public void flushForName(String flushname) {
        if (cache == null) {
            cache = ServletCacheAdministrator.getInstance(MMBaseContext.getServletContext()).getAppScopeCache(MMBaseContext.getServletContext());
        }
        cache.flushGroup(flushname);
    }

}
