package org.mmbase.applications.vprowizards.spring.cache;

import java.util.*;

import javax.servlet.jsp.PageContext;

import org.mmbase.module.core.MMBaseContext;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.web.ServletCacheAdministrator;

public class OSCacheWrapper implements CacheWrapper {

    private Cache cache = null;

    public void flushForName(String flushname) {
        if (cache == null) {
            //TODO:sort this out
//            cache = ServletCacheAdministrator.getInstance(MMBaseContext.getServletContext()).getCache(request,
//                    PageContext.APPLICATION_SCOPE);
        }
    }

}
