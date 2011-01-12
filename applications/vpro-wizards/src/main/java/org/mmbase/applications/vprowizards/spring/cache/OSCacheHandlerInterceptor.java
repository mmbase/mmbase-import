package org.mmbase.applications.vprowizards.spring.cache;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import org.mmbase.util.MMBaseContext;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.web.ServletCacheAdministrator;

/**
 * Dit is een simpele oscache implementatie die op basis van de 'flush' en 'flushname' Een of meerdere oscache groepen flushet. je kunt voor
 * cachename een door komma gescheiden naam van groepen opgeven
 *
 * @author ebunders
 *
 */
public class OSCacheHandlerInterceptor extends CacheHandlerInterceptor {

    private static Logger log = Logging.getLoggerInstance(OSCacheHandlerInterceptor.class);

    private OSCacheNameResolver osCahCacheNameResolver = new BasicOSCacheNameResolver();

    OSCacheHandlerInterceptor() {

        handlings.add(new Handling(CacheFlushHint.TYPE_REQUEST) {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
                    throws Exception {

                log.debug("handling request type flush hint");
                if (shouldFlush(request)) {
                    CacheGroupResolver cacheGroupResolver = new CacheGroupResolver(RequestUtils
                            .getStringParameter(request, "flushname", ""));
                    String groups = cacheGroupResolver.getRequestGroups();
                    if(groups != null){
                        flushGroups(request, groups);
                    }
                }
            }
        });

        handlings.add(new Handling(CacheFlushHint.TYPE_NODE) {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
                    throws Exception {

                log.debug("handling node type flush hint");
                if (shouldFlush(request)) {
                    CacheGroupResolver cacheGroupResolver = new CacheGroupResolver(RequestUtils
                            .getStringParameter(request, "flushname", ""));
                    String groups = cacheGroupResolver.getNodeGroups();
                    if(groups != null){
                        flushGroups(request, groups);
                    }
                }
            }
        });

        handlings.add(new Handling(CacheFlushHint.TYPE_RELATION) {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
                    throws Exception {

                log.debug("handling relation type flush hint");
                if (shouldFlush(request)) {
                    CacheGroupResolver cacheGroupResolver = new CacheGroupResolver(RequestUtils
                            .getStringParameter(request, "flushname", ""));
                    String groups = cacheGroupResolver.getRelationGroups();
                    if(groups != null){
                        flushGroups(request, groups);
                    }
                }
            }
        });

    }


    /**
     * flush the given cache groups.
     * @param request
     * @param flushname a comma separated list of cache groups.
     */
    private void flushGroups(HttpServletRequest request, String flushname) {
        if (flushname != null && !"".equals(flushname)) {
            Cache cache = ServletCacheAdministrator.getInstance(MMBaseContext.getServletContext()).getCache(request,
                    PageContext.APPLICATION_SCOPE);
            StringTokenizer st = new StringTokenizer(flushname, ",");
            while (st.hasMoreTokens()) {
                String groupName = osCahCacheNameResolver.resolveCacheGroupName(st.nextToken().trim(), request) ;
                cache.flushGroup(groupName);
                log.debug("oscache group flushed: " + groupName);
            }
        }else{
            log.debug("can not flush! no flush groups");
        }
    }

    private boolean shouldFlush(HttpServletRequest request) {
        return request.getParameter("flushname")  != null && !"".equals(request.getParameter("flushname"));
    }

    /**
     * This class helps to resolve the value of the cachename parameter into different cache group sets. You can define a cache group set
     * for every type of cache flush hint. it works like this: node:group1,group2 request:group3 relation:group4,group5 if there are no
     * namespace indicators (node: or request: or relation:) then all groups are defined as default groups
     *
     * @author ebunders
     *
     */
    private static class CacheGroupResolver {
        private String groups, requestGroups = null, nodeGroups = null, relationGroups = null;

        CacheGroupResolver(String groups) {
            if (groups.indexOf("request:") > -1 || groups.indexOf("node:") > -1 || groups.indexOf("relation:") > -1) {
                // specialized cache groups
                StringTokenizer st = new StringTokenizer(groups, " ");
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (token.indexOf(":") > -1) {
                        // namespace
                        String ns = token.substring(0, token.indexOf(":")).trim();
                        String groups1 = token.substring(token.indexOf(":") + 1).trim();
                        if ("node".equals(ns)) {
                            nodeGroups = groups1;
                        } else if ("relation".equals(ns)) {
                            relationGroups = groups1;
                        } else if ("request".equals(ns)) {
                            requestGroups = groups1;
                        }
                    } else {
                        // no namespace, illegal syntax. do nothing
                    }
                }
            } else {
                requestGroups = groups.trim();
            }
            log.debug("created cache group resolver: "+this.toString());
        }

        public String getNodeGroups() {
            return nodeGroups;
        }

        public String getRelationGroups() {
            return relationGroups;
        }

        public String getRequestGroups() {
            return requestGroups;
        }

        public String toString() {
            return "node groups: " + getNodeGroups() + ", relation groups: " + getRelationGroups() + ", request groups: "
                    + getRequestGroups();
        }
    }

    public OSCacheNameResolver getOsCahCacheNameResolver() {
        return osCahCacheNameResolver;
    }


    /**
     * The default resolver is {@link BasicOSCachNameResolver} so you don't have to set one.
     * @param osCahCacheNameResolver
     */
    public void setOsCahCacheNameResolver(OSCacheNameResolver osCahCacheNameResolver) {
        this.osCahCacheNameResolver = osCahCacheNameResolver;
    }


}
