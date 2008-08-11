package org.mmbase.applications.vprowizards.spring.cache;

import java.util.*;

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