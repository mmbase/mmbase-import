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
 * @version $Id: AbstractImages.java,v 1.7 2002-06-27 16:02:52 michiel Exp $
 * @since   MMBase-1.6
 */
public abstract class AbstractImages extends MMObjectBuilder {

    private static Logger log = Logging.getLoggerInstance(AbstractImages.class.getName());

    /**
     * Static Image servlet path
     */
    private static String imageServletPath = null;

 
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
        if (imageServletPath == null) {
            imageServletPath = MMBaseServlet.getServletPath(MMBaseContext.getHtmlRootUrlPath(), "images",  "img.db");
        }
       return  imageServletPath;
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

