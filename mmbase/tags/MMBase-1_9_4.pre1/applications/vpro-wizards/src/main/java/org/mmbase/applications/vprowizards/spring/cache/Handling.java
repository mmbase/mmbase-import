/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/ 
package org.mmbase.applications.vprowizards.spring.cache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public abstract class Handling{
    private int type;
    protected CacheFlushHint hint;

    public void setCacheFlushHint(CacheFlushHint cacheFlushHint) {
        this.hint = cacheFlushHint;
    }

    public Handling(int type) {
        this.type = type;
    }

    public abstract void handle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception;


    public int getType() {
        return type;
    }

    public String toString(){
        return "type: "+getType()+", hint: "+hint;
    }

}