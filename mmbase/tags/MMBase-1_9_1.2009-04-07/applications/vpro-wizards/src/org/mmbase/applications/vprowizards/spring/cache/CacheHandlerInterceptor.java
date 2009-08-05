/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.cache;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mmbase.util.logging.Logging;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author ebunders
 *
 */
public abstract class CacheHandlerInterceptor implements HandlerInterceptor {
    /**
     * By what name the list of cache flush hints are registered in the request 
     */
    public static final String PARAM_NAME="__cfh";
    private static org.mmbase.util.logging.Logger log = Logging.getLoggerInstance(CacheHandlerInterceptor.class);
    protected List<Handling> handlings = new ArrayList<Handling>();


    /**
     * Add a handling to this cache handler
     * @param handling
     */
    protected void addHandling(Handling handling){
        handlings.add(handling);
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

    /**
     * for each cache flush hint, all handlings are checked, and all handlings of similar type are invoked.
     */
    @SuppressWarnings("unchecked")
    public final void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        List<CacheFlushHint> hints = (List<CacheFlushHint>) request.getAttribute(PARAM_NAME);
        if(hints != null){
            for(CacheFlushHint hint: hints){
                log.debug("processing hint: "+hint);
                for(Handling handling: handlings){
                    if(handling.getType() == hint.getType()){
                        handling.setCacheFlushHint(hint);
                        handling.handle(request, response, handler, modelAndView);
                    }
                }
            }
        }
    }

        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    /**
     * wrapper voor afhandeling van request hint
     * deze class bevat mapt een type hint aan een afhandeling implementatie.
     * Gebruik deze class in subclasses van {@link CacheHandlerInterceptor}
     * @author ebunders
     *
     */

}
