/**

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.
  
 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license

*/ 

package org.mmbase.servlet.filter;
      
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import javax.servlet.*;

/**
 * Generic filter class
 *
 * For futher explination, 
 *  see http://www.orionserver.com/tutorials/filters
 *
 * @author Marcel Maatkamp, VPRO Netherlands (marmaa_at_vpro.nl)
 * @version $Version$
 */

public class GenericFilter implements javax.servlet.Filter {
    static Logger log = Logging.getLoggerInstance(GenericFilter.class.getName());

    private FilterConfig filterConfig;

    public void doFilter(final ServletRequest request, final ServletResponse response, FilterChain chain) 
        throws java.io.IOException, javax.servlet.ServletException {
        chain.doFilter(request, response);
    }

    public void init(final FilterConfig filterConfig) { 
        setFilterConfig(filterConfig);
    }

    public void destroy() {
        filterConfig=null;
    }
    
    public void setFilterConfig(final FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public FilterConfig getFilterConfig() {
        return filterConfig;
    }
}
