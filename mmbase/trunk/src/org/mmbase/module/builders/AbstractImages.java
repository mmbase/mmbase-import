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
 * AbstractImages holds the images and provides ways to insert, retrieve and
 * search them.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AbstractImages.java,v 1.6 2002-04-12 11:21:25 pierre Exp $
 * @since   MMBase-1.6
 */
public abstract class AbstractImages extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(AbstractImages.class.getName());

    /**
     * Static Image servlet path
     */
    private static String imageServletPath = null;

    /**
     * Static Image servlet path
     * @param rootPath the path that serves as the servlet's root
     */
    public static String getImageServletPath(String rootPath) {
        if (imageServletPath==null) {
            List ls=MMBaseServlet.getServletMappingsByAssociation("image-processing");
            if (ls!=null) {
                String value=(String)ls.get(0);
                // remove mask
                int pos=value.lastIndexOf("*");
                if (pos>0) {
                    value=value.substring(0,pos);
                }
                pos=value.indexOf("*");
                if (pos==0) {
                    value=value.substring(pos+1);
                }
                // remove first slash
                if (value.startsWith("/")) value=value.substring(1);
                // add '?' if it wasn't already there (only needed if not terminated with /)
                if (!value.endsWith("/")) value=value+"?";
                imageServletPath= value;
            } else {
                imageServletPath="img.db?";
            }
            log.service("Images are served on: "+imageServletPath);
        }
        if (rootPath==null) {
            return imageServletPath;
        } else {
            if (rootPath.endsWith("/")) {
                return rootPath+imageServletPath;
            } else {
                return rootPath+"/"+imageServletPath;
            }
        }
    }

    /**
     * clear Static Image servlet path
     */
    public static void clear() {
        imageServletPath=null;
    }

    /**
     * Returns the path to the image serlvet.
     */
    protected String getServlet() {
        // odd, have to remove slash... bit weird?
       return  AbstractImages.getImageServletPath(MMBaseContext.getHtmlRootUrlPath());
    }

    /**
     * An image's gui-indicator is of course some &lt;img src&gt;, but it depends on what kind of image
     * (cached, original) what excactly it must be.
     */
    abstract protected String getGUIIndicatorWithAlt(MMObjectNode node, String title);

    /**
     * Gui indicator of a whole node.
     */
    public String getGUIIndicator(MMObjectNode node) {
        return getGUIIndicatorWithAlt(node, "*");
    }

    public String getGUIIndicator(String field, MMObjectNode node) {
        if (field.equals("handle")) {
            return getGUIIndicatorWithAlt(node, "*");
        }
        // other fields can be handled by the gui function...
        return null;
    }

    abstract public String getImageMimeType(List params);

    /**
     * Returns an image which belongs to the given parameter set.
     * The parameters exist of a list of string values, staring with the proginal image object alias or number,
     * followed by operations (format and transformation instructions)
     *
     * @param params A list of parameters, containign at least the id of the image, possibly followed by operations
     * @return the image as a <a>byte[]</code>, or <code>null</code> if something went wrong
     */
    abstract public byte[] getImageBytes(List params);

}

