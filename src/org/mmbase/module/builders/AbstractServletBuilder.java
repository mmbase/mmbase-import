/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.builders;

import java.util.*;

import org.mmbase.servlet.MMBaseServlet;
import org.mmbase.module.builders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;

/**
 * Some builders are associated with a servlet. Think of images and attachments.
 *
 * There is some common functionality for those kind of builders, which is collected here.
 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractServletBuilder.java,v 1.2 2002-06-28 22:36:12 michiel Exp $
 * @since   MMBase-1.6
 */
public abstract class AbstractServletBuilder extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(AbstractServletBuilder.class.getName());

    /**
     * In this string the path to the servlet is stored.
     */
    private String servletPath = null;
 

    /**
     * This functions should return a string identifying where it is
     * for. This is used when communicating with MMBaseServlet, to
     * find the right servlet.
     *
     * For example 'images' or 'attachments'.
     *
     */
    abstract protected String getAssociation();

    /**
     * If no servlet path can be found via the association (if the
     * servlet did not 'associate' itself with something, like
     * servdb), then the getServletPath function will fall back to
     * this.
     *
     * For example 'img.db' or 'attachment.db'.
     *
     */
    abstract protected String getDefaultPath();


   /**
     * Get a servlet path. Takes away the ? and the * which possibly
     * are present in the servlet-mappings. You can put the argument(s)
     * directly after this string.
     *  
     * @param context The context. Will be ignored if determined already.
     * @param fileName Optional fileName. Will be added to the url, but it will not influence the servlet.
     */

    protected String getServletPath(String context, String fileName) {
        if (servletPath == null) {
            List ls = MMBaseServlet.getServletMappingsByAssociation(getAssociation());
            if (ls != null) {
                servletPath = (String) ls.get(0);
                // remove mask
                int pos = servletPath.lastIndexOf("*");
                if (pos > 0) {
                    servletPath = servletPath.substring(0, pos);
                }
                pos = servletPath.indexOf("*");
                if (pos == 0) {
                    servletPath = servletPath.substring(pos+1);
                }
            } else {
                servletPath = getDefaultPath();
            }
            
            if (servletPath.startsWith("/")) {                     
                // if it not starts with / then no use adding context.                    
                if (context != null) {
                    if (context.endsWith("/")) {
                        servletPath = context + servletPath.substring(1);
                    } else {
                        servletPath = context + servletPath;
                    }
                }
            }
            log.service(getAssociation() + " are served on: " + servletPath);
        }
        String result;
        if (fileName == null) {
            result = servletPath;
        } else {
            if (servletPath.endsWith("/")) {
                result =  servletPath + fileName;
            } else {
                result = servletPath;
            }
        }
        
        // add '?' if it wasn't already there (only needed if not terminated with /)
        if (! result.endsWith("/")) result = result + "?";
        return result;
    }


    /**
     * Returns the path to the  servlet. 
     * @see #getServletPath(String, String)
     */
    protected String getServletPath(String fileName) {
        return getServletPath(MMBaseContext.getHtmlRootUrlPath(), fileName);
    }
    protected String getServletPath() {
        return getServletPath(MMBaseContext.getHtmlRootUrlPath(), null);
    }

    /**
     * Overrides the executeFunction of MMObjectBuilder with a
     * function to get the servletpath associated with this
     * builder. The field can optionally be filled in with the
     * context.
     */

    protected Object executeFunction(MMObjectNode node, String function, String field) {
        if (function.equals("servletpath")) {
            if (field == null || "".equals(field)){
                return getServletPath();
            } else {
                return getServletPath(field, null);
            }
        } 
        return super.executeFunction(node, function, field);
    }
    

}

