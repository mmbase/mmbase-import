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
 * @version $Id: AbstractServletBuilder.java,v 1.4 2002-06-30 19:30:13 michiel Exp $
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



    private String getServletPathWithAssociation(String association, String context) {
        String result;
        List ls = MMBaseServlet.getServletMappingsByAssociation(association);
        if (ls != null) {
            result = (String) ls.get(0);
            // remove mask
            int pos = result.lastIndexOf("*");
            if (pos > 0) {
                result = result.substring(0, pos);
            }
            pos = result.indexOf("*");
            if (pos == 0) {
                result = result.substring(pos+1);
            }
        } else {
            result = getDefaultPath();
        }
        
        if (result.startsWith("/")) {                     
            // if it not starts with / then no use adding context.                    
            if (context != null) {
                if (context.endsWith("/")) {
                    result = context + result.substring(1);
                } else {
                    result = context + result;
                }
            }
        }
        return result;
    }

   /**
     * Get a servlet path. Takes away the ? and the * which possibly
     * are present in the servlet-mappings. You can put the argument(s)
     * directly after this string.
     *  
     * @param context The context (empty string, or starting with /). Will be ignored if determined already. 
     * @param fileName Optional fileName. Will be added to the url, but it will not influence the servlet.
     */

    protected String getServletPath(String context, String fileName) {
        if (servletPath == null) {
            servletPath = getServletPathWithAssociation(getAssociation(), context);
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
     * 'Servlet' builders need a way to transform security to the servlet, in the gui functions, so
     * they have to implement the 'SGUIIndicators'
     */

    abstract protected String getSGUIIndicator(String session, MMObjectNode node);
    abstract protected String getSGUIIndicator(String session, String field, MMObjectNode node);


    /**
     * Gets the GUI indicator of the super class of this class, to avoid circular references in
     * descendants, which will occur if they want to call super.getGUIIndicator().
     */

    final protected String getSuperGUIIndicator(String field, MMObjectNode node) {
        return super.getGUIIndicator(field, node);
    }

    /**
     * This is final, because getSGUIIndicator has to be overridden in stead
     */
    final public String getGUIIndicator(MMObjectNode node) {              
        return getSGUIIndicator("", node);
    }
    /**
     * This is final, because getSGUIIndicator has to be overridden in stead
     */

    final public String getGUIIndicator(String field, MMObjectNode node) { // final, override getSGUIIndicator
        return getSGUIIndicator("", field, node);
    }


    /**
     * Overrides the executeFunction of MMObjectBuilder with a function to get the servletpath
     * associated with this builder. The field can optionally be the number field to obtain a full
     * path to the served object.
     */

    protected Object executeFunction(MMObjectNode node, String function, String field) {
        if (function.equals("servletpath")) {
            String context = null;
            if (field.startsWith("/")) { // hack to be able to supply the context, should be superflouous.
                int pos = field.indexOf('/', 1);
                context = field.substring(0, pos);
                field = field.substring(pos + 1);
            }
            String servlet;
            if (context == null) {
                servlet = getServletPath();
            } else {
                servlet = getServletPath(context, null);
            }

            if ("".equals(field)) {
                return servlet;
            } else {
                return servlet + node.getStringValue(field);
            }
        } else if (function.equals("servletpathof")) { 
            // you should not need this very often, only when you want to serve a node with the 'wrong' servlet this can come in handy.
            return getServletPathWithAssociation(field, MMBaseContext.getHtmlRootUrlPath());
        } else if (function.equals("format")) { // don't issue a warning, builders can override this. 
            // images e.g. return jpg or gif
        } else if (function.equals("mimetype")) { // don't issue a warning, builders can override this. 
            // images, attachments and so on
        } else if (function.equals("sgui")) {
            int comma = field.indexOf(',');
            if (comma == -1) {
                return getGUIIndicator(field, node);
            } else {
                return getSGUIIndicator("session=" + field.substring(0, comma) + "+", field.substring(comma + 1), node);
            }
        } else {                   
            return super.executeFunction(node, function, field);
        }    
        return null;
    }

}
